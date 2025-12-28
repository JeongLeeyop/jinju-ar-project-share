package com.community.cms.api.channel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Channel;

public interface AdmChannelRepository extends JpaRepository<Channel, String>, QuerydslPredicateExecutor<Channel> {
    List<Channel> findAll(Predicate search);
}
