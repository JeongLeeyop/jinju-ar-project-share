import request from '@/utils/request';

const PATH = '/board-skin';

export const getBoardSkinList = () => request({
  url: PATH,
  method: 'get',
});
