package com.community.cms.api.marketplace.service;

import com.community.cms.api.channel.repository.ChannelMemberPermissionRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.marketplace.dto.MarketplaceProductDto;
import com.community.cms.api.marketplace.repository.MarketplaceProductImageRepository;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;
import com.community.cms.api.marketplace.repository.MarketplacePurchaseRepository;
import com.community.cms.api.marketplace.repository.OfflineMarketplaceRepository;
import com.community.cms.api.point.service.PointService;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.ChannelMemberPermission;
import com.community.cms.entity.MarketplaceProduct;
import com.community.cms.entity.MarketplaceProductImage;
import com.community.cms.entity.MarketplacePurchase;
import com.community.cms.entity.OfflineMarketplace;
import com.community.cms.entity.User;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 장터 상품 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplaceProductService {

    private final MarketplaceProductRepository productRepository;
    private final MarketplaceProductImageRepository imageRepository;
    private final MarketplacePurchaseRepository purchaseRepository;
    private final OfflineMarketplaceRepository offlineMarketplaceRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelMemberPermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PointService pointService;

    /**
     * 상품 등록
     */
    @Transactional
    public MarketplaceProductDto createProduct(
            MarketplaceProductDto.CreateRequest request,
            String channelDomain,
            String userUid,
            String userName) {

        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);

        // 권한 체크
        checkProductCreatePermission(channelDomain, userUid, request.getOfflineMarketplaceUid());

        // 오프라인 장터인 경우 존재 확인 및 상품 타입 검증
        if (request.getOfflineMarketplaceUid() != null) {
            offlineMarketplaceRepository.findByUid(request.getOfflineMarketplaceUid())
                    .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));
            
            // 오프라인 장터는 판매(SALE) 유형만 허용
            if (!"SALE".equals(request.getProductType())) {
                throw new RuntimeException("오프라인 장터에서는 판매 상품만 등록할 수 있습니다");
            }
        }

        String productUid = UUID.randomUUID().toString();

        // 재고가 없으면 기본값 1로 설정
        Integer stock = request.getStock() != null ? request.getStock() : 1;

        MarketplaceProduct product = MarketplaceProduct.builder()
                .uid(productUid)
                .channelUid(channelUid) // 변환된 channelUid 사용
                .offlineMarketplaceUid(request.getOfflineMarketplaceUid())
                .category(request.getProductType()) // productType을 category로 저장
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(stock) // 기본값 1
                .location(request.getLocation()) // 거래 장소
                .sellerUid(userUid)
                .sellerName(userName)
                .status("ACTIVE")
                .viewCount(0)
                .build();

        MarketplaceProduct saved = productRepository.save(product);

        // 이미지 저장
        if (request.getImageUids() != null && !request.getImageUids().isEmpty()) {
            saveProductImages(productUid, request.getImageUids());
        }

        // 포인트 적립 (장터 상품 등록) - 일일 횟수 제한 적용
        try {
            pointService.addPointForEventWithValidation(
                    userUid,
                    channelUid,
                    "MARKETPLACE_CREATE",
                    "장터 상품 등록: " + request.getTitle(),
                    productUid,
                    0,  // 글자수 체크 불필요
                    null  // 본인 컨텐츠 체크 불필요
            );
        } catch (Exception e) {
            log.warn("Failed to add point for marketplace product creation: {}", e.getMessage());
        }

        log.info("Marketplace product created: {} by {}", productUid, userUid);

        return toDto(saved, userUid);
    }

    /**
     * 상품 목록 조회 (메인 장터)
     */
    @Transactional(readOnly = true)
    public Page<MarketplaceProductDto> getMainMarketplaceProducts(
            String channelDomain,
            String category,
            String currentUserUid,
            Pageable pageable) {

        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);

        Page<MarketplaceProduct> products;

        if (category != null && !category.isEmpty()) {
            products = productRepository
                    .findByChannelUidAndOfflineMarketplaceUidIsNullAndCategoryOrderByCreatedAtDesc(
                            channelUid, category, pageable);
        } else {
            products = productRepository
                    .findByChannelUidAndOfflineMarketplaceUidIsNullOrderByCreatedAtDesc(
                            channelUid, pageable);
        }

        return products.map(p -> toDto(p, currentUserUid));
    }

    /**
     * 상품 목록 조회 (오프라인 장터)
     */
    @Transactional(readOnly = true)
    public Page<MarketplaceProductDto> getOfflineMarketplaceProducts(
            String offlineMarketplaceUid,
            String category,
            String currentUserUid,
            Pageable pageable) {

        // 오프라인 장터 존재 확인
        offlineMarketplaceRepository.findByUid(offlineMarketplaceUid)
                .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));

        Page<MarketplaceProduct> products;

        if (category != null && !category.isEmpty()) {
            products = productRepository
                    .findByOfflineMarketplaceUidAndCategoryOrderByCreatedAtDesc(
                            offlineMarketplaceUid, category, pageable);
        } else {
            products = productRepository
                    .findByOfflineMarketplaceUidOrderByCreatedAtDesc(
                            offlineMarketplaceUid, pageable);
        }

        return products.map(p -> toDto(p, currentUserUid));
    }

    /**
     * 내 등록 상품 목록
     */
    @Transactional(readOnly = true)
    public Page<MarketplaceProductDto> getMyProducts(String userUid, Pageable pageable) {
        Page<MarketplaceProduct> products = productRepository
                .findBySellerUidOrderByCreatedAtDesc(userUid, pageable);

        return products.map(p -> toDto(p, userUid));
    }

    /**
     * 채널의 모든 상품 조회 (메인 + 오프라인)
     */
    @Transactional(readOnly = true)
    public Page<MarketplaceProductDto> getAllChannelProducts(
            String channelDomain,
            String productType,
            String currentUserUid,
            Pageable pageable) {

        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);

        Page<MarketplaceProduct> products;

        if (productType != null && !productType.isEmpty()) {
            products = productRepository
                    .findByChannelUidAndCategoryOrderByCreatedAtDesc(
                            channelUid, productType, pageable);
        } else {
            products = productRepository
                    .findByChannelUidOrderByCreatedAtDesc(channelUid, pageable);
        }

        return products.map(p -> toDto(p, currentUserUid));
    }

    /**
     * 상품 상세 조회
     */
    @Transactional
    public MarketplaceProductDto getProduct(String uid, String currentUserUid) {
        MarketplaceProduct product = productRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 조회수 증가
        productRepository.incrementViewCount(uid);

        return toDto(product, currentUserUid);
    }

    /**
     * 상품 수정
     */
    @Transactional
    public MarketplaceProductDto updateProduct(
            String uid,
            MarketplaceProductDto.UpdateRequest request,
            String userUid) {

        MarketplaceProduct product = productRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 판매자 확인
        if (!product.getSellerUid().equals(userUid)) {
            throw new RuntimeException("상품을 수정할 권한이 없습니다");
        }

        product.setCategory(request.getCategory());
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        String previousStatus = product.getStatus();
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
            
            // 상태가 SOLD(판매완료)로 변경된 경우 포인트 적립 - 일일 횟수 제한 적용
            if ("SOLD".equalsIgnoreCase(request.getStatus()) && !"SOLD".equalsIgnoreCase(previousStatus)) {
                try {
                    pointService.addPointForEventWithValidation(
                            product.getSellerUid(),
                            product.getChannelUid(),
                            "MARKETPLACE_SELL",
                            "장터 상품 판매 완료: " + product.getTitle(),
                            product.getUid(),
                            0,  // 글자수 체크 불필요
                            null  // 본인 컨텐츠 체크 불필요
                    );
                } catch (Exception e) {
                    log.warn("Failed to add point for marketplace product sell: {}", e.getMessage());
                }
            }
        }

        // 이미지 업데이트
        if (request.getImageUids() != null) {
            imageRepository.deleteByProductUid(uid);
            if (!request.getImageUids().isEmpty()) {
                saveProductImages(uid, request.getImageUids());
            }
        }

        MarketplaceProduct saved = productRepository.save(product);

        log.info("Marketplace product updated: {} by {}", uid, userUid);

        return toDto(saved, userUid);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(String uid, String userUid) {
        MarketplaceProduct product = productRepository.findByUid(uid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 판매자 확인
        if (!product.getSellerUid().equals(userUid)) {
            throw new RuntimeException("상품을 삭제할 권한이 없습니다");
        }

        // 이미지 삭제
        imageRepository.deleteByProductUid(uid);

        // 상품 삭제
        productRepository.delete(product);

        log.info("Marketplace product deleted: {} by {}", uid, userUid);
    }

    /**
     * 상품 이미지 저장
     */
    private void saveProductImages(String productUid, List<String> imageUids) {
        for (int i = 0; i < imageUids.size(); i++) {
            MarketplaceProductImage image = MarketplaceProductImage.builder()
                    .productUid(productUid)
                    .fileUid(imageUids.get(i))
                    .displayOrder(i)
                    .isThumbnail(i == 0) // 첫 번째 이미지를 썸네일로
                    .build();

            imageRepository.save(image);
        }
    }

    /**
     * 상품 등록 권한 체크
     */
    private void checkProductCreatePermission(String channelDomain, String userUid, String offlineMarketplaceUid) {
        // domain → Channel 조회
        Channel channel = channelRepository.findByDomain(channelDomain)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));

        // 커뮤니티 관리자(채널 소유자)인 경우 무조건 등록 가능
        if (userUid.equals(channel.getUserUid())) {
            log.info("Community admin {} registering product in channel {}", userUid, channelDomain);
            return;
        }

        // 오프라인 장터인 경우 생성자만 상품 등록 가능
        if (offlineMarketplaceUid != null) {
            OfflineMarketplace offlineMarketplace = offlineMarketplaceRepository.findByUid(offlineMarketplaceUid)
                    .orElseThrow(() -> new RuntimeException("오프라인 장터를 찾을 수 없습니다"));
            
            log.info("Checking offline marketplace permission - userUid: [{}], createdBy: [{}]", 
                    userUid, offlineMarketplace.getCreatedBy());
            
            if (!userUid.equals(offlineMarketplace.getCreatedBy())) {
                log.warn("Permission denied - userUid: [{}] != createdBy: [{}]", 
                        userUid, offlineMarketplace.getCreatedBy());
                throw new RuntimeException("오프라인 장터는 생성자만 상품을 등록할 수 있습니다");
            }
            log.info("Offline marketplace creator {} registering product in marketplace {}", userUid, offlineMarketplaceUid);
            return;
        }

        // 메인 장터 권한 체크
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channel.getUid())
                .orElseThrow(() -> new RuntimeException("채널 멤버만 등록할 수 있습니다"));

        ChannelMemberPermissionType requiredPermission = ChannelMemberPermissionType.MARKETPLACE_REGISTER;

        ChannelMemberPermission permission = permissionRepository
                .findByChannelMemberIdxAndPermissionType(member.getIdx(), requiredPermission)
                .orElse(null);

        if (permission == null || !permission.isHasPermission()) {
            throw new RuntimeException("상품을 등록할 권한이 없습니다");
        }
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
     * 내 등록 상품 목록 조회 (특정 채널, 온라인/오프라인 필터)
     */
    @Transactional(readOnly = true)
    public Page<MarketplaceProductDto> getMyRegisteredProducts(
            String channelDomain,
            String marketplaceType,  // "online", "offline", null(전체)
            String userUid,
            Pageable pageable) {
        
        // domain → channelUid 변환
        String channelUid = getChannelUidByDomain(channelDomain);
        
        Page<MarketplaceProduct> products;
        
        if ("online".equalsIgnoreCase(marketplaceType)) {
            // 온라인 상품만
            products = productRepository
                    .findBySellerUidAndChannelUidAndOfflineMarketplaceUidIsNullOrderByCreatedAtDesc(
                            userUid, channelUid, pageable);
        } else if ("offline".equalsIgnoreCase(marketplaceType)) {
            // 오프라인 상품만
            products = productRepository
                    .findBySellerUidAndChannelUidAndOfflineMarketplaceUidIsNotNullOrderByCreatedAtDesc(
                            userUid, channelUid, pageable);
        } else {
            // 전체 (온라인 + 오프라인)
            products = productRepository
                    .findBySellerUidAndChannelUidOrderByCreatedAtDesc(userUid, channelUid, pageable);
        }
        
        return products.map(product -> toDto(product, userUid));
    }

    /**
     * Entity to DTO
     */
    private MarketplaceProductDto toDto(MarketplaceProduct entity, String currentUserUid) {
        List<MarketplaceProductImage> images = imageRepository
                .findByProductUidOrderByDisplayOrderAsc(entity.getUid());

        List<String> imageUids = images.stream()
                .map(MarketplaceProductImage::getFileUid)
                .collect(Collectors.toList());

        String thumbnailUid = images.stream()
                .filter(MarketplaceProductImage::isThumbnail)
                .map(MarketplaceProductImage::getFileUid)
                .findFirst()
                .orElse(null);

        String offlineMarketplaceName = null;
        if (entity.getOfflineMarketplaceUid() != null) {
            offlineMarketplaceName = offlineMarketplaceRepository
                    .findByUid(entity.getOfflineMarketplaceUid())
                    .map(OfflineMarketplace::getName)
                    .orElse(null);
        }

        // 거래중 여부 및 현재 구매자 정보 조회
        boolean isTrading = "TRADING".equals(entity.getStatus());
        String currentBuyerUid = null;
        String currentBuyerName = null;
        String currentBuyerIconFileUid = null;
        String currentPurchaseUid = null;

        if (isTrading && entity.getSellerUid().equals(currentUserUid)) {
            // 판매자가 조회하는 경우, 현재 거래중인 구매자 정보 조회
            MarketplacePurchase inProgressPurchase = purchaseRepository
                    .findByProductUidAndStatus(entity.getUid(), "IN_PROGRESS")
                    .orElse(null);
            
            if (inProgressPurchase != null) {
                currentBuyerUid = inProgressPurchase.getBuyerUid();
                currentPurchaseUid = inProgressPurchase.getUid();
                
                // 구매자 정보 조회
                User buyer = userRepository.findById(currentBuyerUid).orElse(null);
                if (buyer != null) {
                    currentBuyerName = buyer.getActualName();
                    currentBuyerIconFileUid = buyer.getIconFileUid();
                }
            }
        }

        return MarketplaceProductDto.builder()
                .uid(entity.getUid())
                .channelUid(entity.getChannelUid())
                .offlineMarketplaceUid(entity.getOfflineMarketplaceUid())
                .offlineMarketplaceName(offlineMarketplaceName)
                .category(entity.getCategory())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .stockQuantity(entity.getStockQuantity())
                .location(entity.getLocation())
                .sellerUid(entity.getSellerUid())
                .sellerName(entity.getSellerName())
                .status(entity.getStatus())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .imageUids(imageUids)
                .thumbnailUid(thumbnailUid)
                .isOffline(entity.getOfflineMarketplaceUid() != null)
                .isMine(entity.getSellerUid().equals(currentUserUid))
                .isTrading(isTrading)
                .currentBuyerUid(currentBuyerUid)
                .currentBuyerName(currentBuyerName)
                .currentBuyerIconFileUid(currentBuyerIconFileUid)
                .currentPurchaseUid(currentPurchaseUid)
                .build();
    }
}
