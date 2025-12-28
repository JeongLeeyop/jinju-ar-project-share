package com.community.cms.api.sms.dto;

import com.community.cms.entity.SmsTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * SMS 템플릿 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplateDto {

    private Long id;
    private String name;
    private String content;
    private String channelUid;
    private Boolean isActive;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환
     */
    public static SmsTemplateDto from(SmsTemplate entity) {
        if (entity == null) {
            return null;
        }

        return SmsTemplateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .content(entity.getContent())
                .channelUid(entity.getChannelUid())
                .isActive(entity.getIsActive())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
