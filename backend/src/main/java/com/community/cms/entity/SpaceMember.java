package com.community.cms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SpaceMember Entity
 * 공간 멤버 중간 엔티티 (Space와 User 간의 다대다 관계)
 * 복합키 (space_uid, user_uid) 사용
 */
@Entity
@Table(name = "space_member")
@IdClass(SpaceMember.SpaceMemberId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceMember {

    /**
     * 공간 UID (복합키 1)
     */
    @Id
    @Column(name = "space_uid", nullable = false, length = 50)
    private String spaceUid;

    /**
     * 사용자 UID (복합키 2)
     */
    @Id
    @Column(name = "user_uid", nullable = false, length = 50)
    private String userUid;

    /**
     * 가입일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * Space 연관관계
     * Jackson 순환 참조 방지
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_uid", insertable = false, updatable = false)
    private Space space;

    /**
     * User 연관관계
     * Jackson 순환 참조 방지
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uid", insertable = false, updatable = false)
    private User user;

    /**
     * SpaceMember 복합키 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpaceMemberId implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String spaceUid;
        private String userUid;
    }
}
