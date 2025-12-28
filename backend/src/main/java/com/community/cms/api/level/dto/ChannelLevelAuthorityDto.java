package com.community.cms.api.level.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.community.cms.common.EnumField;
import javax.validation.constraints.NotBlank;

import com.community.cms.entity2.AuthorityType;
import com.community.cms.entity2.ChannelUseCategory;

import lombok.Data;

public class ChannelLevelAuthorityDto {

    @Data
    public static class list {
        private Integer idx;
        private Integer channelLevelIdx;
        private String authorityType;
    }

    @Data
    public static class detail {
        private Integer idx;
        private Integer channelLevelIdx;
        private String authorityType;
    }

    @Data
    public static class update {
        private Integer idx;
        private Integer channelLevelIdx;
        @NotBlank(message = "타입을 입력하세요.")
		@EnumField(enumClass = AuthorityType.class, message = "타입을 입력하세요")
        private String authorityType;
    }

    @Data
    public static class add {
        private Integer channelLevelIdx;
        @NotBlank(message = "타입을 입력하세요.")
		@EnumField(enumClass = AuthorityType.class, message = "타입을 입력하세요")
        private String authorityType;
    }
}
