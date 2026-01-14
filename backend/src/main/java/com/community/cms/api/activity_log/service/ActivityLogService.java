package com.community.cms.api.activity_log.service;

import com.community.cms.entity.ActivityLog;
import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * 활동 로그 생성 (전역적으로 사용 가능, 비동기 처리)
     * 메인 트랜잭션과 독립적으로 실행되어 에러가 발생해도 메인 로직에 영향 없음
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logActivity(ActivityLogDto.CreateReq createReq) {
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .userUid(createReq.getUserUid())
                    .userName(createReq.getUserName())
                    .channelUid(createReq.getChannelUid())
                    .channelName(createReq.getChannelName())
                    .activityType(createReq.getActivityType())
                    .description(createReq.getDescription())
                    .relatedUid(createReq.getRelatedUid())
                    .relatedName(createReq.getRelatedName())
                    .targetUserUid(createReq.getTargetUserUid())
                    .targetUserName(createReq.getTargetUserName())
                    .metaData(createReq.getMetaData())
                    .build();

            activityLogRepository.save(activityLog);
            log.debug("Activity logged: {} - {}", createReq.getActivityType(), createReq.getDescription());
        } catch (Exception e) {
            log.error("Failed to log activity: {}", e.getMessage(), e);
            // 에러를 던지지 않고 로깅만 함으로써 메인 로직에 영향을 주지 않음
        }
    }

    /**
     * 동기식 활동 로그 생성 (즉시 저장이 필요한 경우)
     */
    @Transactional
    public ActivityLog logActivitySync(ActivityLogDto.CreateReq createReq) {
        try {
            ActivityLog activityLog = ActivityLog.builder()
                    .userUid(createReq.getUserUid())
                    .userName(createReq.getUserName())
                    .channelUid(createReq.getChannelUid())
                    .channelName(createReq.getChannelName())
                    .activityType(createReq.getActivityType())
                    .description(createReq.getDescription())
                    .relatedUid(createReq.getRelatedUid())
                    .relatedName(createReq.getRelatedName())
                    .targetUserUid(createReq.getTargetUserUid())
                    .targetUserName(createReq.getTargetUserName())
                    .metaData(createReq.getMetaData())
                    .build();

            return activityLogRepository.save(activityLog);
        } catch (Exception e) {
            log.error("Failed to log activity synchronously: {}", e.getMessage(), e);
            throw new RuntimeException("활동 로그 생성 실패", e);
        }
    }

    /**
     * 사용자 활동 로그 조회 (기간 필터링)
     */
    @Transactional(readOnly = true)
    public Page<ActivityLogDto.DetailResDto> getUserActivities(ActivityLogDto.ListReq listReq) {
        try {
            Pageable pageable = PageRequest.of(
                    listReq.getPage() != null ? listReq.getPage() : 0,
                    listReq.getSize() != null ? listReq.getSize() : 20,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            LocalDateTime startDate = listReq.getCalculatedStartDate();

            Page<ActivityLog> activityPage = activityLogRepository.findUserActivitiesByPeriod(
                    listReq.getUserUid(),
                    listReq.getChannelUid(),
                    startDate,
                    pageable
            );

            return activityPage.map(ActivityLogDto.DetailResDto::fromEntity);
        } catch (Exception e) {
            log.error("Failed to get user activities: {}", e.getMessage(), e);
            throw new RuntimeException("활동 로그 조회 실패", e);
        }
    }

    /**
     * 날짜별로 그룹화된 활동 로그 조회
     */
    @Transactional(readOnly = true)
    public List<ActivityLogDto.GroupedByDateResDto> getUserActivitiesGroupedByDate(
            ActivityLogDto.ListReq listReq) {
        try {
            Pageable pageable = PageRequest.of(
                    listReq.getPage() != null ? listReq.getPage() : 0,
                    listReq.getSize() != null ? listReq.getSize() : 100,
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );

            LocalDateTime startDate = listReq.getCalculatedStartDate();

            Page<ActivityLog> activityPage = activityLogRepository.findUserActivitiesByPeriod(
                    listReq.getUserUid(),
                    listReq.getChannelUid(),
                    startDate,
                    pageable
            );

            return ActivityLogDto.GroupedByDateResDto.fromActivityList(activityPage.getContent());
        } catch (Exception e) {
            log.error("Failed to get grouped activities: {}", e.getMessage(), e);
            throw new RuntimeException("활동 로그 조회 실패", e);
        }
    }

    /**
     * 채널 활동 로그 조회
     */
    @Transactional(readOnly = true)
    public Page<ActivityLogDto.DetailResDto> getChannelActivities(String channelUid, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ActivityLog> activityPage = activityLogRepository.findByChannelUidOrderByCreatedAtDesc(
                    channelUid, pageable);
            return activityPage.map(ActivityLogDto.DetailResDto::fromEntity);
        } catch (Exception e) {
            log.error("Failed to get channel activities: {}", e.getMessage(), e);
            throw new RuntimeException("채널 활동 로그 조회 실패", e);
        }
    }

    /**
     * 활동 통계 조회
     */
    @Transactional(readOnly = true)
    public ActivityLogDto.StatisticsResDto getUserActivityStatistics(
            String userUid, String channelUid, Integer months) {
        try {
            LocalDateTime startDate = months != null 
                    ? LocalDateTime.now().minusMonths(months) 
                    : LocalDateTime.now().minusMonths(1);

            Long totalActivities = activityLogRepository.countRecentActivities(
                    userUid, channelUid, startDate);

            Long postsCreated = activityLogRepository.countByActivityType(
                    userUid, channelUid, ActivityLog.ActivityType.POST_CREATED);

            Long commentsCreated = activityLogRepository.countByActivityType(
                    userUid, channelUid, ActivityLog.ActivityType.COMMENT_CREATED);

            Long likesAdded = activityLogRepository.countByActivityType(
                    userUid, channelUid, ActivityLog.ActivityType.LIKE_ADDED);

            Long eventsJoined = activityLogRepository.countByActivityType(
                    userUid, channelUid, ActivityLog.ActivityType.EVENT_JOINED);

            Long videosWatched = activityLogRepository.countByActivityType(
                    userUid, channelUid, ActivityLog.ActivityType.VIDEO_WATCHED);

            Map<String, Long> activityTypeCount = new HashMap<>();
            activityTypeCount.put("POSTS", postsCreated);
            activityTypeCount.put("COMMENTS", commentsCreated);
            activityTypeCount.put("LIKES", likesAdded);
            activityTypeCount.put("EVENTS", eventsJoined);
            activityTypeCount.put("VIDEOS", videosWatched);

            return ActivityLogDto.StatisticsResDto.builder()
                    .totalActivities(totalActivities)
                    .postsCreated(postsCreated)
                    .commentsCreated(commentsCreated)
                    .likesAdded(likesAdded)
                    .eventsJoined(eventsJoined)
                    .videosWatched(videosWatched)
                    .activityTypeCount(activityTypeCount)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get activity statistics: {}", e.getMessage(), e);
            throw new RuntimeException("활동 통계 조회 실패", e);
        }
    }

    /**
     * 오래된 활동 로그 정리 (선택적)
     * 예: 1년 이상 지난 로그 삭제
     */
    @Transactional
    public int cleanupOldActivities(int months) {
        try {
            LocalDateTime beforeDate = LocalDateTime.now().minusMonths(months);
            List<ActivityLog> oldActivities = activityLogRepository.findOldActivities(beforeDate);
            activityLogRepository.deleteAll(oldActivities);
            log.info("Cleaned up {} old activity logs", oldActivities.size());
            return oldActivities.size();
        } catch (Exception e) {
            log.error("Failed to cleanup old activities: {}", e.getMessage(), e);
            throw new RuntimeException("활동 로그 정리 실패", e);
        }
    }

    /**
     * 특정 활동 삭제
     */
    @Transactional
    public void deleteActivity(Long idx) {
        try {
            activityLogRepository.deleteById(idx);
        } catch (Exception e) {
            log.error("Failed to delete activity: {}", e.getMessage(), e);
            throw new RuntimeException("활동 로그 삭제 실패", e);
        }
    }
    
    /**
     * 사용자 활동 로그 조회 (관리자용 - 페이지네이션 지원)
     */
    @Transactional(readOnly = true)
    public Page<ActivityLogDto.DetailResDto> getUserActivityLog(String userUid, Pageable pageable) {
        try {
            Page<ActivityLog> activityPage = activityLogRepository.findByUserUidOrderByCreatedAtDesc(
                    userUid, pageable);
            return activityPage.map(ActivityLogDto.DetailResDto::fromEntity);
        } catch (Exception e) {
            log.error("Failed to get user activity log: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 활동 로그 조회 실패", e);
        }
    }
}
