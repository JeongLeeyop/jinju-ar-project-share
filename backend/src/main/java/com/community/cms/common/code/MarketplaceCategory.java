package com.community.cms.common.code;

/**
 * 장터 카테고리
 */
public enum MarketplaceCategory {
    
    /** 판매 */
    SALE("SALE", "판매"),
    
    /** 나눔 */
    SHARE("SHARE", "나눔"),
    
    /** 요청 */
    REQUEST("REQUEST", "요청");
    
    private final String code;
    private final String description;
    
    MarketplaceCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static MarketplaceCategory fromCode(String code) {
        for (MarketplaceCategory category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid marketplace category code: " + code);
    }
}
