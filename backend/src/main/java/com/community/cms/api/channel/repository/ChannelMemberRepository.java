package com.community.cms.api.channel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.ChannelMember;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Integer>, QuerydslPredicateExecutor<ChannelMember> {
    Optional<ChannelMember> findByUserUidAndChannelUid(String userUid, String ChannelUid);

    long countByChannelUidAndUserUidInAndApprovalStatus(String channelUid, List<String> userUidList, boolean approvalStatus);
    long count(Predicate predicate);
    long countByChannelUidAndApprovalStatus(String channelUid, boolean approvalStatus);
    
    /**
     * 채널의 승인된 멤버 목록 조회 (공개 채팅 공간용)
     * @param channelUid 채널 UID
     * @return 승인된 멤버 목록
     */
    List<ChannelMember> findByChannelUidAndApprovalStatus(String channelUid, boolean approvalStatus);
    
    /**
     * 채널의 승인된 멤버 목록 조회 (추방된 회원 제외)
     * @param channelUid 채널 UID
     * @param approvalStatus 승인 상태
     * @param banned 추방 상태
     * @return 승인되고 추방되지 않은 멤버 목록
     */
    List<ChannelMember> findByChannelUidAndApprovalStatusAndBanned(String channelUid, boolean approvalStatus, boolean banned);
    
    /**
     * 사용자가 특정 채널의 승인된 멤버인지 확인
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @return 승인된 멤버 여부
     */
    boolean existsByUserUidAndChannelUidAndApprovalStatus(String userUid, String channelUid, boolean approvalStatus);
    
    /**
     * 사용자가 특정 채널의 승인되고 추방되지 않은 멤버인지 확인
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param approvalStatus 승인 상태
     * @param banned 추방 상태
     * @return 승인되고 추방되지 않은 멤버 여부
     */
    boolean existsByUserUidAndChannelUidAndApprovalStatusAndBanned(String userUid, String channelUid, boolean approvalStatus, boolean banned);
    
    /**
     * 채널의 승인되고 추방되지 않은 멤버 수 조회
     * @param channelUid 채널 UID
     * @param approvalStatus 승인 상태
     * @param banned 추방 상태
     * @return 승인되고 추방되지 않은 멤버 수
     */
    long countByChannelUidAndApprovalStatusAndBanned(String channelUid, boolean approvalStatus, boolean banned);
    
    /**
     * 채널의 승인되고 추방되지 않은 멤버 목록 조회 (페이징)
     * @param channelUid 채널 UID
     * @param approvalStatus 승인 상태
     * @param banned 추방 상태
     * @param pageable 페이징 정보
     * @return 승인되고 추방되지 않은 멤버 페이지
     */
    Page<ChannelMember> findByChannelUidAndApprovalStatusAndBanned(String channelUid, boolean approvalStatus, boolean banned, Pageable pageable);
    
    /**
     * 사용자의 가입 채널 목록 조회
     */
    List<ChannelMember> findByUserUid(String userUid);
    
    /**
     * 채널 멤버 목록 조회 (페이지네이션)
     */
    Page<ChannelMember> findByChannelUid(String channelUid, Pageable pageable);
}
