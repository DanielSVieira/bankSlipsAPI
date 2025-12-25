package com.bankslips.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.repository.ExchangeRateRepository;
import com.bankslips.service.interfaces.ApiService;
import com.exchangerate.domain.ExchangeRate;
import com.exchangerate.domain.dto.ExchangeRateResponse;

@Service
public class ExternalSyncService implements ApiService<ExchangeRateResponse> {

	@Autowired
    private ExchangeRateClient exchangeClient;
	
	@Autowired
    private ExchangeRateRepository exchangeRateRepository;
	
    private final ExecutorService executor;

    // Use @Qualifier to tell Spring exactly which bean to use
    public ExternalSyncService(@Qualifier("executorService") ExecutorService executor) {
        this.executor = executor;
    }

	@Override
	public CompletableFuture<ExchangeRateResponse> syncAsync(String currency) {
	    return CompletableFuture.supplyAsync(() -> {
	        ExchangeRateResponse response = exchangeClient.getRates(currency).block();
	        ExchangeRate test = response.toEntity();
	        exchangeRateRepository.save(response.toEntity());
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
