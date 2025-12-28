package com.community.cms.api.space.service;

import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.space.dto.SpaceInvitationDto;
import com.community.cms.api.space.dto.SpaceInvitationRequest;
import com.community.cms.api.space.repository.SpaceInvitationRepository;
import com.community.cms.api.space.repository.SpaceRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.Space;
import com.community.cms.entity.SpaceInvitation;
import com.community.cms.entity.User;
import com.community.cms.entity2.Channel;
import com.community.cms.util.ActivityLogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpaceInvitation Service
 * 공간 초대 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SpaceInvitationService {

    private final SpaceInvitationRepository invitationRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ActivityLogHelper activityLogHelper;

    /**
     * 초대 생성
     */
    @Transactional
    public SpaceInvitationDto createInvitation(SpaceInvitationRequest request, 
                                               String inviterUid, String inviterName) {
        // 공간 조회
        Space space = spaceRepository.findById(request.getSpaceUid())
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인: 공간 관리자 또는 커뮤니티 관리자
        boolean isSpaceAdmin = space.isAdmin(inviterUid);
        boolean isCommunityAdmin = false;
        
        if (!isSpaceAdmin) {
            // 커뮤니티 관리자 권한 확인 - Channel의 userUid 확인
            log.info("커뮤니티 관리자 권한 확인 시작: userUid={}, channelUid(domain)={}", inviterUid, space.getChannelUid());
            
            Channel channel = channelRepository.findByUid(space.getChannelUid()).orElse(null);
            
            if (channel != null && channel.getUserUid() != null && channel.getUserUid().equals(inviterUid)) {
                isCommunityAdmin = true;
                log.info("✅ 커뮤니티 관리자로 확인됨 (Channel.userUid): userUid={}", inviterUid);
            } else {
                log.info("❌ 커뮤니티 관리자 아님: userUid={}", inviterUid);
            }
        }
        
        if (!isSpaceAdmin && !isCommunityAdmin) {
            throw new IllegalAccessError("공간 관리자 또는 커뮤니티 관리자만 초대할 수 있습니다.");
        }

        // 초대받을 사용자 조회
        User invitedUser = userRepository.findById(request.getInvitedUserUid())
                .orElseThrow(() -> new IllegalArgumentException("초대할 사용자를 찾을 수 없습니다."));

        // 이미 멤버인지 확인
        if (space.isMember(request.getInvitedUserUid())) {
            throw new IllegalStateException("이미 공간의 멤버입니다.");
        }

        // 최대 멤버 수 확인
        if (!space.canAddMember()) {
            throw new IllegalStateException("최대 멤버 수에 도달했습니다.");
        }

        // ✅ 바로 공간에 멤버 추가 (승인/만료 처리 없이 즉시 입장)
        space.addMember(invitedUser);
        spaceRepository.save(space);
        
        log.info("✅ 초대와 동시에 멤버로 추가됨: spaceUid={}, userUid={}", space.getUid(), invitedUser.getUid());

        // 초대 기록은 히스토리 용도로만 생성 (ACCEPTED 상태로 즉시 저장)
        SpaceInvitation invitation = SpaceInvitation.builder()
                .spaceUid(request.getSpaceUid())
                .inviterUid(inviterUid)
                .inviterName(inviterName)
                .invitedUserUid(request.getInvitedUserUid())
                .invitedUserName(invitedUser.getActualName())
                .message(request.getMessage())
                .expiresAt(LocalDateTime.now().plusDays(7)) // 기본값(의미없음, 히스토리용)
                .build();
        
        // 바로 수락 상태로 저장 (히스토리 기록용)
        invitation.accept();
        SpaceInvitation savedInvitation = invitationRepository.save(invitation);

        // 활동 로그 기록 - 초대 및 참여
        activityLogHelper.logSpaceInvited(
                request.getInvitedUserUid(), 
                invitedUser.getActualName(),
                space.getChannelUid(), 
                "",
                space.getName()
        );
        
        activityLogHelper.logSpaceJoined(
                request.getInvitedUserUid(), 
                invitedUser.getActualName(),
                space.getChannelUid(), 
                "",
                space.getUid(),
                space.getName()
        );

        log.info("초대 및 멤버 추가 완료: invitationUid={}, spaceUid={}, invitedUserUid={}", 
                savedInvitation.getUid(), request.getSpaceUid(), request.getInvitedUserUid());

        return SpaceInvitationDto.fromEntity(savedInvitation, space.getName());
    }

    /**
     * 초대 수락
     */
    @Transactional
    public void acceptInvitation(String invitationUid, String userUid) {
        SpaceInvitation invitation = invitationRepository.findById(invitationUid)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없습니다."));

        // 초대받은 사용자 확인
        if (!invitation.getInvitedUserUid().equals(userUid)) {
            throw new IllegalAccessError("본인의 초대만 수락할 수 있습니다.");
        }

        // 초대 수락 가능 여부 확인
        if (!invitation.canAccept()) {
            throw new IllegalStateException("초대를 수락할 수 없습니다. (이미 응답했거나 만료되었습니다)");
        }

        // 공간 조회
        Space space = spaceRepository.findById(invitation.getSpaceUid())
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 최대 멤버 수 확인
        if (!space.canAddMember()) {
            throw new IllegalStateException("최대 멤버 수에 도달했습니다.");
        }

        // 사용자 조회
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 초대 수락
        invitation.accept();
        invitationRepository.save(invitation);

        // 공간에 멤버 추가
        space.addMember(user);
        spaceRepository.save(space);

        // 활동 로그 기록
        activityLogHelper.logSpaceJoined(
                userUid, 
                user.getActualName(),
                space.getChannelUid(), 
                "",
                space.getUid(), 
                space.getName()
        );

        log.info("초대 수락 완료: invitationUid={}, userUid={}, spaceUid={}", 
                invitationUid, userUid, space.getUid());
    }

    /**
     * 초대 거절
     */
    @Transactional
    public void rejectInvitation(String invitationUid, String userUid) {
        SpaceInvitation invitation = invitationRepository.findById(invitationUid)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없습니다."));

        // 초대받은 사용자 확인
        if (!invitation.getInvitedUserUid().equals(userUid)) {
            throw new IllegalAccessError("본인의 초대만 거절할 수 있습니다.");
        }

        // 초대 거절
        invitation.reject();
        invitationRepository.save(invitation);

        log.info("초대 거절 완료: invitationUid={}, userUid={}", invitationUid, userUid);
    }

    /**
     * 초대 취소 (관리자만)
     */
    @Transactional
    public void cancelInvitation(String invitationUid, String adminUid) {
        SpaceInvitation invitation = invitationRepository.findById(invitationUid)
                .orElseThrow(() -> new IllegalArgumentException("초대를 찾을 수 없습니다."));

        // 공간 조회 및 관리자 권한 확인
        Space space = spaceRepository.findById(invitation.getSpaceUid())
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        if (!space.isAdmin(adminUid)) {
            throw new IllegalAccessError("공간 관리자만 초대를 취소할 수 있습니다.");
        }

        // 초대 삭제
        invitationRepository.delete(invitation);

        log.info("초대 취소 완료: invitationUid={}, adminUid={}", invitationUid, adminUid);
    }

    /**
     * 사용자가 받은 초대 목록 조회
     */
    public List<SpaceInvitationDto> getMyInvitations(String userUid) {
        List<SpaceInvitation> invitations = invitationRepository
                .findByInvitedUserUidOrderByCreatedAtDesc(userUid);

        return invitations.stream()
                .map(invitation -> {
                    Space space = spaceRepository.findById(invitation.getSpaceUid()).orElse(null);
                    String spaceName = space != null ? space.getName() : "알 수 없음";
                    return SpaceInvitationDto.fromEntity(invitation, spaceName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 받은 대기중인 초대 목록 조회
     */
    public List<SpaceInvitationDto> getMyPendingInvitations(String userUid) {
        List<SpaceInvitation> invitations = invitationRepository
                .findByInvitedUserUidAndStatusOrderByCreatedAtDesc(
                        userUid, SpaceInvitation.InvitationStatus.PENDING);

        return invitations.stream()
                .map(invitation -> {
                    Space space = spaceRepository.findById(invitation.getSpaceUid()).orElse(null);
                    String spaceName = space != null ? space.getName() : "알 수 없음";
                    return SpaceInvitationDto.fromEntity(invitation, spaceName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 공간의 초대 목록 조회 (관리자만)
     */
    public List<SpaceInvitationDto> getSpaceInvitations(String spaceUid, String adminUid) {
        // 공간 조회 및 관리자 권한 확인
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        if (!space.isAdmin(adminUid)) {
            throw new IllegalAccessError("공간 관리자만 초대 목록을 조회할 수 있습니다.");
        }

        List<SpaceInvitation> invitations = invitationRepository
                .findBySpaceUidOrderByCreatedAtDesc(spaceUid);

        return invitations.stream()
                .map(invitation -> SpaceInvitationDto.fromEntity(invitation, space.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 공간의 대기중인 초대 목록 조회 (관리자만)
     */
    public List<SpaceInvitationDto> getSpacePendingInvitations(String spaceUid, String adminUid) {
        // 공간 조회 및 관리자 권한 확인
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        if (!space.isAdmin(adminUid)) {
            throw new IllegalAccessError("공간 관리자만 초대 목록을 조회할 수 있습니다.");
        }

        List<SpaceInvitation> invitations = invitationRepository
                .findBySpaceUidAndStatusOrderByCreatedAtDesc(
                        spaceUid, SpaceInvitation.InvitationStatus.PENDING);

        return invitations.stream()
                .map(invitation -> SpaceInvitationDto.fromEntity(invitation, space.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 만료된 초대 자동 처리 (스케줄러)
     * 매일 자정에 실행
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void expireOldInvitations() {
        int count = invitationRepository.expireOldInvitations(LocalDateTime.now());
        if (count > 0) {
            log.info("만료된 초대 처리 완료: {}건", count);
        }
    }

    /**
     * 오래된 응답 초대 삭제 (스케줄러)
     * 매주 일요일 자정에 실행 (30일 이전 응답)
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * SUN")
    public void cleanupOldInvitations() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int count = invitationRepository.deleteOldRespondedInvitations(cutoffDate);
        if (count > 0) {
            log.info("오래된 초대 삭제 완료: {}건", count);
        }
    }
}
