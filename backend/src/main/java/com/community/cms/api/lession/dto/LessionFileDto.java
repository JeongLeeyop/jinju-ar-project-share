package com.community.cms.api.lession.dto;

import com.community.cms.api.attached_file.dto.AttachedFileDto;

import lombok.Data;							
					
public class LessionFileDto {
    
    @Data
    public static class list {
        private String fileUid;
        private String name;
        private Integer viewOrder;
        public void setFile(AttachedFileDto.simple file) {
            if(file!=null) this.name = file.getName();
        }
    }
    
    @Data
    public static class detail {
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
	