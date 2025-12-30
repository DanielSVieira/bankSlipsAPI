package com.bankslips.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.exception.BankSlipsContraintViolationException;
import com.bankslips.exception.BankSlipsNotFoundException;
import com.bankslips.exception.InvalidBankSlipsStatusChangeException;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.interfaces.IBankSlipsService;

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
	
    public BankSlips create(BankSlips bankSlips, BindingResult bindingResult) {

        if (bankSlipsValidator.jsonContainsErrors(bindingResult)) {
            List<String> errorMessages = bankSlipsValidator.getErrorMessages(bindingResult);
            throw new BankSlipsContraintViolationException(
                errorMessages.toString(),
                HttpStatus.UNPROCESSABLE_ENTITY
            );
        }

        try {
            return bankSlipsRepository.save(bankSlips);
        } catch (DataIntegrityViolationException ex) {
            throw new BankSlipsContraintViolationException(ErrorMessages.DUPLICATED_EXTERNAL_ID,
                    HttpStatus.CONFLICT);
        }
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
        BankSlips bankSlips = bankSlipsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.BANKSLIPS_NOT_FOUND));
        
        BankSlipsStatus oldStatus = bankSlips.getStatus();
        extraUpdates.accept(bankSlips);
        BankSlipsStatus newStatus = bankSlips.getStatus();
        checkStatusChangeAllowed(oldStatus, newStatus);
        
        bankSlips.setOldStatus(oldStatus);
        return bankSlipsRepository.save(bankSlips);
	}
	
	@Override
	public List<BankSlips> findByStatus(BankSlipsStatus status) {
	    return bankSlipsRepository.findAllByStatus(status); 
	}
	
	private void checkStatusChangeAllowed(BankSlipsStatus oldStatus, BankSlipsStatus newStatus) {
		if(!oldStatus.equals(newStatus)) { return; }
	    if (oldStatus != BankSlipsStatus.PENDING) {
	        throw new InvalidBankSlipsStatusChangeException(
	            ErrorMessages.INVALID_BANK_SLIPS_STATUS_CHANGE);
	    }
	}
	
    public void saveAll(List<BankSlips> slips) {
        bankSlipsRepository.saveAll(slips);
    }

}
