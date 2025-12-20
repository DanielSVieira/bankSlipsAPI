package com.banslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.banklips.domain.BankSlips;
import com.bankslips.Application;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.BankSlipsService;
import com.bankslips.testutils.TestUtils;

@Transactional
@SpringBootTest(classes = Application.class)
public class BankSlipsServiceAsyncTest {
	
    @Autowired
    private BankSlipsService bankSlipsService;

    @Autowired
    private BankSlipsRepository bankSlipsRepository;

    @Test
    void shouldPersist100BankSlips() {
    	int totaRecords = 100;
        List<BankSlips> slips = TestUtils.generateValidBankSlipsList(totaRecords);

        CompletableFuture<Map<String, Object>> future =
                bankSlipsService.bulkSaveAsync(slips);

        future.join();

        assertEquals(totaRecords, bankSlipsRepository.count());
    }

}
