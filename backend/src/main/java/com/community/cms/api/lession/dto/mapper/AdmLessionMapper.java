package com.community.cms.api.lession.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.lession.dto.LessionDto;
import com.community.cms.entity2.Lession;

@Mapper
public interface AdmLessionMapper {
    AdmLessionMapper INSTANCE = Mappers.getMapper(AdmLessionMapper.class);
    LessionDto.list entityToList(Lession Lession);
    LessionDto.detail entityToDetail(Lession Lession);
    Lession addDtoToEntity(LessionDto.add addDto);
    Lession updateDtoToEntity(LessionDto.update updateDto, @MappingTarget Lession entity);
}
