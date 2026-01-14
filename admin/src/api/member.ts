import request from '@/utils/request';

const PATH = '/admin/member';

// 회원 목록 조회
export const getMemberList = (params: any) => request({
  url: '/user',
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 회원 상세 조회
export const getMember = (uid: string) => request({
  url: `/user/${uid}`,
  method: 'get',
});

// 회원 가입 커뮤니티 목록 조회
export const getMemberChannels = (uid: string) => request({
  url: `${PATH}/${uid}/channels`,
  method: 'get',
});

// 회원 가입 공간 목록 조회
export const getMemberSpaces = (uid: string) => request({
  url: `${PATH}/${uid}/spaces`,
  method: 'get',
});

// 회원 포인트 히스토리 조회
export const getMemberPointHistory = (uid: string, params: any) => request({
  url: `/point/history/${uid}`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 회원 활동 내역 조회
export const getMemberActivityLog = (uid: string, params: any) => request({
  url: `${PATH}/${uid}/activities`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

// 회원 삭제 (탈퇴)
export const deleteMember = (uid: string) => request({
  url: `/user/${uid}`,
  method: 'delete',
});
