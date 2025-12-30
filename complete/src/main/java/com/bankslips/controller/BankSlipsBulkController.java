package com.bankslips.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.controller.response.BulkUploadResponse;
import com.bankslips.controller.response.BulkUploadStatusResponse;
import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.exception.JobNotFoundException;
import com.bankslips.service.BulkJobService;
import com.bankslips.service.interfaces.IPersistenceBulkService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/rest")
public class BankSlipsBulkController {

	@Autowired
	private IPersistenceBulkService<BankSlips> bankSlipsAsyncService;
    
    @Autowired
    private BulkJobService bulkJobService;


	@RequestMapping(value = "/bankslips/bulk", method = RequestMethod.POST)
    public ResponseEntity<BulkUploadResponse> uploadBulk(@RequestBody @Valid @NotEmpty List<BankSlips> slips) {
    	bankSlipsAsyncService.bulkSaveInParallel(slips);

        return ResponseEntity.ok(
                BulkUploadResponse.sync(slips.size())
            );
    }
    
	@RequestMapping(value = "/bankslips/bulk/async", method = RequestMethod.POST)
    public ResponseEntity<BulkUploadResponse> uploadBulkAsync(@RequestBody @Valid @NotEmpty List<BankSlips> slips) {
		UUID jobId = bankSlipsAsyncService.startAsyncBulkUpload(slips);
		
	    return ResponseEntity.accepted().body(
	            BulkUploadResponse.async(slips.size(), jobId)
	        );
    }
	
	@RequestMapping(value = "/bankslips/bulk/status/{jobId}", method = RequestMethod.GET)
	public BulkUploadStatusResponse getStatus(@PathVariable @NotNull UUID jobId) {
	    BulkUploadJob job = bulkJobService.getJobById(jobId)
	            .orElseThrow(() -> new JobNotFoundException(String.format(ErrorMessages.JOB_NOT_FOUND, jobId)));

	        return BulkUploadStatusResponse.from(job);
	}
	
	@RequestMapping(value = "/bankslips/bulk/{jobId}/failures", method = RequestMethod.GET)
	public List<BulkUploadFailure> getFailures(@PathVariable UUID jobId) {
	    return bulkJobService.getFailureJobById(jobId);
	}


}

