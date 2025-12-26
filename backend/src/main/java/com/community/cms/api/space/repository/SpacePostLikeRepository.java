package com.community.cms.api.space.repository;

import com.community.cms.entity.SpacePostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpacePostLike Repository
 */
@Repository
public interface SpacePostLikeRepository extends JpaRepository<SpacePostLike, Long> {

    /**
     * 좋아요 조회 (게시글 + 사용자)
     */
    Optional<SpacePostLike> findByPost_UidAndUserUid(String postUid, String userUid);

    /**
     * 좋아요 존재 여부
     */
    boolean existsByPost_UidAndUserUid(String postUid, String userUid);

    /**
     * 게시글별 좋아요 수
     */
    long countByPost_Uid(String postUid);

    /**
     * 게시글별 좋아요 목록
     */
    List<SpacePostLike> findByPost_Uid(String postUid);

    /**
     * 사용자별 좋아요 목록
     */
    List<SpacePostLike> findByUserUid(String userUid);
}