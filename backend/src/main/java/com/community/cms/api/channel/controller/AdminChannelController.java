package com.community.cms.api.channel.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.channel.dto.ChannelDto;
import com.community.cms.api.channel.dto.ChannelSearch;
import com.community.cms.api.channel.service.ChannelService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자용 Channel(커뮤니티) 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/channel")
@AllArgsConstructor
public class AdminChannelController {
    
    private final ChannelService channelService;

    /**
     * 커뮤니티 목록 조회 (관리자)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<Page<ChannelDto.detail>> list(
            @PageableDefault(size = 10, page = 0, direction = Direction.DESC, sort = {"createDate"}) Pageable pageable,
            ChannelSearch search) {
        return ResponseEntity.ok(channelService.adminList(search, pageable));
    }

    /**
     * 커뮤니티 상세 조회 (관리자)
     */
    @GetMapping("/{uid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<ChannelDto.detail> detail(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(channelService.adminDetail(uid));
    }

    /**
     * 커뮤니티 삭제 (관리자)
     */
    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("uid") String uid) {
        channelService.adminDelete(uid);
        return ResponseEntity.ok().build();
    }

    /**
     * 커뮤니티 멤버 목록 조회 (관리자)
     */
    @GetMapping("/{uid}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getMembers(
            @PathVariable("uid") String uid,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(channelService.getChannelMembers(uid, pageable));
    }

    /**
     * 커뮤니티 통계 (관리자)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(channelService.getChannelStats());
    }
}
