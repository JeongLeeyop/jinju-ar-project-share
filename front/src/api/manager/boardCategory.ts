import request from '@/utils/request';

const PATH = '/board-category';

export const getBoardCategory = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const getCommunityCategory = (listQuery: any) => request({
  url: `${PATH}/community`,
  method: 'get',
  params: listQuery,
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

export const addCommunitySetting = (data: any) => request({
  url: `${PATH}/community`,
  method: 'post',
  data,
});

export const addCustomBoardCategory = (data: any) => request({
  url: `${PATH}/custom`,
  method: 'post',
  data,
});

export const addCategoryUse = (data: any) => request({
  url: `${PATH}/categoryUse`,
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
