package com.community.cms.api.withdraw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity.WithdrawHistory;

public interface WithdrawHistoryRepository extends JpaRepository<WithdrawHistory, Integer> {
    
}
