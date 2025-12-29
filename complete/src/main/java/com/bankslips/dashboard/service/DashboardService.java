package com.bankslips.dashboard.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankslips.dashboard.dto.BankslipSummaryDTO;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.interfaces.IBankSlipsService;

@Service
public class DashboardService {

	@Autowired
    private IBankSlipsService bankSlipsService;

    public BankslipSummaryDTO getSummaryByStatus(BankSlipsStatus status) {
        List<BankSlips> slips = bankSlipsService.findByStatus(status);

        long count = slips.size();
        BigDecimal totalAmount = slips.stream()
                                       .map(BankSlips::getTotalInCents)
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BankslipSummaryDTO(status, count, totalAmount);
    }
}
