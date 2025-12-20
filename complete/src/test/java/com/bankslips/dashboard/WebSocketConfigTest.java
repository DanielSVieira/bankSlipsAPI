package com.bankslips.dashboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bankslips.Application;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class WebSocketConfigTest {

    @Test
    void configureMessageBroker_shouldEnableSimpleBrokerAndSetPrefix() {
        WebSocketConfig config = new WebSocketConfig();

        // Mock the MessageBrokerRegistry
        MessageBrokerRegistry registry = Mockito.mock(MessageBrokerRegistry.class);
        SimpleBrokerRegistration simpleBroker = Mockito.mock(SimpleBrokerRegistration.class);

        // When enableSimpleBroker is called, return the mocked SimpleBrokerRegistration
        Mockito.when(registry.enableSimpleBroker("/topic")).thenReturn(simpleBroker);

        // You can also chain methods on SimpleBrokerRegistration if needed
        Mockito.when(simpleBroker.setTaskScheduler(Mockito.any())).thenReturn(simpleBroker);

        // Call the method under test
        config.configureMessageBroker(registry);

        // Verify interactions
        Mockito.verify(registry).enableSimpleBroker("/topic");
        Mockito.verify(registry).setApplicationDestinationPrefixes("/app");
    }
}
