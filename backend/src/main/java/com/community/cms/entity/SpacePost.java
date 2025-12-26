package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SpacePost Entity
 * 공간 게시글 엔티티
 */
@Entity
@Table(name = "space_post")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePost {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String uid;

    /**
     * 공간 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_uid", nullable = false)
    private Space space;

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
     * 제목
     */
    @Column(nullable = false, length = 500)
    private String title;

    /**
     * 내용
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 조회수
     */
    @Column(nullable = false)
    @Builder.Default
    private int viewCount = 0;

    /**
     * 공지 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isNotice = false;

    /**
     * 삭제 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    /**
     * 숨김 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean isHidden = false;

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

    /**
     * 댓글 목록
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "post_uid", insertable = false, updatable = false)
    @Builder.Default
    private List<SpacePostComment> comments = new ArrayList<>();

    /**
     * 첨부파일 목록 (양방향 관계)
     * mappedBy: SpacePostFile의 post 필드를 통해 관계 관리
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("viewOrder ASC")
    @Builder.Default
    private List<SpacePostFile> fileList = new ArrayList<>();

    /**
     * 좋아요 수 (Formula)
     */
    @Formula("(SELECT COUNT(*) FROM space_post_like spl WHERE spl.post_uid = uid)")
    private int likeCount;

    /**
     * 댓글 수 (Formula)
     */
    @Formula("(SELECT COUNT(*) FROM space_post_comment spc WHERE spc.post_uid = uid AND spc.is_deleted = false)")
    private int commentCount;
}
