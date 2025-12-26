package com.community.cms.api.space.controller;

import com.community.cms.api.space.dto.*;
import com.community.cms.api.space.service.SpaceService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.Space;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Space Controller
 * 공간 관련 REST API 엔드포인트
 */
@Slf4j
@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;
    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 검증 및 반환
     * UserDetails에서 username을 가져와 DB에서 User 엔티티 조회
     * @param userDetails @AuthenticationPrincipal로 주입된 UserDetails
     * @return 검증된 User 엔티티 (null 가능 - 공개 공간 접근 시)
     */
    private User validateAndGetCurrentUser(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            // 인증되지 않은 사용자 (공개 공간 접근 가능)
            return null;
        }
        // UserDetails의 username으로 DB에서 User 조회
        return userRepository.findByUserId(userDetails.getUsername())
                .orElse(null);
    }

    /**
     * 인증이 필수인 경우 사용자 검증
     * @param userDetails @AuthenticationPrincipal로 주입된 UserDetails
     * @return 검증된 User 엔티티
     */
    private User requireAuthenticatedUser(UserDetails userDetails) {
        User user = validateAndGetCurrentUser(userDetails);
        if (user == null) {
            log.error("Unauthenticated access attempt detected");
            throw new RuntimeException("인증되지 않은 접근입니다");
        }
        return user;
    }

    /**
     * 공간 생성
     * POST /api/spaces
     */
    @PostMapping
    public ResponseEntity<?> createSpace(
            @Valid @RequestBody SpaceCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        try {
            User user = requireAuthenticatedUser(currentUser);
            
            SpaceDto spaceDto = spaceService.createSpace(
                    request, 
                    user.getUid(), 
                    user.getActualName()
            );
            
            return ResponseEntity.ok(spaceDto);
        } catch (IllegalStateException e) {
            // 공간 생성 제한 등의 비즈니스 로직 예외
            log.warn("공간 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 잘못된 요청 파라미터
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            // 기타 예외
            log.error("공간 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "공간 생성 중 오류가 발생했습니다."));
        }
    }

    /**
     * 공간 조회 (공개 공간은 인증 없이 조회 가능)
     * GET /api/spaces/{spaceUid}
     */
    @GetMapping("/{spaceUid}")
    public ResponseEntity<SpaceDto> getSpace(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        String userUid = user != null ? user.getUid() : null;
        SpaceDto spaceDto = spaceService.getSpace(spaceUid, userUid);
        return ResponseEntity.ok(spaceDto);
    }

    /**
     * 채널의 모든 공간 조회 (공개 공간 포함, 접근 가능한 공간만)
     * GET /api/spaces/channel/{channelUid}
     */
    @GetMapping("/channel/{channelUid}")
    public ResponseEntity<List<SpaceDto>> getSpacesByChannel(
            @PathVariable String channelUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        String userUid = user != null ? user.getUid() : null;
        List<SpaceDto> spaces = spaceService.getSpacesByChannel(channelUid, userUid);
        return ResponseEntity.ok(spaces);
    }

    /**
     * 내가 속한 공간 목록 (인증 필수)
     * GET /api/spaces/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<SpaceDto>> getMySpaces(
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = requireAuthenticatedUser(currentUser);
        List<SpaceDto> spaces = spaceService.getMySpaces(user.getUid());
        return ResponseEntity.ok(spaces);
    }

    /**
     * 공간 검색 (공개 공간 포함)
     * GET /api/spaces/search?channelUid={channelUid}&keyword={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<List<SpaceDto>> searchSpaces(
            @RequestParam String channelUid,
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        String userUid = user != null ? user.getUid() : null;
        List<SpaceDto> spaces = spaceService.searchSpaces(
                channelUid, keyword, userUid);
        return ResponseEntity.ok(spaces);
    }

    /**
     * 공간 타입별 조회 (공개 공간 포함)
     * GET /api/spaces/channel/{channelUid}/type/{spaceType}
     */
    @GetMapping("/channel/{channelUid}/type/{spaceType}")
    public ResponseEntity<List<SpaceDto>> getSpacesByType(
            @PathVariable String channelUid,
            @PathVariable Space.SpaceType spaceType,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        String userUid = user != null ? user.getUid() : null;
        List<SpaceDto> spaces = spaceService.getSpacesByType(
                channelUid, spaceType, userUid);
        return ResponseEntity.ok(spaces);
    }

    /**
     * 페이징된 공간 조회 (공개 공간 포함)
     * GET /api/spaces/channel/{channelUid}/paged
     */
    @GetMapping("/channel/{channelUid}/paged")
    public ResponseEntity<Page<SpaceDto>> getSpacesPaged(
            @PathVariable String channelUid,
            @AuthenticationPrincipal UserDetails currentUser,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        User user = validateAndGetCurrentUser(currentUser);
        String userUid = user != null ? user.getUid() : null;
        Page<SpaceDto> spaces = spaceService.getSpacesPaged(
                channelUid, userUid, pageable);
        return ResponseEntity.ok(spaces);
    }

    /**
     * 공간 수정 (관리자만)
     * PUT /api/spaces/{spaceUid}
     */
    @PutMapping("/{spaceUid}")
    public ResponseEntity<SpaceDto> updateSpace(
            @PathVariable String spaceUid,
            @Valid @RequestBody SpaceUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        SpaceDto spaceDto = spaceService.updateSpace(
                spaceUid, request, 
                user.getUid(), 
                user.getActualName()
        );
        
        return ResponseEntity.ok(spaceDto);
    }

    /**
     * 공간 삭제 (비활성화, 관리자만)
     * DELETE /api/spaces/{spaceUid}
     */
    @DeleteMapping("/{spaceUid}")
    public ResponseEntity<Map<String, String>> deleteSpace(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.deleteSpace(spaceUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "공간이 삭제되었습니다."));
    }

    /**
     * 공간 영구 삭제 (관리자만)
     * DELETE /api/spaces/{spaceUid}/hard
     */
    @DeleteMapping("/{spaceUid}/hard")
    public ResponseEntity<Map<String, String>> hardDeleteSpace(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.hardDeleteSpace(spaceUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "공간이 영구 삭제되었습니다."));
    }

    /**
     * 멤버 추가 (관리자만)
     * POST /api/spaces/{spaceUid}/members/{userUid}
     */
    @PostMapping("/{spaceUid}/members/{userUid}")
    public ResponseEntity<Map<String, String>> addMember(
            @PathVariable String spaceUid,
            @PathVariable String userUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.addMember(
                spaceUid, userUid, 
                user.getUid(), 
                user.getActualName()
        );
        
        return ResponseEntity.ok(Map.of("message", "멤버가 추가되었습니다."));
    }

    /**
     * 멤버 제거/추방 (관리자만)
     * DELETE /api/spaces/{spaceUid}/members/{userUid}
     */
    @DeleteMapping("/{spaceUid}/members/{userUid}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable String spaceUid,
            @PathVariable String userUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.removeMember(
                spaceUid, userUid, 
                user.getUid(), 
                user.getActualName()
        );
        
        return ResponseEntity.ok(Map.of("message", "멤버가 제거되었습니다."));
    }

    /**
     * 공간 나가기 (자발적 탈퇴)
     * POST /api/spaces/{spaceUid}/leave
     */
    @PostMapping("/{spaceUid}/leave")
    public ResponseEntity<Map<String, String>> leaveSpace(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.leaveSpace(spaceUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "공간을 나갔습니다."));
    }

    /**
     * 공간 멤버 목록 조회
     * GET /api/spaces/{spaceUid}/members
     */
    @GetMapping("/{spaceUid}/members")
    public ResponseEntity<List<User>> getSpaceMembers(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        List<User> members = spaceService.getSpaceMembers(spaceUid, user.getUid());
        return ResponseEntity.ok(members);
    }

    /**
     * 공간 관리자 변경 (현재 관리자만)
     * PUT /api/spaces/{spaceUid}/admin/{newAdminUid}
     */
    @PutMapping("/{spaceUid}/admin/{newAdminUid}")
    public ResponseEntity<Map<String, String>> transferAdmin(
            @PathVariable String spaceUid,
            @PathVariable String newAdminUid,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = validateAndGetCurrentUser(currentUser);
        spaceService.transferAdmin(spaceUid, newAdminUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "관리자가 변경되었습니다."));
    }

    /**
     * 초대 가능한 사용자 목록 조회 (채널 멤버 중 공간에 속하지 않은 사용자)
     * GET /api/spaces/{spaceUid}/invitable-users?search={search}
     */
    @GetMapping("/{spaceUid}/invitable-users")
    public ResponseEntity<List<Map<String, String>>> getInvitableUsers(
            @PathVariable String spaceUid,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = requireAuthenticatedUser(currentUser);
        List<Map<String, String>> users = spaceService.getInvitableUsers(spaceUid, user.getUid(), search);
        return ResponseEntity.ok(users);
    }

    /**
     * 다중 사용자 초대
     * POST /api/spaces/{spaceUid}/invite-multiple
     */
    @PostMapping("/{spaceUid}/invite-multiple")
    public ResponseEntity<?> inviteMultipleUsers(
            @PathVariable String spaceUid,
            @RequestBody Map<String, List<String>> requestBody,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        try {
            User user = requireAuthenticatedUser(currentUser);
            List<String> userUids = requestBody.get("userUids");
            
            if (userUids == null || userUids.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "초대할 사용자를 선택해주세요."));
            }
            
            int invitedCount = spaceService.inviteMultipleUsers(
                    spaceUid, userUids, 
                    user.getUid(), 
                    user.getActualName()
            );
            
            return ResponseEntity.ok(Map.of(
                    "message", invitedCount + "명의 사용자를 초대했습니다.",
                    "count", invitedCount
            ));
        } catch (IllegalArgumentException e) {
            log.warn("초대 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("초대 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "초대 중 오류가 발생했습니다."));
        }
    }
}

