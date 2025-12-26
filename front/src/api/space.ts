import request from '@/utils/request';

const SPACE_PATH = '/spaces';
const INVITATION_PATH = '/space-invitations';

// ==================== Space Management ====================

/**
 * 공간 생성
 */
export const createSpace = (data: {
  channelUid: string;
  name: string;
  description?: string;
  spaceType: 'BOARD' | 'CHAT';
  iconUrl?: string;
  invitationRequired?: boolean;
  isPublic?: boolean;
  maxMembers?: number;
}) => request({
  url: SPACE_PATH,
  method: 'post',
  data,
});

/**
 * 공간 조회
 */
export const getSpace = (spaceUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}`,
  method: 'get',
});

/**
 * 채널의 모든 공간 조회 (접근 가능한 공간만)
 */
export const getSpacesByChannel = (channelUid: string) => request({
  url: `${SPACE_PATH}/channel/${channelUid}`,
  method: 'get',
});

/**
 * 내가 속한 공간 목록
 */
export const getMySpaces = () => request({
  url: `${SPACE_PATH}/my`,
  method: 'get',
});

/**
 * 공간 검색
 */
export const searchSpaces = (channelUid: string, keyword: string) => request({
  url: `${SPACE_PATH}/search`,
  method: 'get',
  params: { channelUid, keyword },
});

/**
 * 공간 타입별 조회
 */
export const getSpacesByType = (channelUid: string, spaceType: 'BOARD' | 'CHAT') => request({
  url: `${SPACE_PATH}/channel/${channelUid}/type/${spaceType}`,
  method: 'get',
});

/**
 * 페이징된 공간 조회
 */
export const getSpacesPaged = (channelUid: string, page: number = 0, size: number = 20) => request({
  url: `${SPACE_PATH}/channel/${channelUid}/paged`,
  method: 'get',
  params: { page, size },
});

/**
 * 공간 수정 (관리자만)
 */
export const updateSpace = (spaceUid: string, data: {
  name?: string;
  description?: string;
  iconUrl?: string;
  invitationRequired?: boolean;
  isPublic?: boolean;
  isActive?: boolean;
  maxMembers?: number;
}) => request({
  url: `${SPACE_PATH}/${spaceUid}`,
  method: 'put',
  data,
});

/**
 * 공간 삭제 (비활성화, 관리자만)
 */
export const deleteSpace = (spaceUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}`,
  method: 'delete',
});

/**
 * 공간 영구 삭제 (관리자만)
 */
export const hardDeleteSpace = (spaceUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/hard`,
  method: 'delete',
});

// ==================== Member Management ====================

/**
 * 멤버 추가 (관리자만)
 */
export const addMember = (spaceUid: string, userUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/members/${userUid}`,
  method: 'post',
});

/**
 * 멤버 제거/추방 (관리자만)
 */
export const removeMember = (spaceUid: string, userUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/members/${userUid}`,
  method: 'delete',
});

/**
 * 공간 나가기 (자발적 탈퇴)
 */
export const leaveSpace = (spaceUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/leave`,
  method: 'post',
});

/**
 * 공간 멤버 목록 조회
 */
export const getSpaceMembers = (spaceUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/members`,
  method: 'get',
});

/**
 * 공간 관리자 변경 (현재 관리자만)
 */
export const transferAdmin = (spaceUid: string, newAdminUid: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/admin/${newAdminUid}`,
  method: 'put',
});

// ==================== Invitation Management ====================

/**
 * 초대 생성 (관리자만)
 */
export const createInvitation = (data: {
  spaceUid: string;
  invitedUserUid: string;
  message?: string;
  expiresInDays?: number;
}) => request({
  url: INVITATION_PATH,
  method: 'post',
  data,
});

/**
 * 초대 수락
 */
export const acceptInvitation = (invitationUid: string) => request({
  url: `${INVITATION_PATH}/${invitationUid}/accept`,
  method: 'post',
});

/**
 * 초대 거절
 */
export const rejectInvitation = (invitationUid: string) => request({
  url: `${INVITATION_PATH}/${invitationUid}/reject`,
  method: 'post',
});

/**
 * 초대 취소 (관리자만)
 */
export const cancelInvitation = (invitationUid: string) => request({
  url: `${INVITATION_PATH}/${invitationUid}`,
  method: 'delete',
});

/**
 * 내가 받은 초대 목록
 */
export const getMyInvitations = () => request({
  url: `${INVITATION_PATH}/my`,
  method: 'get',
});

/**
 * 내가 받은 대기중인 초대 목록
 */
export const getMyPendingInvitations = () => request({
  url: `${INVITATION_PATH}/my/pending`,
  method: 'get',
});

/**
 * 공간의 초대 목록 (관리자만)
 */
export const getSpaceInvitations = (spaceUid: string) => request({
  url: `${INVITATION_PATH}/space/${spaceUid}`,
  method: 'get',
});

/**
 * 공간의 대기중인 초대 목록 (관리자만)
 */
export const getSpacePendingInvitations = (spaceUid: string) => request({
  url: `${INVITATION_PATH}/space/${spaceUid}/pending`,
  method: 'get',
});

/**
 * 초대 가능한 사용자 목록 조회
 */
export const getInvitableUsers = (spaceUid: string, search?: string) => request({
  url: `${SPACE_PATH}/${spaceUid}/invitable-users`,
  method: 'get',
  params: { search },
});

/**
 * 다중 사용자 초대
 */
export const inviteMultipleUsers = (spaceUid: string, userUids: string[]) => request({
  url: `${SPACE_PATH}/${spaceUid}/invite-multiple`,
  method: 'post',
  data: { userUids },
});

// ==================== Types ====================

export interface Space {
  uid: string;
  channelUid: string;
  name: string;
  description?: string;
  spaceType: 'BOARD' | 'CHAT';
  spaceTypeDisplay: string;
  adminUid: string;
  iconUrl?: string;
  isActive: boolean;
  maxMembers: number;
  memberCount: number;
  invitationRequired: boolean;
  isPublic: boolean;
  createdAt: string;
  updatedAt?: string;
  isAdmin: boolean;
  isMember: boolean;
}

export interface SpaceInvitation {
  uid: string;
  spaceUid: string;
  spaceName?: string;
  inviterUid: string;
  inviterName?: string;
  invitedUserUid: string;
  invitedUserName?: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED';
  statusDisplay: string;
  message?: string;
  createdAt: string;
  expiresAt: string;
  respondedAt?: string;
  canAccept: boolean;
}
