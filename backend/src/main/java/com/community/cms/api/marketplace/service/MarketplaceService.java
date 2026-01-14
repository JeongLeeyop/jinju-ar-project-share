package com.community.cms.api.marketplace.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.community.cms.entity.MarketplaceProduct;
import com.community.cms.entity.QMarketplaceProduct;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.QChannel;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceProductRepository productRepository;
    private final ChannelRepository channelRepository;

    /**
     * 관리자용 상품 목록 조회
     */
    public Page<MarketplaceProduct> adminList(String channelUid, String status, String keyword, Pageable pageable) {
        QMarketplaceProduct product = QMarketplaceProduct.marketplaceProduct;
        QChannel channel = QChannel.channel;
        
        BooleanBuilder builder = new BooleanBuilder();
        
        // delete_status=false인 채널의 상품만 조회
        builder.and(product.channelUid.in(
            JPAExpressions
                .select(channel.uid)
                .from(channel)
                .where(channel.deleteStatus.eq(false))
        ));
        
        // 채널 필터
        if (StringUtils.hasText(channelUid)) {
            builder.and(product.channelUid.eq(channelUid));
        }
        
        // 상태 필터
        if (StringUtils.hasText(status)) {
            builder.and(product.status.eq(status));
        }
        
        // 키워드 검색 (상품명)
        if (StringUtils.hasText(keyword)) {
            builder.and(product.title.containsIgnoreCase(keyword));
        }
        
        Page<MarketplaceProduct> products = productRepository.findAll(builder, pageable);
        
        // channelName 채우기
        List<MarketplaceProduct> productsWithChannel = products.getContent().stream()
            .map(p -> {
                channelRepository.findByUid(p.getChannelUid())
                    .ifPresent(c -> p.setChannelName(c.getName()));
                return p;
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(productsWithChannel, pageable, products.getTotalElements());
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
