package com.community.cms.api.space.service;

import com.community.cms.api.space.dto.*;
import com.community.cms.api.space.repository.SpaceInvitationRepository;
import com.community.cms.api.space.repository.SpaceRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.Space;
import com.community.cms.entity.SpaceInvitation;
import com.community.cms.entity.SpaceMember;
import com.community.cms.entity.User;
import com.community.cms.util.ActivityLogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Space Service
 * 공간 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceInvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final ActivityLogHelper activityLogHelper;

    /**
     * 공간 생성
     */
    @Transactional
    public SpaceDto createSpace(SpaceCreateRequest request, String adminUid, String adminName) {
        // 관리자 사용자 조회
        User admin = userRepository.findById(adminUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 공간 생성 제한 확인: 사용자당 게시판 1개, 채팅 1개만 허용
        validateSpaceCreationLimit(request.getChannelUid(), adminUid, request.getSpaceType());

        // 공간 생성
        Space space = Space.builder()
                .channelUid(request.getChannelUid())
                .name(request.getName())
                .description(request.getDescription())
                .spaceType(request.getSpaceType())
                .adminUid(adminUid)
                .iconUrl(request.getIconUrl())
                .invitationRequired(request.isInvitationRequired())
                .isPublic(request.isPublic())
                .maxMembers(request.getMaxMembers())
                .isActive(true)
                .build();

        // 먼저 공간을 저장하여 uid를 생성
        Space savedSpace = spaceRepository.save(space);

        // 저장 후 생성자를 멤버로 추가 (이제 space.uid가 존재함)
        savedSpace.addMember(admin);

        // 활동 로그 기록
        activityLogHelper.logSpaceCreated(
                adminUid, adminName,
                request.getChannelUid(), "",
                savedSpace.getUid(), savedSpace.getName()
        );

        log.info("공간 생성 완료: spaceUid={}, name={}, adminUid={}, spaceType={}, isPublic={}", 
                savedSpace.getUid(), savedSpace.getName(), adminUid, savedSpace.getSpaceType(), savedSpace.isPublic());

        return SpaceDto.fromEntity(savedSpace, adminUid);
    }

    /**
     * 공간 조회 (공개 공간은 인증 없이 접근 가능)
     */
    public SpaceDto getSpace(String spaceUid, String currentUserUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 접근 권한 확인 (공개 공간은 누구나 접근 가능)
        validateAccess(space, currentUserUid);

        return SpaceDto.fromEntity(space, currentUserUid);
    }

    /**
     * 채널의 모든 공간 조회
     */
    public List<SpaceDto> getSpacesByChannel(String channelUid, String currentUserUid) {
        List<Space> spaces = spaceRepository.findAccessibleSpaces(channelUid, currentUserUid);
        return spaces.stream()
                .map(space -> SpaceDto.fromEntity(space, currentUserUid))
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 멤버인 공간 조회
     */
    public List<SpaceDto> getMySpaces(String userUid) {
        List<Space> spaces = spaceRepository.findSpacesByMemberUid(userUid);
        return spaces.stream()
                .map(space -> SpaceDto.fromEntity(space, userUid))
                .collect(Collectors.toList());
    }

    /**
     * 공간 검색
     */
    public List<SpaceDto> searchSpaces(String channelUid, String keyword, String currentUserUid) {
        List<Space> spaces = spaceRepository.searchByNameInChannel(channelUid, keyword);
        // 접근 권한 필터링
        return spaces.stream()
                .filter(space -> hasAccess(space, currentUserUid))
                .map(space -> SpaceDto.fromEntity(space, currentUserUid))
                .collect(Collectors.toList());
    }

    /**
     * 공간 타입별 조회
     */
    public List<SpaceDto> getSpacesByType(String channelUid, Space.SpaceType spaceType, String currentUserUid) {
        List<Space> spaces = spaceRepository.findByChannelUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc(
                channelUid, spaceType);
        return spaces.stream()
                .filter(space -> hasAccess(space, currentUserUid))
                .map(space -> SpaceDto.fromEntity(space, currentUserUid))
                .collect(Collectors.toList());
    }

    /**
     * 페이징된 공간 조회 (공개 공간 포함)
     * - 공개 공간(isPublic=true)은 비인증 사용자도 조회 가능
     * - 비공개 공간은 관리자 또는 멤버만 조회 가능
     */
    public Page<SpaceDto> getSpacesPaged(String channelUid, String currentUserUid, Pageable pageable) {
        Page<Space> spaces = spaceRepository.findAccessibleSpacesPaged(channelUid, currentUserUid, pageable);
        return spaces.map(space -> SpaceDto.fromEntity(space, currentUserUid));
    }

    /**
     * 공간 수정
     */
    @Transactional
    public SpaceDto updateSpace(String spaceUid, SpaceUpdateRequest request, String currentUserUid, String currentUserName) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인
        validateAdmin(space, currentUserUid);

        // 수정 가능한 필드 업데이트
        if (request.getName() != null) {
            space.setName(request.getName());
        }
        if (request.getDescription() != null) {
            space.setDescription(request.getDescription());
        }
        if (request.getIconUrl() != null) {
            space.setIconUrl(request.getIconUrl());
        }
        if (request.getInvitationRequired() != null) {
            space.setInvitationRequired(request.getInvitationRequired());
        }
        if (request.getIsPublic() != null) {
            space.setPublic(request.getIsPublic());
        }
        if (request.getIsActive() != null) {
            space.setActive(request.getIsActive());
        }
        if (request.getMaxMembers() != null) {
            space.setMaxMembers(request.getMaxMembers());
        }

        Space updatedSpace = spaceRepository.save(space);

        log.info("공간 수정 완료: spaceUid={}, adminUid={}", spaceUid, currentUserUid);

        return SpaceDto.fromEntity(updatedSpace, currentUserUid);
    }

    /**
     * 공간 삭제 (비활성화)
     */
    @Transactional
    public void deleteSpace(String spaceUid, String currentUserUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인
        validateAdmin(space, currentUserUid);

        // 소프트 삭제 (비활성화)
        space.setActive(false);
        spaceRepository.save(space);

        log.info("공간 삭제(비활성화) 완료: spaceUid={}, adminUid={}", spaceUid, currentUserUid);
    }

    /**
     * 공간 영구 삭제
     */
    @Transactional
    public void hardDeleteSpace(String spaceUid, String currentUserUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인
        validateAdmin(space, currentUserUid);

        // 관련 초대 삭제
        List<SpaceInvitation> invitations = invitationRepository.findBySpaceUidOrderByCreatedAtDesc(spaceUid);
        invitationRepository.deleteAll(invitations);

        // 공간 영구 삭제
        spaceRepository.delete(space);

        log.info("공간 영구 삭제 완료: spaceUid={}, adminUid={}", spaceUid, currentUserUid);
    }

    /**
     * 멤버 추가 (관리자만)
     */
    @Transactional
    public void addMember(String spaceUid, String userUid, String adminUid, String adminName) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인
        validateAdmin(space, adminUid);

        // 최대 멤버 수 확인
        if (!space.canAddMember()) {
            throw new IllegalStateException("최대 멤버 수에 도달했습니다.");
        }

        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 멤버인지 확인
        if (space.isMember(userUid)) {
            throw new IllegalStateException("이미 공간의 멤버입니다.");
        }

        space.addMember(user);
        spaceRepository.save(space);

        // 활동 로그 기록
        activityLogHelper.logSpaceJoined(
                userUid, user.getActualName(),
                space.getChannelUid(), "",
                spaceUid, space.getName()
        );

        log.info("멤버 추가 완료: spaceUid={}, userUid={}, adminUid={}", spaceUid, userUid, adminUid);
    }

    /**
     * 멤버 제거 (관리자만, 추방)
     */
    @Transactional
    public void removeMember(String spaceUid, String userUid, String adminUid, String adminName) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자 권한 확인
        validateAdmin(space, adminUid);

        // 관리자 자신은 제거할 수 없음
        if (space.getAdminUid().equals(userUid)) {
            throw new IllegalStateException("공간 관리자는 제거할 수 없습니다.");
        }

        space.removeMember(userUid);
        spaceRepository.save(space);

        log.info("멤버 제거 완료: spaceUid={}, userUid={}, adminUid={}", spaceUid, userUid, adminUid);
    }

    /**
     * 공간 나가기 (자발적 탈퇴)
     */
    @Transactional
    public void leaveSpace(String spaceUid, String userUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 관리자는 나갈 수 없음
        if (space.getAdminUid().equals(userUid)) {
            throw new IllegalStateException("공간 관리자는 공간을 나갈 수 없습니다. 공간을 삭제하거나 관리자를 변경해주세요.");
        }

        space.removeMember(userUid);
        spaceRepository.save(space);

        log.info("공간 나가기 완료: spaceUid={}, userUid={}", spaceUid, userUid);
    }

    /**
     * 공간 멤버 목록 조회
     */
    public List<User> getSpaceMembers(String spaceUid, String currentUserUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 접근 권한 확인
        validateAccess(space, currentUserUid);

        // SpaceMember를 통해 User 목록 반환
        return space.getSpaceMembers().stream()
                .map(SpaceMember::getUser)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    /**
     * 공간 관리자 변경
     */
    @Transactional
    public void transferAdmin(String spaceUid, String newAdminUid, String currentAdminUid) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));

        // 현재 관리자 권한 확인
        validateAdmin(space, currentAdminUid);

        // 새 관리자가 멤버인지 확인
        if (!space.isMember(newAdminUid)) {
            throw new IllegalStateException("새 관리자는 공간의 멤버여야 합니다.");
        }

        space.setAdminUid(newAdminUid);
        spaceRepository.save(space);

        log.info("공간 관리자 변경 완료: spaceUid={}, oldAdminUid={}, newAdminUid={}", 
                spaceUid, currentAdminUid, newAdminUid);
    }

    /**
     * 공간 생성 제한 확인
     * 사용자는 채널당 게시판(BOARD) 1개, 채팅(CHAT) 1개만 생성 가능
     */
    private void validateSpaceCreationLimit(String channelUid, String adminUid, Space.SpaceType spaceType) {
        // 해당 채널에서 사용자가 관리자로 있는 같은 타입의 활성 공간 개수 확인
        long existingSpaceCount = spaceRepository.countByChannelUidAndAdminUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalse(
                channelUid, adminUid, spaceType);
        
        if (existingSpaceCount > 0) {
            String typeDisplay = spaceType == Space.SpaceType.BOARD ? "게시판" : "채팅";
            throw new IllegalStateException(
                    String.format("이미 %s 공간을 생성하셨습니다. 채널당 %s 공간은 1개만 생성할 수 있습니다.", 
                            typeDisplay, typeDisplay));
        }
        
        log.debug("공간 생성 제한 확인 완료: channelUid={}, adminUid={}, spaceType={}, existingCount={}", 
                channelUid, adminUid, spaceType, existingSpaceCount);
    }

    /**
     * 초대 가능한 사용자 목록 조회 (채널 멤버 중 공간에 속하지 않은 사용자)
     */
    public List<Map<String, String>> getInvitableUsers(String spaceUid, String adminUid, String search) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));
        
        // 관리자 권한 확인
        validateAdmin(space, adminUid);
        
        // 공개 공간인 경우 초대 불가
        if (space.isPublic()) {
            throw new IllegalArgumentException("공개 공간은 초대 기능을 사용할 수 없습니다.");
        }
        
        // 채널 멤버 중 공간에 속하지 않은 사용자 조회
        List<User> channelMembers = userRepository.findChannelMembersNotInSpace(
                space.getChannelUid(), spaceUid, search);
        
        return channelMembers.stream()
                .map(user -> {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("uid", user.getUid());
                    userInfo.put("actualName", user.getActualName());
                    userInfo.put("userId", user.getUserId());
                    return userInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 다중 사용자 초대
     */
    @Transactional
    public int inviteMultipleUsers(String spaceUid, List<String> userUids, String adminUid, String adminName) {
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다."));
        
        // 관리자 권한 확인
        validateAdmin(space, adminUid);
        
        // 공개 공간인 경우 초대 불가
        if (space.isPublic()) {
            throw new IllegalArgumentException("공개 공간은 초대 기능을 사용할 수 없습니다.");
        }
        
        int successCount = 0;
        List<String> invitedUserNames = new ArrayList<>();
        
        for (String userUid : userUids) {
            try {
                // 사용자 조회
                User user = userRepository.findById(userUid)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userUid));
                
                // 이미 멤버인지 확인
                if (space.isMember(userUid)) {
                    log.warn("이미 공간의 멤버입니다: userUid={}, spaceUid={}", userUid, spaceUid);
                    continue;
                }
                
                // 이미 대기중인 초대가 있는지 확인
                boolean hasPendingInvitation = invitationRepository
                        .existsBySpaceUidAndInvitedUserUidAndStatus(
                                spaceUid, userUid, SpaceInvitation.InvitationStatus.PENDING);
                
                if (hasPendingInvitation) {
                    log.warn("이미 대기중인 초대가 있습니다: userUid={}, spaceUid={}", userUid, spaceUid);
                    continue;
                }
                
                // 초대 생성
                SpaceInvitation invitation = SpaceInvitation.builder()
                        .spaceUid(spaceUid)
                        .inviterUid(adminUid)
                        .invitedUserUid(userUid)
                        .status(SpaceInvitation.InvitationStatus.PENDING)
                        .build();
                
                invitationRepository.save(invitation);
                invitedUserNames.add(user.getActualName());
                successCount++;
                
                log.info("공간 초대 생성 완료: spaceUid={}, inviterUid={}, invitedUserUid={}", 
                        spaceUid, adminUid, userUid);
                
            } catch (Exception e) {
                log.error("사용자 초대 중 오류 발생: userUid={}, error={}", userUid, e.getMessage());
            }
        }
        
        // 활동 로그 기록 (초대한 사용자가 1명 이상인 경우)
        if (successCount > 0) {
            String description = String.format("%s님이 %d명의 사용자를 초대했습니다: %s", 
                    adminName, successCount, String.join(", ", invitedUserNames));
            
            activityLogHelper.logCustomActivity(
                    "SPACE_MEMBERS_INVITED",
                    adminUid, adminName,
                    space.getChannelUid(), "",
                    spaceUid, space.getName(),
                    description
            );
        }
        
        return successCount;
    }

    // === 헬퍼 메서드 ===

    /**
     * 접근 권한 확인
     * 공개 공간(isPublic=true)인 경우 인증 없이 접근 가능
     * 비공개 공간인 경우 관리자 또는 멤버만 접근 가능
     */
    private void validateAccess(Space space, String userUid) {
        if (!hasAccess(space, userUid)) {
            throw new IllegalAccessError("공간에 접근할 수 없습니다.");
        }
    }

    /**
     * 접근 가능 여부 확인
     * - 공개 공간(isPublic=true)이면 누구나 접근 가능 (userUid가 null이어도 OK)
     * - 비공개 공간이면 관리자 또는 멤버만 접근 가능
     */
    private boolean hasAccess(Space space, String userUid) {
        // 공개 공간이면 인증 없이 접근 가능
        if (space.isPublic()) {
            return true;
        }
        
        // 비공개 공간이면 userUid가 있어야 하고, 관리자 또는 멤버여야 함
        if (userUid == null) {
            return false;
        }
        
        return space.isAdmin(userUid) || space.isMember(userUid);
    }

    /**
     * 관리자 권한 확인
     */
    private void validateAdmin(Space space, String userUid) {
        if (!space.isAdmin(userUid)) {
            throw new IllegalAccessError("공간 관리자만 수행할 수 있습니다.");
        }
    }
}
