package com.community.cms.api.space.dto;

import com.community.cms.entity.Space;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Space 생성 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceCreateRequest {

    @NotBlank(message = "채널 UID는 필수입니다.")
    private String channelUid;

    @NotBlank(message = "공간 이름은 필수입니다.")
    @Size(min = 1, max = 200, message = "공간 이름은 1-200자 사이여야 합니다.")
    private String name;

    @Size(max = 5000, message = "공간 설명은 5000자를 초과할 수 없습니다.")
    private String description;

    @NotNull(message = "공간 타입은 필수입니다.")
    private Space.SpaceType spaceType;

    private String iconUrl;

    @Builder.Default
    private boolean invitationRequired = true;

    @JsonProperty("isPublic")
    @Builder.Default
    private boolean isPublic = false;

    @Builder.Default
    private int maxMembers = 0;
}
