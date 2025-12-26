package com.community.cms.api.channel.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelCategoryDto;
import com.community.cms.entity2.ChannelCategory;

@Mapper
public interface ChannelCategoryMapper {
    ChannelCategoryMapper INSTANCE = Mappers.getMapper(ChannelCategoryMapper.class);
    ChannelCategoryDto.detail entityToDetailDto(ChannelCategory entity);
}
