package com.community.cms.api.level.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.ChannelLevel;

public interface ChannelLevelRepository extends JpaRepository<ChannelLevel, Integer>, QuerydslPredicateExecutor<ChannelLevel> {
    Optional<ChannelLevel> findByChannelUidAndLevel(String channelUid, int level);
    Optional<ChannelLevel> findById(Integer idx);
    List<ChannelLevel> findAll(Predicate search);
    void deleteByChannelUid(String channelUid);
}
