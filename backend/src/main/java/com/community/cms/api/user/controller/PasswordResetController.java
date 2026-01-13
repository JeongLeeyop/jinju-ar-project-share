package com.community.cms.api.user.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.user.dto.PasswordDto;
import com.community.cms.api.user.service.PasswordResetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 비밀번호 찾기/재설정 컨트롤러 (SMS 임시비밀번호 발급 방식)
 */
@Slf4j
@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * 임시 비밀번호 발급 및 SMS 발송
     */
    @PostMapping("/temp-password")
    public ResponseEntity<PasswordDto.TempPasswordResponse> requestTempPassword(
            @Valid @RequestBody PasswordDto.TempPasswordRequest request) {
        log.info("임시 비밀번호 발급 요청: email={}", request.getEmail());
        PasswordDto.TempPasswordResponse result = passwordResetService.requestTempPassword(request);
        return ResponseEntity.ok(result);
    }

    /**
     * 이메일(아이디) 찾기
     */
    @PostMapping("/find-email")
    public ResponseEntity<PasswordDto.FindEmailResponse> findEmail(
            @Valid @RequestBody PasswordDto.FindEmailRequest request) {
        log.info("이메일 찾기 요청: actualName={}", request.getActualName());
        PasswordDto.FindEmailResponse result = passwordResetService.findEmail(request);
        return ResponseEntity.ok(result);
    }
}
