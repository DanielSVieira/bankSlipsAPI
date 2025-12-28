package com.bankslips.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkUploadJob;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class BankSlipsValidator {

    @Autowired
    private Validator beanValidator;

    @Autowired
    private BulkJobService bulkJobService;

    /*
     * Validate records, and return a sanitized list to be imported
     * Duplicated external IDs are not added to final list
     * If a record is not successfully validated by the domain constraints, it is not added to the final list
     * Every failure record is registered to the failureRecorder
     */
	public List<BankSlips> sanitizeList(List<BankSlips> slips, BulkUploadJob job) {
        Set<String> seenExternalIds = ConcurrentHashMap.newKeySet();
        List<BankSlips> validSlips = new ArrayList<>();

        for (BankSlips slip : slips) {
            boolean isValid = true;

            if (!seenExternalIds.add(slip.getExternalId())) {
                bulkJobService.recordFailure(job, slip, ErrorMessages.DUPLICATED_EXTERNAL_ID);
                continue;
            }

            isValid = validateBankSlip(job, slip, isValid);

            if (isValid) validSlips.add(slip);
        }

        return validSlips;
    }

	private boolean validateBankSlip(BulkUploadJob job, BankSlips slip, boolean isValid) {
		beanValidator.validate(slip);
		Set<ConstraintViolation<BankSlips>> violations = beanValidator.validate(slip);
		if (!violations.isEmpty()) {
		    isValid = false;
		    for (var violation : violations) {
		        bulkJobService.recordFailure(job, slip, violation.getMessage());
		    }
		}
		return isValid;
	}
	
	public List<String> getErrorMessages(BindingResult bindingResult) {
		List<ObjectError> errors = bindingResult.getAllErrors();
		return errors.stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());

	}

	public boolean jsonContainsErrors(BindingResult bindingResult) {
		return !Objects.isNull(bindingResult) && bindingResult.hasFieldErrors(); 
	}
}
