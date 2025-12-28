package com.community.cms.api.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsSendRequest {
    
    @NotNull(message = "수신자 목록은 필수입니다")
    @NotEmpty(message = "수신자 목록은 비어있을 수 없습니다")
    private List<String> receivers; // 수신자 전화번호 목록
    
    @NotNull(message = "메시지 내용은 필수입니다")
    private String message; // 메시지 내용
    
    private String title; // 메시지 제목 (LMS, MMS)
    
    private String msgType; // SMS, LMS, MMS (자동 판별 시 null)
    
    private String reserveDate; // 예약일 (YYYYMMDD)
    
    private String reserveTime; // 예약시간 (HHII)
    
    private Boolean testMode; // 테스트 모드 (true/false)
}
