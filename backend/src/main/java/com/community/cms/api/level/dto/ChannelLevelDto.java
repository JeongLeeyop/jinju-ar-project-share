package com.community.cms.api.level.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.community.cms.entity2.ChannelUseCategory;

import lombok.Data;

public class ChannelLevelDto {

    @Data
    public static class list {
        private Integer idx;
        private String channelUid;
        private int level;
        private String name;
        private String description;
        private String icon;
        private List<ChannelLevelSettingDto.list> levelSettingList; 
        private List<ChannelLevelAuthorityDto.list> levelAuthorityList; 
    }

    @Data
    public static class detail { // 영양상태 정보
        private Integer idx;
        private String channelUid;
        private int level;
        private String name;
        private String description;
        private String icon;
        private List<ChannelLevelSettingDto.list> levelSettingList; 
        private List<ChannelLevelAuthorityDto.list> levelAuthorityList; 
    }

    @Data
    public static class update {
        private Integer idx;
        private String channelUid;
        private int level;
        private String name;
        private String description;
        private String icon;
        private List<ChannelLevelSettingDto.list> levelSettingList; 
        private List<ChannelLevelAuthorityDto.list> levelAuthorityList; 
    }

    @Data
    public static class add {
        private String channelUid;
        private int level;
        private String name;
        private String description;
        private String icon;
        private List<ChannelLevelSettingDto.add> levelSettingList; 
        private List<ChannelLevelAuthorityDto.add> levelAuthorityList; 
    }
}
