package com.community.cms.api.space.repository;

import com.community.cms.entity.SpacePostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpacePostComment Repository
 */
@Repository
public interface SpacePostCommentRepository extends JpaRepository<SpacePostComment, String> {

    /**
     * 게시글별 댓글 목록 조회 (삭제되지 않은 것만)
     */
    List<SpacePostComment> findByPost_UidAndIsDeletedFalseOrderByCreatedAtAsc(String postUid);

    /**
     * 댓글 조회 (삭제되지 않은 것)
     */
    Optional<SpacePostComment> findByUidAndIsDeletedFalse(String uid);

    /**
     * 게시글별 댓글 수 조회 (삭제되지 않은 것만)
     */
    long countByPost_UidAndIsDeletedFalse(String postUid);

    /**
     * 사용자별 댓글 조회
     */
    List<SpacePostComment> findByUserUidAndIsDeletedFalseOrderByCreatedAtDesc(String userUid);
}
