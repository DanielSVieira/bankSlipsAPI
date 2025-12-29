package com.bankslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bankslips.config.BaseTest;
import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.testutils.TestUtils;

import reactor.core.publisher.Mono;

class ExternalSyncServiceIntegrationTest extends BaseTest {

    @Autowired
    private ExternalSyncService externalSyncService;

    @MockBean
    ExchangeRateClient exchangeClient;

    @Test
    void shouldPersistRate() {
        when(exchangeClient.getRates("USD"))
        .thenReturn(Mono.just(TestUtils.createExchangeRateMockData()));

        ExchangeRateResponse response = externalSyncService.syncAsync("USD").join();

	    assertEquals("USD", response.base());
    }
}