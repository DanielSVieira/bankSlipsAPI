package com.bankslips.dashboard.dto;

import java.math.BigDecimal;

import com.bankslips.enums.BankSlipsStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BankslipSummaryDTO {
    private BankSlipsStatus status;       
    private long count;          
    private BigDecimal totalAmount;    

}
