package com.community.cms.api.board.dto.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.common.search.SearchDefault;
import com.community.cms.entity.QBoardCategory;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardCategorySearch extends SearchDefault {
    private String uid;
    private String boardUid;
    private String channelUid;
    
	public Predicate search() {
        QBoardCategory boardCategory = QBoardCategory.boardCategory;
        
        BooleanBuilder builder = super.initSearch(boardCategory);

        if(StringUtils.hasText(uid)) builder.and(boardCategory.uid.eq(uid));
        if(StringUtils.hasText(channelUid)) builder.and(boardCategory.channel.uid.eq(channelUid));
        builder.and(boardCategory.parentUid.isNull());
        builder.and(boardCategory.defaultStatus.isTrue());

        return builder;
    }
}
