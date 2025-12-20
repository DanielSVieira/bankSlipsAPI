package com.bankslips.service.implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
//        List<List<BankSlips>> batches = new ArrayList<>();
//
//        for (int i = 0; i < slips.size(); i += batchSize) {
//            batches.add(slips.subList(i, Math.min(i + batchSize, slips.size())));
//        }
        List<List<BankSlips>> batches = createSlipBatches(slips, batchSize);

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


	//TODO to add a controller to use it and test it
    @Transactional
    public BankSlips paySlip(String id) {
        BankSlips slip = bankSlipsRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank slip not found"));

        if (!slip.getStatus().equals(BankSlipsStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bank slip is not pending");
        }
        

        slip.setStatus(BankSlipsStatus.PAID);
        slip.setPaidAt(LocalDateTime.now());

        return bankSlipsRepository.save(slip);
    }
    
    //TODO extrair para um service
    public BankSlips cancelSlip(String id) { 
    	BankSlips slip = bankSlipsRepository.findById(id)
    		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank slip is not found"));
    	
        if (!slip.getStatus().equals(BankSlipsStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bank slip is not pending");
        }
        
        slip.setStatus(BankSlipsStatus.CANCELED);

        return bankSlipsRepository.save(slip);
    }
    

	private List<List<BankSlips>> createSlipBatches(List<BankSlips> slips, int batchSize) {
		List<List<BankSlips>> batches = 
        	    IntStream.range(0, (slips.size() + batchSize - 1) / batchSize)
        	             .mapToObj(i -> slips.subList(i * batchSize, Math.min((i + 1) * batchSize, slips.size())))
        	             .toList();
		return batches;
	}

}
