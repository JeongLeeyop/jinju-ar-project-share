package com.community.cms.api.marketplace.repository;

import com.community.cms.entity.MarketplaceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장터 상품 Repository
 */
@Repository
public interface MarketplaceProductRepository extends JpaRepository<MarketplaceProduct, Long>, QuerydslPredicateExecutor<MarketplaceProduct> {
    
    Optional<MarketplaceProduct> findByUid(String uid);
    
    // 메인 장터 상품 목록 (offlineMarketplaceUid가 NULL)
    Page<MarketplaceProduct> findByChannelUidAndOfflineMarketplaceUidIsNullOrderByCreatedAtDesc(
            String channelUid, Pageable pageable);
    
    // 메인 장터 카테고리별 상품 목록
    Page<MarketplaceProduct> findByChannelUidAndOfflineMarketplaceUidIsNullAndCategoryOrderByCreatedAtDesc(
            String channelUid, String category, Pageable pageable);
    
    // 오프라인 장터 상품 목록
    Page<MarketplaceProduct> findByOfflineMarketplaceUidOrderByCreatedAtDesc(
            String offlineMarketplaceUid, Pageable pageable);
    
    // 오프라인 장터 카테고리별 상품 목록
    Page<MarketplaceProduct> findByOfflineMarketplaceUidAndCategoryOrderByCreatedAtDesc(
            String offlineMarketplaceUid, String category, Pageable pageable);
    
    // 판매자의 상품 목록
    Page<MarketplaceProduct> findBySellerUidOrderByCreatedAtDesc(String sellerUid, Pageable pageable);
    
    // 판매자의 온라인 상품 목록 (offlineMarketplaceUid가 NULL)
    Page<MarketplaceProduct> findBySellerUidAndOfflineMarketplaceUidIsNullOrderByCreatedAtDesc(
            String sellerUid, Pageable pageable);
    
    // 판매자의 오프라인 상품 목록 (offlineMarketplaceUid가 NOT NULL)
    Page<MarketplaceProduct> findBySellerUidAndOfflineMarketplaceUidIsNotNullOrderByCreatedAtDesc(
            String sellerUid, Pageable pageable);
    
    // 판매자의 특정 채널 상품 목록
    Page<MarketplaceProduct> findBySellerUidAndChannelUidOrderByCreatedAtDesc(
            String sellerUid, String channelUid, Pageable pageable);
    
    // 판매자의 특정 채널 온라인 상품 목록
    Page<MarketplaceProduct> findBySellerUidAndChannelUidAndOfflineMarketplaceUidIsNullOrderByCreatedAtDesc(
            String sellerUid, String channelUid, Pageable pageable);
    
    // 판매자의 특정 채널 오프라인 상품 목록
    Page<MarketplaceProduct> findBySellerUidAndChannelUidAndOfflineMarketplaceUidIsNotNullOrderByCreatedAtDesc(
            String sellerUid, String channelUid, Pageable pageable);
    
    // 채널의 모든 상품 (메인 + 오프라인)
    Page<MarketplaceProduct> findByChannelUidOrderByCreatedAtDesc(String channelUid, Pageable pageable);
    
    // 채널의 모든 상품 (카테고리별)
    Page<MarketplaceProduct> findByChannelUidAndCategoryOrderByCreatedAtDesc(
            String channelUid, String category, Pageable pageable);
    
    // 채널별 상품 목록 (관리자용)
    Page<MarketplaceProduct> findByChannelUid(String channelUid, Pageable pageable);
    
    // 상태별 상품 수 조회
    long countByStatus(String status);
    
    // 채널별 상품 수 조회
    long countByChannelUid(String channelUid);
    
    // 채널별 판매완료된 상품의 총 매출 조회
    @Query(value = "SELECT COALESCE(SUM(price), 0) FROM marketplace_product WHERE channel_uid = :channelUid AND status = 'SOLD_OUT'", nativeQuery = true)
    Long getTotalSalesByChannelUid(@Param("channelUid") String channelUid);
    
    // 전체 판매완료된 상품의 총 매출 조회 (delete_status=false인 채널만)
    @Query(value = "SELECT COALESCE(SUM(mp.price), 0) FROM marketplace_product mp INNER JOIN channel c ON mp.channel_uid = c.uid WHERE mp.status = 'SOLD_OUT' AND c.delete_status = 0", nativeQuery = true)
    Long getTotalSales();
    
    // 조회수 증가
    @Modifying
    @Query("UPDATE MarketplaceProduct p SET p.viewCount = p.viewCount + 1 WHERE p.uid = :uid")
    void incrementViewCount(@Param("uid") String uid);
}
