package com.bankslips.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.interfaces.IBankSlipsService;
import com.bankslips.service.interfaces.IPersistenceBulkService;

@Service
public class BankSlipsBulkService implements IPersistenceBulkService<BankSlips> {

    @Autowired
    private BankSlipsValidator validatorService;

    @Autowired @Lazy
    private IBankSlipsService bankSlipsService;

    @Autowired
    private BulkJobService bulkJobService;
    
    @Autowired
    private BankSlipsRepository bankSlipsRepository;
    
	@Autowired
	private ExecutorService executor;

    private static final int BATCH_SIZE = 500;

    @Async
    public CompletableFuture<Void> bulkSaveAsync(UUID jobId, List<BankSlips> slips) {
        BulkUploadJob job = bulkJobService.startJob(jobId);

        List<BankSlips> validSlips = validatorService.sanitizeList(slips, job);
        List<List<BankSlips>> batches = createBatches(validSlips, BATCH_SIZE);

        Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> processBatch(batch, job), executor)
                        .exceptionally(ex -> {
                        	bulkJobService.handleBatchFailure(batch, job, ex);
                            return null;
                        }))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> bulkJobService.finalizeJob(job));
    }


	    private void processBatch(List<BankSlips> batch, BulkUploadJob job) {
	        try {
	            bankSlipsService.saveAll(batch); // transactional save
	            synchronized (job) {
	                job.setProcessedRecords(job.getProcessedRecords() + batch.size());
	                job.setSuccessRecords(job.getSuccessRecords() + batch.size());
	            }
	        } catch (Exception e) {
	        	bulkJobService.handleBatchFailure(batch, job, e);
	        }
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

	
	@Override
	public UUID startAsyncBulkUpload(List<BankSlips> slips) {
	    UUID jobId = bulkJobService.startJob(slips.size());

	    bulkSaveAsync(jobId, slips);

	    return jobId;
	}

	/*
	 * bulk save synchronous
	 */
	@Override
	public void bulkSaveInParallel(List<BankSlips> slips) { 
        List<CompletableFuture<Void>> futures = slips.stream()
                .map(slip -> CompletableFuture.runAsync(() -> {
                	bankSlipsRepository.save(slip);
                }, executor))
                .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
		
	}

    

}
