package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Activity Entity
 * 활동 로그 엔티티
 */
@Entity
@Table(name = "activity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 활동 타입 (예: SPACE_CREATED, POST_CREATED, MEMBER_ADDED 등)
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * 사용자 UID
     */
    @Column(nullable = false, length = 50)
    private String userUid;

    /**
     * 사용자 이름
     */
    @Column(nullable = false, length = 100)
    private String userName;

    /**
     * 채널 UID
     */
    @Column(length = 50)
    private String channelUid;

    /**
     * 공간 UID (선택)
     */
    @Column(length = 50)
    private String spaceUid;

    /**
     * 활동 설명
     */
    @Column(length = 1000)
    private String description;

    /**
     * 생성일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
