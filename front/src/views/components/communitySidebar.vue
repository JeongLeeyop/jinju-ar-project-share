<template>
  <div class="community-sidebar" :data-domain="$route.params.domain">
    <div class="sidebar-content">
      <h2 class="sidebar-title" @click="goToCommunity">커뮤니티 공간</h2>
      
      <!-- Write Post Button - Show on marketplace, community space, lesson, and video list pages -->
      <div class="write-post-btn-wrapper">
        <button 
            v-if="showWriteButton" 
            class="write-post-btn"
            @click="openWriteModal"
          >
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14.166 2.5009C14.3849 2.28203 14.6447 2.10842 14.9307 1.98996C15.2167 1.87151 15.5232 1.81055 15.8327 1.81055C16.1422 1.81055 16.4487 1.87151 16.7347 1.98996C17.0206 2.10842 17.2805 2.28203 17.4993 2.5009C17.7182 2.71977 17.8918 2.97961 18.0103 3.26558C18.1287 3.55154 18.1897 3.85803 18.1897 4.16757C18.1897 4.4771 18.1287 4.78359 18.0103 5.06956C17.8918 5.35553 17.7182 5.61536 17.4993 5.83424L6.24935 17.0842L1.66602 18.3342L2.91602 13.7509L14.166 2.5009Z" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            {{ writeButtonText }}
        </button>
      </div>
      
      <el-menu
        class="space-menu"
        :default-openeds="['space-submenu']"
        :default-active="selectedSpaceId"
        background-color="transparent"
        text-color="#000"
        active-text-color="#073DFF"
        @select="handleMenuSelect"
      >
        <el-submenu index="space-submenu">
          <template slot="title">
            <span class="menu-title" @click.stop="openCreateSpaceModal">공간 개설하기</span>
            <svg class="plus-icon" @click.stop="openCreateSpaceModal" width="43" height="43" viewBox="0 0 43 43" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="1" y="1" width="41" height="41" rx="20.5" stroke="#073DFF" stroke-width="2"/>
              <path d="M21.5 14V29M29 21.5H14" stroke="#073DFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </template>
          
          <!-- Dynamic Space List from API -->
          <el-menu-item 
            v-for="space in spaces" 
            :key="space.uid" 
            :index="space.uid" 
            class="space-item"
          >
            <span 
              class="space-dot" 
              :class="getSpaceDotClass(space.spaceType)"
            ></span>
            <span class="space-name">{{ space.name }}</span>
            <span class="space-type-badge" :class="getSpaceTypeBadgeClass(space.spaceType)">
              {{ getSpaceTypeLabel(space.spaceType) }}
            </span>
            <!-- <span v-if="space.isSpaceAdmin && !isChannelAdmin" class="admin-badge">관리자</span> -->
          </el-menu-item>
          
          <!-- Empty State -->
          <div v-if="spaces.length === 0 && !loadingSpaces" class="empty-spaces">
            <p>아직 생성된 공간이 없습니다.</p>
          </div>
          
          <!-- Loading State -->
          <div v-if="loadingSpaces" class="loading-spaces">
            <i class="el-icon-loading"></i>
          </div>
        </el-submenu>
      </el-menu>

      <div class="marketplace-section">
        <div class="section-header">
          <h3 class="section-title" @click.stop="openCreateOfflineMarketplaceModal">오프라인 장터</h3>
          <svg 
            class="plus-icon" 
            @click.stop="openCreateOfflineMarketplaceModal" 
            width="43" 
            height="43" 
            viewBox="0 0 43 43" 
            fill="none" 
            xmlns="http://www.w3.org/2000/svg"
          >
            <rect x="1" y="1" width="41" height="41" rx="20.5" stroke="#073DFF" stroke-width="2"/>
            <path d="M21.5 14V29M29 21.5H14" stroke="#073DFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <div class="marketplace-menu">
          <!-- 로딩 상태 -->
          <div v-if="loadingOfflineMarketplaces" class="loading-spaces">
            <i class="el-icon-loading"></i>
          </div>
          <!-- 오프라인 장터 목록 -->
          <div
            v-for="market in offlineMarketplaces"
            :key="market.uid"
            class="marketplace-item"
            :class="{ active: ($route.name === 'OfflineMarketplace' || $route.name === 'Marketplace') && $route.params.marketplaceUid === market.uid }"
            @click="navigateToOfflineMarketplace(market.uid)"
          >
            <span class="item-text">{{ market.name }}</span>
          </div>

          <!-- 빈 상태 -->
          <div v-if="offlineMarketplaces.length === 0 && !loadingOfflineMarketplaces" class="empty-spaces">
            <p>오프라인 장터가 없습니다.</p>
          </div>
        </div>
      </div>

      <!-- 오프라인 장터 생성 모달 -->
      <el-dialog
        :visible.sync="createOfflineMarketplaceModalVisible"
        width="460px"
        center
        :show-close="false"
        :append-to-body="true"
        :modal-append-to-body="true"
        :close-on-click-modal="false"
        custom-class="create-offline-marketplace-modal"
      >
        <div class="modal-details-content">
          <button class="modal-close-btn" @click="createOfflineMarketplaceModalVisible = false">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>

          <h3 class="modal-main-title">오프라인 장터를 생성합니다.</h3>

          <div class="form-section">
            <div class="form-group-spacing">
              <label class="form-label-large">장터 이름</label>
              <div class="input-wrapper">
                <input
                  v-model="newOfflineMarketplace.name"
                  type="text"
                  placeholder="오프라인 장터 이름"
                  class="space-name-input"
                />
              </div>
            </div>

            <div class="form-group-spacing">
              <label class="form-label-large">설명 (선택)</label>
              <div class="input-wrapper textarea-wrapper">
                <textarea 
                  v-model="newOfflineMarketplace.description" 
                  placeholder="장터에 대한 설명을 입력해주세요."
                  class="space-name-input textarea-input"
                  rows="3"
                ></textarea>
              </div>
            </div>
          </div>

          <button 
            class="create-submit-btn" 
            @click="createOfflineMarketplace"
            :disabled="creatingOfflineMarketplace || !newOfflineMarketplace.name.trim()"
          >
            {{ creatingOfflineMarketplace ? '생성 중...' : '오프라인 장터 생성하기' }}
          </button>
        </div>
      </el-dialog>

      <!-- My Page Section -->
      <div class="mypage-section">
        <h3 class="section-title">마이페이지</h3>
        <div class="mypage-menu">
          <div
            class="mypage-item"
            :class="{ active: $route.name === 'MyMarketplace' }"
            @click="navigateToPage('MyMarketplace')"
          >
            <span class="item-text">내 장터 관리</span>
          </div>
          <div
            class="mypage-item"
            :class="{ active: $route.name === 'MySchedule' }"
            @click="navigateToPage('MySchedule')"
          >
            <span class="item-text">내 일정 관리</span>
          </div>
          <div
            class="mypage-item"
            :class="{ active: selectedSpaceId === 'rpoint-history' }"
            @click="navigateToPage('RPointHistory')"
          >
            <span class="item-text">R포인트 내역</span>
          </div>
          <div
            class="mypage-item"
            :class="{ active: selectedSpaceId === 'activity-list' }"
            @click="navigateToPage('ActivityList')"
          >
            <span class="item-text">활동리스트</span>
          </div>
          <!-- <div
            class="mypage-item"
            :class="{ active: selectedSpaceId === 'community-settings' }"
            @click="navigateToPage('CommunitySettings')"
          >
            <span class="item-text">커뮤니티 설정</span>
          </div> -->
        </div>
      </div>
      <!-- 공간 관리자 메뉴 섹션 (공간생성 또는 오프라인장터등록 권한이 있는 경우에만 표시) -->
      <div v-if="hasManagerPermission || isChannelCreator" class="space-admin-section">
        <h3 class="section-title">매니저 메뉴</h3>
        <div class="space-admin-menu">
          <div
            class="space-admin-item"
            :class="{ active: $route.name === 'SpaceManagement' }"
            @click="navigateToPage('SpaceManagement')"
          >
            <span class="item-text">공간 관리</span>
          </div>
          <div
            class="space-admin-item"
            :class="{ active: $route.name === 'SmsManagement' }"
            @click="navigateToPage('SmsManagement')"
          >
            <span class="item-text">문자 발송</span>
          </div>
        </div>
      </div>

      <!-- 관리자 전용 섹션 (커뮤니티 생성자인 경우에만 표시) -->
      <div v-if="isChannelCreator" class="admin-section">
        <h3 class="section-title">관리자 메뉴</h3>
        <div class="admin-menu">
          <div
            class="admin-item"
            :class="{ active: $route.name === 'MemberManagement' }"
            @click="navigateToPage('MemberManagement')"
          >
            <span class="item-text">회원 관리</span>
          </div>
          <div
            class="admin-item"
            :class="{ active: $route.name === 'CommunityManagement' }"
            @click="navigateToPage('CommunityManagement')"
          >
            <span class="item-text">커뮤니티 관리</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Space Modal Component -->
    <CreateSpaceModal
      :visible.sync="createSpaceModalVisible"
      @space-created="handleCreateSpace"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import CreateSpaceModal from './CreateSpaceModal.vue';
import { getSpacesByChannel, Space } from '@/api/space';
import { getOfflineMarketplaces, createOfflineMarketplace, OfflineMarketplace } from '@/api/marketplace';
import { getChannelDomainDetail } from '@/api/channel';
import { checkPermissionByUser } from '@/api/channelMemberPermission';
import { getUserInfo } from '@/api/user';
import { UserModule } from '@/store/modules/user';
import { ChannelPermissionModule } from '@/store/modules/channelPermission';

@Component({
  name: 'CommunitySidebar',
  components: {
    CreateSpaceModal,
  },
})
export default class extends Vue {
  @Prop({ default: '' }) selectedSpaceId!: string;

  private createSpaceModalVisible = false;
  private spaces: Space[] = [];
  private loadingSpaces = false;

  // 오프라인 장터 관련
  private offlineMarketplaces: OfflineMarketplace[] = [];
  private loadingOfflineMarketplaces = false;
  private createOfflineMarketplaceModalVisible = false;
  private creatingOfflineMarketplace = false;
  private newOfflineMarketplace = {
    name: '',
    description: '',
  };

  // 채널 관리자 여부 (TODO: 실제 권한 체크 API 연동 필요)
  private isChannelAdmin = false;

  // Permission and role flags
  private hasManagerPermission = false;

  private isChannelCreator = false;

  private currentChannelUid = '';

  async mounted() {
    await this.loadSpaces();
    await this.loadOfflineMarketplaces();
    await this.checkChannelAdminPermission();
    await this.checkPermissions();
  }

  @Watch('$route.params.domain')
  private async onDomainChange() {
    await this.loadSpaces();
    await this.loadOfflineMarketplaces();
    await this.checkChannelAdminPermission();
    await this.checkPermissions();
  }

  private async checkChannelAdminPermission() {
    // 퍼블리싱 테스트용으로 관리자 체크 비활성화 - 모두에게 표시
    // this.isChannelAdmin = false;
    // return;
    
    const domain = this.$route.params.domain;
    if (!domain) {
      this.isChannelAdmin = false;
      return;
    }
    try {
      // 채널 정보 조회
      const response = await getChannelDomainDetail(domain as string);
      const channel = response.data;
      
      console.log('Channel info:', channel);
      console.log('Current user ID:', UserModule.userId);
      
      // 현재 사용자가 채널 생성자(관리자)인지 확인
      const currentUserId = UserModule.userId;
      
      // channel.userId (채널 생성자의 userId)와 현재 로그인 사용자의 userId 비교
      if (channel && channel.userId && currentUserId) {
        this.isChannelAdmin = channel.userId === currentUserId;
        console.log('Is channel admin:', this.isChannelAdmin);
      } else {
        this.isChannelAdmin = false;
      }
    } catch (error) {
      console.error('채널 관리자 권한 확인 실패:', error);
      this.isChannelAdmin = false;
    }
    this.isChannelAdmin = true;
  }

  /**
   * Check manager and admin permissions for menu visibility
   */
  private async checkPermissions() {
    // Reset permissions
    this.hasManagerPermission = false;
    this.isChannelCreator = false;
    this.currentChannelUid = '';

    // Only check if user is logged in
    if (!UserModule.isLogin) {
      return;
    }

    const domain = this.$route.params.domain;
    if (!domain) return;

    try {
      // Get channel information
      const channelResponse = await getChannelDomainDetail(domain as string);
      this.currentChannelUid = channelResponse.data.uid;

      // Get current user information
      const userResponse: any = await getUserInfo();
      const currentUserInfo: any = userResponse.data;

      // Check if user is the channel creator (for Admin Menu)
      this.isChannelCreator = channelResponse?.data.userUid === currentUserInfo.uid;

      // ✅ ChannelPermissionModule을 사용해서 권한 로드 및 확인
      await ChannelPermissionModule.loadPermissions(this.currentChannelUid);
      
      // 매니저 권한: SPACE_CREATE 또는 OFFLINE_MARKETPLACE_REGISTER 권한이 있거나 채널 관리자인 경우
      this.hasManagerPermission = 
        ChannelPermissionModule.isChannelAdmin ||
        ChannelPermissionModule.canCreateSpace ||
        ChannelPermissionModule.canRegisterOfflineMarketplace;
      
      console.log('Permission check results (using ChannelPermissionModule):', {
        isChannelAdmin: ChannelPermissionModule.isChannelAdmin,
        canCreateSpace: ChannelPermissionModule.canCreateSpace,
        canRegisterOfflineMarketplace: ChannelPermissionModule.canRegisterOfflineMarketplace,
        hasManagerPermission: this.hasManagerPermission,
      });
    } catch (error) {
      console.error('채널 정보 조회 실패:', error);
    }
  }

  private async loadOfflineMarketplaces() {
    const channelUid = this.$route.params.domain;
    if (!channelUid) return;

    try {
      this.loadingOfflineMarketplaces = true;
      const response = await getOfflineMarketplaces(channelUid as string);
      this.offlineMarketplaces = response.data || [];
    } catch (error) {
      console.error('오프라인 장터 목록 조회 실패:', error);
      this.offlineMarketplaces = [];
    } finally {
      this.loadingOfflineMarketplaces = false;
    }
  }

  private async loadSpaces() {
    let channelUid = '';
    await getChannelDomainDetail(this.$route.params.domain).then((res) => {
      if (res.status === 200) {
        channelUid = res.data.uid;
      }
    });
    if (!channelUid) return;

    try {
      this.loadingSpaces = true;
      const response = await getSpacesByChannel(channelUid as string);
      this.spaces = response.data || [];
    } catch (error) {
      console.error('공간 목록 조회 실패:', error);
      this.$message.error('공간 목록을 불러오지 못했습니다.');
      this.spaces = [];
    } finally {
      this.loadingSpaces = false;
    }
  }

  get isMarketplacePage() {
    return this.$route.name === 'Marketplace' || this.$route.name === 'OfflineMarketplace';
  }

  // ✅ 글쓰기 버튼 표시 여부 (페이지 + 권한 체크)
  get showWriteButton() {
    const routeName = this.$route.name;
    
    // 권한이 아직 로드되지 않았으면 버튼 숨김
    if (!ChannelPermissionModule.loaded) {
      console.log('🔄 권한 로드 중... 버튼 숨김');
      return false;
    }
    
    // 커뮤니티 공간 페이지에서는 POST_USE (게시판 이용) 권한 체크
    if (routeName === 'CommunitySpace') {
      const canCreate = ChannelPermissionModule.canCreatePost;
      console.log('📝 CommunitySpace 게시판 이용 권한:', canCreate);
      return canCreate;
    }
    
    // 장터 페이지에서는 MARKETPLACE_USE 권한 체크
    if (routeName === 'Marketplace') {
      return ChannelPermissionModule.canUseMarketplace;
    }
    
    // 오프라인 장터 페이지에서는 OFFLINE_MARKETPLACE_REGISTER 권한 체크
    if (routeName === 'OfflineMarketplace') {
      return ChannelPermissionModule.canRegisterOfflineMarketplace;
    }
    
    // 강좌 페이지에서는 채널 관리자만 등록 가능 (별도 권한 타입 없음)
    if (routeName === 'Lession' || routeName === 'Video') {
      return ChannelPermissionModule.isChannelAdmin;
    }
    
    // 일정 페이지에서는 SCHEDULE_CREATE 권한 체크
    if (routeName === 'Calendar') {
      return ChannelPermissionModule.canCreateSchedule;
    }
    
    return false;
  }

  private openWriteModal() {
    this.$emit('open-write-modal');
  }

  private openCreateSpaceModal() {
    this.createSpaceModalVisible = true;
  }

  private async handleCreateSpace(space: Space) {
    // Refresh space list after creation
    await this.loadSpaces();
    this.$emit('space-created', space);
  }

  private getSpaceDotClass(spaceType: string): string {
    return spaceType === 'BOARD' ? 'red-dot' : 'orange-dot';
  }

  private getSpaceTypeLabel(spaceType: string): string {
    return spaceType === 'CHAT' ? '채팅' : '게시판';
  }

  private getSpaceTypeBadgeClass(spaceType: string): string {
    return spaceType === 'CHAT' ? 'chat-badge' : 'board-badge';
  }


  private handleMenuSelect(index: string) {
    const space = this.spaces.find(s => s.uid === index);
    if (!space) return;

    // Check access permission
    console.log(space);
    if (!space.isPublic && !space.isMember && !space.isAdmin) {
      this.$message.error('이 공간에 접근할 권한이 없습니다. 초대를 받아야 참여할 수 있습니다.');
      return;
    }

    this.$emit('space-select', index);
    
    // Navigate based on space type
    const routeName = space.spaceType === 'CHAT' ? 'CommunityChat' : 'CommunitySpace';
    
    this.$router.push({
      name: routeName,
      params: {
        domain: this.$route.params.domain || 'default',
        spaceId: space.uid,
      },
      query: {
        spaceName: space.name,
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
  }

  private goToCommunity() {
    this.$router.push({
      name: 'CommunityMain',
      params: {
        domain: this.$route.params.domain || 'default',
      },
    });
  }

  private navigateToPage(pageName: string) {
    this.$router.push({
      name: pageName,
      params: {
        domain: this.$route.params.domain || 'default',
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
  }

  private navigateToMarketplace(storeName: string) {
    this.$router.push({
      name: 'Marketplace',
      params: {
        domain: this.$route.params.domain || 'default',
      },
      query: {
        store: storeName,
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
  }

  // 글 작성하기 버튼 텍스트 (장터 페이지일 때 "상품 등록하기")
  get writeButtonText() {
    return this.isMarketplacePage ? '상품 등록하기' : '글 작성하기';
  }

  // 오프라인 장터로 이동
  private navigateToOfflineMarketplace(marketplaceUid: string) {
    this.$router.push({
      name: 'OfflineMarketplace',
      params: {
        domain: this.$route.params.domain || 'default',
        marketplaceUid,
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
  }

  // 오프라인 장터 생성 모달 열기
  private openCreateOfflineMarketplaceModal() {
    this.newOfflineMarketplace = { name: '', description: '' };
    this.createOfflineMarketplaceModalVisible = true;
  }

  // 오프라인 장터 생성
  private async createOfflineMarketplace() {
    const channelUid = this.$route.params.domain;
    if (!channelUid || !this.newOfflineMarketplace.name.trim()) return;

    try {
      this.creatingOfflineMarketplace = true;
      await createOfflineMarketplace(channelUid as string, {
        name: this.newOfflineMarketplace.name,
        description: this.newOfflineMarketplace.description,
      });
      this.$message.success('오프라인 장터가 생성되었습니다');
      this.createOfflineMarketplaceModalVisible = false;
      await this.loadOfflineMarketplaces();
    } catch (error: any) {
      const message = error.response?.data?.message || '오프라인 장터 생성에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.creatingOfflineMarketplace = false;
    }
  }

  // 관리자 페이지로 이동
  private navigateToAdminPage(pageName: string) {
    this.$router.push({
      name: pageName,
      params: {
        domain: this.$route.params.domain || 'default',
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
  }
}
</script>

<style scoped lang="scss">
.community-sidebar {
  width: 270px;
  background: #FFF;
  border-right: 2px solid #EBEBEB;
  padding: 40px 30px 120px;
  position: fixed;
  left: 0;
  top: 120px;
  height: calc(100vh - 120px);
  overflow-y: auto;
  z-index: 100;
}

.community-sidebar::-webkit-scrollbar{width: 5px;}
.community-sidebar::-webkit-scrollbar-thumb{background-color:#0531CC;border-radius:10px}
.community-sidebar::-webkit-scrollbar-track{background-color:#d2d2d2;border-radius:10px}

@media screen and (max-width:1024px) {
  .community-sidebar {
    width:240px;
    padding: 30px 20px;
  }
}

.sidebar-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
  text-align: left;
  margin-bottom: 100px;
}

.sidebar-title {
  font-size: clamp(20px, 2vw, 24px);
  font-weight: 700;
  line-height: 100%;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  margin: 0;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: #073DFF;
  }
}

.write-post-btn-wrapper {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  position: fixed;
  left: 0;
  bottom: 0;
  width: 260px;
  height: 100px;
  background: #FFF;
  z-index: 999;
}

@media screen and (max-width: 1024px) {
  .write-post-btn-wrapper {
    width: 230px;
    height: 100px;
    left: 5px;
  }
}

.write-post-btn {
  flex:0 1 100%;
  width: 100%;
  max-width: 210px;
	height: 50px;
	border-radius: 10px;
  font-size: 16px;
  color: #FFF;
  font-weight: 600;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  cursor: pointer;
  transition: all 0.2s;
  background: #0531CC;

  &:hover {
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0);
  }

  svg {
    flex-shrink: 0;
  }
}

// Element UI menu customization
.space-menu {
  border: none !important;

  ::v-deep .el-submenu__title {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 0 !important;
    height: auto !important;
    line-height: normal !important;
    background-color: transparent !important;
    border: none !important;
    margin-bottom: 20px;

    &:hover {
      background-color: transparent !important;

      .menu-title {
        opacity: 0.8;
      }

      .plus-icon {
        opacity: 0.8;
      }
    }

    .el-submenu__icon-arrow {
      display: none;
    }
  }

  ::v-deep .el-menu-item {
    padding: 0 !important;
    padding-left: 0 !important;
    height: auto !important;
    line-height: normal !important;
    background-color: transparent !important;
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover {
      background-color: transparent !important;

      .space-name {
        color: #073DFF;
        font-weight: 600;
      }
    }

    &.is-active {
      background-color: transparent !important;

      .space-name {
        color: #222;
        font-weight: 600;
      }
    }
  }
}

.menu-title {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: clamp(20px, 2vw, 24px);
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: opacity 0.2s;
}

.plus-icon {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: scale(1.05);
  }

  &:active {
    transform: scale(0.95);
  }
}

.space-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s;
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

.space-name {
  flex: 1;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  transition: all 0.2s;
}

.space-type-badge {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 11px;
  font-weight: 600;
  line-height: 16px;
  white-space: nowrap;
  margin-left: 6px;

  &.chat-badge {
    background: rgba(255, 173, 58, 0.15);
    color: #FF8C00;
  }

  &.board-badge {
    background: rgba(255, 88, 88, 0.15);
    color: #FF5858;
  }
}

.admin-badge {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 4px;
  background: rgba(7, 61, 255, 0.1);
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 11px;
  font-weight: 600;
  line-height: 16px;
  white-space: nowrap;
  margin-left: 6px;
}


// Marketplace Section
.marketplace-section {
  padding-top: 40px;
  border-top: 1px solid #EBEBEB;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 20px;
  
  .section-title {
    color: #073DFF;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: clamp(20px, 2vw, 24px);
    font-weight: 700;
    line-height: 100%;
    cursor: pointer;
    transition: opacity 0.2s;
    margin: 0;

    &:hover {
      opacity: 0.8;
    }
  }

  .plus-icon {
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      transform: scale(1.05);
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

.marketplace-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.marketplace-item {
  cursor: pointer;
  transition: all 0.2s ease;

  .item-text {
    font-size: 18px;
    color: #6B7280;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-weight: 400;
    line-height: 20px;
    transition: color 0.2s ease;
    position: relative;
    padding-left: 15px;

    &::after {
      display: block;
      content: '';
      width: 8px;
      height: 2px;
      background: #6B7280;
      position: absolute;
      top: 50%;
      left: 0;
      transform: translateY(-50%);
    }
  }



  &:hover {
    .item-text {
      color: #073DFF;
      &::after {
        background: #073DFF;
      }
    }
  }

  &.active {
    .item-text {
      color: #222;
      font-weight: 600;
    }
  }
}

// My Page Section
.mypage-section {
  padding-top: 40px;
  border-top: 1px solid #EBEBEB;
}

.section-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 100%;
  margin: 0 0 30px 0;
}

.mypage-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.mypage-item {
  cursor: pointer;
  transition: all 0.2s ease;

  .item-text {
    color: #6B7280;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 20px;
    transition: color 0.2s ease;
    position: relative;
    padding-left: 15px;
    
    &::after {
      display: block;
      content: '';
      width: 8px;
      height: 2px;
      background: #6B7280;
      position: absolute;
      top: 50%;
      left: 0;
      transform: translateY(-50%);
    }
  }
  

  &:hover {
    .item-text {
      color: #073DFF;

      &::after {
        background: #073DFF;
      }
    }
    
  }

  &.active {
    .item-text {
      color: #222;
      font-weight: 600;
    }
  }
}

// 공간 관리자 메뉴 섹션 (마이페이지와 동일한 스타일)
.space-admin-section {
  padding-top: 40px;
  border-top: 1px solid #EBEBEB;
}

.space-admin-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.space-admin-item {
  cursor: pointer;
  transition: all 0.2s ease;

  .item-text {
    color: #6B7280;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 20px;
    transition: color 0.2s ease;
    position: relative;
    padding-left: 15px;
    
    &::after {
      display: block;
      content: '';
      width: 8px;
      height: 2px;
      background: #6B7280;
      position: absolute;
      top: 50%;
      left: 0;
      transform: translateY(-50%);
    }
  }

  &:hover {
    .item-text {
      color: #073DFF;

      &::after {
        background: #073DFF;
      }
    }
  }

  &.active {
    .item-text {
      color: #222;
      font-weight: 600;
    }
  }
}

// Admin Section (관리자 전용 메뉴)
.admin-section {
  padding-top: 40px;
  border-top: 1px solid #EBEBEB;
}

.admin-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.admin-item {
  cursor: pointer;
  transition: all 0.2s ease;

  .item-text {
    color: #6B7280;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 20px;
    transition: color 0.2s ease;
  }

  &:hover {
    .item-text {
      color: #073DFF;
    }
  }

  &.active {
    .item-text {
      color: #222;
      font-weight: 600;
    }
  }
}

// 오프라인 장터 생성 모달 스타일
::v-deep .create-offline-marketplace-modal {
  border-radius: 12px;

  .el-dialog__header {
    padding: 0;
  }

  .el-dialog__body {
    padding: 40px;
  }
}

.modal-content {
  text-align: center;
}

.modal-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 140%;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.modal-subtitle {
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 140%;
  margin: 0 0 30px 0;
  letter-spacing: -0.3px;
}

.modal-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-btn {
  width: 100%;
  height: 50px;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: -0.5px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &:active {
    transform: translateY(0);
  }
}

.group-chat-btn {
  background: #073DFF;
  color: #FFF;

  &:hover {
    background: #0535e6;
  }
}

.board-btn {
  background: #073DFF;
  color: #FFF;

  &:hover {
    background: #0535e6;
  }
}

// Details Modal Styles
.modal-details-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
  position: relative;
}

.modal-close-btn {
  position: absolute;
  right: 0;
  top: 0;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #444;
  transition: color 0.2s;

  &:hover {
    color: #000;
  }
}

.modal-main-title {
  color: #444;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;
  letter-spacing: -2px;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.form-group-spacing {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-label-large {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
  text-align: left;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 52px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  padding: 0 16px;
  background: #FFF;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #073DFF;
  }

  &.textarea-wrapper {
    height: auto;
    min-height: 100px;
    padding: 12px 16px;
    align-items: flex-start;
  }
}

.textarea-input {
  resize: vertical;
  min-height: 76px;
  line-height: 1.5;
}

.input-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #D9D9D9;
  flex-shrink: 0;
}

.space-name-input {
  flex: 1;
  border: none;
  outline: none;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  background: transparent;

  &::placeholder {
    color: #888;
  }
}

.access-control-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.access-buttons {
  display: flex;
  gap: 20px;
}

.access-btn {
  flex: 1;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #F5F5F5;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
  }

  &.active {
    background: #073DFF;
    border-color: #073DFF;
    color: #FFF;
  }
}

.access-note {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 150%;
  letter-spacing: 0;
  text-align: left;
  margin: 0;
}

.create-submit-btn {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #073DFF;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #0535e6;
    border-color: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    background: #CECECE;
    border-color: #CECECE;
    cursor: not-allowed;
  }
}

// Old form styles (keeping for first modal)
.form-group {
  margin-bottom: 24px;
  text-align: left;
}

.form-label {
  display: block;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 140%;
  margin-bottom: 8px;
  letter-spacing: -0.3px;
}

.space-input {
  width: 100%;

  ::v-deep .el-input__inner {
    height: 44px;
    border: 1px solid #CECECE;
    border-radius: 8px;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    padding: 0 16px;

    &:focus {
      border-color: #073DFF;
    }

    &::placeholder {
      color: #888;
    }
  }
}

.color-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.color-option {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 3px solid transparent;

  &:hover {
    transform: scale(1.1);
  }

  &.active {
    border-color: #000;
    transform: scale(1.15);
  }
}

.cancel-btn {
  background: #F5F5F5;
  color: #666;

  &:hover {
    background: #E0E0E0;
  }
}

.submit-btn {
  background: #073DFF;
  color: #FFF;

  &:hover {
    background: #0535e6;
  }
}

.modal-note {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 400;
  line-height: 140%;
  margin: 16px 0 0 0;
  text-align: left;
  letter-spacing: -0.3px;
}

@media screen and (max-width: 768px) {
  ::v-deep .create-space-modal_details {
    .el-dialog {
      width: 90% !important;
      margin: 0 auto;
    }

    .el-dialog__body {
      padding: 24px;
    }
  }

  .modal-main-title {
    font-size: 22px;
  }

  .form-label_large {
    font-size: 18px;
  }

  .access-buttons {
    gap: 12px;
  }

  .modal-details-content {
    gap: 30px;
  }

  .community-sidebar {
    display: none;
  }
}
</style>
