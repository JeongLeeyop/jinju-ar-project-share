package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;

import lombok.Data;

public class ChannelUserDto {

    @Data
    public static class list {
    private String uid;
	private String channelUid; // 챌린지 아이디
	private String userUid; // 카테고리 아이디
    private LocalDateTime startDate;
	private LocalDateTime dueDate; // 종료일
    }

    @Data
    public static class detail { // 영양상태 정보
       private String uid;
	private String channelUid; // 챌린지 아이디
	private String userUid; // 카테고리 아이디
    private LocalDateTime startDate;
	private LocalDateTime dueDate; // 종료일
    }

    @Data
    public static class update {
        private String uid;
	private String channelUid; // 챌린지 아이디
	private String userUid; // 카테고리 아이디
    private LocalDateTime startDate;
	private LocalDateTime dueDate; // 종료일
    }

    @Data
    public static class add {
	private String channelUid; // 챌린지 아이디
	private String userUid; // 카테고리 아이디
	private LocalDateTime dueDate; // 종료일
    }
}
