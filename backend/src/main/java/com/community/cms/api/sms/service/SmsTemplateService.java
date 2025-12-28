package com.community.cms.api.sms.service;

import com.community.cms.api.sms.dto.SmsTemplateDto;
import com.community.cms.api.sms.repository.SmsTemplateRepository;
import com.community.cms.entity.SmsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SMS 템플릿 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsTemplateService {

    private final SmsTemplateRepository smsTemplateRepository;

    /**
     * 채널의 활성화된 템플릿 조회
     * @param channelUid 채널 UID
     * @return 활성화된 템플릿 목록
     */
    @Transactional(readOnly = true)
    public List<SmsTemplateDto> getActiveTemplates(String channelUid) {
        List<SmsTemplate> templates;
        
        if (channelUid == null || channelUid.isEmpty()) {
            // 채널 UID가 없으면 공통 템플릿만 조회
            templates = smsTemplateRepository.findActiveCommonTemplates();
        } else {
            // 채널 UID가 있으면 공통 + 해당 채널 템플릿 조회
            templates = smsTemplateRepository.findActiveTemplatesByChannelUid(channelUid);
        }
        
        log.info("Found {} active SMS templates for channel: {}", templates.size(), channelUid);
        
        return templates.stream()
                .map(SmsTemplateDto::from)
                .collect(Collectors.toList());
    }
}
