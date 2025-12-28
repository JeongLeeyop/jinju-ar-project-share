package com.community.cms.api.event.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.event.dto.EventHistoryDto;
import com.community.cms.entity2.EventHistory;

@Mapper
public interface EventHistoryMapper {
    EventHistoryMapper INSTANCE = Mappers.getMapper(EventHistoryMapper.class);
    // LessionDto.list entityToList(Lession Lession);
    // LessionDto.detail entityToDetail(Lession Lession);
    EventHistory addDtoToEntity(EventHistoryDto.add addDto);
    // Lession updateDtoToEntity(LessionDto.update updateDto, @MappingTarget Lession entity);
}
