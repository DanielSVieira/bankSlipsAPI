package com.bankslips.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

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
	public BankSlips edit(String id, Consumer<BankSlips> extraUpdates);
	public CompletableFuture<Map<String, Object>> bulkSaveAsync(List<BankSlips> slips);
	
	default BankSlips paySlip(String id) {
	    return edit(id, slip -> {
	        slip.setStatus(BankSlipsStatus.PAID);
	        slip.setPaidAt(LocalDateTime.now());
	    });
	}

	default BankSlips cancelSlip(String id) {
	    return edit(id, slip -> {
	        slip.setStatus(BankSlipsStatus.CANCELED);
	        slip.setPaidAt(LocalDateTime.now());
	    });
	}
}
