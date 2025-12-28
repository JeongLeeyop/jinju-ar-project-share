package com.community.cms.api.board.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.board.dto.BoardUseCategoryDto;
import com.community.cms.api.board.dto.BoardUseCategoryDto.Detail;
import com.community.cms.api.board.dto.BoardUseCategoryDto.setting;
import com.community.cms.entity.BoardUseCategory;

@Mapper
public interface BoardUseCategoryMapper {
    BoardUseCategoryMapper INSTANCE = Mappers.getMapper(BoardUseCategoryMapper.class);

    Detail entityToDetailDto(BoardUseCategory entity);
    
    setting entityToSettingDto(BoardUseCategory entity);

    BoardUseCategory addDtoToEntity(BoardUseCategoryDto.add dto);
}
