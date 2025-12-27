package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.community.cms.entity2.ChannelUseCategory;

import lombok.Data;

public class ChannelDto {

    @Data
    public static class list {
        private String uid;
        private String name;
        private String introduce;
        private boolean privateStatus;
        private Integer price;
        private String domain;
        private String userUid;
        private String password;
        private List<ChannelUseCategoryDto.list> categoryList = new ArrayList<ChannelUseCategoryDto.list>();
        private List<ChannelImageDto.detail> iconImageList = new ArrayList<ChannelImageDto.detail>();
        private List<ChannelImageDto.detail> coverImageList = new ArrayList<ChannelImageDto.detail>();
    }

    @Data
    public static class myList {
        List<ChannelDto.list> myList = new ArrayList<ChannelDto.list>();
        List<ChannelDto.list> joinList = new ArrayList<ChannelDto.list>();
    }

    @Data
    public static class detail { // 영양상태 정보
        private String uid;
        private String name;
        private String introduce;
        private boolean privateStatus;
        private Integer price;
        private String domain;
        private String userUid;
        private String creatorName; // 개설자 이름
        private String categoryName; // 카테고리 이름 (첫 번째 카테고리)
        private int memberCount;
        private Long onlineCount;
        private Boolean myJoinStatus;
        private Boolean myApprovalStatus;;
        private Boolean myChannelStatus;
        private Boolean myTodayVisit;
        private String password;
        private List<ChannelUseCategoryDto.detail> categoryList = new ArrayList<ChannelUseCategoryDto.detail>();
        private List<ChannelImageDto.detail> iconImageList = new ArrayList<ChannelImageDto.detail>();
        private List<ChannelImageDto.detail> coverImageList = new ArrayList<ChannelImageDto.detail>();
        private List<ChannelQuestionDto.detail> questionList = new ArrayList<ChannelQuestionDto.detail>();
    }

    @Data
    public static class update {
        private String uid; // 아이디
        private String name;
        private String introduce;
        private boolean privateStatus;
        private Integer price;
        // private String domain;
        private String userUid;
        private String password;

        private boolean questionChangeFlag;

        private LocalDateTime createDate; // 생성일
        private List<ChannelUseCategoryDto.update> categoryList = new ArrayList<ChannelUseCategoryDto.update>();
        private List<ChannelImageDto.update> iconImageList = new ArrayList<ChannelImageDto.update>();
        private List<ChannelImageDto.update> coverImageList = new ArrayList<ChannelImageDto.update>();
        private List<ChannelQuestionDto.update> questionList = new ArrayList<ChannelQuestionDto.update>();
    }

    @Data
    public static class add {
        private String uid;
        private String name;
        private String introduce;
        private boolean privateStatus;
        private Integer price;
        private String domain;
        private String userUid;
        private String password;

        private LocalDateTime createDate; // 생성일
        private List<ChannelUseCategoryDto.add> categoryList = new ArrayList<ChannelUseCategoryDto.add>();
        private List<ChannelImageDto.add> iconImageList = new ArrayList<ChannelImageDto.add>();
        private List<ChannelImageDto.add> coverImageList = new ArrayList<ChannelImageDto.add>();
        private List<ChannelQuestionDto.add> questionList = new ArrayList<ChannelQuestionDto.add>();
    }

}
