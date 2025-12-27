package com.bankslips.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankslips.domain.bulkupload.BulkUploadFailure;

public interface FailureJobsRepository extends JpaRepository<BulkUploadFailure, UUID> {
	
	public Optional<BulkUploadFailure> findById(UUID jobId);
	public List<BulkUploadFailure> findByJobId(UUID jobId);

}
