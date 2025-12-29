package com.community.cms.api.point.service;

import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.entity.PointHistory;
import com.community.cms.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 포인트 서비스
 * - 포인트는 user 테이블의 전역 포인트(user.point)로 관리됨
 * - 포인트 히스토리는 채널별로 기록됨 (추적 목적)
 * - 악용 방지 로직: 일일 제한, 중복 적립 방지, 최소 글자수, 본인 컨텐츠 제외
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final PointSettingService pointSettingService;

    /**
     * 포인트 적립/차감
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param pointType 포인트 타입 (POST, LIKE, COMMENT, etc.)
     * @param pointAmount 포인트 금액 (양수: 적립, 음수: 차감)
     * @param description 설명
     * @param referenceId 참조 ID (게시글 UID, 댓글 UID 등)
     * @return 저장된 포인트 히스토리
     */
    @Transactional
    public PointHistoryDto addPoint(String userUid, String channelUid, String pointType, 
                                    Integer pointAmount, String description, String referenceId) {
        // 사용자 조회 및 전역 포인트 업데이트
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        // 현재 전역 포인트
        Integer currentGlobalPoint = user.getPoint() != null ? user.getPoint() : 0;
        
        // 새로운 전역 포인트 계산
        Integer newGlobalPoint = currentGlobalPoint + pointAmount;
        
        // 포인트가 음수가 되지 않도록 체크 (차감 시)
        if (newGlobalPoint < 0) {
            throw new RuntimeException("포인트가 부족합니다. 현재 포인트: " + currentGlobalPoint);
        }
        
        // user 테이블의 전역 포인트 업데이트
        user.setPoint(newGlobalPoint);
        userRepository.save(user);
        
        // 포인트 히스토리 생성 (채널별 기록용)
        PointHistory pointHistory = PointHistory.builder()
                .userUid(userUid)
                .channelUid(channelUid)
                .pointType(pointType)
                .pointAmount(pointAmount)
                .currentBalance(newGlobalPoint) // 전역 포인트 잔액으로 기록
                .description(description)
                .referenceId(referenceId)
                .build();
        
        PointHistory saved = pointHistoryRepository.save(pointHistory);
        
        log.info("Point added: userUid={}, channelUid={}, pointType={}, amount={}, globalBalance={}", 
                userUid, channelUid, pointType, pointAmount, newGlobalPoint);
        
        return PointHistoryDto.from(saved);
    }

    /**
     * 사용자의 현재 전역 포인트 조회 (user 테이블)
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID (호환성 유지용, 실제로 사용하지 않음)
     * @return 현재 전역 포인트
     */
    @Transactional(readOnly = true)
    public Integer getCurrentPoint(String userUid, String channelUid) {
        // user 테이블의 전역 포인트 조회
        User user = userRepository.findById(userUid).orElse(null);
        if (user == null) {
            return 0;
        }
        return user.getPoint() != null ? user.getPoint() : 0;
    }
    
    /**
     * 사용자의 현재 전역 포인트 조회 (user 테이블)
     * @param userUid 사용자 UID
     * @return 현재 전역 포인트
     */
    @Transactional(readOnly = true)
    public Integer getCurrentPoint(String userUid) {
        User user = userRepository.findById(userUid).orElse(null);
        if (user == null) {
            return 0;
        }
        return user.getPoint() != null ? user.getPoint() : 0;
    }

    /**
     * 포인트 히스토리 조회 (페이징)
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 포인트 히스토리 페이지
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPointHistory(String userUid, String channelUid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PointHistory> pointHistoryPage = pointHistoryRepository
                .findByUserUidAndChannelUidOrderByCreatedAtDesc(userUid, channelUid, pageable);
        
        // 현재 전역 포인트 조회 (user 테이블)
        Integer currentPoint = getCurrentPoint(userUid);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", pointHistoryPage.getContent().stream()
                .map(PointHistoryDto::from)
                .collect(Collectors.toList()));
        result.put("currentPage", pointHistoryPage.getNumber());
        result.put("totalPages", pointHistoryPage.getTotalPages());
        result.put("totalElements", pointHistoryPage.getTotalElements());
        result.put("currentPoint", currentPoint);
        
        return result;
    }

    /**
     * 이벤트 발생 시 포인트 자동 적립
     * 포인트 설정에 따라 해당 이벤트의 포인트를 적립합니다.
     * 
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param eventType 이벤트 타입 (POST, COMMENT, LIKE, ATTENDANCE, MARKETPLACE_CREATE, MARKETPLACE_SELL, COURSE_COMPLETE)
     * @param description 설명
     * @param referenceId 참조 ID
     * @return 저장된 포인트 히스토리 (포인트가 0이면 null)
     */
    @Transactional
    public PointHistoryDto addPointForEvent(String userUid, String channelUid, String eventType, 
                                            String description, String referenceId) {
        // 채널의 포인트 설정에서 해당 이벤트의 포인트 조회
        Integer pointAmount = pointSettingService.getPointForEvent(channelUid, eventType);
        
        // 포인트가 0이면 적립하지 않음
        if (pointAmount == null || pointAmount <= 0) {
            log.debug("No point configured for event: channelUid={}, eventType={}", channelUid, eventType);
            return null;
        }
        
        // 포인트 적립
        return addPoint(userUid, channelUid, eventType, pointAmount, description, referenceId);
    }

    /**
     * 이벤트 발생 시 포인트 자동 적립 (악용 방지 로직 포함)
     * 
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @param eventType 이벤트 타입 (POST, COMMENT, LIKE, MARKETPLACE_CREATE, MARKETPLACE_SELL, COURSE_COMPLETE)
     * @param description 설명
     * @param referenceId 참조 ID (게시글 UID, 댓글 UID 등)
     * @param contentLength 컨텐츠 길이 (게시글/댓글 최소 글자수 체크용, 해당 없으면 0)
     * @param contentOwnerUid 컨텐츠 소유자 UID (댓글/좋아요의 경우 원글 작성자, 해당 없으면 null)
     * @return 저장된 포인트 히스토리 (적립 불가 시 null)
     */
    @Transactional
    public PointHistoryDto addPointForEventWithValidation(String userUid, String channelUid, String eventType, 
                                                          String description, String referenceId,
                                                          int contentLength, String contentOwnerUid) {
        // 채널의 포인트 설정에서 해당 이벤트의 포인트 조회
        Integer pointAmount = pointSettingService.getPointForEvent(channelUid, eventType);
        
        // 포인트가 0이면 적립하지 않음
        if (pointAmount == null || pointAmount <= 0) {
            log.debug("No point configured for event: channelUid={}, eventType={}", channelUid, eventType);
            return null;
        }
        
        // ========== 악용 방지 체크 시작 ==========
        
        // 1. 본인 컨텐츠 체크 (댓글, 좋아요의 경우 본인 게시글에 대한 액션은 포인트 미지급)
        if (contentOwnerUid != null && contentOwnerUid.equals(userUid)) {
            log.debug("Point denied: Self-content action. userUid={}, eventType={}, referenceId={}", 
                    userUid, eventType, referenceId);
            return null;
        }
        
        // 2. 최소 글자수 체크 (게시글, 댓글)
        Integer minLength = pointSettingService.getMinLengthForEvent(channelUid, eventType);
        if (minLength != null && minLength > 0 && contentLength < minLength) {
            log.debug("Point denied: Content too short. userUid={}, eventType={}, length={}, minLength={}", 
                    userUid, eventType, contentLength, minLength);
            return null;
        }
        
        // 3. 중복 적립 체크 (동일 참조 ID에 대한 중복 적립 방지)
        // - 댓글: 같은 게시글에 여러 댓글 달아도 1회만 적립
        // - 좋아요: 같은 게시글에 좋아요해도 1회만 적립
        // - 강좌 수강 완료: 같은 강좌 수강해도 1회만 적립
        boolean isDuplicateEvent = "COMMENT".equalsIgnoreCase(eventType) 
                || "COMMENT_CREATE".equalsIgnoreCase(eventType)
                || "LIKE".equalsIgnoreCase(eventType) 
                || "LIKE_GIVE".equalsIgnoreCase(eventType)
                || "COURSE_COMPLETE".equalsIgnoreCase(eventType);
        
        if (isDuplicateEvent && referenceId != null) {
            boolean alreadyEarned = pointHistoryRepository
                    .existsEarningByReference(userUid, channelUid, eventType, referenceId);
            if (alreadyEarned) {
                log.debug("Point denied: Duplicate earning. userUid={}, eventType={}, referenceId={}", 
                        userUid, eventType, referenceId);
                return null;
            }
        }
        
        // 4. 일일 적립 횟수 제한 체크
        Integer dailyLimit = pointSettingService.getDailyLimitForEvent(channelUid, eventType);
        if (dailyLimit != null && dailyLimit > 0) {
            LocalDate today = LocalDate.now();
            int todayEarnings = pointHistoryRepository.countDailyEarnings(userUid, channelUid, eventType, today);
            if (todayEarnings >= dailyLimit) {
                log.debug("Point denied: Daily limit reached. userUid={}, eventType={}, todayEarnings={}, limit={}", 
                        userUid, eventType, todayEarnings, dailyLimit);
                return null;
            }
        }
        
        // ========== 악용 방지 체크 통과 ==========
        
        // 포인트 적립 (point_history에 기록되므로 별도 이력 저장 불필요)
        PointHistoryDto result = addPoint(userUid, channelUid, eventType, pointAmount, description, referenceId);
        
        return result;
    }
}
