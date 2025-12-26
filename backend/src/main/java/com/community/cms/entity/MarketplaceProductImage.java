package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 장터 상품 이미지 엔티티
 */
@Entity
@Table(name = "marketplace_product_image", indexes = {
    @Index(name = "idx_product_uid", columnList = "product_uid"),
    @Index(name = "idx_display_order", columnList = "display_order")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_uid", nullable = false)
    private String productUid;

    @Column(name = "file_uid", nullable = false)
    private String fileUid;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Builder.Default
    @Column(name = "is_thumbnail", nullable = false)
    private boolean isThumbnail = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
