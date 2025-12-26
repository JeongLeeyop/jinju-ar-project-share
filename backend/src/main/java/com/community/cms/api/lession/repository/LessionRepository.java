package com.community.cms.api.lession.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Lession;

public interface LessionRepository extends JpaRepository<Lession, String>, QuerydslPredicateExecutor<Lession> {
    List<Lession> findAll(Predicate search);

    @Query(value="SELECT AVG(lvwh.percent) FROM lession l LEFT JOIN lession_video lv ON l.uid = lv.lession_uid LEFT JOIN lession_video_watch_history lvwh ON lv.idx = lvwh.video_idx WHERE l.uid = ?1 AND lvwh.user_uid = ?2 GROUP BY lession_uid", nativeQuery=true)
    Integer findMyPercentage(String lessionUid, String userUid);
}
