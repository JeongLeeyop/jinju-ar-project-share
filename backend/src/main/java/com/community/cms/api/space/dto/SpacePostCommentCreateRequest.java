package com.community.cms.api.space.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

/**
 * 공간 게시글 댓글 생성 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostCommentCreateRequest {

    @NotBlank(message = "댓글 내용은 필수입니다")
    private String content;
}
