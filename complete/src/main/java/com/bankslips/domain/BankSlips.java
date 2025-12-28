package com.bankslips.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.utils.FinanceMathUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@EqualsAndHashCode @ToString @Getter @Setter @AllArgsConstructor @NoArgsConstructor @RequiredArgsConstructor
@Table(
	    name = "bank_slips",
	    uniqueConstraints = {
	        @UniqueConstraint(name = "uk_bank_slips_external_id", columnNames = "external_id")
	    },
	    indexes = {
	        @Index(name = "idx_bank_slips_status", columnList = "status"),
	        @Index(name = "idx_bank_slips_due_date", columnList = "due_date"),
	        @Index(name = "idx_bank_slips_paid_at", columnList = "paid_at")
	    }
	)
public class BankSlips {
	
	@Id
	@UuidGenerator
	@Column(name = "uuid", nullable = false, updatable = false)
	@NonNull private UUID id;
	
    @Column(name = "external_id", nullable = false, updatable = false)
    @NotBlank
    private String externalId;

	@NotNull
	(message=ErrorMessages.DUE_DATE_NOT_PROVIDED)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("due_date")
	@FutureOrPresent(message=ErrorMessages.DUE_DATE_IN_PAST)
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
    
    @Nullable
    @PastOrPresent
    private LocalDateTime paidAt;
    
    @Version
    private Long version;
    
    public void applyFineIfPending(LocalDate today) {
        if (status != BankSlipsStatus.PENDING) {
            return;
        }
        LocalDate due = dueDate.toInstant()
                               .atZone(ZoneId.systemDefault())
                               .toLocalDate();
        long daysExpired = Math.max(0,ChronoUnit.DAYS.between(due, today));
        setFine(FinanceMathUtils.calculateSimpleFine(totalInCents, daysExpired));
    }

	
}
