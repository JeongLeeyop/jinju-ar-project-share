package com.community.cms.api.calendar.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.calendar.dto.CalendarDto;
// import com.community.cms.api.review.dto.ReviewDto;
import com.community.cms.entity2.Calendar;

@Mapper
public interface CalendarMapper {
    CalendarMapper INSTANCE = Mappers.getMapper(CalendarMapper.class);
    CalendarDto.list entityToList(Calendar Calendar);
    CalendarDto.detail entityToDetail(Calendar Calendar);
    Calendar addDtoToEntity(CalendarDto.add addDto);
    Calendar updateDtoToEntity(CalendarDto.update updateDto, @MappingTarget Calendar entity);
}