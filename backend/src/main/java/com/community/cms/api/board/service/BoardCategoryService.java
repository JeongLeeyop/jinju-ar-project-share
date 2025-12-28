package com.community.cms.api.board.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.community.cms.api.board.dto.BoardCategoryDto;
import com.community.cms.api.board.dto.BoardUseCategoryDto;
import com.community.cms.api.board.dto.mapper.BoardCategoryMapper;
import com.community.cms.api.board.dto.mapper.BoardUseCategoryMapper;
import com.community.cms.api.board.dto.search.BoardCategorySearch;
import com.community.cms.api.board.repository.BoardCategoryRepository;
import com.community.cms.api.board.repository.BoardUseCategoryRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.post.repository.PostCategoryRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.BoardCategory;
import com.community.cms.entity.BoardUseCategory;
import com.community.cms.entity.PostCategory;
import com.community.cms.entity2.Channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

public interface BoardCategoryService {
	Page<BoardCategoryDto.Detail> list(BoardCategorySearch categorySearch, Pageable pageable);

	List<BoardCategoryDto.Detail> listAll();

	BoardCategoryDto.setting community(BoardCategorySearch search);

	void communitySetting(BoardCategoryDto.settingAdd addDto);

	void add(BoardCategoryDto.Add addDto);

	void update(BoardCategory boardCategory, BoardCategoryDto.Update updateDto);

	void delete(BoardCategory boardCategory);

	void customAdd(BoardCategoryDto.customAdd addDto);
	
	void categoryUseAdd(BoardCategoryDto.categoryUseAdd addDto);
}

@Service
@AllArgsConstructor
class BoardCategoryServiceImpl implements BoardCategoryService {

	private final BoardCategoryRepository boardCategoryRepository;
	private final BoardUseCategoryRepository boardUseCategoryRepository;
	private final PostCategoryRepository postCategoryRepository;
	private final ChannelRepository channelRepository;

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
	public BoardCategoryDto.setting community(BoardCategorySearch search) {
		BoardCategoryDto.setting setting = new BoardCategoryDto.setting();
		
		String channelUid = search.getChannelUid();
		search.setChannelUid(null);

		// default 카테고리
		List<BoardCategoryDto.Detail> defaultList = boardCategoryRepository.findAll(search.search())
		.stream().map(data -> BoardCategoryMapper.INSTANCE.entityToDetailDto(data))
		.collect(Collectors.toList());
		setting.setCommunityDefaultCategoryList(defaultList);
		
		// 커스텀 카테고리 (레벨 3만 가져오기)
		List<BoardCategoryDto.Detail> customList = boardCategoryRepository.findAllByChannelUidAndDepth(channelUid, 2)
		.stream().map(data -> BoardCategoryMapper.INSTANCE.entityToDetailDto(data))
		.collect(Collectors.toList());
		setting.setCommunityCustomCategoryList(customList);

		// 사용중인 카테고리
		List<BoardUseCategoryDto.setting> usedList = boardUseCategoryRepository.findByChannelUid(channelUid)
		.stream().map(data -> BoardUseCategoryMapper.INSTANCE.entityToSettingDto(data))
		.collect(Collectors.toList());
		setting.setCommunityUseCategoryList(usedList);

		return setting;
	}

	@Override
	public void communitySetting(BoardCategoryDto.settingAdd dto) {
		BoardCategoryDto.setting setting = new BoardCategoryDto.setting();
	}

	@Transactional
	@Override
	public void add(BoardCategoryDto.Add addDto) {
		BoardCategory boardCategory = BoardCategoryMapper.INSTANCE.addDtoToEntity(addDto);
		boardCategoryRepository.save(boardCategory);
	}
	
	@Transactional
	@Override
	public void customAdd(BoardCategoryDto.customAdd addDto) {
		Channel channel = channelRepository.findById(addDto.getChannelUid()).orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));
		BoardCategory boardCategory = BoardCategoryMapper.INSTANCE.customAddDtoToEntity(addDto);
		boardCategory.setChannel(channel);
		boardCategoryRepository.save(boardCategory);
	}

	@Transactional
	@Override
	public void categoryUseAdd(BoardCategoryDto.categoryUseAdd addDto) {
		Channel channel = channelRepository.findById(addDto.getChannelUid()).orElseThrow(() -> new NotFoundException(NotFound.CHANNEL));

		List<BoardUseCategory> list = boardUseCategoryRepository.findAllByBoardUidAndChannelUid(addDto.getBoardUid(), channel.getUid());

		BoardUseCategory top = boardUseCategoryRepository.findTop1ByBoardUidAndChannelUidOrderByViewOrderDesc(addDto.getBoardUid(), channel.getUid());
		int index = 0;
		if (top != null) index = top.getViewOrder() + 1;
		// list에서 categoryUid를 추출하여 Set에 저장합니다.
		Set<String> existingCategoryUids = list.stream()
		.map(BoardUseCategory::getCategoryUid)
		.collect(Collectors.toSet());

		// addDto.getCategoryUid()가 비어있는 경우, list의 모든 엔티티를 삭제합니다.
		if (addDto.getCategoryList() == null || addDto.getCategoryList().length == 0) {
			for (BoardUseCategory entity : list) {
				boardUseCategoryRepository.delete(entity);
			}
		} else {
			// addDto.getCategoryList()의 각 uid에 대해 처리합니다.
			for (String uid : addDto.getCategoryList()) {
				// uid가 existingCategoryUids에 없을 때만 새로운 항목을 추가합니다.
				if (!existingCategoryUids.contains(uid)) {
					BoardUseCategory entity = new BoardUseCategory();
					entity.setBoardUid(addDto.getBoardUid());
					entity.setChannelUid(channel.getUid());
					entity.setCategoryUid(uid);
					
					System.out.println("#######################");
					System.out.println(addDto.getBoardUid());
					System.out.println(index);
					
					entity.setViewOrder(index);
					index ++;
					boardUseCategoryRepository.save(entity);

				}
			}
		}

		// 기존 list의 엔티티 중 addDto.getCategoryList()에 없는 uid를 가진 엔티티를 삭제합니다.
		for (BoardUseCategory entity : list) {
			if (!Arrays.asList(addDto.getCategoryList()).contains(entity.getCategoryUid())) {
				boardUseCategoryRepository.delete(entity);
			}
		}
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
