package com.community.cms.api.channel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Channel;

public interface ChannelRepository extends JpaRepository<Channel, String>, QuerydslPredicateExecutor<Channel> {
    List<Channel> findAll(Predicate search);
    Optional<Channel> findByUid(String uid);
    List<Channel> findByUserUid(String uid);
    List<Channel> findByChannelMemberUserUid(String uid);
    Optional<Channel> findByDomain(String domain);
}
