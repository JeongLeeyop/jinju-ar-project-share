package com.community.cms.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.community.cms.common.code.ChannelMemberPermissionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채널 멤버 권한 엔티티
 */
@Entity
@Table(name = "channel_member_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelMemberPermission implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 채널 멤버 인덱스 */
    @Column(name = "channel_member_idx", nullable = false)
    private Integer channelMemberIdx;
    
    /** 권한 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false, length = 50)
    private ChannelMemberPermissionType permissionType;
    
    /** 권한 보유 여부 */
    @Column(name = "has_permission", nullable = false)
    private boolean hasPermission;
    
    /** 권한 부여자 UID */
    @Column(name = "granted_by")
    private String grantedBy;
    
    /** 권한 부여일시 */
    @CreationTimestamp
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;
    
    /** 수정일시 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
