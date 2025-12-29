package com.bankslips.controller.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BulkUploadResponse(
    String message,
    int records,
    LocalDateTime timestamp,
    UUID jobId
) {
	

    public static BulkUploadResponse sync(int records) {
        return new BulkUploadResponse(
    		BulkUploadMessage.SYNC_COMPLETED.text(),
            records,
            LocalDateTime.now(),
            null
        );
    }

    public static BulkUploadResponse async(int records, UUID jobId) {
        return new BulkUploadResponse(
        		BulkUploadMessage.ASYNC_STARTED.text(),
            records,
            LocalDateTime.now(),
            jobId
        );
    }
}
