package com.bankslips.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.banklips.domain.BankSlips;
import com.bankslips.Application;
import com.bankslips.kafkaconfig.ExchangeRateConsumer;
import com.bankslips.repository.BankSlipsRepository;
import com.bankslips.service.interfaces.BankSlipsService;
import com.bankslips.service.interfaces.PersistanceService;
import com.bankslips.testutils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
	    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
	    "spring.kafka.listener.auto-startup=false"
	})
public class BankSlipsBulkControllerTest {
	
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BankSlipsService bankSlipsService;
    
    @MockBean
    private ExchangeRateConsumer exchangeRateConsumer;
    
    @Test
    void bulkUploadSuccess() throws Exception {
    	int totalRecords = 100;
        List<BankSlips> bankSlipsList = TestUtils.generateValidBankSlipsList(totalRecords);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(bankSlipsList);

        mockMvc.perform(post("/rest/bankslips/bulk/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isAccepted());

        verify(bankSlipsService).bulkSaveAsync(anyList());
    }
    
    @Test
    void bulkEmptyListUpload() throws Exception {
        List<BankSlips> bankSlipsList = new ArrayList<BankSlips>();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(bankSlipsList);

        mockMvc.perform(post("/rest/bankslips/bulk/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isAccepted());

        verify(bankSlipsService).bulkSaveAsync(anyList());
    }
    
    @Test
    void bulkInvalidPayloadUpload() throws Exception {

        mockMvc.perform(post("/rest/bankslips/bulk/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());
    }        
    

}
