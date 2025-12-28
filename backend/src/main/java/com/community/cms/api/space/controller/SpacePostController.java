package com.community.cms.api.space.controller;

import com.community.cms.api.space.dto.*;
import com.community.cms.api.space.service.SpacePostService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * SpacePost Controller
 * 공간 게시판 REST API 엔드포인트
 */
@Slf4j
@RestController
@RequestMapping("/api/spaces/{spaceUid}/posts")
@RequiredArgsConstructor
public class SpacePostController {

    private final SpacePostService spacePostService;
    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 검증 및 반환
     * UserDetails에서 username을 가져와 DB에서 User 엔티티 조회
     * @param userDetails @AuthenticationPrincipal로 주입된 UserDetails
     * @return 검증된 User 엔티티 (null 가능 - 공개 공간 접근 시)
     */
    private User validateAndGetCurrentUser(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            // 인증되지 않은 사용자 (공개 공간 접근 가능)
            return null;
        }
        // UserDetails의 username으로 DB에서 User 조회
        return userRepository.findByUserId(userDetails.getUsername())
                .orElse(null);
    }

    /**
     * 인증이 필수인 경우 사용자 검증
     * @param userDetails @AuthenticationPrincipal로 주입된 UserDetails
     * @return 검증된 User 엔티티
     */
    private User requireAuthenticatedUser(UserDetails userDetails) {
        User user = validateAndGetCurrentUser(userDetails);
        if (user == null) {
            log.error("Unauthenticated access attempt detected");
            throw new RuntimeException("인증되지 않은 접근입니다");
        }
        return user;
    }

    /**
     * 게시글 생성
     * POST /api/spaces/{spaceUid}/posts
     */
    @PostMapping
    public ResponseEntity<?> createPost(
            @PathVariable String spaceUid,
            @Valid @RequestBody SpacePostCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            SpacePostDto postDto = spacePostService.createPost(
                    spaceUid,
                    request,
                    user.getUid(),
                    user.getActualName()
            );

            return ResponseEntity.ok(postDto);
        } catch (IllegalStateException e) {
            log.warn("게시글 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 생성 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 조회 (단건)
     * GET /api/spaces/{spaceUid}/posts/{postUid}
     */
    @GetMapping("/{postUid}")
    public ResponseEntity<?> getPost(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = validateAndGetCurrentUser(currentUser);
            String userUid = user != null ? user.getUid() : null;

            SpacePostDto postDto = spacePostService.getPost(postUid, userUid);
            return ResponseEntity.ok(postDto);
        } catch (IllegalArgumentException e) {
            log.warn("게시글 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 접근 거부: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 공간별 게시글 목록 조회
     * GET /api/spaces/{spaceUid}/posts
     */
    @GetMapping
    public ResponseEntity<?> getPostsBySpace(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = validateAndGetCurrentUser(currentUser);
            String userUid = user != null ? user.getUid() : null;

            List<SpacePostDto> posts = spacePostService.getPostsBySpace(spaceUid, userUid);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            log.warn("게시글 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 목록 접근 거부: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 공간별 게시글 페이징 조회
     * GET /api/spaces/{spaceUid}/posts/paged
     */
    @GetMapping("/paged")
    public ResponseEntity<?> getPostsPaged(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal UserDetails currentUser,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        try {
            User user = validateAndGetCurrentUser(currentUser);
            String userUid = user != null ? user.getUid() : null;

            Page<SpacePostDto> posts = spacePostService.getPostsPaged(spaceUid, userUid, pageable);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            log.warn("게시글 페이징 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 페이징 접근 거부: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 페이징 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 페이징 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 검색
     * GET /api/spaces/{spaceUid}/posts/search?keyword={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @PathVariable String spaceUid,
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = validateAndGetCurrentUser(currentUser);
            String userUid = user != null ? user.getUid() : null;

            List<SpacePostDto> posts = spacePostService.searchPosts(spaceUid, keyword, userUid);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            log.warn("게시글 검색 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 검색 접근 거부: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 검색 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 검색 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 수정
     * PUT /api/spaces/{spaceUid}/posts/{postUid}
     */
    @PutMapping("/{postUid}")
    public ResponseEntity<?> updatePost(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @Valid @RequestBody SpacePostUpdateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            SpacePostDto postDto = spacePostService.updatePost(
                    postUid,
                    request,
                    user.getUid(),
                    user.getActualName()
            );

            return ResponseEntity.ok(postDto);
        } catch (IllegalArgumentException e) {
            log.warn("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 수정 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 삭제
     * DELETE /api/spaces/{spaceUid}/posts/{postUid}
     */
    @DeleteMapping("/{postUid}")
    public ResponseEntity<?> deletePost(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            spacePostService.deletePost(postUid, user.getUid(), user.getActualName());
            return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("게시글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("게시글 삭제 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "게시글 삭제 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글 좋아요 토글
     * POST /api/spaces/{spaceUid}/posts/{postUid}/like
     */
    @PostMapping("/{postUid}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            boolean isLiked = spacePostService.toggleLike(postUid, user.getUid());
            String message = isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.";

            return ResponseEntity.ok(Map.of(
                    "message", message,
                    "isLiked", isLiked
            ));
        } catch (IllegalArgumentException e) {
            log.warn("좋아요 토글 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("좋아요 토글 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "좋아요 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 댓글 생성
     * POST /api/spaces/{spaceUid}/posts/{postUid}/comments
     */
    @PostMapping("/{postUid}/comments")
    public ResponseEntity<?> createComment(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @Valid @RequestBody SpacePostCommentCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            SpacePostCommentDto commentDto = spacePostService.createComment(
                    postUid,
                    request,
                    user.getUid(),
                    user.getActualName()
            );

            return ResponseEntity.ok(commentDto);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("댓글 생성 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 생성 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "댓글 생성 중 오류가 발생했습니다."));
        }
    }

    /**
     * 게시글별 댓글 목록 조회
     * GET /api/spaces/{spaceUid}/posts/{postUid}/comments
     */
    @GetMapping("/{postUid}/comments")
    public ResponseEntity<?> getCommentsByPost(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = validateAndGetCurrentUser(currentUser);
            String userUid = user != null ? user.getUid() : null;

            List<SpacePostCommentDto> comments = spacePostService.getCommentsByPost(postUid, userUid);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "댓글 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 댓글 수정
     * PUT /api/spaces/{spaceUid}/posts/{postUid}/comments/{commentUid}
     */
    @PutMapping("/{postUid}/comments/{commentUid}")
    public ResponseEntity<?> updateComment(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @PathVariable String commentUid,
            @Valid @RequestBody SpacePostCommentCreateRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            SpacePostCommentDto commentDto = spacePostService.updateComment(
                    commentUid,
                    request.getContent(),
                    user.getUid(),
                    user.getActualName()
            );

            return ResponseEntity.ok(commentDto);
        } catch (IllegalArgumentException e) {
            log.warn("댓글 수정 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("댓글 수정 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "댓글 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 댓글 삭제
     * DELETE /api/spaces/{spaceUid}/posts/{postUid}/comments/{commentUid}
     */
    @DeleteMapping("/{postUid}/comments/{commentUid}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String spaceUid,
            @PathVariable String postUid,
            @PathVariable String commentUid,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            User user = requireAuthenticatedUser(currentUser);

            spacePostService.deleteComment(commentUid, user.getUid(), user.getActualName());
            return ResponseEntity.ok(Map.of("message", "댓글이 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("댓글 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("댓글 삭제 권한 없음: {}", e.getMessage());
            return ResponseEntity.status(403)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "댓글 삭제 중 오류가 발생했습니다."));
        }
    }
}
