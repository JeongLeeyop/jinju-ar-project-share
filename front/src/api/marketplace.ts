import request from '@/utils/request';

// Base API paths
const MARKETPLACE_PATH = '/marketplace';
const PRODUCTS_PATH = `${MARKETPLACE_PATH}/products`;
const PURCHASES_PATH = `${MARKETPLACE_PATH}/purchases`;
const OFFLINE_PATH = `${MARKETPLACE_PATH}/offline`;

// ==================== 오프라인 장터 API ====================

export interface OfflineMarketplace {
  uid: string;
  channelUid: string;
  name: string;
  description: string;
  isActive: boolean;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
  productCount: number;
}

export interface CreateOfflineMarketplaceRequest {
  name: string;
  description?: string;
}

export interface UpdateOfflineMarketplaceRequest {
  name: string;
  description?: string;
  isActive?: boolean;
}

// 오프라인 장터 생성
export const createOfflineMarketplace = (channelUid: string, data: CreateOfflineMarketplaceRequest) =>
  request({
    url: `${OFFLINE_PATH}/${channelUid}`,
    method: 'post',
    data,
  });

// 채널의 오프라인 장터 목록 조회
export const getOfflineMarketplaces = (channelUid: string, activeOnly = true) =>
  request({
    url: `${OFFLINE_PATH}/${channelUid}`,
    method: 'get',
    params: { activeOnly },
  });

// 채널의 모든 상품 조회 (메인 + 오프라인)
export const getProducts = (channelUid: string, productType?: string, page = 0, size = 100) =>
  request({
    url: `${PRODUCTS_PATH}/channel/${channelUid}`,
    method: 'get',
    params: { productType, page, size },
  });

// 오프라인 장터 상세 조회
export const getOfflineMarketplace = (uid: string) =>
  request({
    url: `${OFFLINE_PATH}/detail/${uid}`,
    method: 'get',
  });

// 오프라인 장터 수정
export const updateOfflineMarketplace = (uid: string, data: UpdateOfflineMarketplaceRequest) =>
  request({
    url: `${OFFLINE_PATH}/${uid}`,
    method: 'put',
    data,
  });

// 오프라인 장터 삭제
export const deleteOfflineMarketplace = (uid: string) =>
  request({
    url: `${OFFLINE_PATH}/${uid}`,
    method: 'delete',
  });

// ==================== 장터 상품 API ====================

export interface MarketplaceProduct {
  uid: string;
  channelUid: string;
  offlineMarketplaceUid: string | null;
  offlineMarketplaceName: string | null;
  productType: 'SALE' | 'SHARE' | 'REQUEST';
  category?: string; // 백엔드에서 category로 내려올 수도 있음
  title: string;
  description: string;
  price: number;
  stock: number;
  stockQuantity?: number; // 백엔드 필드명 대응
  location: string;
  sellerUid: string;
  sellerName: string;
  iconFileUid?: string; // 판매자 프로필 이미지
  status: 'ACTIVE' | 'TRADING' | 'SOLD_OUT' | 'HIDDEN';
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  images?: MarketplaceProductImage[];
  imageUids?: string[];
  thumbnailUid?: string | null;
  isOffline: boolean;
  isMine: boolean;
  isTrading: boolean;
  // 거래중인 구매자 정보
  currentBuyerUid?: string;
  currentBuyerName?: string;
  currentPurchaseUid?: string;
}

export interface MarketplaceProductImage {
  uid: string;
  fileUid: string;
  displayOrder: number;
}

export interface MarketplaceProductCreateRequest {
  channelUid: string;
  offlineMarketplaceUid?: string;
  productType: 'SALE' | 'SHARE' | 'REQUEST';
  title: string;
  description: string;
  price: number;
  stock: number;
  location: string;
  imageUids: string[];
}

export interface CreateProductRequest {
  offlineMarketplaceUid?: string | null;
  category: string;
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUids?: string[];
}

export interface UpdateProductRequest {
  category: string;
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
  status?: string;
  imageUids?: string[];
}

export interface PurchaseRequest {
  quantity: number;
  buyerContact?: string;
}

// 상품 등록
export const createProduct = (data: MarketplaceProductCreateRequest) =>
  request({
    url: `${PRODUCTS_PATH}/${data.channelUid}`,
    method: 'post',
    data,
  });

// 메인 장터 상품 목록
export const getMainMarketplaceProducts = (channelUid: string, category?: string, page = 0, size = 20) =>
  request({
    url: `${PRODUCTS_PATH}/main/${channelUid}`,
    method: 'get',
    params: { category, page, size },
  });

// 오프라인 장터 상품 목록
export const getOfflineMarketplaceProducts = (offlineMarketplaceUid: string, category?: string, page = 0, size = 20) =>
  request({
    url: `${PRODUCTS_PATH}/offline/${offlineMarketplaceUid}`,
    method: 'get',
    params: { category, page, size },
  });

// 내 등록 상품 목록
export const getMyProducts = (page = 0, size = 20) =>
  request({
    url: `${PRODUCTS_PATH}/my`,
    method: 'get',
    params: { page, size },
  });

// 내 등록 상품 목록 (특정 채널, 온라인/오프라인 필터)
export const getMyRegisteredProducts = (channelDomain: string, marketplaceType?: string, page = 0, size = 20) =>
  request({
    url: `${PRODUCTS_PATH}/my/${channelDomain}`,
    method: 'get',
    params: { marketplaceType, page, size },
  });

// 상품 상세 조회
export const getProduct = (uid: string) =>
  request({
    url: `${PRODUCTS_PATH}/${uid}`,
    method: 'get',
  });

// 상품 수정
export const updateProduct = (uid: string, data: UpdateProductRequest) =>
  request({
    url: `${PRODUCTS_PATH}/${uid}`,
    method: 'put',
    data,
  });

// 상품 삭제
export const deleteProduct = (uid: string) =>
  request({
    url: `${PRODUCTS_PATH}/${uid}`,
    method: 'delete',
  });

// ==================== 장터 구매 API ====================

export interface MarketplacePurchase {
  uid: string;
  productUid: string;
  productTitle: string;
  productCategory: string;
  offlineMarketplaceUid: string | null;
  offlineMarketplaceName: string | null;
  productPrice: number;
  productLocation: string | null;
  buyerUid: string;
  buyerName: string;
  buyerContact: string | null;
  buyerIconFileUid: string | null;
  sellerUid: string;
  sellerName: string;
  sellerIconFileUid: string | null;
  quantity: number;
  totalPrice: number;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED';
  paymentMethod: 'POINT' | 'OFFLINE' | 'FREE';
  isOffline: boolean;
  isInProgress: boolean;
  purchasedAt: string;
  completedAt: string | null;
  cancelledAt: string | null;
  thumbnailUid: string | null;
}

export interface OfflineProcessRequest {
  buyerContact: string;
  pointAmount: number;
}

// 오프라인 상품 포인트 차감 요청
export interface OfflineDeductRequest {
  buyerName: string;
  buyerContact: string;
  deductPoints: number;
}

// 오프라인 장터 즉시 구매 요청 (구매자가 직접 구매)
export interface InstantPurchaseRequest {
  quantity: number;
  buyerContact?: string;
}

// 상품 구매 (메인 장터 - 포인트 결제)
export const purchaseProduct = (productUid: string, data: PurchaseRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}`,
    method: 'post',
    data,
  });

// 오프라인 장터 즉시 구매 (구매자가 직접 구매)
export const instantOfflinePurchase = (productUid: string, data: InstantPurchaseRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}/offline/instant`,
    method: 'post',
    data,
  });

// 오프라인 장터 구매 처리 (판매자가 직접 처리)
export const processOfflinePurchase = (productUid: string, purchaseUid: string, data: OfflineProcessRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}/offline/${purchaseUid}`,
    method: 'post',
    data,
  });

// 오프라인 상품 직접 포인트 차감 (판매자가 회원번호로 처리)
export const deductPointForOfflineProduct = (productUid: string, data: OfflineDeductRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}/deduct-point`,
    method: 'post',
    data,
  });

// 내 구매 내역 조회
export const getMyPurchases = (page = 0, size = 20) =>
  request({
    url: `${PURCHASES_PATH}/my`,
    method: 'get',
    params: { page, size },
  });

// 내 구매 내역 조회 (특정 채널, 온라인/오프라인 필터)
export const getMyPurchasedProducts = (channelDomain: string, marketplaceType?: string, page = 0, size = 20) =>
  request({
    url: `${PURCHASES_PATH}/my/${channelDomain}`,
    method: 'get',
    params: { marketplaceType, page, size },
  });

// ==================== 거래 시작/완료/취소 API ====================

export interface StartTradeRequest {
  quantity: number;
  buyerContact?: string;
}

export interface ApplyForRequestRequest {
  message?: string;
  contact?: string;
}

// 거래 시작 (구매자 - 포인트는 확정 시 차감)
export const startTrade = (productUid: string, data: StartTradeRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}/start-trade`,
    method: 'post',
    data,
  });

// 거래 완료 (구매자가 포인트 지급 확정)
export const completeTrade = (purchaseUid: string) =>
  request({
    url: `${PURCHASES_PATH}/${purchaseUid}/complete`,
    method: 'post',
  });

// 거래 취소
export const cancelTrade = (purchaseUid: string) =>
  request({
    url: `${PURCHASES_PATH}/${purchaseUid}/cancel`,
    method: 'post',
  });

// REQUEST 상품 지원
export const applyForRequest = (productUid: string, data: ApplyForRequestRequest) =>
  request({
    url: `${PURCHASES_PATH}/${productUid}/apply`,
    method: 'post',
    data,
  });
