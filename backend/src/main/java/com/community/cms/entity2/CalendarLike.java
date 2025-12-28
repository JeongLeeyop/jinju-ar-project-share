package com.community.cms.entity2;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.community.cms.entity.User;

/**
 * CalendarLike Entity
 * 일정 좋아요 엔티티
 */
@Entity
@Table(name = "calendar_like", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"calendar_idx", "user_uid"}),
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
public class CalendarLike implements Serializable {

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
     * 생성일시
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

