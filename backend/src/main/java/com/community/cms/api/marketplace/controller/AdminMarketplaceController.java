package com.community.cms.api.marketplace.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.marketplace.service.MarketplaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 관리자용 Marketplace(장터) 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/marketplace")
@RequiredArgsConstructor
public class AdminMarketplaceController {

    private final MarketplaceService marketplaceService;

    /**
     * 장터 상품 목록 조회 (관리자)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> list(
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(marketplaceService.adminList(channelUid, status, keyword, pageable));
    }

    /**
     * 장터 상품 상세 조회 (관리자)
     */
    @GetMapping("/{uid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> detail(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(marketplaceService.adminDetail(uid));
    }

    /**
     * 커뮤니티별 장터 상품 조회 (관리자)
     */
    @GetMapping("/channel/{channelUid}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getByChannel(
            @PathVariable("channelUid") String channelUid,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(marketplaceService.adminListByChannel(channelUid, pageable));
    }

    /**
     * 장터 통계 조회 (관리자)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(marketplaceService.getStats());
    }

    /**
     * 장터 매출 현황 조회 (관리자)
     */
    @GetMapping("/sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getSales(
            @RequestParam(required = false) String channelUid,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(marketplaceService.getSalesReport(channelUid, startDate, endDate));
    }
}
