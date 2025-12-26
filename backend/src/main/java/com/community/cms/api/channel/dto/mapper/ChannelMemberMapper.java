package com.community.cms.api.channel.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelMemberDto;
// import com.community.cms.api.review.dto.ReviewDto;
import com.community.cms.entity2.ChannelMember;

@Mapper
public interface ChannelMemberMapper {
    ChannelMemberMapper INSTANCE = Mappers.getMapper(ChannelMemberMapper.class);
    ChannelMemberDto.detail entityToDetailDtoNormal(ChannelMember entity);

    default ChannelMemberDto.detail entityToDetailDto(ChannelMember entity, List<String> userUidList) {
        ChannelMemberDto.detail dto = entityToDetailDtoNormal(entity);
        if(userUidList != null && userUidList.contains(dto.getUserUid())) dto.setOnline(true);
        return dto;
    }
}
