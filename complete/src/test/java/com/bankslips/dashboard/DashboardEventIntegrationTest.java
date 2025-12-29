package com.bankslips.dashboard;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.bankslips.config.BaseTest;
import com.bankslips.dashboard.dto.BankslipSummaryDTO;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.dashboard.service.DashboardService;
import com.bankslips.enums.BankSlipsStatus;


class DashboardEventIntegrationTest extends BaseTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;


    @Test
    void eventPublishesWebSocketMessages() {
        BankslipSummaryDTO pending = new BankslipSummaryDTO(BankSlipsStatus.PENDING, 3, BigDecimal.valueOf(2000));
        BankslipSummaryDTO paid = new BankslipSummaryDTO(BankSlipsStatus.PAID, 1, BigDecimal.valueOf(500));

        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PENDING)).thenReturn(pending);
        when(dashboardService.getSummaryByStatus(BankSlipsStatus.PAID)).thenReturn(paid);

        // Publish event via Spring context
        publisher.publishEvent(new DashboardUpdateEvent(this));

        verify(messagingTemplate, times(1)).convertAndSend("/topic/dashboard/pending", pending);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/dashboard/paid", paid);
    }
}
