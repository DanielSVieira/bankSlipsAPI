package com.bankslips.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkJobStatus;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.JobsRepository;
import com.bankslips.service.interfaces.IBankSlipsService;

@Service
public class BankSlipsAsyncService {

    @Autowired
    private BankSlipsValidator validatorService;

    @Autowired @Lazy
    private IBankSlipsService bankSlipsService;

    @Autowired
    private FailureRecorderService failureRecorder;

    @Autowired
    private JobsRepository jobsRepository;

    private static final int BATCH_SIZE = 500;

    @Async
    public CompletableFuture<Void> bulkSaveAsync(UUID jobId, List<BankSlips> slips) {
        BulkUploadJob job = startJob(jobId);

        List<BankSlips> validSlips = validatorService.sanitizeList(slips, job);
        List<List<BankSlips>> batches = createBatches(validSlips, BATCH_SIZE);

        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> processBatch(batch, job), executor)
                        .exceptionally(ex -> {
                            handleBatchFailure(batch, job, ex);
                            return null;
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> finalizeJob(job));
    }

    private BulkUploadJob startJob(UUID jobId) {
        BulkUploadJob job = jobsRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        job.setStatus(BulkJobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        jobsRepository.save(job);
        return job;
    }

    private void processBatch(List<BankSlips> batch, BulkUploadJob job) {
        try {
            bankSlipsService.saveAll(batch); // transactional save
            synchronized (job) {
                job.setProcessedRecords(job.getProcessedRecords() + batch.size());
                job.setSuccessRecords(job.getSuccessRecords() + batch.size());
            }
        } catch (Exception e) {
            handleBatchFailure(batch, job, e);
        }
    }

    private void handleBatchFailure(List<BankSlips> batch, BulkUploadJob job, Throwable ex) {
        batch.forEach(slip -> failureRecorder.recordFailure(job, slip, ex.getMessage()));
        synchronized (job) {
            job.setProcessedRecords(job.getProcessedRecords() + batch.size());
            job.setFailedRecords(job.getFailedRecords() + batch.size());
        }
        jobsRepository.save(job); // checkpoint after failure
    }

    private void finalizeJob(BulkUploadJob job) {
        job.setFinishedAt(LocalDateTime.now());
        job.setStatus(job.getFailedRecords() > 0 ? BulkJobStatus.COMPLETED_WITH_ERRORS : BulkJobStatus.COMPLETED);
        jobsRepository.save(job);
    }

    /*
     * add the records into batches to be imported
     * It returns a list of lists of bankslips. each list will be processed in its own chunk 
     */
    private List<List<BankSlips>> createBatches(List<BankSlips> slips, int batchSize) {
        return IntStream.range(0, (slips.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> slips.subList(i * batchSize, Math.min((i + 1) * batchSize, slips.size())))
                .toList();
    }
}
