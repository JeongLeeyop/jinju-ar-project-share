import request from '@/utils/request';
import { IPost } from '@/types/post';

const PATH = '/post';

export const getPost = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const viewPost = (uid: string) => request({
  url: `${PATH}/${uid}/view`,
  method: 'get',
});

export const getPostList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const addPost = (data: IPost) => request({
  url: PATH,
  method: 'post',
  data: {
    ...data,
  },
});

export const updatePost = (uid: string, data: IPost) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data: {
    ...data,
  },
});

export const deletePost = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
