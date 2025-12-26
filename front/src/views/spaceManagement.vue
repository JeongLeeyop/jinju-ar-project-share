<template>
  <div class="space-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'space-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="space-management-main">
      <div class="page-header">
        <h1 class="page-title">공간 관리</h1>
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
          <h2 class="section-title">
            <span class="space-dot" :class="selectedSpace.spaceType === 'BOARD' ? 'red-dot' : 'orange-dot'"></span>
            {{ selectedSpace.name }} 멤버 관리
          </h2>
          <div class="space-info">
            <span class="info-item">
              <i class="el-icon-user"></i>
              멤버 {{ selectedSpace.memberCount || 0 }}명
            </span>
            <span class="info-item">
              <i class="el-icon-date"></i>
              생성일: {{ formatDate(selectedSpace.createdAt) }}
            </span>
          </div>
        </div>

        <!-- 검색 -->
        <div class="search-section">
          <el-input
            v-model="memberSearchQuery"
            placeholder="멤버 검색 (이름, 이메일)"
            prefix-icon="el-icon-search"
            clearable
            style="max-width: 400px;"
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
                <i class="el-icon-user"></i>
              </div>
              <div class="member-details">
                <h4 class="member-name">{{ member.name }}</h4>
                <p class="member-email">{{ member.email }}</p>
              </div>
            </div>
            <div class="member-actions">
              <el-button
                type="danger"
                size="small"
                icon="el-icon-delete"
                @click="kickMember(member)"
              >
                추방
              </el-button>
            </div>
          </div>

          <div v-if="filteredMembers.length === 0" class="empty-members">
            <i class="el-icon-user"></i>
            <p>{{ memberSearchQuery ? '검색 결과가 없습니다.' : '멤버가 없습니다.' }}</p>
          </div>
        </div>

        <!-- 초대 섹션 -->
        <div class="invite-section">
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
                  <i class="el-icon-user"></i>
                </div>
                <div class="member-details">
                  <h4 class="member-name">{{ member.name }}</h4>
                  <p class="member-email">{{ member.email }}</p>
                </div>
              </div>
            </div>

            <div v-if="filteredInviteMembers.length === 0" class="empty-invite-members">
              <i class="el-icon-user"></i>
              <p>{{ inviteSearchQuery ? '검색 결과가 없습니다.' : '초대 가능한 멤버가 없습니다.' }}</p>
            </div>
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

interface Space {
  uid: string;
  name: string;
  spaceType: string;
  memberCount?: number;
  createdAt?: string;
}

interface Member {
  uid: string;
  name: string;
  email: string;
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

  // Mock 데이터 (실제로는 API 연동 필요)
  private boardSpaces: Space[] = [
    {
      uid: '1',
      name: '자유 게시판',
      spaceType: 'BOARD',
      memberCount: 15,
      createdAt: '2024-01-15',
    },
    /* {
      uid: '2',
      name: '공지사항',
      spaceType: 'BOARD',
      memberCount: 30,
      createdAt: '2024-02-20',
    }, */
  ];

  private chatSpaces: Space[] = [
    {
      uid: '3',
      name: '일반 채팅방',
      spaceType: 'CHAT',
      memberCount: 25,
      createdAt: '2024-01-10',
    },
  ];

  private members: Member[] = [
    {
      uid: '1',
      name: '정이욥',
      email: 'hong@example.com',
    },
    {
      uid: '2',
      name: '오형래',
      email: 'kim@example.com',
    },
    {
      uid: '3',
      name: '배은별',
      email: 'lee@example.com',
    },
  ];

  // 커뮤니티 전체 멤버 목록 (초대 가능한 멤버)
  private communityMembers: Member[] = [
    {
      uid: '4',
      name: '이은경',
      email: 'park@example.com',
    },
    {
      uid: '5',
      name: '이주현',
      email: 'jung@example.com',
    },
    {
      uid: '6',
      name: '정이욥2',
      email: 'choi@example.com',
    },
    {
      uid: '7',
      name: '홍길동',
      email: 'kang@example.com',
    },
    {
      uid: '8',
      name: '홍길동',
      email: 'yoon@example.com',
    },
    {
      uid: '9',
      name: '홍길동',
      email: 'lim@example.com',
    },
    {
      uid: '10',
      name: '홍길동',
      email: 'han@example.com',
    },
  ];

  // 모든 공간 목록 (게시판 + 채팅)
  get allSpaces() {
    return [...this.boardSpaces, ...this.chatSpaces];
  }

  // 현재 공간 멤버 필터링
  get filteredMembers() {
    if (!this.memberSearchQuery) {
      return this.members;
    }
    const query = this.memberSearchQuery.toLowerCase();
    return this.members.filter(
      (member) =>
        member.name.toLowerCase().includes(query) ||
        member.email.toLowerCase().includes(query),
    );
  }

  // 초대 가능한 멤버 목록 (현재 공간 멤버 제외)
  get availableInviteMembers() {
    const currentMemberUids = this.members.map((m) => m.uid);
    return this.communityMembers.filter(
      (member) => !currentMemberUids.includes(member.uid),
    );
  }

  // 초대 가능한 멤버 검색 필터링
  get filteredInviteMembers() {
    if (!this.inviteSearchQuery) {
      return this.availableInviteMembers;
    }
    const query = this.inviteSearchQuery.toLowerCase();
    return this.availableInviteMembers.filter(
      (member) =>
        member.name.toLowerCase().includes(query) ||
        member.email.toLowerCase().includes(query),
    );
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private selectSpace(space: Space) {
    this.selectedSpace = space;
  }

  private formatDate(date?: string) {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('ko-KR');
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
  private inviteSelectedMembers() {
    if (this.selectedInviteMembers.length === 0) return;

    const selectedNames = this.communityMembers
      .filter((m) => this.selectedInviteMembers.includes(m.uid))
      .map((m) => m.name)
      .join(', ');

    this.$message.success(
      `${this.selectedInviteMembers.length}명의 멤버에게 초대를 발송했습니다. (${selectedNames})`,
    );

    // 초대 완료 후 선택 초기화
    this.selectedInviteMembers = [];
    this.inviteSearchQuery = '';

    // 실제로는 API 호출 및 목록 갱신 필요
  }

  private kickMember(member: Member) {
    this.$confirm(`${member.name}님을 추방하시겠습니까?`, '멤버 추방', {
      confirmButtonText: '추방',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      this.$message.success(`${member.name}님을 추방했습니다.`);
      // 실제로는 API 호출 및 목록 갱신 필요
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
  margin-left: 310px;
  padding: 40px 60px;
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
  margin: 0;
}

// 탭 메뉴
.tabs-section {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
  border-bottom: 2px solid #EBEBEB;
}

.tab-btn {
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

  ::v-deep .el-input__inner {
    height: 44px;
    border-radius: 8px;
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
}

.member-details {
  text-align: left;
}

.member-name {
  font-size: 16px;
  font-weight: 600;
  color: #222;
  margin: 0 0 4px 0;
}

.member-email {
  font-size: 14px;
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
  font-size: 18px;
  font-weight: 700;
  color: #222;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: #073DFF;
  }
}

.invite-search {
  margin-bottom: 16px;

  ::v-deep .el-input__inner {
    height: 44px;
    border-radius: 8px;
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

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin: 0;
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
@media screen and (max-width: 768px) {
  .space-management-main {
    margin-left: 0;
    padding: 20px 16px;
  }

  .tabs-section {
    gap: 12px;
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 2px;
  }

  .tab-btn {
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

  .space-info {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .member-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .member-actions {
    width: 100%;

    .el-button {
      width: 100%;
    }
  }

  .invite-header {
    flex-direction: column;
    align-items: stretch;

    .el-button {
      width: 100%;
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
</style>