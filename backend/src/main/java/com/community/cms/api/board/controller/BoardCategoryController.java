package com.community.cms.api.board.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.board.dto.BoardCategoryDto;
import com.community.cms.api.board.dto.BoardUseCategoryDto;
import com.community.cms.api.board.dto.mapper.BoardCategoryMapper;
import com.community.cms.api.board.dto.search.BoardCategorySearch;
import com.community.cms.api.board.exception.BoardCategoryNotFoundException;
import com.community.cms.api.board.service.BoardCategoryService;
import com.community.cms.entity.BoardCategory;

import lombok.AllArgsConstructor;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CREATOR')")
@RequestMapping("/api/board-category")
@AllArgsConstructor
public class BoardCategoryController {
	
	private final BoardCategoryService boardCategoryService;
	
	@GetMapping
	public ResponseEntity<Page<BoardCategoryDto.Detail>> list(
			BoardCategorySearch categorySearch,
			@PageableDefault(
				size=10,
				page=0,
				direction = Direction.DESC,
				sort = {"createDate"}) Pageable pageable) {
		return ResponseEntity.ok(boardCategoryService.list(categorySearch, pageable));
	}
	
	@GetMapping("{uid}")
	public ResponseEntity<BoardCategoryDto.Detail> get(@PathVariable("uid") BoardCategory boardCategory) {
		if(boardCategory == null) throw new BoardCategoryNotFoundException();
		return ResponseEntity.ok(BoardCategoryMapper.INSTANCE.entityToDetailDto(boardCategory));
	}
	
	@GetMapping("community")
	public ResponseEntity<BoardCategoryDto.setting> community(BoardCategorySearch search) {
		return ResponseEntity.ok(boardCategoryService.community(search));
	}
	

	@GetMapping("all/list")
	public ResponseEntity<List<BoardCategoryDto.Detail>> listAll() {
		return ResponseEntity.ok(boardCategoryService.listAll());
	}
	
	@PostMapping
	public ResponseEntity add(@RequestBody @Valid BoardCategoryDto.Add addDto) {
		boardCategoryService.add(addDto);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("custom")
	public ResponseEntity customAdd(@RequestBody @Valid BoardCategoryDto.customAdd addDto) {
		boardCategoryService.customAdd(addDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("community")
	public ResponseEntity communitySetting(@RequestBody @Valid BoardCategoryDto.settingAdd addDto) {
		boardCategoryService.communitySetting(addDto);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("categoryUse")
	public ResponseEntity categoryUseAdd(@RequestBody @Valid BoardCategoryDto.categoryUseAdd addDto) {
		boardCategoryService.categoryUseAdd(addDto);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("{uid}")
	public ResponseEntity update(
			@PathVariable("uid") BoardCategory boardCategory,
			@RequestBody @Valid BoardCategoryDto.Update updateDto) {
		if(boardCategory == null) throw new BoardCategoryNotFoundException();
		boardCategoryService.update(boardCategory, updateDto);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("{uid}")
	public ResponseEntity delete(@PathVariable("uid") BoardCategory boardCategory) {
		if(boardCategory == null) throw new BoardCategoryNotFoundException();
		boardCategoryService.delete(boardCategory);
		return ResponseEntity.ok().build();
	}
	
}
