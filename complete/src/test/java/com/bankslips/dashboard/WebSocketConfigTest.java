package com.bankslips.dashboard;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import com.bankslips.Application;
import com.bankslips.dashboard.config.WebSocketConfig;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(WebSocketConfig.class)
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false",
	    "spring.kafka.admin.auto-create=false" // Add this
	})
class WebSocketConfigTest {

	@Autowired
    private WebSocketConfig config;
	
	// This creates a "dummy" bean that satisfies the Kafka requirements 
    // without actually starting any Kafka logic.
    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    
    @MockBean
    private KafkaAdmin kafkaAdmin;

    @Test
    void shouldConfigureMessageBroker() {
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
        SimpleBrokerRegistration broker = mock(SimpleBrokerRegistration.class);

        when(registry.enableSimpleBroker("/topic")).thenReturn(broker);

        config.configureMessageBroker(registry);

        verify(registry).enableSimpleBroker("/topic");
        verify(registry).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void shouldRegisterStompEndpoint() {
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration endpoint = mock(StompWebSocketEndpointRegistration.class);

        when(registry.addEndpoint("/ws")).thenReturn(endpoint);
        when(endpoint.setAllowedOriginPatterns("*")).thenReturn(endpoint);

        config.registerStompEndpoints(registry);

        verify(registry).addEndpoint("/ws");
        verify(endpoint).withSockJS();
    }
}

