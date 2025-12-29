package com.bankslips.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import com.bankslips.config.BaseTest;
import com.bankslips.contants.ErrorMessages;
import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.interfaces.IBankSlipsService;
import com.bankslips.testutils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;



public class BankSlipsControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private BindingResult bindingResult;
    
    @Autowired
    private IBankSlipsService bankSlipsService;


    @Test
    void saveBankSlipSuccessfully() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BankSlips bankSlips = TestUtils.generateValidBankSlip();
        String json = mapper.writeValueAsString(bankSlips);

        String result = mockMvc.perform(post("/rest/bankslips/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONObject jsonObject = new JSONObject(result);
        UUID id = UUID.fromString(jsonObject.getString("id"));

        mockMvc.perform(get("/rest/bankslips/" + id))
                .andDo(print())
                .andExpect(status().isOk());
    }
    
    @Test
    void saveBankSlipWithoutDueDate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BankSlips bankSlips = TestUtils.generateBankSlipWithoutDueDate();
        String json = mapper.writeValueAsString(bankSlips);

        mockMvc.perform(post("/rest/bankslips/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$[0]").value(ErrorMessages.DUE_DATE_NOT_PROVIDED));

    }   
    
    @Test
    void saveBankSlipWithAPastDueDate() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BankSlips bankSlips = TestUtils.generateBankSlipWithPastDueDate();
        String json = mapper.writeValueAsString(bankSlips);

        
        mockMvc.perform(post("/rest/bankslips/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$[0]").value(ErrorMessages.DUE_DATE_IN_PAST));
    }   
    
    @Test
    void payUnkownBankSlips() throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	BankSlips bankSlips = TestUtils.generateValidBankSlip();
    	String json = mapper.writeValueAsString(bankSlips);
    	
    	
    	mockMvc.perform(put("/rest/bankslips/pay/"+UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andReturn()
        .getResponse()
        .getContentAsString();
    }
    
    @Test
    void payBankSlips() throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	BankSlips bankSlips = TestUtils.generateValidBankSlip();
    	String json = mapper.writeValueAsString(bankSlips);
    	
    	BankSlips newBankSlips =  bankSlipsService.create(bankSlips, bindingResult);
    	
    	String result =  mockMvc.perform(put("/rest/bankslips/pay/"+newBankSlips.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    	
        JSONObject jsonObject = new JSONObject(result);
        assertEquals(BankSlipsStatus.PAID.toString(), jsonObject.getString("status"));
        
    }
    

    @Test
    void cancelBankSlip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BankSlips bankSlips = TestUtils.generateValidBankSlip();
        String json = mapper.writeValueAsString(bankSlips);
        
        BankSlips newBankSlips =  bankSlipsService.create(bankSlips, bindingResult);

        String result = mockMvc.perform(put("/rest/bankslips/cancel/"+newBankSlips.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        
        JSONObject jsonObject = new JSONObject(result);
        UUID id = UUID.fromString(jsonObject.getString("id"));

        String canceledSlip = mockMvc.perform(get("/rest/bankslips/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        JSONObject canceledSlipJsonObject = new JSONObject(canceledSlip);
        
        assertEquals(BankSlipsStatus.CANCELED.toString(), canceledSlipJsonObject.getString("status"));
        
    }    
    
}
