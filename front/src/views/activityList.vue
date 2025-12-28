<template>
  <div class="activity-list-page">
    <!-- CommunitySidebar -->
    <CommunitySidebar :selectedSpaceId="'activity-list'" />

    <!-- Main Content Area -->
    <div class="activity-main">
      <!-- R Points History Section -->
      <div class="activity-section">
        <h2 class="section-title">활동 리스트</h2>

        <!-- Filters -->
        <div class="filters-container">
          <div class="period-buttons">
            <button
              v-for="period in periods"
              :key="period.value"
              class="period-btn"
              :class="{ active: selectedPeriod === period.value }"
              @click="onSelectPeriod(period.value)"
            >
              {{ period.label }}
            </button>
          </div>

          <div class="date-filters">
            <div class="date-range-picker">
              <div class="date-input-wrapper">
                <input
                  v-model="startDate"
                  type="date"
                  class="date-input"
                />
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14.625 6.1875L9 11.8125L3.375 6.1875" stroke="black" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>

              <svg class="separator-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 10C4 10 6.58778 6.62471 10 10C13.4122 13.3753 16 10 16 10" stroke="#CECECE" stroke-width="1.5" stroke-linecap="round"/>
              </svg>

              <div class="date-input-wrapper">
                <input
                  v-model="endDate"
                  type="date"
                  class="date-input"
                />
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14.625 6.1875L9 11.8125L3.375 6.1875" stroke="black" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
            </div>

            <button class="search-btn" @click="searchActivities">조회</button>
          </div>
        </div>
      </div>

      <!-- Activity List Section -->
      <div class="activity-section activity-history-section">
        <!-- <h2 class="section-title">활동 리스트</h2> -->

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <i class="el-icon-loading"></i>
          <p>활동 내역을 불러오는 중...</p>
        </div>

        <!-- Activity List -->
        <div v-else class="activities-list">
          <div
            v-for="(activity, index) in filteredActivities"
            :key="activity.type + index"
            class="activity-item"
            :class="{ first: index === 0 }"
          >
            <div class="activity-date">{{ activity.date }}</div>
            <div class="activity-description">{{ activity.description }}</div>
          </div>

          <div v-if="filteredActivities.length === 0" class="empty-state">
            <p>활동 내역이 없습니다.</p>
          </div>
        </div>

        <!-- Pagination -->
        <pagination
          v-if="total > 0"
          :total="total"
          :page="page"
          :limit="limit"
          @pagination="onPagination"
        />
      </div>
    </div>

    <!-- Floating Action Button (Mobile Only) -->
    <button class="floating-action-btn mobile-only" @click="handleFloatingAction">
      <svg width="43" height="43" viewBox="0 0 43 43" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="43" height="43" rx="21.5" fill="#073DFF"/>
        <path d="M21.5 14V29M29 21.5H14" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { ChannelModule } from '@/store/modules/channel';
import { getMyActivities, Activity } from '@/api/activity';
import Pagination from '@/components/Pagination/index.vue';

interface ActivityItem {
  date: string;
  description: string;
  type: string;
}

@Component({
  name: 'ActivityList',
  components: {
    CommunitySidebar,
    Pagination,
  },
})
export default class extends Vue {
  private selectedPeriod = '1month';
  private startDate = '';
  private endDate = '';
  private loading = false;
  private currentChannel: any = null;

  // pagination
  private page = 1; // UI 페이지(1-based)
  private limit = 10; // 페이지 사이즈
  private total = 0; // 전체 항목 수

  private periods = [
    { label: '1개월', value: '1month' },
    { label: '3개월', value: '3months' },
    { label: '6개월', value: '6months' },
  ];

  private activitiesData: ActivityItem[] = [];

  async created() {
    await this.initChannel();
    this.initDateRange();
    await this.loadActivities();
  }

  /**
   * 채널 정보 초기화
   */
  private async initChannel() {
    const domain = this.$route.params.domain;
    if (!domain) {
      this.$message.error('채널 정보를 찾을 수 없습니다');
      return;
    }

    if (ChannelModule.selectedChannel && ChannelModule.selectedChannel.uid) {
      this.currentChannel = ChannelModule.selectedChannel;
    } else {
      try {
        const { getChannelDomainDetail } = await import('@/api/channel');
        const res = await getChannelDomainDetail(domain);
        this.currentChannel = res.data;
        ChannelModule.setSelectedChannel(this.currentChannel);
      } catch (error) {
        console.error('채널 정보 조회 실패:', error);
        this.$message.error('채널 정보를 불러오는데 실패했습니다');
      }
    }
  }

  /**
   * channelUid getter
   */
  get channelUid(): string {
    const uid = this.currentChannel?.uid || ChannelModule.selectedChannel?.uid;
    if (!uid) {
      console.warn('[activityList] channelUid is empty');
    }
    return uid || '';
  }

  @Watch('$route.params.domain')
  private async onDomainChange() {
    await this.initChannel();
    this.page = 1;
    await this.loadActivities();
  }

  // period 선택시 호출되는 핸들러 (page 리셋 포함)
  private onSelectPeriod(value: string) {
    this.selectedPeriod = value;
    this.updateDateRange();
    this.page = 1;
    this.loadActivities();
  }

  @Watch('selectedPeriod')
  private onPeriodChange() {
    // backward compatibility: keep behavior but ensure page reset
    this.updateDateRange();
    this.page = 1;
    this.loadActivities();
  }

  /**
   * 날짜 범위 초기화 (1개월)
   */
  private initDateRange() {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);

    this.endDate = this.formatDateForInput(today);
    this.startDate = this.formatDateForInput(oneMonthAgo);
  }

  /**
   * 선택된 기간에 따라 날짜 범위 업데이트
   */
  private updateDateRange() {
    const today = new Date();
    let startDate = new Date();

    switch (this.selectedPeriod) {
      case '1month':
        startDate.setMonth(today.getMonth() - 1);
        break;
      case '3months':
        startDate.setMonth(today.getMonth() - 3);
        break;
      case '6months':
        startDate.setMonth(today.getMonth() - 6);
        break;
      default:
        startDate.setMonth(today.getMonth() - 1);
    }

    this.startDate = this.formatDateForInput(startDate);
    this.endDate = this.formatDateForInput(today);
  }

  /**
   * 날짜를 input date 형식으로 변환 (yyyy-MM-dd)
   */
  private formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * 날짜를 화면 표시 형식으로 변환 (yyyy.MM.dd)
   */
  private formatDateForDisplay(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  }

  /**
   * 활동 리스트 조회
   */
  private async loadActivities() {
    if (!this.channelUid) return;

    this.loading = true;
    try {
      const params: any = {
        channelUid: this.channelUid,
        page: Math.max(0, this.page - 1), // backend expects 0-based
        size: this.limit,
      };

      // 날짜 필터 추가
      if (this.startDate) {
        params.startDate = this.startDate;
      }
      if (this.endDate) {
        params.endDate = this.endDate;
      }

      const res = await getMyActivities(params);

      // API 응답 처리: 페이징된 응답이면 content와 totalElements 사용
      const data = res.data;
      let activities: any[] = [];

      if (data && data.content && Array.isArray(data.content)) {
        activities = data.content;
        this.total = data.totalElements ?? data.total ?? activities.length;
      } else if (Array.isArray(data)) {
        activities = data;
        this.total = activities.length;
      } else if (data && Array.isArray(data.data)) {
        activities = data.data;
        this.total = data.total ?? activities.length;
      } else {
        activities = [];
        this.total = 0;
      }

      // API 응답 데이터를 ActivityItem 형식으로 변환
      this.activitiesData = activities.map((activity: Activity) => ({
        date: this.formatDateForDisplay(activity.createdAt),
        description: activity.description || '',
        type: activity.type || '',
      }));
    } catch (error: any) {
      console.error('활동 리스트 조회 실패:', error);
      const message = error.response?.data?.message || '활동 리스트를 불러오는데 실패했습니다';
      this.$message.error(message);
      this.activitiesData = [];
      this.total = 0;
    } finally {
      this.loading = false;
    }
  }

  get filteredActivities() {
    return this.activitiesData;
  }

  /**
   * 조회 버튼 클릭
   */
  private searchActivities() {
    if (!this.startDate || !this.endDate) {
      this.$message.warning('시작일과 종료일을 선택해주세요');
      return;
    }

    const start = new Date(this.startDate);
    const end = new Date(this.endDate);

    if (start > end) {
      this.$message.warning('시작일은 종료일보다 이전이어야 합니다');
      return;
    }

    // 페이지 리셋 후 조회
    this.page = 1;
    this.loadActivities();
  }

  private onPagination(payload: { page: number; limit: number }) {
    // payload.page is 1-based
    this.page = payload.page;
    this.limit = payload.limit;
    this.loadActivities();
  }

  private handleFloatingAction() {
    this.$message.info('새 항목 추가');
  }
}
</script>

<style scoped lang="scss">
.activity-list-page {
  display: flex;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.activity-main {
  margin-left: 267px;
  padding: 160px 40px 20px;
  flex: 1;
  min-height: calc(100vh - 124px);
  display: flex;
  flex-direction: column;
  gap: 52px;
}

.mobile-only {
  display: none;
}

.activity-section {
  display: flex;
  flex-direction: column;
  gap: 20px;

  &.activity-history-section {
    margin-top: 20px;
  }
}

.section-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.filters-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: center;
}

.period-buttons {
  height: 52px;
  flex: 0 1 calc(100% - 536px);
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

.date-filters {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range-picker {
  display: flex;
  align-items: center;
  gap: 5px;
  max-width: 100%;
  flex: 1;
}

.separator-icon {
  flex-shrink: 0;
}

.date-input-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 52px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #FFF;
  width: 203px;
  min-width: 0;
  flex: 1;
  gap: 8px;
  transition: border-color 0.3s ease;
  position: relative;

  &:focus-within {
    border-color: #073DFF;
  }

  svg {
    flex-shrink: 0;
  }
}

.date-input-wrapper svg {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
}

.date-input {
  flex: 1;
  border: none;
  outline: none;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
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

.activities-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 20px;
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

.activity-date {
  width: 145px;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;
  flex-shrink: 0;
}

.activity-description {
  flex: 1;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 400;
  line-height: 100%;
  text-align: left;
  padding-left: 30px;
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

.loading-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  gap: 16px;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;

  i {
    font-size: 32px;
    color: #073DFF;
  }

  p {
    margin: 0;
  }
}

.floating-action-btn {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 43px;
  height: 43px;
  border: none;
  background: transparent;
  cursor: pointer;
  z-index: 1000;
  transition: transform 0.3s ease;

  &:hover {
    transform: scale(1.1);
  }

  &:active {
    transform: scale(0.95);
  }

  svg {
    width: 100%;
    height: 100%;
  }
}

/* Tablet Design */
@media screen and (max-width: 1200px) {
  .activity-main {
    gap: 40px;
  } 
}

@media screen and (max-width: 1024px) {
  .activity-main {
    margin-left: 240px;
    gap: 40px;
  }

  .section-title {
    font-size: 32px;
  }

  .filters-container {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .period-buttons {
    gap: 16px;
  }

  .period-btn {
    flex: 1;
  }

  .date-filters {
    flex-direction: column;
    width: 100%;
    gap: 12px;
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

  .activity-date {
    font-size: 20px;
  }

  .activity-description {
    font-size: 20px;
    padding-left: 20px;
  }
}

/* Mobile Design */
@media screen and (max-width: 768px) {
  .mobile-only {
    display: flex;
  }

  .activity-main {
    margin: 0;
    padding: 140px 20px 20px;
    gap: 20px;
  }

  .section-title {
    font-size: 24px;
  }

  .filters-container {
    gap: 8px;
  }

  .period-buttons {
    width: 100%;
    gap: 8px;
  }

  .period-btn {
    padding: 16px 20px;
    font-size: 16px;
    height: auto;
  }

  .date-filters {
    gap: 8px;
  }

  .date-input-wrapper {
    width: 125px;
    height: 52px;
  }

  .date-input-wrapper svg {
    flex-shrink: unset;
  }

  .date-input {
    font-size: 16px;
  }

  .search-btn {
    height: 54px;
    font-size: 16px;
  }

  .transaction-item {
    padding: 20px 0;
  }

  .activity-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 20px 0;
  }

  .activity-date {
    width: auto;
    font-size: 18px;
  }

  .activity-description {
    font-size: 18px;
    padding-left: 0;
  }

  .floating-action-btn {
    right: 20px;
    bottom: 20px;
  }
}

@media screen and (max-width: 500px) {
  .activity-main {padding: 110px 20px 20px;}
  .activity-description,
  .activity-date {
    font-size: 16px;
  }
}
</style>
