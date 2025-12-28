package com.community.cms.api.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsHistoryDto {
    
    private Long mid; // 메시지 ID
    
    private String type; // 문자 구분 (SMS, LMS, MMS)
    
    private String sender; // 발신번호
    
    private Integer smsCount; // 전송요청수
    
    private String reserveState; // 요청상태
    
    private String msg; // 메시지 내용
    
    private Integer failCount; // 처리실패건수
    
    private String regDate; // 등록일
    
    private String reserve; // 예약일자
    
    private Integer cost; // 비용 (계산된 값)
}
