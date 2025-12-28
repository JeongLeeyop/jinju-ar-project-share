package com.community.cms.api.calendar.dto;

import java.time.LocalDateTime;

import com.community.cms.entity2.ScheduleParticipant.ParticipantStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일정 참여자 DTO
 */
public class ScheduleParticipantDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private Integer idx;
        private Integer calendarIdx;
        private String userUid;
        private String userName;
        private String userPhone;
        private String userProfileImage;
        private String channelUid;
        private Integer pointsAmount;
        private ParticipantStatus status;
        private Boolean pointGranted;
        private LocalDateTime pointGrantedAt;
        private String pointGrantedBy;
        private LocalDateTime createdAt;
        private LocalDateTime cancelledAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListItem {
        private Integer idx;
        private String userUid;
        private String userName;
        private String userPhone;
        private String userProfileImage;
        private ParticipantStatus status;
        private Boolean pointGranted;
        private LocalDateTime createdAt;
    }
}
