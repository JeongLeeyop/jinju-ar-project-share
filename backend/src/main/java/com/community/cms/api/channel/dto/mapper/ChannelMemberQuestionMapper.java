package com.community.cms.api.channel.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelMemberQuestionDto;
import com.community.cms.entity2.ChannelMemberQuestion;

@Mapper
public interface ChannelMemberQuestionMapper {
    ChannelMemberQuestionMapper INSTANCE = Mappers.getMapper(ChannelMemberQuestionMapper.class);
    ChannelMemberQuestion addDtoToEntity(ChannelMemberQuestionDto.add addDto);
    List<ChannelMemberQuestion> addListDtoToEntityList(List<ChannelMemberQuestionDto.add> addDtoList);
}

