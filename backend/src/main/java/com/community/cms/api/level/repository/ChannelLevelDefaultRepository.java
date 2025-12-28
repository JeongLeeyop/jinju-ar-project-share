package com.community.cms.api.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.ChannelLevelDefault;

public interface ChannelLevelDefaultRepository extends JpaRepository<ChannelLevelDefault, Integer> {
    List<ChannelLevelDefault> findAllByUseYnOrderByLevel(boolean status);
}
