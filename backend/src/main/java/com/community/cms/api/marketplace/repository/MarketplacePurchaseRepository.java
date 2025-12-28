package com.community.cms.api.marketplace.repository;

import com.community.cms.entity.MarketplacePurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장터 구매 내역 Repository
 */
@Repository
public interface MarketplacePurchaseRepository extends JpaRepository<MarketplacePurchase, Long> {
    
    Optional<MarketplacePurchase> findByUid(String uid);
    
    // 구매자의 구매 내역
    Page<MarketplacePurchase> findByBuyerUidOrderByPurchasedAtDesc(String buyerUid, Pageable pageable);
    
    // 구매자의 특정 채널 구매 내역
    @Query("SELECT mp FROM MarketplacePurchase mp JOIN MarketplaceProduct p ON mp.productUid = p.uid " +
           "WHERE mp.buyerUid = :buyerUid AND p.channelUid = :channelUid ORDER BY mp.purchasedAt DESC")
    Page<MarketplacePurchase> findByBuyerUidAndChannelUid(@Param("buyerUid") String buyerUid, 
                                                          @Param("channelUid") String channelUid, 
                                                          Pageable pageable);
    
    // 구매자의 온라인 상품 구매 내역 (offlineMarketplaceUid가 NULL)
    @Query("SELECT mp FROM MarketplacePurchase mp JOIN MarketplaceProduct p ON mp.productUid = p.uid " +
           "WHERE mp.buyerUid = :buyerUid AND p.channelUid = :channelUid AND p.offlineMarketplaceUid IS NULL " +
           "ORDER BY mp.purchasedAt DESC")
    Page<MarketplacePurchase> findByBuyerUidAndChannelUidAndOnline(@Param("buyerUid") String buyerUid, 
                                                                   @Param("channelUid") String channelUid, 
                                                                   Pageable pageable);
    
    // 구매자의 오프라인 상품 구매 내역 (offlineMarketplaceUid가 NOT NULL)
    @Query("SELECT mp FROM MarketplacePurchase mp JOIN MarketplaceProduct p ON mp.productUid = p.uid " +
           "WHERE mp.buyerUid = :buyerUid AND p.channelUid = :channelUid AND p.offlineMarketplaceUid IS NOT NULL " +
           "ORDER BY mp.purchasedAt DESC")
    Page<MarketplacePurchase> findByBuyerUidAndChannelUidAndOffline(@Param("buyerUid") String buyerUid, 
                                                                    @Param("channelUid") String channelUid, 
                                                                    Pageable pageable);
    
    // 판매자의 상품별 구매 내역
    List<MarketplacePurchase> findByProductUidAndSellerUidOrderByPurchasedAtDesc(
            String productUid, String sellerUid);
    
    // 특정 상품의 모든 구매 내역
    List<MarketplacePurchase> findByProductUidOrderByPurchasedAtDesc(String productUid);
}
