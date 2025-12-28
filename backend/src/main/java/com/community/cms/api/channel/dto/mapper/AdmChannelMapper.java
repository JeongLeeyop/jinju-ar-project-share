package com.community.cms.api.channel.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelImageDto;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelImage;
import com.community.cms.entity2.ChannelImageType;

@Mapper
public interface AdmChannelMapper {
    AdmChannelMapper INSTANCE = Mappers.getMapper(AdmChannelMapper.class);
    Channel detailDtoToEntity(ChannelDto.detail deatilDto);
    Channel updateDtoToEntityNormal(ChannelDto.update updateDto, @MappingTarget Channel entity);
    ChannelDto.detail entityToDetailNormal(Channel Channel);
    Channel addDtoToEntityNormal(ChannelDto.add addDto);
    
    default ChannelDto.detail entityToDetail(Channel Channel) {
        ChannelDto.detail dto = entityToDetailNormal(Channel);
        List<ChannelImageDto.detail> iconImageList = new ArrayList<ChannelImageDto.detail>();
        List<ChannelImageDto.detail> coverImageList = new ArrayList<ChannelImageDto.detail>();
        
        List<ChannelImage> fileList = Channel.getImagesList();
        
        if(fileList.size() > 0) {
            for(ChannelImage file : fileList) {
                if (file.getImageType().toString().equals("ICON")) iconImageList.add(AdmChannelImageMapper.INSTANCE.entityToDetail(file));
                else coverImageList.add(AdmChannelImageMapper.INSTANCE.entityToDetail(file));
            }   
        };
        dto.setIconImageList(iconImageList);
        dto.setCoverImageList(coverImageList);
        return dto;
    }

    default Channel addDtoToEntity(ChannelDto.add dto) {
        List<ChannelImage> imageList = new ArrayList<ChannelImage>();
        dto.getIconImageList().forEach(m ->  {
            ChannelImage file = new ChannelImage();
            file.setFileUid(m.getFileUid());
            file.setImageType(ChannelImageType.ICON);
            imageList.add(file);
        });
        dto.getCoverImageList().forEach(e ->  {
            ChannelImage file = new ChannelImage();
            file.setFileUid(e.getFileUid());
            file.setImageType(ChannelImageType.COVER);
            imageList.add(file);
        });
        Channel entity = addDtoToEntityNormal(dto);
        entity.setImagesList(imageList);
        return entity;
    }
    
    default Channel updateDtoToEntity(ChannelDto.update dto, @MappingTarget Channel targetEntity) {
        List<ChannelImage> imageList = targetEntity.getImagesList();
        imageList.clear();
        dto.getIconImageList().forEach(m ->  {
            ChannelImage file = new ChannelImage();
            file.setFileUid(m.getFileUid());
            file.setImageType(ChannelImageType.ICON);
            imageList.add(file);
        });
        dto.getCoverImageList().forEach(e ->  {
            ChannelImage file = new ChannelImage();
            file.setFileUid(e.getFileUid());
            file.setImageType(ChannelImageType.COVER);
            imageList.add(file);
        });
        targetEntity.setImagesList(imageList);
        Channel entity = updateDtoToEntityNormal(dto, targetEntity);
        return entity;
    }
}
