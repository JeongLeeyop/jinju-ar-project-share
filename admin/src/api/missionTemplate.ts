import request from '@/utils/request';

const PATH = '/mission/template';

export const getMissionTemplateList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getMissionTemplateDetail = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const addMissionTemplate = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateMissionTemplate = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteMissionTemplate = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const getMissionTemplateCategory = () => request({
  url: `${PATH}/category/list`,
  method: 'get',
});
