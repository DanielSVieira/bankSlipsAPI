package com.banslips.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.banklips.domain.BankSlips;
import com.bankslips.Application;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.BankSlipsService;
import com.bankslips.testutils.TestUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Transactional
public class BankSlipsServiceAsyncTest {
	
    @Autowired
    private BankSlipsService bankSlipsService;

    @Autowired
    private BankSlipsRepository bankSlipsRepository;
    
    @BeforeEach
    private void setp() { 
    	bankSlipsRepository.deleteAll();
    }

    @Test
    void shouldPersist100BankSlips() {
    	int totaRecords = 100;
        List<BankSlips> slips = TestUtils.generateValidBankSlipsList(totaRecords);

        CompletableFuture<Map<String, Object>> future =
                bankSlipsService.bulkSaveAsync(slips);

        future.join();

        assertEquals(totaRecords, bankSlipsRepository.count());
    }
    
    @Test
    void shouldRejectDuplicateBankSlips() {
    	int totaRecords = 100;
        List<BankSlips> slips = TestUtils.generateValidBankSlipsList(totaRecords);
        BankSlips slipWithDuplicateID =  TestUtils.generateBankSlipWiwithDuplicatedExternalID(slips.get(0));
        slips.add(slipWithDuplicateID);
        
        assertEquals(totaRecords + 1, slips.size());
        

        CompletableFuture<Map<String, Object>> future =
                bankSlipsService.bulkSaveAsync(slips);

        future.join();

        assertEquals(totaRecords, bankSlipsRepository.count());
    }

}
