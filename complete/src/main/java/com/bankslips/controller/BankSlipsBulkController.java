package com.bankslips.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banklips.domain.BankSlips;
import com.bankslips.service.BankSlipsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest")
public class BankSlipsBulkController {

    
    @Autowired
    private BankSlipsService bankSlipsService;


    @PostMapping("/bankslips/bulk")
    public ResponseEntity<Map<String, Object>> uploadBulk(@RequestBody @Valid  List<BankSlips> slips) {
    	bankSlipsService.bulkSave(slips);


        Map<String, Object> response = Map.of(
            "uploaded", slips.size(),
            "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

}

