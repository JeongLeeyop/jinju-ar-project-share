package com.community.cms.api.calendar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CalendarDto {

    @Data
    public static class list {
        private Integer idx;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private String channelUid;
        private String location;
        private Integer points;
        private String eventType;
        private String writerUid;
        private String writerName;
        private String writerIconFileUid;
        private Integer participantCount;
        private Boolean isParticipating;
        // Like and Comment
        private Integer likeCount;
        private Boolean isLiked;
        private Integer commentCount;
    }

    @Data
    public static class fullCalendar {
        private Integer idx;
        private String title;
        private LocalDateTime start;
        private LocalDateTime end;
        private String location;
        private Integer points;
        private String eventType;
        private String writerUid;
        private String writerName;
        private Integer participantCount;
        private Boolean isParticipating;
        private Integer likeCount;
        private Boolean isLiked;
        private Integer commentCount;
        private extendedProps extendedProps;
    }

    @Data
    public static class extendedProps {
        private String description;
        private Integer idx;
        private LocalDateTime createdDate;
    }

    @Data
    public static class detail {
        private Integer idx;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private String channelUid;
        private String location;
        private Integer points;
        private String eventType;
        private String writerUid;
        private String writerName;
        private String writerIconFileUid;
        private Integer participantCount;
        private Boolean isParticipating;
        // Like and Comment
        private Integer likeCount;
        private Boolean isLiked;
        private Integer commentCount;
    }

    @Data
    public static class update {
        private Integer idx;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private String channelUid;
        private String location;
        private Integer points;
        private String eventType;
        private Integer maxParticipants;
    }

    @Data
    public static class add {
        private Integer idx;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String title;
        private String content;
        private LocalDateTime createdDate;
        private String channelUid;
        private String location;
        private Integer points;
        private String eventType;
        private Integer maxParticipants;
    }

    /**
     * 일정 참여 결과 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResult {
        private boolean success;
        private String message;
        private Integer pointsUsed; // paid 타입: 차감된 포인트
        private Integer pointsToEarn; // earn 타입: 획득 예정 포인트
        private Integer currentBalance; // 현재 포인트 잔액
    }
    
    /**
     * 내 일정 관리용 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyScheduleItem {
        private Integer idx;
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String location;
        private Integer participantCount;
        private Integer maxParticipants;
        private String status; // upcoming, ongoing, completed, cancelled
        private String eventType; // free, paid, earn
        private Integer points;
        private String hostUid;
        private String hostName;
        private LocalDateTime participatedAt; // 참여 일정에서 사용
        private LocalDateTime cancelDeadline; // 취소 가능 기한
        private Boolean canCancel; // 취소 가능 여부
    }
}
