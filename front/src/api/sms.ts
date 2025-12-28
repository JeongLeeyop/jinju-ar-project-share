import request from '@/utils/request';

const SMS_PATH = '/sms';

// 문자 발송 요청 인터페이스
export interface SmsSendRequest {
  receivers: string[]; // 수신자 전화번호 목록
  message: string; // 메시지 내용
  title?: string; // 메시지 제목 (LMS, MMS)
  msgType?: string; // SMS, LMS, MMS
  reserveDate?: string; // 예약일 (YYYYMMDD)
  reserveTime?: string; // 예약시간 (HHII)
  testMode?: boolean; // 테스트 모드
}

// 문자 발송 응답 인터페이스
export interface SmsSendResponse {
  resultCode: number;
  message: string;
  msgId: number;
  successCnt: number;
  errorCnt: number;
  msgType: string;
  isSuccess: boolean;
}

// 문자 발송 내역 인터페이스
export interface SmsHistory {
  mid: number; // 메시지 ID
  type: string; // 문자 구분 (SMS, LMS, MMS)
  sender: string; // 발신번호
  smsCount: number; // 전송요청수
  reserveState: string; // 요청상태
  msg: string; // 메시지 내용
  failCount: number; // 처리실패건수
  regDate: string; // 등록일
  reserve: string; // 예약일자
  cost: number; // 비용
}

// 발송 가능 건수 인터페이스
export interface SmsRemain {
  result_code: number;
  message: string;
  SMS_CNT: number; // 단문 발송가능 건수
  LMS_CNT: number; // 장문 발송가능 건수
  MMS_CNT: number; // 그림문자 발송가능 건수
}

// SMS 템플릿 인터페이스
export interface SmsTemplate {
  id: number;
  name: string;
  content: string;
  channelUid: string | null;
  isActive: boolean;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * 문자 발송
 */
export const sendSms = (data: SmsSendRequest) => request({
  url: `${SMS_PATH}/send`,
  method: 'post',
  data,
});

/**
 * 전송 내역 조회
 */
export const getSmsHistory = (params?: {
  page?: number;
  pageSize?: number;
  startDate?: string;
  limitDay?: number;
}) => request({
  url: `${SMS_PATH}/history`,
  method: 'get',
  params,
});

/**
 * 발송 가능 건수 조회
 */
export const getRemainCount = () => request({
  url: `${SMS_PATH}/remain`,
  method: 'get',
});

/**
 * SMS 템플릿 목록 조회
 */
export const getSmsTemplates = (channelUid?: string) => request({
  url: '/sms/templates',
  method: 'get',
  params: channelUid ? { channelUid } : {},
});
