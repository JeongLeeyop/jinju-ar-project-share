package com.community.cms.entity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.entity.Tag;
import com.community.cms.entity.dto.TagDto;

@Mapper
public interface TagMapper {
	TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

	TagDto entityToDto(Tag entity);
}
