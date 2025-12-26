package com.community.cms.api.lession.dto;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.QLession;

import lombok.Data;
import com.community.cms.common.search.SearchDefault;

@Data
public class LessionSearch extends SearchDefault{
    // private String title;
    // private String content;
    // private String categoryUid;
    // private String startDate;
    // private String endDate;
    
    // private boolean useStatus;
    // private boolean flag;
    private String channelUid;
    private String userUid;

    public Predicate search() {
        QLession lession = QLession.lession;
        BooleanBuilder builder = super.initSearch(lession);

        // 날짜
        /* if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            StringTemplate formattedStartDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Lession.startDate,
                ConstantImpl.create("%Y-%m-%d")
            );
            StringTemplate formattedEndDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Lession.endDate,
                ConstantImpl.create("%Y-%m-%d")
            );
            builder.and(formattedStartDate.between(startDate, endDate).or(formattedEndDate.between(startDate, endDate)));
        } */
        
        // 타이틀
        // if (StringUtils.hasText(title)) builder.and(Lession.title.contains(title));
        if (StringUtils.hasText(channelUid)) builder.and(lession.channelUid.eq(channelUid));
        
        // 카테고리
        // if (StringUtils.hasText(categoryUid)) builder.and(lession.categoryList.uid.eq(categoryUid));
	
		// 커스텀 searchType 사용, 서브쿼리로 자식 도메인 검색
        /* if(StringUtils.hasText(searchValue)) {
            switch (searchType) {
            case "nameOrintroduce":
                builder.and(lession.name.contains(searchValue).or(lession.introduce.contains(searchValue)));
                break;
            default:
                BooleanExpression subquery = JPAExpressions.selectOne()
                .from(lession)
                .where(자식도메인).lession.eq(lession)
                        .and(자식도메인.필드1.contains(searchValue)
                        .and(자식도메인.필드2.uid.eq(searchType))))
                .exists();
                builder.and(subquery);
                break;
            }
        } */

        // 카테고리 검색 (전체 : all)
        // if(categoryUid.indexOf("all") < 0) builder.and(lession.categoryList.any().categoryUid.in(categoryUid)); 

        // 플래그
        // if (flag) {
        //    builder.and(Lession.유저목록엔티티.any().userUid.eq(userUid));
        // } 
            // builder.and(Lession.deleteStatus.eq(false));
        return builder;
    }
}
