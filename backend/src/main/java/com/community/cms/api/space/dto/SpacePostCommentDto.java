package com.community.cms.api.space.dto;

import com.community.cms.entity.SpacePostComment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 공간 게시글 댓글 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostCommentDto {

    private String uid;
    private String postUid;
    private String userUid;
    private String writer;
    private String authorName;  // writer와 동일 (프론트 호환성)
    private String iconFileUid;  // 작성자 아이콘 파일 UID
    private String content;

    @JsonProperty("isDeleted")
    private boolean isDeleted;

    @JsonProperty("isAuthor")
    private boolean isAuthor;  // 현재 사용자가 작성자인지

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity to DTO
     */
    public static SpacePostCommentDto from(SpacePostComment comment) {
        return SpacePostCommentDto.builder()
                .uid(comment.getUid())
                .postUid(comment.getPost() != null ? comment.getPost().getUid() : null)
                .userUid(comment.getUserUid())
                .writer(comment.getWriter())
                .authorName(comment.getWriter())
                .iconFileUid(null)  // User 정보로 별도 설정 필요
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .isAuthor(false)  // 서비스에서 설정
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * Entity to DTO with user context
     */
    public static SpacePostCommentDto from(SpacePostComment comment, String currentUserUid) {
        SpacePostCommentDto dto = from(comment);
        dto.setAuthor(comment.getUserUid().equals(currentUserUid));
        return dto;
    }

    /**
     * Entity to DTO with user icon
     */
    public static SpacePostCommentDto from(SpacePostComment comment, String currentUserUid, String iconFileUid) {
        SpacePostCommentDto dto = from(comment, currentUserUid);
        dto.setIconFileUid(iconFileUid);
        return dto;
    }
}
