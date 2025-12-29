package com.bankslips.dashboard;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.bankslips.config.BaseTest;
import com.bankslips.dashboard.controller.DashboardWebSocketController;
import com.bankslips.dashboard.dto.BankslipSummaryDTO;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.dashboard.service.DashboardService;
import com.bankslips.enums.BankSlipsStatus;

public class DashboardWebSocketControllerTest extends BaseTest {
	
    @Autowired
    private DashboardWebSocketController controller;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void onDashboardUpdateShouldSendMessages() {
        BankslipSummaryDTO pending = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 2, BigDecimal.valueOf(5000));
        BankslipSummaryDTO paid = new BankslipSummaryDTO(BankSlipsStatus.PAID, 1, BigDecimal.valueOf(2000));

        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING)).thenReturn(pending);
        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PAID)).thenReturn(paid);

        // Simulate the event
        controller.onDashboardUpdate(new com.bankslips.dashboard.events.DashboardUpdateEvent(this));

        // Verify messages sent
        verify(messagingTemplate, times(1)).convertAndSend("/topic/dashboard/pending", pending);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/dashboard/paid", paid);
    }
    
    @Test
    void broadcastWithEmptySummaries() {
        BankslipSummaryDTO pending = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 0, BigDecimal.ZERO);
        BankslipSummaryDTO paid = new BankslipSummaryDTO(BankSlipsStatus.PAID, 0, BigDecimal.ZERO);

        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING)).thenReturn(pending);
        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PAID)).thenReturn(paid);

        controller.onDashboardUpdate(new DashboardUpdateEvent(this));

        verify(messagingTemplate).convertAndSend("/topic/dashboard/pending", pending);
        verify(messagingTemplate).convertAndSend("/topic/dashboard/paid", paid);
    }

    @Test
    void broadcastWithNullPaidSummary() {
        BankslipSummaryDTO pending = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 5, BigDecimal.valueOf(10000));

        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING)).thenReturn(pending);
        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PAID)).thenReturn(null);

        controller.onDashboardUpdate(new DashboardUpdateEvent(this));

        verify(messagingTemplate).convertAndSend("/topic/dashboard/pending", pending);
    }
    
    @Test
    void multipleEventsBroadcastCorrectly() {
        BankslipSummaryDTO pending1 = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 5, BigDecimal.valueOf(1000));
        BankslipSummaryDTO paid1 = new BankslipSummaryDTO(BankSlipsStatus.PAID, 2, BigDecimal.valueOf(500));

        BankslipSummaryDTO pending2 = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 3, BigDecimal.valueOf(800));
        BankslipSummaryDTO paid2 = new BankslipSummaryDTO(BankSlipsStatus.PAID, 4, BigDecimal.valueOf(1200));

        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING))
            .thenReturn(pending1)
            .thenReturn(pending2);
        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PAID))
            .thenReturn(paid1)
            .thenReturn(paid2);

        controller.onDashboardUpdate(new DashboardUpdateEvent(this));
        controller.onDashboardUpdate(new DashboardUpdateEvent(this));

        verify(messagingTemplate, times(2)).convertAndSend(eq("/topic/dashboard/pending"), any(Object.class));
        verify(messagingTemplate, times(2)).convertAndSend(eq("/topic/dashboard/paid"), any(Object.class));
    }

}
