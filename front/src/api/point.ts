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
 * 채널 회원 포인트 정보 인터페이스
 */
export interface MemberWithPoint {
  memberIdx: number;
  memberUid: string;
  userUid: string;
  userId: string;
  actualName: string;
  email: string;
  iconFileUid: string | null;
  currentPoint: number;
}

/**
 * 관리자 포인트 지급/차감 요청 인터페이스
 */
export interface AdminPointAdjustRequest {
  channelUid: string;
  targetUserUid: string;
  pointAmount: number;
  description: string;
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

/**
 * 채널 회원 목록 및 포인트 조회 (관리자용)
 */
export const getChannelMembersWithPoints = (params: {
  channelUid: string;
  page?: number;
  size?: number;
}) => request({
  url: `/points/admin/members`,
  method: 'get',
  params,
});

/**
 * 관리자용 포인트 지급/차감
 */
export const adminAdjustPoint = (data: AdminPointAdjustRequest) => request({
  url: `/points/admin/adjust`,
  method: 'post',
  data,
});

/**
 * 특정 회원의 포인트 히스토리 조회 (관리자용)
 */
export const getTargetUserPointHistory = (params: {
  targetUserUid: string;
  channelUid: string;
  page?: number;
  size?: number;
}) => request({
  url: `/points/admin/history/${params.targetUserUid}`,
  method: 'get',
  params: {
    channelUid: params.channelUid,
    page: params.page,
    size: params.size,
  },
});
