import request from '@/utils/request';

/**
 * 포인트 히스토리 인터페이스
 */
export interface PointHistory {
  id: number;
  userUid: string;
  channelUid: string;
  pointType: string;
  pointAmount: number;
  currentBalance: number;
  description: string;
  referenceId: string;
  createdAt: string;
}

/**
 * 현재 포인트 조회
 */
export const getCurrentPoint = (channelUid: string) => request({
  url: `/points/current`,
  method: 'get',
  params: { channelUid },
});

/**
 * 포인트 히스토리 조회
 */
export const getPointHistory = (params: {
  channelUid: string;
  page?: number;
  size?: number;
}) => request({
  url: `/points/history`,
  method: 'get',
  params,
});

/**
 * 포인트 적립 (관리자용)
 */
export const addPoint = (data: {
  channelUid: string;
  pointType: string;
  pointAmount: number;
  description: string;
  referenceId?: string;
}) => request({
  url: `/points/add`,
  method: 'post',
  data,
});
