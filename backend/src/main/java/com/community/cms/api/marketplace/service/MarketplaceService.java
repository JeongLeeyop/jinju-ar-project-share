package com.community.cms.api.marketplace.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.community.cms.entity.MarketplaceProduct;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceProductRepository productRepository;

    /**
     * 관리자용 상품 목록 조회
     */
    public Page<MarketplaceProduct> adminList(String channelUid, String status, String keyword, Pageable pageable) {
        if (channelUid != null && !channelUid.isEmpty()) {
            return productRepository.findByChannelUid(channelUid, pageable);
        }
        return productRepository.findAll(pageable);
    }

    /**
     * 관리자용 상품 상세 조회
     */
    public MarketplaceProduct adminDetail(String uid) {
        return productRepository.findByUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    /**
     * 커뮤니티별 상품 목록 조회
     */
    public Page<MarketplaceProduct> adminListByChannel(String channelUid, Pageable pageable) {
        return productRepository.findByChannelUid(channelUid, pageable);
    }

    /**
     * 장터 통계 조회
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("sellingProducts", productRepository.countByStatus("SELLING"));
        stats.put("soldProducts", productRepository.countByStatus("SOLD"));
        return stats;
    }

    /**
     * 매출 리포트 조회
     */
    public Map<String, Object> getSalesReport(String channelUid, String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();
        // 기본 통계
        report.put("totalSales", 0);
        report.put("totalAmount", 0);
        report.put("byChannel", List.of());
        report.put("dailySales", List.of());
        return report;
    }
}
