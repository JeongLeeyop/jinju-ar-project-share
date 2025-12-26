package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class ChannelQuestionDto {

    @Data
    public static class list {
        private Integer idx; // 아이디
        private String title; // 챌린지명
        private LocalDateTime createDate; // 생성일
    }

    @Data
    public static class detail {
        private Integer idx; // 아이디
        private String title; // 챌린지명
        private LocalDateTime createDate; // 생성일
    }

    @Data
    public static class update {
        private Integer idx; // 아이디
        private String title; // 챌린지명
    }

    @Data
    public static class add {
        private String title; // 챌린지명
    }

}
