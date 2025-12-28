package com.community.cms.api.new_alarm.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.new_alarm.dto.NewAlarmDto;
import com.community.cms.entity.NewAlarm;

@Mapper
public interface NewAlarmMapper {
    NewAlarmMapper INSTANCE = Mappers.getMapper(NewAlarmMapper.class);

}
