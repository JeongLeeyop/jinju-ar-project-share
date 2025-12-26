package com.community.cms.api.post.dto.mapper;

import com.community.cms.api.post.dto.PostDto;
import com.community.cms.api.post.dto.PostDto.ClientDetail;
import com.community.cms.api.post.dto.PostDto.ClientList;
import com.community.cms.api.post.dto.PostDto.Detail;
import com.community.cms.entity.Post;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    
    Post addDtoToEntity(PostDto.Add addDto);

    Post updateDtoToEntity(PostDto.Update updateDto, @MappingTarget Post post);

    Detail entityToDetailDto(Post post);

    // ClientList entityToClientListDto(Post post);

    ClientDetail entityToClientDetail(Post post);
}
