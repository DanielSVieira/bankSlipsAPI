package com.bankslips.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banklips.domain.BankSlips;
import com.bankslips.enums.BankSlipsStatus;

@Repository
public interface BankSlipsRepository extends JpaRepository<BankSlips, String> {

	Page<BankSlips> findAll(Pageable pageable);
	Long countByStatus(BankSlipsStatus status);
    long countByIdIn(List<Long> ids);
    List<BankSlips> findAllByIdIn(List<Long> ids);
}
