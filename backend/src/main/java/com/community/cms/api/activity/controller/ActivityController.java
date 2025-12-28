package com.community.cms.api.activity.controller;

import com.community.cms.api.activity.dto.ActivityDto;
import com.community.cms.api.activity.dto.ActivityListRequest;
import com.community.cms.api.activity.service.ActivityService;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;
import com.community.cms.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private final AuthenticationUtil authenticationUtil;

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
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for channel: {} by user: {}", channelUid, user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .channelUid(channelUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            Page<ActivityDto> activities = activityService.getActivitiesByChannel(request);
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
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for space: {} by user: {}", spaceUid, user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .spaceUid(spaceUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            Page<ActivityDto> activities = activityService.getActivitiesBySpace(request);
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
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            log.info("Getting activities for user: {}", user.getActualName());

            ActivityListRequest request = ActivityListRequest.builder()
                    .channelUid(channelUid)
                    .startDate(startDate != null ? java.time.LocalDate.parse(startDate) : null)
                    .endDate(endDate != null ? java.time.LocalDate.parse(endDate) : null)
                    .page(page)
                    .size(size)
                    .build();

            Page<ActivityDto> activities = activityService.getActivitiesByUser(user.getUid(), request);
            return ResponseEntity.ok(activities);
        } catch (RuntimeException e) {
            log.error("Failed to get activities: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
