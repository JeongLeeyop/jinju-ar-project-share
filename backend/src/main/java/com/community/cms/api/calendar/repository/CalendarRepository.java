package com.community.cms.api.calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Integer>, QuerydslPredicateExecutor<Calendar> {
    List<Calendar> findAll(Predicate search);
    
    /**
     * 특정 사용자가 등록한 일정 조회 (채널별)
     */
    List<Calendar> findByWriterUidAndChannelUidOrderByStartDateDesc(String writerUid, String channelUid);
}
