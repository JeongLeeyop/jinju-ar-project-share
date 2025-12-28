package com.community.cms.entity2;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Index;

import org.hibernate.annotations.CreationTimestamp;

import com.community.cms.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 일정 참여자 엔티티
 */
@Entity
@Table(name = "schedule_participant", indexes = {
        @Index(name = "idx_calendar_user", columnList = "calendar_idx, user_uid"),
        @Index(name = "idx_calendar_status", columnList = "calendar_idx, status"),
        @Index(name = "idx_user_status", columnList = "user_uid, status")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ScheduleParticipant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "calendar_idx", nullable = false)
    private Integer calendarIdx;

    @Column(name = "user_uid", nullable = false)
    private String userUid;

    @Column(name = "channel_uid", nullable = false)
    private String channelUid;

    /**
     * 사용 또는 획득할 포인트 (paid: 사용량, earn: 획득 예정량)
     */
    @Column(name = "points_amount")
    private Integer pointsAmount;

    /**
     * 참여 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.REGISTERED;

    /**
     * 포인트 지급 완료 여부 (earn 타입 전용)
     */
    @Column(name = "point_granted")
    @Builder.Default
    private Boolean pointGranted = false;

    /**
     * 포인트 지급일
     */
    @Column(name = "point_granted_at")
    private LocalDateTime pointGrantedAt;

    /**
     * 포인트 지급자 UID
     */
    @Column(name = "point_granted_by")
    private String pointGrantedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Relationships
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "calendar_idx", insertable = false, updatable = false)
    private Calendar calendar;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_uid", insertable = false, updatable = false)
    private User user;

    /**
     * 참여 상태 열거형
     */
    public enum ParticipantStatus {
        REGISTERED("참여중"),
        CANCELLED("취소됨"),
        COMPLETED("완료");

        private final String description;

        ParticipantStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
