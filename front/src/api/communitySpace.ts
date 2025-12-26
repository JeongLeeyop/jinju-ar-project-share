import request from '@/utils/request';

const PATH = '/client/community/space';

// 커뮤니티 공간 목록 조회
export const getSpaceList = (params?: any) => request({
  url: PATH,
  method: 'get',
  params,
});

// 커뮤니티 공간 상세 조회
export const getSpaceDetail = (spaceId: string) => request({
  url: `${PATH}/${spaceId}`,
  method: 'get',
});

// 커뮤니티 공간 참여자 목록 조회
export const getSpaceParticipants = (spaceId: string, params?: any) => request({
  url: `${PATH}/${spaceId}/participants`,
  method: 'get',
  params,
});

// 커뮤니티 공간 참여자 수 조회
export const getSpaceParticipantCount = (spaceId: string) => request({
  url: `${PATH}/${spaceId}/participants/count`,
  method: 'get',
});

// 커뮤니티 공간 생성
export const createSpace = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

// 커뮤니티 공간에 사용자 초대
export const inviteToSpace = (spaceId: string, data: any) => request({
  url: `${PATH}/${spaceId}/invite`,
  method: 'post',
  data,
});

// 커뮤니티 공간 수정
export const updateSpace = (spaceId: string, data: any) => request({
  url: `${PATH}/${spaceId}`,
  method: 'put',
  data,
});

// 커뮤니티 공간 삭제
export const deleteSpace = (spaceId: string) => request({
  url: `${PATH}/${spaceId}`,
  method: 'delete',
});
