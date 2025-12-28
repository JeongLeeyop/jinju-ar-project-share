<template>
  <div class="space-management-page" v-loading="loading">
    <CommunitySidebar 
      :selectedSpaceId="'space-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="space-management-main">
      <div class="page-header">
        <h1 class="page-title">공간 관리</h1>
        <p class="page-subtitle" v-if="!hasSpaceCreatePermission && !isChannelAdmin">
          공간 관리 권한이 없습니다.
        </p>
      </div>

      <!-- 탭 메뉴 (각 공간별) -->
      <div class="tabs-section">
        <button 
          v-for="space in allSpaces" 
          :key="space.uid"
          class="tab-btn" 
          :class="{ active: selectedSpace?.uid === space.uid }"
          @click="selectSpace(space)"
        >
          <span class="space-dot" :class="space.spaceType === 'BOARD' ? 'red-dot' : 'orange-dot'"></span>
          {{ space.name }}
        </button>
      </div>

      <!-- 선택된 공간의 멤버 관리 -->
      <div v-if="selectedSpace" class="member-management-section">
        <div class="section-header">
          <div class="section-header-left">
            <h2 class="section-title">
              <span class="space-dot" :class="selectedSpace.spaceType === 'BOARD' ? 'red-dot' : 'orange-dot'"></span>
              {{ selectedSpace.name }} 멤버 관리
            </h2>
            <div class="space-info">
              <span class="info-item">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" style="vertical-align: middle; margin-right: 4px;">
                  <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  <mask id="mask0_space_info" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="16" height="16">
                    <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_space_info)">
                    <rect x="1.78" y="9.33" width="12.44" height="14.22" rx="6.22" fill="#999"/>
                    <circle cx="8" cy="4.89" r="3.11" fill="#999"/>
                  </g>
                </svg>
                멤버 {{ selectedSpace.memberCount || 0 }}명
              </span>
              <span class="info-item" :class="selectedSpace.isPublic ? 'public-badge' : 'private-badge'">
                <i :class="selectedSpace.isPublic ? 'el-icon-unlock' : 'el-icon-lock'"></i>
                {{ selectedSpace.isPublic ? '공개 공간' : '비공개 공간' }}
              </span>
              <span class="info-item">
                <i class="el-icon-date"></i>
                생성일: {{ formatDate(selectedSpace.createdAt) }}
              </span>
            </div>
          </div>
          <el-button
            type="danger"
            size="small"
            icon="el-icon-delete"
            @click="deleteSpace(selectedSpace)"
            :loading="loading"
          >
            공간 삭제
          </el-button>
        </div>

        <!-- 검색 -->
        <div class="search-section">
          <el-input
            v-model="memberSearchQuery"
            placeholder="멤버 검색 (이름, 이메일)"
            prefix-icon="el-icon-search"
            clearable
          />
        </div>

        <!-- 멤버 목록 -->
        <div class="member-list">
          <div
            v-for="member in filteredMembers"
            :key="member.uid"
            class="member-item"
          >
            <div class="member-info">
              <div class="member-avatar">
                <img 
                  v-if="member.iconFileUid || member.profileImageUid || member.profileImage" 
                  :src="getMemberImage(member)" 
                  alt="프로필 이미지" 
                  class="member-avatar-img"
                  @error="onImageError"
                >
                <svg v-else width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                  <mask id="mask0_space_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="40" height="40">
                    <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_space_member)">
                    <rect x="4.44" y="23.33" width="31.11" height="35.56" rx="15.56" fill="#F5F5F5"/>
                    <circle cx="20" cy="12.22" r="7.78" fill="#F5F5F5"/>
                  </g>
                </svg>
              </div>
              <div class="member-details">
                <h4 class="member-name">{{ member.name || member.userName || '이름 없음' }}</h4>
                <p class="member-email">{{ member.email || '-' }}</p>
              </div>
            </div>
            <div class="member-actions">
              <el-button
                type="danger"
                size="small"
                icon="el-icon-delete"
                @click="kickMember(member)"
                :disabled="selectedSpace.isPublic"
              >
                추방
              </el-button>
              <el-tooltip 
                v-if="selectedSpace.isPublic" 
                content="공개 공간에서는 멤버를 추방할 수 없습니다" 
                placement="top"
              >
                <i class="el-icon-info" style="color: #999; margin-left: 4px;"></i>
              </el-tooltip>
            </div>
          </div>

          <div v-if="filteredMembers.length === 0" class="empty-members">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
              <mask id="mask0_empty_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
                <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
              </mask>
              <g mask="url(#mask0_empty_member)">
                <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
                <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
              </g>
            </svg>
            <p>{{ memberSearchQuery ? '검색 결과가 없습니다.' : '멤버가 없습니다.' }}</p>
          </div>
        </div>

        <!-- 초대 섹션 (비공개 공간만) -->
        <div v-if="!selectedSpace.isPublic" class="invite-section">
          <div class="invite-header">
            <h3 class="section-subtitle">
              <i class="el-icon-plus"></i>
              멤버 초대
            </h3>
            <el-button
              type="primary"
              icon="el-icon-plus"
              @click="inviteSelectedMembers"
              :disabled="selectedInviteMembers.length === 0"
            >
              선택한 멤버 초대 ({{ selectedInviteMembers.length }})
            </el-button>
          </div>

          <!-- 초대 가능 멤버 검색 -->
          <div class="invite-search">
            <el-input
              v-model="inviteSearchQuery"
              placeholder="초대할 회원 검색 (이름, 이메일)"
              prefix-icon="el-icon-search"
              clearable
            />
          </div>

          <!-- 초대 가능 멤버 목록 -->
          <div class="invite-member-list">
            <div
              v-for="member in filteredInviteMembers"
              :key="member.uid"
              class="invite-member-item"
              :class="{ selected: isSelectedForInvite(member.uid) }"
              @click="toggleInviteMember(member)"
            >
              <el-checkbox
                :value="isSelectedForInvite(member.uid)"
                @click.native.stop="toggleInviteMember(member)"
              />
              <div class="member-info">
                <div class="member-avatar">
                  <img 
                    v-if="member.iconFileUid || member.profileImageUid || member.profileImage" 
                    :src="getMemberImage(member)" 
                    alt="프로필 이미지" 
                    class="member-avatar-img"
                    @error="onImageError"
                  >
                  <svg v-else width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                    <mask id="mask0_invite_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="40" height="40">
                      <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_invite_member)">
                      <rect x="4.44" y="23.33" width="31.11" height="35.56" rx="15.56" fill="#F5F5F5"/>
                      <circle cx="20" cy="12.22" r="7.78" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="member-details">
                  <h4 class="member-name">{{ member.name || member.userName || '이름 없음' }}</h4>
                  <p class="member-email">{{ member.email || '-' }}</p>
                </div>
              </div>
            </div>

            <div v-if="filteredInviteMembers.length === 0" class="empty-invite-members">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                <mask id="mask0_empty_invite" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
                  <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_empty_invite)">
                  <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
                  <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
                </g>
              </svg>
              <p>{{ inviteSearchQuery ? '검색 결과가 없습니다.' : '초대 가능한 멤버가 없습니다.' }}</p>
            </div>
          </div>
        </div>
        
        <!-- 공개 공간 안내 메시지 -->
        <div v-else class="public-space-notice">
          <div class="notice-icon">
            <i class="el-icon-info"></i>
          </div>
          <div class="notice-content">
            <h3>공개 공간</h3>
            <p>
              공개 공간은 커뮤니티의 모든 멤버가 자동으로 참여합니다.<br>
              별도의 초대 없이 커뮤니티 멤버 전체가 이 공간을 이용할 수 있습니다.
            </p>
          </div>
        </div>
      </div>

      <!-- 공간이 없는 경우 -->
      <div v-else class="empty-container">
        <i class="el-icon-folder-opened"></i>
        <p>생성한 공간이 없습니다.</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { 
  getSpacesByChannel, 
  getSpaceMembers, 
  getInvitableUsers, 
  inviteToSpace, 
  removeMember,
  deleteSpace,
  Space as SpaceType,
} from '@/api/space';
import { checkPermissionByUser } from '@/api/channelMemberPermission';
import { getChannelDomainDetail } from '@/api/channel';
import { getUserInfo } from '@/api/user';

interface Space {
  uid: string;
  name: string;
  spaceType: string;
  memberCount?: number;
  createdAt?: string;
  isPublic: boolean;
  channelUid?: string;
  isActive?: boolean;
  iconUrl?: string;
}

interface Member {
  uid: string;
  userUid?: string;
  name: string;
  userName?: string;
  email: string;
  iconFileUid?: string;
  profileImageUid?: string;
  profileImage?: string;
}

@Component({
  name: 'SpaceManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private selectedSpace: Space | null = null;
  private memberSearchQuery = '';
  private inviteSearchQuery = '';
  private selectedInviteMembers: string[] = [];
  private currentChannelUid = '';
  private loading = false;
  private hasSpaceCreatePermission = false;
  private isChannelAdmin = false;

  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  // 실제 공간 목록 (API에서 가져온 데이터)
  private allSpacesList: Space[] = [];

  // 모든 공간 목록 (게시판 + 채팅)
  get allSpaces() {
    return this.allSpacesList.filter(space => space.isActive !== false);
  }

  get boardSpaces() {
    return this.allSpaces.filter(space => space.spaceType === 'BOARD');
  }

  get chatSpaces() {
    return this.allSpaces.filter(space => space.spaceType === 'CHAT');
  }

  // 현재 공간의 멤버 목록
  private members: Member[] = [];

  // 초대 가능한 멤버 목록
  private communityMembers: Member[] = [];

  // 현재 공간 멤버 필터링
  get filteredMembers() {
    if (!this.memberSearchQuery) {
      return this.members;
    }
    const query = this.memberSearchQuery.toLowerCase();
    return this.members.filter(
      (member: Member) =>
        member.name.toLowerCase().includes(query) ||
        member.email.toLowerCase().includes(query),
    );
  }

  // 초대 가능한 멤버 목록 (현재 공간 멤버 제외)
  get availableInviteMembers() {
    const currentMemberUids = this.members.map((m: Member) => m.uid);
    return this.communityMembers.filter(
      (member: Member) => !currentMemberUids.includes(member.uid),
    );
  }

  // 초대 가능한 멤버 검색 필터링
  get filteredInviteMembers() {
    if (!this.inviteSearchQuery) {
      return this.availableInviteMembers;
    }
    const query = this.inviteSearchQuery.toLowerCase();
    return this.availableInviteMembers.filter(
      (member: Member) =>
        member.name.toLowerCase().includes(query) ||
        member.email.toLowerCase().includes(query),
    );
  }

  // 라이프사이클: 페이지 진입 시 권한 체크 및 데이터 로드
  async created() {
    await this.checkPermissions();
    if (this.hasSpaceCreatePermission || this.isChannelAdmin) {
      await this.loadSpaces();
    }
  }

  // 권한 체크
  private async checkPermissions() {
    try {
      // URL에서 채널 도메인 가져오기
      const domain = this.$route.params.domain;
      if (!domain) {
        this.$message.error('커뮤니티 정보를 찾을 수 없습니다.');
        this.$router.push('/');
        return;
      }

      // 채널 정보 가져오기
      const channelResponse = await getChannelDomainDetail(domain);
      this.currentChannelUid = channelResponse.data.uid;

      // 공간 생성 권한 체크
      const permissionResponse = await checkPermissionByUser(
        this.currentChannelUid, 
        'SPACE_CREATE'
      );
      this.hasSpaceCreatePermission = permissionResponse.data.hasPermission;

      // 커뮤니티 관리자 권한 체크
      const response: any = await getUserInfo();
      const currentUserInfo: any = response.data;

      this.isChannelAdmin = channelResponse?.data.userUid === currentUserInfo.uid;

      // 권한이 없으면 접근 거부
      if (!this.hasSpaceCreatePermission && !this.isChannelAdmin) {
        this.$message.error('공간 관리 권한이 없습니다.');
        this.$router.push(`/channel/${domain}`);
      }
    } catch (error: any) {
      console.error('권한 체크 실패:', error);
      this.$message.error('권한 확인에 실패했습니다.');
      const domain = this.$route.params.domain;
      if (domain) {
        this.$router.push(`/channel/${domain}`);
      } else {
        this.$router.push('/');
      }
    }
  }

  // 공간 목록 로드
  private async loadSpaces() {
    if (!this.currentChannelUid) return;

    this.loading = true;
    try {
      const response = await getSpacesByChannel(this.currentChannelUid);
      this.allSpacesList = response.data;
      
      // 첫 번째 공간 자동 선택
      if (this.allSpacesList.length > 0) {
        this.selectSpace(this.allSpacesList[0]);
      }
    } catch (error: any) {
      const message = error.response?.data?.message || '공간 목록 조회에 실패했습니다.';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private async selectSpace(space: Space) {
    this.selectedSpace = space;
    
    // 공간 선택 시 멤버 목록과 초대 가능 사용자 목록 로드
    await this.loadMembers();
    if (!space.isPublic) {
      await this.loadInvitableUsers();
    }
  }

  private formatDate(date?: string) {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('ko-KR');
  }

  // 멤버 이미지 URL 가져오기
  private getMemberImage(member: Member): string {
    const imageUid = member.iconFileUid || member.profileImageUid || member.profileImage;
    if (!imageUid) return '';
    
    // 이미 전체 URL인 경우 그대로 반환
    if (imageUid.startsWith('http://') || imageUid.startsWith('https://')) {
      return imageUid;
    }
    
    // API URL과 조합하여 반환
    return `${this.apiUrl}/attached-file/${imageUid}`;
  }

  // 이미지 로드 실패 시 처리
  private onImageError(event: Event) {
    const target = event.target as HTMLImageElement;
    if (target) {
      // 기본 아바타로 변경 (이미지 숨기기)
      target.style.display = 'none';
    }
  }

  // 초대할 멤버 선택 토글
  private toggleInviteMember(member: Member) {
    const index = this.selectedInviteMembers.indexOf(member.uid);
    if (index > -1) {
      this.selectedInviteMembers.splice(index, 1);
    } else {
      this.selectedInviteMembers.push(member.uid);
    }
  }

  // 멤버가 선택되었는지 확인
  private isSelectedForInvite(uid: string): boolean {
    return this.selectedInviteMembers.includes(uid);
  }

  // 선택한 멤버들 초대
  private async inviteSelectedMembers() {
    if (this.selectedInviteMembers.length === 0) return;

    const selectedMembers = this.communityMembers.filter((m: Member) => 
      this.selectedInviteMembers.includes(m.uid)
    );
    
    const selectedNames = selectedMembers
      .map((m: Member) => m.name || m.userName || '이름 없음')
      .join(', ');

    this.loading = true;
    try {
      // 각 선택된 멤버에게 초대 발송
      const invitePromises = selectedMembers.map((member: Member) => {
        const targetUid = member.userUid || member.uid;
        return inviteToSpace({
          spaceUid: this.selectedSpace!.uid,
          invitedUserUid: targetUid,
          message: `${this.selectedSpace!.name} 공간에 초대합니다.`,
        });
      });

      await Promise.all(invitePromises);

      this.$message.success(
        `${this.selectedInviteMembers.length}명의 멤버가 공간에 추가되었습니다. (${selectedNames})`,
      );

      // 초대 완료 후 선택 초기화
      this.selectedInviteMembers = [];
      this.inviteSearchQuery = '';

      // 목록 갱신
      await this.loadMembers();
      await this.loadInvitableUsers();
    } catch (error: any) {
      console.error('멤버 초대 에러:', error);
      const message = error.response?.data?.message || '멤버 초대에 실패했습니다.';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  // 공간 멤버 목록 로드
  private async loadMembers() {
    if (!this.selectedSpace) return;
    
    this.loading = true;
    try {
      const response = await getSpaceMembers(this.selectedSpace.uid);
      const rawMembers = response.data || [];
      
      // API 응답 데이터를 Member 인터페이스 형식으로 변환
      this.members = rawMembers.map((member: any) => ({
        uid: member.uid || member.userUid || '',
        userUid: member.userUid || member.uid,
        name: member.name || member.userName || member.actualName || '이름 없음',
        userName: member.userName || member.name,
        email: member.email || member.userId || '',
        iconFileUid: member.iconFileUid || member.profileImageUid || member.profileImage || null,
        profileImageUid: member.profileImageUid || member.iconFileUid,
        profileImage: member.profileImage || member.iconFileUid,
      }));
      
      console.log('Loaded members:', this.members); // 디버깅용
      
      // 멤버 수 업데이트
      if (this.selectedSpace) {
        this.selectedSpace.memberCount = this.members.length;
      }
    } catch (error: any) {
      console.error('멤버 목록 조회 에러:', error);
      const message = error.response?.data?.message || '멤버 목록 조회에 실패했습니다.';
      this.$message.error(message);
      this.members = [];
    } finally {
      this.loading = false;
    }
  }

  // 초대 가능한 사용자 목록 로드
  private async loadInvitableUsers() {
    if (!this.selectedSpace) return;
    
    this.loading = true;
    try {
      const response = await getInvitableUsers(this.selectedSpace.uid);
      const rawUsers = response.data || [];
      
      // API 응답 데이터를 Member 인터페이스 형식으로 변환
      this.communityMembers = rawUsers.map((member: any) => ({
        uid: member.uid || member.userUid || '',
        userUid: member.userUid || member.uid,
        name: member.name || member.userName || member.actualName || '이름 없음',
        userName: member.userName || member.name,
        email: member.email || member.userId || '',
        iconFileUid: member.iconFileUid || member.profileImageUid || member.profileImage || null,
        profileImageUid: member.profileImageUid || member.iconFileUid,
        profileImage: member.profileImage || member.iconFileUid,
      }));
      
      console.log('Loaded invitable users:', this.communityMembers); // 디버깅용
    } catch (error: any) {
      console.error('초대 가능 사용자 조회 에러:', error);
      const message = error.response?.data?.message || '초대 가능 사용자 조회에 실패했습니다.';
      this.$message.error(message);
      this.communityMembers = [];
    } finally {
      this.loading = false;
    }
  }

  // 멤버 추방
  private kickMember(member: Member) {
    // 공개 공간에서는 추방 불가
    if (this.selectedSpace?.isPublic) {
      this.$message.warning('공개 공간에서는 멤버를 추방할 수 없습니다.');
      return;
    }

    this.$confirm(`${member.name || member.userName}님을 추방하시겠습니까?`, '멤버 추방', {
      confirmButtonText: '추방',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(async () => {
      if (!this.selectedSpace) return;

      this.loading = true;
      try {
        // userUid가 있으면 사용, 없으면 uid 사용
        const targetUid = member.userUid || member.uid;
        await removeMember(this.selectedSpace.uid, targetUid);
        this.$message.success(`${member.name || member.userName}님을 추방했습니다.`);
        
        // 목록 갱신
        await this.loadMembers();
        if (!this.selectedSpace.isPublic) {
          await this.loadInvitableUsers();
        }
      } catch (error: any) {
        console.error('멤버 추방 에러:', error);
        const message = error.response?.data?.message || '멤버 추방에 실패했습니다.';
        this.$message.error(message);
      } finally {
        this.loading = false;
      }
    }).catch(() => {
      // 취소
    });
  }

  // 공간 삭제 (소프트 삭제)
  private deleteSpace(space: Space) {
    this.$confirm(
      `"${space.name}" 공간을 삭제하시겠습니까? 이 작업은 공간을 비활성화하며, 멤버들은 더 이상 이 공간을 볼 수 없게 됩니다.`,
      '공간 삭제',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
        dangerouslyUseHTMLString: true,
      }
    ).then(async () => {
      this.loading = true;
      try {
        await deleteSpace(space.uid);
        this.$message.success(`"${space.name}" 공간이 삭제되었습니다.`);
        
        // 목록에서 제거 (또는 재로드)
        await this.loadSpaces();
        
        // 선택된 공간이 삭제된 경우 선택 해제
        if (this.selectedSpace?.uid === space.uid) {
          this.selectedSpace = null;
        }
      } catch (error: any) {
        const message = error.response?.data?.message || '공간 삭제에 실패했습니다.';
        this.$message.error(message);
      } finally {
        this.loading = false;
      }
    }).catch(() => {
      // 취소
    });
  }
}
</script>

<style scoped lang="scss">
.space-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.space-management-main {
  flex: 1;
  margin-left: 270px;
  padding: 160px 40px 100px 40px;
}

.page-header {
  margin-bottom: 40px;
}

.page-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 32px;
  font-weight: 700;
  line-height: 100%;
  margin: 0 0 8px 0;
}

.page-subtitle {
  color: #999;
  font-size: 14px;
  margin: 8px 0 0 0;
}

// 탭 메뉴
.tabs-section {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 40px;
  border-bottom: 2px solid #EBEBEB;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: thin;
  
  &::-webkit-scrollbar {
    height: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #EBEBEB;
    border-radius: 2px;
    
    &:hover {
      background: #D0D0D0;
    }
  }
}

.tab-btn {
  flex: 0 0 auto; // 고정 너비로 변경
  min-width: fit-content;
  padding: 16px 24px;
  background: none;
  border: none;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap; // 텍스트 줄바꿈 방지

  &:hover {
    color: #073DFF;
  }

  &.active {
    color: #073DFF;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 3px;
      background: #073DFF;
    }
  }
}

.space-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  flex-shrink: 0;

  &.red-dot {
    background-color: #FF5858;
  }

  &.orange-dot {
    background-color: #FFAD3A;
  }
}

.search-section {
  margin-bottom: 24px;

  ::v-deep .el-input {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 20px;
    border-radius: 10px;
    border: 2px solid #EBEBEB;
  }

  ::v-deep .el-input__inner {
    height: auto;
    flex: 1;
    border: none;
    outline: none;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 100%;
    background: transparent;
    margin-left: 10px;
    padding: 0 10px;
  }
}

.member-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 30px;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  padding: 16px;
}

.member-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #F8F9FA;
  border-radius: 8px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #E0E7FF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #073DFF;
  font-size: 24px;
  overflow: hidden;
  flex-shrink: 0;
}

.member-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.member-details {
  text-align: left;
}

.member-name {
  font-size: 18px;
  line-height: 1.4em;
  font-weight: 600;
  color: #222;
  margin: 0 0 4px 0;
}

.member-email {
  font-size: 18px;
  color: #666;
  margin: 0;
}

.empty-members {
  text-align: center;
  padding: 40px 20px;
  color: #999;

  i {
    font-size: 36px;
    margin-bottom: 12px;
    display: block;
  }

  p {
    font-size: 14px;
    margin: 0;
  }
}

.invite-section {
  padding-top: 24px;
  border-top: 1px solid #EBEBEB;
}

.invite-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.section-subtitle {
  font-size: 22px;
  font-weight: 700;
  color: #222;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: #073DFF;
    position: relative;
    padding: 0 0 0 20px;

    &:before {
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-40%);
    }
  }
}

.invite-search {
  margin-bottom: 16px;

  .el-input {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 20px;
    border-radius: 10px;
    border: 2px solid #EBEBEB;
  }

  ::v-deep .el-input__inner {
    height: auto;
    flex: 1;
    border: none;
    outline: none;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 100%;
    background: transparent;
    margin-left: 10px;
    padding: 0 10px;
  }
}

.invite-member-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  padding: 12px;
}

.invite-member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #F8F9FA;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:last-child {
    margin-bottom: 0;
  }

  &:hover {
    background: #E0E7FF;
  }

  &.selected {
    background: #E0E7FF;
    border: 2px solid #073DFF;
  }

  .member-info {
    flex: 1;
  }
}

.empty-invite-members {
  text-align: center;
  padding: 40px 20px;
  color: #999;

  i {
    font-size: 36px;
    margin-bottom: 12px;
    display: block;
  }

  p {
    font-size: 14px;
    margin: 0;
  }
}

// 공개 공간 안내
.public-space-notice {
  padding-top: 24px;
  border-top: 1px solid #EBEBEB;
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: #F7F9FC;
  border: 1px solid #E3F2FD;
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;

  .notice-icon {
    flex-shrink: 0;
    width: 48px;
    height: 48px;
    background: #073DFF;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #FFF;

    i {
      font-size: 24px;
    }
  }

  .notice-content {
    flex: 1;

    h3 {
      font-size: 18px;
      font-weight: 700;
      color: #222;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      margin: 0;
    }
  }
}

// 멤버 관리 섹션
.member-management-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.section-header {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.section-header-left {
  flex: 1;
  min-width: 0;
}

.section-title {
  font-size: 26px;
  font-weight: 700;
  color: #222;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.space-info {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 14px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;

  i {
    color: #073DFF;
  }

  &.public-badge {
    background: #E3F2FD;
    color: #1976D2;
    padding: 6px 12px;
    border-radius: 6px;
    font-weight: 600;
    font-size: 13px;

    i {
      color: #1976D2;
    }
  }

  &.private-badge {
    background: #FFF3E0;
    color: #F57C00;
    padding: 6px 12px;
    border-radius: 6px;
    font-weight: 600;
    font-size: 13px;

    i {
      color: #F57C00;
    }
  }
}

// Empty container
.empty-container {
  text-align: center;
  padding: 80px 20px;
  color: #999;

  i {
    font-size: 64px;
    margin-bottom: 20px;
    display: block;
  }

  p {
    font-size: 18px;
    margin: 0;
  }
}

.tab-btn .space-dot {
  display: inline-block;
  margin-right: 8px;
  vertical-align: middle;
}

// 멤버 액션 버튼
.member-actions {
  display: flex;
  gap: 8px;
}

// 모바일 반응형
@media screen and (max-width: 1024px) {
  .space-management-main {
    padding: 160px 30px 80px 30px;
    margin-left: 240px;
  }
}

@media screen and (max-width: 768px) {
  .space-management-main {
    margin-left: 0;
    padding: 140px 30px 80px 30px;
  }

  .tabs-section {
    position: relative;
    gap: 12px;
    overflow-x: auto;
    overflow-y: hidden;
    white-space: nowrap;
    padding-bottom: 2px;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;
    
    &::-webkit-scrollbar {
      display: none;
    }
    
    // 좌우 그라데이션 효과로 스크롤 가능함을 암시
    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 0;
      bottom: 2px;
      width: 20px;
      pointer-events: none;
      z-index: 1;
    }
    
    &::before {
      left: 0;
      background: linear-gradient(to right, rgba(248, 249, 250, 0.9), transparent);
    }
    
    &::after {
      right: 0;
      background: linear-gradient(to left, rgba(248, 249, 250, 0.9), transparent);
    }
  }

  .tab-btn {
    flex: 0 0 auto;
    min-width: fit-content;
    max-width: 160px;
    font-size: 16px;
    padding: 12px 16px;
  }

  .member-management-section {
    padding: 24px 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .section-header-left {
    width: 100%;
  }

  .space-info {
    width: 100%;
    flex-wrap: wrap;
    gap: 8px;
  }

  .info-item {
    &.public-badge,
    &.private-badge {
      order: -1;
      width: 100%;
      justify-content: center;
      padding: 8px 12px;
      font-size: 14px;
    }
  }

  .member-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .member-actions {
    width: 100%;
    align-items: center;

    .el-button {
      flex: 1;
    }

    .el-icon-info {
      font-size: 18px;
    }
  }

  .invite-header {
    flex-direction: column;
    align-items: stretch;

    .el-button {
      width: 100%;
      span {
        font-size: 18px;
      }
    }
  }

  .invite-member-list {
    max-height: 250px;
  }

  .invite-member-item {
    flex-direction: row;
    align-items: center;
  }

  .space-management-container {
    padding: 20px 16px;
  }

  .space-list {
    grid-template-columns: 1fr;
  }
}

// 425px 이하 모바일 반응형
@media screen and (max-width: 500px) {
  .space-management-main {
    padding: 120px 20px 80px 20px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .tabs-section {
    gap: 8px;
    margin-bottom: 24px;
    overflow-x: auto;
    overflow-y: hidden;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none; // Firefox
    
    &::-webkit-scrollbar {
      display: none; // Chrome, Safari, Edge
    }
    
    // 스크롤 스냅 추가 (선택사항)
    scroll-snap-type: x proximity;
    scroll-behavior: smooth;
  }

  .tab-btn {
    flex: 0 0 auto; // flex: 1 대신 auto로 변경
    min-width: fit-content;
    max-width: 150px; // 최대 너비 제한
    padding: 12px 16px;
    gap: 6px;
    white-space: nowrap;
    scroll-snap-align: start; // 스크롤 스냅 정렬
  }

  .space-dot {
    width: 12px;
    height: 12px;
  }

  .member-management-section {
    padding: 20px 16px;
    border-radius: 10px;
  }

  .section-header {
    margin-bottom: 20px;
    gap: 12px;
    flex-direction: column;
    
    // 삭제 버튼을 전체 너비로 조정
    > .el-button {
      width: 100%;
      margin-top: 8px;
      order: 1;
    }
  }

  .section-header-left {
    width: 100%;
    order: 0;
  }

  .section-title {
    font-size: 18px;
    gap: 8px;
  }

  .space-info {
    gap: 8px;
    font-size: 12px;
    flex-wrap: wrap;
    width: 100%;
  }

  .info-item {
    gap: 4px;
    font-size: 12px;

    &.public-badge,
    &.private-badge {
      order: -1;
      width: 100%;
      justify-content: center;
      padding: 6px 12px;
      font-size: 13px;
      margin-bottom: 8px;
    }
  }

  .search-section {
    margin-bottom: 16px;

    ::v-deep .el-input__inner {
      height: 40px;
      font-size: 14px;
    }
  }

  .member-list {
    max-height: 300px;
    padding: 12px;
    margin-bottom: 20px;
  }

  .member-item {
    padding: 12px;
    margin-bottom: 10px;
    border-radius: 8px;
  }

  .member-avatar {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .member-details {
    .member-name {
      font-size: 14px;
    }

    .member-email {
      font-size: 12px;
    }
  }

  .member-actions {
    width: 100%;
    align-items: center;
    justify-content: space-between;

    .el-button {
      flex: 1;
      max-width: calc(100% - 30px);
    }

    .el-icon-info {
      font-size: 20px;
      margin-left: 8px;
    }
  }

  .empty-members {
    padding: 30px 16px;

    i {
      font-size: 28px;
      margin-bottom: 10px;
    }

    p {
      font-size: 13px;
    }
  }

  .invite-section {
    padding-top: 20px;
  }

  .invite-header {
    margin-bottom: 12px;
    gap: 12px;
  }

  .section-subtitle {
    font-size: 16px;
    gap: 6px;
  }

  .invite-search {
    margin-bottom: 12px;

    ::v-deep .el-input__inner {
      height: 40px;
      font-size: 14px;
    }
  }

  .invite-member-list {
    max-height: 220px;
    padding: 10px;
  }

  .invite-member-item {
    padding: 10px;
    gap: 10px;
    margin-bottom: 6px;

    .member-avatar {
      width: 36px;
      height: 36px;
      font-size: 18px;
    }

    .member-name {
      font-size: 14px;
    }

    .member-email {
      font-size: 12px;
    }
  }

  .empty-invite-members {
    padding: 30px 16px;

    i {
      font-size: 28px;
      margin-bottom: 10px;
    }

    p {
      font-size: 13px;
    }
  }

  .public-space-notice {
    padding: 20px 16px;
    flex-direction: column;
    align-items: center;
    text-align: center;

    .notice-icon {
      width: 40px;
      height: 40px;
      margin-bottom: 12px;

      i {
        font-size: 20px;
      }
    }

    .notice-content {
      h3 {
        font-size: 16px;
        margin-bottom: 6px;
      }

      p {
        font-size: 13px;
        line-height: 1.5;
      }
    }
  }

  .empty-container {
    padding: 60px 20px;

    i {
      font-size: 48px;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
    }
  }

  // Element UI 버튼 조정
  ::v-deep .el-button {
    padding: 10px 16px;
    font-size: 13px;

    &--primary {
      width: 100%;
    }
  }

  ::v-deep .el-checkbox {
    .el-checkbox__inner {
      width: 16px;
      height: 16px;
    }
  }

  // Element UI 툴팁 모바일 최적화
  ::v-deep .el-tooltip__popper {
    font-size: 12px;
    max-width: 200px;
  }

  // 공개 공간 아이콘 강조
  .el-icon-info {
    color: #F57C00 !important;
  }
}

// 추가 반응형 - 매우 작은 화면 (360px 이하)
@media screen and (max-width: 360px) {
  .space-management-main {
    padding: 100px 12px 60px 12px;
  }

  .page-title {
    font-size: 18px;
  }

  .tab-btn {
    flex: 0 0 auto;
    min-width: fit-content;
    max-width: 120px; // 초소형 화면에서는 더 작게
    padding: 10px 12px;
    font-size: 13px;
    gap: 4px;
  }

  .space-dot {
    width: 10px;
    height: 10px;
  }

  .member-management-section {
    padding: 16px 12px;
  }

  .section-title {
    font-size: 16px;
  }

  .member-avatar {
    width: 36px;
    height: 36px;
  }

  .member-name {
    font-size: 13px;
  }

  .member-email {
    font-size: 11px;
  }

  .info-item {
    font-size: 11px;

    &.public-badge,
    &.private-badge {
      font-size: 12px;
      padding: 5px 10px;
    }
  }

  .section-header > .el-button {
    font-size: 13px;
    padding: 10px 16px;
  }

  .member-actions .el-button {
    font-size: 12px;
    padding: 8px 12px;
  }
}
</style>