package com.bankslips.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import com.bankslips.config.BaseTest;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.interfaces.IBankSlipsService;

public class BankSlipsServiceTest extends BaseTest {

    @Test
    void paySlipShouldPublishDashboardEvent() {
        IBankSlipsService service = mock(IBankSlipsService.class, CALLS_REAL_METHODS);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        UUID id = UUID.randomUUID();

        // Mock the edit method called inside paySlip
        when(service.edit(eq(id), any())).thenAnswer(invocation -> {
            BankSlips slip = new BankSlips();
            slip.setId(id);
            slip.setStatus(BankSlipsStatus.PAID);
            return slip;
        });

        // Call the default method
        service.paySlip(id, publisher);

        // Verify that the DashboardUpdateEvent was published
        ArgumentCaptor<DashboardUpdateEvent> captor = ArgumentCaptor.forClass(DashboardUpdateEvent.class);
        verify(publisher, times(1)).publishEvent(captor.capture());

        DashboardUpdateEvent event = captor.getValue();
        assert(event.getSource() == service);
    }
    
    @Test
    void cancelSlipShouldPublishDashboardEvent() {
        IBankSlipsService service = mock(IBankSlipsService.class, CALLS_REAL_METHODS);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        UUID id = UUID.randomUUID();

        // Mock edit method
        when(service.edit(eq(id), any())).thenAnswer(invocation -> {
            BankSlips slip = new BankSlips();
            slip.setId(id);
            slip.setStatus(BankSlipsStatus.CANCELED);
            return slip;
        });

        // Call cancelSlip (currently does NOT publish event)
        service.cancelSlip(id, publisher);

        // Verify event is published
        ArgumentCaptor<DashboardUpdateEvent> captor = ArgumentCaptor.forClass(DashboardUpdateEvent.class);
        verify(publisher, times(1)).publishEvent(captor.capture());

        DashboardUpdateEvent event = captor.getValue();
        assert(event.getSource() == service);
    }
}
