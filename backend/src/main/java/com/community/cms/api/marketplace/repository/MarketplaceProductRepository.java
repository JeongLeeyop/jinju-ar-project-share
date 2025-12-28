package com.community.cms.api.marketplace.repository;

import com.community.cms.entity.MarketplaceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장터 상품 Repository
 */
@Repository
public interface MarketplaceProductRepository extends JpaRepository<MarketplaceProduct, Long> {
    
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
    
    // 채널의 모든 상품 (메인 + 오프라인)
    Page<MarketplaceProduct> findByChannelUidOrderByCreatedAtDesc(String channelUid, Pageable pageable);
    
    // 채널의 모든 상품 (카테고리별)
    Page<MarketplaceProduct> findByChannelUidAndCategoryOrderByCreatedAtDesc(
            String channelUid, String category, Pageable pageable);
    
    // 조회수 증가
    @Modifying
    @Query("UPDATE MarketplaceProduct p SET p.viewCount = p.viewCount + 1 WHERE p.uid = :uid")
    void incrementViewCount(@Param("uid") String uid);
}
