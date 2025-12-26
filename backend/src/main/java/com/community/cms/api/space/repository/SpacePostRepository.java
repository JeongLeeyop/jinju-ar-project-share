package com.community.cms.api.space.repository;

import com.community.cms.entity.SpacePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * SpacePost Repository
 */
@Repository
public interface SpacePostRepository extends JpaRepository<SpacePost, String> {

    /**
     * 공간별 게시글 목록 조회 (삭제되지 않은 것만)
     */
    List<SpacePost> findBySpace_UidAndIsDeletedFalseOrderByIsNoticeDescCreatedAtDesc(String spaceUid);

    /**
     * 공간별 공지사항/일반 게시글 조회 (삭제되지 않은 것만)
     */
    List<SpacePost> findBySpace_UidAndIsNoticeAndIsDeletedFalseOrderByCreatedAtDesc(String spaceUid, boolean isNotice);

    /**
     * 공간별 게시글 페이징 조회 (삭제되지 않은 것만)
     */
    Page<SpacePost> findBySpace_UidAndIsDeletedFalse(String spaceUid, Pageable pageable);

    /**
     * 게시글 조회 (UID로, 삭제되지 않은 것)
     */
    Optional<SpacePost> findByUidAndIsDeletedFalse(String uid);

    /**
     * 공간별 게시글 검색 (제목 + 내용)
     */
    @Query("SELECT sp FROM SpacePost sp WHERE sp.space.uid = :spaceUid AND sp.isDeleted = false " +
           "AND (sp.title LIKE %:keyword% OR sp.content LIKE %:keyword%) " +
           "ORDER BY sp.isNotice DESC, sp.createdAt DESC")
    List<SpacePost> searchBySpaceUid(@Param("spaceUid") String spaceUid, @Param("keyword") String keyword);

    /**
     * 조회수 증가
     */
    @Modifying
    @Query("UPDATE SpacePost sp SET sp.viewCount = sp.viewCount + 1 WHERE sp.uid = :uid")
    void incrementViewCount(@Param("uid") String uid);

    /**
     * 공간별 게시글 수 조회
     */
    long countBySpace_UidAndIsDeletedFalse(String spaceUid);

    /**
     * 사용자별 게시글 조회
     */
    List<SpacePost> findByUserUidAndIsDeletedFalseOrderByCreatedAtDesc(String userUid);
}
