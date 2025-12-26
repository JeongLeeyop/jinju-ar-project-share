package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * SpacePostComment Entity
 * 공간 게시글 댓글 엔티티
 */
@Entity
@Table(name = "space_post_comment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostComment {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

    /**
     * 게시글 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_uid", nullable = false)
    private SpacePost post;

    /**
     * 작성자 UID
     */
    @Column(name = "user_uid", nullable = false, length = 50)
    private String userUid;

    /**
     * 작성자 이름
     */
    @Column(nullable = false, length = 100)
    private String writer;

    /**
     * 댓글 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 삭제 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

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
