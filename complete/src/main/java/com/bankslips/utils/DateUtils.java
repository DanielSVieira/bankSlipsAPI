package com.bankslips.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import com.bankslips.contants.ErrorMessages;
import com.bankslips.exception.InvalideProvidedDateException;

public class DateUtils {
	
	public static long differenceBetweenLocalDate(Date startDate, Date endDate) {
		if(startDate == null || endDate == null) {
			throw new InvalideProvidedDateException(ErrorMessages.INVALID_LOCAL_DATE_PROVIDED);
		}
		
		LocalDate startLocalDate = resetTime(startDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate endLocalDate = resetTime(endDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    
		return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
	}
	
	private static Date resetTime(Date date) {
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

}
