package com.community.cms.api.activity.repository;

import com.community.cms.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Activity Repository
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * 채널별 활동 로그 조회
     */
    List<Activity> findByChannelUidOrderByCreatedAtDesc(String channelUid);

    /**
     * 채널별 활동 로그 페이징 조회
     */
    Page<Activity> findByChannelUidOrderByCreatedAtDesc(String channelUid, Pageable pageable);

    /**
     * 공간별 활동 로그 조회
     */
    List<Activity> findBySpaceUidOrderByCreatedAtDesc(String spaceUid);

    /**
     * 공간별 활동 로그 페이징 조회
     */
    Page<Activity> findBySpaceUidOrderByCreatedAtDesc(String spaceUid, Pageable pageable);

    /**
     * 사용자별 활동 로그 조회
     */
    List<Activity> findByUserUidOrderByCreatedAtDesc(String userUid);

    /**
     * 사용자별 활동 로그 페이징 조회
     */
    Page<Activity> findByUserUidOrderByCreatedAtDesc(String userUid, Pageable pageable);

    /**
     * 사용자+채널별 활동 로그 조회
     */
    List<Activity> findByUserUidAndChannelUidOrderByCreatedAtDesc(String userUid, String channelUid);

    /**
     * 사용자+채널별 활동 로그 페이징 조회
     */
    Page<Activity> findByUserUidAndChannelUidOrderByCreatedAtDesc(String userUid, String channelUid, Pageable pageable);
}
