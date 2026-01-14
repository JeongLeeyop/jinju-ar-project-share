import request from '@/utils/request';

const PATH = '/admin/food';

export const getFoodList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getFood = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const addFood = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateFood = (idx: any, data: any) => request({
  url: `${PATH}/${idx}`,
  method: 'put',
  data,
});

export const deleteFood = (idx: any) => request({
  url: `${PATH}/${idx}`,
  method: 'delete',
});
