package com.community.cms.api.point.controller;

import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.service.PointService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 현재 인증된 사용자 조회 및 검증
     */
    private User validateAndGetCurrentUser(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            log.error("Unauthenticated access attempt detected");
            throw new RuntimeException("인증되지 않은 접근입니다");
        }
        return userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    /**
     * 현재 포인트 조회
     * GET /api/points/current?channelUid={channelUid}
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPoint(
            @RequestParam String channelUid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
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
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
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
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
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
}
