package com.bankslips.utils;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;


public class FinanceMathUtilsTest {
	
    @Test
    public void elevenDaysFine() {
    	BigDecimal totalInCents = new BigDecimal(10000);
    	long expiredDays = 11;
    	
    	assertEquals(new BigDecimal(100), FinanceMathUtils.calculateSimpleFine(totalInCents, expiredDays).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
    @Test
    public void minusTenDaysFine() {
    	BigDecimal totalInCents = new BigDecimal(10000);
    	long expiredDays = -20;
    	
    	assertEquals(new BigDecimal(0), FinanceMathUtils.calculateSimpleFine(totalInCents, expiredDays).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
    @Test
    public void tenTenDaysFine() {
    	BigDecimal totalInCents = new BigDecimal(10000);
    	long expiredDays = 10;
    	
    	assertEquals(new BigDecimal(50), FinanceMathUtils.calculateSimpleFine(totalInCents, expiredDays).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
    @Test
    public void zeroTenDaysFine() {
    	BigDecimal totalInCents = new BigDecimal(10000);
    	long expiredDays = 0;
    	
    	assertEquals(new BigDecimal(0), FinanceMathUtils.calculateSimpleFine(totalInCents, expiredDays).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
    @Test
    public void avoidNullPointerInValue() {
    	BigDecimal totalInCents = null;
    	long expiredDays = 0;
    	
    	assertEquals(new BigDecimal(0), FinanceMathUtils.calculateSimpleFine(totalInCents, expiredDays).setScale(0, BigDecimal.ROUND_DOWN));
    }
    
}
