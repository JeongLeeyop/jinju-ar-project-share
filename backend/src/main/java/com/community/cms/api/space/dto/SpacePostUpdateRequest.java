package com.community.cms.api.space.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 공간 게시글 수정 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostUpdateRequest {

    @Size(max = 500, message = "제목은 500자 이하여야 합니다")
    private String title;

    private String content;

    @JsonProperty("isNotice")
    private Boolean isNotice;

    @JsonProperty("isHidden")
    private Boolean isHidden;

    /**
     * 첨부파일 URL 목록
     */
    private List<String> attachments;
}
