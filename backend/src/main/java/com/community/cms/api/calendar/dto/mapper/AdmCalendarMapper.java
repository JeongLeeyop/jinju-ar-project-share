package com.community.cms.api.calendar.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.entity2.Calendar;

@Mapper
public interface AdmCalendarMapper {
    AdmCalendarMapper INSTANCE = Mappers.getMapper(AdmCalendarMapper.class);
    CalendarDto.list entityToList(Calendar Calendar);
    CalendarDto.detail entityToDetail(Calendar Calendar);
    Calendar addDtoToEntity(CalendarDto.add addDto);
    Calendar updateDtoToEntity(CalendarDto.update updateDto, @MappingTarget Calendar entity);

    default CalendarDto.fullCalendar entityToFullCalendar(Calendar Calendar) {
        CalendarDto.list listDto = entityToList(Calendar);
        CalendarDto.fullCalendar dto = new CalendarDto.fullCalendar();
        CalendarDto.extendedProps dto2 = new CalendarDto.extendedProps();
        dto.setTitle(listDto.getTitle());
        dto.setStart(listDto.getStartDate());
        dto.setEnd(listDto.getEndDate());
        dto2.setDescription(listDto.getContent());
        dto2.setCreatedDate(listDto.getCreatedDate());
        dto2.setIdx(listDto.getIdx());
        dto.setExtendedProps(dto2);
        return dto;
    }
}
