package com.bankslips.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banklips.domain.BankSlips;

@Repository
public interface BankSlipsRepository extends JpaRepository<BankSlips, String> {

	Page<BankSlips> findAll(Pageable pageable);
}
