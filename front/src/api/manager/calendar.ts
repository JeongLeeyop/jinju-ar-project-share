import request from '@/utils/request';

const PATH = '/client/calendar';

export const getCalendar = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'get',
});

export const viewCalendar = (uid: string) => request({
  url: `${PATH}/${uid}/view`,
  method: 'get',
});

export const getCalendarList = (listQuery: any) => request({
  url: `${PATH}/page`,
  method: 'get',
  params: {
    ...listQuery,
    page: listQuery.page - 1,
  },
});

export const addCalendar = (data: any) => request({
  url: PATH,
  method: 'post',
  data: {
    ...data,
  },
});

export const updateCalendar = (uid: string, data: any) => request({
  url: `${PATH}/${uid}`,
  method: 'put',
  data: {
    ...data,
  },
});

export const deleteCalendar = (uid: string) => request({
  url: `${PATH}/${uid}`,
  method: 'delete',
});

export const addCalendarLike = (data: any) => request({
  url: PATH,
  method: 'post',
  data,
});

export const deleteCalendarLike = (idx: any) => request({
  url: `${PATH}/like/${idx}`,
  method: 'delete',
});

// ============ Schedule Participation APIs ============

/**
 * 일정 참여
 */
export const joinCalendarEvent = (idx: number) => request({
  url: `${PATH}/${idx}/join`,
  method: 'post',
});

/**
 * 일정 참여 취소 (24시간 전까지만 가능)
 */
export const cancelCalendarEvent = (idx: number) => request({
  url: `${PATH}/${idx}/cancel`,
  method: 'delete',
});

/**
 * 일정 참여자 목록 조회
 */
export const getCalendarParticipants = (idx: number) => request({
  url: `${PATH}/${idx}/participants`,
  method: 'get',
});

/**
 * 참여자에게 포인트 지급 (earn 타입 일정)
 */
export const grantPointToParticipant = (participantIdx: number) => request({
  url: `${PATH}/participant/${participantIdx}/grant-point`,
  method: 'post',
});

// ============ Like APIs ============

/**
 * 좋아요 토글 (추가/취소)
 */
export const toggleCalendarLike = (idx: number) => request({
  url: `${PATH}/${idx}/like`,
  method: 'post',
});

/**
 * 좋아요 정보 조회
 */
export const getCalendarLikeInfo = (idx: number) => request({
  url: `${PATH}/${idx}/like`,
  method: 'get',
});

// ============ Comment APIs ============

/**
 * 댓글 목록 조회
 */
export const getCalendarComments = (idx: number) => request({
  url: `${PATH}/${idx}/comments`,
  method: 'get',
});

/**
 * 댓글 작성
 */
export const createCalendarComment = (idx: number, data: { content: string; parentIdx?: number }) => request({
  url: `${PATH}/${idx}/comments`,
  method: 'post',
  data,
});

/**
 * 댓글 수정
 */
export const updateCalendarComment = (commentIdx: number, data: { content: string }) => request({
  url: `${PATH}/comments/${commentIdx}`,
  method: 'put',
  data,
});

/**
 * 댓글 삭제
 */
export const deleteCalendarComment = (commentIdx: number) => request({
  url: `${PATH}/comments/${commentIdx}`,
  method: 'delete',
});

// ============ 내 일정 관리 APIs ============

/**
 * 내가 등록한 일정 목록 조회
 */
export const getMyRegisteredSchedules = (channelUid: string) => request({
  url: `${PATH}/my/registered`,
  method: 'get',
  params: { channelUid },
});

/**
 * 내가 참여한 일정 목록 조회
 */
export const getMyParticipatedSchedules = (channelUid: string) => request({
  url: `${PATH}/my/participated`,
  method: 'get',
  params: { channelUid },
});

/**
 * 내가 등록한 일정 취소
 */
export const cancelMyRegisteredSchedule = (idx: number) => request({
  url: `${PATH}/my/${idx}`,
  method: 'delete',
});

// ============ 포인트 내역 조회 ============

/**
 * 일정 관련 포인트 내역 조회
 */
export const getSchedulePointHistory = (channelUid: string) => request({
  url: `/point/history`,
  method: 'get',
  params: { channelUid, pointType: 'SCHEDULE' },
});

// ============ Interfaces ============

export interface MyScheduleItem {
  idx: number;
  title: string;
  startDate: string;
  endDate: string;
  location: string;
  participantCount: number;
  maxParticipants: number;
  status: 'upcoming' | 'ongoing' | 'completed' | 'cancelled';
  eventType: 'free' | 'paid' | 'earn';
  points: number;
  hostUid: string;
  hostName: string;
  participatedAt?: string;
  cancelDeadline?: string;
  canCancel: boolean;
}

