package com.community.cms.api.calendar.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.community.cms.api.attached_file.service.AttachedFileService;
import com.community.cms.api.calendar.dto.CalendarCommentDto;
import com.community.cms.api.calendar.dto.CalendarDto;
import com.community.cms.api.calendar.dto.CalendarLikeDto;
import com.community.cms.api.calendar.dto.CalendarSearch;
import com.community.cms.api.calendar.dto.ScheduleParticipantDto;
import com.community.cms.api.calendar.service.CalendarCommentService;
import com.community.cms.api.calendar.service.CalendarLikeService;
import com.community.cms.api.calendar.service.CalendarService;
import com.community.cms.entity.AttachedFile;
import com.community.cms.entity2.Calendar;
import com.community.cms.oauth.SinghaUser;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/client/calendar")
@AllArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;
    private final AttachedFileService attachedFileService;
    private final CalendarLikeService likeService;
    private final CalendarCommentService commentService;

    @GetMapping
    public ResponseEntity<List<CalendarDto.list>> list(@AuthenticationPrincipal SinghaUser authUser,
            CalendarSearch search) {
        return ResponseEntity.ok(calendarService.list(authUser, search));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<CalendarDto.list>> list(@PageableDefault(size = 10, page = 0) Pageable pageable,
            @AuthenticationPrincipal SinghaUser authUser, CalendarSearch search) {
        return ResponseEntity.ok(calendarService.list(authUser, search, pageable));
    }

    @GetMapping("{idx}")
    public ResponseEntity<CalendarDto.detail> detail(@AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(calendarService.detail(authUser, idx));
    }

    @PostMapping
    public ResponseEntity add(@AuthenticationPrincipal SinghaUser authUser, @RequestBody CalendarDto.add addDto) {
        calendarService.add(authUser, addDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{idx}")
    public ResponseEntity delete(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar) {
        calendarService.delete(authUser, Calendar);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{idx}")
    public ResponseEntity update(@AuthenticationPrincipal SinghaUser authUser, @PathVariable("idx") Calendar Calendar,
            @RequestBody CalendarDto.update updateDto) {
        calendarService.update(authUser, Calendar, updateDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<AttachedFile> fileUpload(MultipartFile file) {
        return ResponseEntity.ok(attachedFileService.save(file, "calendar"));
    }

    // ============ Schedule Participation Endpoints ============

    /**
     * 일정 참여
     */
    @PostMapping("/{idx}/join")
    public ResponseEntity<CalendarDto.JoinResult> joinEvent(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(calendarService.joinEvent(authUser, idx));
    }

    /**
     * 일정 참여 취소 (24시간 전까지만 가능)
     */
    @DeleteMapping("/{idx}/cancel")
    public ResponseEntity<Void> cancelParticipation(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        calendarService.cancelParticipation(authUser, idx);
        return ResponseEntity.ok().build();
    }

    /**
     * 일정 참여자 목록 조회
     */
    @GetMapping("/{idx}/participants")
    public ResponseEntity<List<ScheduleParticipantDto.ListItem>> getParticipants(
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(calendarService.getParticipants(idx));
    }

    /**
     * 참여자에게 포인트 지급 (earn 타입 일정, 일정 작성자만 가능)
     */
    @PostMapping("/participant/{participantIdx}/grant-point")
    public ResponseEntity<Void> grantPointToParticipant(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("participantIdx") Integer participantIdx) {
        calendarService.grantPointToParticipant(authUser, participantIdx);
        return ResponseEntity.ok().build();
    }

    // ============ Like Endpoints ============

    /**
     * 좋아요 토글 (추가/취소)
     */
    @PostMapping("/{idx}/like")
    public ResponseEntity<CalendarLikeDto.ToggleResult> toggleLike(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(likeService.toggleLike(authUser, idx));
    }

    /**
     * 좋아요 정보 조회
     */
    @GetMapping("/{idx}/like")
    public ResponseEntity<CalendarLikeDto.LikeInfo> getLikeInfo(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(likeService.getLikeInfo(authUser, idx));
    }

    // ============ Comment Endpoints ============

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/{idx}/comments")
    public ResponseEntity<CalendarCommentDto.ListResponse> getComments(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        return ResponseEntity.ok(commentService.getComments(authUser, idx));
    }

    /**
     * 댓글 작성
     */
    @PostMapping("/{idx}/comments")
    public ResponseEntity<CalendarCommentDto.ListItem> createComment(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx,
            @RequestBody CalendarCommentDto.CreateReq request) {
        request.setCalendarIdx(idx);
        return ResponseEntity.ok(commentService.createComment(authUser, request));
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentIdx}")
    public ResponseEntity<CalendarCommentDto.ListItem> updateComment(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("commentIdx") Long commentIdx,
            @RequestBody CalendarCommentDto.UpdateReq request) {
        return ResponseEntity.ok(commentService.updateComment(authUser, commentIdx, request));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentIdx}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("commentIdx") Long commentIdx) {
        commentService.deleteComment(authUser, commentIdx);
        return ResponseEntity.ok().build();
    }
    
    // ============ 내 일정 관리 Endpoints ============
    
    /**
     * 내가 등록한 일정 목록 조회
     */
    @GetMapping("/my/registered")
    public ResponseEntity<List<CalendarDto.MyScheduleItem>> getMyRegisteredSchedules(
            @AuthenticationPrincipal SinghaUser authUser,
            @org.springframework.web.bind.annotation.RequestParam("channelUid") String channelUid) {
        return ResponseEntity.ok(calendarService.getMyRegisteredSchedules(authUser, channelUid));
    }
    
    /**
     * 내가 참여한 일정 목록 조회
     */
    @GetMapping("/my/participated")
    public ResponseEntity<List<CalendarDto.MyScheduleItem>> getMyParticipatedSchedules(
            @AuthenticationPrincipal SinghaUser authUser,
            @org.springframework.web.bind.annotation.RequestParam("channelUid") String channelUid) {
        return ResponseEntity.ok(calendarService.getMyParticipatedSchedules(authUser, channelUid));
    }
    
    /**
     * 내가 등록한 일정 취소
     */
    @DeleteMapping("/my/{idx}")
    public ResponseEntity<Void> cancelMySchedule(
            @AuthenticationPrincipal SinghaUser authUser,
            @PathVariable("idx") Integer idx) {
        calendarService.cancelMySchedule(authUser, idx);
        return ResponseEntity.ok().build();
    }
}
