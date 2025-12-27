package com.bankslips.domain.bulkupload;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class BulkUploadJob {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BulkJobStatus status;

    private int totalRecords;
    private int processedRecords;
    private int successRecords;
    private int failedRecords;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Lob @Nullable
    private String errorSummary; 

}
