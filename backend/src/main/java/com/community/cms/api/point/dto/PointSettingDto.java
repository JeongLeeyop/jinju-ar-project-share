package com.community.cms.api.point.dto;

import com.community.cms.entity.PointSetting;
import lombok.*;

/**
 * 포인트 설정 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointSettingDto {
    private Long id;
    private String channelUid;
    
    // 포인트 적립량
    private Integer postCreate;
    private Integer commentCreate;
    private Integer likeGive;
    private Integer dailyAttendance;
    private Integer marketplaceCreate;
    private Integer marketplaceSell;
    private Integer courseComplete;
    
    // 일일 적립 횟수 제한 (0 = 무제한)
    private Integer postDailyLimit;
    private Integer commentDailyLimit;
    private Integer likeDailyLimit;
    private Integer marketplaceCreateDailyLimit;
    private Integer marketplaceSellDailyLimit;
    private Integer courseCompleteDailyLimit;
    
    // 최소 글자수 제한
    private Integer postMinLength;
    private Integer commentMinLength;

    /**
     * Entity -> DTO 변환
     */
    public static PointSettingDto from(PointSetting entity) {
        return PointSettingDto.builder()
                .id(entity.getId())
                .channelUid(entity.getChannelUid())
                .postCreate(entity.getPostCreate())
                .commentCreate(entity.getCommentCreate())
                .likeGive(entity.getLikeGive())
                .dailyAttendance(entity.getDailyAttendance())
                .marketplaceCreate(entity.getMarketplaceCreate())
                .marketplaceSell(entity.getMarketplaceSell())
                .courseComplete(entity.getCourseComplete())
                .postDailyLimit(entity.getPostDailyLimit())
                .commentDailyLimit(entity.getCommentDailyLimit())
                .likeDailyLimit(entity.getLikeDailyLimit())
                .marketplaceCreateDailyLimit(entity.getMarketplaceCreateDailyLimit())
                .marketplaceSellDailyLimit(entity.getMarketplaceSellDailyLimit())
                .courseCompleteDailyLimit(entity.getCourseCompleteDailyLimit())
                .postMinLength(entity.getPostMinLength())
                .commentMinLength(entity.getCommentMinLength())
                .build();
    }

    /**
     * DTO -> Entity 변환 (신규 생성용)
     */
    public PointSetting toEntity(String channelUid) {
        return PointSetting.builder()
                .channelUid(channelUid)
                .postCreate(this.postCreate != null ? this.postCreate : 100)
                .commentCreate(this.commentCreate != null ? this.commentCreate : 50)
                .likeGive(this.likeGive != null ? this.likeGive : 10)
                .dailyAttendance(this.dailyAttendance != null ? this.dailyAttendance : 50)
                .marketplaceCreate(this.marketplaceCreate != null ? this.marketplaceCreate : 100)
                .marketplaceSell(this.marketplaceSell != null ? this.marketplaceSell : 200)
                .courseComplete(this.courseComplete != null ? this.courseComplete : 150)
                .postDailyLimit(this.postDailyLimit != null ? this.postDailyLimit : 10)
                .commentDailyLimit(this.commentDailyLimit != null ? this.commentDailyLimit : 20)
                .likeDailyLimit(this.likeDailyLimit != null ? this.likeDailyLimit : 30)
                .marketplaceCreateDailyLimit(this.marketplaceCreateDailyLimit != null ? this.marketplaceCreateDailyLimit : 5)
                .marketplaceSellDailyLimit(this.marketplaceSellDailyLimit != null ? this.marketplaceSellDailyLimit : 0)
                .courseCompleteDailyLimit(this.courseCompleteDailyLimit != null ? this.courseCompleteDailyLimit : 0)
                .postMinLength(this.postMinLength != null ? this.postMinLength : 20)
                .commentMinLength(this.commentMinLength != null ? this.commentMinLength : 5)
                .build();
    }

    /**
     * 기본값 DTO 생성
     */
    public static PointSettingDto defaultSettings() {
        return PointSettingDto.builder()
                .postCreate(100)
                .commentCreate(50)
                .likeGive(10)
                .dailyAttendance(50)
                .marketplaceCreate(100)
                .marketplaceSell(200)
                .courseComplete(150)
                .postDailyLimit(10)
                .commentDailyLimit(20)
                .likeDailyLimit(30)
                .marketplaceCreateDailyLimit(5)
                .marketplaceSellDailyLimit(0)
                .courseCompleteDailyLimit(0)
                .postMinLength(20)
                .commentMinLength(5)
                .build();
    }
}
