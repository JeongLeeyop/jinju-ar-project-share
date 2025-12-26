package com.community.cms.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * SpaceInvitation Entity (공간 초대)
 * 사용자를 공간에 초대하기 위한 초대장 엔티티입니다.
 */
@Entity
@Table(name = "space_invitation", indexes = {
    @Index(name = "idx_space_uid", columnList = "space_uid"),
    @Index(name = "idx_invited_user_uid", columnList = "invited_user_uid"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceInvitation {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 100)
    private String uid;

    // 공간 UID
    @Column(name = "space_uid", nullable = false, length = 100)
    private String spaceUid;

    // 초대한 사용자 UID
    @Column(name = "inviter_uid", nullable = false, length = 100)
    private String inviterUid;

    // 초대한 사용자 이름
    @Column(name = "inviter_name", length = 100)
    private String inviterName;

    // 초대받은 사용자 UID
    @Column(name = "invited_user_uid", nullable = false, length = 100)
    private String invitedUserUid;

    // 초대받은 사용자 이름
    @Column(name = "invited_user_name", length = 100)
    private String invitedUserName;

    // 초대 상태 (PENDING: 대기, ACCEPTED: 수락, REJECTED: 거절, EXPIRED: 만료)
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    // 초대 메시지
    @Column(name = "message", length = 1000)
    private String message;

    // 초대 생성일
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 만료일 (기본 7일)
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 응답일
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // 초대 상태 Enum
    public enum InvitationStatus {
        PENDING("대기중"),
        ACCEPTED("수락됨"),
        REJECTED("거절됨"),
        EXPIRED("만료됨");

        private final String displayName;

        InvitationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 헬퍼 메서드: 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt) && status == InvitationStatus.PENDING;
    }

    // 헬퍼 메서드: 수락 가능 여부 확인
    public boolean canAccept() {
        return status == InvitationStatus.PENDING && !isExpired();
    }

    // 헬퍼 메서드: 초대 수락
    public void accept() {
        if (canAccept()) {
            this.status = InvitationStatus.ACCEPTED;
            this.respondedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("초대를 수락할 수 없습니다.");
        }
    }

    // 헬퍼 메서드: 초대 거절
    public void reject() {
        if (status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.REJECTED;
            this.respondedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("초대를 거절할 수 없습니다.");
        }
    }

    // 헬퍼 메서드: 초대 만료 처리
    public void expire() {
        if (status == InvitationStatus.PENDING) {
            this.status = InvitationStatus.EXPIRED;
        }
    }
}
