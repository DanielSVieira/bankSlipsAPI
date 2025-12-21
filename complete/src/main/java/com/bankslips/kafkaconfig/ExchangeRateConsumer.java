package com.bankslips.kafkaconfig;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bankslips.repository.ExchangeRateRepository;
import com.exchangerate.domain.ExchangeRate;
import com.exchangerate.domain.dto.ExchangeRateResponse;

@Component
public class ExchangeRateConsumer {

	@Autowired
    private ExchangeRateRepository exchangeRateRepository;
	
	private final String EXCHANGE_RATES_TOPICS = "exchange-rates";
	private final String EXCHANGE_RATES_GROUP_ID = "exchange-rate-group";
	private final String EXCHANGE_RATES_CONTAINER_FACTORY = "kafkaListenerContainerFactory";
    
    @KafkaListener(
        topics = EXCHANGE_RATES_TOPICS,
        groupId = EXCHANGE_RATES_GROUP_ID,
        containerFactory = EXCHANGE_RATES_CONTAINER_FACTORY
    )
    public void consume(ExchangeRateResponse response) {

        if (!isRecordAlreadyPersisted(response)) {
            exchangeRateRepository.save(response.toEntity());
        }
    }

    /**
     * idempotencyCheck. Avoid duplication
     * @param response
     * @return
     */
	private boolean isRecordAlreadyPersisted(ExchangeRateResponse response) {
		Optional<ExchangeRate> exists = exchangeRateRepository
                .findByCurrencyAndRateDate(response.base(), response.date());

		return exists.isPresent();
	}
}

