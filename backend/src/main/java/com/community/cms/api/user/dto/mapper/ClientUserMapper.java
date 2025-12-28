package com.community.cms.api.user.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.user.dto.ClientUserDto;
import com.community.cms.api.user.dto.UserDto;
import com.community.cms.entity.User;

@Mapper
public interface ClientUserMapper {
    ClientUserMapper INSTANCE = Mappers.getMapper(ClientUserMapper.class);

    User joinDtoToEntity(ClientUserDto.join dto, @MappingTarget User entity);

    User joinDtoToEntity(ClientUserDto.join dto);

    ClientUserDto.info entityToInfoDto(User entity);
    
    ClientUserDto.channelInfo entityToChannelInfoDto(User entity);

    User updateDtoToEntity(ClientUserDto.update dto, @MappingTarget User entity);
}
