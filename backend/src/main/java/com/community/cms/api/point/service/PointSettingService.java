package com.community.cms.api.point.service;

import com.community.cms.api.point.dto.PointSettingDto;
import com.community.cms.api.point.repository.PointSettingRepository;
import com.community.cms.entity.PointSetting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트 설정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointSettingService {

    private final PointSettingRepository pointSettingRepository;

    /**
     * 채널의 포인트 설정 조회
     * 설정이 없으면 기본값 반환
     */
    @Transactional(readOnly = true)
    public PointSettingDto getPointSetting(String channelUid) {
        return pointSettingRepository.findByChannelUid(channelUid)
                .map(PointSettingDto::from)
                .orElse(PointSettingDto.defaultSettings());
    }

    /**
     * 채널의 포인트 설정 저장
     * 기존 설정이 있으면 업데이트, 없으면 새로 생성
     */
    @Transactional
    public PointSettingDto savePointSetting(String channelUid, PointSettingDto dto) {
        PointSetting setting = pointSettingRepository.findByChannelUid(channelUid)
                .orElse(PointSetting.builder().channelUid(channelUid).build());

        // 포인트 적립량 업데이트
        if (dto.getPostCreate() != null) setting.setPostCreate(dto.getPostCreate());
        if (dto.getCommentCreate() != null) setting.setCommentCreate(dto.getCommentCreate());
        if (dto.getLikeGive() != null) setting.setLikeGive(dto.getLikeGive());
        if (dto.getDailyAttendance() != null) setting.setDailyAttendance(dto.getDailyAttendance());
        if (dto.getMarketplaceCreate() != null) setting.setMarketplaceCreate(dto.getMarketplaceCreate());
        if (dto.getMarketplaceSell() != null) setting.setMarketplaceSell(dto.getMarketplaceSell());
        if (dto.getCourseComplete() != null) setting.setCourseComplete(dto.getCourseComplete());
        
        // 일일 적립 횟수 제한 업데이트
        if (dto.getPostDailyLimit() != null) setting.setPostDailyLimit(dto.getPostDailyLimit());
        if (dto.getCommentDailyLimit() != null) setting.setCommentDailyLimit(dto.getCommentDailyLimit());
        if (dto.getLikeDailyLimit() != null) setting.setLikeDailyLimit(dto.getLikeDailyLimit());
        if (dto.getMarketplaceCreateDailyLimit() != null) setting.setMarketplaceCreateDailyLimit(dto.getMarketplaceCreateDailyLimit());
        if (dto.getMarketplaceSellDailyLimit() != null) setting.setMarketplaceSellDailyLimit(dto.getMarketplaceSellDailyLimit());
        if (dto.getCourseCompleteDailyLimit() != null) setting.setCourseCompleteDailyLimit(dto.getCourseCompleteDailyLimit());
        
        // 최소 글자수 제한 업데이트
        if (dto.getPostMinLength() != null) setting.setPostMinLength(dto.getPostMinLength());
        if (dto.getCommentMinLength() != null) setting.setCommentMinLength(dto.getCommentMinLength());

        PointSetting saved = pointSettingRepository.save(setting);
        log.info("Point setting saved for channel: {}", channelUid);
        
        return PointSettingDto.from(saved);
    }

    /**
     * 채널의 포인트 설정을 기본값으로 초기화
     */
    @Transactional
    public PointSettingDto resetPointSetting(String channelUid) {
        PointSetting setting = pointSettingRepository.findByChannelUid(channelUid)
                .orElse(PointSetting.builder().channelUid(channelUid).build());

        // 포인트 적립량 기본값으로 초기화
        setting.setPostCreate(100);
        setting.setCommentCreate(50);
        setting.setLikeGive(10);
        setting.setDailyAttendance(50);
        setting.setMarketplaceCreate(100);
        setting.setMarketplaceSell(200);
        setting.setCourseComplete(150);
        
        // 일일 적립 횟수 제한 기본값으로 초기화
        setting.setPostDailyLimit(10);
        setting.setCommentDailyLimit(20);
        setting.setLikeDailyLimit(30);
        setting.setMarketplaceCreateDailyLimit(5);
        setting.setMarketplaceSellDailyLimit(0);
        setting.setCourseCompleteDailyLimit(0);
        
        // 최소 글자수 제한 기본값으로 초기화
        setting.setPostMinLength(20);
        setting.setCommentMinLength(5);

        PointSetting saved = pointSettingRepository.save(setting);
        log.info("Point setting reset to default for channel: {}", channelUid);
        
        return PointSettingDto.from(saved);
    }

    /**
     * 이벤트 타입에 대한 일일 제한 횟수 조회
     * @param channelUid 채널 UID
     * @param eventType 이벤트 타입
     * @return 일일 제한 횟수 (0 = 무제한)
     */
    @Transactional(readOnly = true)
    public Integer getDailyLimitForEvent(String channelUid, String eventType) {
        PointSetting setting = pointSettingRepository.findByChannelUid(channelUid)
                .orElse(null);

        if (setting == null) {
            // 기본값 반환
            return getDefaultDailyLimitForEvent(eventType);
        }

        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return setting.getPostDailyLimit();
            case "COMMENT":
            case "COMMENT_CREATE":
                return setting.getCommentDailyLimit();
            case "LIKE":
            case "LIKE_GIVE":
                return setting.getLikeDailyLimit();
            case "MARKETPLACE_CREATE":
                return setting.getMarketplaceCreateDailyLimit();
            case "MARKETPLACE_SELL":
                return setting.getMarketplaceSellDailyLimit();
            case "COURSE_COMPLETE":
                return setting.getCourseCompleteDailyLimit();
            default:
                return 0;
        }
    }

    /**
     * 이벤트 타입에 대한 최소 글자수 조회
     * @param channelUid 채널 UID
     * @param eventType 이벤트 타입
     * @return 최소 글자수 (0 = 제한 없음)
     */
    @Transactional(readOnly = true)
    public Integer getMinLengthForEvent(String channelUid, String eventType) {
        PointSetting setting = pointSettingRepository.findByChannelUid(channelUid)
                .orElse(null);

        if (setting == null) {
            // 기본값 반환
            return getDefaultMinLengthForEvent(eventType);
        }

        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return setting.getPostMinLength();
            case "COMMENT":
            case "COMMENT_CREATE":
                return setting.getCommentMinLength();
            default:
                return 0;
        }
    }

    /**
     * 일일 제한 기본값
     */
    private Integer getDefaultDailyLimitForEvent(String eventType) {
        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return 10;
            case "COMMENT":
            case "COMMENT_CREATE":
                return 20;
            case "LIKE":
            case "LIKE_GIVE":
                return 30;
            case "MARKETPLACE_CREATE":
                return 5;
            case "MARKETPLACE_SELL":
            case "COURSE_COMPLETE":
                return 0; // 무제한
            default:
                return 0;
        }
    }

    /**
     * 최소 글자수 기본값
     */
    private Integer getDefaultMinLengthForEvent(String eventType) {
        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return 20;
            case "COMMENT":
            case "COMMENT_CREATE":
                return 5;
            default:
                return 0;
        }
    }

    /**
     * 특정 이벤트에 대한 포인트 값 조회
     * @param channelUid 채널 UID
     * @param eventType 이벤트 타입 (POST, COMMENT, LIKE, ATTENDANCE, MARKETPLACE_CREATE, MARKETPLACE_SELL, COURSE_COMPLETE)
     * @return 해당 이벤트의 포인트 값
     */
    @Transactional(readOnly = true)
    public Integer getPointForEvent(String channelUid, String eventType) {
        PointSetting setting = pointSettingRepository.findByChannelUid(channelUid)
                .orElse(null);

        if (setting == null) {
            // 기본값 반환
            return getDefaultPointForEvent(eventType);
        }

        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return setting.getPostCreate();
            case "COMMENT":
            case "COMMENT_CREATE":
                return setting.getCommentCreate();
            case "LIKE":
            case "LIKE_GIVE":
                return setting.getLikeGive();
            case "ATTENDANCE":
            case "DAILY_ATTENDANCE":
                return setting.getDailyAttendance();
            case "MARKETPLACE_CREATE":
                return setting.getMarketplaceCreate();
            case "MARKETPLACE_SELL":
                return setting.getMarketplaceSell();
            case "COURSE_COMPLETE":
                return setting.getCourseComplete();
            default:
                log.warn("Unknown event type: {}", eventType);
                return 0;
        }
    }

    /**
     * 이벤트 타입별 기본값
     */
    private Integer getDefaultPointForEvent(String eventType) {
        switch (eventType.toUpperCase()) {
            case "POST":
            case "POST_CREATE":
                return 100;
            case "COMMENT":
            case "COMMENT_CREATE":
                return 50;
            case "LIKE":
            case "LIKE_GIVE":
                return 10;
            case "ATTENDANCE":
            case "DAILY_ATTENDANCE":
                return 50;
            case "MARKETPLACE_CREATE":
                return 100;
            case "MARKETPLACE_SELL":
                return 200;
            case "COURSE_COMPLETE":
                return 150;
            default:
                return 0;
        }
    }
}
