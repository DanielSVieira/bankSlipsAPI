package com.bankslips.kafkaconfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.repository.ExchangeRateRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExchangeRateConsumer {

	@Autowired
    private ExchangeRateRepository exchangeRateRepository;
	
    @Value("${spring.kafka.topics.exchange-rates}")
    private String exchangeRatesTopic;

    @Value("${spring.kafka.topics.exchange-rates-group-id}")
    private String groupId;

    @Value("${spring.kafka.topics.exchange-rates-container-factory}")
    private String containerFactory;

    @KafkaListener(
        topics = "${spring.kafka.topics.exchange-rates}",
        groupId = "${spring.kafka.topics.exchange-rates-group-id}",
        containerFactory = "${spring.kafka.topics.exchange-rates-container-factory}"
    )
    public void consume(ExchangeRateResponse response) {
        try {
            exchangeRateRepository.save(response.toEntity());
        } catch (DataIntegrityViolationException e) {
            log.info("Exchange rate already exists for {} on {}", response.base(), response.date());
        }
    }

}

