package com.community.cms.api.activity_log.dto;

import com.community.cms.entity.ActivityLog;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityLogDto {

    /**
     * 활동 로그 생성 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateReq {
        private String userUid;
        private String userName;
        private String channelUid;
        private String channelName;
        private ActivityLog.ActivityType activityType;
        private String description;
        private String relatedUid;
        private String relatedName;
        private String targetUserUid;
        private String targetUserName;
        private String metaData;

        /**
         * 빠른 생성을 위한 정적 팩토리 메서드들
         */
        public static CreateReq of(String userUid, String userName, String channelUid, 
                                   ActivityLog.ActivityType activityType, String relatedName) {
            String description = ActivityLog.generateDescription(activityType, null, relatedName);
            return CreateReq.builder()
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(channelUid)
                    .activityType(activityType)
                    .description(description)
                    .relatedName(relatedName)
                    .build();
        }

        public static CreateReq ofWithTarget(String userUid, String userName, String channelUid,
                                             ActivityLog.ActivityType activityType,
                                             String targetUserName, String relatedName) {
            String description = ActivityLog.generateDescription(activityType, targetUserName, relatedName);
            return CreateReq.builder()
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(channelUid)
                    .activityType(activityType)
                    .description(description)
                    .targetUserName(targetUserName)
                    .relatedName(relatedName)
                    .build();
        }
    }

    /**
     * 활동 로그 상세 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResDto {
        private Long idx;
        private String userUid;
        private String userName;
        private String channelUid;
        private String channelName;
        private ActivityLog.ActivityType activityType;
        private String activityTypeName;
        private String description;
        private String relatedUid;
        private String relatedName;
        private String targetUserUid;
        private String targetUserName;
        private String metaData;
        private LocalDateTime createdAt;
        private String createdAtFormatted;

        public static DetailResDto fromEntity(ActivityLog activityLog) {
            return DetailResDto.builder()
                    .idx(activityLog.getIdx())
                    .userUid(activityLog.getUserUid())
                    .userName(activityLog.getUserName())
                    .channelUid(activityLog.getChannelUid())
                    .channelName(activityLog.getChannelName())
                    .activityType(activityLog.getActivityType())
                    .activityTypeName(activityLog.getActivityType().getDefaultMessage())
                    .description(activityLog.getDescription())
                    .relatedUid(activityLog.getRelatedUid())
                    .relatedName(activityLog.getRelatedName())
                    .targetUserUid(activityLog.getTargetUserUid())
                    .targetUserName(activityLog.getTargetUserName())
                    .metaData(activityLog.getMetaData())
                    .createdAt(activityLog.getCreatedAt())
                    .createdAtFormatted(activityLog.getCreatedAt().format(
                            DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                    .build();
        }
    }

    /**
     * 활동 로그 목록 조회 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListReq {
        private String userUid;
        private String channelUid;
        private Integer months; // 1, 3, 6 개월
        private ActivityLog.ActivityType activityType;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer page;
        private Integer size;

        public LocalDateTime getCalculatedStartDate() {
            if (startDate != null) {
                return startDate;
            }
            if (months != null) {
                return LocalDateTime.now().minusMonths(months);
            }
            // 기본값: 1개월
            return LocalDateTime.now().minusMonths(1);
        }
    }

    /**
     * 날짜별로 그룹화된 활동 로그 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GroupedByDateResDto {
        private String date; // yyyy.MM.dd 형식
        private List<DetailResDto> activities;

        public static List<GroupedByDateResDto> fromActivityList(List<ActivityLog> activityLogs) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            
            Map<String, List<ActivityLog>> groupedMap = activityLogs.stream()
                    .collect(Collectors.groupingBy(
                            log -> log.getCreatedAt().format(formatter)
                    ));

            return groupedMap.entrySet().stream()
                    .map(entry -> GroupedByDateResDto.builder()
                            .date(entry.getKey())
                            .activities(entry.getValue().stream()
                                    .map(DetailResDto::fromEntity)
                                    .collect(Collectors.toList()))
                            .build())
                    .sorted((a, b) -> b.getDate().compareTo(a.getDate())) // 날짜 내림차순
                    .collect(Collectors.toList());
        }
    }

    /**
     * 활동 통계 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatisticsResDto {
        private Long totalActivities;
        private Long postsCreated;
        private Long commentsCreated;
        private Long likesAdded;
        private Long eventsJoined;
        private Long videosWatched;
        private Map<String, Long> activityTypeCount;
    }
}
