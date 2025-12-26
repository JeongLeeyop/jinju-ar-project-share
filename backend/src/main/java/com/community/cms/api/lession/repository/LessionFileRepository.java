package com.community.cms.api.lession.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.community.cms.entity2.LessionFile;

public interface LessionFileRepository extends JpaRepository<LessionFile, Integer>, QuerydslPredicateExecutor<LessionFile> {
    void deleteByLessionUid(String uid);
}
