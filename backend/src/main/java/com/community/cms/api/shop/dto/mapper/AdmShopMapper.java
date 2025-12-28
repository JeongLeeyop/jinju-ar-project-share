package com.community.cms.api.shop.dto.mapper;

import java.time.LocalTime;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.shop.dto.AdmShopDto;
import com.community.cms.api.shop.dto.AdmShopPickupTimeDto;
import com.community.cms.entity.Shop;

@Mapper
public interface AdmShopMapper {
    AdmShopMapper INSTANCE = Mappers.getMapper(AdmShopMapper.class);

    AdmShopDto.list entityToListDto(Shop entity);

    /*
    default Shop addDtoToEntity(AdmShopDto.add addDto) {
        Shop entity = addDtoToEntityNormal(addDto);
        setPickupTimes(entity, addDto.getPickupTimes());
        return entity;
    }

    default Shop updateDtoToEntity(AdmShopDto.update updateDto, Shop entity) {
        entity = updateDtoToEntityNormal(updateDto, entity);
        setPickupTimes(entity, updateDto.getPickupTimes());
        return entity;
    }
    */
}
