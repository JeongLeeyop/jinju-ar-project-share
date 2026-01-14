import qs from 'qs';
import axios from 'axios';
import request from '@/utils/request';

const PATH = '/client/user';

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

export const logout = () => axios({
    method: 'get',
    url: '/oauth/logout',
  });

export const updateOnline = () => axios({
  method: 'get',
  url: '/oauth/updateOnline',
});

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

export const getUserInfo = () => request({
  url: PATH,
  method: 'get',
});

export const getUserChannelInfo = (uid: string) => request({
  url: `${PATH}/channelInfo/${uid}`,
  method: 'get',
});

export const userIdCheck = (userId: string) => request({
  url: `${PATH}/id-check/${userId}`,
  method: 'get',
});

export const checkNameAndPhoneDuplicate = (data: { actualName: string; concatNumber: string; currentUserUid?: string }) => request({
  url: `${PATH}/check-duplicate`,
  method: 'post',
  data,
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

// 비밀번호 찾기 API
const PASSWORD_PATH = '/password';

/**
 * 이메일(아이디) 찾기
 */
export interface FindEmailRequest {
  actualName: string;
  concatNumber: string;
}

export interface FindEmailResponse {
  found: boolean;
  maskedEmail: string;
  message: string;
}

export const findEmail = (data: FindEmailRequest) => request({
  url: `${PASSWORD_PATH}/find-email`,
  method: 'post',
  data,
});

/**
 * 임시 비밀번호 발급 (SMS 발송)
 */
export interface TempPasswordRequest {
  email: string;
  concatNumber: string;
}

export interface TempPasswordResponse {
  success: boolean;
  message: string;
  maskedPhone: string;
}

export const requestTempPassword = (data: TempPasswordRequest) => request({
  url: `${PASSWORD_PATH}/temp-password`,
  method: 'post',
  data,
});
