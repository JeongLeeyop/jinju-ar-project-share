package com.community.cms.api.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.Predicate;
import com.community.cms.api.board.dto.search.BoardSearch;
import com.community.cms.entity.Board;

public interface BoardRepository extends JpaRepository<Board, String>, QuerydslPredicateExecutor<Board> {

	List<Board> findAll(Predicate search);

	Board findTopBySkinOrderByCreateDate(String boardType);

    List<Board> findAllByOrderByCreateDate();

}
