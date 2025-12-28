import request from '@/utils/request';

const PATH = '/client/channel-member';

export const getChannelMemberList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
    // 기본적으로 추방된 회원은 제외 (관리자 페이지에서만 명시적으로 includeBanned: true 전달)
    excludeBanned: params.excludeBanned !== undefined ? params.excludeBanned : true,
  },
});

export const getMyChannelList = () => request({
  url: `${PATH}/my`,
  method: 'get',
});

export const getChannelDetail = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const getUserCount = (params: any) => request({
  url: `${PATH}/userCount`,
  method: 'get',
  params,
});

export const approval = (uid: any) => request({
  url: `${PATH}/approval/${uid}`,
  method: 'put',
});

export const rejectMember = (uid: any) => request({
  url: `${PATH}/reject/${uid}`,
  method: 'delete',
});

export const removeMember = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const unbanMember = (uid: any) => request({
  url: `${PATH}/unban/${uid}`,
  method: 'put',
});
