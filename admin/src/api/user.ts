import qs from 'qs';
import axios from 'axios';
import request from '@/utils/request';

const PATH = '/user';

export const login = (data: any) => {
  data.grant_type = 'password';
  return axios({
    method: 'post',
    url: '/oauth/token',
    data: qs.stringify(data),
    headers: {
      Authorization: 'Basic c2luZ2hhX29hdXRoOnNpbmdoYXNjcmVjdCFAIyQ=',
    },
  });
};

export const tokenCheck = (jwt: any) => {
  const token = {
    token: jwt,
  };
  return axios({
    method: 'post',
    url: '/oauth/check_token',
    data: qs.stringify(token),
  });
};

export const getUserList = (listQuery: any) => request({
  url: PATH,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getManagerList = (listQuery: any) => request({
  url: `${PATH}/manager/list`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const getUser = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const userIdCheck = (userId: string) => request({
  url: `${PATH}/id-check/${userId}`,
  method: 'get',
});

export const getInstitutionUserList = (institutionUid: string) => request({
  url: `${PATH}/institution/${institutionUid}`,
  method: 'get',
});

export const addUser = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const addManager = (data: any) => request({
  url: `${PATH}/manager`,
  method: 'post',
  data,
});

export const updateUser = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data,
});

export const updateManager = (uid: string, data: any) => request({
  url: `${PATH}/manager/${uid}`,
  method: 'put',
  data,
});

export const deleteUser = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const getUserListByRole = (roleCode: string) => request({
  url: `${PATH}/role/${roleCode}/list`,
  method: 'get',
});

export const getTeacherList = (institutionUid: string) => request({
  url: `${PATH}/institution/${institutionUid}/teacher`,
  method: 'get',
});
