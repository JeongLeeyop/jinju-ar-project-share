package com.community.cms.api.channel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.community.cms.api.channel.dto.ChannelCategoryDto;
import com.community.cms.api.channel.dto.mapper.ChannelCategoryMapper;
import com.community.cms.api.channel.repository.AdmChannelCategoryRepository;

import lombok.AllArgsConstructor;

public interface AdmChannelCategoryService {
	List<ChannelCategoryDto.detail> listAll();
}

@Service
@AllArgsConstructor
class AdmChannelCategoryServiceImpl implements AdmChannelCategoryService {

	private final AdmChannelCategoryRepository admChannelCategoryRepository;
	
	@Override
	public List<ChannelCategoryDto.detail> listAll() {
		return admChannelCategoryRepository.findAll()
			.stream().map(data -> ChannelCategoryMapper.INSTANCE.entityToDetailDto(data))
			.collect(Collectors.toList());
	}
}
