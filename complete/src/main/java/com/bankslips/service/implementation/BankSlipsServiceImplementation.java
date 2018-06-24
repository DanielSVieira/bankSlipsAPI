package com.bankslips.service.implementation;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

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


@Service
public class BankSlipsServiceImplementation implements BankSlipsService{
	
	@Autowired
	private BankSlipsRepository bankSlipsRepository;
	
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

}
