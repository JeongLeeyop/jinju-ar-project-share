import request from '@/utils/request';

const PATH = '/board-category';

export const getBoardCategory = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const listBoardCategory = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const listAllBoardCategory = () => request({
  url: `${PATH}/all/list`,
  method: 'get',
});

export const addBoardCategory = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateBoardCategory = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteBoardCategory = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
