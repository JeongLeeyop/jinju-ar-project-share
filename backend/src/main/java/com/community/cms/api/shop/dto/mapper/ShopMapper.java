package com.community.cms.api.shop.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.shop.dto.ShopDto;
import com.community.cms.entity.Shop;

@Mapper
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopDto.detail entityToDetailDto(Shop entity);

    ShopDto.list entityToListDto(Shop entity);
}
