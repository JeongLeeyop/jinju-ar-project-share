package com.community.cms.api.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.ChannelQuestion;

public interface ChannelQuestionRepository extends JpaRepository<ChannelQuestion, Integer> {
    void deleteByChannelUid(String channelUid);
}
