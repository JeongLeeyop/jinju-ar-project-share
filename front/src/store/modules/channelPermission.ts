import * as vuexModuleDecorators from 'vuex-module-decorators';
import store from '@/store';
import { getMyPermissions, MyPermissionsResponse } from '@/api/channelMemberPermission';

/**
 * 채널별 사용자 권한 상태 인터페이스
 */
export interface IChannelPermissionState {
  isChannelAdmin: boolean;
  isMember: boolean;
  isBanned: boolean;
  permissions: {
    POST_USE: boolean;           // 게시판 이용 (등록/수정/삭제 통합)
    SPACE_CREATE: boolean;
    MARKETPLACE_USE: boolean;
    MARKETPLACE_REGISTER: boolean;
    OFFLINE_MARKETPLACE_REGISTER: boolean;
    SCHEDULE_PARTICIPATE: boolean;
    SCHEDULE_CREATE: boolean;
    [key: string]: boolean;
  };
  currentChannelUid: string;
  loaded: boolean;
  loading: boolean;
}

/**
 * 기본 권한 상태
 */
const defaultPermissions = {
  POST_USE: false,               // 게시판 이용 (등록/수정/삭제 통합)
  SPACE_CREATE: false,
  MARKETPLACE_USE: false,
  MARKETPLACE_REGISTER: false,
  OFFLINE_MARKETPLACE_REGISTER: false,
  SCHEDULE_PARTICIPATE: false,
  SCHEDULE_CREATE: false,
};

@vuexModuleDecorators.Module({ dynamic: true, store, name: 'channelPermission' })
class ChannelPermission extends vuexModuleDecorators.VuexModule implements IChannelPermissionState {
  public isChannelAdmin = false;
  public isMember = false;
  public isBanned = false;
  public permissions = { ...defaultPermissions };
  public currentChannelUid = '';
  public loaded = false;
  public loading = false;

  /**
   * 권한 상태 설정
   */
  @vuexModuleDecorators.Mutation
  private SET_PERMISSIONS(data: MyPermissionsResponse) {
    this.isChannelAdmin = data.isChannelAdmin || false;
    this.isMember = data.isMember !== false; // undefined면 true
    this.isBanned = data.isBanned || false;
    
    // 권한 매핑
    if (data.permissions) {
      this.permissions = {
        POST_USE: data.permissions.POST_USE || false,  // 게시판 이용 (등록/수정/삭제 통합)
        SPACE_CREATE: data.permissions.SPACE_CREATE || false,
        MARKETPLACE_USE: data.permissions.MARKETPLACE_USE || false,
        MARKETPLACE_REGISTER: data.permissions.MARKETPLACE_REGISTER || false,
        OFFLINE_MARKETPLACE_REGISTER: data.permissions.OFFLINE_MARKETPLACE_REGISTER || false,
        SCHEDULE_PARTICIPATE: data.permissions.SCHEDULE_PARTICIPATE || false,
        SCHEDULE_CREATE: data.permissions.SCHEDULE_CREATE || false,
      };
    }
    
    this.loaded = true;
  }

  @vuexModuleDecorators.Mutation
  private SET_CHANNEL_UID(channelUid: string) {
    this.currentChannelUid = channelUid;
  }

  @vuexModuleDecorators.Mutation
  private SET_LOADING(loading: boolean) {
    this.loading = loading;
  }

  @vuexModuleDecorators.Mutation
  private RESET_PERMISSIONS() {
    this.isChannelAdmin = false;
    this.isMember = false;
    this.isBanned = false;
    this.permissions = { ...defaultPermissions };
    this.loaded = false;
  }

  /**
   * 채널의 사용자 권한 로드
   * @param channelUid 채널 UID (domain 또는 uid)
   */
  @vuexModuleDecorators.Action
  public async loadPermissions(channelUid: string) {
    // 같은 채널이고 이미 로드됨 → 스킵
    if (this.currentChannelUid === channelUid && this.loaded) {
      console.log('✅ 권한 이미 로드됨:', channelUid);
      return;
    }

    // 채널 변경 시 리셋
    if (this.currentChannelUid !== channelUid) {
      this.RESET_PERMISSIONS();
    }

    this.SET_CHANNEL_UID(channelUid);
    this.SET_LOADING(true);

    try {
      console.log('🔄 권한 로드 시작:', channelUid);
      const response = await getMyPermissions(channelUid);
      this.SET_PERMISSIONS(response.data);
      console.log('✅ 권한 로드 완료:', response.data);
    } catch (error) {
      console.error('❌ 권한 로드 실패:', error);
      // 실패 시 기본값 유지
      this.RESET_PERMISSIONS();
    } finally {
      this.SET_LOADING(false);
    }
  }

  /**
   * 권한 강제 새로고침
   */
  @vuexModuleDecorators.Action
  public async refreshPermissions() {
    if (!this.currentChannelUid) {
      console.warn('현재 채널 UID가 없습니다');
      return;
    }

    this.SET_LOADING(true);
    
    try {
      const response = await getMyPermissions(this.currentChannelUid);
      this.SET_PERMISSIONS(response.data);
      console.log('✅ 권한 새로고침 완료:', response.data);
    } catch (error) {
      console.error('❌ 권한 새로고침 실패:', error);
    } finally {
      this.SET_LOADING(false);
    }
  }

  /**
   * 권한 리셋 (로그아웃 시)
   */
  @vuexModuleDecorators.Action
  public resetPermissions() {
    this.RESET_PERMISSIONS();
    this.SET_CHANNEL_UID('');
  }

  /**
   * 특정 권한 확인
   * 커뮤니티 관리자는 모든 권한이 true
   */
  get hasPermission() {
    return (permissionType: string): boolean => {
      // 커뮤니티 관리자는 모든 권한
      if (this.isChannelAdmin) {
        return true;
      }
      // 추방된 회원은 모든 권한 없음
      if (this.isBanned) {
        return false;
      }
      // 멤버가 아니면 권한 없음
      if (!this.isMember) {
        return false;
      }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      return (this.permissions as any)[permissionType] || false;
    };
  }

  /**
   * 게시판 이용 권한 확인 (등록/수정/삭제 통합)
   */
  get canUsePost() {
    return this.hasPermission('POST_USE');
  }

  /**
   * 게시글 작성 권한 확인 (canUsePost 별칭)
   */
  get canCreatePost() {
    return this.canUsePost;
  }

  /**
   * 게시글 수정 권한 확인 (canUsePost 별칭)
   */
  get canUpdatePost() {
    return this.canUsePost;
  }

  /**
   * 게시글 삭제 권한 확인 (canUsePost 별칭)
   */
  get canDeletePost() {
    return this.canUsePost;
  }

  /**
   * 공간 생성 권한 확인
   */
  get canCreateSpace() {
    return this.hasPermission('SPACE_CREATE');
  }

  /**
   * 장터 이용 권한 확인
   */
  get canUseMarketplace() {
    return this.hasPermission('MARKETPLACE_USE');
  }

  /**
   * 장터 등록 권한 확인
   */
  get canRegisterMarketplace() {
    return this.hasPermission('MARKETPLACE_REGISTER');
  }

  /**
   * 오프라인 장터 등록 권한 확인
   */
  get canRegisterOfflineMarketplace() {
    return this.hasPermission('OFFLINE_MARKETPLACE_REGISTER');
  }

  /**
   * 일정 참여 권한 확인
   */
  get canParticipateSchedule() {
    return this.hasPermission('SCHEDULE_PARTICIPATE');
  }

  /**
   * 일정 생성 권한 확인
   */
  get canCreateSchedule() {
    return this.hasPermission('SCHEDULE_CREATE');
  }
}

export const ChannelPermissionModule = vuexModuleDecorators.getModule(ChannelPermission);
