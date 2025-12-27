package com.community.cms.api.space.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 공간 게시글 생성 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostCreateRequest {

    @Size(max = 500, message = "제목은 500자 이하여야 합니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @JsonProperty("isNotice")
    @Builder.Default
    private boolean isNotice = false;

    /**
     * 첨부파일 URL 목록
     */
    @Builder.Default
    private List<String> attachments = new ArrayList<>();
}
