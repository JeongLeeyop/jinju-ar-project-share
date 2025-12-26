package com.community.cms.api.lession.dto;

import java.time.LocalDateTime;

import lombok.Data;

public class LessionVideoTimeLineDto {

    @Data
    public static class list {
        private Integer idx;
        private String time;
        private String description;
        private int videoIdx;
        private Integer viewOrder;
        private LocalDateTime createDate;
    }

    @Data
    public static class detail {
        private Integer idx;
        private String time;
        private String description;
        private int videoIdx;
        private Integer viewOrder;
        private LocalDateTime createDate;
    }

    @Data
    public static class update {
        private Integer idx;
        private String time;
        private String description;
        private int videoIdx;
        private Integer viewOrder;
        private LocalDateTime createDate;
    }

    @Data
    public static class add {
        private Integer idx;
        private String time;
        private String description;
        private int videoIdx;
        private Integer viewOrder;
        private LocalDateTime createDate;
    }
}
