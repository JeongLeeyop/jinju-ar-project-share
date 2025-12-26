import request from '@/utils/request';

const PATH = '/client/push-alarm';

export const getAlarmList = (params: any) => request({
  url: PATH,
  method: 'get',
  params,
});

export const getAlarmCount = () => request({
  url: `${PATH}/count`,
  method: 'get',
});

export const readAll = () => request({
  url: `${PATH}/readAll`,
  method: 'put',
});

export const deleteAlarm = (id: number) => request({
  url: `${PATH}/${id}`,
  method: 'delete',
});
