package com.community.cms.api.marketplace.controller;

import com.community.cms.api.marketplace.dto.OfflineMarketplaceDto;
import com.community.cms.api.marketplace.service.OfflineMarketplaceService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;
import com.community.cms.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 오프라인 장터 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/marketplace/offline")
@RequiredArgsConstructor
public class OfflineMarketplaceController {

    private final OfflineMarketplaceService offlineMarketplaceService;
    private final UserRepository userRepository;
    private final AuthenticationUtil authenticationUtil;


    /**
     * 오프라인 장터 생성 (커뮤니티 관리자만)
     */
    @PostMapping("/{channelUid}")
    public ResponseEntity<?> createOfflineMarketplace(
            @PathVariable String channelUid,
            @Valid @RequestBody OfflineMarketplaceDto.CreateRequest request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            OfflineMarketplaceDto result = offlineMarketplaceService.createOfflineMarketplace(
                    request, channelUid, user.getUid());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to create offline marketplace: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 채널의 오프라인 장터 목록 조회
     */
    @GetMapping("/{channelUid}")
    public ResponseEntity<?> getChannelOfflineMarketplaces(
            @PathVariable String channelUid,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        try {
            List<OfflineMarketplaceDto> result = offlineMarketplaceService
                    .getChannelOfflineMarketplaces(channelUid, activeOnly);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get offline marketplaces: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 상세 조회
     */
    @GetMapping("/detail/{uid}")
    public ResponseEntity<?> getOfflineMarketplace(
            @PathVariable String uid,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            String currentUserUid = "";
            if (userDetails != null) {
                User user = userRepository.findByUserId(userDetails.getUsername()).orElse(null);
                if (user != null) {
                    currentUserUid = user.getUid();
                }
            }
            OfflineMarketplaceDto result = offlineMarketplaceService.getOfflineMarketplace(uid, currentUserUid);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get offline marketplace: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 수정
     */
    @PutMapping("/{uid}")
    public ResponseEntity<?> updateOfflineMarketplace(
            @PathVariable String uid,
            @Valid @RequestBody OfflineMarketplaceDto.UpdateRequest request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            OfflineMarketplaceDto result = offlineMarketplaceService.updateOfflineMarketplace(
                    uid, request, user.getUid());
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to update offline marketplace: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * 오프라인 장터 삭제
     */
    @DeleteMapping("/{uid}")
    public ResponseEntity<?> deleteOfflineMarketplace(
            @PathVariable String uid,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            offlineMarketplaceService.deleteOfflineMarketplace(uid, user.getUid());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete offline marketplace: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
