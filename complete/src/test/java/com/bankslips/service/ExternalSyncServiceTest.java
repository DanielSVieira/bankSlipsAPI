package com.bankslips.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.integration.exchange.ExchangeRateClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ExternalSyncServiceTest {

    @InjectMocks
    private ExternalSyncService externalSyncService;

    @Mock
    private ExchangeRateClient exchangeClient;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @BeforeEach
    void setup() {
        // inject executor
        org.springframework.test.util.ReflectionTestUtils.setField(externalSyncService, "executor", executor);
    }

    @Test
    void shouldReturnDTO() throws Exception {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse(
                "USD",
                LocalDate.now(),
                Map.of("EUR", BigDecimal.valueOf(0.92))
        );
        when(exchangeClient.getRates("USD")).thenReturn(Mono.just(mockResponse));

        CompletableFuture<ExchangeRateResponse> future = externalSyncService.syncAsync("USD");
        ExchangeRateResponse response = future.get();

        assertEquals("USD", response.base());
        assertEquals(1, response.rates().size());
        assertEquals(BigDecimal.valueOf(0.92), response.rates().get("EUR"));
    }

    @Test
    void shouldHandleEmptyMonoGracefully() throws Exception {
        when(exchangeClient.getRates("USD")).thenReturn(Mono.empty());

        CompletableFuture<ExchangeRateResponse> future = externalSyncService.syncAsync("USD");
        ExchangeRateResponse response = future.get();

        assertNull(response);
    }

    @Test
    void shouldPropagateApiExceptions() {
        when(exchangeClient.getRates("USD")).thenReturn(Mono.error(new RuntimeException("API down")));

        CompletableFuture<ExchangeRateResponse> future = externalSyncService.syncAsync("USD");

        RuntimeException ex = assertThrows(RuntimeException.class, future::join);
        assertEquals("API down", ex.getCause().getMessage());
    }
}
