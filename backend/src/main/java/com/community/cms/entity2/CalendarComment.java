package com.community.cms.entity2;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.community.cms.entity.User;

/**
 * CalendarComment Entity
 * 일정 댓글 엔티티
 */
@Entity
@Table(name = "calendar_comment",
    indexes = {
        @Index(name = "idx_calendar_idx", columnList = "calendar_idx"),
        @Index(name = "idx_user_uid", columnList = "user_uid"),
        @Index(name = "idx_created_at", columnList = "created_at")
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    /**
     * 일정 인덱스
     */
    @Column(name = "calendar_idx", nullable = false)
    private Integer calendarIdx;

    /**
     * 일정 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_idx", insertable = false, updatable = false)
    private Calendar calendar;

    /**
     * 사용자 UID
     */
    @Column(name = "user_uid", nullable = false, length = 50)
    private String userUid;

    /**
     * 사용자 (다대일 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", insertable = false, updatable = false)
    private User user;

    /**
     * 댓글 내용
     */
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    /**
     * 부모 댓글 인덱스 (대댓글용)
     */
    @Column(name = "parent_idx")
    private Long parentIdx;

    /**
     * 삭제 여부
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 생성일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

