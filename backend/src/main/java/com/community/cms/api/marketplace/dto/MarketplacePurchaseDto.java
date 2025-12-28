package com.community.cms.api.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 장터 구매 내역 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplacePurchaseDto {

    private String uid;
    private String productUid;
    private String productTitle;
    private String productCategory;
    private String buyerUid;
    private String buyerName;
    private String buyerContact;
    private String sellerUid;
    private String sellerName;
    private Integer quantity;
    private Integer totalPrice;
    private String status;
    private String paymentMethod;
    
    @JsonProperty("isOffline")
    private boolean isOffline;
    
    private LocalDateTime purchasedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    private String thumbnailUid;  // 상품 썸네일

    /**
     * 판매자의 오프라인 구매 처리 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfflineProcessRequest {
        
        @NotNull(message = "구매자 연락처를 입력해주세요")
        private String buyerContact;
        
        @NotNull(message = "차감할 포인트를 입력해주세요")
        private Integer pointAmount;
    }

    /**
     * 구매자의 즉시 구매 요청 DTO (오프라인 장터)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstantPurchaseRequest {
        
        @NotNull(message = "구매 수량을 입력해주세요")
        private Integer quantity;
        
        private String buyerContact;
    }
}
