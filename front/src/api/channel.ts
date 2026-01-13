import request from '@/utils/request';

const PATH = '/client/channel';

export const getChannelList = (params: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
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

export const getChannelDomainDetail = (domain: string) => request({
  url: `${PATH}/domain/${domain}`,
  method: 'get',
});

export const addChannel = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const updateChannel = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const deleteChannel = (uid: any) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const getChannelCategory = () => request({
  url: `${PATH}/category/list`,
  method: 'get',
});

export const checkDomain = (domain: string) => request({
  url: `${PATH}/domain/check/${domain}`,
  method: 'get',
});

export const joinchannel = (data: any) => request({
  url: `${PATH}/join`,
  method: 'post',
  data,
});

export const validateCode = (params: any) => request({
  url: `${PATH}/code/${params.uid}`,
  method: 'get',
  params,
});

export const getChannelMemberCount = (domain: string) => request({
  url: `${PATH}/domain/${domain}/member-count`,
  method: 'get',
});

// 사용자가 생성한 커뮤니티 개수 조회
export const getMyChannelCount = () => request({
  url: `${PATH}/my-channel-count`,
  method: 'get',
});

// 커뮤니티 생성 가능 여부 확인 (최대 3개)
export const canCreateChannel = () => request({
  url: `${PATH}/can-create`,
  method: 'get',
});
