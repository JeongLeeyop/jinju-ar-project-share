import request from '@/utils/request';

const PATH = '/api/admin/channel';

// 커뮤니티 목록 조회
export const getChannelList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 커뮤니티 상세 조회
export const getChannel = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

// 커뮤니티 삭제
export const deleteChannel = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

// 커뮤니티 멤버 목록 조회
export const getChannelMembers = (uid: string, params: any) => request({
  url: `${PATH}/${uid}/members`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 커뮤니티 통계 조회
export const getChannelStats = () => request({
  url: `${PATH}/stats`,
  method: 'get',
});
