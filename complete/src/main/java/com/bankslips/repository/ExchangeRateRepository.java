package com.bankslips.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankslips.domain.exchangerate.ExchangeRate;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
	
    Optional<ExchangeRate> findByCurrencyAndRateDate(String currency, LocalDate rateDate);
    Optional<ExchangeRate> findByCurrency(String currency);

}
