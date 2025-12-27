package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class ChannelUseCategoryDto {

    @Data
    public static class list {
        private String uid; // 아이디
        private String categoryUid;
        private String channelUid;
        // private int viewOrder; //순서
    }

    @Data
    public static class detail { // 영양상태 정보
        // private String uid; // 아이디
        private String uid;
        private String categoryUid;
        private String channelUid;
        private ChannelCategoryDto.detail category; // 카테고리 정보
        // private int viewOrder; //순서
        // public void getCategoryUid(String categoryUid) {
        // this.uid = categoryUid;
        // }
    }

    @Data
    public static class update {
        // private String uid; // 아이디
        private String categoryUid;
        // private String channelUid;
        // private int viewOrder; //순서
    }

    @Data
    public static class add {
        // private String uid; // 아이디
        private String categoryUid;
        private String channelUid;
        // private int viewOrder; //순서
    }

}
