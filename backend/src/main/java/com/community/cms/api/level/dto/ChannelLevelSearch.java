package com.community.cms.api.level.dto;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.QChannelLevel;

import lombok.Data;

@Data
public class ChannelLevelSearch{
    private String title;
    private String userUid;
    private String channelUid;

    private String searchType;
	private String searchValue;

    public Predicate search() {
        QChannelLevel channelLevel = QChannelLevel.channelLevel;
        BooleanBuilder builder = new BooleanBuilder();
		
        if(StringUtils.hasText(channelUid)) {
            builder.and(channelLevel.channelUid.eq(channelUid));
        }
        if(StringUtils.hasText(searchValue)) {
            switch (searchType) {
            case "name":
                builder.and(channelLevel.name.contains(searchValue));
                break;
            }
        }
        return builder;
    }
}