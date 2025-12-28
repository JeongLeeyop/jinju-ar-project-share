import request from '@/utils/request';

const ACTIVITY_PATH = '/activities';

/**
 * Activity 인터페이스
 */
export interface Activity {
  id: number;
  type: string;
  userUid: string;
  userName: string;
  channelUid: string;
  spaceUid?: string;
  description: string;
  createdAt: string;
}

/**
 * Activity List Request 인터페이스
 */
export interface ActivityListRequest {
  channelUid?: string;
  spaceUid?: string;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

/**
 * 채널별 활동 리스트 조회
 */
export const getActivitiesByChannel = (channelUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/channel/${channelUid}`,
    method: 'get',
    params,
  });

/**
 * 공간별 활동 리스트 조회
 */
export const getActivitiesBySpace = (spaceUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/space/${spaceUid}`,
    method: 'get',
    params,
  });

/**
 * 내 활동 리스트 조회
 */
export const getMyActivities = (params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/my`,
    method: 'get',
    params,
  });
