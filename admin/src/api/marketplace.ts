import request from '@/utils/request';

const PATH = '/marketplace';

// 장터 상품 목록 조회
export const getMarketplaceProducts = (params?: any) => request({
  url: `${PATH}/product`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});

// 장터 상품 상세 조회
export const getMarketplaceProductDetail = (uid: string) => request({
  url: `${PATH}/product/${uid}`,
  method: 'get',
});

// 장터 구매 내역 조회
export const getMarketplacePurchases = (params?: any) => request({
  url: `${PATH}/purchase`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});

// 장터 매출 통계
export const getMarketplaceSalesStats = (channelUid?: string) => request({
  url: `${PATH}/sales/stats`,
  method: 'get',
  params: channelUid ? { channelUid } : {},
});

// 전체 장터 목록 (관리자용)
export const getAllMarketplaces = (params?: any) => request({
  url: `${PATH}/all`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});
