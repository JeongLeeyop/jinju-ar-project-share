package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * SpacePostLike Entity
 * 공간 게시글 좋아요 엔티티
 */
@Entity
@Table(name = "space_post_like")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_uid", nullable = false)
    private SpacePost post;

    /**
     * 사용자 UID
     */
    @Column(name = "user_uid", nullable = false, length = 50)
    private String userUid;

    /**
     * 생성일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
