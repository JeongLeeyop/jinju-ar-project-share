package com.community.cms.api.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 오프라인 장터 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineMarketplaceDto {

    private String uid;
    private String channelUid;
    private String name;
    private String description;
    
    @JsonProperty("isActive")
    private boolean isActive;
    
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Integer productCount;  // 상품 개수

    /**
     * 오프라인 장터 생성 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        
        @NotBlank(message = "장터 이름을 입력해주세요")
        private String name;
        
        private String description;
    }

    /**
     * 오프라인 장터 수정 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        
        @NotBlank(message = "장터 이름을 입력해주세요")
        private String name;
        
        private String description;
        
        private Boolean isActive;
    }
}
