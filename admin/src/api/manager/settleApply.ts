import request from '@/utils/request';

const PATH = '/manager/settle/apply';

export const getSettleApplyList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const applySettle = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});
