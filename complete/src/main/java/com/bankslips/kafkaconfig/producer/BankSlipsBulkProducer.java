package com.bankslips.kafkaconfig.producer;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bankslips.domain.BankSlips;
import com.bankslips.kafkaconfig.event.BankSlipsBulkEvent;

@Service
public class BankSlipsBulkProducer {

    private static final String TOPIC = "bankslips-bulk-topic";

    private final KafkaTemplate<String, BankSlipsBulkEvent> kafkaTemplate;

    public BankSlipsBulkProducer(KafkaTemplate<String, BankSlipsBulkEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(UUID jobId, java.util.List<BankSlips> slips) {
        BankSlipsBulkEvent event = new BankSlipsBulkEvent(jobId, slips);
        kafkaTemplate.send(TOPIC, jobId.toString(), event);
    }
}
