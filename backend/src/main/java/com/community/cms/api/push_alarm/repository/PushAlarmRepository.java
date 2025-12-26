package com.community.cms.api.push_alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.community.cms.entity.PushAlarm;

public interface PushAlarmRepository extends JpaRepository<PushAlarm, Integer>, QuerydslPredicateExecutor<PushAlarm> {
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM push_alarm pa WHERE pa.user_uid = ?1 AND pa.read_status = 0")
    int findNewCntByUserUid(String userUid);
    
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE push_alarm pa SET pa.read_status = 1 WHERE pa.user_uid = ?1 AND pa.read_status = 0")
    int readAll(String userUid);
}
