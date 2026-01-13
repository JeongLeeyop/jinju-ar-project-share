package com.community.cms.api.marketplace.service;

import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.marketplace.dto.OfflineMarketplaceDto;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;
import com.community.cms.api.marketplace.repository.OfflineMarketplaceRepository;
import com.community.cms.entity.OfflineMarketplace;
import com.community.cms.entity2.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 오프라인 장터 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OfflineMarketplaceService {

    private final OfflineMarketplaceRepository offlineMarketplaceRepository;
    private final MarketplaceProductRepository productRepository;
    private final ChannelRepository channelRepository;

    /**
     * 오프라인 장터 생성
     */
    @Transactional
    public OfflineMarketplaceDto createOfflineMarketplace(
            OfflineMarketplaceDto.CreateRequest request,
            String channelDomain,
            String adminUid) {

        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);

        // 중복 이름 체크
        if (offlineMarketplaceRepository.existsByChannelUidAndName(channelUid, request.getName())) {
            throw new RuntimeException("이미 존재하는 장터 이름입니다");
        }

        OfflineMarketplace marketplace = OfflineMarketplace.builder()
                .uid(UUID.randomUUID().toString())
                .channelUid(channelUid)
                .name(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .createdBy(adminUid)
                .build();

        OfflineMarketplace saved = offlineMarketplaceRepository.save(marketplace);
        
        log.info("Offline marketplace created: {} by {}", saved.getUid(), adminUid);
        
        return toDto(saved, 0);
    }

    /**
     * 채널의 오프라인 장터 목록 조회
     */
    @Transactional(readOnly = true)
    public List<OfflineMarketplaceDto> getChannelOfflineMarketplaces(String channelDomain, boolean activeOnly) {
        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);
        
        List<OfflineMarketplace> marketplaces;
        
        if (activeOnly) {
            marketplaces = offlineMarketplaceRepository
                    .findByChannelUidAndIsActiveOrderByCreatedAtDesc(channelUid, true);
        } else {
            marketplaces = offlineMarketplaceRepository
                    .findByChannelUidOrderByCreatedAtDesc(channelUid);
        }

        return marketplaces.stream()
                .map(m -> {
                    int productCount = productRepository
                            .findByOfflineMarketplaceUidOrderByCreatedAtDesc(m.getUid(), null)
                            .getContent()
                            .size();
                    return toDto(m, productCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * Channel domain → uid 변환
     */
    private String getChannelUidByDomain(String domain) {
        return channelRepository.findByDomain(domain)
                .map(Channel::getUid)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
    }

    /**
     * 오프라인 장터 상세 조회
     */
    @Transactional(readOnly = true)
    public OfflineMarketplaceDto getOfflineMarketplace(String uid, String currentUserUid) {
        OfflineMarketplace marketplace = offlineMarketplaceRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));

        int productCount = productRepository
                .findByOfflineMarketplaceUidOrderByCreatedAtDesc(uid, null)
                .getContent()
                .size();

        return toDto(marketplace, productCount, currentUserUid);
    }

    /**
     * 오프라인 장터 수정
     */
    @Transactional
    public OfflineMarketplaceDto updateOfflineMarketplace(
            String uid,
            OfflineMarketplaceDto.UpdateRequest request,
            String adminUid) {

        OfflineMarketplace marketplace = offlineMarketplaceRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));

        // 이름 변경 시 중복 체크
        if (!marketplace.getName().equals(request.getName()) &&
                offlineMarketplaceRepository.existsByChannelUidAndName(
                        marketplace.getChannelUid(), request.getName())) {
            throw new RuntimeException("이미 존재하는 장터 이름입니다");
        }

        marketplace.setName(request.getName());
        marketplace.setDescription(request.getDescription());
        
        if (request.getIsActive() != null) {
            marketplace.setActive(request.getIsActive());
        }

        OfflineMarketplace saved = offlineMarketplaceRepository.save(marketplace);
        
        log.info("Offline marketplace updated: {} by {}", uid, adminUid);

        int productCount = productRepository
                .findByOfflineMarketplaceUidOrderByCreatedAtDesc(uid, null)
                .getContent()
                .size();

        return toDto(saved, productCount);
    }

    /**
     * 오프라인 장터 삭제
     */
    @Transactional
    public void deleteOfflineMarketplace(String uid, String adminUid) {
        OfflineMarketplace marketplace = offlineMarketplaceRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));

        // 상품이 있는지 확인
        int productCount = productRepository
                .findByOfflineMarketplaceUidOrderByCreatedAtDesc(uid, null)
                .getContent()
                .size();

        if (productCount > 0) {
            throw new RuntimeException("상품이 등록되어 있는 장터는 삭제할 수 없습니다");
        }

        offlineMarketplaceRepository.delete(marketplace);
        
        log.info("Offline marketplace deleted: {} by {}", uid, adminUid);
    }

    /**
     * Entity to DTO
     */
    private OfflineMarketplaceDto toDto(OfflineMarketplace entity, int productCount, String currentUserUid) {
        boolean isCreator = currentUserUid != null && currentUserUid.equals(entity.getCreatedBy());
        
        return OfflineMarketplaceDto.builder()
                .uid(entity.getUid())
                .channelUid(entity.getChannelUid())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.isActive())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .productCount(productCount)
                .isCreator(isCreator)
                .build();
    }
    
    /**
     * Entity to DTO (without user context)
     */
    private OfflineMarketplaceDto toDto(OfflineMarketplace entity, int productCount) {
        return toDto(entity, productCount, null);
    }
}
