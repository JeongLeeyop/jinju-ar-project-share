import request from '@/utils/request';

const PATH = '/channel-member-permissions';

/**
 * 권한 타입 정의
 */
export interface PermissionType {
  code: string;
  name: string;
  description: string;
}

/**
 * 권한 상세 정보
 */
export interface PermissionDetail {
  id: number;
  channelMemberIdx: number;
  permissionType: string;
  permissionTypeName: string;
  hasPermission: boolean;
  createdAt: string;
  updatedAt: string;
  createdByUid: string;
}

/**
 * 멤버 권한 목록 응답
 */
export interface MemberPermissionsResponse {
  channelMemberIdx: number;
  permissions: PermissionDetail[];
}

/**
 * 권한 확인 응답
 */
export interface CheckPermissionResponse {
  hasPermission: boolean;
  permissionType: string;
}

/**
 * 멤버의 모든 권한 조회
 */
export const getMemberPermissions = (channelMemberIdx: number) => request({
  url: `${PATH}/member/${channelMemberIdx}`,
  method: 'get',
});

/**
 * 권한 생성 또는 업데이트
 */
export const createOrUpdatePermission = (data: {
  channelMemberIdx: number;
  permissionType: string;
  hasPermission: boolean;
}) => request({
  url: PATH,
  method: 'post',
  data,
});

/**
 * 멤버 권한 일괄 설정
 */
export const bulkUpdatePermissions = (data: {
  channelMemberIdx: number;
  permissions: { permissionType: string; hasPermission: boolean }[];
}) => request({
  url: `${PATH}/bulk`,
  method: 'put',
  data,
});

/**
 * 권한 확인 (channelMemberIdx로)
 */
export const checkPermission = (channelMemberIdx: number, permissionType: string) => request({
  url: `${PATH}/check`,
  method: 'get',
  params: { channelMemberIdx, permissionType },
});

/**
 * 권한 확인 (현재 사용자의 channelUid로)
 */
export const checkPermissionByUser = (channelUid: string, permissionType: string) => request({
  url: `${PATH}/check-by-user`,
  method: 'get',
  params: { channelUid, permissionType },
});

/**
 * 권한 삭제
 */
export const deletePermission = (permissionId: number) => request({
  url: `${PATH}/${permissionId}`,
  method: 'delete',
});

/**
 * 모든 권한 타입 조회
 */
export const getAllPermissionTypes = () => request({
  url: `${PATH}/types`,
  method: 'get',
});
