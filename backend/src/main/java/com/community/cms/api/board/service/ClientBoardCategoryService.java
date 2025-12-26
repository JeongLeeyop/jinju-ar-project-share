package com.community.cms.api.board.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.community.cms.api.board.dto.BoardCategoryDto;
import com.community.cms.api.board.dto.BoardUseCategoryDto;
import com.community.cms.api.board.dto.mapper.BoardCategoryMapper;
import com.community.cms.api.board.dto.mapper.BoardUseCategoryMapper;
import com.community.cms.api.board.dto.search.BoardCategorySearch;
import com.community.cms.api.board.repository.BoardCategoryRepository;
import com.community.cms.api.board.repository.BoardUseCategoryRepository;
import com.community.cms.api.post.repository.PostCategoryRepository;
import com.community.cms.entity.BoardCategory;
import com.community.cms.entity.PostCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

public interface ClientBoardCategoryService {
	Page<BoardCategoryDto.Detail> list(BoardCategorySearch categorySearch, Pageable pageable);

	List<BoardCategoryDto.Detail> listAll();

	List<BoardUseCategoryDto.Detail> community(BoardCategorySearch search);

	void add(BoardCategoryDto.Add addDto);

	void update(BoardCategory boardCategory, BoardCategoryDto.Update updateDto);

	void delete(BoardCategory boardCategory);
}

@Service
@AllArgsConstructor
class ClientBoardCategoryServiceImpl implements ClientBoardCategoryService {

	private final BoardCategoryRepository boardCategoryRepository;
	private final BoardUseCategoryRepository boardUseCategoryRepository;
	private final PostCategoryRepository postCategoryRepository;

	@Override
	public Page<BoardCategoryDto.Detail> list(BoardCategorySearch categorySearch, Pageable pageable) {
		return boardCategoryRepository.findAll(categorySearch.search(), pageable)
			.map(data -> BoardCategoryMapper.INSTANCE.entityToDetailDto(data));
	}

	@Override
	public List<BoardCategoryDto.Detail> listAll() {
		return boardCategoryRepository.findAllByDepthOrderByCreateDate(1)
			.stream().map(data -> BoardCategoryMapper.INSTANCE.entityToDetailDto(data))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<BoardUseCategoryDto.Detail> community(BoardCategorySearch search) {
		return boardUseCategoryRepository.findAllByBoardUidAndChannelUid(search.getBoardUid(), search.getChannelUid())
			.stream().map(data -> BoardUseCategoryMapper.INSTANCE.entityToDetailDto(data))
			.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public void add(BoardCategoryDto.Add addDto) {
		BoardCategory boardCategory = BoardCategoryMapper.INSTANCE.addDtoToEntity(addDto);
		boardCategoryRepository.save(boardCategory);
	}

	@Transactional
	@Override
	public void update(BoardCategory boardCategory, BoardCategoryDto.Update updateDto) {
		boardCategory = BoardCategoryMapper.INSTANCE.updateDtoToEntity(updateDto, boardCategory);
		boardCategoryRepository.save(boardCategory);

		// postCategory에 삭제된 카테고리 반영하기
		if(updateDto.getDeleteList() !=null && !updateDto.getDeleteList().isEmpty()){
			List<PostCategory> result = postCategoryRepository.findAllByCategoryUidIn(updateDto.getDeleteList());
			for (PostCategory item : result) {
				postCategoryRepository.delete(item);
			}
		}
	}

	@Transactional
	@Override
	public void delete(BoardCategory boardCategory) {
		boardCategoryRepository.delete(boardCategory);
	}
}
