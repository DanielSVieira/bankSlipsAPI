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


	@Scheduled(fixedRate = 300000) // every 5 minutes
	public void syncExchangeRates() {
	    for (String currency : CURRENCIES_LIST) {
	        exchangeRateService.syncAsync(currency) 
	            .thenAccept(response -> {
	            	log.debug("SUCCESS: Generated DTO for " + currency + ": " + response);
	                try {
						kafkaTemplate.send(kafkaTopics.getExchangeRates(), response); 
	                } catch (Exception e) {
	                    System.err.println("KAFKA ERROR: Could not send " + currency + " but DTO is valid.");
	                }
	            })
	            .exceptionally(ex -> {
	                System.err.println("SERVICE ERROR for " + currency + ": " + ex.getMessage());
	                return null;
	            });
	    }
	}

}
