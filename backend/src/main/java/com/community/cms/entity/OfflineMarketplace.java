package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 오프라인 장터 엔티티
 */
@Entity
@Table(name = "offline_marketplace", indexes = {
    @Index(name = "idx_channel_uid", columnList = "channel_uid"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineMarketplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(name = "channel_uid", nullable = false)
    private String channelUid;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
