package com.community.cms.api.calendar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.community.cms.entity2.ScheduleParticipant;
import com.community.cms.entity2.ScheduleParticipant.ParticipantStatus;

/**
 * 일정 참여자 Repository
 */
@Repository
public interface ScheduleParticipantRepository
        extends JpaRepository<ScheduleParticipant, Integer>, JpaSpecificationExecutor<ScheduleParticipant> {

    /**
     * 특정 일정의 특정 사용자 참여 정보 조회 (상태 필터)
     */
    Optional<ScheduleParticipant> findByCalendarIdxAndUserUidAndStatus(Integer calendarIdx, String userUid,
            ParticipantStatus status);

    /**
     * 특정 일정의 특정 사용자 참여 정보 조회 (상태 무관)
     */
    Optional<ScheduleParticipant> findByCalendarIdxAndUserUid(Integer calendarIdx, String userUid);

    /**
     * 특정 일정의 모든 참여자 목록 조회
     */
    List<ScheduleParticipant> findByCalendarIdx(Integer calendarIdx);

    /**
     * 특정 일정의 특정 상태 참여자 목록 조회
     */
    List<ScheduleParticipant> findByCalendarIdxAndStatus(Integer calendarIdx, ParticipantStatus status);

    /**
     * 특정 일정에 특정 상태로 참여 중인지 확인
     */
    boolean existsByCalendarIdxAndUserUidAndStatus(Integer calendarIdx, String userUid, ParticipantStatus status);

    /**
     * 특정 일정의 특정 상태 참여자 수 조회
     */
    int countByCalendarIdxAndStatus(Integer calendarIdx, ParticipantStatus status);

    /**
     * 특정 사용자의 참여 일정 목록 조회
     */
    List<ScheduleParticipant> findByUserUidAndStatus(String userUid, ParticipantStatus status);

    /**
     * 특정 채널에서 특정 사용자의 모든 참여 기록 조회
     */
    List<ScheduleParticipant> findByChannelUidAndUserUid(String channelUid, String userUid);
    
    /**
     * 특정 채널에서 특정 사용자의 특정 상태 참여 기록 조회 (최신순)
     */
    List<ScheduleParticipant> findByUserUidAndChannelUidAndStatusOrderByCreatedAtDesc(
            String userUid, String channelUid, ParticipantStatus status);
}
