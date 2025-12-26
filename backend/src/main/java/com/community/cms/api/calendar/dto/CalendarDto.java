package com.community.cms.api.calendar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

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
    }

    @Data
    public static class fullCalendar {
        private String title;
        private LocalDateTime start;
        private LocalDateTime end;
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
    }

}
