package com.community.cms.api.attached_file.dto;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import lombok.Data;

public class AttachedFileDto {
    @Data
    public static class detail {
        private String uid;
        private String originalName;
        private long fileSize;
        private String fileType;
        private String url;
        private LocalDateTime createDate;
    }

    @Data
    public static class clientDetail {
        private String uid;
        private String originalName;
        private long fileSize;
        private String fileType;
    }
    
    @Data
    public static class simple {
        private String uid;
        private String name;
        public void setOriginalName(String name) {
            this.name = name;
        }
    }
}
