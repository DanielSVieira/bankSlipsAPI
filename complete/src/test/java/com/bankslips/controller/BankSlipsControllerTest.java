package com.bankslips.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Calendar;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.banklips.domain.BankSlips;
import com.bankslips.Application;
import com.bankslips.enums.BankSlipsStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class BankSlipsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void saveBankSlipValidationMessageEmptyEntity() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = new BankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void saveBankSlipSuccefully() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		System.out.println(jsonParam);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isCreated());
	}
	
	@Test
	public void saveInvalidDate() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		bankSlips.setDueDate(cal.getTime());
		
		String jsonParam = mapper.writeValueAsString(bankSlips);
		System.out.println(jsonParam);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void saveInvalidName() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();

		bankSlips.setCustomer("a");
		
		String jsonParam = mapper.writeValueAsString(bankSlips);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void saveInvalidBigName() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		bankSlips.setCustomer("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
				+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		String jsonParam = mapper.writeValueAsString(bankSlips);
		System.out.println(jsonParam);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isUnprocessableEntity());
	}
	
	@Test
	public void bankSlipsNotProvidedTest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String jsonParam = mapper.writeValueAsString(new String());
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam))
				.andDo(print()).andExpect(status().isBadRequest());
	}
	
	@Test
	public void showBankSlips() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		String result = this.mockMvc.perform(post("/rest/bankslips/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonParam))
				.andReturn()
				.getResponse()
				.getContentAsString();
		JSONObject jsonObject = new JSONObject(result);
		String id = jsonObject.getString("id");
		
        this.mockMvc.perform(get("/rest/bankslips/" + id))
                .andDo(print())
                .andExpect(status().isOk());
	}
	
	@Test
	public void error404ShowingBankSlips() throws Exception {
		String unknownId = "da66dd4c-8c96-437f-92b6-0ca9b574a167";
        this.mockMvc.perform(get("/rest/bankslips/" + unknownId))
                .andDo(print())
                .andExpect(status().isNotFound());
	}
	
	@Test
	public void error400ShowingBankSlips() throws Exception {
		String unknownId = "da66dd4c";
        this.mockMvc.perform(get("/rest/bankslips/" + unknownId))
                .andDo(print())
                .andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void listCreatedBankSlips() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		System.out.println(jsonParam);
		this.mockMvc.perform(post("/rest/bankslips/").contentType(MediaType.APPLICATION_JSON).content(jsonParam));
		
        this.mockMvc.perform(get("/rest/bankslips/"))
                .andDo(print())
                .andExpect(status().isOk());
	}	
	
	@Test
	public void cancelBankSlips() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		String result = this.mockMvc.perform(post("/rest/bankslips/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonParam))
				.andReturn()
				.getResponse()
				.getContentAsString();
		JSONObject jsonObject = new JSONObject(result);
		String id = jsonObject.getString("id");
		
		BankSlips bankSlipsUpdate = new BankSlips();
		bankSlipsUpdate.setStatus(BankSlipsStatus.CANCELED);
		String cancelParam = mapper.writeValueAsString(bankSlipsUpdate);
		
		
        this.mockMvc.perform(put("/rest/bankslips/" + id).contentType(MediaType.APPLICATION_JSON).content(cancelParam))
                .andDo(print())
                .andExpect(status().isOk());
	}	
	
	@Test
	public void cancelBankSlipsStatus404() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlipsUpdate = new BankSlips();
		bankSlipsUpdate.setStatus(BankSlipsStatus.CANCELED);
		String cancelParam = mapper.writeValueAsString(bankSlipsUpdate);
		String unknownId = "da66dd4c-8c96-437f-92b6-0ca9b574a167";
		
		
        this.mockMvc.perform(put("/rest/bankslips/" + unknownId).contentType(MediaType.APPLICATION_JSON).content(cancelParam))
                .andDo(print())
                .andExpect(status().isNotFound());
	}	
	
	@Test
	public void payBankSlips() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlips = generateValidBankSlips();
		String jsonParam = mapper.writeValueAsString(bankSlips);
		String result = this.mockMvc.perform(post("/rest/bankslips/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonParam))
				.andReturn()
				.getResponse()
				.getContentAsString();
		JSONObject jsonObject = new JSONObject(result);
		String id = jsonObject.getString("id");
		
		BankSlips bankSlipsUpdate = new BankSlips();
		bankSlipsUpdate.setStatus(BankSlipsStatus.PAID);
		String cancelParam = mapper.writeValueAsString(bankSlipsUpdate);
		
		
        this.mockMvc.perform(put("/rest/bankslips/" + id).contentType(MediaType.APPLICATION_JSON).content(cancelParam))
                .andDo(print())
                .andExpect(status().isOk());
	}

	
	@Test
	public void payBankSlipsStatus404() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		BankSlips bankSlipsUpdate = new BankSlips();
		bankSlipsUpdate.setStatus(BankSlipsStatus.PAID);
		String cancelParam = mapper.writeValueAsString(bankSlipsUpdate);
		String unknownId = "da66dd4c-8c96-437f-92b6-0ca9b574a167";
		
		
        this.mockMvc.perform(put("/rest/bankslips/" + unknownId).contentType(MediaType.APPLICATION_JSON).content(cancelParam))
                .andDo(print())
                .andExpect(status().isNotFound());
	}	
	
	private BankSlips generateValidBankSlips() {
		BankSlips bankSlips = new BankSlips();
		bankSlips.setCustomer("abc");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		bankSlips.setDueDate(cal.getTime());
		bankSlips.setTotalInCents(new BigDecimal(100000));
		return bankSlips;
	}		
	
	
}