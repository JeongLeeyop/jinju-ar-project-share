package com.community.cms.api.user.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.channel.service.ChannelService;
import com.community.cms.api.space.service.SpaceService;
import com.community.cms.api.activity_log.service.ActivityLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자용 Member(회원) 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/member")
@RequiredArgsConstructor
public class AdminMemberController {

    private final ChannelService channelService;
    private final SpaceService spaceService;
    private final ActivityLogService activityLogService;

    /**
     * 회원 가입 커뮤니티 목록 조회
     */
    @GetMapping("/{uid}/channels")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getMemberChannels(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(channelService.getUserChannels(uid));
    }

    /**
     * 회원 가입 공간 목록 조회
     */
    @GetMapping("/{uid}/spaces")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getMemberSpaces(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(spaceService.getUserSpaces(uid));
    }

    /**
     * 회원 활동 내역 조회
     */
    @GetMapping("/{uid}/activities")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getMemberActivities(
            @PathVariable("uid") String uid,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getUserActivityLog(uid, pageable));
    }
}
