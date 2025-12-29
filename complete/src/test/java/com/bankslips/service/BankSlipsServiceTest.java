package com.bankslips.service;

import static org.mockito.Mockito.*;

import java.util.UUID;

import com.bankslips.Application;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.interfaces.IBankSlipsService;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false"
	})
public class BankSlipsServiceTest {

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
