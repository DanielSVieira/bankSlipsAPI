package com.bankslips.kafkaconfig.event;

import java.util.List;
import java.util.UUID;
import com.bankslips.domain.BankSlips;

public record BankSlipsBulkEvent(
    UUID jobId,
    List<BankSlips> slips
) {}
