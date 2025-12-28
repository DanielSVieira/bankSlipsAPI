package com.bankslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bankslips.Application;
import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.testutils.TestUtils;

import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false"
	})
class ExternalSyncServiceIntegrationTest {

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