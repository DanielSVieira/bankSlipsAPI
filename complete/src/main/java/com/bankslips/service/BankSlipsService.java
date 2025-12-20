package com.bankslips.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import com.banklips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

public interface BankSlipsService {

	public BankSlips create(BankSlips bankSlips, BindingResult bindingResult);
	public Page<BankSlips> list(Pageable pageable) ;
	public BankSlips show(String bankSlipsId);
	public BankSlips updateBankSlipsStatus(BankSlips bankSlips, BankSlipsStatus bankSlipsStatus);
	public void bulkSave(List<BankSlips> bankSlips);
	public BankSlips paySlip(String id);
	public BankSlips cancelSlip(String id);
	public CompletableFuture<Map<String, Object>> bulkSaveAsync(List<BankSlips> slips);
	
	
}
