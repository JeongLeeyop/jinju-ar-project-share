package com.community.cms.api.channel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.channel.dto.ChannelMemberPermissionDto;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.channel.service.ChannelMemberPermissionService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.User;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelMember;
import com.community.cms.oauth.SinghaUser;
import com.community.cms.util.AuthenticationUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 채널 멤버 권한 관리 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/channel-member-permissions")
@RequiredArgsConstructor
public class ChannelMemberPermissionController {
    
    private final ChannelMemberPermissionService permissionService;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final AuthenticationUtil authenticationUtil;
    
    /**
     * 채널 관리자 권한 확인
     */
    private void validateChannelAdmin(String channelUid, User user) {
        log.debug("Validating channel admin - channelUid: {}, userUid: {}", channelUid, user.getUid());
        
        // 1. 채널 조회
        Channel channel = channelRepository.findByUid(channelUid)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        
        log.debug("Channel found - name: {}, adminUid: {}", channel.getName(), channel.getUserUid());
        
        // 2. 채널 관리자인지 확인 (Channel의 userUid가 관리자 UID)
        if (!channel.getUserUid().equals(user.getUid())) {
            log.warn("User {} attempted to access admin function for channel {} but is not admin. Channel admin: {}", 
                     user.getUid(), channelUid, channel.getUserUid());
            throw new RuntimeException("채널 관리자만 이용할 수 있습니다");
        }
        
        log.debug("User {} is confirmed as channel admin", user.getUid());
        
        // 3. 채널 관리자는 채널 멤버 테이블에 없어도 모든 권한을 가짐
        // 채널 멤버 테이블 확인은 선택적으로 처리
        log.debug("Channel admin {} has full access to channel {}", user.getUid(), channelUid);
    }
    
    /**
     * 멤버의 모든 권한 조회
     */
    @GetMapping("/member/{channelMemberIdx}")
    public ResponseEntity<?> getMemberPermissions(
            @PathVariable Integer channelMemberIdx,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // 멤버의 채널 정보 확인
            ChannelMember member = channelMemberRepository.findById(channelMemberIdx)
                    .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
            
            // 관리자 권한 확인
            validateChannelAdmin(member.getChannelUid(), user);
            
            ChannelMemberPermissionDto.ListResponse response = 
                    permissionService.getMemberPermissions(channelMemberIdx);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to get member permissions: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 권한 생성 또는 업데이트
     */
    @PostMapping
    public ResponseEntity<?> createOrUpdatePermission(
            @Valid @RequestBody ChannelMemberPermissionDto.CreateRequest request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // 멤버의 채널 정보 확인
            ChannelMember member = channelMemberRepository.findById(request.getChannelMemberIdx())
                    .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
            
            // 관리자 권한 확인
            validateChannelAdmin(member.getChannelUid(), user);
            
            ChannelMemberPermissionDto.Detail response = 
                    permissionService.createOrUpdatePermission(request, user.getUid());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to create/update permission: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 멤버 권한 일괄 설정
     */
    @PutMapping("/bulk")
    public ResponseEntity<?> bulkUpdatePermissions(
            @Valid @RequestBody ChannelMemberPermissionDto.BulkUpdateRequest request,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // 멤버의 채널 정보 확인
            ChannelMember member = channelMemberRepository.findById(request.getChannelMemberIdx())
                    .orElseThrow(() -> new RuntimeException("채널 멤버를 찾을 수 없습니다"));
            
            // 관리자 권한 확인
            validateChannelAdmin(member.getChannelUid(), user);
            
            ChannelMemberPermissionDto.ListResponse response = 
                    permissionService.bulkUpdatePermissions(request, user.getUid());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to bulk update permissions: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 권한 확인
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkPermission(
            @RequestParam Integer channelMemberIdx,
            @RequestParam String permissionType,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            ChannelMemberPermissionType type = ChannelMemberPermissionType.fromCode(permissionType);
            ChannelMemberPermissionDto.CheckResponse response = 
                    permissionService.checkPermission(channelMemberIdx, type);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to check permission: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 사용자 UID로 권한 확인
     */
    @GetMapping("/check-by-user")
    public ResponseEntity<?> checkPermissionByUserUid(
            @RequestParam String channelUid,
            @RequestParam String permissionType,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // domain → uid 변환 (channelUid가 domain일 수 있음)
            String actualChannelUid = getChannelUidByDomainOrUid(channelUid);
            
            ChannelMemberPermissionType type = ChannelMemberPermissionType.fromCode(permissionType);
            ChannelMemberPermissionDto.CheckResponse response = 
                    permissionService.checkPermissionByUserUid(user.getUid(), actualChannelUid, type);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to check permission by user: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 권한 삭제
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<?> deletePermission(
            @PathVariable Long permissionId,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // TODO: 권한 삭제 전 관리자 권한 확인 로직 추가
            
            permissionService.deletePermission(permissionId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "권한이 삭제되었습니다");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to delete permission: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 모든 권한 타입 조회
     */
    @GetMapping("/types")
    public ResponseEntity<List<ChannelMemberPermissionType>> getAllPermissionTypes() {
        return ResponseEntity.ok(permissionService.getAllPermissionTypes());
    }
    
    /**
     * 현재 사용자의 모든 권한 조회 (채널 기준)
     * 프론트엔드에서 권한 기반 UI 제어에 사용
     */
    @GetMapping("/my-permissions")
    public ResponseEntity<?> getMyPermissions(
            @RequestParam String channelUid,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // domain → uid 변환 (channelUid가 domain일 수 있음)
            String actualChannelUid = getChannelUidByDomainOrUid(channelUid);
            
            // 현재 사용자의 모든 권한 조회
            Map<String, Object> result = permissionService.getAllUserPermissions(user.getUid(), actualChannelUid);
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to get my permissions: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * domain 또는 uid를 실제 channel uid로 변환
     */
    private String getChannelUidByDomainOrUid(String domainOrUid) {
        // 먼저 domain으로 조회 시도
        return channelRepository.findByDomain(domainOrUid)
                .map(Channel::getUid)
                .orElse(domainOrUid);  // domain이 아니면 uid로 간주
    }
    
    /**
     * 채널의 모든 승인된 회원에게 기본 권한을 일괄 부여
     * (기존 회원들에게 기본 권한이 없는 경우 사용)
     */
    @PostMapping("/grant-default-permissions")
    public ResponseEntity<?> grantDefaultPermissionsToAllMembers(
            @RequestParam String channelUid,
            @AuthenticationPrincipal SinghaUser userDetails) {
        try {
            User user = authenticationUtil.validateAndGetCurrentUser(userDetails);
            
            // domain → uid 변환
            String actualChannelUid = getChannelUidByDomainOrUid(channelUid);
            
            // 관리자 권한 확인
            validateChannelAdmin(actualChannelUid, user);
            
            // 해당 채널의 모든 승인된 회원 조회 (추방되지 않은 회원만)
            List<ChannelMember> members = channelMemberRepository.findByChannelUidAndApprovalStatusAndBanned(actualChannelUid, true, false);
            
            int grantedCount = 0;
            
            // 기본 권한 목록 (게시판 이용, 장터 이용)
            ChannelMemberPermissionType[] defaultPermissions = {
                ChannelMemberPermissionType.POST_USE,        // 게시판 이용 (등록/수정/삭제 통합)
                ChannelMemberPermissionType.MARKETPLACE_USE  // 장터 이용
            };
            
            for (ChannelMember member : members) {
                for (ChannelMemberPermissionType permType : defaultPermissions) {
                    ChannelMemberPermissionDto.CreateRequest request = 
                        new ChannelMemberPermissionDto.CreateRequest(
                            member.getIdx(),
                            permType,
                            true
                        );
                    permissionService.createOrUpdatePermission(request, user.getUid());
                }
                grantedCount++;
            }
            
            log.info("Granted default permissions to {} members in channel {}", grantedCount, actualChannelUid);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", grantedCount + "명의 회원에게 기본 권한을 부여했습니다.");
            result.put("grantedCount", grantedCount);
            
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            log.error("Failed to grant default permissions: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
