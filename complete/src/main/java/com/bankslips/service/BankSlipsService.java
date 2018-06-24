package com.bankslips.service;

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
	
}
