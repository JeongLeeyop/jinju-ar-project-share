package com.community.cms.api.space.dto;

import com.community.cms.entity.SpaceInvitation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * SpaceInvitation DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceInvitationDto {

    private String uid;
    private String spaceUid;
    private String spaceName;
    private String inviterUid;
    private String inviterName;
    private String invitedUserUid;
    private String invitedUserName;
    private String status;
    private String statusDisplay;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime respondedAt;
    
    @JsonProperty("canAccept")
    private boolean canAccept;

    /**
     * Entity를 DTO로 변환
     */
    public static SpaceInvitationDto fromEntity(SpaceInvitation invitation) {
        return SpaceInvitationDto.builder()
                .uid(invitation.getUid())
                .spaceUid(invitation.getSpaceUid())
                .inviterUid(invitation.getInviterUid())
                .inviterName(invitation.getInviterName())
                .invitedUserUid(invitation.getInvitedUserUid())
                .invitedUserName(invitation.getInvitedUserName())
                .status(invitation.getStatus().name())
                .statusDisplay(invitation.getStatus().getDisplayName())
                .message(invitation.getMessage())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .respondedAt(invitation.getRespondedAt())
                .canAccept(invitation.canAccept())
                .build();
    }

    /**
     * 공간 이름을 포함하여 Entity를 DTO로 변환
     */
    public static SpaceInvitationDto fromEntity(SpaceInvitation invitation, String spaceName) {
        SpaceInvitationDto dto = fromEntity(invitation);
        dto.setSpaceName(spaceName);
        return dto;
    }
}
