package com.community.cms.api.calendar.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.api.activity_log.dto.ActivityLogDto;
import com.community.cms.api.activity_log.service.ActivityLogService;
import com.community.cms.api.calendar.dto.CalendarCommentDto;
import com.community.cms.api.calendar.repository.CalendarCommentRepository;
import com.community.cms.api.calendar.repository.CalendarRepository;
import com.community.cms.api.channel.service.ChannelMemberPermissionService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.exception.NotFoundException;
import com.community.cms.common.exception.code.NotFound;
import com.community.cms.entity.ActivityLog;
import com.community.cms.entity.User;
import com.community.cms.entity2.Calendar;
import com.community.cms.entity2.CalendarComment;
import com.community.cms.oauth.SinghaUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 일정 댓글 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarCommentService {

    private final CalendarCommentRepository commentRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final ChannelMemberPermissionService permissionService;

    /**
     * 댓글 목록 조회
     */
    public CalendarCommentDto.ListResponse getComments(SinghaUser authUser, Integer calendarIdx) {
        List<CalendarComment> comments = commentRepository.findByCalendarIdxAndNotDeleted(calendarIdx);
        String currentUserUid = authUser.getUser().getUid();

        List<CalendarCommentDto.ListItem> items = comments.stream()
                .map(comment -> toListItem(comment, currentUserUid))
                .collect(Collectors.toList());

        int totalCount = commentRepository.countByCalendarIdxAndNotDeleted(calendarIdx);

        return CalendarCommentDto.ListResponse.builder()
                .comments(items)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 댓글 생성
     */
    @Transactional
    public CalendarCommentDto.ListItem createComment(SinghaUser authUser, CalendarCommentDto.CreateReq request) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        Calendar calendar = calendarRepository.findById(request.getCalendarIdx())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));

        CalendarComment comment = CalendarComment.builder()
                .calendarIdx(request.getCalendarIdx())
                .userUid(user.getUid())
                .content(request.getContent())
                .parentIdx(request.getParentIdx())
                .isDeleted(false)
                .build();

        CalendarComment saved = commentRepository.save(comment);

        // Log activity
        activityLogService.logActivity(ActivityLogDto.CreateReq.builder()
                .userUid(user.getUid())
                .userName(user.getActualName())
                .channelUid(calendar.getChannelUid())
                .activityType(ActivityLog.ActivityType.COMMENT_CREATED)
                .description("'" + calendar.getTitle() + "' 일정에 댓글을 작성하였습니다")
                .relatedUid(String.valueOf(request.getCalendarIdx()))
                .relatedName(calendar.getTitle())
                .targetUserUid(calendar.getWriterUid())
                .build());

        log.info("Calendar comment created: calendarIdx={}, userUid={}, commentIdx={}",
                request.getCalendarIdx(), user.getUid(), saved.getIdx());

        return toListItem(saved, user.getUid());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CalendarCommentDto.ListItem updateComment(
            SinghaUser authUser, 
            Long commentIdx, 
            CalendarCommentDto.UpdateReq request) {
        
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        CalendarComment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 일정의 채널 UID 조회
        Calendar calendar = calendarRepository.findById(comment.getCalendarIdx())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        String channelUid = calendar.getChannelUid();

        // Check ownership or admin
        boolean isOwner = comment.getUserUid().equals(user.getUid());
        boolean isChannelAdmin = permissionService.isChannelAdmin(user.getUid(), channelUid);
        
        if (!isOwner && !isChannelAdmin) {
            throw new RuntimeException("댓글 작성자 또는 커뮤니티 관리자만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
        CalendarComment updated = commentRepository.save(comment);

        log.info("Calendar comment updated: commentIdx={}, userUid={}", commentIdx, user.getUid());

        return toListItem(updated, user.getUid());
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteComment(SinghaUser authUser, Long commentIdx) {
        User user = userRepository.findById(authUser.getUser().getUid())
                .orElseThrow(() -> new NotFoundException(NotFound.USER));

        CalendarComment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 일정의 채널 UID 조회
        Calendar calendar = calendarRepository.findById(comment.getCalendarIdx())
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        String channelUid = calendar.getChannelUid();

        // Check ownership or admin
        boolean isOwner = comment.getUserUid().equals(user.getUid());
        boolean isChannelAdmin = permissionService.isChannelAdmin(user.getUid(), channelUid);
        
        if (!isOwner && !isChannelAdmin) {
            throw new RuntimeException("댓글 작성자 또는 커뮤니티 관리자만 삭제할 수 있습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);

        log.info("Calendar comment deleted: commentIdx={}, userUid={}", commentIdx, user.getUid());
    }

    /**
     * CalendarComment를 ListItem DTO로 변환
     */
    private CalendarCommentDto.ListItem toListItem(CalendarComment comment, String currentUserUid) {
        User user = userRepository.findById(comment.getUserUid()).orElse(null);

        return CalendarCommentDto.ListItem.builder()
                .idx(comment.getIdx())
                .calendarIdx(comment.getCalendarIdx())
                .userUid(comment.getUserUid())
                .userName(user != null ? user.getActualName() : "Unknown")
                .userProfileImage(user != null ? user.getIconFileUid() : null)
                .content(comment.getContent())
                .parentIdx(comment.getParentIdx())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isOwner(comment.getUserUid().equals(currentUserUid))
                .build();
    }
}

