package com.community.cms.api.channel.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelImageDto;
// import com.community.cms.api.channel.dto.ChannelImageDto;
import com.community.cms.api.channel.dto.ChannelSearch;
// import com.community.cms.api.review.dto.ReviewDto;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelImage;
import com.community.cms.entity2.ChannelImageType;

@Mapper
public interface ChannelMapper {
    ChannelMapper INSTANCE = Mappers.getMapper(ChannelMapper.class);
    Channel detailDtoToEntity(ChannelDto.detail deatilDto);
    
    ChannelDto.list entityToListNormal(Channel Channel);
    ChannelDto.myList entityToMyList(Channel Channel);

    ChannelDto.detail entityToDetailNormal(Channel Channel);
    
    Channel addDtoToEntityNormal(ChannelDto.add addDto);
    Channel updateDtoToEntityNormal(ChannelDto.update addDto, @MappingTarget Channel entity);
    
    
    default ChannelDto.detail entityToDetail(Channel Channel) {
        ChannelDto.detail dto = entityToDetailNormal(Channel);
        List<ChannelImageDto.detail> iconImageList = new ArrayList<ChannelImageDto.detail>();
        List<ChannelImageDto.detail> coverImageList = new ArrayList<ChannelImageDto.detail>();
        
        List<ChannelImage> fileList = Channel.getImagesList();
        
        if(fileList.size() > 0) {
            for(ChannelImage file : fileList) {
                if (file.getImageType().toString().equals("ICON")) iconImageList.add(ChannelImageMapper.INSTANCE.entityToDetail(file));
                else if (file.getImageType().toString().equals("COVER")) coverImageList.add(ChannelImageMapper.INSTANCE.entityToDetail(file));
            }   
        };
        dto.setIconImageList(iconImageList);
        dto.setCoverImageList(coverImageList);
        return dto;
    }

    default ChannelDto.list entityToList(Channel Channel) {
        ChannelDto.list dto = entityToListNormal(Channel);
        List<ChannelImageDto.detail> iconImageList = new ArrayList<ChannelImageDto.detail>();
        List<ChannelImageDto.detail> coverImageList = new ArrayList<ChannelImageDto.detail>();
        
        List<ChannelImage> fileList = Channel.getImagesList();
        
        if(fileList.size() > 0) {
            for(ChannelImage file : fileList) {
                if (file.getImageType().toString().equals("ICON")) iconImageList.add(ChannelImageMapper.INSTANCE.entityToDetail(file));
                else if (file.getImageType().toString().equals("COVER")) coverImageList.add(ChannelImageMapper.INSTANCE.entityToDetail(file));
            }   
        };
        dto.setIconImageList(iconImageList);
        dto.setCoverImageList(coverImageList);
        return dto;
    }

    default ChannelDto.detail entityToDetailMyFlag(Channel Channel, ChannelSearch search) {
        ChannelDto.detail dto = entityToDetailNormal(Channel);
        
        if(search != null && search.isMyFlag()) {
            // Add your custom logic here if needed for myFlag
        }
        
        return dto;
    }

    default Channel addDtoToEntity(ChannelDto.add dto) {
        List<ChannelImage> imageList = new ArrayList<ChannelImage>();
        int iconOrderView = 1;
        for (ChannelImageDto.add image : dto.getIconImageList()) {
            ChannelImage file = new ChannelImage();
            file.setFileUid(image.getFileUid());
            file.setImageType(ChannelImageType.ICON);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(iconOrderView);
            imageList.add(file);
            iconOrderView++;
        };
        int coverOrderView = 1;
        for (ChannelImageDto.add image : dto.getCoverImageList()) {
            ChannelImage file = new ChannelImage();
            file.setFileUid(image.getFileUid());
            file.setImageType(ChannelImageType.COVER);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(coverOrderView);
            imageList.add(file);
            coverOrderView++;
        };
        Channel entity = addDtoToEntityNormal(dto);
        entity.setImagesList(imageList);
        return entity;
    }
    
    @Mapping(target = "questionList", ignore = true) 
    default Channel updateDtoToEntity(ChannelDto.update dto, Channel entity) {
        List<ChannelImage> imageList = new ArrayList<ChannelImage>();
        int iconOrderView = 1;
        for (ChannelImageDto.update image : dto.getIconImageList()) {
            ChannelImage file = new ChannelImage();
            file.setFileUid(image.getFileUid());
            file.setImageType(ChannelImageType.ICON);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(iconOrderView);
            imageList.add(file);
            iconOrderView++;
        };
        int coverOrderView = 1;
        for (ChannelImageDto.update image : dto.getCoverImageList()) {
            ChannelImage file = new ChannelImage();
            file.setFileUid(image.getFileUid());
            file.setImageType(ChannelImageType.COVER);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(coverOrderView);
            imageList.add(file);
            coverOrderView++;
        };
        Channel channel = updateDtoToEntityNormal(dto, entity);
        channel.setImagesList(imageList);
        return channel;
    }
}
