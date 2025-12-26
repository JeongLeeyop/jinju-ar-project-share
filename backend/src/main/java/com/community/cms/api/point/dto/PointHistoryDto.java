package com.community.cms.api.point.dto;

import com.community.cms.entity.PointHistory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 포인트 히스토리 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDto {

    private Long id;
    private String userUid;
    private String channelUid;
    private String pointType;
    private Integer pointAmount;
    private Integer currentBalance;
    private String description;
    private String referenceId;
    private LocalDateTime createdAt;

    /**
     * Entity to DTO
     */
    public static PointHistoryDto from(PointHistory entity) {
        return PointHistoryDto.builder()
                .id(entity.getId())
                .userUid(entity.getUserUid())
                .channelUid(entity.getChannelUid())
                .pointType(entity.getPointType())
                .pointAmount(entity.getPointAmount())
                .currentBalance(entity.getCurrentBalance())
                .description(entity.getDescription())
                .referenceId(entity.getReferenceId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // 기존 호환성을 위한 내부 클래스 유지
    @Data
    public static class list {
        private Integer idx;
        private Integer point;
        private Integer remainPoint;
        private String reason;
        private LocalDateTime createDate;
    }
}
