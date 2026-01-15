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
              @click="handlePeriodChange(period.value)"
            >
              {{ period.label }}
            </button>
          </div>

          <div class="date-filters">
            <div class="date-range-picker">
              <!-- eslint-disable-next-line vue/no-unused-properties -->
              <el-date-picker
                v-model="startDate"
                type="date"
                placeholder="시작일"
                format="yyyy.MM.dd"
                value-format="yyyy-MM-dd"
                class="custom-date-picker"
              />

              <svg class="separator-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 10C4 10 6.58778 6.62471 10 10C13.4122 13.3753 16 10 16 10" stroke="#CECECE" stroke-width="1.5" stroke-linecap="round"/>
              </svg>

              <!-- eslint-disable-next-line vue/no-unused-properties -->
              <el-date-picker
                v-model="endDate"
                type="date"
                placeholder="종료일"
                format="yyyy.MM.dd"
                value-format="yyyy-MM-dd"
                class="custom-date-picker"
              />
            </div>

            <button class="search-btn" @click="searchActivities">조회</button>
          </div>
        </div>
      </div>

      <!-- Activity List Section -->
      <div class="activity-section activity-history-section">
        <!-- Activity List -->
        <div class="activities-list">
          <div
            v-for="(activity, index) in filteredActivities"
            :key="index"
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
    <!-- <button class="floating-action-btn mobile-only" @click="handleFloatingAction">
      <svg width="43" height="43" viewBox="0 0 43 43" fill="none" xmlns="http://www.w3.org/2000/svg">
        <rect width="43" height="43" rx="21.5" fill="#073DFF"/>
        <path d="M21.5 14V29M29 21.5H14" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button> -->
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import { getUserActivities, Activity, ActivityListRequest } from '@/api/activity';
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
  private selectedPeriod = 1; // 1, 3, 6 months
  private currentChannel: any = null;

  // pagination
  private page = 1; // UI 페이지(1-based)
  private limit = 20; // 페이지 사이즈
  private total = 0; // 전체 항목 수
  private startDate: string | null = null;
  private endDate: string | null = null;

  private periods = [
    { label: '1개월', value: 1 },
    { label: '3개월', value: 3 },
    { label: '6개월', value: 6 },
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

  /**
   * userUid getter
   */
  get userUid(): string {
    return UserModule.userId || '';
  }

  @Watch('$route.params.domain')
  private async onDomainChange() {
    await this.initChannel();
    this.page = 1;
    await this.loadActivities();
  }

  /**
   * 기간 버튼 클릭 시 날짜 자동 설정 및 조회
   */
  private async handlePeriodChange(periodValue: number) {
    this.selectedPeriod = periodValue;
    this.updateDateRange();
    this.page = 1;
    await this.loadActivities();
    this.$message.success('조회되었습니다.');
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

    startDate.setMonth(today.getMonth() - this.selectedPeriod);

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
    // ISO 형식 또는 날짜 형식 모두 지원
    if (dateString.includes('.')) {
      return dateString; // 이미 yyyy.MM.dd 형식
    }
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
    if (!this.channelUid) {
      console.warn('[activityList] channelUid is empty, cannot load activities');
      return;
    }

    if (!this.userUid) {
      console.warn('[activityList] userUid is empty, cannot load activities');
      return;
    }

    if (!this.startDate || !this.endDate) {
      console.warn('[activityList] startDate or endDate is empty');
      return;
    }

    try {
      const params: ActivityListRequest = {
        channelUid: this.channelUid,
        months: this.selectedPeriod,
        page: Math.max(0, this.page - 1), // backend expects 0-based
        size: this.limit,
      };

      console.log('[activityList] Loading activities with params:', params);

      const res = await getUserActivities(this.userUid, params);
      console.log('[activityList] API response:', res.data);

      // API 응답 처리: 페이징된 응답
      const data = res.data;
      let activities: Activity[] = [];

      if (data && data.content && Array.isArray(data.content)) {
        activities = data.content;
        this.total = data.totalElements ?? data.total ?? activities.length;
      } else if (Array.isArray(data)) {
        activities = data;
        this.total = activities.length;
      } else {
        console.warn('[activityList] Unexpected API response format:', data);
        activities = [];
        this.total = 0;
      }

      console.log(`[activityList] Loaded ${activities.length} activities, total: ${this.total}`);

      // API 응답 데이터를 ActivityItem 형식으로 변환
      this.activitiesData = activities.map((activity: Activity) => ({
        date: activity.createdAtFormatted || this.formatDateForDisplay(activity.createdAt),
        description: activity.description || '',
        type: activity.activityType || '',
      }));
    } catch (error: any) {
      console.error('활동 리스트 조회 실패:', error);
      const message = error.response?.data?.message || '활동 리스트를 불러오는데 실패했습니다';
      this.$message.error(message);
      this.activitiesData = [];
      this.total = 0;
    }
  }

  get filteredActivities() {
    return this.activitiesData;
  }

  /**
   * 조회 버튼 클릭
   */
  private searchActivities() {
    this.page = 1;
    this.loadActivities();
  }

  /**
   * 페이지네이션 변경
   */
  private onPagination(data: { page: number; limit: number }) {
    this.page = data.page;
    this.limit = data.limit;
    this.loadActivities();
  }

  /**
   * Floating Action Button 클릭
   */
  private handleFloatingAction() {
    // 모바일에서 추가 액션 처리
    console.log('Floating action button clicked');
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
  padding: 160px 30px 20px;
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

.custom-date-picker {
  width: 203px;
  height: 52px;

  ::v-deep .el-input__inner {
    height: 52px;
    padding: 0 12px;
    border-radius: 10px;
    border: 1px solid #CECECE;
    color: #888;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 20px;

    &:focus {
      border-color: #073DFF;
    }
  }

  ::v-deep .el-input__prefix {
    display: none;
  }

  ::v-deep .el-input__suffix {
    right: 10px;
  }
}

.separator-icon {
  flex-shrink: 0;
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

  .custom-date-picker {
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

  .custom-date-picker {
    width: 125px;
    height: 52px;
  }

  .search-btn {
    height: 54px;
    font-size: 16px;
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
  .activity-main {
    padding: 110px 20px 20px;
  }

  .activity-description,
  .activity-date {
    font-size: 16px;
  }
}
</style>
