package com.bankslips.dashboard;

import static org.mockito.Mockito.*;

import java.util.UUID;

import com.bankslips.Application;
import com.bankslips.dashboard.events.DashboardUpdateEvent;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.interfaces.IBankSlipsService;

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
@ActiveProfiles("test")
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false"
	})
class BankSlipsServiceEventTest {

    @Test
    void paySlipShouldPublishDashboardEvent() {
        IBankSlipsService service = mock(IBankSlipsService.class, CALLS_REAL_METHODS);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        UUID id = UUID.randomUUID();

        // Mock edit method
        when(service.edit(eq(id), any())).thenAnswer(invocation -> {
            BankSlips slip = new BankSlips();
            slip.setId(id);
            slip.setStatus(BankSlipsStatus.PAID);
            return slip;
        });

        // Call default method
        service.paySlip(id, publisher);

        // Verify event was published
        ArgumentCaptor<DashboardUpdateEvent> captor = ArgumentCaptor.forClass(DashboardUpdateEvent.class);
        verify(publisher, times(1)).publishEvent(captor.capture());
        DashboardUpdateEvent event = captor.getValue();

        assert(event.getSource() == service);
    }

}
