package com.bankslips.domain.bulkupload;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class BulkUploadFailure {

    @Id
    @GeneratedValue
    private Long id;

    private UUID jobId;

    private String externalId;

    private String reason;
}
