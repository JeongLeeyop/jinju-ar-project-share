<template>
  <div class="community-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'community-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="community-management-main">
      <div class="page-header">
        <h1 class="page-title">커뮤니티 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'point' }"
          @click="activeTab = 'point'"
        >
          포인트 관리
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'adjust' }"
          @click="activeTab = 'adjust'"
        >
          포인트 차감/지급
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'settings' }"
          @click="activeTab = 'settings'"
        >
          커뮤니티 설정
        </button>
      </div>

      <!-- 포인트 관리 탭 -->
      <div v-if="activeTab === 'point'" class="point-management-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-coin"></i>
            포인트 적립 설정
          </h2>
          <p class="section-description">
            사용자 활동에 따른 포인트 적립 금액을 설정합니다.
          </p>
        </div>

        <!-- 포인트 설정 폼 -->
        <div class="point-settings-form">
          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-edit"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">게시글 작성</h3>
                <p class="setting-description">게시판에 새 글을 작성할 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.postCreate"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-chat-line-square"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">댓글 작성</h3>
                <p class="setting-description">게시글에 댓글을 작성할 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.commentCreate"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">좋아요 누름</h3>
                <p class="setting-description">게시글이나 댓글에 좋아요를 누를 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.likeGive"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-user-solid"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">출석 체크</h3>
                <p class="setting-description">매일 첫 로그인 시 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.dailyAttendance"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-shopping-cart-2"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">장터 상품 등록</h3>
                <p class="setting-description">장터에 상품을 등록할 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.marketplaceCreate"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-sold-out"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">장터 상품 판매</h3>
                <p class="setting-description">장터에서 상품을 판매했을 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.marketplaceSell"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-reading"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">강좌 수강 완료</h3>
                <p class="setting-description">강좌를 완료했을 때 적립되는 포인트</p>
              </div>
            </div>
            <div class="setting-input">
              <el-input-number
                v-model="pointSettings.courseComplete"
                :min="0"
                :max="10000"
                :step="10"
                controls-position="right"
              />
              <span class="unit">P</span>
            </div>
          </div>
        </div>

        <!-- 저장 버튼 -->
        <div class="save-section">
          <el-button
            type="primary"
            size="large"
            icon="el-icon-check"
            @click="savePointSettings"
            :loading="saving"
          >
            설정 저장
          </el-button>
          <el-button
            size="large"
            icon="el-icon-refresh"
            @click="resetPointSettings"
          >
            기본값으로 초기화
          </el-button>
        </div>
      </div>

      <!-- 포인트 차감/지급 탭 -->
      <div v-if="activeTab === 'adjust'" class="point-adjust-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-wallet"></i>
            포인트 차감/지급
          </h2>
          <p class="section-description">
            회원을 선택하여 포인트를 차감하거나 지급할 수 있습니다.
          </p>
        </div>

        <!-- 회원 검색 -->
        <div class="member-search-section">
          <el-input
            v-model="memberSearchQuery"
            placeholder="회원 검색 (이름, 이메일, 아이디)"
            prefix-icon="el-icon-search"
            clearable
            style="max-width: 500px;"
          />
        </div>

        <!-- 회원 목록 -->
        <div class="member-list-section">
          <div class="member-list-header">
            <span class="header-item name">회원 정보</span>
            <span class="header-item point">보유 포인트</span>
            <span class="header-item action">액션</span>
          </div>

          <div class="member-list-body">
            <div
              v-for="member in filteredMembers"
              :key="member.uid"
              class="member-row"
            >
              <div class="member-info-cell">
                <div class="member-avatar">
                  <i class="el-icon-user"></i>
                </div>
                <div class="member-details">
                  <h4 class="member-name">{{ member.name }}</h4>
                  <p class="member-email">{{ member.email }}</p>
                </div>
              </div>

              <div class="member-point-cell">
                <span class="point-amount">{{ member.point.toLocaleString() }}</span>
                <span class="point-unit">P</span>
              </div>

              <div class="member-action-cell">
                <el-button
                  type="success"
                  size="small"
                  icon="el-icon-plus"
                  @click="openPointModal('add', member)"
                >
                  지급
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  icon="el-icon-minus"
                  @click="openPointModal('subtract', member)"
                >
                  차감
                </el-button>
              </div>
            </div>

            <div v-if="filteredMembers.length === 0" class="empty-members">
              <i class="el-icon-user"></i>
              <p>{{ memberSearchQuery ? '검색 결과가 없습니다.' : '회원이 없습니다.' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 커뮤니티 설정 탭 (추후 구현) -->
      <div v-if="activeTab === 'settings'" class="settings-section">
        <div class="empty-container">
          <i class="el-icon-setting"></i>
          <p>커뮤니티 설정 기능은 준비 중입니다.</p>
        </div>
      </div>
    </div>

    <!-- 포인트 조정 모달 -->
    <el-dialog
      :visible.sync="pointModalVisible"
      width="500px"
      :show-close="false"
      custom-class="point-adjust-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="pointModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">
          {{ pointModalType === 'add' ? '포인트 지급' : '포인트 차감' }}
        </h2>

        <div class="modal-member-info">
          <div class="member-avatar">
            <i class="el-icon-user"></i>
          </div>
          <div class="member-details">
            <h4 class="member-name">{{ selectedMember?.name }}</h4>
            <p class="member-point">
              현재 포인트: <strong>{{ selectedMember?.point.toLocaleString() }}P</strong>
            </p>
          </div>
        </div>

        <div class="modal-form">
          <div class="form-group">
            <label class="form-label">
              {{ pointModalType === 'add' ? '지급할' : '차감할' }} 포인트
            </label>
            <el-input-number
              v-model="pointAmount"
              :min="1"
              :max="pointModalType === 'subtract' ? selectedMember?.point : 999999"
              :step="100"
              controls-position="right"
              style="width: 100%;"
            />
          </div>

          <div class="form-group">
            <label class="form-label">사유 (선택)</label>
            <el-input
              v-model="pointReason"
              type="textarea"
              :rows="3"
              placeholder="포인트 조정 사유를 입력하세요."
            />
          </div>
        </div>

        <div class="modal-actions">
          <el-button @click="pointModalVisible = false">
            취소
          </el-button>
          <el-button
            :type="pointModalType === 'add' ? 'success' : 'warning'"
            @click="adjustPoint"
            :loading="adjusting"
          >
            {{ pointModalType === 'add' ? '지급하기' : '차감하기' }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

interface PointSettings {
  postCreate: number;
  commentCreate: number;
  likeGive: number;
  dailyAttendance: number;
  marketplaceCreate: number;
  marketplaceSell: number;
  courseComplete: number;
}

interface Member {
  uid: string;
  name: string;
  email: string;
  point: number;
}

@Component({
  name: 'CommunityManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeTab = 'point';
  private saving = false;

  // 포인트 조정 관련
  private memberSearchQuery = '';
  private pointModalVisible = false;
  private pointModalType: 'add' | 'subtract' = 'add';
  private selectedMember: Member | null = null;
  private pointAmount = 100;
  private pointReason = '';
  private adjusting = false;

  // 포인트 설정 (기본값)
  private pointSettings: PointSettings = {
    postCreate: 100,
    commentCreate: 50,
    likeGive: 10,
    dailyAttendance: 50,
    marketplaceCreate: 100,
    marketplaceSell: 200,
    courseComplete: 150,
  };

  // 기본값 백업
  private defaultPointSettings: PointSettings = {
    postCreate: 100,
    commentCreate: 50,
    likeGive: 10,
    dailyAttendance: 50,
    marketplaceCreate: 100,
    marketplaceSell: 200,
    courseComplete: 150,
  };

  // Mock 회원 데이터
  private members: Member[] = [
    {
      uid: '1',
      name: '정이욥',
      email: 'hong@example.com',
      point: 1500,
    },
    {
      uid: '2',
      name: '오형래',
      email: 'kim@example.com',
      point: 2300,
    },
    {
      uid: '3',
      name: '배은별',
      email: 'lee@example.com',
      point: 890,
    },
    {
      uid: '4',
      name: '이은경',
      email: 'park@example.com',
      point: 4200,
    },
    {
      uid: '5',
      name: '이주현',
      email: 'jung@example.com',
      point: 650,
    },
    {
      uid: '6',
      name: '정이욥2',
      email: 'choi@example.com',
      point: 3100,
    },
  ];

  // 회원 검색 필터링
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

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  // 포인트 설정 저장
  private savePointSettings() {
    this.saving = true;

    // 실제로는 API 호출
    setTimeout(() => {
      this.$message.success('포인트 설정이 저장되었습니다.');
      this.saving = false;
    }, 1000);
  }

  // 기본값으로 초기화
  private resetPointSettings() {
    this.$confirm(
      '포인트 설정을 기본값으로 초기화하시겠습니까?',
      '설정 초기화',
      {
        confirmButtonText: '초기화',
        cancelButtonText: '취소',
        type: 'warning',
      },
    ).then(() => {
      this.pointSettings = { ...this.defaultPointSettings };
      this.$message.success('포인트 설정이 기본값으로 초기화되었습니다.');
    }).catch(() => {
      // 취소
    });
  }

  // 포인트 조정 모달 열기
  private openPointModal(type: 'add' | 'subtract', member: Member) {
    this.pointModalType = type;
    this.selectedMember = member;
    this.pointAmount = 100;
    this.pointReason = '';
    this.pointModalVisible = true;
  }

  // 포인트 조정 실행
  private adjustPoint() {
    if (!this.selectedMember || this.pointAmount <= 0) {
      this.$message.warning('올바른 포인트를 입력해주세요.');
      return;
    }

    this.adjusting = true;

    // 실제로는 API 호출
    setTimeout(() => {
      const member = this.members.find((m) => m.uid === this.selectedMember!.uid);
      if (member) {
        if (this.pointModalType === 'add') {
          member.point += this.pointAmount;
          this.$message.success(
            `${member.name}님에게 ${this.pointAmount.toLocaleString()}P를 지급했습니다.`,
          );
        } else {
          member.point -= this.pointAmount;
          this.$message.success(
            `${member.name}님으로부터 ${this.pointAmount.toLocaleString()}P를 차감했습니다.`,
          );
        }
      }

      this.adjusting = false;
      this.pointModalVisible = false;
      this.selectedMember = null;
    }, 1000);
  }
}
</script>

<style scoped lang="scss">
.community-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.community-management-main {
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

// 포인트 관리 섹션
.point-management-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.section-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #EBEBEB;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    color: #073DFF;
    font-size: 28px;
  }
}

.section-description {
  font-size: 14px;
  color: #666;
  margin: 0;
}

// 포인트 설정 폼
.point-settings-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.point-setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #F8F9FA;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background: #F0F2FF;
    border-color: #073DFF;
  }
}

.setting-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.setting-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #E0E7FF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #073DFF;
  font-size: 24px;
  flex-shrink: 0;
}

.setting-details {
  flex: 1;
}

.setting-title {
  font-size: 16px;
  font-weight: 600;
  color: #222;
  margin: 0 0 4px 0;
}

.setting-description {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.setting-input {
  display: flex;
  align-items: center;
  gap: 8px;

  ::v-deep .el-input-number {
    width: 150px;

    .el-input__inner {
      height: 44px;
      border-radius: 8px;
      text-align: left;
      padding-left: 15px;
      padding-right: 50px;
    }
  }

  .unit {
    font-size: 16px;
    font-weight: 600;
    color: #073DFF;
  }
}

// 저장 섹션
.save-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 30px;
  border-top: 1px solid #EBEBEB;

  ::v-deep .el-button {
    min-width: 180px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
  }
}

// 포인트 차감/지급 섹션
.point-adjust-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.member-search-section {
  margin-bottom: 30px;

  ::v-deep .el-input__inner {
    height: 44px;
    border-radius: 8px;
  }
}

.member-list-section {
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  overflow: hidden;
}

.member-list-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  padding: 16px 20px;
  background: #F8F9FA;
  border-bottom: 1px solid #EBEBEB;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.header-item {
  &.name {
    text-align: left;
  }

  &.point {
    text-align: center;
  }

  &.action {
    text-align: right;
  }
}

.member-list-body {
  max-height: 600px;
  overflow-y: auto;
}

.member-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #EBEBEB;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #F8F9FA;
  }
}

.member-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;

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
    flex-shrink: 0;
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
}

.member-point-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  .point-amount {
    font-size: 20px;
    font-weight: 700;
    color: #073DFF;
  }

  .point-unit {
    font-size: 14px;
    font-weight: 600;
    color: #073DFF;
  }
}

.member-action-cell {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.empty-members {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 포인트 조정 모달
::v-deep .point-adjust-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
  }
}

.modal-content {
  position: relative;
}

.modal-close-btn {
  position: absolute;
  top: 0;
  right: 0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #666;

  &:hover {
    color: #333;
  }
}

.modal-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin: 0 0 24px 0;
}

.modal-member-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 8px;
  margin-bottom: 24px;

  .member-avatar {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #E0E7FF;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #073DFF;
    font-size: 28px;
  }

  .member-details {
    text-align: left;
  }

  .member-name {
    font-size: 18px;
    font-weight: 600;
    color: #222;
    margin: 0 0 6px 0;
  }

  .member-point {
    font-size: 14px;
    color: #666;
    margin: 0;

    strong {
      color: #073DFF;
      font-weight: 700;
    }
  }
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #222;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  ::v-deep .el-button {
    min-width: 100px;
    height: 44px;
  }
}

// 설정 섹션 (준비중)
.settings-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

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

// 모바일 반응형
@media screen and (max-width: 768px) {
  .community-management-main {
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

  .point-management-section {
    padding: 24px 16px;
  }

  .point-setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .setting-info {
    width: 100%;
  }

  .setting-input {
    width: 100%;
    justify-content: space-between;

    ::v-deep .el-input-number {
      flex: 1;
      max-width: 200px;
    }
  }

  .save-section {
    flex-direction: column;

    ::v-deep .el-button {
      width: 100%;
    }
  }

  .member-list-header {
    display: none;
  }

  .member-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .member-point-cell {
    justify-content: flex-start;
    padding-left: 60px;
  }

  .member-action-cell {
    justify-content: flex-start;
    padding-left: 60px;

    .el-button {
      flex: 1;
    }
  }

  ::v-deep .point-adjust-modal {
    .el-dialog {
      width: 95% !important;
    }

    .el-dialog__body {
      padding: 24px;
    }
  }

  .modal-actions {
    flex-direction: column-reverse;

    ::v-deep .el-button {
      width: 100%;
    }
  }
}
</style>
