import request from '@/utils/request';
import { IPost } from '@/types/post';

const PATH = '/tfse';

export const getPost = (idx: number) => request({
  url: `${PATH}/${idx}`,
  method: 'get',
});

export const viewPost = (idx: number) => request({
  url: `${PATH}/${idx}/view`,
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

export const updatePost = (idx: number, data: IPost) => request({
  url: `${PATH}/${idx}`,
  method: 'put',
  data: {
    ...data,
  },
});

export const deletePost = (idx: number) => request({
  url: `${PATH}/${idx}`,
  method: 'delete',
});

export const getFeedback = (idx: number) => request({
  url: `${PATH}/selfFeedback/${idx}`,
  method: 'get',
});

export const getFeedbackList = (listQuery: any) => request({
  url: `${PATH}/selfFeedback/list`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const addFeedback = (data: any) => request({
  url: `${PATH}/selfFeedback`,
  method: 'post',
  data,
});

export const deleteFeedback = (idx: number) => request({
  url: `${PATH}/selfFeedback/${idx}`,
  method: 'delete',
});
