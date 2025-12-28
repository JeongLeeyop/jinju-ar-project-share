package com.community.cms.api.lession.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.cms.entity2.LessionVideoFile;

public interface LessionVideoFileRepository extends JpaRepository<LessionVideoFile, Integer> {
    void deleteByvideoIdx(Integer idx);
}
