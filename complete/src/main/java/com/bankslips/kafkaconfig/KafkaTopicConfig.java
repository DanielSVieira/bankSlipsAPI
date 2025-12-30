package com.bankslips.kafkaconfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.bankslips.config.KafkaTopicProperties;

@Configuration
public class KafkaTopicConfig {
	
	@Autowired
	private KafkaTopicProperties kafkaTopics;
	

    @Bean
    public NewTopic exchangeRatesTopic() {
        return TopicBuilder.name(kafkaTopics.getExchangeRates())
                .build();
       
    }
}

