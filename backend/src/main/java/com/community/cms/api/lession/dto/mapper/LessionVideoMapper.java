package com.community.cms.api.lession.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.lession.dto.LessionVideoFileDto;
import com.community.cms.api.lession.dto.LessionVideoDto;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.LessionVideoFile;
import com.community.cms.entity2.LessionVideo;

@Mapper
public interface LessionVideoMapper {
    LessionVideoMapper INSTANCE = Mappers.getMapper(LessionVideoMapper.class);
    LessionVideoDto.list entityToList(LessionVideo LessionVideo);
    LessionVideoDto.detail entityToDetail(LessionVideo LessionVideo);
    
    LessionVideo addDtoToEntity(LessionVideoDto.add addDto);
    LessionVideo updateDtoToEntity(LessionVideoDto.update updateDto, @MappingTarget LessionVideo entity);
    
    /* default LessionVideo addDtoToEntity(LessionVideoDto.add dto) {
        List<LessionVideoFile> fileList = new ArrayList<LessionVideoFile>();
        int iconOrderView = 1;
        for (LessionVideoFileDto.add item : dto.getFileList()) {
            LessionVideoFile fileEntity = new LessionVideoFile();
            fileEntity.setFileUid(item.getFileUid());
            item.setLessionVideoUid(dto.getUid());
            fileEntity.setViewOrder(iconOrderView);
            fileList.add(fileEntity);
            iconOrderView++;
        };
        LessionVideoFile file = new LessionVideoFile();
            file.setFileUid(file.getFileUid());
            file.setChannelUid(dto.getUid());
            file.setViewOrder(coverOrderView);
            imageList.add(file);
            coverOrderView++;
        };
        Channel entity = addDtoToEntityNormal(dto);
        entity.setFileList(imageList);
        return entity;
    } */
    
    /* default LessionVideo updateDtoToEntity(LessionVideoDto.update dto, LessionVideo entity) {
        List<LessionVideoFile> imageList = new ArrayList<LessionVideoFile>();
        int iconOrderView = 1;
        for (LessionVideoFileDto.update image : dto.getIconImageList()) {
            LessionVideoFile file = new LessionVideoFile();
            file.setFileUid(image.getFileUid());
            file.setImageType(LessionVideoFileType.ICON);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(iconOrderView);
            imageList.add(file);
            iconOrderView++;
        };
        int coverOrderView = 1;
        for (LessionVideoFileDto.update image : dto.getCoverImageList()) {
            LessionVideoFile file = new LessionVideoFile();
            file.setFileUid(image.getFileUid());
            file.setImageType(LessionVideoFileType.COVER);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(coverOrderView);
            imageList.add(file);
            coverOrderView++;
        };
        Channel channel = updateDtoToEntityNormal(dto, entity);
        channel.setImagesList(imageList);
        return channel;
    }
 */
}