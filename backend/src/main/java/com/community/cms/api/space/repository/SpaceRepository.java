package com.community.cms.api.space.repository;

import com.community.cms.entity.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Space Repository
 * 공간 데이터 접근을 위한 Repository
 */
@Repository
public interface SpaceRepository extends JpaRepository<Space, String> {

    /**
     * 채널의 모든 공간 조회 (삭제되지 않은 것만)
     */
    List<Space> findByChannelUidAndIsDeletedFalseOrderByCreatedAtDesc(String channelUid);

    /**
     * 채널의 활성화된 공간 조회 (삭제되지 않은 것만)
     */
    List<Space> findByChannelUidAndIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc(String channelUid);

    /**
     * 채널의 공개 공간 조회 (삭제되지 않은 것만)
     */
    List<Space> findByChannelUidAndIsPublicTrueAndIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc(String channelUid);

    /**
     * 특정 사용자가 관리자인 공간 조회 (삭제되지 않은 것만)
     */
    List<Space> findByAdminUidAndIsDeletedFalseOrderByCreatedAtDesc(String adminUid);

    /**
     * 특정 사용자가 멤버인 공간 조회
     */
    @Query("SELECT s FROM Space s JOIN s.spaceMembers sm WHERE sm.user.uid = :userUid AND s.isActive = true AND s.isDeleted = false ORDER BY s.createdAt DESC")
    List<Space> findSpacesByMemberUid(@Param("userUid") String userUid);

    /**
     * 특정 사용자가 접근 가능한 공간 조회
     * - 공개 공간(isPublic=true)은 누구나 접근 가능
     * - 비공개 공간은 관리자이거나 멤버인 경우만 접근 가능
     * - userUid가 null이면 공개 공간만 반환
     */
    @Query("SELECT DISTINCT s FROM Space s LEFT JOIN s.spaceMembers sm WHERE s.channelUid = :channelUid " +
           "AND s.isActive = true AND s.isDeleted = false " +
           "AND (s.isPublic = true OR :userUid IS NOT NULL AND (s.adminUid = :userUid OR sm.user.uid = :userUid)) " +
           "ORDER BY s.createdAt DESC")
    List<Space> findAccessibleSpaces(@Param("channelUid") String channelUid, @Param("userUid") String userUid);

    /**
     * 특정 채널에서 공간 이름 검색
     */
    @Query("SELECT s FROM Space s WHERE s.channelUid = :channelUid " +
           "AND s.isActive = true AND s.isDeleted = false " +
           "AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY s.createdAt DESC")
    List<Space> searchByNameInChannel(@Param("channelUid") String channelUid, @Param("keyword") String keyword);

    /**
     * 페이징된 공간 조회
     */
    Page<Space> findByChannelUidAndIsActiveTrueAndIsDeletedFalse(String channelUid, Pageable pageable);

    /**
     * 접근 가능한 공간을 페이징하여 조회
     * - 공개 공간(isPublic=true)은 누구나 접근 가능
     * - 비공개 공간은 관리자이거나 멤버인 경우만 접근 가능
     * - userUid가 null이면 공개 공간만 반환
     */
    @Query("SELECT DISTINCT s FROM Space s LEFT JOIN s.spaceMembers sm WHERE s.channelUid = :channelUid " +
           "AND s.isActive = true AND s.isDeleted = false " +
           "AND (s.isPublic = true OR :userUid IS NOT NULL AND (s.adminUid = :userUid OR sm.user.uid = :userUid))")
    Page<Space> findAccessibleSpacesPaged(@Param("channelUid") String channelUid, 
                                          @Param("userUid") String userUid, 
                                          Pageable pageable);

    /**
     * 공간 타입별 조회
     */
    List<Space> findByChannelUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc(
        String channelUid, Space.SpaceType spaceType);

    /**
     * 공간 UID와 채널 UID로 조회 (보안 체크용)
     */
    Optional<Space> findByUidAndChannelUid(String uid, String channelUid);

    /**
     * 특정 사용자가 특정 공간의 멤버인지 확인
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Space s JOIN s.spaceMembers sm " +
           "WHERE s.uid = :spaceUid AND sm.user.uid = :userUid")
    boolean isUserMemberOfSpace(@Param("spaceUid") String spaceUid, @Param("userUid") String userUid);

    /**
     * 채널의 공간 개수 조회
     */
    long countByChannelUidAndIsActiveTrueAndIsDeletedFalse(String channelUid);

    /**
     * UID로 활성화되고 삭제되지 않은 공간 조회
     */
    Optional<Space> findByUidAndIsActiveTrueAndIsDeletedFalse(String uid);

    /**
     * 특정 관리자가 특정 채널에서 특정 타입의 활성 공간 개수 조회
     * 공간 생성 제한(1개) 체크를 위해 사용
     */
    long countByChannelUidAndAdminUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalse(
        String channelUid, String adminUid, Space.SpaceType spaceType);
}
