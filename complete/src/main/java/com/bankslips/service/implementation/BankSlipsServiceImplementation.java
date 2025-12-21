package com.bankslips.service.implementation;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import com.banklips.domain.BankSlips;
import com.bankslips.contants.ErrorMessages;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.exception.BankSlipsContraintViolationException;
import com.bankslips.exception.BankSlipsNotFoundException;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.BankSlipsService;
import com.bankslips.utils.DateUtils;
import com.bankslips.utils.FinanceMathUtils;
import com.bankslips.validator.BankSlipsValidator;

import jakarta.transaction.Transactional;


@Service
public class BankSlipsServiceImplementation implements BankSlipsService{
	
	
	@Autowired
	private BankSlipsRepository bankSlipsRepository;
	
	@Autowired
	private ExecutorService executor;
	
	public BankSlips create(BankSlips bankSlips, BindingResult bindingResult)  {
		if (BankSlipsValidator.jsonContainsErrors(bindingResult)) {
			List<String> errorMessages = BankSlipsValidator.getErrorMessages(bindingResult);
			throw new BankSlipsContraintViolationException(errorMessages.toString(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return bankSlipsRepository.save(bankSlips);	
	}
	
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
	
	public BankSlips updateBankSlipsStatus(BankSlips bankSlips, BankSlipsStatus newStatus) {
		bankSlips.setStatus(newStatus);
		return bankSlipsRepository.save(bankSlips);
	}
	
	
	//TODO to add a controler to use it and test it
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
	
    //TODO to add a controller to use it and test it
    /**
     * Bulk save in chunks asynchronously
     */
    public CompletableFuture<Map<String, Object>> bulkSaveAsync(List<BankSlips> slips) {
        int batchSize = 500; // adjustable based on memory/DB
        List<BankSlips> uniqueSlips = removeDuplication(slips);
        List<List<BankSlips>> batches = createSlipBatches(uniqueSlips, batchSize);
        
        List<CompletableFuture<Void>> futures = batches.stream()
            .map(batch -> CompletableFuture.runAsync(() -> {
            	bankSlipsRepository.saveAll(batch); // save batch in one DB call
            }, executor))
            .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> Map.of(
                    "uploaded", slips.size(),
                    "timestamp", LocalDateTime.now()
                ));
    }


	private List<List<BankSlips>> createSlipBatches(List<BankSlips> slips, int batchSize) {
		List<List<BankSlips>> batches = 
        	    IntStream.range(0, (slips.size() + batchSize - 1) / batchSize)
        	             .mapToObj(i -> slips.subList(i * batchSize, Math.min((i + 1) * batchSize, slips.size())))
        	             .toList();
		return batches;
	}
	
	public List<BankSlips> removeDuplication(Collection<BankSlips> slips) {
	    Set<String> seenExternalIds = new HashSet<>();
	    List<BankSlips> uniqueSlips = new ArrayList<>(slips.size());

	    for (BankSlips slip : slips) {
	        if (seenExternalIds.add(slip.getExternalId())) {
	            uniqueSlips.add(slip);
	        }
	    }

	    return uniqueSlips;
	}
	

	@Override
	@Transactional
	public BankSlips edit(String id, Consumer<BankSlips> extraUpdates) {
        BankSlips slip = bankSlipsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank slip not found"));
        
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

}
