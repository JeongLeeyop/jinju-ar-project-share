package com.community.cms.api.board.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.community.cms.api.board.dto.BoardUseCategoryDto;
import com.community.cms.api.board.dto.mapper.BoardUseCategoryMapper;
import com.community.cms.api.board.repository.BoardUseCategoryRepository;
import com.community.cms.entity.BoardUseCategory;

import lombok.AllArgsConstructor;

public interface BoardUseCategoryService {
	void add(BoardUseCategoryDto.add addDto);
	void delete(BoardUseCategory entity);
}

@Service
@AllArgsConstructor
class BoardUseCategoryServiceImpl implements BoardUseCategoryService {
	private final BoardUseCategoryRepository boardUseCategoryRepository;

	@Transactional
	@Override
	public void add(BoardUseCategoryDto.add addDto) {
		BoardUseCategory boardUseCategory = BoardUseCategoryMapper.INSTANCE.addDtoToEntity(addDto);
		boardUseCategoryRepository.save(boardUseCategory);
	}
	
	@Transactional
	@Override
	public void delete(BoardUseCategory boardUseCategory) {
		boardUseCategoryRepository.delete(boardUseCategory);
	}
}
