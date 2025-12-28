package com.community.cms.api.attached_file.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.attached_file.dto.AttachedFileDto;
import com.community.cms.entity.AttachedFile;

@Mapper
public interface AttachedFileMapper {
    AttachedFileMapper INSTANCE = Mappers.getMapper(AttachedFileMapper.class);

    @Mapping(target = "url", expression = "java(\"/api/attached-file/\" + entity.getUid())")
    AttachedFileDto.detail entityToDetailDto(AttachedFile entity);

    AttachedFileDto.clientDetail entityToClientDetailDto(AttachedFile entity);
}
