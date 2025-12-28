package com.community.cms.api.lession.dto;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.common.search.SearchDefault;
import com.community.cms.entity2.QLessionVideo;

import lombok.Data;

@Data
public class LessionVideoSearch extends SearchDefault{
    private String lessionUid;
    private String channelUid;
    private String userUid;

    public Predicate search() {
        QLessionVideo lessionVideo = QLessionVideo.lessionVideo;
        BooleanBuilder builder = super.initSearch(lessionVideo);

        // 타이틀
        // if (StringUtils.hasText(title)) builder.and(LessionVideo.title.contains(title));
        if (StringUtils.hasText(lessionUid)) builder.and(lessionVideo.lession.uid.eq(lessionUid));
        if (StringUtils.hasText(channelUid)) builder.and(lessionVideo.lession.channelUid.eq(channelUid));
        
        return builder;
    }
}
