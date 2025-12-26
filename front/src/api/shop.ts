import request from '@/utils/request';

const PATH = '/shop';

export const getShopList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getShopListAll = () => request({
  url: `${PATH}/list/all`,
  method: 'get',
});

export const getShopDetail = (id: any) => request({
  url: `${PATH}/${id}`,
  method: 'get',
});

export const addShop = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateShop = (id: any, data: any) => request({
  url: `${PATH}/${id}`,
  method: 'put',
  data,
});

export const withdrawShop = (id: any) => request({
  url: `${PATH}/${id}`,
  method: 'delete',
});
