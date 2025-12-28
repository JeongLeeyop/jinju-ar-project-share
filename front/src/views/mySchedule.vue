<template>
  <div class="my-schedule-page">
    <CommunitySidebar 
      :selectedSpaceId="'my-schedule'" 
      @space-select="handleSpaceSelect"
    />

    <div class="my-schedule-main">
      <div class="page-header">
        <h1 class="page-title">내 일정 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'registered' }"
          @click="activeTab = 'registered'"
        >
          등록 일정
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'participated' }"
          @click="activeTab = 'participated'"
        >
          참여 일정
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'point-history' }"
          @click="activeTab = 'point-history'"
        >
          포인트 내역
        </button>
      </div>

      <!-- 등록 일정 -->
      <div v-if="activeTab === 'registered'" class="tab-content">
        <div v-if="loadingRegistered" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="registeredSchedules.length > 0" class="schedules-list">
          <div
            v-for="schedule in registeredSchedules"
            :key="schedule.idx"
            class="schedule-card registered-card"
          >
            <div class="schedule-header">
              <h3 class="schedule-title" @click="openScheduleDetail(schedule)">{{ schedule.title }}</h3>
              <span class="status-badge" :class="getStatusClass(schedule)">
                {{ getStatusLabel(schedule) }}
              </span>
            </div>
            <div class="schedule-info">
              <p class="schedule-date">
                <i class="el-icon-time"></i>
                {{ formatScheduleDate(schedule) }}
              </p>
              <p class="schedule-location">
                <i class="el-icon-location"></i>
                {{ schedule.location }}
              </p>
              <p class="schedule-participants">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" style="vertical-align: middle; margin-right: 4px;">
                  <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  <mask id="mask0_schedule_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="16" height="16">
                    <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_schedule_user)">
                    <rect x="1.78" y="9.33" width="12.44" height="14.22" rx="6.22" fill="#999"/>
                    <circle cx="8" cy="4.89" r="3.11" fill="#999"/>
                  </g>
                </svg>
                참여자: {{ schedule.participantCount }} / {{ schedule.maxParticipants }}명
              </p>
            </div>
            <div class="schedule-actions">
              <button class="action-btn primary" @click.stop="viewParticipants(schedule)">
                참여자
              </button>
              <button class="action-btn secondary" @click.stop="editSchedule(schedule)">
                수정
              </button>
              <button class="action-btn danger" @click.stop="cancelSchedule(schedule)">
                취소
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>등록한 일정이 없습니다</p>
        </div>
      </div>

      <!-- 참여 일정 -->
      <div v-if="activeTab === 'participated'" class="tab-content">
        <div class="notice-message">
          <i class="el-icon-info"></i>
          <span>참여 일정은 1일 전까지 취소가 가능합니다.</span>
        </div>

        <div v-if="loadingParticipated" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="participatedSchedules.length > 0" class="schedules-list">
          <div
            v-for="schedule in participatedSchedules"
            :key="schedule.idx"
            class="schedule-card participated-card"
          >
            <div class="schedule-header">
              <h3 class="schedule-title" @click="openScheduleDetail(schedule)">{{ schedule.title }}</h3>
              <span class="status-badge" :class="getStatusClass(schedule)">
                {{ getStatusLabel(schedule) }}
              </span>
            </div>
            <div class="schedule-info">
              <p class="schedule-date">
                <i class="el-icon-time"></i>
                {{ formatScheduleDate(schedule) }}
              </p>
              <p class="schedule-location">
                <i class="el-icon-location"></i>
                {{ schedule.location }}
              </p>
              <p class="schedule-host">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg" style="vertical-align: middle; margin-right: 4px;">
                  <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  <mask id="mask0_schedule_host" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="16" height="16">
                    <circle cx="8" cy="8" r="8" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_schedule_host)">
                    <rect x="1.78" y="9.33" width="12.44" height="14.22" rx="6.22" fill="#999"/>
                    <circle cx="8" cy="4.89" r="3.11" fill="#999"/>
                  </g>
                </svg>
                주최자: {{ schedule.hostName }}
              </p>
              <p class="participation-date">
                참여 신청일: {{ formatDate(schedule.participatedAt) }}
              </p>
            </div>
            <div class="schedule-actions">
              <button 
                v-if="checkCanCancel(schedule)"
                class="action-btn danger" 
                @click.stop="cancelParticipation(schedule)"
              >
                참여 취소 (무료)
              </button>
              <button 
                v-else-if="schedule.status === 'upcoming'"
                class="action-btn secondary" 
                @click.stop="requestCancelParticipation(schedule)"
                disabled
                title="취소 가능 기한이 지났습니다"
              >
                참여 취소 불가
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>참여한 일정이 없습니다</p>
        </div>
      </div>

      <!-- 포인트 내역 -->
      <div v-if="activeTab === 'point-history'" class="tab-content">
        <div v-if="loadingPointHistory" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="pointHistory.length > 0" class="point-history-list">
          <div class="point-summary">
            <div class="summary-card">
              <h4>총 획득 포인트</h4>
              <p class="point-value earned">+{{ totalEarnedPoints.toLocaleString() }}</p>
            </div>
            <div class="summary-card">
              <h4>총 사용 포인트</h4>
              <p class="point-value spent">-{{ totalSpentPoints.toLocaleString() }}</p>
            </div>
            <div class="summary-card">
              <h4>현재 포인트</h4>
              <p class="point-value current">{{ currentPoints.toLocaleString() }}</p>
            </div>
          </div>

          <div class="history-table">
            <table>
              <thead>
                <tr>
                  <th>날짜</th>
                  <th>내역</th>
                  <th>설명</th>
                  <th>포인트</th>
                  <th>잔액</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="history in pointHistory" 
                  :key="history.idx"
                  class="history-row"
                >
                  <td>{{ formatDate(history.createdAt) }}</td>
                  <td>
                    <span class="history-type" :class="history.pointType">
                      {{ getHistoryTypeLabel(history.pointType) }}
                    </span>
                  </td>
                  <td>{{ history.description }}</td>
                  <td>
                    <span class="point-change" :class="{ positive: history.pointAmount > 0, negative: history.pointAmount < 0 }">
                      {{ history.pointAmount > 0 ? '+' : '' }}{{ history.pointAmount.toLocaleString() }}
                    </span>
                  </td>
                  <td>{{ history.currentBalance.toLocaleString() }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>포인트 내역이 없습니다</p>
        </div>
      </div>
    </div>

    <!-- 참여자 목록 모달 -->
    <el-dialog
      :visible.sync="participantsModalVisible"
      width="600px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="participants-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closeParticipantsModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">참여자 목록</h3>
        
        <div v-if="selectedSchedule" class="schedule-summary">
          <p class="summary-item">
            <span class="summary-label">일정명:</span>
            <span class="summary-value">{{ selectedSchedule.title }}</span>
          </p>
          <p class="summary-item">
            <span class="summary-label">참여자:</span>
            <span class="summary-value">{{ selectedSchedule.participantCount }} / {{ selectedSchedule.maxParticipants }}명</span>
          </p>
        </div>

        <div class="participants-list">
          <div 
            v-for="participant in participants" 
            :key="participant.idx"
            class="participant-item"
          >
            <div class="participant-info">
              <img 
                v-if="participant.userProfileImage" 
                :src="`${apiUrl}/attached-file/${participant.userProfileImage}`" 
                alt="프로필" 
                class="participant-avatar" 
              />
              <div v-else class="participant-avatar-placeholder">
                <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  <mask id="mask0_participant" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_participant)">
                    <rect x="4" y="21" width="28" height="32" rx="14" fill="#999"/>
                    <circle cx="18" cy="11" r="7" fill="#999"/>
                  </g>
                </svg>
              </div>
              <div class="participant-details">
                <p class="participant-name">{{ participant.userName }}</p>
                <p class="participant-phone">{{ participant.userPhone || '-' }}</p>
              </div>
            </div>
            <p class="participant-date">{{ formatDate(participant.createdAt) }}</p>
          </div>
        </div>

        <div class="modal-actions">
          <button class="modal-btn secondary" @click="closeParticipantsModal">
            닫기
          </button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from '@/views/components/communitySidebar.vue';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import { 
  getMyRegisteredSchedules, 
  getMyParticipatedSchedules, 
  cancelMyRegisteredSchedule,
  cancelCalendarEvent,
  getCalendarParticipants,
  MyScheduleItem
} from '@/api/manager/calendar';
import { getTargetUserPointHistory } from '@/api/point';
import { getUserInfo } from '@/api/user';
import { EventBus, EVENTS } from '@/utils/eventBus';

interface Schedule {
  idx: number;
  title: string;
  startDate: string;
  endDate: string;
  location: string;
  participantCount: number;
  maxParticipants: number;
  status: 'upcoming' | 'ongoing' | 'completed' | 'cancelled';
  eventType?: 'free' | 'paid' | 'earn';
  points?: number;
  hostUid?: string;
  hostName?: string;
  participatedAt?: string;
  cancelDeadline?: string;
  canCancel?: boolean;
}

interface PointHistory {
  idx: number;
  pointType: string;
  description: string;
  pointAmount: number;
  currentBalance: number;
  createdAt: string;
  referenceId?: string;
}

interface Participant {
  idx: number;
  userUid: string;
  userName: string;
  userPhone: string;
  userProfileImage: string;
  createdAt: string;
}

@Component({
  name: 'MySchedule',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeTab = 'registered';
  private loadingRegistered = false;
  private loadingParticipated = false;
  private loadingPointHistory = false;

  private registeredSchedules: Schedule[] = [];
  private participatedSchedules: Schedule[] = [];
  private pointHistory: PointHistory[] = [];
  private participants: Participant[] = [];

  private participantsModalVisible = false;
  private selectedSchedule: Schedule | null = null;
  
  private currentChannel: any = null;
  private currentPoints = 0;
  private apiUrl = process.env.VUE_APP_BASE_API;

  async created() {
    await this.initChannel();
  }

  /**
   * 채널 정보 초기화 및 데이터 로드
   */
  private async initChannel() {
    // domain 파라미터에서 채널 정보 로드
    const domain = this.$route.params.domain;
    if (!domain) {
      this.$message.error('채널 정보를 찾을 수 없습니다');
      return;
    }

    // ChannelModule에서 선택된 채널 가져오기
    if (ChannelModule.selectedChannel && ChannelModule.selectedChannel.uid) {
      this.currentChannel = ChannelModule.selectedChannel;
    } else {
      // 채널 정보가 없으면 domain으로 조회
      try {
        const { getChannelDomainDetail } = await import('@/api/channel');
        const res = await getChannelDomainDetail(domain);
        this.currentChannel = res.data;
        ChannelModule.setSelectedChannel(this.currentChannel);
      } catch (error) {
        console.error('채널 정보 조회 실패:', error);
        this.$message.error('채널 정보를 불러오는데 실패했습니다');
        return;
      }
    }

    // 현재 포인트 조회
    await this.loadCurrentPoints();

    // 데이터 로드
    this.loadRegisteredSchedules();
    this.loadParticipatedSchedules();
    this.loadPointHistory();
  }
  
  /**
   * channelUid getter - domain과 혼동 방지
   */
  get channelUid(): string {
    // 주의: domain이 아닌 실제 channel uid를 반환해야 함
    const uid = this.currentChannel?.uid || ChannelModule.selectedChannel?.uid;
    if (!uid) {
      console.warn('[mySchedule] channelUid is empty');
    }
    return uid || '';
  }

  @Watch('$route.params.domain')
  private async onDomainChange() {
    await this.initChannel();
  }

  /**
   * 현재 포인트 조회
   */
  private async loadCurrentPoints() {
    try {
      const res = await getUserInfo();
      this.currentPoints = res.data.point || 0;
    } catch (error) {
      console.error('포인트 조회 실패:', error);
    }
  }

  /**
   * 등록 일정 로드
   */
  private async loadRegisteredSchedules() {
    if (!this.channelUid) return;
    
    this.loadingRegistered = true;
    try {
      const res = await getMyRegisteredSchedules(this.channelUid);
      this.registeredSchedules = (res.data || []).map((item: any) => ({
        idx: item.idx,
        title: item.title,
        startDate: item.startDate,
        endDate: item.endDate,
        location: item.location || '장소 미정',
        participantCount: item.participantCount || 0,
        maxParticipants: item.maxParticipants || 0,
        status: item.status,
        eventType: item.eventType,
        points: item.points,
        hostUid: item.hostUid,
        hostName: item.hostName,
        canCancel: item.canCancel,
      }));
    } catch (error: any) {
      console.error('Failed to load registered schedules:', error);
      const message = error.response?.data?.message || '등록 일정을 불러오는데 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loadingRegistered = false;
    }
  }

  /**
   * 참여 일정 로드
   */
  private async loadParticipatedSchedules() {
    if (!this.channelUid) return;
    
    this.loadingParticipated = true;
    try {
      const res = await getMyParticipatedSchedules(this.channelUid);
      this.participatedSchedules = (res.data || []).map((item: any) => ({
        idx: item.idx,
        title: item.title,
        startDate: item.startDate,
        endDate: item.endDate,
        location: item.location || '장소 미정',
        participantCount: item.participantCount || 0,
        maxParticipants: item.maxParticipants || 0,
        status: item.status,
        eventType: item.eventType,
        points: item.points,
        hostUid: item.hostUid,
        hostName: item.hostName,
        participatedAt: item.participatedAt,
        cancelDeadline: item.cancelDeadline,
        canCancel: item.canCancel,
      }));
    } catch (error: any) {
      console.error('Failed to load participated schedules:', error);
      const message = error.response?.data?.message || '참여 일정을 불러오는데 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loadingParticipated = false;
    }
  }

  /**
   * 포인트 내역 로드
   */
  private async loadPointHistory() {
    if (!this.channelUid || !UserModule.isLogin) return;
    
    this.loadingPointHistory = true;
    try {
      // 사용자 본인의 포인트 내역 조회
      const userUid = UserModule.userId;
      const res = await getTargetUserPointHistory({
        targetUserUid: userUid,
        channelUid: this.channelUid,
        page: 0,
        size: 100,
      });
      this.pointHistory = (res.data?.content || []).filter((item: any) => 
        item.pointType?.includes('SCHEDULE')
      );
    } catch (error: any) {
      console.error('Failed to load point history:', error);
      // 권한이 없어도 무시 (본인 포인트 내역은 별도 API가 필요할 수 있음)
    } finally {
      this.loadingPointHistory = false;
    }
  }

  /**
   * 포인트 계산 - 총 획득 포인트
   */
  get totalEarnedPoints(): number {
    return this.pointHistory
      .filter((h) => h.pointAmount > 0)
      .reduce((sum, h) => sum + h.pointAmount, 0);
  }

  /**
   * 포인트 계산 - 총 사용 포인트
   */
  get totalSpentPoints(): number {
    return Math.abs(
      this.pointHistory
        .filter((h) => h.pointAmount < 0)
        .reduce((sum, h) => sum + h.pointAmount, 0),
    );
  }

  // 일정 상태 레이블
  private getStatusLabel(schedule: Schedule): string {
    const statusMap: { [key: string]: string } = {
      upcoming: '예정',
      ongoing: '진행중',
      completed: '완료',
      cancelled: '취소',
    };
    return statusMap[schedule.status] || '알 수 없음';
  }

  // 일정 상태 클래스
  private getStatusClass(schedule: Schedule): string {
    return schedule.status;
  }

  // 일정 날짜 포맷
  private formatScheduleDate(schedule: Schedule): string {
    const startDate = new Date(schedule.startDate);
    const endDate = new Date(schedule.endDate);
    const dateStr = `${startDate.getFullYear()}.${String(startDate.getMonth() + 1).padStart(2, '0')}.${String(startDate.getDate()).padStart(2, '0')}`;
    const startTimeStr = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;
    const endTimeStr = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;
    return `${dateStr} ${startTimeStr} - ${endTimeStr}`;
  }

  // 날짜 포맷
  private formatDate(dateString: string): string {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  }

  // 포인트 내역 타입 레이블
  private getHistoryTypeLabel(type: string): string {
    const typeMap: { [key: string]: string } = {
      SCHEDULE: '일정 참여',
      SCHEDULE_REFUND: '참여 취소 환불',
      SCHEDULE_CANCELLED_REFUND: '일정 취소 환불',
      SCHEDULE_EARN: '일정 참여 획득',
      SCHEDULE_PARTICIPATION: '일정 참여',
      SCHEDULE_CANCEL: '일정 취소',
    };
    return typeMap[type] || type;
  }

  // 취소 가능 여부 체크
  private checkCanCancel(schedule: Schedule): boolean {
    // 백엔드에서 전달받은 canCancel 사용
    return schedule.canCancel === true;
  }

  // 일정 상세 열기
  private openScheduleDetail(schedule: Schedule) {
    // 일정 상세 페이지로 이동
    this.$router.push({
      name: 'CalendarDetail',
      params: {
        domain: this.$route.params.domain,
        idx: String(schedule.idx),
      },
    }).catch(() => {});
  }

  // 참여자 보기
  private async viewParticipants(schedule: Schedule) {
    this.selectedSchedule = schedule;
    
    try {
      const res = await getCalendarParticipants(schedule.idx);
      this.participants = (res.data || []).map((p: any) => ({
        idx: p.idx,
        userUid: p.userUid,
        userName: p.userName,
        userPhone: p.userPhone || '',
        userProfileImage: p.userProfileImage,
        createdAt: p.createdAt,
      }));
      this.participantsModalVisible = true;
    } catch (error: any) {
      console.error('참여자 목록 조회 실패:', error);
      const message = error.response?.data?.message || '참여자 목록을 불러오는데 실패했습니다';
      this.$message.error(message);
    }
  }

  // 참여자 모달 닫기
  private closeParticipantsModal() {
    this.participantsModalVisible = false;
    this.selectedSchedule = null;
    this.participants = [];
  }

  // 일정 수정
  private editSchedule(schedule: Schedule) {
    // 일정 수정 페이지로 이동
    this.$router.push({
      name: 'CalendarEdit',
      params: {
        domain: this.$route.params.domain,
        idx: String(schedule.idx),
      },
    }).catch(() => {
      // 라우트가 없으면 모달 등으로 대체
      this.$message.info(`일정 수정: ${schedule.title}`);
    });
  }

  // 내가 등록한 일정 취소
  private async cancelSchedule(schedule: Schedule) {
    try {
      await this.$confirm(
        `"${schedule.title}" 일정을 취소하시겠습니까?\n\n참여자가 있는 경우 포인트가 환불됩니다.`,
        '일정 취소',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );

      await cancelMyRegisteredSchedule(schedule.idx);
      this.$message.success('일정이 취소되었습니다');
      
      // 목록 새로고침
      await this.loadRegisteredSchedules();
      
      // 헤더 포인트 갱신 (환불이 있을 수 있으므로)
      EventBus.$emit(EVENTS.POINTS_UPDATED);
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('일정 취소 실패:', error);
        const message = error.response?.data?.message || '일정 취소에 실패했습니다';
        this.$message.error(message);
      }
    }
  }

  // 참여 취소
  private async cancelParticipation(schedule: Schedule) {
    try {
      await this.$confirm(
        `"${schedule.title}" 참여를 취소하시겠습니까?\n\n취소 기한 내 취소 시 포인트가 전액 환불됩니다.`,
        '참여 취소',
        {
          confirmButtonText: '확인',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );

      await cancelCalendarEvent(schedule.idx);
      this.$message.success('참여가 취소되었습니다. 포인트가 환불됩니다.');
      
      // 목록 새로고침
      await this.loadParticipatedSchedules();
      await this.loadPointHistory();
      await this.loadCurrentPoints();
      
      // 헤더 포인트 갱신
      EventBus.$emit(EVENTS.POINTS_UPDATED);
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('참여 취소 실패:', error);
        const message = error.response?.data?.message || '참여 취소에 실패했습니다';
        this.$message.error(message);
      }
    }
  }

  // 참여 취소 요청 (기한 초과)
  private requestCancelParticipation(schedule: Schedule) {
    this.$message.warning('취소 가능 기한이 지났습니다');
  }

  // 공간 선택 핸들러
  private handleSpaceSelect(spaceId: string) {
    // Handle sidebar space selection if needed
    console.log('Space selected:', spaceId);
  }
}
</script>

<style lang="scss" scoped>
.my-schedule-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.my-schedule-main {
  flex: 1;
  margin-left: 270px;
  padding: 140px 40px 40px;

  @media screen and (max-width: 1024px) {
    margin-left: 240px;
    padding: 140px 30px 20px;
  }
  
  @media screen and (max-width: 768px) {
    margin-left: 0;
    padding: 120px 30px 20px;
  }

  @media screen and (max-width: 500px) {
    padding: 100px 20px 20px;
  }
}

.page-header {
  margin-bottom: 24px;

  .page-title {
    color: #000;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 28px;
    font-weight: 700;
    line-height: 1.3;
    margin: 0;

    @media (max-width: 768px) {
      font-size: 24px;
    }
  }
}

// 탭 스타일
.tabs-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  border-bottom: 2px solid #EBEBEB;

  @media (max-width: 768px) {
    gap: 8px;
  }

  .tab-btn {
    padding: 12px 20px;
    background: none;
    border: none;
    color: #666;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    position: relative;
    transition: color 0.2s;

    @media (max-width: 768px) {
      padding: 10px 14px;
      font-size: 14px;
    }

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
}

.tab-content {
  min-height: 400px;
}

// 안내 메시지
.notice-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  background: #E8F0FF;
  border-left: 4px solid #073DFF;
  border-radius: 8px;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;

  i {
    font-size: 16px;
  }

  @media (max-width: 768px) {
    font-size: 13px;
    padding: 10px 12px;
  }
}

// 로딩 상태
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #666;

  i {
    font-size: 48px;
    margin-bottom: 16px;
  }

  p {
    font-size: 14px;
    margin: 0;
  }
}

// 빈 상태
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #666;
  font-size: 14px;
}

// 일정 리스트
.schedules-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex-wrap: wrap;
  gap: 20px;
}

.schedule-card {
  flex: 0 1 calc(100% / 3 - 14px);
  background: #FFF;
  border-radius: 12px;
  padding: 20px;
  padding: 40px 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  @media (max-width: 768px) {
    padding: 16px;
  }
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 12px;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 8px;
  }
}

.schedule-title {
  flex: 1;
  min-height: 70px;
  font-size: 28px;
  line-height: 1.4;
  text-align: left;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  cursor: pointer;
  flex: 1;
  transition: color 0.2s;
//   transition: color 0.2s;

  &:hover {
    color: #073DFF;
  }

  @media (max-width: 768px) {
    font-size: 16px;
  }
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;

  &.upcoming {
    background: #E8F0FF;
    color: #073DFF;
  }

  &.ongoing {
    background: #FFF8E1;
    color: #F57C00;
  }

  &.completed {
    background: #E8F5E9;
    color: #388E3C;
  }

  &.cancelled {
    background: #FFEBEE;
    color: #C62828;
  }
}

.schedule-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 16px;

  p {
    margin: 0;
    color: #666;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-size: 20px;
    display: flex;
    align-items: center;
    gap: 8px;

    i {
      font-size: 14px;
      color: #999;
    }
  }
}

.schedule-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;

  @media (max-width: 768px) {
    flex-direction: column;
  }
}

.action-btn {
  flex: 1;
  height: 52px;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  @media (max-width: 768px) {
    flex: none;
    width: 100%;
    height: 40px;
  }

  &.primary {
    background: #073DFF;
    color: #FFF;

    &:hover {
      background: #0535e6;
    }
  }

  &.secondary {
    background: #F5F5F5;
    color: #666;
    border: 1px solid #CECECE;

    &:hover {
      background: #E0E0E0;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;

      &:hover {
        background: #F5F5F5;
      }
    }
  }

  &.danger {
    background: #FFEBEE;
    color: #C62828;
    border: 1px solid #FFCDD2;

    &:hover {
      background: #FFCDD2;
    }
  }
}

// 포인트 내역
.point-history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.point-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }

  .summary-card {
    background: #FFF;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    h4 {
      margin: 0 0 12px;
      color: #666;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
      font-weight: 500;
    }

    .point-value {
      margin: 0;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 32px;
      font-weight: 700;
      line-height: 1.2;

      &.earned {
        color: #4CAF50;
      }

      &.spent {
        color: #F44336;
      }

      &.current {
        color: #073DFF;
      }
    }
  }
}

.history-table {
  background: #FFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  table {
    width: 100%;
    border-collapse: collapse;

    thead {
      background: #F8F9FA;

      th {
        padding: 12px;
        text-align: left;
        color: #666;
        font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
        font-size: 13px;
        font-weight: 600;
        border-bottom: 2px solid #EBEBEB;
      }
    }

    tbody {
      tr {
        border-bottom: 1px solid #F0F0F0;
        transition: background 0.2s;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background: #F8F9FA;
        }

        td {
          padding: 12px;
          color: #333;
          font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
          font-size: 14px;
        }
      }
    }
  }

  @media (max-width: 768px) {
    overflow-x: auto;

    table {
      min-width: 600px;
    }
  }
}

.history-type {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;

  &.participation {
    background: #E8F0FF;
    color: #073DFF;
  }

  &.cancel {
    background: #FFEBEE;
    color: #C62828;
  }

  &.refund {
    background: #FFF8E1;
    color: #F57C00;
  }
}

.point-change {
  font-weight: 700;

  &.positive {
    color: #4CAF50;
  }

  &.negative {
    color: #F44336;
  }
}

// 모달 스타일
::v-deep .participants-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 30px;
    position: relative;
  }
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.modal-close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  width: 28px;
  height: 28px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #666;
  transition: color 0.2s;

  &:hover {
    color: #333;
  }
}

.modal-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0;
  text-align: center;
}

.schedule-summary {
  background: #F8F9FA;
  padding: 16px;
  border-radius: 8px;

  .summary-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 0 10px 0;
    font-size: 14px;

    &:last-child {
      margin-bottom: 0;
    }

    .summary-label {
      color: #666;
      font-weight: 500;
    }

    .summary-value {
      color: #222;
      font-weight: 600;
    }
  }
}

.participants-list {
  max-height: 400px;
  overflow-y: auto;
}

.participant-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #F0F0F0;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #F8F9FA;
  }
}

.participant-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.participant-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background: #E8F0FF;
}

.participant-details {
  .participant-name {
    margin: 0 0 4px;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 600;
  }

  .participant-phone {
    margin: 0;
    color: #888;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 13px;
  }
}

.participant-date {
  margin: 0;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 13px;
}

.modal-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.modal-btn {
  height: 40px;
  padding: 0 24px;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &.secondary {
    background: #F5F5F5;
    color: #666;
    border: 1px solid #CECECE;

    &:hover {
      background: #E0E0E0;
    }
  }
}

@media screen and (max-width: 1400px) {
  .schedule-card {flex: 0 1 calc(100% / 2 - 10px);}
}

@media screen and (max-width: 1024px) {
  .schedule-title {font-size: 24px; min-height: 50px;}
}

// 425px 이하 모바일 반응형
@media (max-width: 425px) {
  .page-header {
    margin-bottom: 24px;

    .page-title {
      font-size: 20px;
    }
  }

  .tabs-section {
    gap: 0;
    margin-bottom: 24px;

    .tab-btn {
      flex: 1;
      padding: 12px 8px;
      font-size: 13px;
      text-align: center;
      white-space: nowrap;
    }
  }

  .tab-content {
    min-height: 300px;
  }

  .notice-message {
    padding: 12px 14px;
    font-size: 13px;
    margin-bottom: 20px;
    flex-wrap: wrap;

    i {
      font-size: 16px;
    }
  }

  .loading-container {
    padding: 60px 20px;

    i {
      font-size: 36px;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
    }
  }

  .empty-container {
    padding: 60px 20px;
    font-size: 14px;
  }

  .schedules-list {
    gap: 16px;
  }

  .schedule-card {
    padding: 16px;
    border-radius: 10px;
  }

  .schedule-header {
    margin-bottom: 12px;
    gap: 8px;
  }

  .schedule-title {
    font-size: 16px;
  }

  .status-badge {
    padding: 4px 10px;
    font-size: 11px;
  }

  .schedule-info {
    gap: 6px;
    margin-bottom: 14px;

    p {
      font-size: 13px;
      gap: 6px;

      i {
        font-size: 14px;
      }
    }
  }

  .schedule-actions {
    gap: 8px;
  }

  .action-btn {
    height: 36px;
    font-size: 13px;
    border-radius: 6px;
  }

  // 포인트 내역
  .point-history-list {
    gap: 16px;
  }

  .point-summary {
    gap: 12px;

    .summary-card {
      padding: 16px;
      border-radius: 10px;

      h4 {
        font-size: 12px;
        margin-bottom: 8px;
      }

      .point-value {
        font-size: 24px;
      }
    }
  }

  .history-table {
    border-radius: 10px;

    table {
      min-width: 500px;

      thead th {
        padding: 12px 10px;
        font-size: 12px;
      }

      tbody tr td {
        padding: 12px 10px;
        font-size: 12px;
      }
    }
  }

  .history-type {
    padding: 3px 8px;
    font-size: 11px;
  }

  // 모달 스타일
  ::v-deep .participants-modal {
    width: 100% !important;
    max-width: 100vw;
    margin: 0 !important;
    height: 100%;
    border-radius: 0;

    .el-dialog__body {
      padding: 60px 16px 20px;
      height: 100%;
      overflow-y: auto;
    }
  }

  .modal-content {
    gap: 20px;
  }

  .modal-close-btn {
    right: 16px;
    top: 16px;
  }

  .modal-title {
    font-size: 20px;
  }

  .schedule-summary {
    padding: 16px;
    border-radius: 8px;

    .summary-item {
      margin-bottom: 10px;
      font-size: 14px;
    }
  }

  .participants-list {
    max-height: 320px;
  }

  .participant-item {
    padding: 12px;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .participant-avatar {
    width: 40px;
    height: 40px;
  }

  .participant-details {
    .participant-name {
      font-size: 14px;
    }

    .participant-phone {
      font-size: 12px;
    }
  }

  .participant-date {
    font-size: 12px;
    align-self: flex-end;
  }

  .modal-actions {
    margin-top: 8px;
  }

  .modal-btn {
    height: 44px;
    padding: 0 24px;
    font-size: 14px;
    border-radius: 6px;
  }
}
</style>
