package com.bankslips.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bankslips.config.KafkaTopicProperties;
import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("!test")
public class ExchangeRateScheduler {
	
	@Autowired
    private ExternalSyncService exchangeRateService;
	
	@Autowired
    private KafkaTemplate<String, ExchangeRateResponse> kafkaTemplate;
	
	@Autowired
	private KafkaTopicProperties kafkaTopics;

	
	private final List<String> CURRENCIES_LIST = List.of("USD", "EUR", "BRL");


	@Scheduled(fixedRate = 300000) //5 minutes
	public void syncExchangeRates() {
	    for (String currency : CURRENCIES_LIST) {
	        exchangeRateService.syncAsync(currency)
	            .thenAccept(response -> {
	                log.debug("Publishing to Kafka: " + currency);
	                kafkaTemplate.send(kafkaTopics.getExchangeRates(), response);
	            })
	            .exceptionally(ex -> {
	                log.error("Error fetching rates for " + currency, ex);
	                return null;
	            });
	    }
	}


}
