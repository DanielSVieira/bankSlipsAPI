package com.bankslips.utils;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.bankslips.exception.InvalideProvidedDateException;

public class DateUtilsTest {
	
    @Test
    public void oneDayDifference() {
    	Date startDate = getTodayDate();
    	Date endDate = addDays(1);

    	assertEquals(1, DateUtils.differenceBetweenLocalDate(startDate, endDate));
    }
    
    @Test
    public void minusOneDayDifference() {
    	Date startDate = getTodayDate();
    	Date endDate = addDays(-1);
    	
    	assertEquals(-1, DateUtils.differenceBetweenLocalDate(startDate, endDate));
    }
    
    @Test
    public void tenDayDifference() {
    	Date startDate = getTodayDate();
    	Date endDate = addDays(10);
    	
    	assertEquals(10, DateUtils.differenceBetweenLocalDate(startDate, endDate));
    }
    
    
    @Test(expected = InvalideProvidedDateException.class)
    public void nullPointerAvoid() {
    	Date startDate = null;
    	Date endDate = getTodayDate();
    	
    	DateUtils.differenceBetweenLocalDate(startDate, endDate);
    }
    
    @Test(expected = InvalideProvidedDateException.class)
    public void nullPointerAvoidAtEndLocalDate() {
    	Date startDate = getTodayDate();
    	Date endDate = null;
    	
    	DateUtils.differenceBetweenLocalDate(startDate, endDate);
    }
    
    @Test
    public void sameDate() {
    	Date startDate = getTodayDate();
    	Date endDate = getTodayDate();
    	
    	assertEquals(0, DateUtils.differenceBetweenLocalDate(startDate, endDate));
    }
    
    private Date getTodayDate() {
    	return Calendar.getInstance().getTime();
    }
    
    private Date addDays(int daysToadd) {
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, daysToadd);
    	return cal.getTime();
    }
    

}
