package com.community.cms.api.marketplace.service;

import com.community.cms.api.channel.repository.ChannelMemberPermissionRepository;
import com.community.cms.api.channel.repository.ChannelMemberRepository;
import com.community.cms.api.channel.repository.ChannelRepository;
import com.community.cms.api.marketplace.dto.MarketplaceProductDto;
import com.community.cms.api.marketplace.dto.MarketplacePurchaseDto;
import com.community.cms.api.marketplace.repository.MarketplaceProductImageRepository;
import com.community.cms.api.marketplace.repository.MarketplaceProductRepository;
import com.community.cms.api.marketplace.repository.MarketplacePurchaseRepository;
import com.community.cms.api.marketplace.repository.OfflineMarketplaceRepository;
import com.community.cms.api.point.repository.PointHistoryRepository;
import com.community.cms.api.user.repository.UserRepository;
import com.community.cms.common.code.ChannelMemberPermissionType;
import com.community.cms.entity.*;
import com.community.cms.entity2.Channel;
import com.community.cms.entity2.ChannelMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 장터 구매 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketplacePurchaseService {

    private final MarketplacePurchaseRepository purchaseRepository;
    private final MarketplaceProductRepository productRepository;
    private final MarketplaceProductImageRepository imageRepository;
    private final OfflineMarketplaceRepository offlineMarketplaceRepository;
    private final ChannelRepository channelRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelMemberPermissionRepository permissionRepository;

    /**
     * 상품 구매 (메인 장터 - 포인트 결제)
     */
    @Transactional
    public MarketplacePurchaseDto purchaseProduct(
            String productUid,
            MarketplaceProductDto.PurchaseRequest request,
            String buyerUid,
            String buyerName) {

        MarketplaceProduct product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 자기 자신의 상품 구매 방지
        if (product.getSellerUid().equals(buyerUid)) {
            throw new RuntimeException("자신의 상품은 구매할 수 없습니다");
        }

        // 오프라인 장터 상품은 이 메서드로 구매 불가
        if (product.getOfflineMarketplaceUid() != null) {
            throw new RuntimeException("오프라인 장터 상품은 장터 관리자에게 문의 후 결제 가능합니다");
        }

        // 장터 이용 권한 체크
        checkMarketplaceUsePermission(product.getChannelUid(), buyerUid);

        // 재고 확인
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("재고가 부족합니다");
        }

        // 총 가격 계산
        int totalPrice = product.getPrice() * request.getQuantity();

        // 포인트 차감
        deductPoints(buyerUid, product.getChannelUid(), totalPrice, productUid);

        // 재고 감소
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        if (product.getStockQuantity() == 0) {
            product.setStatus("SOLD_OUT");
        }
        productRepository.save(product);

        // 구매 내역 생성
        MarketplacePurchase purchase = MarketplacePurchase.builder()
                .uid(UUID.randomUUID().toString())
                .productUid(productUid)
                .buyerUid(buyerUid)
                .buyerName(buyerName)
                .buyerContact(request.getBuyerContact())
                .sellerUid(product.getSellerUid())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status("COMPLETED")
                .paymentMethod("POINT")
                .isOffline(false)
                .completedAt(LocalDateTime.now())
                .build();

        MarketplacePurchase saved = purchaseRepository.save(purchase);

        log.info("Product purchased: {} by {} ({}P)", productUid, buyerUid, totalPrice);

        return toDto(saved, product);
    }

    /**
     * 오프라인 장터 구매 처리 (판매자가 직접 처리)
     */
    @Transactional
    public MarketplacePurchaseDto processOfflinePurchase(
            String productUid,
            String purchaseUid,
            MarketplacePurchaseDto.OfflineProcessRequest request,
            String sellerUid) {

        MarketplaceProduct product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 판매자 확인
        if (!product.getSellerUid().equals(sellerUid)) {
            throw new RuntimeException("상품 판매자만 구매 처리를 할 수 있습니다");
        }

        // 구매 내역 조회
        MarketplacePurchase purchase = purchaseRepository.findByUid(purchaseUid)
                .orElseThrow(() -> new RuntimeException("구매 내역을 찾을 수 없습니다"));

        // 이미 처리된 구매인지 확인
        if (!"PENDING".equals(purchase.getStatus())) {
            throw new RuntimeException("이미 처리된 구매입니다");
        }

        // 구매자 포인트 차감
        deductPoints(purchase.getBuyerUid(), product.getChannelUid(), 
                     request.getPointAmount(), productUid);

        // 구매 내역 업데이트
        purchase.setBuyerContact(request.getBuyerContact());
        purchase.setTotalPrice(request.getPointAmount());
        purchase.setStatus("COMPLETED");
        purchase.setCompletedAt(LocalDateTime.now());

        MarketplacePurchase saved = purchaseRepository.save(purchase);

        log.info("Offline purchase processed: {} by seller {} ({}P)", 
                 purchaseUid, sellerUid, request.getPointAmount());

        return toDto(saved, product);
    }

    /**
     * 오프라인 장터 즉시 구매 (구매자가 직접 구매)
     */
    @Transactional
    public MarketplacePurchaseDto instantOfflinePurchase(
            String productUid,
            MarketplacePurchaseDto.InstantPurchaseRequest request,
            String buyerUid,
            String buyerName) {

        MarketplaceProduct product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 자기 자신의 상품 구매 방지
        if (product.getSellerUid().equals(buyerUid)) {
            throw new RuntimeException("자신의 상품은 구매할 수 없습니다");
        }

        // 오프라인 장터 상품이 아니면 에러
        if (product.getOfflineMarketplaceUid() == null) {
            throw new RuntimeException("오프라인 장터 상품이 아닙니다");
        }

        // 장터 이용 권한 체크
        checkMarketplaceUsePermission(product.getChannelUid(), buyerUid);

        // 재고 확인
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("재고가 부족합니다");
        }

        // 총 가격 계산 (나눔 상품은 0원)
        int totalPrice = "SHARE".equals(product.getCategory()) ? 0 : product.getPrice() * request.getQuantity();

        // 포인트 차감 (나눔 상품이 아닐 경우만)
        if (totalPrice > 0) {
            deductPoints(buyerUid, product.getChannelUid(), totalPrice, productUid);
        }

        // 재고 감소
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
        if (product.getStockQuantity() == 0) {
            product.setStatus("SOLD_OUT");
        }
        productRepository.save(product);

        // 구매 내역 생성
        MarketplacePurchase purchase = MarketplacePurchase.builder()
                .uid(UUID.randomUUID().toString())
                .productUid(productUid)
                .buyerUid(buyerUid)
                .buyerName(buyerName)
                .buyerContact(request.getBuyerContact())
                .sellerUid(product.getSellerUid())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status("COMPLETED")
                .paymentMethod(totalPrice > 0 ? "POINT" : "FREE")
                .isOffline(true)
                .completedAt(LocalDateTime.now())
                .build();

        MarketplacePurchase saved = purchaseRepository.save(purchase);

        log.info("Offline product purchased: {} by {} ({}P)", productUid, buyerUid, totalPrice);

        return toDto(saved, product);
    }

    /**
     * 오프라인 상품 직접 포인트 차감 (판매자가 회원번호로 처리)
     */
    @Transactional
    public void deductPointForOfflineProduct(
            String productUid,
            MarketplacePurchaseDto.OfflineDeductRequest request,
            String sellerUid,
            String sellerName) {

        MarketplaceProduct product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 판매자 확인
        if (!product.getSellerUid().equals(sellerUid)) {
            throw new RuntimeException("상품 판매자만 포인트 차감을 할 수 있습니다");
        }

        // 오프라인 장터 상품인지 확인
        if (product.getOfflineMarketplaceUid() == null) {
            throw new RuntimeException("온라인 장터 상품은 직접 구매해야 합니다");
        }

        // 구매자 조회 (이름으로 검색 - 실제로는 회원번호나 UID로 검색하는 것이 더 정확)
        // TODO: 실제 구현시에는 회원번호 필드를 추가하고 정확히 검색
        User buyer = userRepository.findAll().stream()
                .filter(u -> u.getActualName() != null && u.getActualName().equals(request.getBuyerName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 이름의 회원을 찾을 수 없습니다"));


        // 포인트 차감 (나눔 상품이 아닐 경우만)
        if (request.getDeductPoints() > 0) {
            deductPoints(buyer.getUid(), product.getChannelUid(), request.getDeductPoints(), productUid);
        }

        // 구매 내역 생성
        MarketplacePurchase purchase = MarketplacePurchase.builder()
                .uid(UUID.randomUUID().toString())
                .productUid(productUid)
                .buyerUid(buyer.getUid())
                .buyerName(buyer.getActualName())
                .buyerContact(request.getBuyerContact())
                .sellerUid(sellerUid)
                .quantity(1)
                .totalPrice(request.getDeductPoints())
                .status("COMPLETED")
                .paymentMethod(request.getDeductPoints() > 0 ? "POINT" : "FREE")
                .isOffline(true)
                .completedAt(LocalDateTime.now())
                .build();

        purchaseRepository.save(purchase);

        log.info("Offline point deducted: {} for buyer {} by seller {} ({}P)", 
                 productUid, buyer.getUid(), sellerUid, request.getDeductPoints());
    }

    /**
     * 내 구매 내역 조회
     */
    @Transactional(readOnly = true)
    public Page<MarketplacePurchaseDto> getMyPurchases(String buyerUid, Pageable pageable) {
        Page<MarketplacePurchase> purchases = purchaseRepository
                .findByBuyerUidOrderByPurchasedAtDesc(buyerUid, pageable);

        return purchases.map(p -> {
            MarketplaceProduct product = productRepository.findByUid(p.getProductUid())
                    .orElse(null);
            return toDto(p, product);
        });
    }

    /**
     * 내 구매 내역 조회 (특정 채널, 온라인/오프라인 필터)
     */
    @Transactional(readOnly = true)
    public Page<MarketplacePurchaseDto> getMyPurchasedProducts(
            String channelDomain,
            String marketplaceType,  // "online", "offline", null(전체)
            String buyerUid,
            Pageable pageable) {
        
        // domain → channelUid 변환
        Channel channel = channelRepository.findByDomain(channelDomain)
                .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없습니다"));
        String channelUid = channel.getUid();
        
        Page<MarketplacePurchase> purchases;
        
        if ("online".equalsIgnoreCase(marketplaceType)) {
            // 온라인 상품만
            purchases = purchaseRepository
                    .findByBuyerUidAndChannelUidAndOnline(buyerUid, channelUid, pageable);
        } else if ("offline".equalsIgnoreCase(marketplaceType)) {
            // 오프라인 상품만
            purchases = purchaseRepository
                    .findByBuyerUidAndChannelUidAndOffline(buyerUid, channelUid, pageable);
        } else {
            // 전체 (온라인 + 오프라인)
            purchases = purchaseRepository
                    .findByBuyerUidAndChannelUid(buyerUid, channelUid, pageable);
        }
        
        return purchases.map(p -> {
            MarketplaceProduct product = productRepository.findByUid(p.getProductUid())
                    .orElse(null);
            return toDto(p, product);
        });
    }

    /**
     * 상품별 구매 내역 조회 (판매자용)
     */
    @Transactional(readOnly = true)
    public Page<MarketplacePurchaseDto> getProductPurchases(
            String productUid,
            String sellerUid,
            Pageable pageable) {

        MarketplaceProduct product = productRepository.findByUid(productUid)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다"));

        // 판매자 확인
        if (!product.getSellerUid().equals(sellerUid)) {
            throw new RuntimeException("판매자만 구매 내역을 조회할 수 있습니다");
        }

        // TODO: Page로 변환 필요 (현재는 List 반환)
        // 임시로 전체 조회
        return Page.empty(pageable);
    }

    /**
     * 장터 이용 권한 체크
     */
    private void checkMarketplaceUsePermission(String channelUid, String userUid) {
        ChannelMember member = channelMemberRepository.findByUserUidAndChannelUid(userUid, channelUid)
                .orElseThrow(() -> new RuntimeException("채널 멤버가 아닙니다"));

        ChannelMemberPermission permission = permissionRepository
                .findByChannelMemberIdxAndPermissionType(
                        member.getIdx(), 
                        ChannelMemberPermissionType.MARKETPLACE_USE)
                .orElse(null);

        if (permission == null || !permission.isHasPermission()) {
            throw new RuntimeException("장터 이용 권한이 없습니다");
        }
    }

    /**
     * 포인트 차감
     */
    private void deductPoints(String userUid, String channelUid, int amount, String productUid) {
        User user = userRepository.findById(userUid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 현재 포인트 조회
        Integer currentBalance = pointHistoryRepository
                .findCurrentBalance(userUid, channelUid)
                .orElse(0);

        // 포인트 부족 체크
        if (currentBalance < amount) {
            throw new RuntimeException("포인트가 부족합니다 (보유: " + currentBalance + "P, 필요: " + amount + "P)");
        }

        // 새로운 잔액 계산
        int newBalance = currentBalance - amount;

        // 포인트 히스토리 생성
        PointHistory history = PointHistory.builder()
                .userUid(userUid)
                .channelUid(channelUid)
                .pointType("MARKETPLACE")
                .pointAmount(-amount)  // 음수로 차감
                .currentBalance(newBalance)
                .description("장터 상품 구매")
                .referenceId(productUid)
                .build();

        pointHistoryRepository.save(history);

        // 사용자 테이블의 포인트도 업데이트 (선택적)
        if (user.getPoint() != null) {
            user.setPoint(Math.max(0, user.getPoint() - amount));
            userRepository.save(user);
        }

        log.info("Points deducted: {} from user {} ({}P -> {}P)", 
                 amount, userUid, currentBalance, newBalance);
    }

    /**
     * Entity to DTO
     */
    private MarketplacePurchaseDto toDto(MarketplacePurchase entity, MarketplaceProduct product) {
        String thumbnailUid = null;
        String productTitle = "삭제된 상품";
        String productCategory = "";
        String offlineMarketplaceUid = null;
        String offlineMarketplaceName = null;

        if (product != null) {
            productTitle = product.getTitle();
            productCategory = product.getCategory();
            offlineMarketplaceUid = product.getOfflineMarketplaceUid();
            
            thumbnailUid = imageRepository
                    .findByProductUidAndIsThumbnailTrue(product.getUid())
                    .map(MarketplaceProductImage::getFileUid)
                    .orElse(null);
            
            // 오프라인 장터 이름 조회
            if (offlineMarketplaceUid != null) {
                offlineMarketplaceName = offlineMarketplaceRepository
                        .findByUid(offlineMarketplaceUid)
                        .map(OfflineMarketplace::getName)
                        .orElse(null);
            }
        }

        return MarketplacePurchaseDto.builder()
                .uid(entity.getUid())
                .productUid(entity.getProductUid())
                .productTitle(productTitle)
                .productCategory(productCategory)
                .offlineMarketplaceUid(offlineMarketplaceUid)
                .offlineMarketplaceName(offlineMarketplaceName)
                .buyerUid(entity.getBuyerUid())
                .buyerName(entity.getBuyerName())
                .buyerContact(entity.getBuyerContact())
                .sellerUid(entity.getSellerUid())
                .sellerName(product != null ? product.getSellerName() : "")
                .quantity(entity.getQuantity())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .isOffline(entity.isOffline())
                .purchasedAt(entity.getPurchasedAt())
                .completedAt(entity.getCompletedAt())
                .cancelledAt(entity.getCancelledAt())
                .thumbnailUid(thumbnailUid)
                .build();
    }
}
