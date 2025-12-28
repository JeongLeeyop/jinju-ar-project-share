package com.community.cms.api.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SmsSendResponse {
    
    private Integer resultCode;
    
    private String message;
    
    private Long msgId;
    
    private Integer successCnt;
    
    private Integer errorCnt;
    
    private String msgType;
    
    @JsonProperty("isSuccess")
    private boolean isSuccess;
}
