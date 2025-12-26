package com.community.cms.api.channel.dto;

import javax.validation.constraints.NotBlank;

import com.community.cms.api.attached_file.dto.AttachedFileDto;
import com.community.cms.common.EnumField;
import com.community.cms.entity2.ChannelImageType;

import lombok.Data;							
					
public class ChannelImageDto {
    
    @Data
    public static class List {
        // private Integer idx;    
        private String fileUid;
        private Integer viewOrder;

        @NotBlank(message = "이미지 타입을 입력하세요.")
		@EnumField(enumClass = ChannelImageType.class, message = "ICON, COVER 중 하나로 입력하세요.")
		private String imageType;
        // private Integer channel;
    }
    
    @Data
    public static class detail {
        // private Integer idx;    
        private String fileUid;
        private String name;
        private Integer viewOrder;
        public void setFile(AttachedFileDto.simple file) {
            if(file!=null) this.name = file.getName();
        }
    }
    
    @Data
    public static class add {
        private String fileUid;
    }

    @Data
    public static class update {
        private Integer idx;
        private String fileUid;
    }
}	
	