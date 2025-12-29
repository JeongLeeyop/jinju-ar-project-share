package com.community.cms.api.point.repository;

import com.community.cms.entity.PointSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 포인트 설정 Repository
 */
@Repository
public interface PointSettingRepository extends JpaRepository<PointSetting, Long> {
    
    /**
     * 채널 UID로 포인트 설정 조회
     */
    Optional<PointSetting> findByChannelUid(String channelUid);
    
    /**
     * 채널 UID로 포인트 설정 존재 여부 확인
     */
    boolean existsByChannelUid(String channelUid);
}
