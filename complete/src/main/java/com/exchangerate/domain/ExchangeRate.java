package com.exchangerate.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "exchange_rates",
    uniqueConstraints = @UniqueConstraint(columnNames = {"currency", "rate_date"})
)
@EqualsAndHashCode @ToString @Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ExchangeRate {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String currency; //@TODO. refactor to ENUM USD, EUR, BRL

    private BigDecimal rate;

    private LocalDate rateDate;

    @Nullable
    private LocalDateTime updatedAt;
}