package com.bankslips.controller;

import com.banklips.domain.BankSlips;
import com.bankslips.Application;
import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.service.BankSlipsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class BankSlipsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private BindingResult bindingResult;
    
    @Autowired
    private BankSlipsService bankSlipsService;

    private BankSlips generateValidBankSlips() {
        BankSlips bankSlips = new BankSlips();
        bankSlips.setCustomer("abc");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        bankSlips.setDueDate(cal.getTime());
        bankSlips.setTotalInCents(new BigDecimal(10000));
        return bankSlips;
    }
    
    private BankSlips generateBankSlipsWithoutDueDate() {
        BankSlips bankSlips = new BankSlips();
        bankSlips.setCustomer("abc");
        bankSlips.setTotalInCents(new BigDecimal(10000));
        return bankSlips;
    }    

    @Test
    void saveBankSlipSuccessfully() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        BankSlips bankSlips = generateValidBankSlips();
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
        BankSlips bankSlips = generateBankSlipsWithoutDueDate();
        String json = mapper.writeValueAsString(bankSlips);

        mockMvc.perform(post("/rest/bankslips/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

    }    
    
    @Test
    void payUnkownBankSlips() throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	BankSlips bankSlips = generateValidBankSlips();
    	String json = mapper.writeValueAsString(bankSlips);
    	
    	mockMvc.perform(put("/rest/bankslips/pay/9999")
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
    	BankSlips bankSlips = generateValidBankSlips();
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
        BankSlips bankSlips = generateValidBankSlips();
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
