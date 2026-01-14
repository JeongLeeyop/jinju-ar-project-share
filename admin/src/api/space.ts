import request from '@/utils/request';

const PATH = '/api/admin/space';

// 공간 목록 조회
export const getSpaceList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 공간 상세 조회
export const getSpace = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

// 커뮤니티별 공간 목록 조회
export const getSpacesByChannel = (channelUid: string, params: any) => request({
  url: `${PATH}/channel/${channelUid}`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 공간 멤버 목록 조회
export const getSpaceMembers = (uid: string, params: any) => request({
  url: `${PATH}/${uid}/members`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 공간 삭제
export const deleteSpace = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});
