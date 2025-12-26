package com.community.cms.api.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.ChannelLevelSettingDefault;

public interface ChannelLevelSettingDefaultRepository extends JpaRepository<ChannelLevelSettingDefault, Integer> {
    List<ChannelLevelSettingDefault> findAllByUseYnAndLevel(boolean status, int level);
}
