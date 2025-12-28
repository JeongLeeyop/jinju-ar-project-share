import request from '@/utils/request';

const PATH = '/admin/product';

export const getProductList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getProductDetail = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const addProduct = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateProduct = (uid: any, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteProduct = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
