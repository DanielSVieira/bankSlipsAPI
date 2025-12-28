package com.bankslips.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkJobStatus;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.FailureJobsRepository;
import com.bankslips.repository.BulkJobsRepository;

@Service
public class BulkJobService  {

	@Autowired
	private BulkJobsRepository jobRepository;
	
	@Autowired
	private FailureJobsRepository failureJobsRepository;
	
	
	public Optional<BulkUploadJob> getJobById(UUID jobId) {
		return jobRepository.findById(jobId);
	}
	
	public List<BulkUploadFailure> getFailureJobById(UUID jobID) {
		return failureJobsRepository.findByJobId(jobID);
	}
	
	public BulkUploadJob create(BulkUploadJob bulkUploadJob) {
		return jobRepository.save(bulkUploadJob);
	}
	

    public BulkUploadJob startJob(UUID jobId) {
        BulkUploadJob job = getJobById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
        job.setStatus(BulkJobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        create(job);
        return job;
    }
    
    public UUID startJob(int totalRecords) {
        BulkUploadJob job = new BulkUploadJob();
        job.setStatus(BulkJobStatus.PENDING);
        job.setTotalRecords(totalRecords);
        job.setStartedAt(LocalDateTime.now());

        return create(job).getId();
    }
    

    public void handleBatchFailure(List<BankSlips> batch, BulkUploadJob job, Throwable ex) {
        batch.forEach(slip -> recordFailure(job, slip, ex.getMessage()));
        synchronized (job) {
            job.setProcessedRecords(job.getProcessedRecords() + batch.size());
            job.setFailedRecords(job.getFailedRecords() + batch.size());
        }
        jobRepository.save(job); // checkpoint after failure
    }

    public void finalizeJob(BulkUploadJob job) {
        job.setFinishedAt(LocalDateTime.now());
        job.setStatus(job.getFailedRecords() > 0 ? BulkJobStatus.COMPLETED_WITH_ERRORS : BulkJobStatus.COMPLETED);
        jobRepository.save(job);
    }
    

    
    /*
     *Insert record failure, on the async process, so that the status can be checked later  
     */
    public void recordFailure(BulkUploadJob job, BankSlips slip, String reason) {
        var failure = new BulkUploadFailure();
        failure.setJobId(job.getId());
        failure.setExternalId(slip.getExternalId());
        failure.setReason(reason);
        failureJobsRepository.save(failure);

        job.setFailedRecords(job.getFailedRecords() + 1);
        jobRepository.save(job);
    }
    
    public List<BulkUploadFailure> findByJobId(UUID jobID){
    	return failureJobsRepository.findByJobId(jobID);
    }
}
