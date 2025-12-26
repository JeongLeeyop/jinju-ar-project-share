package com.community.cms.api.lession.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class LessionVideoDto {

    @Data
    public static class list {
        private Integer idx;
        private String lessionUid;
        private LocalDateTime createDate;
        private Integer viewOrder;
        private String title;
        private String description;
        private String content;
        private String urlCode;
        private int viewCount;
        private int myWatchPercent;
        private int lastWatchSecond;
        private LocalDateTime lastWatchUpdate;
        private List<LessionVideoFileDto.list> fileList;
        private List<LessionVideoTimeLineDto.list> timeLineList;
    }

    @Data
    public static class detail {
        private Integer idx;
        private String lessionUid;
        private LocalDateTime createDate;
        private Integer viewOrder;
        private String title;
        private String description;
        private String content;
        private String urlCode;
        private int viewCount;
        private int myWatchPercent;
        private int lastWatchSecond;
        private LocalDateTime lastWatchUpdate;
        private List<LessionVideoFileDto.detail> fileList;
        private List<LessionVideoTimeLineDto.list> timeLineList;
    }

    @Data
    public static class update {
        private Integer idx;
        private String lessionUid;
        private LocalDateTime createDate;
        private Integer viewOrder;
        private String title;
        private String description;
        private String content;
        private String urlCode;
        private int viewCount;
        private List<LessionVideoFileDto.list> fileList;
        private List<LessionVideoTimeLineDto.list> timeLineList;
    }

    @Data
    public static class add {
        private Integer idx;
        private String lessionUid;
        private LocalDateTime createDate;
        private Integer viewOrder;
        private String title;
        private String description;
        private String content;
        private String urlCode;
        private int viewCount;
        private List<LessionVideoFileDto.list> fileList;
        private List<LessionVideoTimeLineDto.list> timeLineList;
    }

    @Data
    public static class watchHistory {
        private int videoIdx;
        private int percent;
        private int lastWatchSecond;
        private String channelUid;
    }
}
