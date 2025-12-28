package com.community.cms.api.calendar.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    
    // idx, eventType, points 필드를 무시하여 수정 불가능하도록 함
    @Mapping(target = "idx", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    @Mapping(target = "points", ignore = true)
    Calendar updateDtoToEntity(CalendarDto.update updateDto, @MappingTarget Calendar entity);

    default CalendarDto.fullCalendar entityToFullCalendar(Calendar Calendar) {
        CalendarDto.list listDto = entityToList(Calendar);
        CalendarDto.fullCalendar dto = new CalendarDto.fullCalendar();
        CalendarDto.extendedProps dto2 = new CalendarDto.extendedProps();
        
        // 기본 정보
        dto.setIdx(listDto.getIdx());
        dto.setTitle(listDto.getTitle());
        dto.setStart(listDto.getStartDate());
        dto.setEnd(listDto.getEndDate());
        dto.setLocation(listDto.getLocation());
        
        // 포인트 및 이벤트 타입 정보
        dto.setPoints(listDto.getPoints());
        dto.setEventType(listDto.getEventType());
        
        // 작성자 정보
        dto.setWriterUid(listDto.getWriterUid());
        dto.setWriterName(listDto.getWriterName());
        
        // 참여 정보
        dto.setParticipantCount(listDto.getParticipantCount());
        dto.setIsParticipating(listDto.getIsParticipating());
        
        // 좋아요 및 댓글 정보
        dto.setLikeCount(listDto.getLikeCount());
        dto.setIsLiked(listDto.getIsLiked());
        dto.setCommentCount(listDto.getCommentCount());
        
        // 확장 속성
        dto2.setDescription(listDto.getContent());
        dto2.setCreatedDate(listDto.getCreatedDate());
        dto2.setIdx(listDto.getIdx());
        dto.setExtendedProps(dto2);
        
        return dto;
    }
}
