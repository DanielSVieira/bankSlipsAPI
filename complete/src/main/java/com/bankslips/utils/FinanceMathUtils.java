package com.bankslips.utils;

import java.math.BigDecimal;

public class FinanceMathUtils {
	
	private static final BigDecimal LESS_THAN_TEN_DAYS_RATE = new BigDecimal(0.005);
	private static final BigDecimal MORE_THAN_TEN_DAYS_RATE = new BigDecimal(0.01);
	
	public static BigDecimal calculateSimpleFine(BigDecimal originalValue, long daysExpired) {
		BigDecimal fine = new BigDecimal(0);
		if(daysExpired > 0 && daysExpired <= 10) {
			fine = originalValue.multiply(LESS_THAN_TEN_DAYS_RATE);	
		} if(daysExpired > 10) {
			fine = originalValue.multiply(MORE_THAN_TEN_DAYS_RATE);
		}
		
		return fine;
	}

}
