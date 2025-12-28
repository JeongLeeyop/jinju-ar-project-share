package com.community.cms.api.lession.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.LessionVideo;

public interface LessionVideoRepository extends JpaRepository<LessionVideo, Integer>, QuerydslPredicateExecutor<LessionVideo> {
    List<LessionVideo> findAll(Predicate search);
}
