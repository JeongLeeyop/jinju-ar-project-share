package com.community.cms.api.calendar.dto;

import java.time.LocalDateTime;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.QCalendar;

import lombok.Data;

@Data
public class CalendarSearch{
    private String startDate;
    private String endDate;
    private String title;
    private String channelUid;

    private String searchType;
	private String searchValue;

    public Predicate search() {
        QCalendar calendar = QCalendar.calendar;
        // BooleanBuilder builder = super.initSearch(calendar);
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(channelUid)){
           builder.and(calendar.channelUid.eq(channelUid));
        }
        
        // 3일 이상 지난 일정 필터링 (startDate 기준)
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        builder.and(calendar.startDate.goe(threeDaysAgo));
        
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            /* StringTemplate formattedStartDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Calendar.startDate,
                ConstantImpl.create("%Y-%m-%d")
            );
            StringTemplate formattedEndDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                Calendar.endDate,
                ConstantImpl.create("%Y-%m-%d")
            ); */
            // builder.and(formattedStartDate.between(startDate, endDate).or(formattedEndDate.between(startDate, endDate)));
        }
        // if (StringUtils.hasText(title)) builder.and(Calendar.title.contains(title));
        // if (StringUtils.hasText(categoryUid)) builder.and(calendar.categoryList.uid.eq(categoryUid));
	
		
        if(StringUtils.hasText(searchValue)) {
            switch (searchType) {
            /* case "name":
                builder.and(calendar.name.contains(searchValue));
                break;
            } */
            }
        }
        return builder;
    }
}
