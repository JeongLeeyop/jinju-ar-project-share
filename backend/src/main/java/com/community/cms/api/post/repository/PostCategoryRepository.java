package com.community.cms.api.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.community.cms.entity.PostCategory;

public interface PostCategoryRepository extends JpaRepository<PostCategory, String>{
@Query("SELECT pc FROM PostCategory pc WHERE pc.categoryUid IN :uidList")
List<PostCategory> findAllByCategoryUidIn(@Param("uidList") List<String> uidList);
    
    List<PostCategory> findByCategoryUid(String uid);
}
