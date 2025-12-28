<template>
  <div class="activity-list-page">
    <!-- CommunitySidebar -->
    <CommunitySidebar :selectedSpaceId="'activity-list'" />

    <!-- Main Content Area -->
    <div class="activity-main">
      <!-- Mobile-only Profile Card -->
      <!-- <div class="profile-card mobile-only">
        <div class="profile-info">
          <div class="profile-avatar">
            <svg width="100" height="100" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="50" cy="50" r="50" fill="#D9D9D9"/>
              <mask id="mask0_profile" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="100" height="100">
                <circle cx="50" cy="50" r="50" fill="#D9D9D9"/>
              </mask>
              <g mask="url(#mask0_profile)">
                <rect x="11.1016" y="58.332" width="77.7778" height="88.8889" rx="38.8889" fill="#F5F5F5"/>
                <circle cx="50.0108" cy="30.546" r="19.4444" fill="#F5F5F5"/>
              </g>
            </svg>
          </div>
          <div class="profile-details">
            <h3 class="profile-name"> 님</h3>
            <div class="points-badge">
              <span class="points-text">알 포인트: {{ totalPoints.toLocaleString() }}</span>
            </div>
          </div>
        </div>
        <button class="profile-edit-btn">프로필 수정</button>
      </div> -->

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
              @click="selectedPeriod = period.value"
            >
              {{ period.label }}
            </button>
          </div>

          <div class="date-filters">
            <div class="date-range-picker">
              <div class="date-input-wrapper">
                <input
                  v-model="startDate"
                  type="text"
                  placeholder="2025.01.01"
                  class="date-input"
                  @focus="$event.target.type='date'"
                  @blur="$event.target.type='text'"
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
                  type="text"
                  placeholder="2025.02.01"
                  class="date-input"
                  @focus="$event.target.type='date'"
                  @blur="$event.target.type='text'"
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

      <!-- R Points Transactions List -->
   <!--    <div class="transactions-list">
        <div
          v-for="(transaction, index) in filteredTransactions"
          :key="index"
          class="transaction-item"
          :class="{ first: index === 0 }"
        >
          <div class="transaction-content">
            <div class="transaction-title">{{ transaction.title }}</div>
            <div class="transaction-meta">
              <span class="transaction-date">{{ transaction.date }}</span>
              <div class="transaction-amount-wrapper">
                <span class="transaction-amount">{{ formatAmount(transaction.amount) }}</span>
                <span class="transaction-type" :class="transaction.type">{{ transaction.typeLabel }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="filteredTransactions.length === 0" class="empty-state">
          <p>R포인트 내역이 없습니다.</p>
        </div>
      </div>
 -->
      <!-- Activity List Section -->
      <div class="activity-section activity-history-section">
        <!-- <h2 class="section-title">활동 리스트</h2> -->

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
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

interface ActivityItem {
  date: string;
  description: string;
}

interface TransactionItem {
  title: string;
  date: string;
  amount: number;
  type: 'deduct' | 'earn';
  typeLabel: string;
}

@Component({
  name: 'ActivityList',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private selectedPeriod = '1month';
  private startDate = '2025.01.01';
  private endDate = '2025.02.01';
  private totalPoints = 0;

  private periods = [
    { label: '1개월', value: '1month' },
    { label: '3개월', value: '3months' },
    { label: '6개월', value: '6months' },
  ];

  private transactionsData: TransactionItem[] = [
    {
      title: '산청 사과 1박스',
      date: '2025.01.01',
      amount: -15000,
      type: 'deduct',
      typeLabel: '차감',
    },
    {
      title: '고구마 수확 도움이 필요합니다.',
      date: '2025.02.01',
      amount: 10000,
      type: 'earn',
      typeLabel: '적립',
    },
  ];

  private activitiesData: ActivityItem[] = [
    {
      date: '2025.01.01',
      description: '산청의료협동조합 공간에 초대되었습니다.',
    },
    {
      date: '2025.02.01',
      description: '산청등산산모임 공간을 개설하였습니다.',
    },
    {
      date: '2025.02.04',
      description: '이명관님 게시글에 댓글을 작성하였습니다.',
    },
    {
      date: '2025.02.04',
      description: '고혈압, 당뇨 예방을 위한 식단 관리',
    },
  ];

  get filteredTransactions() {
    return this.transactionsData;
  }

  get filteredActivities() {
    return this.activitiesData;
  }

  private formatAmount(amount: number): string {
    const sign = amount >= 0 ? '+' : '';
    return `${sign}${amount.toLocaleString()}`;
  }

  private searchActivities() {
    this.$message.info('조회 기능을 실행합니다.');
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

.profile-card {
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;

  .profile-info {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 8px;
  }

  .profile-avatar {
    width: 100px;
    height: 100px;
    flex-shrink: 0;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  .profile-details {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
  }

  .profile-name {
    color: #000;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 100%;
    margin: 0;
  }

  .points-badge {
    display: flex;
    padding: 10px 12px;
    justify-content: center;
    align-items: center;
    gap: 8px;
    border-radius: 20px;
    background: rgba(7, 61, 255, 0.10);
  }

  .points-text {
    color: #073DFF;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 12px;
    font-weight: 700;
    line-height: 100%;
  }

  .profile-edit-btn {
    display: flex;
    height: 54px;
    padding: 12px;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-radius: 10px;
    background: #073DFF;
    border: none;
    color: #FFF;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
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

.transactions-list {
  display: flex;
  flex-direction: column;
}

.transaction-item {
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

.transaction-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.transaction-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  text-align: left;
}

.transaction-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.transaction-date {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 400;
  line-height: 100%;
}

.transaction-amount-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.transaction-amount {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
}

.transaction-type {
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 700;
  line-height: 100%;

  &.deduct {
    color: #FF3B30;
  }

  &.earn {
    color: #34C759;
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
