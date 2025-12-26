package com.community.cms.api.board.dto.mapper;

import com.community.cms.api.board.dto.BoardSkinDto.Detail;
import com.community.cms.entity.BoardSkin;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BoardSkinMapper {
    BoardSkinMapper INSTANCE = Mappers.getMapper(BoardSkinMapper.class);

    Detail entityToDetailDto(BoardSkin entity);
}
