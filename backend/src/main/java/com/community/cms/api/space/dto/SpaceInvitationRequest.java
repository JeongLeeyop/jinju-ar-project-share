package com.community.cms.api.space.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Space 초대 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceInvitationRequest {

    @NotBlank(message = "공간 UID는 필수입니다.")
    private String spaceUid;

    @NotBlank(message = "초대할 사용자 UID는 필수입니다.")
    private String invitedUserUid;

    @Size(max = 1000, message = "초대 메시지는 1000자를 초과할 수 없습니다.")
    private String message;

    // 만료일 (일 단위, 기본 7일)
    @Builder.Default
    private int expiresInDays = 7;
}

/**
 * 다중 사용자 초대 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class SpaceBulkInvitationRequest {

    @NotBlank(message = "공간 UID는 필수입니다.")
    private String spaceUid;

    private List<String> invitedUserUids;

    @Size(max = 1000, message = "초대 메시지는 1000자를 초과할 수 없습니다.")
    private String message;

    @Builder.Default
    private int expiresInDays = 7;
}
