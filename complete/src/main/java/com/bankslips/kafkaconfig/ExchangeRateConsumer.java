package com.bankslips.kafkaconfig;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bankslips.repository.ExchangeRateRepository;
import com.exchangerate.domain.dto.ExchangeRateResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExchangeRateConsumer {

	@Autowired
    private ExchangeRateRepository exchangeRateRepository;
	
	private final String EXCHANGE_RATES_TOPICS = "exchange-rates";
	private final String EXCHANGE_RATES_GROUP_ID = "exchange-rate-group";
	private final String EXCHANGE_RATES_CONTAINER_FACTORY = "kafkaListenerContainerFactory";
    
    @KafkaListener(
        topics = EXCHANGE_RATES_TOPICS,
        groupId = EXCHANGE_RATES_GROUP_ID,
        containerFactory = EXCHANGE_RATES_CONTAINER_FACTORY
    )
    
    public void consume(ExchangeRateResponse response) {
        try {
            exchangeRateRepository.save(response.toEntity());
        } catch (DataIntegrityViolationException e) {
            log.info("Exchange rate already exists for {} on {}", response.base(), response.date());
        }
    }

}

