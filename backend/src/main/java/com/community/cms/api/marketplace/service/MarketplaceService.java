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
import com.community.cms.entity.MarketplaceProductImage;
import com.community.cms.entity.QMarketplaceProduct;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.QChannel;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;
import com.community.cms.api.marketplace.repository.MarketplaceProductImageRepository;
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
    private final MarketplaceProductImageRepository productImageRepository;
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
        
        // channelName과 첫 번째 이미지 채우기
        List<MarketplaceProduct> productsWithChannel = products.getContent().stream()
            .map(p -> {
                // channelName 설정
                channelRepository.findByUid(p.getChannelUid())
                    .ifPresent(c -> p.setChannelName(c.getName()));
                
                // 첫 번째 이미지 설정 (display_order 기준)
                List<MarketplaceProductImage> images = productImageRepository
                    .findByProductUidOrderByDisplayOrderAsc(p.getUid());
                if (!images.isEmpty()) {
                    p.setIconFileUid(images.get(0).getFileUid());
                }
                
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
     * 장터 통계 조회 (필터 적용)
     */
    public Map<String, Object> getStats(String channelUid, String status, String keyword) {
        QMarketplaceProduct product = QMarketplaceProduct.marketplaceProduct;
        QChannel channel = QChannel.channel;
        
        // 기본 빌더: delete_status=false인 채널의 상품만
        BooleanBuilder baseBuilder = new BooleanBuilder();
        baseBuilder.and(product.channelUid.in(
            JPAExpressions
                .select(channel.uid)
                .from(channel)
                .where(channel.deleteStatus.eq(false))
        ));
        
        // 채널 필터
        if (StringUtils.hasText(channelUid)) {
            baseBuilder.and(product.channelUid.eq(channelUid));
        }
        
        // 키워드 검색
        if (StringUtils.hasText(keyword)) {
            baseBuilder.and(product.title.containsIgnoreCase(keyword));
        }
        
        // 총 상품 수 (필터 적용)
        long totalProducts = productRepository.count(baseBuilder);
        
        // 판매중 (ACTIVE) - 필터링
        BooleanBuilder activeBuilder = new BooleanBuilder(baseBuilder);
        if (StringUtils.hasText(status)) {
            // status 필터가 있으면 해당 status만 계산
            if ("ACTIVE".equals(status)) {
                activeBuilder.and(product.status.eq("ACTIVE"));
            }
        } else {
            activeBuilder.and(product.status.eq("ACTIVE"));
        }
        long saleCount = StringUtils.hasText(status) && !"ACTIVE".equals(status) ? 0 : productRepository.count(activeBuilder);
        
        // 판매완료 (SOLD_OUT) - 필터링
        BooleanBuilder soldBuilder = new BooleanBuilder(baseBuilder);
        if (StringUtils.hasText(status)) {
            // status 필터가 있으면 해당 status만 계산
            if ("SOLD_OUT".equals(status)) {
                soldBuilder.and(product.status.eq("SOLD_OUT"));
            }
        } else {
            soldBuilder.and(product.status.eq("SOLD_OUT"));
        }
        long soldCount = StringUtils.hasText(status) && !"SOLD_OUT".equals(status) ? 0 : productRepository.count(soldBuilder);
        
        // 판매완료된 상품의 총 매출 (필터 적용)
        BooleanBuilder salesBuilder = new BooleanBuilder(baseBuilder);
        salesBuilder.and(product.status.eq("SOLD_OUT"));
        
        // 직접 계산 (필터링된 결과에서 price 합계)
        List<MarketplaceProduct> soldProducts = (List<MarketplaceProduct>) productRepository.findAll(salesBuilder);
        Long totalSales = soldProducts.stream()
            .mapToLong(p -> p.getPrice() != null ? p.getPrice() : 0)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", totalProducts);
        stats.put("saleCount", saleCount);
        stats.put("soldCount", soldCount);
        stats.put("totalSales", totalSales);
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
