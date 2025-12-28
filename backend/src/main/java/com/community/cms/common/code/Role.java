package com.community.cms.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
        // 계정 타입
        Guest("ROLE_GUEST", "손님"),
        USER("ROLE_USER", "일반 사용자"),
        CREATOR("ROLE_CREATOR", "크리에이터"),
        ADMIN("ROLE_ADMIN", "관리자");
    
        private final String key;
        private final String title;
        
        }
