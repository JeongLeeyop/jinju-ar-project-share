import request from '@/utils/request';

const PERMISSION_PATH = '/channel-member-permissions';

// 권한 타입 인터페이스
export interface PermissionType {
  code: string;
  description: string;
  category: string;
}

// 권한 상세 정보
export interface Permission {
  id: number;
  channelMemberIdx: number;
  permissionType: string;
  permissionDescription: string;
  hasPermission: boolean;
  grantedBy: string;
  grantedAt: string;
}

// 멤버 권한 목록 응답
export interface MemberPermissionsResponse {
  channelMemberIdx: number;
  permissions: Permission[];
}

// 권한 체크 응답
export interface CheckPermissionResponse {
  channelMemberIdx: number;
  permissionType: string;
  hasPermission: boolean;
}

// 권한 생성/업데이트 요청
export interface CreatePermissionRequest {
  channelMemberIdx: number;
  permissionType: string;
  hasPermission: boolean;
}

// 권한 일괄 업데이트 요청
export interface BulkUpdatePermissionRequest {
  channelMemberIdx: number;
  permissions: {
    permissionType: string;
    hasPermission: boolean;
  }[];
}

// 채널 멤버 정보
export interface ChannelMember {
  idx: number;
  userUid: string;
  userId: string;
  actualName: string;
  nickName: string;
  profileImage: string;
  channelUid: string;
  introduce: string;
  approvalStatus: boolean;
  createdAt: string;
}

/**
 * 모든 권한 타입 조회
 */
export const getAllPermissionTypes = () =>
  request({
    url: `${PERMISSION_PATH}/types`,
    method: 'get',
  });

/**
 * 멤버의 모든 권한 조회
 */
export const getMemberPermissions = (channelMemberIdx: number) =>
  request({
    url: `${PERMISSION_PATH}/member/${channelMemberIdx}`,
    method: 'get',
  });

/**
 * 사용자 UID로 권한 확인
 */
export const checkPermissionByUser = (channelUid: string, permissionType: string) =>
  request({
    url: `${PERMISSION_PATH}/check-by-user`,
    method: 'get',
    params: { channelUid, permissionType },
  });

/**
 * 권한 생성 또는 업데이트
 */
export const createOrUpdatePermission = (data: CreatePermissionRequest) =>
  request({
    url: PERMISSION_PATH,
    method: 'post',
    data,
  });

/**
 * 멤버 권한 일괄 설정
 */
export const bulkUpdatePermissions = (data: BulkUpdatePermissionRequest) =>
  request({
    url: `${PERMISSION_PATH}/bulk`,
    method: 'put',
    data,
  });

/**
 * 권한 삭제
 */
export const deletePermission = (permissionId: number) =>
  request({
    url: `${PERMISSION_PATH}/${permissionId}`,
    method: 'delete',
  });

/**
 * 채널 멤버 목록 조회 (권한 관리용)
 */
export const getChannelMembers = (channelUid: string) =>
  request({
    url: '/client/channel-member',
    method: 'get',
    params: { channelUid },
  });
