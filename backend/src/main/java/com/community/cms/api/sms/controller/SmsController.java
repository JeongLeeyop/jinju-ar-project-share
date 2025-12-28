package com.community.cms.api.sms.controller;

import com.community.cms.api.sms.dto.SmsHistoryDto;
import com.community.cms.api.sms.dto.SmsSendRequest;
import com.community.cms.api.sms.dto.SmsSendResponse;
import com.community.cms.api.sms.service.SmsService;
import com.community.cms.entity.User;
import com.community.cms.api.user.repository.UserRepository;
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

@Slf4j
@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {
    
    private final SmsService smsService;
    private final UserRepository userRepository;
    
    /**
     * Get current authenticated user from UserDetails
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
     * 문자 발송
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendSms(
            @Valid @RequestBody SmsSendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
            log.info("SMS 발송 요청 - 사용자: {}, 수신자 수: {}", 
                    user.getActualName(), request.getReceivers().size());
            
            SmsSendResponse response = smsService.sendSms(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", response.getMessage());
                errorResponse.put("resultCode", response.getResultCode());
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (RuntimeException e) {
            log.error("SMS 발송 실패: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 전송 내역 조회
     */
    @GetMapping("/history")
    public ResponseEntity<?> getSmsHistory(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "30") Integer pageSize,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) Integer limitDay,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
            List<SmsHistoryDto> histories = smsService.getSmsHistory(page, pageSize, startDate, limitDay);
            
            return ResponseEntity.ok(histories);
            
        } catch (RuntimeException e) {
            log.error("SMS 내역 조회 실패: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 발송 가능 건수 조회
     */
    @GetMapping("/remain")
    public ResponseEntity<?> getRemainCount(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = validateAndGetCurrentUser(userDetails);
            
            Map<String, Object> remain = smsService.getRemainCount();
            
            return ResponseEntity.ok(remain);
            
        } catch (RuntimeException e) {
            log.error("발송 가능 건수 조회 실패: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
