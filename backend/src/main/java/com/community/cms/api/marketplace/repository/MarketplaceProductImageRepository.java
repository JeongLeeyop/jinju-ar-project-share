package com.community.cms.api.marketplace.repository;

import com.community.cms.entity.MarketplaceProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장터 상품 이미지 Repository
 */
@Repository
public interface MarketplaceProductImageRepository extends JpaRepository<MarketplaceProductImage, Long> {
    
    List<MarketplaceProductImage> findByProductUidOrderByDisplayOrderAsc(String productUid);
    
    Optional<MarketplaceProductImage> findByProductUidAndIsThumbnailTrue(String productUid);
    
    void deleteByProductUid(String productUid);
}
