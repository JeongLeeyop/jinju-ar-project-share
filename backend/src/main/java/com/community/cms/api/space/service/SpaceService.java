package com.community.cms.api.space.service;

import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.space.dto.*;
import com.community.cms.api.space.repository.SpaceInvitationRepository;
import com.community.cms.api.space.repository.SpaceMemberRepository;
import com.community.cms.api.space.repository.SpaceRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.Space;
import com.community.cms.entity.SpaceInvitation;
import com.community.cms.entity.SpaceMember;
import com.community.cms.entity.User;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.util.ActivityLogHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final SpaceMemberRepository spaceMemberRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ActivityLogHelper activityLogHelper;

    /**
     * 공간 생성
     */
    @Transactional
    public SpaceDto createSpace(SpaceCreateRequest request, String adminUid, String adminName) {
        // 관리자 사용자 조회
        User admin = userRepository.findById(adminUid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 공간 이름 길이 검증 (최대 7자)
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("공간 이름을 입력해주세요.");
        }
        if (request.getName().trim().length() > 7) {
            throw new IllegalArgumentException("공간 이름은 최대 7자까지 가능합니다.");
        }

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

        // 공개 공간(게시판, 채팅)이 아닌 경우에만 생성자를 멤버로 추가
        // 공개 공간은 커뮤니티 멤버를 동적으로 조회하므로 별도로 추가하지 않음
        if (!savedSpace.isPublic()) {
            savedSpace.addMember(admin);
            spaceRepository.save(savedSpace);
        }

        // 활동 로그 기록
        activityLogHelper.logSpaceCreated(
                adminUid, adminName,
                request.getChannelUid(), "",
                savedSpace.getUid(), savedSpace.getName());

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

        // 접근 권한 확인 (공개 공간은 누구나 접근 가능, 커뮤니티 관리자는 모든 공간 접근 가능)
        validateAccess(space, currentUserUid);

        SpaceDto dto = SpaceDto.fromEntity(space, currentUserUid);
        
        // 커뮤니티 관리자인 경우 isAdmin을 true로 설정 (관리 권한 부여)
        // 하지만 isSpaceAdmin은 false로 유지 (뱃지 표시 안함)
        if (currentUserUid != null && isChannelAdmin(space.getChannelUid(), currentUserUid)) {
            dto.setAdmin(true);
            if (!space.isAdmin(currentUserUid)) {
                dto.setSpaceAdmin(false);  // 공간 관리자가 아니면 뱃지 표시 안함
            }
            log.debug("커뮤니티 관리자로 공간 조회: userUid={}, spaceUid={}, isSpaceAdmin={}", 
                     currentUserUid, spaceUid, dto.isSpaceAdmin());
        }
        
        // 공개 공간인 경우: 실제 커뮤니티 멤버 수로 업데이트
        if (space.isPublic()) {
            int actualMemberCount = getPublicSpaceMemberCount(space);
            dto.setMemberCount(actualMemberCount);
            
            // 현재 사용자가 커뮤니티 멤버이면 공간 멤버로 표시
            if (currentUserUid != null) {
                dto.setMember(isChannelMember(space.getChannelUid(), currentUserUid));
            }
        }
        
        return dto;
    }
    
    /**
     * 공개 공간의 실제 멤버 수 조회 (커뮤니티 전체 멤버 수)
     */
    private int getPublicSpaceMemberCount(Space space) {
        try {
            Channel channel = channelRepository.findByDomain(space.getChannelUid()).orElse(null);
            if (channel == null) {
                return 0;
            }
            
            long count = channelMemberRepository.countByChannelUidAndApprovalStatus(channel.getUid(), true);
            return (int) count;
            
        } catch (Exception e) {
            log.error("공개 공간 멤버 수 조회 실패: spaceUid={}, error={}", space.getUid(), e.getMessage());
            return 0;
        }
    }

    /**
     * 채널의 모든 공간 조회
     */
    public List<SpaceDto> getSpacesByChannel(String channelUid, String currentUserUid) {
        log.info("=== 공간 조회 시작 ===");
        log.info("channelUid(domain): {}, currentUserUid: {}", channelUid, currentUserUid);
        
        List<Space> spaces = spaceRepository.findAccessibleSpaces(channelUid, currentUserUid);
        log.info("조회된 공간 수: {}", spaces.size());
        
        boolean isChannelAdminUser = currentUserUid != null && isChannelAdmin(channelUid, currentUserUid);
        log.info("커뮤니티 관리자 여부: {}", isChannelAdminUser);
        
        return spaces.stream()
                .map(space -> {
                    log.debug("공간 처리: name={}, isPublic={}, adminUid={}", 
                             space.getName(), space.isPublic(), space.getAdminUid());
                    
                    SpaceDto dto = SpaceDto.fromEntity(space, currentUserUid);
                    
                    // 커뮤니티 관리자인 경우 isAdmin을 true로 설정 (관리 권한 부여)
                    // 하지만 isSpaceAdmin은 공간 관리자만 true (뱃지 표시)
                    if (isChannelAdminUser) {
                        dto.setAdmin(true);
                        if (!space.isAdmin(currentUserUid)) {
                            dto.setSpaceAdmin(false);  // 공간 관리자가 아니면 뱃지 표시 안함
                        }
                    }
                    
                    // 공개 공간인 경우: 실제 커뮤니티 멤버 수로 업데이트
                    if (space.isPublic()) {
                        int actualMemberCount = getPublicSpaceMemberCount(space);
                        dto.setMemberCount(actualMemberCount);
                        
                        // 현재 사용자가 커뮤니티 멤버이면 공간 멤버로 표시
                        if (currentUserUid != null) {
                            dto.setMember(isChannelMember(space.getChannelUid(), currentUserUid));
                        }
                    }
                    
                    return dto;
                })
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
        List<Space> spaces = spaceRepository
                .findByChannelUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc(
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
    public SpaceDto updateSpace(String spaceUid, SpaceUpdateRequest request, String currentUserUid,
            String currentUserName) {
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
                spaceUid, space.getName());

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
     * - 공개 공간(isPublic=true): 커뮤니티 전체 멤버 동적 조회
     * - 비공개 공간: 실제 space_member 테이블에서 조회
     */
    public List<SpaceMemberDto> getSpaceMembers(String spaceUid) {
        // Space 존재 확인
        Space space = spaceRepository.findById(spaceUid)
                .orElseThrow(() -> new RuntimeException("공간을 찾을 수 없습니다"));

        // 공개 공간인 경우: 커뮤니티 전체 멤버 반환 (동적 조회)
        if (space.isPublic()) {
            return getPublicSpaceMembers(space);
        }

        // 비공개 공간: 실제 멤버 목록 조회
        List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceUid(spaceUid);

        // User 정보를 DTO로 변환
        return spaceMembers.stream()
                .map(spaceMember -> {
                    User user = userRepository.findByUid(spaceMember.getUserUid())
                            .orElse(null);
                    return user != null ? SpaceMemberDto.from(user) : null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
    
    /**
     * 공개 공간의 멤버 목록 조회 (커뮤니티 전체 멤버)
     * 채널의 승인된 멤버 전체를 반환 (자동 동기화)
     */
    private List<SpaceMemberDto> getPublicSpaceMembers(Space space) {
        try {
            // 1. Channel 조회 (domain으로)
            Channel channel = channelRepository.findByDomain(space.getChannelUid())
                    .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
            
            // 2. 채널의 승인된 멤버 목록 조회
            List<ChannelMember> channelMembers = channelMemberRepository
                    .findByChannelUidAndApprovalStatus(channel.getUid(), true);
            
            // 3. User 정보 조회 및 DTO 변환
            List<SpaceMemberDto> members = channelMembers.stream()
                    .map(channelMember -> {
                        User user = userRepository.findByUid(channelMember.getUserUid())
                                .orElse(null);
                        return user != null ? SpaceMemberDto.from(user) : null;
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            log.info("공개 공간 멤버 조회: spaceUid={}, spaceType={}, channelUid={}, memberCount={}", 
                    space.getUid(), space.getSpaceType(), channel.getUid(), members.size());
            
            return members;
            
        } catch (Exception e) {
            log.error("공개 공간 멤버 조회 실패: spaceUid={}, error={}", space.getUid(), e.getMessage());
            // 실패 시 빈 목록 반환
            return new ArrayList<>();
        }
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
        long existingSpaceCount = spaceRepository
                .countByChannelUidAndAdminUidAndSpaceTypeAndIsActiveTrueAndIsDeletedFalse(
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

        log.info("=== 초대 가능한 사용자 조회 시작 ===");
        log.info("spaceUid: {}, channelUid(domain): {}, search: '{}'", spaceUid, space.getChannelUid(), search);

        // 관리자 권한 확인
        validateAdmin(space, adminUid);

        // 공개 공간인 경우 초대 불가
        if (space.isPublic()) {
            throw new IllegalArgumentException("공개 공간은 초대 기능을 사용할 수 없습니다.");
        }

        // 1. 먼저 채널 멤버 전체 조회
        List<ChannelMember> allChannelMembers = channelMemberRepository
                .findByChannelUidAndApprovalStatus(space.getChannelUid(), true);
        log.info("채널 전체 승인된 멤버 수: {}", allChannelMembers.size());
        if (!allChannelMembers.isEmpty()) {
            log.info("채널 멤버 목록 (최대 5명):");
            allChannelMembers.stream().limit(5).forEach(cm -> {
                User u = userRepository.findById(cm.getUserUid()).orElse(null);
                log.info("  - ChannelMember: userUid={}, channelUid={}, approvalStatus={}, userName={}", 
                    cm.getUserUid(), cm.getChannelUid(), cm.isApprovalStatus(), 
                    u != null ? u.getActualName() : "N/A");
            });
        }

        // 2. 현재 공간 멤버 조회
        List<SpaceMember> spaceMembers = spaceMemberRepository.findBySpaceUid(spaceUid);
        log.info("현재 공간 멤버 수: {}", spaceMembers.size());
        if (!spaceMembers.isEmpty()) {
            log.info("공간 멤버 목록:");
            spaceMembers.forEach(sm -> {
                log.info("  - SpaceMember: userUid={}, spaceUid={}, userName={}", 
                    sm.getUserUid(), sm.getSpaceUid(), 
                    sm.getUser() != null ? sm.getUser().getActualName() : "N/A");
            });
        }

        // 3. 채널 멤버 중 공간에 속하지 않은 사용자 조회 (쿼리) - 본인 제외
        List<User> channelMembers = userRepository.findChannelMembersNotInSpace(
                space.getChannelUid(), spaceUid, adminUid, search);
        log.info("쿼리 결과 초대 가능한 사용자 수 (본인 제외): {}", channelMembers.size());
        if (!channelMembers.isEmpty()) {
            log.info("쿼리 결과 사용자 목록:");
            channelMembers.forEach(u -> log.info("  - {}: {} (uid={})", u.getActualName(), u.getUserId(), u.getUid()));
        }

        // 4. 검증을 위해 수동으로도 필터링해보기
        List<String> spaceMemberUids = spaceMembers.stream()
                .map(sm -> sm.getUserUid())
                .collect(Collectors.toList());
        
        log.info("제외할 공간 멤버 UID 목록: {}", spaceMemberUids);
        log.info("제외할 본인 UID: {}", adminUid);
        
        List<User> manualFiltered = allChannelMembers.stream()
                .map(cm -> userRepository.findById(cm.getUserUid()).orElse(null))
                .filter(u -> u != null)
                .filter(u -> {
                    // 본인 제외
                    if (u.getUid().equals(adminUid)) {
                        log.debug("제외됨 (본인): {}", u.getActualName());
                        return false;
                    }
                    return true;
                })
                .filter(u -> {
                    boolean notInSpace = !spaceMemberUids.contains(u.getUid());
                    if (!notInSpace) {
                        log.debug("제외됨 (이미 공간 멤버): {}", u.getActualName());
                    }
                    return notInSpace;
                })
                .filter(u -> {
                    if (search == null || search.isEmpty()) return true;
                    boolean matches = u.getActualName().toLowerCase().contains(search.toLowerCase());
                    if (!matches) {
                        log.debug("제외됨 (검색어 불일치): {}", u.getActualName());
                    }
                    return matches;
                })
                .collect(Collectors.toList());
        
        log.info("수동 필터링 결과 사용자 수: {}", manualFiltered.size());
        if (!manualFiltered.isEmpty()) {
            log.info("수동 필터링 결과 사용자 목록:");
            manualFiltered.forEach(u -> log.info("  - {}: {} (uid={})", u.getActualName(), u.getUserId(), u.getUid()));
        }
        
        log.info("=== 초대 가능한 사용자 조회 종료 ===");

        // 쿼리 결과가 비어있으면 수동 필터링 결과 사용
        List<User> resultUsers = channelMembers.isEmpty() && !manualFiltered.isEmpty() 
                ? manualFiltered 
                : channelMembers;

        // 최종 결과에서 본인 제외 (쿼리 결과에도 본인이 포함될 수 있으므로)
        resultUsers = resultUsers.stream()
                .filter(u -> !u.getUid().equals(adminUid))
                .collect(Collectors.toList());
        
        log.info("본인 제외 후 최종 초대 가능한 사용자 수: {}", resultUsers.size());

        return resultUsers.stream()
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

                // 최대 멤버 수 확인
                if (!space.canAddMember()) {
                    log.warn("최대 멤버 수에 도달했습니다: spaceUid={}", spaceUid);
                    break; // 더 이상 추가 불가
                }

                // ✅ 바로 공간에 멤버 추가 (승인/만료 처리 없이 즉시 입장)
                space.addMember(user);
                
                log.info("✅ 초대와 동시에 멤버로 추가됨: spaceUid={}, userUid={}", spaceUid, userUid);

                // 초대 기록은 히스토리 용도로만 생성 (ACCEPTED 상태로 즉시 저장)
                SpaceInvitation invitation = SpaceInvitation.builder()
                        .spaceUid(spaceUid)
                        .inviterUid(adminUid)
                        .inviterName(adminName)
                        .invitedUserUid(userUid)
                        .invitedUserName(user.getActualName())
                        .expiresAt(LocalDateTime.now().plusDays(7)) // 기본값(의미없음, 히스토리용)
                        .build();
                
                // 바로 수락 상태로 저장 (히스토리 기록용)
                invitation.accept();
                invitationRepository.save(invitation);
                
                invitedUserNames.add(user.getActualName());
                successCount++;

                // 활동 로그 기록 - 초대 및 참여
                activityLogHelper.logSpaceInvited(
                        userUid, 
                        user.getActualName(),
                        space.getChannelUid(), 
                        "",
                        space.getName()
                );
                
                activityLogHelper.logSpaceJoined(
                        userUid, 
                        user.getActualName(),
                        space.getChannelUid(), 
                        "",
                        spaceUid,
                        space.getName()
                );

                log.info("초대 및 멤버 추가 완료: spaceUid={}, inviterUid={}, invitedUserUid={}",
                        spaceUid, adminUid, userUid);

            } catch (Exception e) {
                log.error("사용자 초대 중 오류 발생: userUid={}, error={}", userUid, e.getMessage());
            }
        }

        // 공간 저장 (멤버 추가 반영)
        if (successCount > 0) {
            spaceRepository.save(space);
            log.info("✅ 공간에 {}명의 멤버 추가 완료: spaceUid={}", successCount, spaceUid);
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
     * - 비공개 공간이면 userUid가 있어야 하고, 커뮤니티 관리자, 공간 관리자 또는 멤버여야 함
     */
    private boolean hasAccess(Space space, String userUid) {
        // 공개 공간이면 인증 없이 접근 가능
        if (space.isPublic()) {
            return true;
        }

        // 비공개 공간이면 userUid가 있어야 함
        if (userUid == null) {
            return false;
        }

        // 1. 공간 관리자인지 확인
        if (space.isAdmin(userUid)) {
            return true;
        }

        // 2. 커뮤니티 관리자인지 확인
        try {
            Channel channel = channelRepository.findByDomain(space.getChannelUid()).orElse(null);
            if (channel != null && channel.getUserUid() != null && channel.getUserUid().equals(userUid)) {
                log.debug("커뮤니티 관리자로 비공개 공간 접근 허용: userUid={}, spaceUid={}", userUid, space.getUid());
                return true;
            }
        } catch (Exception e) {
            log.error("커뮤니티 관리자 확인 중 오류: {}", e.getMessage());
        }

        // 3. 공간 멤버인지 확인
        return isSpaceMember(space, userUid);
    }
    
    /**
     * 사용자가 공간의 멤버인지 확인
     * - 공개 공간(isPublic=true): 커뮤니티 멤버이면 자동으로 공간 멤버
     * - 비공개 공간: space_member 테이블에서 확인
     */
    private boolean isSpaceMember(Space space, String userUid) {
        // 공개 공간인 경우: 커뮤니티 멤버 여부 확인
        if (space.isPublic()) {
            return isChannelMember(space.getChannelUid(), userUid);
        }
        
        // 비공개 공간: 실제 멤버 테이블 확인
        return space.isMember(userUid);
    }
    
    /**
     * 사용자가 채널의 승인된 멤버인지 확인
     */
    private boolean isChannelMember(String channelDomain, String userUid) {
        try {
            // Channel 조회 (domain으로)
            Channel channel = channelRepository.findByDomain(channelDomain).orElse(null);
            if (channel == null) {
                return false;
            }
            
            // 채널 멤버 여부 확인
            return channelMemberRepository.existsByUserUidAndChannelUidAndApprovalStatus(
                    userUid, channel.getUid(), true);
                    
        } catch (Exception e) {
            log.error("채널 멤버 확인 실패: channelDomain={}, userUid={}, error={}", 
                    channelDomain, userUid, e.getMessage());
            return false;
        }
    }

    /**
     * 사용자가 채널 관리자인지 확인
     */
    private boolean isChannelAdmin(String channelDomain, String userUid) {
        try {
            Channel channel = channelRepository.findByDomain(channelDomain).orElse(null);
            if (channel == null) {
                return false;
            }
            
            return channel.getUserUid() != null && channel.getUserUid().equals(userUid);
                    
        } catch (Exception e) {
            log.error("채널 관리자 확인 실패: channelDomain={}, userUid={}, error={}", 
                    channelDomain, userUid, e.getMessage());
            return false;
        }
    }

    /**
     * 관리자 권한 검증 (공간 관리자 또는 커뮤니티 관리자 허용)
     */
    private void validateAdmin(Space space, String userUid) {
        log.info("=== 관리자 권한 검증 시작 ===");
        log.info("spaceUid: {}, userUid: {}, channelUid(domain): {}", space.getUid(), userUid, space.getChannelUid());
        
        // 1. 공간 관리자 체크
        if (space.getAdminUid().equals(userUid)) {
            log.info("✅ 공간 관리자로 확인됨: userUid={}, spaceUid={}", userUid, space.getUid());
            return;
        }
        
        // 2. 커뮤니티(채널) 관리자 체크 - Channel의 userUid 확인
        // channelUid에는 domain이 저장되어 있으므로 findByDomain 사용
        log.info("공간 관리자 아님. 커뮤니티 관리자 권한 확인 시작...");
        
        try {
            Channel channel = channelRepository.findByDomain(space.getChannelUid()).orElse(null);
            
            if (channel != null) {
                log.info("Channel 조회 성공: domain={}, channelUid={}, channelUserUid={}", 
                        channel.getDomain(), channel.getUid(), channel.getUserUid());
                
                // Channel의 userUid와 현재 사용자 UID 비교
                if (channel.getUserUid() != null && channel.getUserUid().equals(userUid)) {
                    log.info("✅ 커뮤니티 관리자로 확인됨 (Channel.userUid): userUid={}", userUid);
                    return;
                }
                
                log.warn("❌ 커뮤니티 관리자 아님: userUid={}, channelUserUid={}", 
                        userUid, channel.getUserUid());
            } else {
                log.warn("❌ Channel을 찾을 수 없음: domain={}", space.getChannelUid());
            }
            
        } catch (Exception e) {
            log.error("Channel 조회 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("=== 관리자 권한 검증 실패 ===");
        throw new IllegalAccessError("공간 관리자 또는 커뮤니티 관리자만 수행할 수 있습니다.");
    }
}
