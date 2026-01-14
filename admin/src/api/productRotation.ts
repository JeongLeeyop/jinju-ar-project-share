import request from '@/utils/request';

const PATH = '/admin/product/rotation';

export const getProductList = (weekNum: number) => request({
  url: `${PATH}/${weekNum}`,
  method: 'get',
});

export const saveRotation = (weekNum: number, data: any) => request({
  url: `${PATH}/${weekNum}`,
  method: 'post',
  data,
});
