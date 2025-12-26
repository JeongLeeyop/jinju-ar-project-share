package com.community.cms.api.payment.repository;

import com.community.cms.entity.PaymentResult;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentResultRepository extends JpaRepository<PaymentResult, Integer>{
    
}
