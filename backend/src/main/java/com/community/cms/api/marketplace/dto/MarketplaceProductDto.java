package com.community.cms.api.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 장터 상품 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceProductDto {

    private String uid;
    private String channelUid;
    private String offlineMarketplaceUid;
    private String offlineMarketplaceName;
    private String category;
    private String title;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private String location;  // 거래 장소
    private String sellerUid;
    private String sellerName;
    private String iconFileUid;  // 판매자 프로필 이미지
    private String status;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<String> imageUids;
    private String thumbnailUid;
    
    @JsonProperty("isOffline")
    private boolean isOffline;
    
    @JsonProperty("isMine")
    private boolean isMine;
    
    @JsonProperty("isTrading")
    private boolean isTrading;  // 거래중 여부
    
    // 현재 거래 중인 구매자 정보 (판매자가 조회시)
    private String currentBuyerUid;
    private String currentBuyerName;
    private String currentBuyerIconFileUid;
    private String currentPurchaseUid;

    /**
     * 상품 등록 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        
        private String offlineMarketplaceUid;  // NULL이면 메인 장터
        
        @NotBlank(message = "상품 유형을 선택해주세요")
        private String productType;  // SALE, SHARE, REQUEST
        
        @NotBlank(message = "상품명을 입력해주세요")
        private String title;
        
        private String description;
        
        @NotNull(message = "가격을 입력해주세요")
        private Integer price;
        
        @NotBlank(message = "거래 장소를 입력해주세요")
        private String location;  // 거래 장소
        
        private Integer stock;  // 재고 (기본값 1)
        
        private List<String> imageUids;  // 첨부 이미지 UID 목록
    }

    /**
     * 상품 수정 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        
        @NotBlank(message = "카테고리를 선택해주세요")
        private String category;
        
        @NotBlank(message = "상품명을 입력해주세요")
        private String title;
        
        private String description;
        
        @NotNull(message = "가격을 입력해주세요")
        private Integer price;
        
        @NotNull(message = "재고 수량을 입력해주세요")
        private Integer stockQuantity;
        
        private String status;  // ACTIVE, SOLD_OUT, HIDDEN
        
        private List<String> imageUids;
    }

    /**
     * 구매 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseRequest {
        
        @NotNull(message = "구매 수량을 입력해주세요")
        private Integer quantity;
        
        private String buyerContact;  // 오프라인 장터용 연락처
    }
}
