package com.community.cms.api.activity.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * Activity List Request DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListRequest {

    /**
     * 채널 UID
     */
    private String channelUid;

    /**
     * 공간 UID (선택)
     */
    private String spaceUid;

    /**
     * 조회 시작일
     */
    private LocalDate startDate;

    /**
     * 조회 종료일
     */
    private LocalDate endDate;

    /**
     * 페이지 번호
     */
    private Integer page;

    /**
     * 페이지 크기
     */
    private Integer size;
}
