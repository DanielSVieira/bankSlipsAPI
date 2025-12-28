package com.bankslips.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankslips.domain.bulkupload.BulkUploadJob;

@Repository
public interface BulkJobsRepository extends JpaRepository<BulkUploadJob, UUID> {
	
	public Optional<BulkUploadJob> findById(UUID id);

}
