package com.community.cms.api.sms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.cms.api.sms.dto.SmsHistoryDto;
import com.community.cms.api.sms.dto.SmsSendRequest;
import com.community.cms.api.sms.dto.SmsSendResponse;
import com.community.cms.api.sms.service.SmsService;
import com.community.cms.entity.User;
import com.community.cms.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 관리자용 SMS 전송 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/sms")
@RequiredArgsConstructor
public class AdminSmsController {

    private final SmsService smsService;
    private final UserRepository userRepository;

    /**
     * SMS 전송
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SmsSendResponse> sendSms(@RequestBody SmsSendRequest request) {
        try {
            SmsSendResponse response = smsService.sendSms(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("SMS 전송 실패: ", e);
            return ResponseEntity.ok(SmsSendResponse.builder()
                    .isSuccess(false)
                    .message("SMS 전송에 실패했습니다: " + e.getMessage())
                    .resultCode(-1)
                    .build());
        }
    }

    /**
     * SMS 전송 내역 조회
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<List<SmsHistoryDto>> getHistory(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false, defaultValue = "30") Integer limitDay) {
        List<SmsHistoryDto> history = smsService.getSmsHistory(page, pageSize, startDate, limitDay);
        return ResponseEntity.ok(history);
    }

    /**
     * SMS 잔여 건수 조회
     */
    @GetMapping("/remain")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<?> getRemainCount() {
        return ResponseEntity.ok(smsService.getRemainCount());
    }

    /**
     * SMS 전송 대상 회원 목록 조회
     */
    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'CREATOR')")
    public ResponseEntity<List<User>> getMemberList(
            @RequestParam(required = false) String keyword) {
        List<User> members;
        if (keyword != null && !keyword.isEmpty()) {
            members = userRepository.findByActualNameContainingOrConcatNumberContaining(keyword, keyword);
        } else {
            members = userRepository.findAll();
        }
        return ResponseEntity.ok(members);
    }
}
