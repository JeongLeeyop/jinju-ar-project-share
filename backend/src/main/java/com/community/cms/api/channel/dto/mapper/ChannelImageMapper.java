package com.community.cms.api.channel.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelImageDto;
import com.community.cms.entity2.ChannelImage;

@Mapper
public interface ChannelImageMapper {
    ChannelImageMapper INSTANCE = Mappers.getMapper(ChannelImageMapper.class);
    
    ChannelImageDto.detail entityToDetail(ChannelImage challengeFile);
    ChannelImage detailDtoToEntity(ChannelImageDto.detail detailDto);
}
