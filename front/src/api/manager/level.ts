import request from '@/utils/request';

const PATH = '/channel/level';

export const getLevel = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const getEvent = () => request({
  url: `${PATH}/event`,
  method: 'get',
});

export const getAuthority = () => request({
  url: `${PATH}/authority`,
  method: 'get',
});

export const getLevelList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getLevelListAll = () => request({
  url: `${PATH}/all/list`,
  method: 'get',
});

export const addLevel = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateLevel = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteLevel = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
