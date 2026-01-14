import request from '@/utils/request';

const PATH = '/mission';
const PATH2 = '/mission/template';

export const getMissionTemplateList = () => request({
  url: PATH2,
  method: 'get',
});

export const getMissionTemplateDetail = (uid: any) => request({
  url: `${PATH2}/${uid}`,
  method: 'get',
});

export const addMissionTemplate = (data: any) => request({
  url: PATH2,
  method: 'post',
  data,
});

export const updateMissionTemplate = (uid: string, data: any) => request({
  url: `${PATH2}/${uid}`,
  method: 'put',
  data,
});

export const deleteMissionTemplate = (uid: any) => request({
  url: `${PATH2}/${uid}`,
  method: 'delete',
});

export const getMissionList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getMissionDetail = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const addMission = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateMission = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteMission = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const getMissionCategory = () => request({
  url: `${PATH}/category/list`,
  method: 'get',
});
