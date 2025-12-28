package com.community.cms.api.marketplace.repository;

import com.community.cms.entity.OfflineMarketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 오프라인 장터 Repository
 */
@Repository
public interface OfflineMarketplaceRepository extends JpaRepository<OfflineMarketplace, Long> {
    
    Optional<OfflineMarketplace> findByUid(String uid);
    
    List<OfflineMarketplace> findByChannelUidAndIsActiveOrderByCreatedAtDesc(String channelUid, boolean isActive);
    
    List<OfflineMarketplace> findByChannelUidOrderByCreatedAtDesc(String channelUid);
    
    boolean existsByChannelUidAndName(String channelUid, String name);
}
