package com.community.cms.api.channel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.channel.dto.ChannelCategoryDto;
import com.community.cms.api.channel.service.AdmChannelCategoryService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/channel/category")
@AllArgsConstructor
public class ChannelCategoryController {
	private final AdmChannelCategoryService admChannelCategoryService;
	
	@GetMapping("/list")
	public ResponseEntity<List<ChannelCategoryDto.detail>> listAll() {
		return ResponseEntity.ok(admChannelCategoryService.listAll());
	}
}
