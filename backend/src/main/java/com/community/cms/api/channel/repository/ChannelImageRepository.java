package com.community.cms.api.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.ChannelImage;
public interface ChannelImageRepository extends JpaRepository<ChannelImage, Integer> {
    void deleteByChannelUid(String channelUid);
}
