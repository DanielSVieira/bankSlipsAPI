package com.bankslips.service.interfaces;

import java.time.LocalDateTime;

import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

public interface IBankSlipsService extends IPersistanceService<BankSlips> {
    
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
	
	default BankSlips updateBankSlipsStatus(BankSlips bankSlips, BankSlipsStatus newStatus) {
	    return edit(bankSlips.getId(), slip -> {
	        slip.setStatus(newStatus);
	    });
	}
}