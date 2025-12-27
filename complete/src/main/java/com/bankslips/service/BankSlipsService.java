package com.bankslips.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.domain.BankSlips;
import com.bankslips.domain.bulkupload.BulkJobStatus;
import com.bankslips.domain.bulkupload.BulkUploadFailure;
import com.bankslips.domain.bulkupload.BulkUploadJob;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.exception.BankSlipsContraintViolationException;
import com.bankslips.exception.BankSlipsNotFoundException;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.repository.FailureJobsRepository;
import com.bankslips.repository.JobsRepository;
import com.bankslips.service.interfaces.IBankSlipsService;
import com.bankslips.utils.DateUtils;
import com.bankslips.utils.FinanceMathUtils;

import jakarta.transaction.Transactional;


@Service
public class BankSlipsService implements IBankSlipsService {
	
	@Autowired
	private BankSlipsRepository bankSlipsRepository;
	
	@Autowired
	private ExecutorService executor;

	@Autowired
	private JobsRepository jobsRepository;
	
    @Autowired
    private BankSlipsAsyncService asyncService;
	
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
	
	@Override
	public Page<BankSlips> list(Pageable pageable) {
		return bankSlipsRepository.findAll(pageable);
	}
	
	//TODO refactor this code. better calculate the fine
	public BankSlips show(String bankSlipsId) {
		try {
			Optional<BankSlips> bankSlipsOptional = bankSlipsRepository.findById(bankSlipsId);
			BankSlips bankSlips = bankSlipsOptional.get();
			if(bankSlips.getStatus().equals(BankSlipsStatus.PENDING)) {
				long daysExpired = DateUtils.differenceBetweenLocalDate(bankSlips.getDueDate(), new Date());
				BigDecimal fine = FinanceMathUtils.calculateSimpleFine(bankSlips.getTotalInCents(), daysExpired);
				bankSlips.setFine(fine);
			}
			
			return bankSlips;
		} catch (NoSuchElementException e) {
			throw new BankSlipsNotFoundException(ErrorMessages.BANKSLIPS_NOT_FOUND + bankSlipsId );
		}
	}
	
	/*
	 * bulk save synchronous
	 */
	public void bulkSave(List<BankSlips> slips) { 
        List<CompletableFuture<Void>> futures = slips.stream()
                .map(slip -> CompletableFuture.runAsync(() -> {
                	bankSlipsRepository.save(slip);
                }, executor))
                .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
		
	}
	
	@Override
	@Transactional
	public BankSlips edit(String id, Consumer<BankSlips> extraUpdates) {
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

	@Override
	public UUID startBulkSave(List<BankSlips> slips) {
	    BulkUploadJob job = new BulkUploadJob();
	    job.setStatus(BulkJobStatus.PENDING);
	    job.setTotalRecords(slips.size());
	    job.setStartedAt(LocalDateTime.now());

	    jobsRepository.save(job);

	    asyncService.bulkSaveAsync(job.getId(), slips);

	    return job.getId();
	}
	
    public void saveAll(List<BankSlips> slips) {
        bankSlipsRepository.saveAll(slips);
    }

}
