import request from '@/utils/request';

const PATH = '/admin/product/order';

export const getProductOrderList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getProductOrderListByDay = (listQuery: any) => request({
  url: `${PATH}/day`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getProductOrderDetailByDay = (idx: number) => request({
  url: `${PATH}/day/${idx}/detail`,
  method: 'get',
});

export const getProductOrderListByProduct = (listQuery: any) => request({
  url: `${PATH}/product`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getProductOrderListByShop = (listQuery: any) => request({
  url: `${PATH}/shop`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getExcel = (listQuery: any) => request({
  url: `${PATH}/excel`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
  responseType: 'blob',
});

export const getProductOrderDetail = (idx: number) => request({
  url: `${PATH}/${idx}/detail`,
  method: 'get',
});

export const getProductOrderDetailByProduct = (idx: number, params: any) => request({
  url: `${PATH}/product/${idx}/detail`,
  method: 'get',
  params,
});

export const getProductOrderDetailByShop = (idx: number, params: any) => request({
  url: `${PATH}/shop/${idx}/detail`,
  method: 'get',
  params,
});

export const getProductOrderDetailByStation = (idx: number, params: any) => request({
  url: `${PATH}/station/${idx}/detail`,
  method: 'get',
  params,
});

export const getProductOrderTotalStatistics = (params: any) => request({
  url: `${PATH}/total/statistics`,
  method: 'get',
  params,
});

export const updateProductOrderStatus = (idx: number, data: any) => request({
  url: `${PATH}/${idx}`,
  method: 'put',
  data,
});

export const updateProductOrderStatusByDay = (idx: number, data: any) => request({
  url: `${PATH}/day/${idx}`,
  method: 'put',
  data,
});

export const updateProductReceiveStatus = (params: any, data: any) => request({
  url: `${PATH}/receive/status`,
  method: 'put',
  params,
  data,
});
