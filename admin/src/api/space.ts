import request from '@/utils/request';

const PATH = '/space';

// 공간 목록 조회
export const getSpaceList = (channelUid: string, params?: any) => request({
  url: PATH,
  method: 'get',
  params: {
    channelUid,
    ...params,
    page: params?.page ? params.page - 1 : 0,
  },
});

// 공간 상세 조회
export const getSpaceDetail = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

// 공간 삭제
export const deleteSpace = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

// 공간 멤버 목록 조회
export const getSpaceMembers = (spaceUid: string, params?: any) => request({
  url: `${PATH}/${spaceUid}/members`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});

// 전체 공간 목록 (관리자용)
export const getAllSpaces = (params?: any) => request({
  url: `${PATH}/all`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});
