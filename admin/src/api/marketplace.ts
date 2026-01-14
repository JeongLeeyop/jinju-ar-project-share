import request from '@/utils/request';

const PATH = '/api/admin/marketplace';

// 장터 상품 목록 조회
export const getMarketplaceList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 장터 상품 상세 조회
export const getMarketplaceProduct = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

// 장터 매출 현황 조회
export const getMarketplaceSales = (params: any) => request({
  url: `${PATH}/sales`,
  method: 'get',
  params,
});

// 장터 통계 조회
export const getMarketplaceStats = () => request({
  url: `${PATH}/stats`,
  method: 'get',
});

// 커뮤니티별 장터 조회
export const getMarketplaceByChannel = (channelUid: string, params: any) => request({
  url: `${PATH}/channel/${channelUid}`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});
