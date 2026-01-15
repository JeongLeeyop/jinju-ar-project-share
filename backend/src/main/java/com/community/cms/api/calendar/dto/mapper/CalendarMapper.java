package com.community.cms.api.calendar.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.calendar.dto.CalendarDto;
// import com.community.cms.api.review.dto.ReviewDto;
import com.community.cms.entity2.Calendar;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CalendarMapper {
    CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
    CalendarDto.list entityToList(Calendar Calendar);
    CalendarDto.detail entityToDetail(Calendar Calendar);
    Calendar addDtoToEntity(CalendarDto.add addDto);
    
    @Mapping(target = "idx", ignore = true)
    @Mapping(target = "writerUid", ignore = true)
    @Mapping(target = "channelUid", ignore = true)
    @Mapping(target = "maxParticipants", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    Calendar updateDtoToEntity(CalendarDto.update updateDto, @MappingTarget Calendar entity);
}