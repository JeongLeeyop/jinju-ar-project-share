package com.community.cms.api.sms.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SmsDto {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendRequest {
        private List<String> recipients;  // 수신자 번호 목록
        private List<String> userUids;    // 회원 UID 목록 (회원 선택 시)
        private String message;           // 메시지 내용
        private String title;             // 제목 (LMS인 경우)
        private String smsType;           // SMS, LMS, MMS
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendResponse {
        private boolean success;
        private String message;
        private int successCount;
        private int failCount;
        private String msgId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class History {
        private Long id;
        private String uid;
        private String recipient;
        private String message;
        private String status;
        private String smsType;
        private LocalDateTime sentAt;
        private String senderName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemainCount {
        private int smsCount;
        private int lmsCount;
        private int mmsCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberForSms {
        private String uid;
        private String nickname;
        private String phoneNumber;
        private String channelName;
    }
}
