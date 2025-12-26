package com.community.cms.api.post.repository;

import com.community.cms.entity.PostData;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostDataRepository extends JpaRepository<PostData, String>{
}
