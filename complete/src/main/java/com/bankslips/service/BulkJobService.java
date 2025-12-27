package com.bankslips.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.FailureJobsRepository;
import com.bankslips.repository.JobsRepository;

@Service
public class BulkJobService {

	@Autowired
	private JobsRepository jobRepository;
	
	@Autowired
	private FailureJobsRepository failureJobsRepository;
	
	public Optional<BulkUploadJob> getJobById(UUID jobId) {
		return jobRepository.findById(jobId);
	}
	
	public List<BulkUploadFailure> getFailureJobById(UUID jobID) {
		return failureJobsRepository.findByJobId(jobID);
	}
}
