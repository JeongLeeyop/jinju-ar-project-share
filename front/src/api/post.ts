import request from '@/utils/request';
import { IPost } from '@/types/post';

const PATH = '/client/post';

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

export const addPostLike = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const deletePostLike = (idx: any) => request({
  url: `${PATH}/like/${idx}`,
  method: 'delete',
});

// 파일 업로드 (AttachedFile)
export const uploadFile = (formData: FormData) => request({
  url: '/post/upload',
  method: 'post',
  data: formData,
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});
