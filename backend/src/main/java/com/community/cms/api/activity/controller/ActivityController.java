package com.community.cms.api.activity.controller;

import com.community.cms.api.activity.dto.ActivityDto;
import com.community.cms.api.activity.dto.ActivityListRequest;
import com.community.cms.api.activity.service.ActivityService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity Controller
 * 활동 리스트 관련 REST API
 */
@Slf4j
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 검증 및 반환
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
     * 채널별 활동 리스트 조회
     * GET /api/activities/channel/{channelUid}
     */
    @GetMapping("/channel/{channelUid}")
    public ResponseEntity<?> getActivitiesByChannel(
            @PathVariable String channelUid,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for channel: {} by user: {}", channelUid, user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .channelUid(channelUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            List<ActivityDto> activities = activityService.getActivitiesByChannel(request);
            return ResponseEntity.ok(activities);
        } catch (RuntimeException e) {
            log.error("Failed to get activities: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 공간별 활동 리스트 조회
     * GET /api/activities/space/{spaceUid}
     */
    @GetMapping("/space/{spaceUid}")
    public ResponseEntity<?> getActivitiesBySpace(
            @PathVariable String spaceUid,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for space: {} by user: {}", spaceUid, user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .spaceUid(spaceUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            List<ActivityDto> activities = activityService.getActivitiesBySpace(request);
            return ResponseEntity.ok(activities);
        } catch (RuntimeException e) {
            log.error("Failed to get activities: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 사용자별 활동 리스트 조회
     * GET /api/activities/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyActivities(
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for user: {}", user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .channelUid(channelUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            List<ActivityDto> activities = activityService.getActivitiesByUser(user.getUid(), request);
            return ResponseEntity.ok(activities);
        } catch (RuntimeException e) {
            log.error("Failed to get activities: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
