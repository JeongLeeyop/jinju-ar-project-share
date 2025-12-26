package com.community.cms.api.coupon.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.coupon.dto.UserCouponDto;
import com.community.cms.entity.UserCoupon;

@Mapper
public interface UserCouponMapper {
    UserCouponMapper INSTANCE = Mappers.getMapper(UserCouponMapper.class);

    UserCouponDto.list entityToListDto(UserCoupon entity);
}
