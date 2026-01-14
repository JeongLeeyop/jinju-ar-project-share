import request from '@/utils/request';

const PATH = '/api/channel';

// 채널(커뮤니티) 목록 조회
export const getChannelList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page ? params.page - 1 : 0,
  },
});

// 채널 상세 조회
export const getChannelDetail = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

// 채널 삭제
export const deleteChannel = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

// 채널 멤버 목록 조회
export const getChannelMembers = (channelUid: string, params?: any) => request({
  url: `/client/channel/${channelUid}/member`,
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});

// 전체 채널 목록 (관리자용 - 모든 채널)
export const getAllChannels = (params: any) => request({
  url: '/client/channel',
  method: 'get',
  params: {
    ...params,
    page: params.page ? params.page - 1 : 0,
  },
});
