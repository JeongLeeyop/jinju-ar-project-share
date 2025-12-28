import request from '@/utils/request';

const PATH = '/client/post-like';

export const likePost = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'post',
});
