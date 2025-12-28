package com.community.cms.api.level.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.community.cms.common.EnumField;
import com.community.cms.entity2.ChannelUseCategory;
import com.community.cms.entity2.EventType;

import lombok.Data;

public class ChannelLevelSettingDto {

    @Data
    public static class list {
        private Integer idx;
        private Integer channelLevelIdx;
        private String eventType;
        private int goal;
    }

    @Data
    public static class detail {
        private Integer idx;
        private Integer channelLevelIdx;
        private String eventType;
        private int goal;
    }

    @Data
    public static class update {
        private Integer idx;
        private Integer channelLevelIdx;
        
        @NotBlank(message = "타입을 입력하세요.")
		@EnumField(enumClass = EventType.class, message = "타입을 입력하세요")
        private String eventType;
        private int goal;
    }

    @Data
    public static class add {
        private Integer channelLevelIdx;
        @NotBlank(message = "타입을 입력하세요.")
		@EnumField(enumClass = EventType.class, message = "타입을 입력하세요")
        private String eventType;
        private int goal;
    }
}
