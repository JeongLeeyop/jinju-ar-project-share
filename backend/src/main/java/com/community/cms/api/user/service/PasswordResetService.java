package com.community.cms.api.user.service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.community.cms.api.sms.dto.SmsSendRequest;
import com.community.cms.api.sms.dto.SmsSendResponse;
import com.community.cms.api.sms.service.SmsService;
import com.community.cms.api.user.dto.PasswordDto;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 비밀번호 찾기/재설정 서비스 (SMS 임시비밀번호 발급 방식)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final SmsService smsService;

    // 임시 비밀번호 생성용 문자
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 임시 비밀번호 발급 및 SMS 발송
     */
    @Transactional
    public PasswordDto.TempPasswordResponse requestTempPassword(PasswordDto.TempPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String concatNumber = normalizePhoneNumber(request.getConcatNumber());
        
        // 이메일과 전화번호로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserIdAndConcatNumber(email, concatNumber);
        
        // 하이픈 포함 번호로도 검색
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByUserIdAndConcatNumber(email, request.getConcatNumber().trim());
        }
        
        if (userOptional.isEmpty()) {
            return PasswordDto.TempPasswordResponse.builder()
                    .success(false)
                    .message("입력하신 정보와 일치하는 회원을 찾을 수 없습니다.")
                    .build();
        }
        
        User user = userOptional.get();
        
        // 임시 비밀번호 생성 (8자리)
        String tempPassword = generateTempPassword(8);
        
        // 비밀번호 변경
        user.setUserPassword(tempPassword);
        userRepository.save(user);
        
        // SMS 발송
        String message = String.format("[진주알 커뮤니티] 임시 비밀번호가 발급되었습니다.\n\n임시 비밀번호: %s\n\n로그인 후 반드시 비밀번호를 변경해주세요.", tempPassword);
        
        SmsSendRequest smsRequest = SmsSendRequest.builder()
                .receivers(Collections.singletonList(concatNumber))
                .message(message)
                .msgType("LMS")
                .title("[진주알] 임시 비밀번호 안내")
                .build();
        
        try {
            SmsSendResponse smsResponse = smsService.sendSms(smsRequest);
            
            if (smsResponse.isSuccess()) {
                log.info("임시 비밀번호 SMS 발송 성공: {}", maskPhoneNumber(concatNumber));
                return PasswordDto.TempPasswordResponse.builder()
                        .success(true)
                        .message("임시 비밀번호가 " + maskPhoneNumber(request.getConcatNumber()) + "로 발송되었습니다.")
                        .maskedPhone(maskPhoneNumber(request.getConcatNumber()))
                        .build();
            } else {
                log.error("임시 비밀번호 SMS 발송 실패: {}", smsResponse.getMessage());
                // SMS 발송 실패시 비밀번호 롤백은 하지 않음 (이미 저장됨)
                return PasswordDto.TempPasswordResponse.builder()
                        .success(false)
                        .message("SMS 발송에 실패했습니다. 잠시 후 다시 시도해주세요.")
                        .build();
            }
        } catch (Exception e) {
            log.error("임시 비밀번호 SMS 발송 중 오류: {}", e.getMessage(), e);
            return PasswordDto.TempPasswordResponse.builder()
                    .success(false)
                    .message("SMS 발송 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                    .build();
        }
    }

    /**
     * 이메일(아이디) 찾기
     */
    public PasswordDto.FindEmailResponse findEmail(PasswordDto.FindEmailRequest request) {
        String actualName = request.getActualName().trim();
        String concatNumber = normalizePhoneNumber(request.getConcatNumber());
        
        // 이름과 핸드폰 번호로 사용자 조회
        Optional<User> userOptional = userRepository.findByActualNameAndConcatNumber(actualName, concatNumber);
        
        // 하이픈 포함 번호로도 검색
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByActualNameAndConcatNumber(actualName, request.getConcatNumber().trim());
        }
        
        if (userOptional.isEmpty()) {
            return PasswordDto.FindEmailResponse.builder()
                    .found(false)
                    .message("입력하신 정보와 일치하는 회원을 찾을 수 없습니다.")
                    .build();
        }
        
        User user = userOptional.get();
        // 마스킹 없이 전체 이메일 표시
        String email = user.getUserId();
        
        return PasswordDto.FindEmailResponse.builder()
                .found(true)
                .maskedEmail(email)  // maskedEmail 필드명 유지하지만 마스킹 안함
                .message("회원님의 이메일(아이디)을 찾았습니다.")
                .build();
    }

    /**
     * 임시 비밀번호 생성 (대문자, 소문자, 숫자, 특수문자 포함)
     */
    private String generateTempPassword(int length) {
        StringBuilder password = new StringBuilder();
        
        // 각 유형에서 최소 1개씩 포함
        password.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        password.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));
        
        // 나머지 자리 랜덤으로 채우기
        String allChars = UPPER + LOWER + DIGITS + SPECIAL;
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
        }
        
        // 셔플
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }

    /**
     * 전화번호 정규화 (하이픈 제거)
     */
    private String normalizePhoneNumber(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("-", "").trim();
    }

    /**
     * 전화번호 마스킹 (010-****-5678 형식)
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 7) {
            return "***";
        }
        
        String normalized = phone.replaceAll("-", "");
        if (normalized.length() == 11) {
            return normalized.substring(0, 3) + "-****-" + normalized.substring(7);
        } else if (normalized.length() == 10) {
            return normalized.substring(0, 3) + "-***-" + normalized.substring(6);
        }
        return "***-****-" + normalized.substring(normalized.length() - 4);
    }
}
