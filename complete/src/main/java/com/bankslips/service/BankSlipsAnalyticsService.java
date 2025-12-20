package com.bankslips.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bankslips.enums.BankSlipsStatus;
import com.bankslips.repository.BankSlipsRepository;

@Service
public class BankSlipsAnalyticsService {

    @Autowired
    private BankSlipsRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 5000)
    public void pushAnalytics() {
        long total = repository.count();
        long paid = repository.countByStatus(BankSlipsStatus.PAID);
        long pending = repository.countByStatus(BankSlipsStatus.PENDING);
        long canceled = repository.countByStatus(BankSlipsStatus.CANCELED);

        Map<String, Object> analytics = Map.of(
            "total", total,
            "paid", paid,
            "pending", pending,
            "canceled", canceled
        );

        messagingTemplate.convertAndSend("/topic/analytics", analytics);
    }
}
