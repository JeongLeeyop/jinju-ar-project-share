package com.community.cms.api.lession.dto.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.lession.dto.LessionFileDto;
import com.community.cms.api.lession.dto.LessionDto;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.LessionFile;
import com.community.cms.entity2.Lession;

@Mapper
public interface LessionMapper {
    LessionMapper INSTANCE = Mappers.getMapper(LessionMapper.class);
    LessionDto.list entityToList(Lession Lession);
    LessionDto.detail entityToDetail(Lession Lession);
    
    Lession addDtoToEntity(LessionDto.add addDto);
    Lession updateDtoToEntity(LessionDto.update updateDto, @MappingTarget Lession entity);
    
    /* default Lession addDtoToEntity(LessionDto.add dto) {
        List<LessionFile> fileList = new ArrayList<LessionFile>();
        int iconOrderView = 1;
        for (LessionFileDto.add item : dto.getFileList()) {
            LessionFile fileEntity = new LessionFile();
            fileEntity.setFileUid(item.getFileUid());
            item.setLessionUid(dto.getUid());
            fileEntity.setViewOrder(iconOrderView);
            fileList.add(fileEntity);
            iconOrderView++;
        };
        LessionFile file = new LessionFile();
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
    
    /* default Lession updateDtoToEntity(LessionDto.update dto, Lession entity) {
        List<LessionFile> imageList = new ArrayList<LessionFile>();
        int iconOrderView = 1;
        for (LessionFileDto.update image : dto.getIconImageList()) {
            LessionFile file = new LessionFile();
            file.setFileUid(image.getFileUid());
            file.setImageType(LessionFileType.ICON);
            file.setChannelUid(dto.getUid());
            file.setViewOrder(iconOrderView);
            imageList.add(file);
            iconOrderView++;
        };
        int coverOrderView = 1;
        for (LessionFileDto.update image : dto.getCoverImageList()) {
            LessionFile file = new LessionFile();
            file.setFileUid(image.getFileUid());
            file.setImageType(LessionFileType.COVER);
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