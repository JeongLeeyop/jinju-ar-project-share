package com.community.cms.api.space.dto;

import com.community.cms.entity.Space;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Space DTO
 * 공간 정보를 전송하기 위한 Data Transfer Object
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceDto {

    private String uid;
    private String channelUid;
    private String channelName;  // 소속 커뮤니티 이름
    private String name;
    private String description;
    private String spaceType;
    private String spaceTypeDisplay;
    private String adminUid;
    private String iconUrl;
    
    @JsonProperty("isActive")
    private boolean isActive;
    
    private int maxMembers;
    private int memberCount;
    private boolean invitationRequired;
    
    @JsonProperty("isPublic")
    private boolean isPublic;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime joinDate;  // 가입일 (회원상세용)
    
    // 현재 사용자 관련 정보
    @JsonProperty("isAdmin")
    private boolean isAdmin;  // 공간 관리자 또는 커뮤니티 관리자 (관리 권한 있음)
    
    @JsonProperty("isSpaceAdmin")
    private boolean isSpaceAdmin;  // 공간 관리자만 true (관리자 뱃지 표시용)
    
    @JsonProperty("isMember")
    private boolean isMember;

    /**
     * Entity를 DTO로 변환
     */
    public static SpaceDto fromEntity(Space space) {
        return SpaceDto.builder()
                .uid(space.getUid())
                .channelUid(space.getChannelUid())
                .name(space.getName())
                .description(space.getDescription())
                .spaceType(space.getSpaceType().name())
                .spaceTypeDisplay(space.getSpaceType().getDisplayName())
                .adminUid(space.getAdminUid())
                .iconUrl(space.getIconUrl())
                .isActive(space.isActive())
                .maxMembers(space.getMaxMembers())
                .memberCount(space.getMemberCount())
                .invitationRequired(space.isInvitationRequired())
                .isPublic(space.isPublic())
                .createdAt(space.getCreatedAt())
                .updatedAt(space.getUpdatedAt())
                .build();
    }

    /**
     * 현재 사용자 정보를 포함하여 Entity를 DTO로 변환
     */
    public static SpaceDto fromEntity(Space space, String currentUserUid) {
        SpaceDto dto = fromEntity(space);
        dto.setAdmin(space.isAdmin(currentUserUid));
        dto.setSpaceAdmin(space.isAdmin(currentUserUid));  // 공간 관리자 여부 (뱃지용)
        dto.setMember(space.isMember(currentUserUid));
        return dto;
    }
}
