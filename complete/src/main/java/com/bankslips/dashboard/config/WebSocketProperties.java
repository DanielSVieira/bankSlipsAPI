package com.bankslips.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "websocket.stomp")
@Getter @Setter
public class WebSocketProperties {

    private String endpoint;
    private String allowedOrigins;
    private String topicPrefix;
    private String appPrefix;

}
