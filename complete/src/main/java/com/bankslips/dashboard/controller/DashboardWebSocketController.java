package com.bankslips.dashboard.controller;

import com.bankslips.dashboard.dto.BankslipSummaryDTO;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.dashboard.service.DashboardService;
import com.bankslips.enums.BankSlipsStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DashboardWebSocketController {

    private final DashboardService dashboardService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DashboardWebSocketController(DashboardService dashboardService,
                                        SimpMessagingTemplate messagingTemplate) {
        this.dashboardService = dashboardService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Call this method whenever a bankslip status changes
     */
    public void broadcastDashboardUpdate() {
        BankslipSummaryDTO pending = dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING);
        BankslipSummaryDTO paid = dashboardService.getSummaryByStatus(BankSlipsStatus.PAID);

        messagingTemplate.convertAndSend("/topic/dashboard/pending", pending);
        messagingTemplate.convertAndSend("/topic/dashboard/paid", paid);
    }
    
    @EventListener
    public void onDashboardUpdate(DashboardUpdateEvent event) {
        broadcastDashboardUpdate();
    }
}
