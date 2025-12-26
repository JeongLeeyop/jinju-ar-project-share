package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;

import lombok.Data;

public class ChannelMemberQuestionDto {

    @Data
    public static class list {
        private Integer idx; // 아이디
        private String answer; // 
        private LocalDateTime createDate; // 생성일
        private ChannelQuestionDto.list channelQuestion;
    }

    @Data
    public static class detail {
        private Integer idx; // 아이디
        private String answer; // 
        private ChannelQuestionDto.list channelQuestion;
        private LocalDateTime createDate; // 생성일
    }

    @Data
    public static class update {
        private Integer idx; // 아이디
        private String answer; // 
        private Integer channelQuestionIdx;
    }

    @Data
    public static class add {
        private String answer; // 
        private Integer channelQuestionIdx;
    }

}
