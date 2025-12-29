package com.community.cms.api.channel.dto;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.community.cms.common.search.SearchDefault;
import com.community.cms.entity2.QChannel;

import lombok.Data;

@Data
public class ChannelSearch{
    private String startDate;
    private String endDate;
    private boolean useStatus;
    private boolean myFlag;
    private String userUid;
    private String title;
    private String categoryUid;
    private Integer minPrice;
    private Boolean privateStatus;

    private String searchType;
	private String searchValue;

    public Predicate search() {
        QChannel channel = QChannel.channel;
        // BooleanBuilder builder = super.initSearch(channel);
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            /* StringTemplate formattedStartDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Channel.startDate,
                ConstantImpl.create("%Y-%m-%d")
            );
            StringTemplate formattedEndDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Channel.endDate,
                ConstantImpl.create("%Y-%m-%d")
            ); */
            // builder.and(formattedStartDate.between(startDate, endDate).or(formattedEndDate.between(startDate, endDate)));
        }
        // if (StringUtils.hasText(title)) builder.and(Channel.title.contains(title));
        // if (StringUtils.hasText(categoryUid)) builder.and(channel.categoryList.uid.eq(categoryUid));
		
        if(minPrice != null) {
            if(minPrice == 0) builder.and(channel.price.eq(0).or(channel.price.isNull()));
            else if(minPrice > 0) builder.and(channel.price.gt(minPrice).or(channel.price.eq(minPrice)));
        }

        if(privateStatus != null) {
            builder.and(channel.privateStatus.eq(privateStatus));
        }

        if(StringUtils.hasText(searchValue)) {
            switch (searchType) {
            case "name":
                builder.and(channel.name.contains(searchValue));
                break;
            case "introduce":
                builder.and(channel.introduce.contains(searchValue));
                break;
            case "nameOrintroduce":
                builder.and(channel.name.contains(searchValue).or(channel.introduce.contains(searchValue)));
                break;
            /* default:
                BooleanExpression subquery = JPAExpressions.selectOne()
                .from(postData)
                .where(postData.post.eq(post)
                        .and(postData.inputValue.contains(searchValue)
                        .and(postData.field.uid.eq(searchType))))
                .exists();
                builder.and(subquery);
                break;
            } */
            }
        }

        int allIndex = categoryUid.indexOf("all");
        if(allIndex < 0) builder.and(channel.categoryList.any().categoryUid.in(categoryUid));

        if (myFlag) {
        //    builder.and(Channel.channelUserList.any().userUid.eq(userUid));
        }
        
        // 삭제되지 않은 채널만 보여줌
        builder.and(channel.deleteStatus.eq(false));
        
        return builder;
    }
}
