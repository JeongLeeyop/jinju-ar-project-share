package com.community.cms.api.channel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.Channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChannelRepository extends JpaRepository<Channel, String>, QuerydslPredicateExecutor<Channel> {
    List<Channel> findAll(Predicate search);
    Optional<Channel> findByUid(String uid);
    List<Channel> findByUserUid(String uid);
    List<Channel> findByChannelMemberUserUid(String uid);
    Optional<Channel> findByDomain(String domain);
    
    // 사용자가 생성한 채널 개수
    long countByUserUid(String userUid);
    long countByUserUidAndDeleteStatusFalse(String userUid);
    
    // 관리자용: deleteStatus=false인 채널만 조회
    Page<Channel> findByDeleteStatusFalse(Pageable pageable);
}
