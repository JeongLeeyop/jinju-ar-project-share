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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 포인트 서비스
 * - 포인트는 user 테이블의 전역 포인트(user.point)로 관리됨
 * - 포인트 히스토리는 채널별로 기록됨 (추적 목적)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;

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
}
