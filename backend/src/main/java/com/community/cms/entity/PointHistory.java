package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 포인트 히스토리 엔티티
 */
@Entity
@Table(name = "point_history", indexes = {
    @Index(name = "idx_user_uid", columnList = "user_uid"),
    @Index(name = "idx_channel_uid", columnList = "channel_uid"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_user_channel", columnList = "user_uid, channel_uid")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uid", nullable = false)
    private String userUid;

    @Column(name = "channel_uid", nullable = false)
    private String channelUid;

    @Column(name = "point_type", nullable = false, length = 50)
    private String pointType;  // POST, LIKE, COMMENT, ATTENDANCE, LECTURE, SCHEDULE, MARKETPLACE

    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount;  // 양수: 적립, 음수: 차감

    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance;  // 현재 잔액

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "reference_id")
    private String referenceId;  // 참조 ID (게시글 UID, 댓글 UID 등)

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
