package com.community.cms.api.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity.Bank;

public interface BankRepository extends JpaRepository<Bank, Integer> {
    
}
