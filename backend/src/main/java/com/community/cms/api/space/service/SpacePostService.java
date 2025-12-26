package com.community.cms.api.space.service;

import com.community.cms.api.activity.repository.ActivityRepository;
import com.community.cms.api.attached_file.repository.AttachedFileRepository;
import com.community.cms.api.space.dto.*;
import com.community.cms.api.space.repository.*;
import com.community.cms.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SpacePost Service
 * 공간 게시글 비즈?�스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpacePostService {

    private final SpacePostRepository spacePostRepository;
    private final SpacePostCommentRepository spacePostCommentRepository;
    private final SpacePostLikeRepository spacePostLikeRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceMemberRepository spaceMemberRepository;
    private final ActivityRepository activityRepository;
    private final AttachedFileRepository attachedFileRepository;

    /**
     * 게시글 ?�성
     */
    @Transactional
    public SpacePostDto createPost(String spaceUid, SpacePostCreateRequest request, 
                                   String userUid, String userName) {
        // 공간 존재 ?�인
        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // 멤버 권한 ?�인 (공개 공간???�니�?멤버?�야 ??
        if (!space.isPublic() && !isSpaceMember(spaceUid, userUid)) {
            throw new IllegalStateException("공간 멤버�?게시글???�성?????�습?�다");
        }

        // 공�??�항?� 관리자�??�성 가??
        if (request.isNotice() && !space.getAdminUid().equals(userUid)) {
            throw new IllegalStateException("공�??�항?� 관리자�??�성?????�습?�다");
        }

        // 게시글 생성
        SpacePost post = SpacePost.builder()
                .space(space)
                .userUid(userUid)
                .writer(userName)
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(request.isNotice())
                .viewCount(0)
                .isDeleted(false)
                .isHidden(false)
                .fileList(new ArrayList<>())  // ✅ 빈 리스트로 초기화
                .build();

        // 첨부파일 처리
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            log.debug("Processing {} attachments for post", request.getAttachments().size());
            
            for (String fileUrl : request.getAttachments()) {
                log.debug("Processing attachment: {}", fileUrl);
                
                // URL에서 파일 UID 추출
                String fileUid = extractFileUidFromUrl(fileUrl);
                
                if (fileUid != null) {
                    log.debug("Extracted file UID: {}", fileUid);
                    
                    // 파일 존재 확인
                    attachedFileRepository.findById(fileUid).ifPresentOrElse(
                        file -> {
                            log.debug("Found file in DB: {} ({})", file.getOriginalName(), file.getUid());
                            
                            SpacePostFile postFile = SpacePostFile.builder()
                                    .post(post)  // ✅ 엔티티 객체 설정
                                    .file(file)  // ✅ 엔티티 객체 설정
                                    .build();
                            post.getFileList().add(postFile);
                            
                            log.debug("Added file to post.fileList: {}", postFile.getUid());
                        },
                        () -> log.warn("File not found in DB with UID: {}", fileUid)
                    );
                } else {
                    log.warn("Could not extract file UID from: {}", fileUrl);
                }
            }
            
            log.debug("Total files added to post: {}", post.getFileList().size());
        } else {
            log.debug("No attachments to process");
        }

        SpacePost savedPost = spacePostRepository.save(post);

        // ?�동 로그 ?�??
        logActivity(
                "POST_CREATED",
                userUid,
                userName,
                space.getChannelUid(),
                spaceUid,
                String.format("%s?�이 게시글???�성?�습?�다: %s", userName, request.getTitle())
        );

        return SpacePostDto.from(savedPost, userUid, false);
    }

    /**
     * 게시글 조회 (?�건)
     */
    @Transactional
    public SpacePostDto getPost(String postUid, String currentUserUid) {
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        // ?��? 처리??게시글?� ?�성?��? 관리자�?�????�음
        if (post.isHidden()) {
            Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(
                    post.getSpace() != null ? post.getSpace().getUid() : null)
                    .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다"));

            if (currentUserUid == null || 
                (!post.getUserUid().equals(currentUserUid) && !space.getAdminUid().equals(currentUserUid))) {
                throw new IllegalStateException("숨김 처리된 게시글입니다");
            }
        }

        // 조회??증�?
        spacePostRepository.incrementViewCount(postUid);

        // 좋아???��? ?�인
        boolean isLiked = currentUserUid != null && 
                         spacePostLikeRepository.existsByPost_UidAndUserUid(postUid, currentUserUid);

        return SpacePostDto.from(post, currentUserUid, isLiked);
    }

    /**
     * 공간의 게시글 목록 조회
     * 공지사항은 상단 3개까지만 고정되고, 나머지는 일반 게시글로 처리
     */
    @Transactional(readOnly = true)
    public List<SpacePostDto> getPostsBySpace(String spaceUid, String currentUserUid) {
        // 공간 존재 확인
        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간을 찾을 수 없습니다"));

        // 비공개 공간은 멤버만 조회 가능
        if (!space.isPublic() && (currentUserUid == null || !isSpaceMember(spaceUid, currentUserUid))) {
            throw new IllegalStateException("공간 멤버만 게시글을 조회할 수 있습니다");
        }

        // 공지사항과 일반 게시글 분리 조회
        List<SpacePost> noticePosts = spacePostRepository
                .findBySpace_UidAndIsNoticeAndIsDeletedFalseOrderByCreatedAtDesc(spaceUid, true);
        
        List<SpacePost> normalPosts = spacePostRepository
                .findBySpace_UidAndIsNoticeAndIsDeletedFalseOrderByCreatedAtDesc(spaceUid, false);

        // 공지사항 상단 3개 고정
        List<SpacePostDto> result = new ArrayList<>();
        
        // 1. 상단 고정 공지사항 (최대 3개)
        int fixedNoticeCount = Math.min(3, noticePosts.size());
        for (int i = 0; i < fixedNoticeCount; i++) {
            SpacePost post = noticePosts.get(i);
            if (!post.isHidden() || 
                (currentUserUid != null && 
                 (post.getUserUid().equals(currentUserUid) || space.getAdminUid().equals(currentUserUid)))) {
                boolean isLiked = currentUserUid != null && 
                                spacePostLikeRepository.existsByPost_UidAndUserUid(post.getUid(), currentUserUid);
                result.add(SpacePostDto.from(post, currentUserUid, isLiked));
            }
        }
        
        // 2. 일반 게시글 + 3개 이후 공지사항 (공지사항 배지는 유지)
        List<SpacePost> remainingPosts = new ArrayList<>();
        
        // 3개 이후 공지사항 추가
        for (int i = fixedNoticeCount; i < noticePosts.size(); i++) {
            remainingPosts.add(noticePosts.get(i));
        }
        
        // 일반 게시글 추가
        remainingPosts.addAll(normalPosts);
        
        // 생성일 기준 정렬 (최신순)
        remainingPosts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
        
        for (SpacePost post : remainingPosts) {
            if (!post.isHidden() || 
                (currentUserUid != null && 
                 (post.getUserUid().equals(currentUserUid) || space.getAdminUid().equals(currentUserUid)))) {
                boolean isLiked = currentUserUid != null && 
                                spacePostLikeRepository.existsByPost_UidAndUserUid(post.getUid(), currentUserUid);
                result.add(SpacePostDto.from(post, currentUserUid, isLiked));
            }
        }

        return result;
    }

    /**
     * 공간�?게시글 ?�이�?조회
     */
    @Transactional(readOnly = true)
    public Page<SpacePostDto> getPostsPaged(String spaceUid, String currentUserUid, Pageable pageable) {
        // 공간 존재 ?�인
        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // 비공�?공간?� 멤버�?조회 가??
        if (!space.isPublic() && (currentUserUid == null || !isSpaceMember(spaceUid, currentUserUid))) {
            throw new IllegalStateException("공간 멤버�?게시글??조회?????�습?�다");
        }

        Page<SpacePost> posts = spacePostRepository.findBySpace_UidAndIsDeletedFalse(spaceUid, pageable);

        return posts.map(post -> {
            boolean isLiked = currentUserUid != null && 
                            spacePostLikeRepository.existsByPost_UidAndUserUid(post.getUid(), currentUserUid);
            return SpacePostDto.from(post, currentUserUid, isLiked);
        });
    }

    /**
     * 게시글 검??
     */
    @Transactional(readOnly = true)
    public List<SpacePostDto> searchPosts(String spaceUid, String keyword, String currentUserUid) {
        // 공간 존재 ?�인
        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(spaceUid)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // 비공�?공간?� 멤버�?검??가??
        if (!space.isPublic() && (currentUserUid == null || !isSpaceMember(spaceUid, currentUserUid))) {
            throw new IllegalStateException("공간 멤버�?게시글??검?�할 ???�습?�다");
        }

        List<SpacePost> posts = spacePostRepository.searchBySpaceUid(spaceUid, keyword);

        return posts.stream()
                .filter(post -> !post.isHidden() || 
                              (currentUserUid != null && 
                               (post.getUserUid().equals(currentUserUid) || space.getAdminUid().equals(currentUserUid))))
                .map(post -> {
                    boolean isLiked = currentUserUid != null && 
                                    spacePostLikeRepository.existsByPost_UidAndUserUid(post.getUid(), currentUserUid);
                    return SpacePostDto.from(post, currentUserUid, isLiked);
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 ?�정
     */
    @Transactional
    public SpacePostDto updatePost(String postUid, SpacePostUpdateRequest request, 
                                   String userUid, String userName) {
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(
                post.getSpace() != null ? post.getSpace().getUid() : null)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // ?�성???�는 관리자�??�정 가??
        boolean isAdmin = space.getAdminUid().equals(userUid);
        boolean isAuthor = post.getUserUid().equals(userUid);

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("게시글 ?�정 권한???�습?�다");
        }

        // 필드 업데이트
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getIsNotice() != null) {
            // 공�??�항 ?�정?� 관리자�?가??
            if (request.getIsNotice() && !isAdmin) {
                throw new IllegalStateException("공�??�항 ?�정?� 관리자�??????�습?�다");
            }
            post.setNotice(request.getIsNotice());
        }
        if (request.getIsHidden() != null && isAdmin) {
            // ?��? 처리??관리자�?가??
            post.setHidden(request.getIsHidden());
        }

        // 첨부파일 업데이트
        if (request.getAttachments() != null) {
            log.debug("Updating attachments. Current count: {}, New count: {}", 
                     post.getFileList().size(), request.getAttachments().size());
            
            // ✅ 기존 컬렉션을 clear()한 후 새 항목 추가 (orphanRemoval 오류 방지)
            post.getFileList().clear();
            
            // 새로운 파일 추가
            for (String fileUrl : request.getAttachments()) {
                log.debug("Processing attachment for update: {}", fileUrl);
                
                // URL에서 파일 UID 추출
                String fileUid = extractFileUidFromUrl(fileUrl);
                
                if (fileUid != null) {
                    log.debug("Extracted file UID: {}", fileUid);
                    
                    // 파일 존재 확인
                    attachedFileRepository.findById(fileUid).ifPresentOrElse(
                        file -> {
                            log.debug("Found file in DB: {} ({})", file.getOriginalName(), file.getUid());
                            
                            SpacePostFile postFile = SpacePostFile.builder()
                                    .post(post)  // ✅ 엔티티 객체 설정
                                    .file(file)  // ✅ 엔티티 객체 설정
                                    .build();
                            post.getFileList().add(postFile);
                            
                            log.debug("Added file to post.fileList: {}", postFile.getUid());
                        },
                        () -> log.warn("File not found in DB with UID: {}", fileUid)
                    );
                } else {
                    log.warn("Could not extract file UID from: {}", fileUrl);
                }
            }
            
            log.debug("Total files after update: {}", post.getFileList().size());
        }

        SpacePost updatedPost = spacePostRepository.save(post);

        // ?�동 로그 ?�??
        logActivity(
                "POST_UPDATED",
                userUid,
                userName,
                space.getChannelUid(),
                space.getUid(),
                String.format("%s?�이 게시글???�정?�습?�다: %s", userName, post.getTitle())
        );

        boolean isLiked = spacePostLikeRepository.existsByPost_UidAndUserUid(postUid, userUid);
        return SpacePostDto.from(updatedPost, userUid, isLiked);
    }

    /**
     * 게시글 ??�� (?�프????��)
     */
    @Transactional
    public void deletePost(String postUid, String userUid, String userName) {
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(
                post.getSpace() != null ? post.getSpace().getUid() : null)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // ?�성???�는 관리자�???�� 가??
        if (!post.getUserUid().equals(userUid) && !space.getAdminUid().equals(userUid)) {
            throw new IllegalStateException("게시글 ??�� 권한???�습?�다");
        }

        post.setDeleted(true);
        spacePostRepository.save(post);

        // ?�동 로그 ?�??
        logActivity(
                "POST_DELETED",
                userUid,
                userName,
                space.getChannelUid(),
                space.getUid(),
                String.format("%s?�이 게시글????��?�습?�다: %s", userName, post.getTitle())
        );
    }

    /**
     * 게시글 좋아???��?
     */
    @Transactional
    public boolean toggleLike(String postUid, String userUid) {
        // 게시글 존재 ?�인
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        // 좋아??존재 ?�인
        var existingLike = spacePostLikeRepository.findByPost_UidAndUserUid(postUid, userUid);

        if (existingLike.isPresent()) {
            // ?��? 좋아?�한 경우 -> 좋아??취소
            spacePostLikeRepository.delete(existingLike.get());
            return false;
        } else {
            // 좋아??추�?
            SpacePostLike like = SpacePostLike.builder()
                    .post(post)
                    .userUid(userUid)
                    .build();
            spacePostLikeRepository.save(like);
            return true;
        }
    }

    /**
     * ?��? ?�성
     */
    @Transactional
    public SpacePostCommentDto createComment(String postUid, SpacePostCommentCreateRequest request,
                                            String userUid, String userName) {
        // 게시글 존재 ?�인
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(
                post.getSpace() != null ? post.getSpace().getUid() : null)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // 비공�?공간?� 멤버�??��? ?�성 가??
        if (!space.isPublic() && !isSpaceMember(space.getUid(), userUid)) {
            throw new IllegalStateException("공간 멤버�??��????�성?????�습?�다");
        }

        SpacePostComment comment = SpacePostComment.builder()
                .post(post)
                .userUid(userUid)
                .writer(userName)
                .content(request.getContent())
                .isDeleted(false)
                .build();

        SpacePostComment savedComment = spacePostCommentRepository.save(comment);

        // ?�동 로그 ?�??
        logActivity(
                "COMMENT_CREATED",
                userUid,
                userName,
                space.getChannelUid(),
                space.getUid(),
                String.format("%s?�이 ?��????�성?�습?�다", userName)
        );

        return SpacePostCommentDto.from(savedComment, userUid);
    }

    /**
     * 게시글�??��? 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SpacePostCommentDto> getCommentsByPost(String postUid, String currentUserUid) {
        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(postUid)
                .orElseThrow(() -> new IllegalArgumentException("게시글??찾을 ???�습?�다"));

        List<SpacePostComment> comments = spacePostCommentRepository
                .findByPost_UidAndIsDeletedFalseOrderByCreatedAtAsc(postUid);

        return comments.stream()
                .map(comment -> SpacePostCommentDto.from(comment, currentUserUid))
                .collect(Collectors.toList());
    }

    /**
     * ?��? ?�정
     */
    @Transactional
    public SpacePostCommentDto updateComment(String commentUid, String content, 
                                            String userUid, String userName) {
        SpacePostComment comment = spacePostCommentRepository.findByUidAndIsDeletedFalse(commentUid)
                .orElseThrow(() -> new IllegalArgumentException("?��???찾을 ???�습?�다"));

        // ?�성?�만 ?�정 가??
        if (!comment.getUserUid().equals(userUid)) {
            throw new IllegalStateException("?��? ?�정 권한???�습?�다");
        }

        comment.setContent(content);
        SpacePostComment updatedComment = spacePostCommentRepository.save(comment);

        return SpacePostCommentDto.from(updatedComment, userUid);
    }

    /**
     * ?��? ??�� (?�프????��)
     */
    @Transactional
    public void deleteComment(String commentUid, String userUid, String userName) {
        SpacePostComment comment = spacePostCommentRepository.findByUidAndIsDeletedFalse(commentUid)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

        SpacePost post = spacePostRepository.findByUidAndIsDeletedFalse(
                comment.getPost() != null ? comment.getPost().getUid() : null)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        Space space = spaceRepository.findByUidAndIsActiveTrueAndIsDeletedFalse(
                post.getSpace() != null ? post.getSpace().getUid() : null)
                .orElseThrow(() -> new IllegalArgumentException("공간??찾을 ???�습?�다"));

        // ?�성???�는 관리자�???�� 가??
        if (!comment.getUserUid().equals(userUid) && !space.getAdminUid().equals(userUid)) {
            throw new IllegalStateException("?��? ??�� 권한???�습?�다");
        }

        comment.setDeleted(true);
        spacePostCommentRepository.save(comment);

        // ?�동 로그 ?�??
        logActivity(
                "COMMENT_DELETED",
                userUid,
                userName,
                space.getChannelUid(),
                space.getUid(),
                String.format("%s?�이 ?��?????��?�습?�다", userName)
        );
    }

    /**
     * 공간 멤버 ?��? ?�인
     */
    private boolean isSpaceMember(String spaceUid, String userUid) {
        if (userUid == null) {
            return false;
        }
        return spaceMemberRepository.existsBySpaceUidAndUserUid(spaceUid, userUid);
    }

    /**
     * ?�동 로그 ?�??
     */
    private void logActivity(String type, String userUid, String userName,
                            String channelUid, String spaceUid, String description) {
        try {
            Activity activity = Activity.builder()
                    .type(type)
                    .userUid(userUid)
                    .userName(userName)
                    .channelUid(channelUid)
                    .spaceUid(spaceUid)
                    .description(description)
                    .build();

            activityRepository.save(activity);
        } catch (Exception e) {
            log.error("Failed to log activity: {}", e.getMessage(), e);
            // ?�동 로그 ?�패가 메인 로직???�향??주�? ?�도�??�외�??�음
        }
    }

    /**
     * 파일 URL 또는 UID에서 파일 UID 추출
     * 다양한 형식 지원:
     * - 직접 UID: "abc-123-def"
     * - 경로 포함: "/uploads/post/abc-123-def"
     * - 쿼리 파라미터: "/api/file?uid=abc-123-def"
     */
    private String extractFileUidFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return null;
        }
        
        fileUrl = fileUrl.trim();
        
        log.debug("Extracting file UID from: {}", fileUrl);
        
        // 1. 쿼리 파라미터에서 uid 추출 (?uid=xxx)
        if (fileUrl.contains("?uid=")) {
            String[] parts = fileUrl.split("\\?uid=");
            if (parts.length > 1) {
                String uid = parts[1].split("&")[0];
                log.debug("Extracted UID from query param: {}", uid);
                return uid;
            }
        }
        
        // 2. URL 경로에서 마지막 세그먼트 추출
        if (fileUrl.contains("/")) {
            // 슬래시로 분리하여 마지막 부분 추출
            String[] segments = fileUrl.split("/");
            String lastSegment = segments[segments.length - 1];
            // 쿼리 파라미터 제거
            if (lastSegment.contains("?")) {
                lastSegment = lastSegment.split("\\?")[0];
            }
            log.debug("Extracted UID from path: {}", lastSegment);
            return lastSegment;
        }
        
        // 3. 이미 UID 형식인 경우 (UUID 패턴)
        log.debug("Using as-is UID: {}", fileUrl);
        return fileUrl;
    }
}
