package com.community.cms.api.point.controller;

import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.service.PointService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity2.Channel;
import com.community.cms.entity.User;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.oauth.SinghaUser;
import com.community.cms.util.ActivityLogHelper;
import com.community.cms.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 포인트 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ActivityLogHelper activityLogHelper;
    private final AuthenticationUtil authenticationUtil;
    /**
     * 현재 인증된 사용자 조회 및 검증
     */
    

    /**
     * 채널 관리자 권한 검증
     */
    private void validateChannelAdmin(String channelUid, String userUid) {
        Channel channel = channelRepository.findByUid(channelUid)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        
        if (!channel.getUserUid().equals(userUid)) {
            throw new RuntimeException("채널 관리자만 포인트 지급/차감을 할 수 있습니다");
        }
    }

    /**
     * 현재 포인트 조회
     * GET /api/points/current?channelUid={channelUid}
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPoint(
            @RequestParam String channelUid,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            Integer currentPoint = pointService.getCurrentPoint(user.getUid(), channelUid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("currentPoint", currentPoint);
            response.put("channelUid", channelUid);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to get current point: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 포인트 히스토리 조회 (페이징)
     * GET /api/points/history?channelUid={channelUid}&page=0&size=20
     */
    @GetMapping("/history")
    public ResponseEntity<?> getPointHistory(
            @RequestParam String channelUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            Map<String, Object> result = pointService.getPointHistory(
                    user.getUid(), 
                    channelUid, 
                    page, 
                    size
            );
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get point history: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 포인트 적립 (관리자용 - 추후 확장)
     * POST /api/points/add
     */
    @PostMapping("/add")
    public ResponseEntity<?> addPoint(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            String channelUid = (String) request.get("channelUid");
            String pointType = (String) request.get("pointType");
            Integer pointAmount = (Integer) request.get("pointAmount");
            String description = (String) request.get("description");
            String referenceId = (String) request.get("referenceId");
            
            PointHistoryDto result = pointService.addPoint(
                    user.getUid(),
                    channelUid,
                    pointType,
                    pointAmount,
                    description,
                    referenceId
            );
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to add point: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 채널 회원 목록 및 포인트 조회 (관리자용)
     * GET /api/points/admin/members?channelUid={channelUid}&page=0&size=20
     */
    @GetMapping("/admin/members")
    public ResponseEntity<?> getChannelMembersWithPoints(
            @RequestParam String channelUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User currentUser = authenticationUtil.validateAndGetCurrentUser(userDetails);
            validateChannelAdmin(channelUid, currentUser.getUid());
            
            // 채널 회원 목록 조회 (승인된 회원만)
            Page<ChannelMember> members = channelMemberRepository
                    .findByChannelUidAndApprovalStatusAndBanned(channelUid, true, false, PageRequest.of(page, size));
            
            List<Map<String, Object>> memberList = members.getContent().stream()
                    .map(member -> {
                        Map<String, Object> memberInfo = new HashMap<>();
                        memberInfo.put("memberIdx", member.getIdx());
                        memberInfo.put("memberUid", member.getIdx()); // idx를 memberUid로 사용
                        memberInfo.put("userUid", member.getUserUid());
                        
                        // 사용자 정보 조회
                        userRepository.findById(member.getUserUid()).ifPresent(user -> {
                            memberInfo.put("userId", user.getUserId());
                            memberInfo.put("actualName", user.getActualName());
                            memberInfo.put("email", user.getEmail());
                            memberInfo.put("iconFileUid", user.getIconFileUid());
                        });
                        
                        // 해당 채널의 포인트 조회
                        Integer currentPoint = pointService.getCurrentPoint(member.getUserUid(), channelUid);
                        memberInfo.put("currentPoint", currentPoint);
                        
                        return memberInfo;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", memberList);
            result.put("currentPage", members.getNumber());
            result.put("totalPages", members.getTotalPages());
            result.put("totalElements", members.getTotalElements());
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get channel members with points: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 관리자용 포인트 지급/차감
     * POST /api/points/admin/adjust
     */
    @PostMapping("/admin/adjust")
    public ResponseEntity<?> adminAdjustPoint(
            @Valid @RequestBody AdminPointAdjustRequest request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User currentUser = authenticationUtil.validateAndGetCurrentUser(userDetails);
            validateChannelAdmin(request.getChannelUid(), currentUser.getUid());
            
            // 대상 사용자 확인
            User targetUser = userRepository.findById(request.getTargetUserUid())
                    .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다"));
            
            // 해당 채널의 회원인지 확인
            ChannelMember member = channelMemberRepository
                    .findByUserUidAndChannelUid(request.getTargetUserUid(), request.getChannelUid())
                    .orElseThrow(() -> new RuntimeException("대상 사용자가 채널 회원이 아닙니다"));
            
            if (!member.isApprovalStatus() || member.isBanned()) {
                throw new RuntimeException("승인되지 않았거나 추방된 회원입니다");
            }
            
            // 포인트 타입 결정
            String pointType = request.getPointAmount() >= 0 ? "ADMIN_ADD" : "ADMIN_DEDUCT";
            
            // 포인트 지급/차감
            PointHistoryDto result = pointService.addPoint(
                    request.getTargetUserUid(),
                    request.getChannelUid(),
                    pointType,
                    request.getPointAmount(),
                    request.getDescription(),
                    "ADMIN:" + currentUser.getUid()
            );
            
            log.info("Admin point adjustment: admin={}, target={}, amount={}, description={}", 
                    currentUser.getUid(), request.getTargetUserUid(), request.getPointAmount(), request.getDescription());
            
            // 활동 로그 기록
            Channel channel = channelRepository.findByUid(request.getChannelUid())
                    .orElse(null);
            String channelName = channel != null ? channel.getName() : "";
            
            int absAmount = Math.abs(request.getPointAmount());
            if (request.getPointAmount() >= 0) {
                // 포인트 지급
                activityLogHelper.logRPointEarned(
                        request.getTargetUserUid(),
                        targetUser.getActualName(),
                        request.getChannelUid(),
                        channelName,
                        absAmount,
                        request.getDescription()
                );
            } else {
                // 포인트 차감
                activityLogHelper.logRPointUsed(
                        request.getTargetUserUid(),
                        targetUser.getActualName(),
                        request.getChannelUid(),
                        channelName,
                        absAmount,
                        request.getDescription()
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("pointHistory", result);
            response.put("targetUserUid", request.getTargetUserUid());
            response.put("targetUserName", targetUser.getActualName());
            response.put("newBalance", result.getCurrentBalance());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to adjust point: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 특정 회원의 포인트 히스토리 조회 (관리자용)
     * GET /api/points/admin/history/{targetUserUid}?channelUid={channelUid}&page=0&size=20
     */
    @GetMapping("/admin/history/{targetUserUid}")
    public ResponseEntity<?> getTargetUserPointHistory(
            @PathVariable String targetUserUid,
            @RequestParam String channelUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User currentUser = authenticationUtil.validateAndGetCurrentUser(userDetails);
            validateChannelAdmin(channelUid, currentUser.getUid());
            
            // 대상 사용자 확인
            // User targetUser = userRepository.findById(targetUserUid)
                    // .orElseThrow(() -> new RuntimeException("대상 사용자를 찾을 수 없습니다"));
            
            Map<String, Object> result = pointService.getPointHistory(currentUser.getUid(), channelUid, page, size);
            result.put("targetUserUid", currentUser.getUid());
            result.put("targetUserName", currentUser.getActualName());
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get target user point history: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 관리자용 포인트 지급/차감 요청 DTO
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AdminPointAdjustRequest {
        @NotBlank(message = "채널 UID는 필수입니다")
        private String channelUid;
        
        @NotBlank(message = "대상 사용자 UID는 필수입니다")
        private String targetUserUid;
        
        @NotNull(message = "포인트 금액은 필수입니다")
        private Integer pointAmount;
        
        @NotBlank(message = "설명은 필수입니다")
        private String description;
    }
}
