package com.community.cms.api.point.service;

import com.community.cms.api.point.dto.PointHistoryDto;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.entity.PointHistory;
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
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;

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
        // 현재 잔액 조회
        Integer currentBalance = pointHistoryRepository.findCurrentBalance(userUid, channelUid)
                .orElse(0);
        
        // 새로운 잔액 계산
        Integer newBalance = currentBalance + pointAmount;
        
        // 포인트 히스토리 생성
        PointHistory pointHistory = PointHistory.builder()
                .userUid(userUid)
                .channelUid(channelUid)
                .pointType(pointType)
                .pointAmount(pointAmount)
                .currentBalance(newBalance)
                .description(description)
                .referenceId(referenceId)
                .build();
        
        PointHistory saved = pointHistoryRepository.save(pointHistory);
        
        log.info("Point added: userUid={}, channelUid={}, pointType={}, amount={}, balance={}", 
                userUid, channelUid, pointType, pointAmount, newBalance);
        
        return PointHistoryDto.from(saved);
    }

    /**
     * 사용자의 현재 포인트 조회
     * @param userUid 사용자 UID
     * @param channelUid 채널 UID
     * @return 현재 포인트
     */
    @Transactional(readOnly = true)
    public Integer getCurrentPoint(String userUid, String channelUid) {
        return pointHistoryRepository.findCurrentBalance(userUid, channelUid)
                .orElse(0);
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
        
        // 현재 포인트 조회
        Integer currentPoint = getCurrentPoint(userUid, channelUid);
        
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
