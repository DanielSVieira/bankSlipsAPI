package com.bankslips.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankslips.domain.exchangerate.ExchangeRate;
import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.repository.ExchangeRateRepository;
import com.bankslips.service.interfaces.IApiService;

@Service
public class ExternalSyncService implements IApiService<ExchangeRateResponse> {

	@Autowired
    private ExchangeRateClient exchangeClient;
	
	@Autowired
    private ExchangeRateRepository exchangeRateRepository;
	
	@Autowired
    private ExecutorService executor;

	@Override
	public CompletableFuture<ExchangeRateResponse> syncAsync(String currency) {
	    return CompletableFuture.supplyAsync(() -> {
	        ExchangeRateResponse response = exchangeClient.getRates(currency).block();
	        saveIfNotExists(response);
	        return response;
	    }, executor);
	}

	@Override
	@Transactional
	public void saveIfNotExists(ExchangeRateResponse data) {
		ExchangeRate exchangeRate = data.toEntity();
		exchangeRateRepository.findByCurrencyAndRateDate(
				exchangeRate.getCurrency(), exchangeRate.getRateDate()
		    ).orElseGet(() -> exchangeRateRepository.save(exchangeRate));
		
	}

}
