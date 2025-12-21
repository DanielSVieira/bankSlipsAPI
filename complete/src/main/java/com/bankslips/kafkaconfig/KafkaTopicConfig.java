package com.bankslips.kafkaconfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
	
	private final String RATES_TOPIC_NAME = "exchange-rates";   

    @Bean
    public NewTopic exchangeRatesTopic() {
        return TopicBuilder.name(RATES_TOPIC_NAME)
                .partitions(3)
                .replicas(1)
                .build();
    }
}

