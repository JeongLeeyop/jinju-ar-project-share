<template>
  <div class="calendar-page">
    <!-- HomeHeader is auto-inserted by layout -->
    
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="'calendar'" @open-write-modal="handleWriteModal" />

    <!-- Main Content Area -->
    <div class="calendar-main">
      <!-- Empty State -->
      <div v-if="eventList.length === 0 && !loading" class="empty-state">
        <div class="empty-box">
          <h2 class="empty-title">예정된 일정이 없습니다.</h2>
          <p class="empty-subtitle">예정된 일정은 여기에 표시됩니다.</p>
        </div>
      </div>

      <!-- Events List -->
      <div v-else class="events-container">
        <div
          v-for="event in eventList"
          :key="event.idx"
          class="event-card-wrapper"
        >
          <!-- Event Header -->
          <div class="event-header">
            <div class="event-date-info">
              <div class="event-date">
                <svg width="25" height="25" viewBox="0 0 25 25" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8.27778 7.22222V3M16.7222 7.22222V3M7.22222 11.4444H17.7778M5.11111 22H19.8889C20.4488 22 20.9858 21.7776 21.3817 21.3817C21.7776 20.9858 22 20.4488 22 19.8889V7.22222C22 6.66232 21.7776 6.12535 21.3817 5.72944C20.9858 5.33353 20.4488 5.11111 19.8889 5.11111H5.11111C4.55121 5.11111 4.01424 5.33353 3.61833 5.72944C3.22242 6.12535 3 6.66232 3 7.22222V19.8889C3 20.4488 3.22242 20.9858 3.61833 21.3817C4.01424 21.7776 4.55121 22 5.11111 22Z" stroke="black" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="date-text">{{ event.start | formatEventDate }}</span>
              </div>
              
              <div class="event-meta">
                <div class="meta-item">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 8V12L15 15M21 12C21 13.1819 20.7672 14.3522 20.3149 15.4442C19.8626 16.5361 19.1997 17.5282 18.364 18.364C17.5282 19.1997 16.5361 19.8626 15.4442 20.3149C14.3522 20.7672 13.1819 21 12 21C10.8181 21 9.64778 20.7672 8.55585 20.3149C7.46392 19.8626 6.47177 19.1997 5.63604 18.364C4.80031 17.5282 4.13738 16.5361 3.68508 15.4442C3.23279 14.3522 3 13.1819 3 12C3 9.61305 3.94821 7.32387 5.63604 5.63604C7.32387 3.94821 9.61305 3 12 3C14.3869 3 16.6761 3.94821 18.364 5.63604C20.0518 7.32387 21 9.61305 21 12Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ event.start | formatEventTime }} ~ {{ event.end | formatEventTime }}</span>
                </div>

                <div v-if="event.location" class="meta-item">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17.657 16.657L13.414 20.9C13.2284 21.0857 13.0081 21.2331 12.7656 21.3336C12.523 21.4342 12.2631 21.4859 12.0005 21.4859C11.738 21.4859 11.478 21.4342 11.2354 21.3336C10.9929 21.2331 10.7726 21.0857 10.587 20.9L6.343 16.657C5.22422 15.5382 4.46234 14.1127 4.15369 12.5609C3.84504 11.009 4.00349 9.40051 4.60901 7.93873C5.21452 6.47694 6.2399 5.22754 7.55548 4.3485C8.87107 3.46947 10.4178 3.00029 12 3.00029C13.5822 3.00029 15.1289 3.46947 16.4445 4.3485C17.7601 5.22754 18.7855 6.47694 19.391 7.93873C19.9965 9.40051 20.155 11.009 19.8463 12.5609C19.5377 14.1127 18.7758 15.5382 17.657 16.657Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M15 11C15 11.7956 14.6839 12.5587 14.1213 13.1213C13.5587 13.6839 12.7956 14 12 14C11.2044 14 10.4413 13.6839 9.87868 13.1213C9.31607 12.5587 9 11.7956 9 11C9 10.2044 9.31607 9.44129 9.87868 8.87868C10.4413 8.31607 11.2044 8 12 8C12.7956 8 13.5587 8.31607 14.1213 8.87868C14.6839 9.44129 15 10.2044 15 11Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ event.location || '산청의료협동조합 본관 2층' }}</span>
                </div>

                <div v-if="event.points" class="meta-item">
                  <div class="points-badge">R</div>
                  <span class="points-value">{{ event.points || '5,000' }}</span>
                </div>
              </div>
            </div>

            <button class="join-button" @click="handleJoinEvent(event)">
              참여하기
            </button>
          </div>

          <!-- Event Content Card -->
          <div class="event-content">
            <div class="timeline-connector"></div>
            
            <div class="event-card">
              <!-- User Info -->
              <div class="user-info">
                <div class="user-avatar">
                  <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_user)">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <span class="user-name">{{ event.writer || ' 님' }}</span>
              </div>

              <!-- Event Details -->
              <div class="event-details">
                <h3 class="event-title">{{ event.title }}</h3>
                <p class="event-description">{{ event.description || event.extendedProps?.description }}</p>
                
                <!-- Stats -->
                <div class="event-stats">
                  <div class="stat-item" :class="{ liked: isLiked(event.idx) }" @click.stop="toggleLike(event.idx)">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M7.49301 21.5C7.06801 21.5 6.67301 21.264 6.51801 20.868C6.17475 19.9943 5.99902 19.0637 6.00001 18.125C6.00001 16.375 6.59901 14.767 7.60201 13.491C7.75301 13.299 7.97501 13.182 8.20201 13.094C8.67501 12.911 9.09201 12.58 9.41401 12.17C10.1903 11.1796 11.1647 10.3622 12.275 9.77C12.998 9.386 13.625 8.814 13.928 8.055C14.1408 7.52325 14.2501 6.95575 14.25 6.383V5.75C14.25 5.55109 14.329 5.36032 14.4697 5.21967C14.6103 5.07902 14.8011 5 15 5C15.5967 5 16.169 5.23705 16.591 5.65901C17.013 6.08097 17.25 6.65326 17.25 7.25C17.25 8.402 16.99 9.493 16.527 10.468C16.261 11.026 16.634 11.75 17.252 11.75H20.378C21.404 11.75 22.323 12.444 22.432 13.465C22.477 13.887 22.5 14.315 22.5 14.75C22.5041 17.4863 21.5691 20.1412 19.851 22.271C19.463 22.753 18.864 23 18.246 23H14.23C13.747 23 13.266 22.922 12.807 22.77L9.69301 21.73C9.23411 21.5774 8.75362 21.4997 8.27001 21.5H7.49301ZM2.33101 13.727C1.77993 15.1277 1.498 16.6198 1.50001 18.125C1.49875 19.3133 1.67399 20.4952 2.02001 21.632C2.28001 22.482 3.10501 23 3.99401 23H4.90001C5.34501 23 5.62001 22.502 5.42301 22.102C4.81315 20.8651 4.49695 19.5041 4.49901 18.125C4.49901 16.417 4.97501 14.82 5.80101 13.459C6.04601 13.056 5.77301 12.5 5.30101 12.5H4.25001C3.41801 12.5 2.63701 12.953 2.33101 13.727Z" :fill="isLiked(event.idx) ? '#073DFF' : '#CECECE'"/>
                    </svg>
                    <span class="stat-count">{{ getLikeCount(event.idx) }}</span>
                  </div>

                  <div class="stat-divider"></div>

                  <div class="stat-item" :class="{ active: shouldShowComments(event.idx) }" @click.stop="toggleComments(event.idx)">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path fill-rule="evenodd" clip-rule="evenodd" d="M4.848 2.771C7.21613 2.4234 9.60649 2.24927 12 2.25C14.43 2.25 16.817 2.428 19.152 2.77C21.13 3.062 22.5 4.794 22.5 6.74V12.76C22.5 14.706 21.13 16.438 19.152 16.73C17.9983 16.8991 16.8389 17.0268 15.676 17.113C15.6168 17.1169 15.5593 17.1342 15.5079 17.1637C15.4564 17.1931 15.4123 17.234 15.379 17.283L12.624 21.416C12.5555 21.5187 12.4627 21.6029 12.3539 21.6612C12.245 21.7194 12.1235 21.7499 12 21.7499C11.8765 21.7499 11.755 21.7194 11.6461 21.6612C11.5373 21.6029 11.4445 21.5187 11.376 21.416L8.621 17.283C8.58768 17.234 8.54361 17.1931 8.49214 17.1637C8.44068 17.1342 8.38317 17.1169 8.324 17.113C7.16113 17.0265 6.00172 16.8984 4.848 16.729C2.87 16.439 1.5 14.705 1.5 12.759V6.741C1.5 4.795 2.87 3.061 4.848 2.771ZM6.75 8.25C6.75 8.05109 6.82902 7.86032 6.96967 7.71967C7.11032 7.57902 7.30109 7.5 7.5 7.5H16.5C16.6989 7.5 16.8897 7.57902 17.0303 7.71967C17.171 7.86032 17.25 8.05109 17.25 8.25C17.25 8.44891 17.171 8.63968 17.0303 8.78033C16.8897 8.92098 16.6989 9 16.5 9H7.5C7.30109 9 7.11032 8.92098 6.96967 8.78033C6.82902 8.63968 6.75 8.44891 6.75 8.25ZM7.5 10.5C7.30109 10.5 7.11032 10.579 6.96967 10.7197C6.82902 10.8603 6.75 11.0511 6.75 11.25C6.75 11.4489 6.82902 11.6397 6.96967 11.7803C7.11032 11.921 7.30109 12 7.5 12H12C12.1989 12 12.3897 11.921 12.5303 11.7803C12.671 11.6397 12.75 11.4489 12.75 11.25C12.75 11.0511 12.671 10.8603 12.5303 10.7197C12.3897 10.579 12.1989 10.5 12 10.5H7.5Z" :fill="shouldShowComments(event.idx) ? '#073DFF' : '#CECECE'"/>
                    </svg>
                    <span class="stat-count">{{ event.commentCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Comments Section -->
          <div class="comments-section" v-if="shouldShowComments(event.idx)">
            <div class="comment-list">
              <div class="comment-item">
                <div class="comment-header">
                  <div class="user-info">
                    <div class="user-avatar">
                      <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        <mask id="mask0_comment" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                          <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        </mask>
                        <g mask="url(#mask0_comment)">
                          <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                          <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                        </g>
                      </svg>
                    </div>
                    <span class="user-name">김철수</span>
                  </div>
                  <span class="comment-date">2025. 01. 10.</span>
                </div>
                <div class="comment-text">
                  좋은 일정이네요! 꼭 참여하고 싶습니다.
                </div>
              </div>

              <div class="comment-item">
                <div class="comment-header">
                  <div class="user-info">
                    <div class="user-avatar">
                      <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        <mask id="mask1_comment" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                          <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        </mask>
                        <g mask="url(#mask1_comment)">
                          <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                          <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                        </g>
                      </svg>
                    </div>
                    <span class="user-name">이영희</span>
                  </div>
                  <span class="comment-date">2025. 01. 11.</span>
                </div>
                <div class="comment-text">
                  시간이 잘 맞네요. 저도 참여할게요!
                </div>
              </div>
            </div>

            <div class="comment-input-area">
              <input 
                type="text" 
                class="comment-input" 
                placeholder="댓글을 입력하세요." 
                v-model="commentInputs[event.idx]"
                @keyup.enter="handleCommentSubmit(event.idx)"
              />
              <button class="comment-submit-btn" @click="handleCommentSubmit(event.idx)">댓글입력</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Floating Write Button (Mobile Only) -->
      <button class="write-event-btn-fixed" @click="handleWriteModal">
        <svg class="btn-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 5V19M5 12H19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>

    <!-- Create Event Modal -->
    <el-dialog
      :visible.sync="createEventModalVisible"
      width="704px"
      custom-class="create-event-modal"
      :show-close="false"
    >
      <div class="modal-content-wrapper">
        <button class="modal-close-btn" @click="createEventModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">어떤 일정인가요?</h2>

        <div class="form-container">
          <!-- Event Title -->
          <div class="form-group">
            <label class="form-label">일정 제목</label>
            <input
              v-model="eventForm.title"
              type="text"
              class="form-input"
              placeholder="일정 제목에 대해서 작성해주세요."
            />
          </div>

          <!-- Event Description -->
          <div class="form-group">
            <label class="form-label">일정소개</label>
            <textarea
              v-model="eventForm.description"
              class="form-textarea"
              placeholder="일정에 대해서 간략하게 소개해 주세요."
              rows="4"
            ></textarea>
          </div>

          <!-- Date and Time -->
          <div class="form-group">
            <label class="form-label">날짜 및 시간</label>
            <div class="datetime-container">
              <el-date-picker
                v-model="eventForm.date"
                type="date"
                placeholder="2025. 01. 01 (수)"
                class="date-picker"
                format="yyyy. MM. dd (E)"
              />
              
              <div class="time-range">
                <el-time-select
                  v-model="eventForm.startTime"
                  placeholder="오후 3:00"
                  :picker-options="{
                    start: '00:00',
                    step: '00:30',
                    end: '23:30'
                  }"
                  class="time-picker"
                />
                
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 10C4 10 6.58778 6.62471 10 10C13.4122 13.3753 16 10 16 10" stroke="#CECECE" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
                
                <el-time-select
                  v-model="eventForm.endTime"
                  placeholder="오후 4:00"
                  :picker-options="{
                    start: '00:00',
                    step: '00:30',
                    end: '23:30'
                  }"
                  class="time-picker"
                />
              </div>
            </div>
          </div>

          <!-- Location -->
          <div class="form-group">
            <label class="form-label">진행 장소</label>
            <input
              v-model="eventForm.location"
              type="text"
              class="form-input"
              placeholder="어디에서 진행되는 거래인가요?"
            />
          </div>

          <!-- Event Type -->
          <div class="form-group">
            <label class="form-label">일정 참여 방법</label>
            <div class="event-type-buttons">
              <button
                v-for="type in eventTypes"
                :key="type.value"
                class="event-type-btn"
                :class="{ active: eventForm.type === type.value }"
                @click="eventForm.type = type.value"
              >
                {{ type.label }}
              </button>
            </div>
            <input
              v-if="eventForm.type !== 'free'"
              v-model="eventForm.points"
              type="text"
              class="form-input"
              placeholder="R포인트를 작성해 주세요."
            />
          </div>

          <!-- Submit Button -->
          <button class="submit-button" @click="handleCreateEvent">
            일정 만들기
          </button>
        </div>
      </div>
    </el-dialog>

    <!-- Join Event Modal -->
    <el-dialog
      :visible.sync="joinEventModalVisible"
      width="498px"
      custom-class="join-event-modal"
      :show-close="false"
    >
      <div class="modal-content-wrapper">
        <button class="modal-close-btn" @click="joinEventModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">{{ selectedEvent.title }}</h2>
        <div class="modal-subtitle">R: {{ selectedEvent.points || 5000 }}</div>

        <div class="points-breakdown">
          <div class="breakdown-header">
            <h3>이명관 님의 R 포인트</h3>
          </div>
          
          <div class="breakdown-items">
            <div class="breakdown-item">
              <span>사용 가능한 R 포인트</span>
              <span class="amount">43,500</span>
            </div>
            <div class="breakdown-item">
              <span>차감 R 포인트</span>
              <span class="amount">-5,000</span>
            </div>
            <div class="breakdown-item total">
              <span>잔액 R 포인트</span>
              <span class="amount highlight">38,500</span>
            </div>
          </div>
        </div>

        <button class="submit-button" @click="handleConfirmJoin">
          R 포인트 지급하기
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getCalendarList } from '@/api/manager/calendar';
import { ChannelModule } from '@/store/modules/channel';
import moment from 'moment';

@Component({
  name: 'Calendar',
  components: {
    CommunitySidebar,
  },
  filters: {
    formatEventDate(date: string) {
      return moment(date).format('M/D (ddd)');
    },
    formatEventTime(date: string) {
      return moment(date).format('A h:mm');
    },
  },
})
export default class extends Vue {
  private loading = false;
  private eventList: any[] = [];
  private createEventModalVisible = false;
  private joinEventModalVisible = false;
  private selectedEvent: any = {};

  // Like and comment state management
  private likedEvents: { [key: number]: boolean } = {};
  private likeCounts: { [key: number]: number } = {};
  private showCommentsForEvent: { [key: number]: boolean } = {};
  private commentInputs: { [key: number]: string } = {};

  private eventForm = {
    title: '',
    description: '',
    date: '',
    startTime: '',
    endTime: '',
    location: '',
    type: 'free',
    points: '',
  };

  private eventTypes = [
    { label: '무료', value: 'free' },
    { label: '유료', value: 'paid' },
    { label: '획득', value: 'earn' },
  ];

  mounted() {
    this.fetchEventList();
  }

  private async fetchEventList() {
    this.loading = true;
    try {
      const res = await getCalendarList({
        channelUid: ChannelModule.selectedChannel.uid,
      });
      this.eventList = res.data.content || [];
      
      // If no data from API, use dummy data
      if (this.eventList.length === 0) {
        this.eventList = this.getDummyEventList();
      }
    } catch (error) {
      console.error('Error fetching calendar list:', error);
      // Use dummy data on error
      this.eventList = this.getDummyEventList();
    } finally {
      this.loading = false;
    }
  }

  private getDummyEventList() {
    return [
      {
        idx: 1,
        title: '산청 등산 모임',
        description: '산청의 아름다운 산을 함께 등산하며 건강한 하루를 보내요! 초보자도 환영합니다.',
        start: '2025-01-15T15:00:00',
        end: '2025-01-15T17:00:00',
        location: '산청의료협동조합 본관 2층',
        writer: '',
        points: 5000,
        likeCount: 126,
        commentCount: 14,
        type: 'paid',
      },
      {
        idx: 2,
        title: '건강 강좌: 겨울철 건강관리',
        description: '겨울철 면역력을 높이는 방법과 건강한 생활습관에 대해 배워봅시다.',
        start: '2025-01-18T14:00:00',
        end: '2025-01-18T16:00:00',
        location: '산청의료협동조합 강당',
        writer: '김민수',
        points: 0,
        likeCount: 89,
        commentCount: 7,
        type: 'free',
      },
      {
        idx: 3,
        title: '지역 농산물 직거래 장터',
        description: '우리 지역에서 생산된 신선한 농산물을 직접 만나보세요. R포인트 획득 기회!',
        start: '2025-01-20T10:00:00',
        end: '2025-01-20T15:00:00',
        location: '산청 중앙시장',
        writer: '이영희',
        points: 3000,
        likeCount: 245,
        commentCount: 32,
        type: 'earn',
      },
      {
        idx: 4,
        title: '요가 클래스',
        description: '몸과 마음의 건강을 위한 요가 수업. 초급반과 중급반으로 나누어 진행됩니다.',
        start: '2025-01-22T09:00:00',
        end: '2025-01-22T10:30:00',
        location: '산청 커뮤니티 센터',
        writer: '박지연',
        points: 2000,
        likeCount: 67,
        commentCount: 5,
        type: 'paid',
      },
      {
        idx: 5,
        title: '마을 청소 봉사활동',
        description: '깨끗한 우리 마을을 만들기 위한 청소 봉사활동에 함께해요. 참여자 모두에게 R포인트 지급!',
        start: '2025-01-25T08:00:00',
        end: '2025-01-25T11:00:00',
        location: '산청 마을회관 앞',
        writer: '최동욱',
        points: 5000,
        likeCount: 156,
        commentCount: 18,
        type: 'earn',
      },
      {
        idx: 6,
        title: '전통 음식 만들기 체험',
        description: '할머니의 손맛을 배우는 전통 음식 만들기 체험 프로그램입니다.',
        start: '2025-01-28T13:00:00',
        end: '2025-01-28T16:00:00',
        location: '산청 문화센터',
        writer: '정숙자',
        points: 8000,
        likeCount: 198,
        commentCount: 25,
        type: 'paid',
      },
      {
        idx: 7,
        title: '무료 건강검진',
        description: '산청의료협동조합에서 제공하는 무료 건강검진 이벤트입니다. 선착순 50명!',
        start: '2025-02-01T09:00:00',
        end: '2025-02-01T17:00:00',
        location: '산청의료협동조합',
        writer: '의료진',
        points: 0,
        likeCount: 312,
        commentCount: 45,
        type: 'free',
      },
      {
        idx: 8,
        title: '어린이 독서 교실',
        description: '책과 친해지는 시간! 초등학생을 위한 독서 토론 프로그램입니다.',
        start: '2025-02-05T15:00:00',
        end: '2025-02-05T17:00:00',
        location: '산청 도서관',
        writer: '김선생',
        points: 0,
        likeCount: 78,
        commentCount: 9,
        type: 'free',
      },
    ];
  }

  private handleWriteModal() {
    this.createEventModalVisible = true;
  }

  private handleJoinEvent(event: any) {
    this.selectedEvent = event;
    this.joinEventModalVisible = true;
  }

  private handleCreateEvent() {
    this.$message.success('일정이 생성되었습니다!');
    this.createEventModalVisible = false;
    this.eventForm = {
      title: '',
      description: '',
      date: '',
      startTime: '',
      endTime: '',
      location: '',
      type: 'free',
      points: '',
    };
  }

  private handleConfirmJoin() {
    this.$message.success('일정에 참여하였습니다!');
    this.joinEventModalVisible = false;
  }

  // Like functionality
  private toggleLike(eventIdx: number) {
    const isCurrentlyLiked = this.likedEvents[eventIdx] || false;
    this.$set(this.likedEvents, eventIdx, !isCurrentlyLiked);
    
    // Update like count
    const currentCount = this.likeCounts[eventIdx] || this.getInitialLikeCount(eventIdx);
    this.$set(this.likeCounts, eventIdx, isCurrentlyLiked ? currentCount - 1 : currentCount + 1);
  }

  private isLiked(eventIdx: number): boolean {
    return this.likedEvents[eventIdx] || false;
  }

  private getLikeCount(eventIdx: number): number {
    if (this.likeCounts[eventIdx] !== undefined) {
      return this.likeCounts[eventIdx];
    }
    return this.getInitialLikeCount(eventIdx);
  }

  private getInitialLikeCount(eventIdx: number): number {
    const event = this.eventList.find(e => e.idx === eventIdx);
    return event?.likeCount || 0;
  }

  // Comment functionality
  private toggleComments(eventIdx: number) {
    this.$set(this.showCommentsForEvent, eventIdx, !this.showCommentsForEvent[eventIdx]);
  }

  private shouldShowComments(eventIdx: number): boolean {
    return this.showCommentsForEvent[eventIdx] || false;
  }

  private handleCommentSubmit(eventIdx: number) {
    const commentText = this.commentInputs[eventIdx];
    if (!commentText || commentText.trim() === '') {
      this.$message.warning('댓글 내용을 입력해주세요.');
      return;
    }

    // In a real app, this would call an API to submit the comment
    this.$message.success('댓글이 등록되었습니다!');
    
    // Clear the input
    this.$set(this.commentInputs, eventIdx, '');
    
    // Optionally increment comment count
    const event = this.eventList.find(e => e.idx === eventIdx);
    if (event) {
      event.commentCount = (event.commentCount || 0) + 1;
    }
  }
}
</script>

<style scoped lang="scss">
.calendar-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.calendar-main {
  margin-left: 267px;
  padding: 50px 80px;
  flex: 1;
  min-height: calc(100vh - 124px);
}

.empty-state {
  display: flex;
  // justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 12px;
  padding: 60px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FAFAFA;
  min-width: 600px;
  width: 100%;
  transition: border-color 0.3s ease;

  &:hover {
    border-color: #D0D0D0;
  }
}

.empty-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.empty-subtitle {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.events-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.event-card-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;

  &:not(:last-child) {
    margin-bottom: 16px;
  }
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 8px;
}

.event-date-info {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.event-date {
  display: flex;
  align-items: center;
  gap: 8px;

  svg {
    flex-shrink: 0;
  }

  .date-text {
    color: #000;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 22px;
    font-weight: 700;
    line-height: 100%;
  }
}

.event-meta {
  display: flex;
  align-items: flex-start;
  gap: 36px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  white-space: nowrap;

  svg {
    flex-shrink: 0;
    width: 24px;
    height: 24px;
  }
}

.points-badge {
  display: flex;
  width: 24px;
  height: 24px;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border-radius: 50%;
  background: rgba(7, 61, 255, 0.08);
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
}

.points-value {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 100%;
}

.join-button {
  display: flex;
  width: 252px;
  height: 60px;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border-radius: 10px;
  border: 1px solid #073DFF;
  background: transparent;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
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

.event-content {
  display: flex;
  align-items: stretch;
  gap: 40px;
  position: relative;
  min-height: 200px;
}

.timeline-connector {
  width: 12px;
  border-right: 2px solid #EBEBEB;
  flex-shrink: 0;
  position: relative;
  align-self: stretch;

  &::before {
    content: '';
    position: absolute;
    right: -2px;
    top: 0;
    width: 2px;
    height: 100%;
    background: linear-gradient(180deg, #EBEBEB 0%, rgba(235, 235, 235, 0.3) 100%);
  }
  
/*   &::after {
    content: '';
    position: absolute;
    right: -6px;
    top: 0;
    width: 10px;
    height: 10px;
    background: #073DFF;
    border-radius: 50%;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  } */
}

.event-card {
  display: flex;
  flex-direction: column;
  gap: 32px;
  flex: 1;
  padding: 40px;
  margin-bottom: 64px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    border-color: #D0D0D0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .user-avatar {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    overflow: hidden;

    svg {
      display: block;
      width: 100%;
      height: 100%;
    }
  }

  .user-name {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    line-height: 100%;
  }
}

.event-details {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.event-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
  word-break: keep-all;
  text-align: left;
}

.event-description {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 22px;
  font-weight: 400;
  line-height: 150%;
  margin: 0;
  word-break: keep-all;
  text-align: left;
}

.event-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 18px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  transition: all 0.2s ease;

  svg {
    flex-shrink: 0;
    transition: all 0.2s ease;
  }

  &:hover {
    color: #073DFF;
    cursor: pointer;

    svg {
      opacity: 0.8;
    }
  }

  &.liked {
    color: #073DFF;
    font-weight: 600;

    .stat-count {
      color: #073DFF;
    }
  }

  &.active {
    color: #073DFF;
    font-weight: 600;

    .stat-count {
      color: #073DFF;
    }
  }
}

.stat-count {
  transition: color 0.2s ease;
}

.stat-divider {
  width: 1px;
  height: 14px;
  background: #EBEBEB;
}

/* Comments Section */
.comments-section {
  margin-top: 20px;
  padding: 24px;
  background: #F9FAFB;
  border-radius: 10px;
  border: 1px solid #E5E7EB;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.comment-item {
  background: #FFF;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  transition: all 0.2s ease;

  &:hover {
    border-color: #D1D5DB;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .user-avatar {
      flex-shrink: 0;

      svg {
        display: block;
      }
    }

    .user-name {
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 18px;
      font-weight: 600;
      line-height: 100%;
    }
  }
}

.comment-date {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 100%;
}

.comment-text {
  color: #374151;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  word-break: keep-all;
}

.comment-input-area {
  display: flex;
  gap: 12px;
  align-items: center;
}

.comment-input {
  flex: 1;
  height: 48px;
  padding: 0 16px;
  border-radius: 8px;
  border: 1px solid #D1D5DB;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #9CA3AF;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #9CA3AF;
  }
}

.comment-submit-btn {
  height: 48px;
  padding: 0 24px;
  border-radius: 8px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;

  &:hover {
    background: #0530CC;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background: #D1D5DB;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

/* Modal Styles */
::v-deep .create-event-modal {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog {
    border-radius: 12px;
  }

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

::v-deep .join-event-modal {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog {
    border-radius: 12px;
  }

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
    width: 100%;
  }

  @media screen and (max-width: 768px) {
    width: 100% !important;
    margin: 0 !important;
    margin-top: auto !important;
    border-radius: 10px 10px 0 0;
    max-width: 100vw;
    position: fixed;
    bottom: 0;
    left: 0;
    animation: slideUp 0.3s ease-out;

    .el-dialog__body {
      padding: 40px 20px;
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Fix z-index to appear above header */
::v-deep .el-dialog__wrapper {
  z-index: 9999 !important;

  @media screen and (max-width: 768px) {
    .join-event-modal {
      display: flex;
      align-items: flex-end;
    }
  }
}

.modal-content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
  position: relative;
}

.write-event-btn-fixed {
  display: none;
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 56px;
  height: 56px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 50%;
  color: #FFF;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-icon {
    display: block;
  }

  &:hover {
    background: #0530CC;
    transform: scale(1.1);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: scale(0.95);
  }
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
  color: #888;
  transition: all 0.2s ease;

  &:hover {
    color: #000;
    transform: rotate(90deg);
  }
}

.modal-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;
  text-align: center;
  word-break: keep-all;
}

.modal-subtitle {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  margin-top: -30px;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.form-input {
  height: 52px;
  padding: 0 16px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #B0B0B0;
  }
}

.form-textarea {
  min-height: 104px;
  padding: 16px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
  resize: vertical;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #B0B0B0;
  }
}

.datetime-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.date-picker {
  width: 208px;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 5px;
  flex: 1;
}

.time-picker {
  flex: 1;
}

.event-type-buttons {
  display: flex;
  gap: 20px;
}

.event-type-btn {
  width: 100px;
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
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover:not(.active) {
    border-color: #073DFF;
    background: #FAFAFA;
    transform: translateY(-1px);
  }

  &.active {
    background: #073DFF;
    border-color: #073DFF;
    color: #FFF;
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }
}

.submit-button {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: none;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    background: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.35);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }
}

.points-breakdown {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: stretch;
  padding: 30px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #FFF;
}

.breakdown-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEBEB;

  h3 {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 700;
    line-height: 150%;
    margin: 0;
  }
}

.breakdown-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;

  span {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 150%;
  }

  .amount {
    font-size: 22px;
    font-weight: 700;
    transition: color 0.2s ease;

    &.highlight {
      color: #073DFF;
    }
  }

  &.total {
    padding-top: 16px;
    margin-top: 8px;
    border-top: 1px solid #EBEBEB;
  }
}

/* Responsive Design */
@media screen and (max-width: 1600px) {
  .calendar-main {
    padding: 50px 60px;
  }
}

@media screen and (max-width: 1366px) {
  .calendar-main {
    padding: 48px 40px;
  }

  .event-title {
    font-size: 32px;
  }

  .event-description {
    font-size: 20px;
  }
}

@media screen and (max-width: 1024px) {
  .calendar-main {
    margin-left: 0;
    padding: 40px;
  }

  .event-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .join-button {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .calendar-main {
    padding: 32px 24px;
  }

  .event-meta {
    flex-wrap: wrap;
    gap: 16px;
  }

  .event-card {
    padding: 32px 24px;
    gap: 24px;
  }

  .event-title {
    font-size: 28px;
  }

  .event-description {
    font-size: 18px;
    line-height: 160%;
  }

  .event-details {
    gap: 24px;
  }

  .event-stats {
    margin-top: 12px;
  }

  .write-event-btn-fixed {
    display: flex;
  }
}

@media screen and (max-width: 600px) {
  .calendar-main {
    padding: 20px 16px;
    margin-top: 100px;
  }

  .empty-box {
    min-width: auto;
    width: 100%;
    padding: 40px 24px;
  }

  .empty-title {
    font-size: 22px;
  }

  .empty-subtitle {
    font-size: 14px;
  }

  .modal-content-wrapper {
    gap: 30px;
  }

  ::v-deep .create-event-modal {
    width: 100% !important;
    margin: 0 !important;
    
    .el-dialog {
      width: 100% !important;
      height: 100%;
      margin: 0 !important;
      max-height: 100vh;
    }

    .el-dialog__body {
      padding: 24px;
      height: 100vh;
      overflow-y: auto;
    }
  }

  ::v-deep .join-event-modal{
    width: 100% !important;
    margin: 0 !important;
    
    .el-dialog {
      width: 100% !important;
      height: 100%;
      margin: 0 !important;
      max-height: 100vh;
    }

    .el-dialog__body {
      padding: 24px;
      height: 70vh;
      overflow-y: auto;
    }
  }

  .event-meta {
    gap: 12px;
  }

  .meta-item {
    font-size: 16px;

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .join-button {
    width: 100%;
    height: 52px;
    font-size: 16px;
  }
}

/* Element UI Custom Styling */
::v-deep .el-date-picker,
::v-deep .el-time-select {
  .el-input__inner {
    height: 52px;
    border-radius: 10px;
    border: 1px solid #CECECE;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    color: #222;
    transition: all 0.3s ease;

    &::placeholder {
      color: #888;
    }

    &:hover {
      border-color: #B0B0B0;
    }

    &:focus {
      border-color: #073DFF;
      box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
    }
  }

  .el-input__icon {
    line-height: 52px;
  }
}

::v-deep .el-picker-panel {
  border-radius: 10px;
  border: 1px solid #EBEBEB;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

::v-deep .el-date-picker__header-label,
::v-deep .el-picker-panel__icon-btn {
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
}

::v-deep .el-date-table td.available:hover,
::v-deep .el-month-table td .cell:hover,
::v-deep .el-year-table td .cell:hover {
  color: #073DFF;
}

::v-deep .el-date-table td.today span {
  color: #073DFF;
  font-weight: 700;
}

::v-deep .el-date-table td.current:not(.disabled) span {
  background-color: #073DFF;
}

::v-deep .el-time-select-item.selected:not(.disabled) {
  background-color: #073DFF;
  font-weight: 700;
}

::v-deep .el-time-select-item:hover {
  background-color: rgba(7, 61, 255, 0.1);
}

/* Smooth page transitions */
.events-container {
  animation: fadeIn 0.5s ease-out;
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

.event-card-wrapper {
  animation: slideIn 0.4s ease-out backwards;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Stagger animation for event cards */
.event-card-wrapper:nth-child(1) { animation-delay: 0.05s; }
.event-card-wrapper:nth-child(2) { animation-delay: 0.1s; }
.event-card-wrapper:nth-child(3) { animation-delay: 0.15s; }
.event-card-wrapper:nth-child(4) { animation-delay: 0.2s; }
.event-card-wrapper:nth-child(5) { animation-delay: 0.25s; }
.event-card-wrapper:nth-child(6) { animation-delay: 0.3s; }
.event-card-wrapper:nth-child(7) { animation-delay: 0.35s; }
.event-card-wrapper:nth-child(8) { animation-delay: 0.4s; }

/* Selection and interaction states */
::selection {
  background-color: rgba(7, 61, 255, 0.2);
  color: inherit;
}

::-moz-selection {
  background-color: rgba(7, 61, 255, 0.2);
  color: inherit;
}

/* Scrollbar styling */
.calendar-main::-webkit-scrollbar {
  width: 8px;
}

.calendar-main::-webkit-scrollbar-track {
  background: #F5F5F5;
  border-radius: 4px;
}

.calendar-main::-webkit-scrollbar-thumb {
  background: #CECECE;
  border-radius: 4px;
  transition: background 0.2s ease;
}

.calendar-main::-webkit-scrollbar-thumb:hover {
  background: #B0B0B0;
}

/* Focus visible for accessibility */
.join-button:focus-visible,
.submit-button:focus-visible,
.event-type-btn:focus-visible {
  outline: 2px solid #073DFF;
  outline-offset: 2px;
}

.form-input:focus-visible,
.form-textarea:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.15);
}
</style>
