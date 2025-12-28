package com.community.cms.api.board.dto.search;

import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.community.cms.entity.QBoard;
import com.community.cms.entity.QBoardCategory;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BoardSearch {
	private String searchType;
	private String searchValue;
	
	public Predicate search() {
		
		QBoard board = QBoard.board;
		
		BooleanBuilder builder = new BooleanBuilder();
		
		if (StringUtils.hasText(searchType)) {
			switch (searchType) {
			case "name":
				builder.and(board.name.contains(searchValue));
				break;
			case "skin":
				builder.and(board.skin.contains(searchValue));
				break;
			default:
				break;
			}
		}
		return builder;
	}
}
