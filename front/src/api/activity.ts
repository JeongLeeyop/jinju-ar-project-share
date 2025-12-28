import request from '@/utils/request';

const ACTIVITY_PATH = '/activity-log';

/**
 * Activity Type Enum
 */
export enum ActivityType {
  SPACE_INVITED = 'SPACE_INVITED',
  SPACE_CREATED = 'SPACE_CREATED',
  SPACE_JOINED = 'SPACE_JOINED',
  SPACE_MEMBERS_INVITED = 'SPACE_MEMBERS_INVITED',
  POST_CREATED = 'POST_CREATED',
  POST_UPDATED = 'POST_UPDATED',
  POST_DELETED = 'POST_DELETED',
  COMMENT_CREATED = 'COMMENT_CREATED',
  COMMENT_UPDATED = 'COMMENT_UPDATED',
  COMMENT_DELETED = 'COMMENT_DELETED',
  LIKE_ADDED = 'LIKE_ADDED',
  LIKE_REMOVED = 'LIKE_REMOVED',
  COMMUNITY_JOINED = 'COMMUNITY_JOINED',
  COMMUNITY_LEFT = 'COMMUNITY_LEFT',
  EVENT_CREATED = 'EVENT_CREATED',
  EVENT_JOINED = 'EVENT_JOINED',
  EVENT_CANCELLED = 'EVENT_CANCELLED',
  PRODUCT_CREATED = 'PRODUCT_CREATED',
  PRODUCT_PURCHASED = 'PRODUCT_PURCHASED',
  TRADE_STARTED = 'TRADE_STARTED',
  TRADE_COMPLETED = 'TRADE_COMPLETED',
  RPOINT_EARNED = 'RPOINT_EARNED',
  LESSON_ENROLLED = 'LESSON_ENROLLED',
  ATTENDANCE_CHECKED = 'ATTENDANCE_CHECKED',
  VIDEO_WATCHED = 'VIDEO_WATCHED',
}

/**
 * Activity 인터페이스
 */
export interface Activity {
  idx: number;
  userUid: string;
  userName: string;
  channelUid: string;
  channelName?: string;
  activityType: ActivityType;
  activityTypeName: string;
  description: string;
  relatedUid?: string;
  relatedName?: string;
  targetUserUid?: string;
  targetUserName?: string;
  metaData?: string;
  createdAt: string;
  createdAtFormatted: string;
}

/**
 * Activity List Request 인터페이스
 */
export interface ActivityListRequest {
  userUid?: string;
  channelUid?: string;
  months?: number; // 1, 3, 6 개월
  activityType?: ActivityType;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
}

/**
 * 날짜별 그룹화된 활동 인터페이스
 */
export interface GroupedActivity {
  date: string; // yyyy.MM.dd 형식
  activities: Activity[];
}

/**
 * 활동 통계 인터페이스
 */
export interface ActivityStatistics {
  totalActivities: number;
  postsCreated: number;
  commentsCreated: number;
  likesAdded: number;
  eventsJoined: number;
  videosWatched: number;
  activityTypeCount: { [key: string]: number };
}

/**
 * 사용자 활동 로그 조회 (페이징)
 * GET /api/activity-log/user/{userUid}?channelUid=xxx&months=1&page=0&size=20
 */
export const getUserActivities = (userUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/user/${userUid}`,
    method: 'get',
    params,
  });

/**
 * 사용자 활동 로그 조회 (날짜별 그룹화)
 * GET /api/activity-log/user/{userUid}/grouped?channelUid=xxx&months=1
 */
export const getUserActivitiesGrouped = (userUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/user/${userUid}/grouped`,
    method: 'get',
    params,
  });

/**
 * 채널 활동 로그 조회
 * GET /api/activity-log/channel/{channelUid}?page=0&size=20
 */
export const getChannelActivities = (channelUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/channel/${channelUid}`,
    method: 'get',
    params,
  });

/**
 * 사용자 활동 통계 조회
 * GET /api/activity-log/user/{userUid}/statistics?channelUid=xxx&months=1
 */
export const getUserActivityStatistics = (userUid: string, params?: ActivityListRequest) =>
  request({
    url: `${ACTIVITY_PATH}/user/${userUid}/statistics`,
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
