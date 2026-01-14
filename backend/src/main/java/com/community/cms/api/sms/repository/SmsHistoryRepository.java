package com.community.cms.api.sms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.community.cms.api.sms.entity.SmsHistory;

@Repository
public interface SmsHistoryRepository extends JpaRepository<SmsHistory, Long> {

    @Query("SELECT h FROM SmsHistory h WHERE " +
           "(:startDate IS NULL OR h.sentAt >= CAST(:startDate AS java.time.LocalDateTime)) AND " +
           "(:endDate IS NULL OR h.sentAt <= CAST(:endDate AS java.time.LocalDateTime)) AND " +
           "(:keyword IS NULL OR h.recipient LIKE %:keyword% OR h.message LIKE %:keyword%) " +
           "ORDER BY h.sentAt DESC")
    Page<SmsHistory> findHistory(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("keyword") String keyword,
            Pageable pageable);
}
