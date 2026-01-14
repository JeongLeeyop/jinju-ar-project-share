<template>
  <div class="rpoint-history-page" v-loading.fullscreen.lock="loading">
    <!-- CommunitySidebar -->
    <CommunitySidebar :selectedSpaceId="'rpoint-history'" />

    <!-- Main Content Area -->
    <div class="rpoint-main">
      <!-- Profile Card -->
      <div class="profile-card">
        <div class="profile-avatar">
          <img v-if="userIconFileUid" :src="`${apiUrl}/attached-file/${userIconFileUid}`" alt="프로필 이미지" class="profile-avatar-img">
          <svg v-else width="100" height="100" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="50" cy="50" r="50" fill="#D9D9D9" />
            <mask id="mask0_profile" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="100"
              height="100">
              <circle cx="50" cy="50" r="50" fill="#D9D9D9" />
            </mask>
            <g mask="url(#mask0_profile)">
              <rect x="11.1111" y="58.3333" width="77.7778" height="88.8889" rx="14" fill="#F5F5F5" />
              <circle cx="50" cy="30.5556" r="19.4444" fill="#F5F5F5" />
            </g>
          </svg>
        </div>

        <div class="profile-info">
          <div class="profile-name-section">
            <h2 class="profile-name">{{ userName }} 님</h2>
            <button class="edit-profile-btn" @click="handleEditProfile">프로필 수정</button>
          </div>

          <div class="points-section">
            <span class="points-label">보유 R포인트</span>
            <span class="points-value">{{ totalPoints.toLocaleString() }}</span>
          </div>
        </div>
      </div>

      <!-- R-Point History Section -->
      <div class="history-section">
        <h2 class="section-title">R포인트 내역</h2>

        <!-- Filters -->
        <div class="filters-container">
          <div class="period-buttons">
            <button v-for="period in periods" :key="period.value" class="period-btn"
              :class="{ active: selectedPeriod === period.value }" @click="handlePeriodChange(period.value)">
              {{ period.label }}
            </button>
          </div>

          <div class="date-filters">
            <div class="date-range-picker">
              <div class="date-input-wrapper">
                <input v-model="startDate" type="text" placeholder="2025.01.01" class="date-input" readonly />
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16.25 6.875L10 13.125L3.75 6.875" stroke="black" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
              </div>

              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 10C4 10 6.58778 6.62471 10 10C13.4122 13.3753 16 10 16 10" stroke="#CECECE"
                  stroke-width="1.5" stroke-linecap="round" />
              </svg>

              <div class="date-input-wrapper">
                <input v-model="endDate" type="text" placeholder="2025.02.01" class="date-input" readonly />
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M16.25 6.875L10 13.125L3.75 6.875" stroke="black" stroke-width="1.5" stroke-linecap="round"
                    stroke-linejoin="round" />
                </svg>
              </div>
            </div>

            <button class="search-btn" @click="searchHistory">조회</button>
          </div>
        </div>

        <!-- History List -->
        <div class="history-list">
          <div v-for="(item, index) in filteredHistory" :key="index" class="history-item"
            :class="{ first: index === 0 }">
            <div class="history-date">{{ item.date }}</div>
            <div class="history-description">{{ item.description }}</div>
            <div class="history-points">
              <span class="points-amount">{{ item.amount > 0 ? '+' : '' }}{{ item.amount.toLocaleString() }}</span>
              <span class="points-type" :class="item.type">{{ item.type === 'earn' ? '적립' : '차감' }}</span>
            </div>
          </div>

          <div v-if="filteredHistory.length === 0" class="empty-state">
            <p>R포인트 내역이 없습니다.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- User Info Modal -->
    <UserInfo :userModalVisible="userModalVisible" @close="userModalVisible = false;" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import { getCurrentPoint, getPointHistory, PointHistory } from '@/api/point';
import { getUserInfo } from '@/api/user';
import CommunitySidebar from './components/communitySidebar.vue';
import UserInfo from './components/UserInfo.vue';

interface HistoryItem {
  date: string;
  description: string;
  amount: number;
  type: 'earn' | 'spend';
}

@Component({
  name: 'RPointHistory',
  components: {
    CommunitySidebar,
    UserInfo,
  },
})
export default class extends Vue {
  private totalPoints = 0;
  private selectedPeriod = '1month';
  private startDate = '';
  private endDate = '';
  private userModalVisible = false;
  private loading = false;
  private channelUid = ChannelModule.selectedChannel?.uid || 'default';
  private page = 0;
  private size = 100;
  private userIconFileUid = '';

  private periods = [
    { label: '1개월', value: '1month' },
    { label: '3개월', value: '3months' },
    { label: '6개월', value: '6months' },
  ];

  private historyData: HistoryItem[] = [];
  private backendHistory: PointHistory[] = [];

  get apiUrl() {
    return '/api';
  }

  get userName() {
    return UserModule.actualName || '사용자';
  }

  async mounted() {
    this.initializeDates();
    await this.loadUserInfo();
    await this.loadPointData();
  }

  // 사용자 정보 로드
  private async loadUserInfo() {
    try {
      const response = await getUserInfo();
      if (response.data && response.data.iconFileUid) {
        this.userIconFileUid = response.data.iconFileUid;
      }
    } catch (error) {
      console.error('Failed to load user info:', error);
      // 사용자 정보 로드 실패는 무시 (프로필 이미지만 영향)
    }
  }

  // 초기 날짜 설정 (기본 1개월)
  private initializeDates() {
    const today = new Date();
    const oneMonthAgo = new Date(today);
    oneMonthAgo.setMonth(today.getMonth() - 1);

    this.endDate = this.formatDateForInput(today);
    this.startDate = this.formatDateForInput(oneMonthAgo);
  }

  // 날짜 포맷팅 (YYYY.MM.DD)
  private formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  }

  // 백엔드에서 포인트 데이터 로드
  private async loadPointData() {
    this.loading = true;
    try {
      // 현재 포인트 조회
      const pointResponse = await getCurrentPoint(this.channelUid);
      this.totalPoints = pointResponse.data.currentPoint || 0;

      // 포인트 히스토리 조회
      const historyResponse = await getPointHistory({
        channelUid: this.channelUid,
        page: this.page,
        size: this.size,
      });

      this.backendHistory = historyResponse.data.content || [];

      // 백엔드 데이터를 UI 형식으로 변환
      this.historyData = this.backendHistory.map((item) => ({
        date: this.$options.filters?.parseDate(item.createdAt, 'YYYY.MM.DD') || item.createdAt,
        description: item.description,
        amount: item.pointAmount,
        type: item.pointAmount > 0 ? 'earn' : 'spend',
      }));
    } catch (error: any) {
      console.error('Failed to load point data:', error);
      const message = error.response?.data?.message || '포인트 정보를 불러오는데 실패했습니다.';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  get filteredHistory() {
    if (!this.startDate || !this.endDate) {
      return this.historyData;
    }

    // 날짜 문자열을 Date 객체로 변환
    const start = new Date(this.startDate.replace(/\./g, '-'));
    const end = new Date(this.endDate.replace(/\./g, '-'));
    end.setHours(23, 59, 59, 999); // 종료일 마지막 시각까지 포함

    return this.historyData.filter((item) => {
      const itemDate = new Date(item.date.replace(/\./g, '-'));
      return itemDate >= start && itemDate <= end;
    });
  }

  private searchHistory() {
    // 필터링은 computed property에서 자동으로 처리됨
    this.$message.success('조회되었습니다.');
  }

  // 기간 버튼 클릭 시 날짜 자동 설정
  private handlePeriodChange(periodValue: string) {
    this.selectedPeriod = periodValue;

    const today = new Date();
    const startDate = new Date(today);

    switch (periodValue) {
      case '1month':
        startDate.setMonth(today.getMonth() - 1);
        break;
      case '3months':
        startDate.setMonth(today.getMonth() - 3);
        break;
      case '6months':
        startDate.setMonth(today.getMonth() - 6);
        break;
    }

    this.startDate = this.formatDateForInput(startDate);
    this.endDate = this.formatDateForInput(today);
  }

  private handleEditProfile() {
    this.userModalVisible = true;
  }
}
</script>

<style scoped lang="scss">
.rpoint-history-page {
  display: flex;
  min-height: 100vh;
  background: #FFF;
}

.rpoint-main {
  margin-left: 267px;
  padding: 160px 30px 20px;
  flex: 1;
  min-height: calc(100vh - 124px);
  display: flex;
  flex-direction: column;
  gap: 52px;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;
}

.profile-avatar {
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;

  svg {
    display: block;
    width: 100%;
    height: 100%;
  }
}

.profile-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.profile-name-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.profile-name {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: clamp(28px, 2vw, 36px);
  font-weight: 600;
  line-height: 100%;
  margin: 0;
}

.edit-profile-btn {
  width: 135px;
  height: 38px;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #0535e6;
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.2);
  }

  &:active {
    transform: translateY(0);
  }
}

.points-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.points-label {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 600;
  line-height: 100%;
}

.points-value {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 600;
  line-height: 100%;
}

.history-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
}

.filters-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.period-buttons {
  flex: 0 1 calc(100% / 2 - 10px);
  display: flex;
  gap: 20px;
}

.period-btn {
  width: 100%;
  max-width: 100px;
  height: 52px;
  background: #CECECE;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(.active) {
    background: #B0B0B0;
  }

  &.active {
    background: #073DFF;
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }
}

@media screen and (max-width:1200px) {
  .filters-container {
    flex-direction: column-reverse;
    align-items: start;
  }

  .period-buttons {
    width: 100%;
    flex: 0 1 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 20px;
  }

  .period-btn {
    flex: 0 1 calc(100% / 3 - 9px);
  }
}

.date-filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range-picker {
  display: flex;
  align-items: center;
  gap: 5px;
}

.date-input-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 52px;
  padding: 0 16px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #FFF;
  width: 203px;
  transition: border-color 0.3s ease;

  &:focus-within {
    border-color: #073DFF;
  }

  svg {
    flex-shrink: 0;
  }
}

.date-input {
  flex: 1;
  border: none;
  outline: none;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  background: transparent;

  &::placeholder {
    color: #888;
  }
}

.search-btn {
  width: 70px;
  height: 52px;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #0535e6;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.2);
  }

  &:active {
    transform: translateY(0);
  }
}

.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 1px solid #222;
  transition: background 0.2s ease;

  &.first {
    border-top: 2px solid #222;
  }

  &:hover {
    background: rgba(7, 61, 255, 0.02);
  }
}

.history-date {
  width: 145px;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;
}

.history-description {
  flex: 1;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;
  text-align: left;
  padding-left: 30px;
}

.history-points {
  display: flex;
  align-items: center;
  gap: 8px;
}

.points-amount {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;
}

.points-type {
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;

  &.earn {
    color: #34C759;
  }

  &.spend {
    color: #FF3B30;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
}

/* Responsive Design */

@media screen and (max-width: 1024px) {
  .profile-card {
    align-items: flex-start;
  }

  .profile-info {
    align-items: flex-start;
    width: 100%;
    gap: 20px;
  }

  .points-section {
    align-items: flex-end;
  }

  .filters-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .date-filters {
    width: 100%;
    flex-direction: column;
  }

  .date-range-picker {
    width: 100%;
  }

  .date-input-wrapper {
    flex: 1;
  }

  .search-btn {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .rpoint-main {
    padding: 32px 24px;
  }

  .profile-card {
    padding: 24px;
  }

  .profile-name {
    font-size: 28px;
  }

  .points-value {
    font-size: 32px;
  }

  .section-title {
    font-size: 28px;
  }

  .date-input-wrapper {
    width: 125px;
  }

  .date-input-wrapper svg {
    flex-shrink: unset;
  }

  .period-buttons {
    gap: 12px;
  }

  .period-btn {
    width: 100%;
  }

  .history-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    padding: 20px;
  }

  .history-date {
    width: auto;
  }

  .history-points {
    align-self: flex-end;
  }
}

@media screen and (max-width: 1024px) {
  .profile-card {
    flex-direction: column;
  }

  .profile-avatar {
    width: 80px;
    height: 80px;
  }

  .profile-name-section {
    flex: 0 1 100%;
    width: 100%;
  }

  .points-section {
    width: 100%;
    text-align: right;
  }
}

@media screen and (max-width: 768px) {
  .profile-avatar {
    width: 50px;
    height: 50px;
  }

  .rpoint-main {
    padding: 140px 30px 20px;
    margin: 0;
  }

  .profile-name {
    font-size: 24px;
  }

  .section-title {
    font-size: 24px;
  }

  .history-description,
  .history-date,
  .points-amount,
  .points-type {
    font-size: 18px;
  }
  .history-description {padding-left: 0;}
}

@media screen and (max-width: 500px) {
  .profile-name {
    font-size: 20px;
  }

  .section-title {
    font-size: 20px;
  }

  .points-label {
    font-size: 20px;
  }

  .edit-profile-btn {
    width: 120px
  }

  .rpoint-main {
    gap: 20px;
    padding: 120px 20px 20px;
  }
}
</style>
