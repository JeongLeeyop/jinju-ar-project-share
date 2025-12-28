package com.community.cms.api.lession.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class LessionDto {

    @Data
    public static class list {
        private String uid;
        private String name;
        private String description;
        private LocalDateTime createDate;
        private String channelUid;
        private Integer myWatchPercent;
        private List<LessionVideoDto.list> videoList = new ArrayList<LessionVideoDto.list>();
        private List<LessionFileDto.list> fileList = new ArrayList<LessionFileDto.list>();
    }

    @Data
    public static class detail {
        private String uid;
        private String name;
        private String description;
        private LocalDateTime createDate;
        private String channelUid;
        private int myWatchPercent;
        private List<LessionFileDto.detail> fileList = new ArrayList<LessionFileDto.detail>();
    }

    @Data
    public static class update {
        private String uid;
        private String name;
        private String description;
        private LocalDateTime createDate;
        private String channelUid;
        private List<LessionFileDto.update> fileList = new ArrayList<LessionFileDto.update>();
    }

    @Data
    public static class add {
        private String name;
        private String description;
        private LocalDateTime createDate;
        private String channelUid;
        private List<LessionFileDto.add> fileList = new ArrayList<LessionFileDto.add>();
    }

}
