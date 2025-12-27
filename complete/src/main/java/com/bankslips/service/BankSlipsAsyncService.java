package com.bankslips.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
        BulkUploadJob job = jobsRepository.findById(jobId).orElseThrow();
        job.setStatus(BulkJobStatus.RUNNING);
        jobsRepository.save(job);

        List<BankSlips> validSlips = validatorService.sanitizeList(slips, job);
        List<List<BankSlips>> batches = createBatches(validSlips, BATCH_SIZE);

        // Step 3: Persist valid slips
        for (List<BankSlips> batch : batches) {
            try {
                bankSlipsService.saveAll(batch);
                job.setProcessedRecords(job.getProcessedRecords() + batch.size());
                job.setSuccessRecords(job.getSuccessRecords() + batch.size());
            } catch (Exception e) {
                batch.forEach(slip -> failureRecorder.recordFailure(job, slip, e.getMessage()));
            }
            jobsRepository.save(job);
        }

        // Step 5: Finalize job
        job.setFinishedAt(LocalDateTime.now());
        job.setStatus(job.getFailedRecords() > 0 ? BulkJobStatus.COMPLETED_WITH_ERRORS : BulkJobStatus.COMPLETED);
        jobsRepository.save(job);

        return CompletableFuture.completedFuture(null);
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
