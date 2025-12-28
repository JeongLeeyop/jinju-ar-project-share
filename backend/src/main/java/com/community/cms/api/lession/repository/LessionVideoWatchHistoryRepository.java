package com.community.cms.api.lession.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.LessionVideoWatchHistory;

public interface LessionVideoWatchHistoryRepository extends JpaRepository<LessionVideoWatchHistory, Integer> {
    Optional<LessionVideoWatchHistory> findByVideoIdxAndUserUid(Integer videoIdx, String UserUid);
}
