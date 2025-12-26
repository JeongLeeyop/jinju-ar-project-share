import request from '@/utils/request';
import { ILession } from '@/types/lession';

const PATH = '/client/lession';

export const newLession: ILession = {
  uid: '',
  name: '',
  description: '',
  privateState: false,
  secretState: false,
  createDate: '',
  channelUid: '',
  myWatchPercent: 0,
  video: [],
  fileList: [],
};

export const getLession = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const getCommunityLession = (uid: string) => request({
  url: `${PATH}/${uid}/community`,
  method: 'get',
});

export const getLessionList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getLessionListAll = () => request({
  url: `${PATH}/all/list`,
  method: 'get',
});

export const addLession = (data: ILession) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateLession = (uid: string, data: ILession) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteLession = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
