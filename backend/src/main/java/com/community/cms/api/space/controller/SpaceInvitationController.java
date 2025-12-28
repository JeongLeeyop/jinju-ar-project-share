package com.community.cms.api.space.controller;

import com.community.cms.api.space.dto.SpaceInvitationDto;
import com.community.cms.api.space.dto.SpaceInvitationRequest;
import com.community.cms.api.space.service.SpaceInvitationService;
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
import java.util.List;
import java.util.Map;

/**
 * SpaceInvitation Controller
 * 공간 초대 관련 REST API 엔드포인트
 */
@Slf4j
@RestController
@RequestMapping("/api/space-invitations")
@RequiredArgsConstructor
public class SpaceInvitationController {

    private final SpaceInvitationService invitationService;
    private final UserRepository userRepository;
    private final AuthenticationUtil authenticationUtil;
    /**
     * 초대 생성 (관리자만)
     * POST /api/space-invitations
     */
    @PostMapping
    public ResponseEntity<SpaceInvitationDto> createInvitation(
            @Valid @RequestBody SpaceInvitationRequest request,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        
        SpaceInvitationDto invitation = invitationService.createInvitation(
                request,
                user.getUid(),
                user.getActualName()
        );
        
        return ResponseEntity.ok(invitation);
    }

    /**
     * 초대 수락
     * POST /api/space-invitations/{invitationUid}/accept
     */
    @PostMapping("/{invitationUid}/accept")
    public ResponseEntity<Map<String, String>> acceptInvitation(
            @PathVariable String invitationUid,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        invitationService.acceptInvitation(invitationUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "초대를 수락했습니다."));
    }

    /**
     * 초대 거절
     * POST /api/space-invitations/{invitationUid}/reject
     */
    @PostMapping("/{invitationUid}/reject")
    public ResponseEntity<Map<String, String>> rejectInvitation(
            @PathVariable String invitationUid,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        invitationService.rejectInvitation(invitationUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "초대를 거절했습니다."));
    }

    /**
     * 초대 취소 (관리자만)
     * DELETE /api/space-invitations/{invitationUid}
     */
    @DeleteMapping("/{invitationUid}")
    public ResponseEntity<Map<String, String>> cancelInvitation(
            @PathVariable String invitationUid,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        invitationService.cancelInvitation(invitationUid, user.getUid());
        return ResponseEntity.ok(Map.of("message", "초대를 취소했습니다."));
    }

    /**
     * 내가 받은 초대 목록
     * GET /api/space-invitations/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<SpaceInvitationDto>> getMyInvitations(
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        List<SpaceInvitationDto> invitations = invitationService.getMyInvitations(user.getUid());
        return ResponseEntity.ok(invitations);
    }

    /**
     * 내가 받은 대기중인 초대 목록
     * GET /api/space-invitations/my/pending
     */
    @GetMapping("/my/pending")
    public ResponseEntity<List<SpaceInvitationDto>> getMyPendingInvitations(
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        List<SpaceInvitationDto> invitations = invitationService.getMyPendingInvitations(user.getUid());
        return ResponseEntity.ok(invitations);
    }

    /**
     * 공간의 초대 목록 (관리자만)
     * GET /api/space-invitations/space/{spaceUid}
     */
    @GetMapping("/space/{spaceUid}")
    public ResponseEntity<List<SpaceInvitationDto>> getSpaceInvitations(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        List<SpaceInvitationDto> invitations = invitationService.getSpaceInvitations(
                spaceUid, user.getUid());
        return ResponseEntity.ok(invitations);
    }

    /**
     * 공간의 대기중인 초대 목록 (관리자만)
     * GET /api/space-invitations/space/{spaceUid}/pending
     */
    @GetMapping("/space/{spaceUid}/pending")
    public ResponseEntity<List<SpaceInvitationDto>> getSpacePendingInvitations(
            @PathVariable String spaceUid,
            @AuthenticationPrincipal SinghaUser currentUser) {
        
        User user = authenticationUtil.validateAndGetCurrentUser(currentUser);
        List<SpaceInvitationDto> invitations = invitationService.getSpacePendingInvitations(
                spaceUid, user.getUid());
        return ResponseEntity.ok(invitations);
    }
}
