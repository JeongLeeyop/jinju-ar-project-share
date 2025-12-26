package com.community.cms.api.space.repository;

import com.community.cms.entity.SpaceInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SpaceInvitation Repository
 * 공간 초대 데이터 접근을 위한 Repository
 */
@Repository
public interface SpaceInvitationRepository extends JpaRepository<SpaceInvitation, String> {

    /**
     * 특정 공간의 모든 초대 조회
     */
    List<SpaceInvitation> findBySpaceUidOrderByCreatedAtDesc(String spaceUid);

    /**
     * 특정 사용자가 받은 초대 조회
     */
    List<SpaceInvitation> findByInvitedUserUidOrderByCreatedAtDesc(String invitedUserUid);

    /**
     * 특정 사용자가 받은 대기중인 초대 조회
     */
    List<SpaceInvitation> findByInvitedUserUidAndStatusOrderByCreatedAtDesc(
        String invitedUserUid, SpaceInvitation.InvitationStatus status);

    /**
     * 특정 사용자가 특정 공간에서 받은 대기중인 초대가 있는지 확인
     */
    Optional<SpaceInvitation> findBySpaceUidAndInvitedUserUidAndStatus(
        String spaceUid, String invitedUserUid, SpaceInvitation.InvitationStatus status);

    /**
     * 특정 공간의 대기중인 초대 조회
     */
    List<SpaceInvitation> findBySpaceUidAndStatusOrderByCreatedAtDesc(
        String spaceUid, SpaceInvitation.InvitationStatus status);

    /**
     * 초대한 사용자별 초대 목록 조회
     */
    List<SpaceInvitation> findByInviterUidOrderByCreatedAtDesc(String inviterUid);

    /**
     * 만료된 초대 조회 및 상태 업데이트
     */
    @Query("SELECT i FROM SpaceInvitation i WHERE i.status = 'PENDING' AND i.expiresAt < :now")
    List<SpaceInvitation> findExpiredInvitations(@Param("now") LocalDateTime now);

    /**
     * 만료된 초대 일괄 업데이트
     */
    @Modifying
    @Query("UPDATE SpaceInvitation i SET i.status = 'EXPIRED' WHERE i.status = 'PENDING' AND i.expiresAt < :now")
    int expireOldInvitations(@Param("now") LocalDateTime now);

    /**
     * 특정 공간의 초대 통계
     */
    @Query("SELECT i.status, COUNT(i) FROM SpaceInvitation i WHERE i.spaceUid = :spaceUid GROUP BY i.status")
    List<Object[]> getInvitationStatsBySpace(@Param("spaceUid") String spaceUid);

    /**
     * 오래된 초대 삭제 (선택적, 응답된 초대 중 일정 기간 지난 것)
     */
    @Modifying
    @Query("DELETE FROM SpaceInvitation i WHERE i.status IN ('ACCEPTED', 'REJECTED', 'EXPIRED') " +
           "AND i.respondedAt < :beforeDate")
    int deleteOldRespondedInvitations(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * 특정 공간과 사용자의 대기중인 초대 존재 여부 확인
     */
    boolean existsBySpaceUidAndInvitedUserUidAndStatus(
        String spaceUid, String invitedUserUid, SpaceInvitation.InvitationStatus status);
}
