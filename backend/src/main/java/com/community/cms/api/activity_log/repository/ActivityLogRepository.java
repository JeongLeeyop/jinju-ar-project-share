package com.community.cms.api.activity_log.repository;

import com.community.cms.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * 특정 유저의 활동 로그 조회 (기간 필터링, 페이징)
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.userUid = :userUid " +
           "AND (:channelUid IS NULL OR a.channelUid = :channelUid) " +
           "AND a.createdAt >= :startDate " +
           "ORDER BY a.createdAt DESC")
    Page<ActivityLog> findUserActivitiesByPeriod(
            @Param("userUid") String userUid,
            @Param("channelUid") String channelUid,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    /**
     * 특정 유저의 전체 활동 로그 조회 (채널 필터링, 페이징)
     */
    Page<ActivityLog> findByUserUidAndChannelUidOrderByCreatedAtDesc(
            String userUid,
            String channelUid,
            Pageable pageable
    );

    /**
     * 특정 채널의 모든 활동 로그 조회
     */
    Page<ActivityLog> findByChannelUidOrderByCreatedAtDesc(
            String channelUid,
            Pageable pageable
    );

    /**
     * 활동 타입별 조회
     */
    Page<ActivityLog> findByUserUidAndChannelUidAndActivityTypeOrderByCreatedAtDesc(
            String userUid,
            String channelUid,
            ActivityLog.ActivityType activityType,
            Pageable pageable
    );

    /**
     * 특정 유저의 최근 활동 개수 조회
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.userUid = :userUid " +
           "AND (:channelUid IS NULL OR a.channelUid = :channelUid) " +
           "AND a.createdAt >= :startDate")
    Long countRecentActivities(
            @Param("userUid") String userUid,
            @Param("channelUid") String channelUid,
            @Param("startDate") LocalDateTime startDate
    );

    /**
     * 특정 유저의 활동 타입별 개수 조회
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.userUid = :userUid " +
           "AND a.channelUid = :channelUid " +
           "AND a.activityType = :activityType")
    Long countByActivityType(
            @Param("userUid") String userUid,
            @Param("channelUid") String channelUid,
            @Param("activityType") ActivityLog.ActivityType activityType
    );

    /**
     * 특정 관련 엔티티에 대한 활동 로그 조회
     */
    List<ActivityLog> findByRelatedUidOrderByCreatedAtDesc(String relatedUid);

    /**
     * 오래된 활동 로그 삭제를 위한 조회
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.createdAt < :beforeDate")
    List<ActivityLog> findOldActivities(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 특정 유저의 활동 로그 조회 (관리자용)
     */
    Page<ActivityLog> findByUserUidOrderByCreatedAtDesc(String userUid, Pageable pageable);
}
