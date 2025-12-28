package com.community.cms.api.calendar.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 일정 댓글 DTO
 */
public class CalendarCommentDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        private Integer calendarIdx;
        private String content;
        private Long parentIdx;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReq {
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListItem {
        private Long idx;
        private Integer calendarIdx;
        private String userUid;
        private String userName;
        private String userProfileImage;
        private String content;
        private Long parentIdx;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean isOwner;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private List<ListItem> comments;
        private int totalCount;
    }
}

