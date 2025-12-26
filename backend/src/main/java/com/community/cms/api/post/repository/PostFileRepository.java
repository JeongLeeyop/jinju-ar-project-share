package com.community.cms.api.post.repository;

import com.community.cms.entity.PostFile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFileRepository extends JpaRepository<PostFile, String> {
}
