package com.bankslips.dashboard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bankslips.dashboard.dto.BankslipSummaryDTO;
import com.bankslips.dashboard.service.DashboardService;
import com.bankslips.enums.BankSlipsStatus;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
    private DashboardService dashboardService;

	@RequestMapping(value = "/summary", method = RequestMethod.GET)
    public List<BankslipSummaryDTO> getDashboardSummary() {
        return List.of(
            dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING),
            dashboardService.getSummaryByStatus(BankSlipsStatus.PAID)
        );
    }
}
