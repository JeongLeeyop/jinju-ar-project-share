import request from '@/utils/request';

const PATH = '/admin/sms';

// SMS 발송
export const sendSms = (data: any) => request({
  url: `${PATH}/send`,
  method: 'post',
  data,
});

// SMS 발송 내역 조회
export const getSmsHistory = (params: any) => request({
  url: `${PATH}/history`,
  method: 'get',
  params,
});

// 잔여 발송 건수 조회
export const getSmsRemainCount = () => request({
  url: `${PATH}/remain`,
  method: 'get',
});

// 회원 목록 조회 (SMS 발송용)
export const getMemberListForSms = (params: any) => request({
  url: `${PATH}/members`,
  method: 'get',
  params: {
    ...params,
    page: params.page - 1,
  },
});

