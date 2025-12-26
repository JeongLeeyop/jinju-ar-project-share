import request from '@/utils/request';

const PATH = '/station';

export const getStationList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getStationListAll = () => request({
  url: `${PATH}/list/all`,
  method: 'get',
});

export const getStationDetail = (id: any) => request({
  url: `${PATH}/${id}`,
  method: 'get',
});

export const addStation = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateStation = (id: any, data: any) => request({
  url: `${PATH}/${id}`,
  method: 'put',
  data,
});

export const deleteStation = (id: any) => request({
  url: `${PATH}/${id}`,
  method: 'delete',
});
