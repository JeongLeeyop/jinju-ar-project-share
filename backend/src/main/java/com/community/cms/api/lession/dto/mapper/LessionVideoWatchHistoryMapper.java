package com.community.cms.api.lession.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.lession.dto.LessionVideoDto;
import com.community.cms.entity2.LessionVideoWatchHistory;

@Mapper
public interface LessionVideoWatchHistoryMapper {
    LessionVideoWatchHistoryMapper INSTANCE = Mappers.getMapper(LessionVideoWatchHistoryMapper.class);
    LessionVideoDto.watchHistory entityToDto(LessionVideoWatchHistory lession);
    LessionVideoWatchHistory dtoToEntity(LessionVideoDto.watchHistory dto);
    LessionVideoWatchHistory updateDtoToEntity(LessionVideoDto.watchHistory dto, @MappingTarget LessionVideoWatchHistory lessionVideoWatchHistory);
}