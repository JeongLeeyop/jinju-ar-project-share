package com.community.cms.api.channel.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.community.cms.common.code.ChannelMemberPermissionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채널 멤버 권한 DTO
 */
public class ChannelMemberPermissionDto {
    
    /**
     * 권한 상세 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        private Long id;
        private Integer channelMemberIdx;
        private ChannelMemberPermissionType permissionType;
        private String permissionTypeName;
        private boolean hasPermission;
        private String grantedBy;
        private LocalDateTime grantedAt;
        private LocalDateTime updatedAt;
    }
    
    /**
     * 권한 생성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "채널 멤버 인덱스는 필수입니다")
        private Integer channelMemberIdx;
        
        @NotNull(message = "권한 타입은 필수입니다")
        private ChannelMemberPermissionType permissionType;
        
        @NotNull(message = "권한 보유 여부는 필수입니다")
        private Boolean hasPermission;
    }
    
    /**
     * 권한 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull(message = "권한 보유 여부는 필수입니다")
        private Boolean hasPermission;
    }
    
    /**
     * 멤버 권한 일괄 설정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkUpdateRequest {
        @NotNull(message = "채널 멤버 인덱스는 필수입니다")
        private Integer channelMemberIdx;
        
        @NotNull(message = "권한 목록은 필수입니다")
        private List<PermissionItem> permissions;
    }
    
    /**
     * 권한 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionItem {
        @NotNull(message = "권한 타입은 필수입니다")
        private ChannelMemberPermissionType permissionType;
        
        @NotNull(message = "권한 보유 여부는 필수입니다")
        private Boolean hasPermission;
    }
    
    /**
     * 권한 목록 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Integer channelMemberIdx;
        private String userUid;
        private String userName;
        private List<Detail> permissions;
    }
    
    /**
     * 권한 확인 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckResponse {
        private ChannelMemberPermissionType permissionType;
        private boolean hasPermission;
    }
}
