import request from '@/utils/request';

const PATH = '/admin/settle/apply';

export const getSettleApplyList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getSettleApply = (idx: any) => request({
  url: `${PATH}/${idx}`,
  method: 'get',
});

export const approvalSettle = (data: any) => request({
  url: `${PATH}/approval`,
  method: 'post',
  data,
});
