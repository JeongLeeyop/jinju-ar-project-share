package com.community.cms.api.activity.service;

import com.community.cms.api.activity.dto.ActivityDto;
import com.community.cms.api.activity.dto.ActivityListRequest;
import com.community.cms.api.activity.repository.ActivityRepository;
import com.community.cms.entity.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;

    /**
     * 채널별 활동 리스트 조회
     */
    public List<ActivityDto> getActivitiesByChannel(ActivityListRequest request) {
        log.info("Getting activities for channel: {}", request.getChannelUid());

        if (request.getPage() != null && request.getSize() != null) {
            // 페이징 처리
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<Activity> activities = activityRepository.findByChannelUidOrderByCreatedAtDesc(
                    request.getChannelUid(), pageable);
            
            return activities.getContent().stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        } else {
            // 전체 조회
            List<Activity> activities = activityRepository.findByChannelUidOrderByCreatedAtDesc(
                    request.getChannelUid());
            
            return activities.stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 공간별 활동 리스트 조회
     */
    public List<ActivityDto> getActivitiesBySpace(ActivityListRequest request) {
        log.info("Getting activities for space: {}", request.getSpaceUid());

        if (request.getPage() != null && request.getSize() != null) {
            // 페이징 처리
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<Activity> activities = activityRepository.findBySpaceUidOrderByCreatedAtDesc(
                    request.getSpaceUid(), pageable);
            
            return activities.getContent().stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        } else {
            // 전체 조회
            List<Activity> activities = activityRepository.findBySpaceUidOrderByCreatedAtDesc(
                    request.getSpaceUid());
            
            return activities.stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 사용자별 활동 리스트 조회
     */
    public List<ActivityDto> getActivitiesByUser(String userUid, ActivityListRequest request) {
        log.info("Getting activities for user: {}", userUid);

        if (request.getPage() != null && request.getSize() != null) {
            // 페이징 처리
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<Activity> activities = activityRepository.findByUserUidOrderByCreatedAtDesc(
                    userUid, pageable);
            
            return activities.getContent().stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        } else {
            // 전체 조회
            List<Activity> activities = activityRepository.findByUserUidOrderByCreatedAtDesc(userUid);
            
            return activities.stream()
                    .filter(activity -> filterByDateRange(activity, request))
                    .map(ActivityDto::from)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 날짜 범위 필터링
     */
    private boolean filterByDateRange(Activity activity, ActivityListRequest request) {
        if (request.getStartDate() == null && request.getEndDate() == null) {
            return true;
        }

        LocalDateTime createdAt = activity.getCreatedAt();
        
        if (request.getStartDate() != null) {
            LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
            if (createdAt.isBefore(startDateTime)) {
                return false;
            }
        }

        if (request.getEndDate() != null) {
            LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);
            if (createdAt.isAfter(endDateTime)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 활동 로그 저장 (헬퍼 메서드)
     */
    @Transactional
    public void logActivity(String type, String userUid, String userName, 
                           String channelUid, String description) {
        Activity activity = Activity.builder()
                .type(type)
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .description(description)
                .build();
        
        activityRepository.save(activity);
        log.info("Activity logged: type={}, user={}, channel={}", type, userName, channelUid);
    }

    /**
     * 활동 로그 저장 (공간 포함)
     */
    @Transactional
    public void logActivity(String type, String userUid, String userName, 
                           String channelUid, String spaceUid, String description) {
        Activity activity = Activity.builder()
                .type(type)
                .userUid(userUid)
                .userName(userName)
                .channelUid(channelUid)
                .spaceUid(spaceUid)
                .description(description)
                .build();
        
        activityRepository.save(activity);
        log.info("Activity logged: type={}, user={}, channel={}, space={}", 
                type, userName, channelUid, spaceUid);
    }
}
