package com.community.cms.api.sms.repository;

import com.community.cms.entity.SmsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * SMS 템플릿 리포지토리
 */
public interface SmsTemplateRepository extends JpaRepository<SmsTemplate, Long> {

    /**
     * 활성화된 템플릿 조회 (전체 공통 + 특정 채널)
     * @param channelUid 채널 UID
     * @return 활성화된 템플릿 목록
     */
    @Query("SELECT st FROM SmsTemplate st " +
           "WHERE st.isActive = true " +
           "AND (st.channelUid IS NULL OR st.channelUid = :channelUid) " +
           "ORDER BY st.sortOrder ASC, st.createdAt DESC")
    List<SmsTemplate> findActiveTemplatesByChannelUid(@Param("channelUid") String channelUid);

    /**
     * 모든 활성화된 템플릿 조회 (전체 공통만)
     * @return 활성화된 공통 템플릿 목록
     */
    @Query("SELECT st FROM SmsTemplate st " +
           "WHERE st.isActive = true " +
           "AND st.channelUid IS NULL " +
           "ORDER BY st.sortOrder ASC, st.createdAt DESC")
    List<SmsTemplate> findActiveCommonTemplates();
}
