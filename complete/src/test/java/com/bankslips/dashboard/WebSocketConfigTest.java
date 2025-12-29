package com.bankslips.dashboard;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.SimpleBrokerRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import com.bankslips.config.BaseTest;
import com.bankslips.dashboard.config.WebSocketConfig;


class WebSocketConfigTest extends BaseTest {

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

