package com.banslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.bankslips.Application;
import com.bankslips.integration.exchange.ExchangeRateClient;
import com.bankslips.repository.ExchangeRateRepository;
import com.bankslips.service.ExternalSyncService;
import com.bankslips.testutils.TestUtils;
import com.exchangerate.domain.dto.ExchangeRateResponse;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ExternalSyncServiceTest {

    @InjectMocks
    private ExternalSyncService exchangeRateService;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private ExchangeRateClient exchangeClient;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @BeforeEach
    void setup() {
        // or use ReflectionTestUtils to inject if private field without setter
        ReflectionTestUtils.setField(exchangeRateService, "executor", executor);
        
        // Mockito setup for a sample response
        ExchangeRateResponse mockResponse = new ExchangeRateResponse(
            "USD",
            LocalDate.now(),
            Map.of("EUR", BigDecimal.valueOf(0.92))
        );

        Mockito.when(exchangeClient.getRates(Mockito.anyString()))
               .thenReturn(Mono.just(mockResponse));
    }

    @Test
    void syncRatesAsync_shouldInvokeApiAndSaveData() throws Exception {
        CompletableFuture<ExchangeRateResponse> future = exchangeRateService.syncAsync("USD");

        ExchangeRateResponse response = future.get(); // wait for completion

        assertEquals("USD", response.base());
        verify(exchangeRateRepository).save(any()); // check it saved to repository
    }
    

    @Test
    void syncExchangeRatesSuccessfulPersist() throws Exception {
        when(exchangeClient.getRates("USD"))
            .thenReturn(Mono.just(TestUtils.createExchangeRateMockData()));

        exchangeRateService.syncAsync("USD").join();

        verify(exchangeRateRepository, times(1)).save(any());
    }
    

}

