package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.community.cms.api.level.dto.ChannelLevelDto;
import com.community.cms.api.user.dto.UserDto;

import lombok.Data;

public class ChannelMemberDto {

    @Data
    public static class list {
        private Integer idx;
        private String userUid;
        private String channelUid;
        private String introduce;
        private ChannelLevelDto.list channelLevel;
        private List<ChannelMemberQuestionDto.list> questionList;
        private LocalDateTime createDate;
    }

    @Data
    public static class detail {
        private Integer idx;
        private String userUid;
        private String channelUid;
        private String introduce;
        private boolean banned;
        private ChannelLevelDto.detail channelLevel;
        private List<ChannelMemberQuestionDto.detail> questionList;
        private LocalDateTime createDate;
        private UserDto.Detail user;
        private boolean isOnline;
    }

    @Data
    public static class update {
        private Integer idx;
        private String userUid;
        private String channelUid;
        private ChannelLevelDto.update channelLevel;
        private LocalDateTime createDate;
    }

    @Data
    public static class add {
        private Integer idx;
        private String userUid;
        private String channelUid;
        private String introduce;
        private List<ChannelMemberQuestionDto.add> questionList;
        private ChannelLevelDto.add channelLevel;
        private LocalDateTime createDate;
    }

    @Data
    public static class count {
        private long totalCount;
        private long onlineCount;
        private long holdingCount;
    }

}
