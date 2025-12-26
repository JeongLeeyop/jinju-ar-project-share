package com.community.cms.api.channel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.entity2.ChannelMember;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Integer>, QuerydslPredicateExecutor<ChannelMember> {
    Optional<ChannelMember> findByUserUidAndChannelUid(String userUid, String ChannelUid);

    long countByChannelUidAndUserUidInAndApprovalStatus(String channelUid, List<String> userUidList, boolean approvalStatus);
    long count(Predicate predicate);
    long countByChannelUidAndApprovalStatus(String channelUid, boolean approvalStatus);
}
