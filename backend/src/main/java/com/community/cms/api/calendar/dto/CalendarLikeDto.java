package com.community.cms.api.calendar.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일정 좋아요 DTO
 */
public class CalendarLikeDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToggleResult {
        private boolean success;
        private String message;
        private boolean isLiked;
        private int likeCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeInfo {
        private Integer calendarIdx;
        private boolean isLiked;
        private int likeCount;
    }
}

