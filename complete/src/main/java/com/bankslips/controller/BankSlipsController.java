package com.bankslips.controller;

import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.banklips.domain.BankSlips;
import com.bankslips.service.interfaces.BankSlipsService;
import com.bankslips.service.interfaces.PersistanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest")
public class BankSlipsController {

	@Autowired
	private BankSlipsService bankSlipsService;

	@RequestMapping(value = "/bankslips/", method = RequestMethod.POST)
	public ResponseEntity<BankSlips> create(@RequestBody @Valid BankSlips bankSlips, BindingResult bindingResult) {
		bankSlipsService.create(bankSlips, bindingResult);
		return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/bankslips/", method = RequestMethod.GET)
	@ResponseBody
	public Page<BankSlips> list(Pageable pageable) {
		Page<BankSlips> page = bankSlipsService.list(pageable);
		return page;
	}

	@RequestMapping(value = "/bankslips/{bankSlipsId}", method = RequestMethod.GET)
	public ResponseEntity<BankSlips> show(@PathVariable("bankSlipsId") UUID bankSlipsId) {
		BankSlips bankSlips = bankSlipsService.show(bankSlipsId.toString());
		return new ResponseEntity<BankSlips>(bankSlips, HttpStatus.OK);
	}

	@RequestMapping(value = "/bankslips/{bankSlipsId}", method = RequestMethod.PUT)
	public ResponseEntity<BankSlips> update(@RequestBody BankSlips bankSlips,
			@PathVariable("bankSlipsId") String bankSlipsId) {

		BankSlips retrievedBankSlips = bankSlipsService.show(bankSlipsId);
		BankSlips paidBankSlips = bankSlipsService.updateBankSlipsStatus(retrievedBankSlips, bankSlips.getStatus());
		return new ResponseEntity<BankSlips>(paidBankSlips, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/bankslips/cancel/{bankSlipsId}", method = RequestMethod.PUT)
	public ResponseEntity<BankSlips> cancel(@RequestBody
			@PathVariable("bankSlipsId") String bankSlipsId) {

		BankSlips paidBankSlips = bankSlipsService.cancelSlip(bankSlipsId);
		return new ResponseEntity<BankSlips>(paidBankSlips, HttpStatus.OK);
	}	

	@RequestMapping(value = "/bankslips/pay/{bankSlipsId}", method = RequestMethod.PUT)
	public ResponseEntity<BankSlips> pay(@RequestBody
			@PathVariable("bankSlipsId") String bankSlipsId) {

		BankSlips paidBankSlips = bankSlipsService.paySlip(bankSlipsId);
		return new ResponseEntity<BankSlips>(paidBankSlips, HttpStatus.OK);
	}
	
	
	

}
