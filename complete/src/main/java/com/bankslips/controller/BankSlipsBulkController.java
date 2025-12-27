package com.bankslips.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.service.BulkJobService;
import com.bankslips.service.interfaces.IBankSlipsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest")
public class BankSlipsBulkController {

    
    @Autowired
    private IBankSlipsService bankSlipsService;
    
    @Autowired
    private BulkJobService bulkJobService;


	@RequestMapping(value = "/bankslips/bulk", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> uploadBulk(@RequestBody @Valid  List<BankSlips> slips) {
		
		if (slips == null || slips.isEmpty()) {
		    return ResponseEntity.badRequest()
		        .body(Map.of("message", "Bulk upload list cannot be empty"));
		}
		
    	bankSlipsService.bulkSave(slips);


        Map<String, Object> response = Map.of(
            "uploaded", slips.size(),
            "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
    
	@RequestMapping(value = "/bankslips/bulk/async", method = RequestMethod.POST)
    public ResponseEntity<?> uploadBulkAsync(@RequestBody @Valid List<BankSlips> slips) {
		if (slips == null || slips.isEmpty()) {
		    return ResponseEntity.badRequest()
		        .body(Map.of("message", "Bulk upload list cannot be empty"));
		}
		
		UUID jobId = bankSlipsService.startBulkSave(slips);
		
        return ResponseEntity.accepted()
                .body(Map.of("message", "Bulk upload started", 
                		     "slips", slips.size(),
                		     "jobId", jobId));
    }
	
	@RequestMapping(value = "/bankslips/bulk/status/{jobId}", method = RequestMethod.GET)
	public ResponseEntity<?> getStatus(@PathVariable UUID jobId) {

	    Optional<BulkUploadJob> jobOptional = bulkJobService.getJobById(jobId);
	    BulkUploadJob job = jobOptional.get();

	    return ResponseEntity.ok(
	        Map.of(
	            "status", job.getStatus(),
	            "total", job.getTotalRecords(),
	            "processed", job.getProcessedRecords(),
	            "success", job.getSuccessRecords(),
	            "failed", job.getFailedRecords(),
	            "startedAt", job.getStartedAt(),
	            "finishedAt", job.getFinishedAt()
	        )
	    );
	}
	
	@RequestMapping(value = "/bankslips/bulk/{jobId}/failures", method = RequestMethod.GET)
	public List<BulkUploadFailure> getFailures(@PathVariable UUID jobId) {
	    return bulkJobService.getFailureJobById(jobId);
	}


}

