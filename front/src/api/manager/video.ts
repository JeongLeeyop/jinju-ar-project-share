import request from '@/utils/request';
import { IVideo } from '@/types/lession';

const PATH = '/lession/video';

export const getVideo = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const viewVideo = (uid: string) => request({
  url: `${PATH}/${uid}/view`,
  method: 'get',
});

export const getVideoList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const addVideo = (data: IVideo) => request({
  url: PATH,
  method: 'post',
  data: {
    ...data,
  },
});

export const updateVideo = (uid: string, data: IVideo) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data: {
    ...data,
  },
});

export const deleteVideo = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const addVideoLike = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const deleteVideoLike = (idx: any) => request({
  url: `${PATH}/like/${idx}`,
  method: 'delete',
});
