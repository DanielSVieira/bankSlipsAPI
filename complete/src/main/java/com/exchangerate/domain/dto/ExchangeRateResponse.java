package com.exchangerate.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.exchangerate.domain.ExchangeRate;

public record ExchangeRateResponse(
        String base,
        LocalDate date,
        Map<String, BigDecimal> rates
) {
	
    public ExchangeRate toEntity() {
        ExchangeRate entity = new ExchangeRate();
        entity.setCurrency(base);
        entity.setRateDate(date);
        entity.setRate(
            rates.values().iterator().next()
        );

        return entity;
    }
	
	
	
}

