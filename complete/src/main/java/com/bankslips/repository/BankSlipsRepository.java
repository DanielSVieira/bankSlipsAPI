package com.bankslips.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.banklips.domain.BankSlips;

@Repository
public interface BankSlipsRepository extends PagingAndSortingRepository<BankSlips, String> {

	Page<BankSlips> findAll(Pageable pageable);
}
