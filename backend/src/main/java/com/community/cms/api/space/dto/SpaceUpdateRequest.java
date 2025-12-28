package com.community.cms.api.space.dto;

import lombok.*;

import javax.validation.constraints.Size;

/**
 * Space 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceUpdateRequest {

    @Size(min = 1, max = 200, message = "공간 이름은 1-200자 사이여야 합니다.")
    private String name;

    @Size(max = 5000, message = "공간 설명은 5000자를 초과할 수 없습니다.")
    private String description;

    private String iconUrl;

    private Boolean invitationRequired;

    private Boolean isPublic;

    private Boolean isActive;

    private Integer maxMembers;
}
