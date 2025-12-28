package com.community.cms.util;

import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.User;
import com.community.cms.oauth.SinghaUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 인증 관련 유틸리티 클래스
 * 전역적으로 사용되는 인증 관련 헬퍼 메서드 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자 검증 및 반환
     * 
     * @param userDetails SinghaUser 인증 객체
     * @return 검증된 User 엔티티
     * @throws RuntimeException 인증되지 않았거나 사용자를 찾을 수 없는 경우
     */
    public User validateAndGetCurrentUser(SinghaUser userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            log.error("Unauthenticated access attempt detected");
            throw new RuntimeException("인증되지 않은 접근입니다");
        }
        return userRepository.findByUid(userDetails.getUser().getUid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
    }

    /**
     * 현재 인증된 사용자 검증 및 반환 (null 허용)
     * 공개 접근이 허용되는 경우 사용
     * 
     * @param userDetails SinghaUser 인증 객체
     * @return 검증된 User 엔티티 (인증되지 않은 경우 null)
     */
    public User validateAndGetCurrentUserOrNull(SinghaUser userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            return null;
        }
        return userRepository.findByUid(userDetails.getUser().getUid())
                .orElse(null);
    }

    /**
     * 현재 인증된 사용자의 UID 반환
     * 
     * @param userDetails SinghaUser 인증 객체
     * @return 사용자 UID
     */
    public String getCurrentUserUid(SinghaUser userDetails) {
        return validateAndGetCurrentUser(userDetails).getUid();
    }

    /**
     * 현재 인증된 사용자의 이름 반환
     * 
     * @param userDetails SinghaUser 인증 객체
     * @return 사용자 이름
     */
    public String getCurrentUserName(SinghaUser userDetails) {
        return validateAndGetCurrentUser(userDetails).getActualName();
    }
}
