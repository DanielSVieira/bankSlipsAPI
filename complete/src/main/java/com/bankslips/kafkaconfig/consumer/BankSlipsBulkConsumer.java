package com.bankslips.kafkaconfig.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bankslips.domain.BankSlips;
import com.bankslips.kafkaconfig.event.BankSlipsBulkEvent;
import com.bankslips.service.BankSlipsBulkService;
import com.bankslips.service.interfaces.IPersistenceBulkService;

@Component
public class BankSlipsBulkConsumer {

    private IPersistenceBulkService<BankSlips> bulkService;


    @KafkaListener(
        topics = "bankslips-bulk-topic",
        groupId = "bankslips-bulk-consumer",
        containerFactory = "bulkKafkaListenerContainerFactory"
    )
    public void consume(BankSlipsBulkEvent event) {
        bulkService.processKafkaBulk(event.jobId(), event.slips());
    }
}
