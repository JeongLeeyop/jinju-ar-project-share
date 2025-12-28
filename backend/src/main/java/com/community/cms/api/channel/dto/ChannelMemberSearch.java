package com.community.cms.api.channel.dto;

import java.util.List;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.QChannelMember;

import lombok.Data;

@Data
public class ChannelMemberSearch {

    private String userUid;
    private String channelUid;

    private Boolean isOnline;
    private Boolean isHolding;
    private Boolean excludeBanned; // 추방된 회원 제외 여부
    private String[] userUidList;

    private String searchType;
    private String searchValue;

    public Predicate search() {
        QChannelMember channelMember = QChannelMember.channelMember;
        // BooleanBuilder builder = super.initSearch(channel);
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(channelUid)) {
            builder.and(channelMember.channelUid.eq(channelUid));
        }
        if (isOnline != null && isOnline) {
            builder.and(channelMember.userUid.in(userUidList));
        }
        if (isHolding != null && isHolding) {
            builder.and(channelMember.approvalStatus.eq(false));
        } else if (isHolding != null && !isHolding) {
            builder.and(channelMember.approvalStatus.eq(true));
        }
        
        // 추방된 회원 제외
        if (excludeBanned != null && excludeBanned) {
            builder.and(channelMember.banned.eq(false));
        }
        /*
         * if(StringUtils.hasText(searchValue)) {
         * switch (searchType) {
         * case "name":
         * builder.and(channel.name.contains(searchValue));
         * break;
         * case "introduce":
         * builder.and(channel.introduce.contains(searchValue));
         * break;
         * case "nameOrintroduce":
         * builder.and(channel.name.contains(searchValue).or(channel.introduce.contains(
         * searchValue)));
         * break;
         * default:
         * BooleanExpression subquery = JPAExpressions.selectOne()
         * .from(postData)
         * .where(postData.post.eq(post)
         * .and(postData.inputValue.contains(searchValue)
         * .and(postData.field.uid.eq(searchType))))
         * .exists();
         * builder.and(subquery);
         * break;
         * }
         * }
         * }
         */

        return builder;
    }
}
