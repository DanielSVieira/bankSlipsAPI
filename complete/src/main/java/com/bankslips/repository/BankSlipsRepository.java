package com.bankslips.repository;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankslips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

@Repository
public interface BankSlipsRepository extends JpaRepository<BankSlips, UUID> {

	Page<BankSlips> findAll(Pageable pageable);
	Long countByStatus(BankSlipsStatus status);
    long countByIdIn(List<Long> ids);
    List<BankSlips> findAllByIdIn(List<Long> ids);
    Optional<BankSlips> findByExternalId(String externalId);
    boolean existsByExternalId(String externalId);
    List<BankSlips> findAllByExternalIdIn(Set<String> externalIds);
    
}
