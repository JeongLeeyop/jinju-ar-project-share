package com.community.cms.api.space.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.space.dto.SpaceDto;
import com.community.cms.api.space.service.SpaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 관리자용 Space(공간) 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/space")
@RequiredArgsConstructor
public class AdminSpaceController {

    private final SpaceService spaceService;

    /**
     * 공간 목록 조회 (관리자)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<Page<SpaceDto>> list(
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false) String spaceType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(spaceService.adminList(channelUid, spaceType, keyword, pageable));
    }

    /**
     * 공간 상세 조회 (관리자)
     */
    @GetMapping("/{uid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<SpaceDto> detail(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(spaceService.adminDetail(uid));
    }

    /**
     * 커뮤니티별 공간 목록 조회 (관리자)
     */
    @GetMapping("/channel/{channelUid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<Page<SpaceDto>> getByChannel(
            @PathVariable("channelUid") String channelUid,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(spaceService.adminListByChannel(channelUid, pageable));
    }

    /**
     * 공간 멤버 목록 조회 (관리자)
     */
    @GetMapping("/{uid}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getMembers(
            @PathVariable("uid") String uid,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(spaceService.getSpaceMembers(uid, pageable));
    }

    /**
     * 공간 삭제 (관리자)
     */
    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("uid") String uid) {
        spaceService.adminDelete(uid);
        return ResponseEntity.ok().build();
    }
}
