import request from '@/utils/request';

const PATH = 'admin/coupon';

export const getCouponList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getCouponDetail = (idx: any) => request({
  url: `${PATH}/${idx}`,
  method: 'get',
});

export const addCoupon = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateCoupon = (idx: string, data: any) => request({
  url: `${PATH}/${idx}`,
  method: 'put',
  data,
});

export const deleteCoupon = (idx: any) => request({
  url: `${PATH}/${idx}`,
  method: 'delete',
});
