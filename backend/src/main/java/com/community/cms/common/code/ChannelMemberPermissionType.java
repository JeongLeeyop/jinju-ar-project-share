package com.community.cms.common.code;

/**
 * 채널 멤버 권한 타입
 */
public enum ChannelMemberPermissionType {
    
    /** 게시판 이용 - 등록 */
    POST_CREATE("POST_CREATE", "게시글 등록"),
    
    /** 게시판 이용 - 수정 */
    POST_UPDATE("POST_UPDATE", "게시글 수정"),
    
    /** 게시판 이용 - 삭제 */
    POST_DELETE("POST_DELETE", "게시글 삭제"),
    
    /** 공간 생성 */
    SPACE_CREATE("SPACE_CREATE", "공간 생성"),
    
    /** 장터 이용 */
    MARKETPLACE_USE("MARKETPLACE_USE", "장터 이용"),
    
    /** 장터 등록 */
    MARKETPLACE_REGISTER("MARKETPLACE_REGISTER", "장터 등록"),
    
    /** 오프라인 장터 등록 */
    OFFLINE_MARKETPLACE_REGISTER("OFFLINE_MARKETPLACE_REGISTER", "오프라인 장터 등록"),
    
    /** 일정 참여 */
    SCHEDULE_PARTICIPATE("SCHEDULE_PARTICIPATE", "일정 참여"),

    /** 일정 생성 */
    SCHEDULE_CREATE("SCHEDULE_CREATE", "일정 생성");
    
    private final String code;
    private final String description;
    
    ChannelMemberPermissionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 코드로 권한 타입 찾기
     */
    public static ChannelMemberPermissionType fromCode(String code) {
        for (ChannelMemberPermissionType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid permission type code: " + code);
    }
}
