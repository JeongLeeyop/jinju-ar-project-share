package com.community.cms.api.new_alarm.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.community.cms.entity.NewAlarm;

public interface NewAlarmRepository extends JpaRepository<NewAlarm, Long>, QuerydslPredicateExecutor<NewAlarm> {
    
    @Modifying
    @Query(value = "Update FROM NewAlarm set checkStatus = 1 where userUid = ?1 and postUid = ?2")
    void check(String userUid, String postUid);

    @Query(value = "Select count(*) FROM NewAlarm na where na.userUid = ?1 and na.type = 1 and na.checkStatus != 1")
    int count(String userUid);

    @Modifying
    @Transactional
    @Query(value = "Delete from new_alarm na where na.create_date <= date_sub(now(), interval 1 week)", nativeQuery = true)
    int deleteOldAlarm();
}
