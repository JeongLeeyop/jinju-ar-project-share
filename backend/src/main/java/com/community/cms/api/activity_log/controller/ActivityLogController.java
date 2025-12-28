package com.community.cms.api.activity_log.controller;

import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.oauth.SinghaUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /**
     * 사용자 활동 로그 조회 (페이징)
     * GET /api/activity-log/user/{userUid}?channelUid=xxx&months=1&page=0&size=20
     */
    @GetMapping("/user/{userUid}")
    public ResponseEntity<Page<ActivityLogDto.DetailResDto>> getUserActivities(
            @PathVariable String userUid,
            @AuthenticationPrincipal SinghaUser authUser,
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false, defaultValue = "1") Integer months,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

            String authUserUid = authUser.getUser().getUid();
            ActivityLogDto.ListReq listReq = ActivityLogDto.ListReq.builder()
                .userUid(authUserUid)
                .channelUid(channelUid)
                .months(months)
                .page(page)
                .size(size)
                .build();

        Page<ActivityLogDto.DetailResDto> activities = activityLogService.getUserActivities(listReq);
        return ResponseEntity.ok(activities);
    }

    /**
     * 사용자 활동 로그 조회 (날짜별 그룹화)
     * GET /api/activity-log/user/{userUid}/grouped?channelUid=xxx&months=1
     */
    @GetMapping("/user/{userUid}/grouped")
    public ResponseEntity<Map<String, Object>> getUserActivitiesGrouped(
            @PathVariable String userUid,
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false, defaultValue = "1") Integer months,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        
        ActivityLogDto.ListReq listReq = ActivityLogDto.ListReq.builder()
                .userUid(userUid)
                .channelUid(channelUid)
                .months(months)
                .page(page)
                .size(size)
                .build();

        List<ActivityLogDto.GroupedByDateResDto> groupedActivities = 
                activityLogService.getUserActivitiesGroupedByDate(listReq);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", groupedActivities);
        response.put("totalDates", groupedActivities.size());
        response.put("months", months);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 채널 활동 로그 조회
     * GET /api/activity-log/channel/{channelUid}?page=0&size=20
     */
    @GetMapping("/channel/{channelUid}")
    public ResponseEntity<Page<ActivityLogDto.DetailResDto>> getChannelActivities(
            @PathVariable String channelUid,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {
        
        Page<ActivityLogDto.DetailResDto> activities = 
                activityLogService.getChannelActivities(channelUid, page, size);
        return ResponseEntity.ok(activities);
    }

    /**
     * 사용자 활동 통계 조회
     * GET /api/activity-log/user/{userUid}/statistics?channelUid=xxx&months=1
     */
    @GetMapping("/user/{userUid}/statistics")
    public ResponseEntity<ActivityLogDto.StatisticsResDto> getUserActivityStatistics(
            @PathVariable String userUid,
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false, defaultValue = "1") Integer months) {
        
        ActivityLogDto.StatisticsResDto statistics = 
                activityLogService.getUserActivityStatistics(userUid, channelUid, months);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 활동 로그 수동 생성 (테스트 또는 관리자용)
     * POST /api/activity-log
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createActivity(
            @RequestBody ActivityLogDto.CreateReq createReq) {
        
        activityLogService.logActivitySync(createReq);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Activity log created successfully");
        response.put("description", createReq.getDescription());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 활동 로그 삭제
     * DELETE /api/activity-log/{idx}
     */
    @DeleteMapping("/{idx}")
    public ResponseEntity<Map<String, String>> deleteActivity(@PathVariable Long idx) {
        activityLogService.deleteActivity(idx);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Activity log deleted successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 오래된 활동 로그 정리 (관리자용)
     * DELETE /api/activity-log/cleanup?months=12
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldActivities(
            @RequestParam(required = false, defaultValue = "12") Integer months) {
        
        int deletedCount = activityLogService.cleanupOldActivities(months);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Old activity logs cleaned up");
        response.put("deletedCount", deletedCount);
        response.put("months", months);
        
        return ResponseEntity.ok(response);
    }
}
