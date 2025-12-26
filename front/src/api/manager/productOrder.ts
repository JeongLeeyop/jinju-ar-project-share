import request from '@/utils/request';

const PATH = '/manager/product/order';

export const getProductOrderList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getProductOrderTotalStatistics = (params: any) => request({
  url: `${PATH}/total/statistics`,
  method: 'get',
  params,
});
