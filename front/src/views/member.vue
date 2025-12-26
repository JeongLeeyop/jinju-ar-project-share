<template>
  <div class="member-page">
    <!-- CommunitySidebar -->
    <CommunitySidebar :selectedSpaceId="'member'" @open-write-modal="handleWriteModal" />

    <!-- Main Content Area -->
    <div class="member-main">
      <!-- Header Stats -->
      <div class="member-header">
        <div class="stats-card">
          <div class="stats-container">
            <div class="stat-item">
              <span class="stat-label">회원 수</span>
              <div class="stat-badge">
                <span class="stat-value">{{ totalCount }}명</span>
              </div>
            </div>
            <!-- <div class="stat-item desktop-only">
              <span class="stat-label">실시간 접속자 수</span>
              <div class="stat-badge">
                <span class="stat-value">{{ onlineCount }}명</span>
              </div>
            </div> -->
          </div>
        </div>
      </div>

      <!-- Search Box -->
      <div class="search-box">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12.9373 7.87402C12.9372 6.53146 12.4034 5.24406 11.4541 4.29468C10.5047 3.34524 9.21674 2.81152 7.87402 2.81152C6.53138 2.81158 5.24407 3.34528 4.29468 4.29468C3.34529 5.24407 2.81158 6.53139 2.81152 7.87402C2.81152 9.21674 3.34524 10.5047 4.29468 11.4541C5.24406 12.4034 6.53146 12.9372 7.87402 12.9373C9.21674 12.9373 10.5047 12.4035 11.4541 11.4541C12.4035 10.5047 12.9373 9.21674 12.9373 7.87402ZM14.0623 7.87402C14.0623 9.32652 13.5501 10.7256 12.6282 11.8328L16.1475 15.3521C16.367 15.5717 16.3671 15.9278 16.1475 16.1475C15.9278 16.3671 15.5717 16.367 15.3521 16.1475L11.8328 12.6282C10.7256 13.5501 9.32652 14.0623 7.87402 14.0623C6.23302 14.0622 4.65891 13.4099 3.49854 12.2495C2.33825 11.0891 1.68652 9.51501 1.68652 7.87402C1.68658 6.23302 2.33816 4.65891 3.49854 3.49854C4.65891 2.33816 6.23302 1.68658 7.87402 1.68652C9.51501 1.68652 11.0891 2.33825 12.2495 3.49854C13.4099 4.65891 14.0622 6.23302 14.0623 7.87402Z" fill="#888888"/>
        </svg>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="회원명을 검색해보세요!"
          class="search-input"
          @input="handleSearch"
        />
      </div>

      <!-- Members Grid -->
      <div class="members-container">
        <div
          v-for="member in filteredMembers"
          :key="member.uid"
          class="member-card"
        >
          <!-- Status Badge (Desktop only) -->
          <!-- <div class="status-badge desktop-only">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="6" cy="6" r="6" :fill="member.online ? '#34C759' : '#FF5858'"/>
            </svg>
            <span class="status-text">{{ member.online ? '온라인' : '오프라인' }}</span>
          </div> -->

          <!-- Member Info -->
          <div class="member-info">
            <div class="member-profile">
              <div class="member-avatar">
                <svg width="42" height="42" viewBox="0 0 42 42" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="21" cy="21" r="21" fill="#D9D9D9"/>
                  <mask id="mask0_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="42" height="42">
                    <circle cx="21" cy="21" r="21" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_member)">
                    <rect x="4.66666" y="24.5" width="32.6667" height="37.3333" rx="14" fill="#F5F5F5"/>
                    <circle cx="21" cy="12.8333" r="8.16667" fill="#F5F5F5"/>
                  </g>
                </svg>
              </div>
              <h3 class="member-name">{{ member.user?.actualName || member.name }} 님</h3>
            </div>

            <p class="member-email">{{ member.user?.email || member.email }}</p>

            <div class="member-meta">
              <div class="meta-item">
                <span class="meta-label">가입일자</span>
              </div>
              <div class="meta-divider"></div>
              <div class="meta-item">
                <span class="meta-value">{{ member.createDate | formatJoinDate }}</span>
              </div>
            </div>
          </div>

          <!-- Invite Button -->
          <button class="invite-button" @click="handleInvite(member)">
            초대하기
          </button>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="filteredMembers.length === 0" class="empty-state">
        <p>회원 목록을 찾을 수 없습니다.</p>
      </div>
    </div>

    <!-- Floating Write Button (Mobile) -->
    <button class="floating-write-btn" @click="handleWriteModal">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 5V19M19 12H5" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <!-- Invite Modal -->
    <el-dialog
      :visible.sync="inviteModalVisible"
      width="346px"
      custom-class="invite-modal"
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
    >
      <div class="invite-modal-content">
        <button class="modal-close-btn" @click="inviteModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">초대 가능한 커뮤니티 공간</h2>

        <div class="communities-list">
          <div
            v-for="community in availableCommunities"
            :key="community.id"
            class="community-item"
            :class="{ selected: selectedCommunity === community.id }"
            @click="selectCommunity(community.id)"
          >
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="8" cy="8" r="8" :fill="community.color"/>
            </svg>
            <span class="community-name">{{ community.name }}</span>
          </div>
        </div>

        <button
          class="invite-submit-button"
          :class="{ disabled: !selectedCommunity }"
          :disabled="!selectedCommunity"
          @click="confirmInvite"
        >
          커뮤니티 공간 초대하기
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getChannelMemberList, getUserCount } from '@/api/channelMember';
import { ChannelModule } from '@/store/modules/channel';
import moment from 'moment';

@Component({
  name: 'Member',
  components: {
    CommunitySidebar,
  },
  filters: {
    formatJoinDate(date: string) {
      return moment(date).format('YYYY년 MM월 DD일');
    },
  },
})
export default class extends Vue {
  private searchQuery = '';
  private totalCount = 1250;
  private onlineCount = 250;
  private channelMembers: any[] = [];
  private inviteModalVisible = false;
  private selectedMember: any = null;
  private selectedCommunity: string | null = null;

  private availableCommunities = [
    {
      id: 'hiking',
      name: '산청등산모임',
      color: '#FF5858',
    },
    {
      id: 'medical',
      name: '산청의료협동조합',
      color: '#FFAD3A',
    },
  ];

  private listQuery: any = {
    channelUid: ChannelModule.selectedChannel.uid,
    searchType: 'name',
    searchValue: '',
    isOnline: false,
    page: 1,
    size: 100,
  };

  get filteredMembers() {
    if (!this.searchQuery.trim()) {
      return this.channelMembers;
    }
    
    const query = this.searchQuery.toLowerCase();
    return this.channelMembers.filter((member) => {
      const name = (member.user?.actualName || member.name || '').toLowerCase();
      const email = (member.user?.email || member.email || '').toLowerCase();
      return name.includes(query) || email.includes(query);
    });
  }

  mounted() {
    this.fetchMemberList();
  }

  private async fetchMemberList() {
    try {
      const res = await getChannelMemberList(this.listQuery);
      this.channelMembers = res.data.content || [];
      
      // If no data, use dummy data
      if (this.channelMembers.length === 0) {
        this.channelMembers = this.getDummyMembers();
      }
      
      const countRes = await getUserCount(this.listQuery);
      this.totalCount = countRes.data.totalCount || this.channelMembers.length;
      this.onlineCount = countRes.data.onlineCount || this.channelMembers.filter(m => m.online).length;
    } catch (error) {
      console.error('Error fetching member list:', error);
      this.channelMembers = this.getDummyMembers();
      this.totalCount = 1250;
      this.onlineCount = 250;
    }
  }

  private getDummyMembers() {
    return [
      {
        uid: '1',
        user: {
          actualName: '오형래',
          email: 'ohr4671@naver.com',
        },
        createDate: '2025-09-23',
        online: false,
        introduce: '안녕하세요, 산청의료협동조합 회원입니다.',
      },
      {
        uid: '2',
        user: {
          actualName: '오형래',
          email: 'ohr4671@naver.com',
        },
        createDate: '2025-09-23',
        online: true,
        introduce: '등산을 좋아하는 산청 주민입니다.',
      },
    ];
  }

  private handleWriteModal() {
    this.$message.info('글 작성 기능은 준비 중입니다.');
  }

  private handleSearch() {
    // Search is handled by computed property
  }

  private handleInvite(member: any) {
    this.selectedMember = member;
    this.selectedCommunity = null;
    this.inviteModalVisible = true;
  }

  private selectCommunity(communityId: string) {
    this.selectedCommunity = communityId;
  }

  private confirmInvite() {
    if (!this.selectedCommunity || !this.selectedMember) {
      return;
    }

    const community = this.availableCommunities.find(c => c.id === this.selectedCommunity);
    this.$message.success(`${this.selectedMember.user?.actualName || this.selectedMember.name}님을 ${community?.name}에 초대했습니다!`);
    
    this.inviteModalVisible = false;
    this.selectedCommunity = null;
    this.selectedMember = null;
  }
}
</script>

<style scoped lang="scss">
.member-page {
  display: flex;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.member-main {
  margin-left: 267px;
  padding: 50px 80px;
  flex: 1;
  min-height: calc(100vh - 124px);
}

.member-header {
  margin-bottom: 40px;
}

.stats-card {
  min-width: 200px;
  display: flex;
  justify-content: center;
  padding: 20px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;
  width: fit-content;
}

.stats-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 80px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.stat-label {
  color: #222;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 100%;
}

.stat-badge {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px 12px;
  border-radius: 38px;
  border: 1px solid #CECECE;
  background: #FFF;
}

.stat-value {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 100%;
}

.search-box {
  display: flex;
  height: 48px;
  align-items: center;
  gap: 20px;
  padding: 8px 20px;
  margin-bottom: 32px;
  border-radius: 10px;
  border: 1px solid #EBEBEB;
  background: #F8F9FB;
  transition: all 0.3s ease;

  &:focus-within {
    background: #FFF;
    border-color: #073DFF;
  }

  svg {
    flex-shrink: 0;
  }
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
  background: transparent;

  &::placeholder {
    color: #888;
  }
}

.members-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(363px, 1fr));
  gap: 32px;
}

.member-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  border-radius: 20px;
  border: 2px solid #EBEBEB;
  background: #FFF;
  transition: all 0.3s ease;

  &:hover {
    border-color: #D0D0D0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  }
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  align-self: flex-start;

  svg {
    flex-shrink: 0;
  }
}

.status-text {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 20px;
}

.member-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.member-profile {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  overflow: hidden;

  svg {
    display: block;
    width: 100%;
    height: 100%;
  }
}

.member-name {
  flex: 1;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 32px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.member-email {
  color: #6B7280;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  margin: 0;
}

.member-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.meta-label,
.meta-value {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta-divider {
  width: 1px;
  height: 14px;
  background: #D9D9D9;
}

.invite-button {
  display: flex;
  height: 54px;
  justify-content: center;
  align-items: center;
  align-self: stretch;
  border-radius: 10px;
  border: 1px solid #073DFF;
  background: transparent;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #073DFF;
    color: #FFF;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.2);
  }

  &:active {
    transform: translateY(0);
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
}

/* Floating Write Button */
.floating-write-btn {
  display: none;
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #073DFF;
  border: none;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.4);
  cursor: pointer;
  z-index: 1000;
  justify-content: center;
  align-items: center;
  transition: all 0.3s ease;

  &:hover {
    background: #0530CC;
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.5);
  }

  &:active {
    transform: scale(0.95);
  }

  svg {
    width: 24px;
    height: 24px;
  }
}

.desktop-only {
  display: flex;
}

/* Modal Styles */
::v-deep .invite-modal {
  border-radius: 10px;

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 64px 40px 40px;
    position: relative;
  }
}

::v-deep .el-dialog__wrapper {
  z-index: 9999 !important;
}

.invite-modal-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 20px;
  position: relative;
  width: 100%;
}

.modal-close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #888;
  transition: all 0.2s ease;

  &:hover {
    color: #000;
    transform: rotate(90deg);
  }
}

.modal-title {
  color: #444;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 150%;
  margin: 0 auto 2px;
  word-break: keep-all;
  align-self: center;
}

.communities-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}

.community-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #FFF;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: #B0B0B0;
    background: #FAFAFA;
  }

  &.selected {
    border-color: #073DFF;
    background: rgba(7, 61, 255, 0.02);
  }

  svg {
    flex-shrink: 0;
  }
}

.community-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
}

.invite-submit-button {
  display: flex;
  height: 54px;
  width: 100%;
  padding: 12px 0;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #FFF;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover:not(.disabled) {
    background: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.35);
  }

  &:active:not(.disabled) {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }

  &.disabled {
    background: #D2D2D2;
    border-color: #D2D2D2;
    color: #FFF;
    cursor: not-allowed;
  }
}

/* Responsive Design */
@media screen and (max-width: 1600px) {
  .member-main {
    padding: 50px 60px;
  }

  .members-container {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}

@media screen and (max-width: 1366px) {
  .member-main {
    padding: 48px 40px;
  }
}

@media screen and (max-width: 1024px) {
  .member-main {
    margin-left: 0;
    padding: 40px 20px;
  }

  .stats-card {
    width: 100%;
  }

  .stats-container {
    width: 100%;
    justify-content: space-around;
  }

  .search-box {
    width: 100%;
  }

  .members-container {
    grid-template-columns: 1fr;
  }

  .floating-write-btn {
    display: flex;
  }
}

@media screen and (max-width: 768px) {
  .community-sidebar {
    display: none;
  }

  .member-main {
    margin-left: 0;
    padding: 40px 20px;
  }

  .member-header {
    margin-bottom: 32px;
  }

  .stats-card {
    padding: 20px;
    width: 100%;
    justify-content: center;
  }

  .stats-container {
    gap: 0;
    padding: 0 40px;
    justify-content: center;
    width: 100%;
  }

  .desktop-only {
    display: none !important;
  }

  .search-box {
    height: 48px;
    gap: 20px;
    padding: 8px 20px;
    margin-bottom: 32px;
  }

  .search-input {
    font-size: 16px;
  }

  .members-container {
    gap: 16px;
  }

  .member-card {
    padding: 20px;
    gap: 16px;
  }

  .member-profile {
    gap: 16px;
  }

  .member-avatar {
    width: 42px;
    height: 42px;
  }

  .member-name {
    font-size: 18px;
    font-weight: 700;
  }

  .member-email {
    font-size: 12px;
    text-align: left;
  }

  .member-meta {
    gap: 8px;
    justify-content: left;
  }

  .meta-label,
  .meta-value {
    font-size: 12px;
  }

  .invite-button {
    height: 54px;
    font-size: 16px;
  }

  /* Modal styles for mobile */
  ::v-deep .invite-modal {
    width: 100% !important;
    height: 100vh !important;
    margin: 0 !important;
    
    .el-dialog {
      width: 100% !important;
      max-width: 100vw !important;
      margin: 0 !important;
      border-radius: 0 !important;
      height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .el-dialog__body {
      padding: 64px 40px 40px;
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      overflow-y: auto;
    }
  }

  .modal-title {
    font-size: 26px;
  }

  .invite-modal-content {
    gap: 20px;
  }

  .communities-list {
    gap: 8px;
  }

  .community-item {
    padding: 12px;
  }

  .community-name {
    font-size: 18px;
  }

  .invite-submit-button {
    height: 54px;
    font-size: 18px;
  }

  .floating-write-btn {
    bottom: 20px;
    right: 20px;
  }
}

/* Smooth transitions */
.member-card {
  animation: fadeIn 0.4s ease-out backwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.member-card:nth-child(1) { animation-delay: 0.05s; }
.member-card:nth-child(2) { animation-delay: 0.1s; }
.member-card:nth-child(3) { animation-delay: 0.15s; }
.member-card:nth-child(4) { animation-delay: 0.2s; }
.member-card:nth-child(5) { animation-delay: 0.25s; }
.member-card:nth-child(6) { animation-delay: 0.3s; }
</style>
