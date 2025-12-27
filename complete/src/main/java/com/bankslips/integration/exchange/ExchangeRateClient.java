package com.bankslips.integration.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.exception.ExternalApiUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import reactor.core.publisher.Mono;

@Component
public class ExchangeRateClient {

    private final WebClient webClient;
    private final String BASE_URL_RATES = "https://api.frankfurter.app/";
    private final String URI_RATES = "latest?from={currency}";
    private final String RETRY_NAME = "exchange-api";
    private final String FALLBACK_METHOD_NAME = "fallback";

    public ExchangeRateClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl(BASE_URL_RATES).build();
    }

    @Retry(name = RETRY_NAME)
    @CircuitBreaker(name = RETRY_NAME, fallbackMethod = FALLBACK_METHOD_NAME)
    public Mono<ExchangeRateResponse> getRates(String currency) {
    	Mono<ExchangeRateResponse> ratesResponse = webClient.get()
            .uri(URI_RATES, currency)
            .retrieve()
            .bodyToMono(ExchangeRateResponse.class);
    	return ratesResponse;
    }

    private Mono<ExchangeRateResponse> fallback(Throwable ex) {
    	//TODO
    	//post to some observability channel (Sentry,  Slack...)
        return Mono.error(new ExternalApiUnavailableException(ex.getMessage()));
    }
}

