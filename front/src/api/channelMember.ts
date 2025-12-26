import request from '@/utils/request';

const PATH = '/client/channel-member';

export const getChannelMemberList = (params: any) => request({
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

export const getUserCount = (params: any) => request({
  url: `${PATH}/userCount`,
  method: 'get',
  params,
});

export const approval = (uid: any) => request({
  url: `${PATH}/approval/${uid}`,
  method: 'put',
});
