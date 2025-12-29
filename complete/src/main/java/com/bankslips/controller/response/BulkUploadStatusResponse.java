package com.bankslips.controller.response;

import java.time.LocalDateTime;

import com.bankslips.domain.bulkupload.BulkUploadJob;

public record BulkUploadStatusResponse(
	    String status,
	    int total,
	    int processed,
	    int success,
	    int failed,
	    LocalDateTime startedAt,
	    LocalDateTime finishedAt
	) {
	    public static BulkUploadStatusResponse from(BulkUploadJob job) {
	        return new BulkUploadStatusResponse(
	            job.getStatus().name(),
	            job.getTotalRecords(),
	            job.getProcessedRecords(),
	            job.getSuccessRecords(),
	            job.getFailedRecords(),
	            job.getStartedAt(),
	            job.getFinishedAt()
	        );
	    }
	}


