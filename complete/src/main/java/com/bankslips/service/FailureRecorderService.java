package com.bankslips.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.repository.FailureJobsRepository;
import com.bankslips.repository.JobsRepository;

@Service
public class FailureRecorderService {

    @Autowired
    private FailureJobsRepository failureJobsRepository;

    @Autowired
    private JobsRepository jobsRepository;

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
        jobsRepository.save(job);
    }
}

