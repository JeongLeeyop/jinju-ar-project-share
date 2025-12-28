package com.community.cms.api.post.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity.PostLike;

public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {

    Optional<PostLike> findByPostUidAndUserUid(String postUid, String userUid);
    
}
