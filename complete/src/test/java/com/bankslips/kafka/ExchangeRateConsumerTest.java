package com.bankslips.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;
import com.bankslips.kafkaconfig.ExchangeRateConsumer;
import com.bankslips.repository.ExchangeRateRepository;
import com.bankslips.testutils.TestUtils;

class ExchangeRateConsumerTest {

    private ExchangeRateConsumer consumer;

    @Mock
    private ExchangeRateRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        consumer = new ExchangeRateConsumer();
        ReflectionTestUtils.setField(consumer, "exchangeRateRepository", repository);
    }

    @Test
    void shouldSaveDTOViaConsume() {
        ExchangeRateResponse dto = TestUtils.createExchangeRateMockData();
        consumer.consume(dto);

        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldIgnoreNullDTO() {
        assertDoesNotThrow(() -> consumer.consume(null));
        verify(repository, never()).save(any());
    }

    @Test
    void shouldHandleDuplicateDataGracefully() {
        ExchangeRateResponse dto = TestUtils.createExchangeRateMockData();
        doThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate"))
                .when(repository).save(any());

        assertDoesNotThrow(() -> consumer.consume(dto));
        verify(repository, times(1)).save(any());
    }

    @Test
    void shouldPropagateUnexpectedExceptions() {
        ExchangeRateResponse dto = TestUtils.createExchangeRateMockData();
        doThrow(new RuntimeException("DB down")).when(repository).save(any());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> consumer.consume(dto));
        assertEquals("DB down", ex.getMessage());
    }

    @Test
    void shouldPersistMultipleCurrencies() {
        ExchangeRateResponse usd = new ExchangeRateResponse("USD", LocalDate.now(), Map.of("EUR", BigDecimal.valueOf(0.92)));
        ExchangeRateResponse eur = new ExchangeRateResponse("EUR", LocalDate.now(), Map.of("USD", BigDecimal.valueOf(1.08)));

        consumer.consume(usd);
        consumer.consume(eur);

        verify(repository, times(2)).save(any());
    }
}
