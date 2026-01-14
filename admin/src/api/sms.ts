import request from '@/utils/request';

const SMS_PATH = '/sms';

// 문자 발송 요청 인터페이스
export interface SmsSendRequest {
  receivers: string[]
  message: string
  title?: string
  msgType?: string
  reserveDate?: string
  reserveTime?: string
  testMode?: boolean
}

// 문자 발송
export const sendSms = (data: SmsSendRequest) => request({
  url: `${SMS_PATH}/send`,
  method: 'post',
  data,
});

// 전송 내역 조회
export const getSmsHistory = (params?: {
  page?: number
  pageSize?: number
  startDate?: string
  limitDay?: number
}) => request({
  url: `${SMS_PATH}/history`,
  method: 'get',
  params,
});

// 발송 가능 건수 조회
export const getRemainCount = () => request({
  url: `${SMS_PATH}/remain`,
  method: 'get',
});

// 회원 목록 조회 (문자 발송용)
export const getUsersForSms = (params?: any) => request({
  url: '/user',
  method: 'get',
  params: params ? {
    ...params,
    page: params.page ? params.page - 1 : 0,
  } : {},
});
