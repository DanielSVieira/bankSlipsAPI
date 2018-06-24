package com.bankslips.validator;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.banklips.domain.BankSlips;
import com.bankslips.controller.BankSlipsController;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankSlipsValidatorTest {

	@Autowired
	private TestRestTemplate template;

	@LocalServerPort
	private int port;

	private URL base;

	HttpHeaders headers = new HttpHeaders();

	@MockBean
	private BankSlipsController controller;

	@Before
	public void setUp() throws MalformedURLException {
		this.base = new URL("http://localhost:" + port + "/");

	}

	@Test
	public void saveBankSlipValidationMessageEmptyEntity() throws Exception {
		ResponseEntity<String> response = template.getForEntity(base.toString() + "rest/bankslips/", String.class);
		assertTrue(response.getStatusCode().equals(HttpStatus.OK));
	}

	@Test
	public void saveBankSlipValidationMessageEmptyEntity2() throws Exception {
		BankSlips bankSlips = new BankSlips();
		HttpEntity<BankSlips> entity = new HttpEntity<BankSlips>(bankSlips, headers);

		 ResponseEntity<String> response = template.exchange(base.toString() + "rest/bankslips/", HttpMethod.POST, entity ,String.class);
//		ResponseEntity<String> response = template.exchange(createURLWithPort("/students/Student1/courses"),
//				HttpMethod.POST, entity, String.class);
		System.out.println(response);
		System.out.println(response.getBody());
		System.out.println(response.getStatusCode());
		assertTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST));
	}

}
