package com.community.cms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Space Entity (공간)
 * 커뮤니티 내의 독립적인 공간을 나타냅니다.
 * 각 공간은 게시판 또는 채팅 타입을 가지며, 초대 기반 멤버십과 관리자 권한을 지원합니다.
 */
@Entity
@Table(name = "space", indexes = {
    @Index(name = "idx_channel_uid", columnList = "channel_uid"),
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 100)
    private String uid;

    // 커뮤니티/채널 UID
    @Column(name = "channel_uid", nullable = false, length = 100)
    private String channelUid;

    // 공간 이름
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    // 공간 설명
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 공간 타입 (BOARD: 게시판, CHAT: 채팅)
    @Column(name = "space_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;

    // 공간 관리자 UID
    @Column(name = "admin_uid", nullable = false, length = 100)
    private String adminUid;

    // 공간 아이콘/이미지
    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    // 공간 활성화 여부
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    // 최대 멤버 수 (0이면 제한 없음)
    @Column(name = "max_members")
    @Builder.Default
    private int maxMembers = 0;

    // 현재 멤버 수
    @Column(name = "member_count", nullable = false)
    @Builder.Default
    private int memberCount = 1; // 생성자 포함

    // 초대 필수 여부 (true이면 초대 없이 접근 불가)
    @Column(name = "invitation_required", nullable = false)
    @Builder.Default
    private boolean invitationRequired = true;

    // 공개 여부 (false이면 목록에 표시되지 않음)
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false;

    // 삭제 여부 (소프트 삭제)
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    // 생성일
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 공간 멤버 (일대다 관계)
    // Jackson 순환 참조 방지: Space DTO에서는 memberCount를 사용하고 members는 직렬화하지 않음
    // SpaceMember 중간 엔티티를 통한 관계 (joinedAt 필드 포함)
    @JsonIgnore
    @OneToMany(mappedBy = "space", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SpaceMember> spaceMembers = new HashSet<>();

    // 공간 타입 Enum
    public enum SpaceType {
        BOARD("게시판"),
        CHAT("채팅");

        private final String displayName;

        SpaceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 헬퍼 메서드: 멤버 추가
    public void addMember(User user) {
        SpaceMember spaceMember = SpaceMember.builder()
                .spaceUid(this.uid)
                .userUid(user.getUid())
                .space(this)
                .build();
        this.spaceMembers.add(spaceMember);
        this.memberCount = this.spaceMembers.size();
    }

    // 헬퍼 메서드: 멤버 제거
    public void removeMember(String userUid) {
        this.spaceMembers.removeIf(sm -> sm.getUserUid().equals(userUid));
        this.memberCount = this.spaceMembers.size();
    }

    // 헬퍼 메서드: 사용자가 관리자인지 확인
    public boolean isAdmin(String userUid) {
        return this.adminUid != null && this.adminUid.equals(userUid);
    }

    // 헬퍼 메서드: 사용자가 멤버인지 확인
    public boolean isMember(String userUid) {
        return this.spaceMembers.stream().anyMatch(sm -> sm.getUserUid().equals(userUid));
    }

    // 헬퍼 메서드: 최대 멤버 수 체크
    public boolean canAddMember() {
        return maxMembers == 0 || memberCount < maxMembers;
    }
}
