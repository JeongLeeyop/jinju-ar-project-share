package com.community.cms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * SMS 템플릿 엔티티
 */
@Entity
@Table(name = "sms_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 템플릿 이름
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 템플릿 내용
     */
    @Column(nullable = false, length = 2000, columnDefinition = "TEXT")
    private String content;

    /**
     * 채널 UID (null이면 전체 채널 공통 템플릿)
     */
    @Column(name = "channel_uid", length = 50)
    private String channelUid;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 생성일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
