import request from '@/utils/request';

const PATH = '/admin/settle/setting';

export const getSettleSetting = () => request({
  url: PATH,
  method: 'get',
});

export const saveSettleSetting = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});
