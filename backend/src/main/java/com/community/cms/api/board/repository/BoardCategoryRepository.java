package com.community.cms.api.board.repository;

import java.util.List;

import com.community.cms.api.board.dto.search.BoardCategorySearch;
import com.community.cms.entity.BoardCategory;
import com.querydsl.core.types.Predicate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, String>, QuerydslPredicateExecutor<BoardCategory>{

	List<BoardCategory> findAll(Predicate search);
	List<BoardCategory> findAllByChannelUidAndDepth(String channelUid, int depth);
	List<BoardCategory> findAllByDepthOrderByCreateDate(int depth);

	@Modifying
	@Query("DELETE from BoardCategory bc where bc.uid in ?1")
	void deleteList(String[] deleteList);
}
