package com.community.cms.api.marketplace.controller;

import com.community.cms.api.marketplace.dto.MarketplaceProductDto;
import com.community.cms.api.marketplace.dto.MarketplacePurchaseDto;
import com.community.cms.api.marketplace.service.MarketplacePurchaseService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 장터 구매 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/marketplace/purchases")
@RequiredArgsConstructor
public class MarketplacePurchaseController {

    private final MarketplacePurchaseService purchaseService;
    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 조회
     */
    private User validateAndGetCurrentUser(UserDetails userDetails) {
        if (userDetails == null || userDetails.getUsername() == null) {
            log.error("Unauthenticated access attempt detected");
            throw new RuntimeException("인증되지 않은 접근입니다");
        }
        return userRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    /**
     * 상품 구매 (메인 장터 - 포인트 결제)
     */
    @PostMapping("/{productUid}")
    public ResponseEntity<?> purchaseProduct(
            @PathVariable String productUid,
            @Valid @RequestBody MarketplaceProductDto.PurchaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.purchaseProduct(
                    productUid, request, user.getUid(), user.getActualName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to purchase product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 구매 처리 (판매자가 직접 처리)
     */
    @PostMapping("/{productUid}/offline/{purchaseUid}")
    public ResponseEntity<?> processOfflinePurchase(
            @PathVariable String productUid,
            @PathVariable String purchaseUid,
            @Valid @RequestBody MarketplacePurchaseDto.OfflineProcessRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.processOfflinePurchase(
                    productUid, purchaseUid, request, user.getUid());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to process offline purchase: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 상품 직접 포인트 차감 (판매자가 회원번호로 처리)
     */
    @PostMapping("/{productUid}/deduct-point")
    public ResponseEntity<?> deductPointForOfflineProduct(
            @PathVariable String productUid,
            @Valid @RequestBody MarketplacePurchaseDto.OfflineDeductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User seller = validateAndGetCurrentUser(userDetails);
            purchaseService.deductPointForOfflineProduct(
                    productUid, request, seller.getUid(), seller.getActualName());
            return ResponseEntity.ok(Map.of("message", "포인트가 차감되었습니다"));
        } catch (RuntimeException e) {
            log.error("Failed to deduct point for offline product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 즉시 구매 (구매자가 직접 구매 - 새로운 API)
     */
    @PostMapping("/{productUid}/offline/instant")
    public ResponseEntity<?> instantOfflinePurchase(
            @PathVariable String productUid,
            @Valid @RequestBody MarketplacePurchaseDto.InstantPurchaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.instantOfflinePurchase(
                    productUid, request, user.getUid(), user.getActualName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to instant purchase: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 내 구매 내역 조회
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyPurchases(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            Page<MarketplacePurchaseDto> result = purchaseService.getMyPurchases(user.getUid(), pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get my purchases: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 내 구매 내역 조회 (특정 채널, 온라인/오프라인 필터)
     */
    @GetMapping("/my/{channelDomain}")
    public ResponseEntity<?> getMyPurchasedProducts(
            @PathVariable String channelDomain,
            @RequestParam(required = false) String marketplaceType,  // "online", "offline", null(전체)
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            Page<MarketplacePurchaseDto> result = purchaseService.getMyPurchasedProducts(
                    channelDomain, marketplaceType, user.getUid(), pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get my purchased products: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 상품별 구매 내역 조회 (판매자용)
     */
    @GetMapping("/product/{productUid}")
    public ResponseEntity<?> getProductPurchases(
            @PathVariable String productUid,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            Page<MarketplacePurchaseDto> result = purchaseService.getProductPurchases(
                    productUid, user.getUid(), pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get product purchases: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 거래 시작 (구매자가 거래 시작 - 포인트는 확정 시 차감)
     */
    @PostMapping("/{productUid}/start-trade")
    public ResponseEntity<?> startTrade(
            @PathVariable String productUid,
            @Valid @RequestBody MarketplacePurchaseDto.StartTradeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.startTrade(
                    productUid, request, user.getUid(), user.getActualName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to start trade: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 거래 완료 (구매자가 포인트 지급 확정)
     */
    @PostMapping("/{purchaseUid}/complete")
    public ResponseEntity<?> completeTrade(
            @PathVariable String purchaseUid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.completeTrade(purchaseUid, user.getUid());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to complete trade: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 거래 취소 (구매자 또는 판매자가 취소)
     */
    @PostMapping("/{purchaseUid}/cancel")
    public ResponseEntity<?> cancelTrade(
            @PathVariable String purchaseUid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            purchaseService.cancelTrade(purchaseUid, user.getUid());
            return ResponseEntity.ok(Map.of("message", "거래가 취소되었습니다"));
        } catch (RuntimeException e) {
            log.error("Failed to cancel trade: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * REQUEST 상품 지원
     */
    @PostMapping("/{productUid}/apply")
    public ResponseEntity<?> applyForRequest(
            @PathVariable String productUid,
            @Valid @RequestBody MarketplacePurchaseDto.ApplyForRequestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplacePurchaseDto result = purchaseService.applyForRequest(
                    productUid, request, user.getUid(), user.getActualName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to apply for request: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
