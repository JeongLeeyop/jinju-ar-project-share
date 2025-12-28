package com.community.cms.api.point.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.entity.PointHistory;

@Mapper
public interface PointHistoryMapper {
    PointHistoryMapper INSTANCE = Mappers.getMapper(PointHistoryMapper.class);

    PointHistoryDto.list entityToListDto(PointHistory entity);
}
