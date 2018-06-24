package com.banklips.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.enums.BankSlipsStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@EqualsAndHashCode @ToString @Getter @Setter @AllArgsConstructor @NoArgsConstructor @RequiredArgsConstructor
public class BankSlips {
	
	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	@NonNull private String id;

	@NotNull
	(message=ErrorMessages.DUE_DATE_NOT_PROVIDED)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("due_date")
	@FutureOrPresent
	@NonNull private Date dueDate;
	
    @NotNull
    (message=ErrorMessages.TOTAL_IN_CENTS_NOT_PROVIDED)
    @DecimalMin("10000")
    @JsonProperty("total_in_cents")
    @NonNull private BigDecimal totalInCents;
    
    @NotNull
    (message=ErrorMessages.CUSTOMER_NOT_PROVIDED)
    @Size(min = 3, max = 255, message=ErrorMessages.CUSTOMER_INVALID_SIZE)
    @NonNull private String customer;
    
    @Enumerated(value = EnumType.STRING)
    private BankSlipsStatus status = BankSlipsStatus.PENDING;
    
    @Transient
	private BigDecimal fine;
	
}
