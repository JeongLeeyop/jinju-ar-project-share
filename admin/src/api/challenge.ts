import request from '@/utils/request';

const PATH = '/challenge';

export const getChallengeList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

export const getChallengeDetail = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const addChallenge = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateChallenge = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteChallenge = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const getChallengeCategory = () => request({
  url: `${PATH}/category/list`,
  method: 'get',
});
