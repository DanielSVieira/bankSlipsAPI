package com.bankslips.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "spring.kafka.topics")
@Getter @Setter
public class KafkaTopicProperties {
    private String exchangeRates;
    private String exchangeRatesGroupId;
    private String exchangeRatesContainerFactory;

}
