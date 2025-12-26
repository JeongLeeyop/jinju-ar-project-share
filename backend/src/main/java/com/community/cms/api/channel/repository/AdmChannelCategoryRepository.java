package com.community.cms.api.channel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.community.cms.entity2.ChannelCategory;

public interface AdmChannelCategoryRepository extends JpaRepository<ChannelCategory, String>, QuerydslPredicateExecutor<ChannelCategory>{
}