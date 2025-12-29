package com.community.cms.api.sms.service;

import com.community.cms.api.sms.dto.SmsHistoryDto;
import com.community.cms.api.sms.dto.SmsSendRequest;
import com.community.cms.api.sms.dto.SmsSendResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SmsService {
    
    @Value("${aligo.api.key:}")
    private String apiKey;
    
    @Value("${aligo.api.user-id:}")
    private String userId;
    
    @Value("${aligo.api.sender:}")
    private String sender;
    
    private static final String ALIGO_API_URL = "https://apis.aligo.in";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public SmsService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 문자 발송
     */
    public SmsSendResponse sendSms(SmsSendRequest request) {
        try {
            // API Key, User ID 검증
            if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
                return SmsSendResponse.builder()
                        .resultCode(-1)
                        .message("알리고 API 설정이 필요합니다. application.yml에 API Key와 User ID를 설정해주세요.")
                        .isSuccess(false)
                        .build();
            }
            
            // 수신자 번호를 쉼표로 구분
            String receiver = String.join(",", request.getReceivers());
            
            // 메시지 타입 자동 판별 (지정되지 않은 경우)
            String msgType = request.getMsgType();
            if (msgType == null || msgType.isEmpty()) {
                int messageLength = request.getMessage().getBytes().length;
                msgType = messageLength <= 90 ? "SMS" : "LMS";
            }
            
            // 요청 파라미터 구성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("key", apiKey);
            params.add("user_id", userId);
            params.add("sender", sender);
            params.add("receiver", receiver);
            params.add("msg", request.getMessage());
            params.add("msg_type", msgType);
            
            if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                params.add("title", request.getTitle());
            }
            
            if (request.getReserveDate() != null && !request.getReserveDate().isEmpty()) {
                params.add("rdate", request.getReserveDate());
            }
            
            if (request.getReserveTime() != null && !request.getReserveTime().isEmpty()) {
                params.add("rtime", request.getReserveTime());
            }
            
            if (request.getTestMode() != null && request.getTestMode()) {
                params.add("testmode_yn", "Y");
            }
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            
            log.info("SMS 발송 API 호출: URL={}, receiver={}, msgType={}", 
                    ALIGO_API_URL + "/send/", receiver, msgType);
            
            // API 호출 - String으로 먼저 받기
            ResponseEntity<String> response = restTemplate.exchange(
                    ALIGO_API_URL + "/send/",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            String responseBody = response.getBody();
            log.info("SMS API 응답: status={}, body={}", response.getStatusCode(), responseBody);
            
            if (responseBody == null || responseBody.isEmpty()) {
                return SmsSendResponse.builder()
                        .resultCode(-1)
                        .message("API 응답이 없습니다")
                        .isSuccess(false)
                        .build();
            }
            
            // HTML 응답인 경우 처리
            if (responseBody.trim().startsWith("<")) {
                log.error("SMS API가 HTML을 반환했습니다: {}", responseBody.substring(0, Math.min(200, responseBody.length())));
                return SmsSendResponse.builder()
                        .resultCode(-1)
                        .message("API 인증 실패 또는 잘못된 요청입니다. API Key와 User ID를 확인해주세요.")
                        .isSuccess(false)
                        .build();
            }
            
            // JSON 파싱
            Map<String, Object> body = objectMapper.readValue(responseBody, Map.class);
            
            if (body == null) {
                return SmsSendResponse.builder()
                        .resultCode(-1)
                        .message("API 응답 파싱 실패")
                        .isSuccess(false)
                        .build();
            }
            
            Integer resultCode = safeGetInteger(body, "result_code");
            String message = safeGetString(body, "message");
            
            return SmsSendResponse.builder()
                    .resultCode(resultCode)
                    .message(message != null ? message : "")
                    .msgId(safeGetLong(body, "msg_id"))
                    .successCnt(safeGetInteger(body, "success_cnt"))
                    .errorCnt(safeGetInteger(body, "error_cnt"))
                    .msgType(safeGetString(body, "msg_type"))
                    .isSuccess(resultCode != null && resultCode > 0)
                    .build();
                    
        } catch (Exception e) {
            log.error("SMS 발송 실패", e);
            return SmsSendResponse.builder()
                    .resultCode(-1)
                    .message("문자 발송 중 오류가 발생했습니다: " + e.getMessage())
                    .isSuccess(false)
                    .build();
        }
    }
    
    /**
     * 전송 내역 조회
     */
    public List<SmsHistoryDto> getSmsHistory(Integer page, Integer pageSize, String startDate, Integer limitDay) {
        try {
            // API Key, User ID 검증
            if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
                log.error("알리고 API 설정이 필요합니다");
                return new ArrayList<>();
            }
            
            // 요청 파라미터 구성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("key", apiKey);
            params.add("user_id", userId);
            
            if (page != null) {
                params.add("page", String.valueOf(page));
            }
            
            if (pageSize != null) {
                params.add("page_size", String.valueOf(pageSize));
            }
            
            if (startDate != null && !startDate.isEmpty()) {
                params.add("start_date", startDate);
            }
            
            if (limitDay != null) {
                params.add("limit_day", String.valueOf(limitDay));
            }
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            
            // API 호출 - String으로 먼저 받기
            ResponseEntity<String> response = restTemplate.exchange(
                    ALIGO_API_URL + "/list/",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            String responseBody = response.getBody();
            
            if (responseBody == null || responseBody.isEmpty()) {
                return new ArrayList<>();
            }
            
            // HTML 응답인 경우 처리
            if (responseBody.trim().startsWith("<")) {
                log.error("SMS 내역 조회 API가 HTML을 반환했습니다");
                return new ArrayList<>();
            }
            
            // JSON 파싱
            Map<String, Object> body = objectMapper.readValue(responseBody, Map.class);
            
            if (body == null || body.get("list") == null) {
                return new ArrayList<>();
            }
            
            List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
            List<SmsHistoryDto> histories = new ArrayList<>();
            
            for (Map<String, Object> item : list) {
                String msg = safeGetString(item, "msg");
                
                // "문화제작소"가 포함된 메시지는 필터링
                if (msg != null && msg.contains("문화제작소")) {
                    continue;
                }
                
                String type = safeGetString(item, "type");
                Integer smsCount = safeGetInteger(item, "sms_count");
                Integer cost = calculateCost(type, smsCount != null ? smsCount : 0);
                
                SmsHistoryDto history = SmsHistoryDto.builder()
                        .mid(safeGetLong(item, "mid"))
                        .type(type)
                        .sender(safeGetString(item, "sender"))
                        .smsCount(smsCount)
                        .reserveState(safeGetString(item, "reserve_state"))
                        .msg(msg)
                        .failCount(safeGetInteger(item, "fail_count"))
                        .regDate(safeGetString(item, "reg_date"))
                        .reserve(safeGetString(item, "reserve"))
                        .cost(cost)
                        .build();
                
                histories.add(history);
            }
            
            return histories;
            
        } catch (Exception e) {
            log.error("SMS 내역 조회 실패", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 발송 가능 건수 조회
     */
    public Map<String, Object> getRemainCount() {
        try {
            // API Key, User ID 검증
            if (apiKey == null || apiKey.isEmpty() || userId == null || userId.isEmpty()) {
                return Map.of("error", "알리고 API 설정이 필요합니다");
            }
            
            // 요청 파라미터 구성
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("key", apiKey);
            params.add("user_id", userId);
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            
            // API 호출 - String으로 먼저 받기
            ResponseEntity<String> response = restTemplate.exchange(
                    ALIGO_API_URL + "/remain/",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            String responseBody = response.getBody();
            
            if (responseBody == null || responseBody.isEmpty()) {
                return Map.of("error", "응답이 없습니다");
            }
            
            // HTML 응답인 경우 처리
            if (responseBody.trim().startsWith("<")) {
                log.error("발송 가능 건수 조회 API가 HTML을 반환했습니다");
                return Map.of("error", "API 인증 실패");
            }
            
            // JSON 파싱
            return objectMapper.readValue(responseBody, Map.class);
            
        } catch (Exception e) {
            log.error("발송 가능 건수 조회 실패", e);
            return Map.of("error", "발송 가능 건수 조회에 실패했습니다");
        }
    }
    
    /**
     * 비용 계산 (타입별 단가)
     */
    private Integer calculateCost(String type, Integer count) {
        if (count == null) count = 0;
        
        int pricePerMessage;
        switch (type) {
            case "SMS":
                pricePerMessage = 100;
                break;
            case "LMS":
                pricePerMessage = 200;
                break;
            case "MMS":
                pricePerMessage = 300;
                break;
            default:
                pricePerMessage = 100;
        }
        
        return count * pricePerMessage;
    }
    
    /**
     * Map에서 안전하게 Integer 추출
     */
    private Integer safeGetInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer from key '{}': {}", key, value);
            return null;
        }
    }
    
    /**
     * Map에서 안전하게 Long 추출
     */
    private Long safeGetLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse long from key '{}': {}", key, value);
            return null;
        }
    }
    
    /**
     * Map에서 안전하게 String 추출
     */
    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
}
