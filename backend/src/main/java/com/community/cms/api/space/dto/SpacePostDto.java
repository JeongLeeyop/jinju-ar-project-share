package com.community.cms.api.space.dto;

import com.community.cms.entity.SpacePost;
import com.community.cms.entity.SpacePostFile;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공간 게시글 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpacePostDto {

    private String uid;
    private String spaceUid;
    private String userUid;
    private String writer;
    private String authorName;  // writer와 동일 (기존 Post DTO와 호환성)
    private String title;
    private String content;
    private int viewCount;
    private int likeCount;
    private int commentCount;

    @JsonProperty("isNotice")
    private boolean isNotice;

    @JsonProperty("isDeleted")
    private boolean isDeleted;

    @JsonProperty("isHidden")
    private boolean isHidden;

    @JsonProperty("isLiked")
    private boolean isLiked;  // 현재 사용자가 좋아요 했는지

    @JsonProperty("isAuthor")
    private boolean isAuthor;  // 현재 사용자가 작성자인지

    private List<String> attachments = new ArrayList<>();  // 첨부파일 UID 목록 (기존 Post와 동일)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity to DTO
     */
    public static SpacePostDto from(SpacePost post) {
        // 첨부파일 UID 목록 추출 (기존 Post와 동일한 방식)
        List<String> attachmentUids = new ArrayList<>();
        if (post.getFileList() != null && !post.getFileList().isEmpty()) {
            attachmentUids = post.getFileList().stream()
                    .filter(file -> file.getFile() != null)
                    .map(file -> file.getFile().getUid())
                    .collect(Collectors.toList());
        }

        return SpacePostDto.builder()
                .uid(post.getUid())
                .spaceUid(post.getSpace() != null ? post.getSpace().getUid() : null)
                .userUid(post.getUserUid())
                .writer(post.getWriter())
                .authorName(post.getWriter())  // writer와 동일한 값
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isNotice(post.isNotice())
                .isDeleted(post.isDeleted())
                .isHidden(post.isHidden())
                .isLiked(false)  // 서비스에서 설정
                .isAuthor(false)  // 서비스에서 설정
                .attachments(attachmentUids)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * Entity to DTO with user context
     */
    public static SpacePostDto from(SpacePost post, String currentUserUid, boolean isLiked) {
        SpacePostDto dto = from(post);
        dto.setLiked(isLiked);
        dto.setAuthor(currentUserUid != null && post.getUserUid().equals(currentUserUid));
        return dto;
    }
}
