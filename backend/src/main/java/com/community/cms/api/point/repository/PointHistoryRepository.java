package com.community.cms.api.point.repository;

import com.community.cms.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 포인트 히스토리 Repository
 */
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long>, QuerydslPredicateExecutor<PointHistory> {

    // 기존 메서드 (호환성 유지)
    @Query(value = "SELECT COUNT(*) FROM point_history ph WHERE ph.user_uid = ?1 AND ph.reason = ?2 AND \n" + 
        "date_format(ph.create_date, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d')", nativeQuery = true)
    int countTodayByUserUidAndReason(String userUid, String reason);
    
    @Query(value = "SELECT COUNT(*) FROM point_history ph WHERE ph.user_uid = ?1 AND ph.reason = ?2 AND \n" + 
        "date_format(ph.create_date, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d')", nativeQuery = true)
    int countTodayByUserUidAndTfse(String userUid, String reason);
    
    @Query(value = "SELECT COUNT(*) FROM point_history ph WHERE ph.user_uid = ?1 AND ph.reason = ?2 AND \n" + 
        "date_format(ph.create_date, '%Y-%m-%d') = date_format(now(), '%Y-%m-%d')", nativeQuery = true)
    int countTodayByUserUidAndChallenge(String userUid, String reason);

    /**
     * 사용자의 특정 채널 포인트 히스토리 조회 (페이징)
     */
    Page<PointHistory> findByUserUidAndChannelUidOrderByCreatedAtDesc(
            String userUid, 
            String channelUid, 
            Pageable pageable
    );

    /**
     * 사용자의 현재 포인트 잔액 조회 (가장 최근 기록의 잔액)
     */
    @Query(value = "SELECT ph.current_balance FROM point_history ph " +
           "WHERE ph.user_uid = :userUid AND ph.channel_uid = :channelUid " +
           "ORDER BY ph.created_at DESC, ph.id DESC " +
           "LIMIT 1", nativeQuery = true)
    Optional<Integer> findCurrentBalance(@Param("userUid") String userUid, 
                                         @Param("channelUid") String channelUid);

    /**
     * 사용자의 특정 채널 포인트 총합 조회
     */
    @Query("SELECT COALESCE(SUM(ph.pointAmount), 0) FROM PointHistory ph " +
           "WHERE ph.userUid = :userUid AND ph.channelUid = :channelUid")
    Integer sumPointsByUserAndChannel(@Param("userUid") String userUid, 
                                     @Param("channelUid") String channelUid);
}
