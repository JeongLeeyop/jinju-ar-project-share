package com.community.cms.api.event.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class EventHistoryDto {

    @Data
    public static class list {
        private Integer idx;
        private String eventType;
        private String UserUid;
        private String channelUid;
        private Integer videoIdx;
        private String postUid;
        private String commentUid;
        private Integer likeIdx;
        private LocalDateTime createDate;
    }

    @Data
    public static class detail {
        private Integer idx;
        private String eventType;
        private String UserUid;
        private String channelUid;
        private Integer videoIdx;
        private String postUid;
        private String commentUid;
        private Integer likeIdx;
        private LocalDateTime createDate;
    }

    @Data
    public static class update {
        private Integer idx;
        private String eventType;
        private String UserUid;
        private String channelUid;
        private Integer videoIdx;
        private String postUid;
        private String commentUid;
        private Integer likeIdx;
    }

    @Data
    public static class add {
        private String eventType;
        private String userUid;
        private String channelUid;
        private Integer videoIdx;
        private String postUid;
        private String commentUid;
        private Integer likeIdx;

        public add(String eventType, String userUid, String channelUid, Integer videoIdx, String postUid, String commentUid, Integer likeIdx) {
            this.eventType = eventType;
            this.userUid = userUid;
            this.channelUid = channelUid;
            this.videoIdx = videoIdx;
            this.postUid = postUid;
            this.commentUid = commentUid;
            this.likeIdx = likeIdx;
        }
    }

    public interface userScore {
        String getEventType();
        int getCount();
    }
    
    @Data
    public static class rankingAll {
        List<calculateRanking> day;
        List<calculateRanking> week;
        List<calculateRanking> month;
        List<calculateRanking> all;
    }
    public interface calculateRanking {
        String getUserUid();
        String getActualName();
        String getLevel();
        int getPoint();
    }
}
