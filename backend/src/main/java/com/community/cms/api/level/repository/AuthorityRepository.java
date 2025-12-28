package com.community.cms.api.level.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.community.cms.entity2.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM authority WHERE use_status = 1")
    List<Authority> findAll();
}
