package com.bankslips.testutils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.banklips.domain.BankSlips;

public class TestUtils {
	
	private static String CUSTOMER_PREFIX = "Customer";
	private static Random random;
	
    public static List<BankSlips> generateValidBankSlipsList(int numberOdRecords) {
    	
        random = new Random();

        return IntStream.range(0, numberOdRecords)
                .mapToObj(i -> {
                    BankSlips bankSlips = new BankSlips();

                    bankSlips.setCustomer(CUSTOMER_PREFIX + (random.nextInt(900) + 100));
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, random.nextInt(30) + 1);
                    bankSlips.setDueDate(cal.getTime());
                    bankSlips.setTotalInCents(generateRandomNumberBiggerThan10000());

                    return bankSlips;
                })
                .collect(Collectors.toList());
    }
    
    private static BigDecimal generateRandomNumberBiggerThan10000() {
    	return new BigDecimal(10000 + random.nextInt(90000));
    }
	

}
