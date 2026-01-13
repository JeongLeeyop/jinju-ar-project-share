package com.community.cms.api.user.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.user.dto.ClientUserDto;
import com.community.cms.entity.User;

@Mapper
public interface ClientUserMapper {
    ClientUserMapper INSTANCE = Mappers.getMapper(ClientUserMapper.class);

    User joinDtoToEntity(ClientUserDto.join dto, @MappingTarget User entity);

    User joinDtoToEntity(ClientUserDto.join dto);

    ClientUserDto.info entityToInfoDto(User entity);
    
    ClientUserDto.channelInfo entityToChannelInfoDto(User entity);

    // 업데이트할 필드만 명시적으로 매핑
    // newPassword와 newPasswordCheck는 서비스에서 직접 처리하므로 무시
    @Mapping(target = "actualName", source = "actualName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "concatNumber", source = "concatNumber")
    @Mapping(target = "iconFileUid", source = "iconFileUid")
    @Mapping(target = "userPassword", ignore = true)
    User updateDtoToEntity(ClientUserDto.update dto, @MappingTarget User entity);
}
