package com.community.cms.api.level.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.level.dto.ChannelLevelDto;
import com.community.cms.entity2.ChannelLevel;

@Mapper
public interface ChannelLevelMapper {
    ChannelLevelMapper INSTANCE = Mappers.getMapper(ChannelLevelMapper.class);
    ChannelLevel detailDtoToEntity(ChannelLevelDto.detail deatilDto);
    ChannelLevelDto.list entityToList(ChannelLevel Channel);
    ChannelLevelDto.detail entityToDetail(ChannelLevel Channel);
    ChannelLevel addDtoToEntity(ChannelLevelDto.add addDto);
    ChannelLevel updateDtoToEntity(ChannelLevelDto.add addDto, @MappingTarget ChannelLevel entity);
}
