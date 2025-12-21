package com.bankslips.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.exchangerate.domain.dto.ExchangeRateResponse;

@Service
@Profile("!test")
public class ExchangeRateScheduler {
	
	@Autowired
    private ExternalSyncService exchangeRateService;
	
	@Autowired
    private KafkaTemplate<String, ExchangeRateResponse> kafkaTemplate;


	@Scheduled(fixedRate = 300000) // every 5 minutes
	public void syncExchangeRates() {
	    List<String> currencies = List.of("USD", "EUR", "BRL");
	    for (String currency : currencies) {
	        exchangeRateService.syncAsync(currency) 
	            .thenAccept(response -> {
	                kafkaTemplate.send("exchange-rates", response); 
	            });
	    }
	}

}
