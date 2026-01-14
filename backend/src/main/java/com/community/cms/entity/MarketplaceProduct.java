package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 장터 상품 엔티티
 */
@Entity
@Table(name = "marketplace_product", indexes = {
    @Index(name = "idx_channel_uid", columnList = "channel_uid"),
    @Index(name = "idx_offline_marketplace_uid", columnList = "offline_marketplace_uid"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_seller_uid", columnList = "seller_uid"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(name = "channel_uid", nullable = false)
    private String channelUid;
    
    @Transient
    private String channelName;  // 조인 결과 저장용

    @Column(name = "offline_marketplace_uid")
    private String offlineMarketplaceUid;  // NULL이면 메인 장터

    @Column(nullable = false, length = 50)
    private String category;  // SALE, SHARE, REQUEST

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Integer price = 0;

    @Builder.Default
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 1;

    @Column(length = 200)
    private String location;  // 거래 장소

    @Column(name = "seller_uid", nullable = false)
    private String sellerUid;

    @Column(name = "seller_name", nullable = false, length = 100)
    private String sellerName;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";  // ACTIVE, TRADING, SOLD_OUT, HIDDEN

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
