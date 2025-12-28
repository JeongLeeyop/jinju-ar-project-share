package com.community.cms.api.marketplace.controller;

import com.community.cms.api.marketplace.dto.MarketplaceProductDto;
import com.community.cms.api.marketplace.service.MarketplaceProductService;
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
 * 장터 상품 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/marketplace/products")
@RequiredArgsConstructor
public class MarketplaceProductController {

    private final MarketplaceProductService productService;
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
     * 상품 등록
     */
    @PostMapping("/{channelUid}")
    public ResponseEntity<?> createProduct(
            @PathVariable String channelUid,
            @Valid @RequestBody MarketplaceProductDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplaceProductDto result = productService.createProduct(
                    request, channelUid, user.getUid(), user.getActualName());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to create product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 메인 장터 상품 목록
     */
    @GetMapping("/main/{channelUid}")
    public ResponseEntity<?> getMainMarketplaceProducts(
            @PathVariable String channelUid,
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            String currentUserUid = "";
            if (userDetails != null) {
                User user = userRepository.findByUserId(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    currentUserUid = user.getUid();
                }
            }
            
            Page<MarketplaceProductDto> result = productService.getMainMarketplaceProducts(
                    channelUid, category, currentUserUid, pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get main marketplace products: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 채널의 모든 상품 목록 (메인 + 오프라인)
     */
    @GetMapping("/channel/{channelUid}")
    public ResponseEntity<?> getAllChannelProducts(
            @PathVariable String channelUid,
            @RequestParam(required = false) String productType,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 100) Pageable pageable) {
        try {
            String currentUserUid = "";
            if (userDetails != null) {
                User user = userRepository.findByUserId(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    currentUserUid = user.getUid();
                }
            }
            
            Page<MarketplaceProductDto> result = productService.getAllChannelProducts(
                    channelUid, productType, currentUserUid, pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get all channel products: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 상품 목록
     */
    @GetMapping("/offline/{offlineMarketplaceUid}")
    public ResponseEntity<?> getOfflineMarketplaceProducts(
            @PathVariable String offlineMarketplaceUid,
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            String currentUserUid = "";
            if (userDetails != null) {
                User user = userRepository.findByUserId(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    currentUserUid = user.getUid();
                }
            }
            
            Page<MarketplaceProductDto> result = productService.getOfflineMarketplaceProducts(
                    offlineMarketplaceUid, category, currentUserUid, pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get offline marketplace products: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 내 등록 상품 목록
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyProducts(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            Page<MarketplaceProductDto> result = productService.getMyProducts(user.getUid(), pageable);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get my products: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{uid}")
    public ResponseEntity<?> getProduct(
            @PathVariable String uid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String currentUserUid = "";
            if (userDetails != null) {
                User user = userRepository.findByUserId(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    currentUserUid = user.getUid();
                }
            }
            
            MarketplaceProductDto result = productService.getProduct(uid, currentUserUid);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 상품 수정
     */
    @PutMapping("/{uid}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String uid,
            @Valid @RequestBody MarketplaceProductDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            MarketplaceProductDto result = productService.updateProduct(uid, request, user.getUid());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to update product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable String uid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            productService.deleteProduct(uid, user.getUid());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete product: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
