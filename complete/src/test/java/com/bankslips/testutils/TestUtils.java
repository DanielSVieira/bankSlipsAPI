package com.bankslips.testutils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bankslips.domain.BankSlips;
import com.bankslips.domain.exchangerate.dto.ExchangeRateResponse;

public class TestUtils {
	
	private static final String CUSTOMER_PREFIX = "Customer";
	private static Random random;
	private static final int EXTERNAL_ID_SIZE = 20; 
	
    public static List<BankSlips> generateValidBankSlipsList(int numberOdRecords) {
    	
        random = new Random();

        return IntStream.range(0, numberOdRecords)
                .mapToObj(i -> {
                	BankSlips bankSlips = generateValidBankSlip();


                    return bankSlips;
                })
                .collect(Collectors.toList());
    }
    
    public static BankSlips generateBankSlipWiwithDuplicatedExternalID(BankSlips bankSlips) {
        BankSlips bankSlipsWithDuplicated = new BankSlips();
        bankSlipsWithDuplicated.setCustomer("abc-duplicated");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 2);
        bankSlipsWithDuplicated.setDueDate(cal.getTime());
        bankSlipsWithDuplicated.setTotalInCents(new BigDecimal(10000));
        bankSlipsWithDuplicated.setExternalId(bankSlips.getExternalId());       
        return bankSlipsWithDuplicated;
    }
    
    public static BankSlips generateValidBankSlip() {
        BankSlips bankSlips = new BankSlips();
        bankSlips.setCustomer(CUSTOMER_PREFIX + (random.nextInt(900) + 100));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, random.nextInt(30) + 1);
        bankSlips.setDueDate(cal.getTime());
        bankSlips.setTotalInCents(generateRandomNumberBiggerThan10000());
        bankSlips.setExternalId(generateRandomString(EXTERNAL_ID_SIZE));     
        return bankSlips;
    }
    
    public static BankSlips generateBankSlipWithoutDueDate() {
        BankSlips bankSlips = new BankSlips();
        bankSlips.setCustomer(CUSTOMER_PREFIX + (random.nextInt(900) + 100));
        bankSlips.setTotalInCents(generateRandomNumberBiggerThan10000());
        bankSlips.setExternalId(generateRandomString(EXTERNAL_ID_SIZE));     
        return bankSlips;
    }    
    
    public static BankSlips generateBankSlipWithPastDueDate() {
    	BankSlips bankSlips = generateValidBankSlip();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
    	bankSlips.setDueDate(calendar.getTime());
    	
    	return bankSlips;
    }

    

    
    private static BigDecimal generateRandomNumberBiggerThan10000() {
    	return new BigDecimal(10000 + random.nextInt(90000));
    }
    
    public static String generateRandomString(int length) {
    	return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static ExchangeRateResponse createExchangeRateMockData() {
    	return new ExchangeRateResponse(
    		    "USD",
    		    LocalDate.now(),
    		    Map.of("EUR", BigDecimal.valueOf(0.93), "BRL", BigDecimal.valueOf(5.1))
    		);
    }
	

}
