package com.community.cms.api.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.ChannelUseCategory;

public interface ChannelUseCategoryRepository extends JpaRepository<ChannelUseCategory, String> {
    void deleteByChannelUid(String ChannelUid);
}
