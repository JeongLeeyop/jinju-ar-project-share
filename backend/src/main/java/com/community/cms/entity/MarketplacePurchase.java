package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 장터 구매 내역 엔티티
 */
@Entity
@Table(name = "marketplace_purchase", indexes = {
    @Index(name = "idx_product_uid", columnList = "product_uid"),
    @Index(name = "idx_buyer_uid", columnList = "buyer_uid"),
    @Index(name = "idx_seller_uid", columnList = "seller_uid"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_purchased_at", columnList = "purchased_at")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketplacePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(name = "product_uid", nullable = false)
    private String productUid;

    @Column(name = "buyer_uid", nullable = false)
    private String buyerUid;

    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;

    @Column(name = "buyer_contact", length = 100)
    private String buyerContact;

    @Column(name = "seller_uid", nullable = false)
    private String sellerUid;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "PENDING";  // PENDING, COMPLETED, CANCELLED, REFUNDED

    @Builder.Default
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod = "POINT";  // POINT, OFFLINE

    @Builder.Default
    @Column(name = "is_offline", nullable = false)
    private boolean isOffline = false;

    @CreationTimestamp
    @Column(name = "purchased_at", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
