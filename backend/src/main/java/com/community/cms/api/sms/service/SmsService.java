package com.community.cms.api.sms.service;

import com.community.cms.api.sms.dto.SmsHistoryDto;
import com.community.cms.api.sms.dto.SmsSendRequest;
import com.community.cms.api.sms.dto.SmsSendResponse;
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
    
    public SmsService() {
        this.restTemplate = new RestTemplate();
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
            
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    ALIGO_API_URL + "/send/",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            Map<String, Object> body = response.getBody();
            
            if (body == null) {
                return SmsSendResponse.builder()
                        .resultCode(-1)
                        .message("API 응답이 없습니다")
                        .isSuccess(false)
                        .build();
            }
            
            Integer resultCode = (Integer) body.get("result_code");
            String message = (String) body.get("message");
            
            return SmsSendResponse.builder()
                    .resultCode(resultCode)
                    .message(message != null ? message : "")
                    .msgId(body.get("msg_id") != null ? Long.valueOf(body.get("msg_id").toString()) : null)
                    .successCnt((Integer) body.get("success_cnt"))
                    .errorCnt((Integer) body.get("error_cnt"))
                    .msgType((String) body.get("msg_type"))
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
            
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    ALIGO_API_URL + "/list/",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            Map<String, Object> body = response.getBody();
            
            if (body == null || body.get("list") == null) {
                return new ArrayList<>();
            }
            
            List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");
            List<SmsHistoryDto> histories = new ArrayList<>();
            
            for (Map<String, Object> item : list) {
                String type = (String) item.get("type");
                Integer smsCount = item.get("sms_count") != null ? Integer.valueOf(item.get("sms_count").toString()) : 0;
                Integer cost = calculateCost(type, smsCount);
                
                SmsHistoryDto history = SmsHistoryDto.builder()
                        .mid(item.get("mid") != null ? Long.valueOf(item.get("mid").toString()) : null)
                        .type(type)
                        .sender((String) item.get("sender"))
                        .smsCount(smsCount)
                        .reserveState((String) item.get("reserve_state"))
                        .msg((String) item.get("msg"))
                        .failCount(item.get("fail_count") != null ? Integer.valueOf(item.get("fail_count").toString()) : 0)
                        .regDate((String) item.get("reg_date"))
                        .reserve((String) item.get("reserve"))
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
            
            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(
                    ALIGO_API_URL + "/remain/",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );
            
            return response.getBody();
            
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
}
