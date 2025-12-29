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

/**
 * 포인트 설정 인터페이스
 */
export interface PointSettingDto {
  id?: number;
  channelUid?: string;
  // 포인트 적립량
  postCreate: number;
  commentCreate: number;
  likeGive: number;
  dailyAttendance: number;
  marketplaceCreate: number;
  marketplaceSell: number;
  courseComplete: number;
  // 일일 적립 횟수 제한 (0 = 무제한)
  postDailyLimit: number;
  commentDailyLimit: number;
  likeDailyLimit: number;
  marketplaceCreateDailyLimit: number;
  marketplaceSellDailyLimit: number;
  courseCompleteDailyLimit: number;
  // 최소 글자수 제한
  postMinLength: number;
  commentMinLength: number;
}

/**
 * 포인트 설정 조회 (관리자용)
 */
export const getPointSettings = (channelUid: string) => request({
  url: `/points/settings`,
  method: 'get',
  params: { channelUid },
});

/**
 * 포인트 설정 저장 (관리자용)
 */
export const savePointSettings = (channelUid: string, data: PointSettingDto) => request({
  url: `/points/settings`,
  method: 'put',
  params: { channelUid },
  data,
});

/**
 * 포인트 설정 초기화 (관리자용)
 */
export const resetPointSettings = (channelUid: string) => request({
  url: `/points/settings/reset`,
  method: 'post',
  params: { channelUid },
});
