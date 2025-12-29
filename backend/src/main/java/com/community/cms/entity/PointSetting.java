package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 포인트 적립 설정 엔티티
 * 채널별 이벤트에 대한 포인트 적립량을 설정합니다.
 */
@Entity
@Table(name = "point_setting", indexes = {
    @Index(name = "idx_point_setting_channel", columnList = "channel_uid")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_uid", nullable = false, unique = true)
    private String channelUid;

    /** 게시글 작성 시 적립 포인트 */
    @Column(name = "post_create", nullable = false)
    @Builder.Default
    private Integer postCreate = 100;

    /** 댓글 작성 시 적립 포인트 */
    @Column(name = "comment_create", nullable = false)
    @Builder.Default
    private Integer commentCreate = 50;

    /** 좋아요 누르기 시 적립 포인트 */
    @Column(name = "like_give", nullable = false)
    @Builder.Default
    private Integer likeGive = 10;

    /** 출석 체크 시 적립 포인트 */
    @Column(name = "daily_attendance", nullable = false)
    @Builder.Default
    private Integer dailyAttendance = 50;

    /** 장터 상품 등록 시 적립 포인트 */
    @Column(name = "marketplace_create", nullable = false)
    @Builder.Default
    private Integer marketplaceCreate = 100;

    /** 장터 상품 판매 완료 시 적립 포인트 */
    @Column(name = "marketplace_sell", nullable = false)
    @Builder.Default
    private Integer marketplaceSell = 200;

    /** 강좌 수강 완료 시 적립 포인트 */
    @Column(name = "course_complete", nullable = false)
    @Builder.Default
    private Integer courseComplete = 150;

    // ==================== 일일 적립 횟수 제한 (0 = 무제한) ====================
    
    /** 게시글 일일 적립 횟수 제한 */
    @Column(name = "post_daily_limit", nullable = false)
    @Builder.Default
    private Integer postDailyLimit = 10;

    /** 댓글 일일 적립 횟수 제한 */
    @Column(name = "comment_daily_limit", nullable = false)
    @Builder.Default
    private Integer commentDailyLimit = 20;

    /** 좋아요 일일 적립 횟수 제한 */
    @Column(name = "like_daily_limit", nullable = false)
    @Builder.Default
    private Integer likeDailyLimit = 30;

    /** 장터 등록 일일 적립 횟수 제한 */
    @Column(name = "marketplace_create_daily_limit", nullable = false)
    @Builder.Default
    private Integer marketplaceCreateDailyLimit = 5;

    /** 장터 판매 일일 적립 횟수 제한 */
    @Column(name = "marketplace_sell_daily_limit", nullable = false)
    @Builder.Default
    private Integer marketplaceSellDailyLimit = 0;

    /** 강좌 완료 일일 적립 횟수 제한 */
    @Column(name = "course_complete_daily_limit", nullable = false)
    @Builder.Default
    private Integer courseCompleteDailyLimit = 0;

    // ==================== 최소 글자수 제한 ====================
    
    /** 게시글 최소 글자수 */
    @Column(name = "post_min_length", nullable = false)
    @Builder.Default
    private Integer postMinLength = 20;

    /** 댓글 최소 글자수 */
    @Column(name = "comment_min_length", nullable = false)
    @Builder.Default
    private Integer commentMinLength = 5;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
