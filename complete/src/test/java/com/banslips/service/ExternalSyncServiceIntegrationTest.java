package com.banslips.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.repository.ExchangeRateRepository;
import com.bankslips.service.ExternalSyncService;
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

    @Autowired
    private ExchangeRateRepository repository;

    @MockBean
    ExchangeRateClient exchangeClient;

    @Test
    void shouldPersistRate() {
        when(exchangeClient.getRates("USD"))
            .thenReturn(Mono.just(TestUtils.createExchangeRateMockData()));

        externalSyncService.syncAsync("USD").join();

        assertTrue(repository.findByCurrency("USD").isPresent());
    }
}