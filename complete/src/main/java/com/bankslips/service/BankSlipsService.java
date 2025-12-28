package com.bankslips.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkJobStatus;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.exception.BankSlipsContraintViolationException;
import com.bankslips.exception.BankSlipsNotFoundException;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.repository.BulkJobsRepository;
import com.bankslips.service.interfaces.IBankSlipsService;
import com.bankslips.service.interfaces.IPersistenceBulkService;

import jakarta.transaction.Transactional;


@Service
public class BankSlipsService implements IBankSlipsService {
	
	@Autowired
	private BankSlipsRepository bankSlipsRepository;
	
    @Autowired
    @Lazy
    private BankSlipsService self;
    
    @Autowired
    private BankSlipsValidator bankSlipsValidator;
	
	public BankSlips create(BankSlips bankSlips, BindingResult bindingResult)  {
		if (bankSlipsValidator.jsonContainsErrors(bindingResult)) {
			List<String> errorMessages = bankSlipsValidator.getErrorMessages(bindingResult);
			throw new BankSlipsContraintViolationException(errorMessages.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return bankSlipsRepository.save(bankSlips);	
	}
	
	public BankSlips create(BankSlips bankSlips)  {
		return bankSlipsRepository.save(bankSlips);	
	}	
	
	@Override
	public Page<BankSlips> list(Pageable pageable) {
		return bankSlipsRepository.findAll(pageable);
	}
	
	public BankSlips show(UUID bankSlipsId) {
	    BankSlips bankSlips = bankSlipsRepository
	            .findById(bankSlipsId)
	            .orElseThrow(() ->
	                    new BankSlipsNotFoundException(
	                            ErrorMessages.BANKSLIPS_NOT_FOUND + bankSlipsId
	                    )
	            );
	    
	    bankSlips.applyFineIfPending(LocalDate.now());
	    return bankSlips;
	}
	
	@Override
	@Transactional
	public BankSlips edit(UUID id, Consumer<BankSlips> extraUpdates) {
        BankSlips slip = bankSlipsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.BANKSLIPS_NOT_FOUND));
        
        BankSlipsStatus oldStatus = slip.getStatus();
        extraUpdates.accept(slip);
        BankSlipsStatus newStatus = slip.getStatus();
        if (!oldStatus.equals(newStatus)) {
            validateStatusChange(oldStatus, newStatus);
        }
        
        return bankSlipsRepository.save(slip);

	}
	
	private void validateStatusChange(BankSlipsStatus oldStatus, BankSlipsStatus newStatus) {
	    if (oldStatus != BankSlipsStatus.PENDING) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	            "Bank slip status can only be changed if it is pending");
	    }
	}
	
    public void saveAll(List<BankSlips> slips) {
        bankSlipsRepository.saveAll(slips);
    }

}
