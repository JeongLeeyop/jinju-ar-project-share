package com.community.cms.api.channel.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelImageDto;
import com.community.cms.entity2.ChannelImage;

@Mapper
public interface AdmChannelImageMapper {
    AdmChannelImageMapper INSTANCE = Mappers.getMapper(AdmChannelImageMapper.class);
    
    ChannelImageDto.detail entityToDetail(ChannelImage channelImage);
    ChannelImage detailDtoToEntity(ChannelImageDto.detail detailDto);
}
