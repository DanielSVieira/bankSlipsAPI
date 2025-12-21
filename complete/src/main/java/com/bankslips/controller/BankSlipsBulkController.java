package com.bankslips.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banklips.domain.BankSlips;
import com.bankslips.service.interfaces.PersistanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest")
public class BankSlipsBulkController {

    
    @Autowired
    private PersistanceService bankSlipsService;


	@RequestMapping(value = "/bankslips/bulk", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> uploadBulk(@RequestBody @Valid  List<BankSlips> slips) {
    	bankSlipsService.bulkSave(slips);


        Map<String, Object> response = Map.of(
            "uploaded", slips.size(),
            "timestamp", LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
    
	@RequestMapping(value = "/bankslips/bulk/async", method = RequestMethod.POST)
    public ResponseEntity<?> uploadBulkAsync(@RequestBody @Valid List<BankSlips> slips) {
        bankSlipsService.bulkSaveAsync(slips);

        return ResponseEntity.accepted()
                .body(Map.of("message", "Bulk upload started", "slips", slips.size()));
    }

}

