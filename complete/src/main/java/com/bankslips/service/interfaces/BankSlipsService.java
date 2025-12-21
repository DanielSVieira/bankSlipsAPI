package com.bankslips.service.interfaces;

import java.time.LocalDateTime;

import com.banklips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

public interface BankSlipsService extends PersistanceService<BankSlips> {
    
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