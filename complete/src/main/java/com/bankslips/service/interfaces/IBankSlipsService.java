package com.bankslips.service.interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.context.ApplicationEventPublisher;

import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

public interface IBankSlipsService extends IPersistanceService<BankSlips> {
    
	default BankSlips paySlip(UUID id, ApplicationEventPublisher eventPublisher) {
	    return edit(id, slip -> {
	        slip.setStatus(BankSlipsStatus.PAID);
	        slip.setPaidAt(LocalDateTime.now());
	    }, eventPublisher);
	}

	default BankSlips cancelSlip(UUID id, ApplicationEventPublisher eventPublisher) {
	    return edit(id, slip -> {
	        slip.setStatus(BankSlipsStatus.CANCELED);
	        slip.setPaidAt(LocalDateTime.now());
	    }, eventPublisher);
	}
	
	default BankSlips updateBankSlipsStatus(BankSlips bankSlips, 
			BankSlipsStatus newStatus, ApplicationEventPublisher eventPublisher) {
	    return edit(bankSlips.getId(), slip -> {
	        slip.setStatus(newStatus);
	    }, eventPublisher);
	}
	
	default BankSlips edit(UUID id, Consumer<BankSlips> extraUpdates, ApplicationEventPublisher publisher) {
	    BankSlips bankSlipsUpdated = edit(id, extraUpdates);
	    if (!bankSlipsUpdated.getStatus().equals(bankSlipsUpdated.getOldStatus())) {
	        publisher.publishEvent(new DashboardUpdateEvent(this));
	    }
	    return bankSlipsUpdated;
	}
	
	List<BankSlips> findByStatus(BankSlipsStatus status);
}