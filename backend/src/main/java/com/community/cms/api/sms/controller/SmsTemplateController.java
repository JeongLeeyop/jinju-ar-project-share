package com.community.cms.api.sms.controller;

import com.community.cms.api.sms.dto.SmsTemplateDto;
import com.community.cms.api.sms.service.SmsTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SMS 템플릿 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/sms/templates")
@RequiredArgsConstructor
public class SmsTemplateController {

    private final SmsTemplateService smsTemplateService;

    /**
     * 활성화된 SMS 템플릿 목록 조회
     * GET /api/sms/templates?channelUid={channelUid}
     */
    @GetMapping
    public ResponseEntity<?> getActiveTemplates(
            @RequestParam(required = false) String channelUid) {
        try {
            List<SmsTemplateDto> templates = smsTemplateService.getActiveTemplates(channelUid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("templates", templates);
            response.put("count", templates.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get SMS templates: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "템플릿 조회에 실패했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
