<template>
  <div class="member-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'member-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="member-management-main">
      <div class="page-header">
        <h1 class="page-title">회원 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'members' }"
          @click="activeTab = 'members'"
        >
          회원 목록
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'pending' }"
          @click="activeTab = 'pending'"
        >
          가입 승인 대기
          <span v-if="pendingMembers.length > 0" class="badge">{{ pendingMembers.length }}</span>
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'permissions' }"
          @click="activeTab = 'permissions'"
        >
          권한 관리
        </button>
      </div>

      <!-- 회원 목록 -->
      <div v-if="activeTab === 'members'" class="tab-content">
        <!-- 검색 및 필터 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
          <!-- 회원 초대하기 버튼 숨김 처리 -->
          <!-- <button class="invite-btn" @click="openInviteModal">
            <i class="el-icon-plus"></i>
            회원 초대하기
          </button> -->
        </div>

        <!-- 회원 테이블 -->
        <div v-if="loadingMembers" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredMembers.length > 0" class="members-table-wrapper">
          <table class="members-table">
            <thead>
              <tr>
                <th>회원명</th>
                <th>이메일</th>
                <th>가입일</th>
                <th>상태</th>
                <th>관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in filteredMembers" :key="member.uid">
                <td>
                  <div class="member-info">
                    <div class="member-avatar-placeholder">
                      <i class="el-icon-user"></i>
                    </div>
                    <span class="member-name">{{ member.name }}</span>
                  </div>
                </td>
                <td>{{ member.email }}</td>
                <td>{{ formatDate(member.joinedAt) }}</td>
                <td>
                  <span class="status-badge" :class="member.status">
                    {{ getStatusLabel(member.status) }}
                  </span>
                </td>
                <td>
                  <button 
                    class="action-btn danger-outline" 
                    @click="confirmBanMember(member)"
                  >
                    추방
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="empty-container">
          <p>회원이 없습니다</p>
        </div>
      </div>

      <!-- 가입 승인 대기 -->
      <div v-if="activeTab === 'pending'" class="tab-content">
        <div v-if="loadingPending" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="pendingMembers.length > 0" class="pending-list">
          <div 
            v-for="pending in pendingMembers" 
            :key="pending.uid"
            class="pending-card"
          >
            <div class="pending-header">
              <div class="member-info">
                <div class="member-avatar-large-placeholder">
                  <i class="el-icon-user"></i>
                </div>
                <div class="member-details">
                  <h3 class="member-name">{{ pending.name }}</h3>
                  <p class="member-email">{{ pending.email }}</p>
                </div>
              </div>
              <span class="pending-date">{{ formatDate(pending.requestedAt) }}</span>
            </div>

            <div v-if="pending.message" class="pending-message">
              <p class="message-label">가입 신청 메시지</p>
              <p class="message-content">{{ pending.message }}</p>
            </div>

            <div class="pending-actions">
              <button 
                class="action-btn secondary" 
                @click="openSurveyModal(pending)"
              >
                설문조사 확인
              </button>
              <button 
                class="action-btn secondary" 
                @click="rejectMember(pending)"
              >
                거절
              </button>
              <button 
                class="action-btn primary" 
                @click="approveMember(pending)"
              >
                승인
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>승인 대기 중인 회원이 없습니다</p>
        </div>
      </div>

      <!-- 권한 관리 -->
      <div v-if="activeTab === 'permissions'" class="tab-content">
        <!-- 검색 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="permissionSearchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
        </div>

        <div v-if="loadingPermissions" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredPermissionMembers.length > 0" class="permissions-list">
          <div 
            v-for="member in filteredPermissionMembers" 
            :key="member.uid"
            class="permission-card"
          >
            <div class="permission-header">
              <div class="member-info">
                <div class="member-avatar-placeholder">
                  <i class="el-icon-user"></i>
                </div>
                <div class="member-details">
                  <h3 class="member-name">{{ member.name }}</h3>
                  <p class="member-email">{{ member.email }}</p>
                </div>
              </div>
              <button 
                class="action-btn primary" 
                @click="openPermissionModal(member)"
              >
                <i class="el-icon-edit"></i>
                권한 수정
              </button>
            </div>

            <div class="permission-tags">
              <span 
                v-for="perm in member.permissions" 
                :key="perm"
                class="permission-tag"
              >
                {{ getPermissionLabel(perm) }}
              </span>
              <span v-if="member.permissions.length === 0" class="no-permission">
                부여된 권한 없음
              </span>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>회원이 없습니다</p>
        </div>
      </div>
    </div>

    <!-- 회원 초대 모달 -->
    <el-dialog
      :visible.sync="inviteModalVisible"
      width="600px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="invite-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closeInviteModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">회원 초대하기</h3>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">초대할 이메일</label>
            <input 
              v-model="inviteForm.email" 
              type="email" 
              placeholder="이메일 주소를 입력해주세요"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">초대 가능한 공간 선택</label>
            <div class="space-checkboxes">
              <label 
                v-for="space in availableSpaces" 
                :key="space.uid"
                class="space-checkbox-item"
              >
                <input 
                  type="checkbox" 
                  :value="space.uid"
                  v-model="inviteForm.spaceUids"
                />
                <span class="space-checkbox-label">{{ space.name }}</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">초대 메시지 (선택)</label>
            <textarea 
              v-model="inviteForm.message" 
              placeholder="초대 메시지를 입력해주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>

        <button 
          class="submit-btn" 
          @click="submitInvite"
          :disabled="!inviteForm.email || submittingInvite"
        >
          {{ submittingInvite ? '전송 중...' : '초대 보내기' }}
        </button>
      </div>
    </el-dialog>

    <!-- 설문조사 확인 모달 -->
    <el-dialog
      :visible.sync="surveyModalVisible"
      width="700px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="survey-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closeSurveyModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">가입 설문조사</h3>

        <div v-if="selectedPending" class="survey-content">
          <div class="survey-applicant">
            <div class="member-avatar-large-placeholder">
              <i class="el-icon-user"></i>
            </div>
            <div class="applicant-info">
              <h4>{{ selectedPending.name }}</h4>
              <p>{{ selectedPending.email }}</p>
            </div>
          </div>

          <div class="survey-questions">
            <div class="survey-item">
              <h4 class="survey-question">1. 커뮤니티 참여 동기가 무엇인가요?</h4>
              <p class="survey-answer">{{ selectedPending.survey?.question1 || '응답 없음' }}</p>
            </div>

            <div class="survey-item">
              <h4 class="survey-question">2. 주로 관심 있는 분야는 무엇인가요?</h4>
              <p class="survey-answer">{{ selectedPending.survey?.question2 || '응답 없음' }}</p>
            </div>

            <div class="survey-item">
              <h4 class="survey-question">3. 어떤 활동에 참여하고 싶으신가요?</h4>
              <p class="survey-answer">{{ selectedPending.survey?.question3 || '응답 없음' }}</p>
            </div>

            <div class="survey-item">
              <h4 class="survey-question">4. 추가로 전하고 싶은 말씀이 있나요?</h4>
              <p class="survey-answer">{{ selectedPending.survey?.question4 || '응답 없음' }}</p>
            </div>
          </div>

          <div class="survey-actions">
            <button 
              class="action-btn secondary" 
              @click="rejectFromSurvey"
            >
              거절
            </button>
            <button 
              class="action-btn primary" 
              @click="approveFromSurvey"
            >
              승인
            </button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 권한 수정 모달 -->
    <el-dialog
      :visible.sync="permissionModalVisible"
      width="650px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="permission-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closePermissionModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">회원 권한 설정</h3>

        <div v-if="selectedMember" class="permission-member-info">
          <div class="member-avatar-placeholder">
            <i class="el-icon-user"></i>
          </div>
          <div class="member-details">
            <h4>{{ selectedMember.name }}</h4>
            <p>{{ selectedMember.email }}</p>
          </div>
        </div>

        <div class="permission-section">
          <p class="permission-section-label">부여할 권한을 선택해주세요 (다중 선택 가능)</p>
          
          <div class="permission-checkboxes">
            <label 
              v-for="perm in availablePermissions" 
              :key="perm.value"
              class="permission-checkbox-item"
            >
              <input 
                type="checkbox" 
                :value="perm.value"
                v-model="permissionForm.permissions"
              />
              <span class="permission-checkbox-label">
                {{ perm.label }}
              </span>
            </label>
          </div>
        </div>

        <button 
          class="submit-btn" 
          @click="submitPermission"
          :disabled="submittingPermission"
        >
          {{ submittingPermission ? '저장 중...' : '권한 저장' }}
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

// 임시 인터페이스
interface Member {
  uid: string;
  name: string;
  email: string;
  profileImage: string;
  joinedAt: string;
  status: 'active' | 'banned';
  permissions: string[];
}

interface PendingMember {
  uid: string;
  name: string;
  email: string;
  profileImage: string;
  requestedAt: string;
  message?: string;
  survey?: {
    question1: string;
    question2: string;
    question3: string;
    question4: string;
  };
}

interface Space {
  uid: string;
  name: string;
  spaceType: string;
}

@Component({
  name: 'MemberManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeTab: 'members' | 'pending' | 'permissions' = 'members';
  private loadingMembers = false;
  private loadingPending = false;
  private loadingPermissions = false;
  private searchKeyword = '';
  private permissionSearchKeyword = '';

  // 회원 목록 (덤프 데이터)
  private members: Member[] = [
    {
      uid: 'mem-1',
      name: '정이욥',
      email: 'kim@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=1',
      joinedAt: '2025-01-15T10:30:00',
      status: 'active',
      permissions: ['write', 'comment', 'like', 'marketplace'],
    },
    {
      uid: 'mem-2',
      name: '오형래',
      email: 'lee@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=2',
      joinedAt: '2025-02-20T14:20:00',
      status: 'active',
      permissions: ['write', 'comment', 'like', 'space_join', 'chat_join', 'schedule_attend', 'course_attend'],
    },
    {
      uid: 'mem-3',
      name: '배은별',
      email: 'park@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=3',
      joinedAt: '2025-03-10T09:15:00',
      status: 'active',
      permissions: ['write', 'comment', 'like'],
    },
    {
      uid: 'mem-4',
      name: '이은경',
      email: 'choi@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=4',
      joinedAt: '2025-04-05T16:45:00',
      status: 'active',
      permissions: ['write', 'comment', 'like', 'space_join', 'chat_join', 'marketplace', 'schedule_attend', 'schedule_create', 'course_attend', 'offline_point_deduct'],
    },
    {
      uid: 'mem-5',
      name: '이주현',
      email: 'jung@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=5',
      joinedAt: '2025-05-12T11:20:00',
      status: 'banned',
      permissions: [],
    },
  ];

  // 가입 승인 대기 (덤프 데이터)
  private pendingMembers: PendingMember[] = [
    {
      uid: 'pend-1',
      name: '정이욥2',
      email: 'kang@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=6',
      requestedAt: '2025-12-24T10:30:00',
      message: '친구 추천으로 가입하게 되었습니다. 잘 부탁드립니다!',
      survey: {
        question1: '지역 사회 공동체 활동에 관심이 있어서 가입하게 되었습니다.',
        question2: '문화/예술 분야와 지역 봉사 활동에 관심이 많습니다.',
        question3: '오프라인 모임과 온라인 강좌에 적극적으로 참여하고 싶습니다.',
        question4: '좋은 인연들을 만나고 싶습니다. 잘 부탁드립니다!',
      },
    },
    {
      uid: 'pend-2',
      name: '홍길동',
      email: 'yoon@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=7',
      requestedAt: '2025-12-25T14:20:00',
      message: '지역 공동체에 관심이 많아 가입 신청합니다.',
      survey: {
        question1: '이웃과 함께하는 지역 커뮤니티를 만들고 싶어서 가입했습니다.',
        question2: '스포츠/레저, 취미생활 공유에 관심 있습니다.',
        question3: '동호회 활동과 정기 모임에 참여하고 싶습니다.',
        question4: '함께 성장하는 커뮤니티를 기대합니다.',
      },
    },
    {
      uid: 'pend-3',
      name: '한소희',
      email: 'han@example.com',
      profileImage: 'https://i.pravatar.cc/150?img=8',
      requestedAt: '2025-12-26T09:15:00',
      survey: {
        question1: '창업과 비즈니스 네트워킹 기회를 찾기 위해 가입했습니다.',
        question2: '비즈니스와 IT/기술 분야에 관심이 많습니다.',
        question3: '세미나와 워크샵에 참여하고 멘토링을 받고 싶습니다.',
        question4: '좋은 비즈니스 인맥을 쌓고 싶습니다.',
      },
    },
  ];

  // 초대 가능한 공간 (덤프 데이터)
  private availableSpaces: Space[] = [
    {
      uid: 'space-1',
      name: '자유 게시판',
      spaceType: 'BOARD',
    },
    {
      uid: 'space-2',
      name: '공지사항',
      spaceType: 'BOARD',
    },
    {
      uid: 'space-3',
      name: '전체 채팅방',
      spaceType: 'CHAT',
    },
    {
      uid: 'space-4',
      name: '친목 채팅방',
      spaceType: 'CHAT',
    },
  ];

  // 초대 모달
  private inviteModalVisible = false;
  private submittingInvite = false;
  private inviteForm = {
    email: '',
    spaceUids: [] as string[],
    message: '',
  };

  // 설문조사 모달
  private surveyModalVisible = false;
  private selectedPending: PendingMember | null = null;

  // 권한 모달
  private permissionModalVisible = false;
  private submittingPermission = false;
  private selectedMember: Member | null = null;
  private permissionForm = {
    permissions: [] as string[],
  };

  // 사용 가능한 권한 목록
  private availablePermissions = [
    { value: 'write', label: '글쓰기' },
    { value: 'comment', label: '댓글달기' },
    { value: 'like', label: '좋아요' },
    { value: 'space_join', label: '공간참여대상' },
    { value: 'chat_join', label: '채팅참여대상' },
    { value: 'marketplace', label: '장터이용권한' },
    { value: 'schedule_attend', label: '일정참여' },
    { value: 'schedule_create', label: '일정생성' },
    { value: 'course_attend', label: '강좌수강' },
    { value: 'offline_point_deduct', label: '오프라인매장포인트차감권한' },
  ];

  get filteredMembers() {
    if (!this.searchKeyword) return this.members;
    const keyword = this.searchKeyword.toLowerCase();
    return this.members.filter(member => 
      member.name.toLowerCase().includes(keyword) || 
      member.email.toLowerCase().includes(keyword)
    );
  }

  get filteredPermissionMembers() {
    if (!this.permissionSearchKeyword) return this.members;
    const keyword = this.permissionSearchKeyword.toLowerCase();
    return this.members.filter(member => 
      member.name.toLowerCase().includes(keyword) || 
      member.email.toLowerCase().includes(keyword)
    );
  }

  mounted() {
    // 실제 API 연동시 데이터 로드
    // this.loadMembers();
    // this.loadPendingMembers();
    // this.loadAvailableSpaces();
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
  }

  private getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      active: '활동중',
      banned: '추방됨',
    };
    return labels[status] || status;
  }

  // 회원 추방 확인
  private async confirmBanMember(member: Member) {
    try {
      const confirm = await this.$confirm(
        `${member.name}님을 추방하시겠습니까? 추방된 회원은 커뮤니티에 접근할 수 없습니다.`,
        '회원 추방',
        {
          confirmButtonText: '추방',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );

      if (confirm) {
        await this.banMember(member);
      }
    } catch (error) {
      if (error === 'cancel') return;
    }
  }

  // 회원 추방
  private async banMember(member: Member) {
    try {
      // TODO: 실제 API 호출
      // await banMember(member.uid);

      // 임시 딜레이
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 상태 업데이트
      member.status = 'banned';

      this.$message.success(`${member.name}님이 추방되었습니다`);
    } catch (error: any) {
      const message = error.response?.data?.message || '회원 추방에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 가입 승인
  private async approveMember(pending: PendingMember) {
    try {
      // TODO: 실제 API 호출
      // await approveMember(pending.uid);

      // 임시 딜레이
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 목록에서 제거
      const index = this.pendingMembers.findIndex(m => m.uid === pending.uid);
      if (index > -1) {
        this.pendingMembers.splice(index, 1);
      }

      this.$message.success(`${pending.name}님의 가입을 승인했습니다`);
    } catch (error: any) {
      const message = error.response?.data?.message || '가입 승인에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 가입 거절
  private async rejectMember(pending: PendingMember) {
    try {
      const confirm = await this.$confirm(
        `${pending.name}님의 가입 신청을 거절하시겠습니까?`,
        '가입 거절',
        {
          confirmButtonText: '거절',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );

      if (confirm) {
        // TODO: 실제 API 호출
        // await rejectMember(pending.uid);

        // 임시 딜레이
        await new Promise(resolve => setTimeout(resolve, 1000));

        // 목록에서 제거
        const index = this.pendingMembers.findIndex(m => m.uid === pending.uid);
        if (index > -1) {
          this.pendingMembers.splice(index, 1);
        }

        this.$message.success(`${pending.name}님의 가입 신청을 거절했습니다`);
      }
    } catch (error: any) {
      if (error === 'cancel') return;
      const message = error.response?.data?.message || '가입 거절에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 초대 모달 열기
  private openInviteModal() {
    this.inviteForm = {
      email: '',
      spaceUids: [],
      message: '',
    };
    this.inviteModalVisible = true;
  }

  // 초대 모달 닫기
  private closeInviteModal() {
    this.inviteModalVisible = false;
  }

  // 초대 보내기
  private async submitInvite() {
    if (!this.inviteForm.email) return;

    try {
      this.submittingInvite = true;

      // TODO: 실제 API 호출
      // await inviteMember({
      //   email: this.inviteForm.email,
      //   spaceUids: this.inviteForm.spaceUids,
      //   message: this.inviteForm.message,
      // });

      // 임시 딜레이
      await new Promise(resolve => setTimeout(resolve, 1000));

      this.$message.success('초대를 보냈습니다');
      this.closeInviteModal();
    } catch (error: any) {
      const message = error.response?.data?.message || '초대 전송에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingInvite = false;
    }
  }

  // 설문조사 모달 열기
  private openSurveyModal(pending: PendingMember) {
    this.selectedPending = pending;
    this.surveyModalVisible = true;
  }

  // 설문조사 모달 닫기
  private closeSurveyModal() {
    this.surveyModalVisible = false;
    this.selectedPending = null;
  }

  // 설문조사 모달에서 승인
  private async approveFromSurvey() {
    if (!this.selectedPending) return;
    
    const pending = this.selectedPending;
    this.closeSurveyModal();
    await this.approveMember(pending);
  }

  // 설문조사 모달에서 거절
  private async rejectFromSurvey() {
    if (!this.selectedPending) return;
    
    const pending = this.selectedPending;
    this.closeSurveyModal();
    await this.rejectMember(pending);
  }

  // 권한 라벨 가져오기
  private getPermissionLabel(value: string): string {
    const perm = this.availablePermissions.find(p => p.value === value);
    return perm ? perm.label : value;
  }

  // 권한 모달 열기
  private openPermissionModal(member: Member) {
    this.selectedMember = member;
    this.permissionForm.permissions = [...member.permissions];
    this.permissionModalVisible = true;
  }

  // 권한 모달 닫기
  private closePermissionModal() {
    this.permissionModalVisible = false;
    this.selectedMember = null;
    this.permissionForm.permissions = [];
  }

  // 권한 저장
  private async submitPermission() {
    if (!this.selectedMember) return;

    try {
      this.submittingPermission = true;

      // TODO: 실제 API 호출
      // await updateMemberPermissions({
      //   memberUid: this.selectedMember.uid,
      //   permissions: this.permissionForm.permissions,
      // });

      // 임시 딜레이
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 로컬 데이터 업데이트
      this.selectedMember.permissions = [...this.permissionForm.permissions];

      this.$message.success('권한이 저장되었습니다');
      this.closePermissionModal();
    } catch (error: any) {
      const message = error.response?.data?.message || '권한 저장에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingPermission = false;
    }
  }
}
</script>

<style scoped lang="scss">
.member-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.member-management-main {
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
  transition: all 0.2s;
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

  .badge {
    background: #FF5858;
    color: #FFF;
    font-size: 12px;
    font-weight: 700;
    padding: 2px 8px;
    border-radius: 12px;
    min-width: 20px;
    text-align: center;
  }
}

// 탭 컨텐츠
.tab-content {
  min-height: 400px;
}

// 필터 바
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  height: 48px;
  background: #FFF;
  border: 1px solid #CECECE;
  border-radius: 8px;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #073DFF;
  }

  i {
    color: #888;
    font-size: 18px;
  }
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  color: #222;

  &::placeholder {
    color: #AAA;
  }
}

.invite-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 48px;
  padding: 0 24px;
  background: #073DFF;
  color: #FFF;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0535e6;
    transform: translateY(-2px);
  }

  i {
    font-size: 18px;
  }
}

// 로딩
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;

  i {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 회원 테이블
.members-table-wrapper {
  background: #FFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.members-table {
  width: 100%;
  border-collapse: collapse;

  thead {
    background: #F8F9FA;

    th {
      padding: 16px 20px;
      text-align: left;
      color: #666;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
      font-weight: 600;
      border-bottom: 1px solid #EBEBEB;
    }
  }

  tbody {
    tr {
      border-bottom: 1px solid #F0F0F0;
      transition: background 0.2s;

      &:hover {
        background: #F8F9FA;
      }

      &:last-child {
        border-bottom: none;
      }
    }

    td {
      padding: 16px 20px;
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
    }
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.member-avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5B8DEE 0%, #4A7DD9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 20px;
  flex-shrink: 0;
}

.member-avatar-large {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.member-avatar-large-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5B8DEE 0%, #4A7DD9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 28px;
  flex-shrink: 0;
  border: 2px solid #4A7DD9;
}

.member-name {
  color: #222;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;

  &.active {
    background: #E8F0FF;
    color: #073DFF;
  }

  &.banned {
    background: #FFE8E8;
    color: #FF5858;
  }
}

// 액션 버튼
.action-btn {
  height: 36px;
  padding: 16px;
  border: none;
  border-radius: 6px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

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
  }

  &.danger-outline {
    background: #FFF;
    color: #FF5858;
    border: 1px solid #FF5858;

    &:hover {
      background: #FF5858;
      color: #FFF;
    }
  }
}

// 가입 대기 목록
.pending-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.pending-card {
  background: #FFF;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .member-details {
    .member-name {
        text-align:left;
      font-size: 18px;
      margin: 0 0 4px 0;
    }

    .member-email {
      color: #888;
      font-size: 14px;
      margin: 0;
    }
  }
}

.pending-date {
  color: #888;
  font-size: 14px;
}

.pending-message {
  background: #F8F9FA;
  border-radius: 8px;
  text-align:left;

  .message-label {
    color: #666;
    font-size: 13px;
    font-weight: 600;
    margin: 0 0 8px 0;
  }

  .message-content {
    color: #222;
    font-size: 14px;
    line-height: 1.6;
    margin: 0;
  }
}

.pending-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

// 빈 상태
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;
  font-size: 16px;
}

// 초대 모달
::v-deep .invite-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.modal-close-btn {
  position: absolute;
  right: 20px;
  top: 20px;
  width: 32px;
  height: 32px;
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
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0;
  text-align: center;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
}

.form-input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  box-sizing: border-box;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

// 공간 체크박스
.space-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #F8F9FA;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.space-checkbox-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }

  input[type="checkbox"] {
    width: 20px;
    height: 20px;
    cursor: pointer;
  }

  .space-checkbox-label {
    color: #222;
    font-size: 15px;
    font-weight: 500;
    user-select: none;
  }
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
  }
}

// 설문조사 모달 스타일
.survey-modal {
  .modal-content {
    max-height: 80vh;
    overflow-y: auto;
  }

  .survey-content {
    margin-top: 20px;
  }

  .survey-applicant {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background-color: #f5f8fa;
    border-radius: 12px;
    margin-bottom: 24px;

    .member-avatar-large-placeholder {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: linear-gradient(135deg, #5B8DEE 0%, #4A7DD9 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #FFF;
      font-size: 28px;
      flex-shrink: 0;
      border: 2px solid #4A7DD9;
    }

    .applicant-info {
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: #1a1a1a;
        margin: 0 0 4px 0;
      }

      p {
        font-size: 14px;
        color: #666;
        margin: 0;
      }
    }
  }

  .survey-questions {
    display: flex;
    flex-direction: column;
    gap: 24px;
    margin-bottom: 32px;
  }

  .survey-item {
    .survey-question {
      font-size: 16px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 12px 0;
    }

    .survey-answer {
      font-size: 15px;
      color: #333;
      line-height: 1.6;
      padding: 16px;
      background-color: #f9fafb;
      border-radius: 8px;
      margin: 0;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }

  .survey-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #e5e7eb;

    .action-btn {
      width: 100%;
    }
  }
}

// 권한 관리 스타일
.permissions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.permission-card {
  background: #FFF;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.permission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .member-details {
    .member-name {
      text-align: left;
      font-size: 18px;
      margin: 0 0 4px 0;
    }

    .member-email {
      color: #888;
      font-size: 14px;
      margin: 0;
      text-align: left;
    }
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 32px;
}

.permission-tag {
  display: inline-block;
  padding: 6px 14px;
  background: #E8F0FF;
  color: #073DFF;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}

.no-permission {
  color: #AAA;
  font-size: 14px;
  font-style: italic;
}

// 권한 모달 스타일
::v-deep .permission-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.permission-member-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 12px;
  margin-bottom: 24px;

  .member-details {
    h4 {
      font-size: 18px;
      font-weight: 600;
      color: #222;
      margin: 0 0 4px 0;
      text-align: left;
    }

    p {
      font-size: 14px;
      color: #888;
      margin: 0;
      text-align: left;
    }
  }
}

.permission-section {
  margin-bottom: 24px;
}

.permission-section-label {
  color: #222;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
}

.permission-checkboxes {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.permission-checkbox-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FFF;
  border: 2px solid #E0E0E0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    background: #F5F8FF;
  }

  input[type="checkbox"] {
    width: 20px;
    height: 20px;
    cursor: pointer;
    flex-shrink: 0;
  }

  .permission-checkbox-label {
    color: #222;
    font-size: 15px;
    font-weight: 500;
    user-select: none;
    flex: 1;
  }
}

@media screen and (max-width: 1024px) {
  .member-management-main {
    margin-left: 0;
    padding: 30px 20px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;

    .search-box {
      max-width: none;
    }
  }

  .members-table-wrapper {
    overflow-x: auto;
  }

  .members-table {
    min-width: 700px;
  }
}

@media screen and (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .tabs-section {
    gap: 10px;
  }

  .tab-btn {
    padding: 12px 16px;
    font-size: 16px;
  }

  .pending-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .pending-actions {
    width: 100%;

    .action-btn {
      flex: 1;
    }
  }

  .permission-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .action-btn {
      width: 100%;
      justify-content: center;
    }
  }

  .permission-checkboxes {
    grid-template-columns: 1fr;
  }

  .survey-modal {
    .survey-applicant {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .survey-actions {
      flex-direction: column;

      .action-btn {
        width: 100%;
      }
    }
  }
}

// 425px 이하 모바일 반응형
@media screen and (max-width: 425px) {
  .member-management-main {
    padding: 20px 16px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .tabs-section {
    gap: 0;
    margin-bottom: 24px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .tab-btn {
    flex: 1;
    min-width: max-content;
    padding: 12px 10px;
    font-size: 13px;
    gap: 4px;
    white-space: nowrap;

    .badge {
      font-size: 10px;
      padding: 2px 6px;
      min-width: 16px;
    }
  }

  .tab-content {
    min-height: 300px;
  }

  // 필터 바
  .filter-bar {
    margin-bottom: 20px;
    gap: 12px;
  }

  .search-box {
    height: 40px;
    padding: 0 12px;
    gap: 10px;

    i {
      font-size: 16px;
    }
  }

  .search-input {
    font-size: 14px;
  }

  .invite-btn {
    height: 40px;
    padding: 0 16px;
    font-size: 14px;

    i {
      font-size: 16px;
    }
  }

  // 로딩
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

  // 회원 테이블
  .members-table-wrapper {
    border-radius: 10px;
    width: 100%;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background: #ccc;
      border-radius: 2px;
    }
  }

  .members-table {
    min-width: 550px;
    width: max-content;

    thead th {
      padding: 12px 12px;
      font-size: 12px;
      white-space: nowrap;
    }

    tbody td {
      padding: 12px 12px;
      font-size: 12px;
      white-space: nowrap;
    }
  }

  .member-avatar-placeholder {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }

  .member-name {
    font-size: 13px;
  }

  .status-badge {
    padding: 3px 8px;
    font-size: 11px;
  }

  .action-btn {
    height: 32px;
    padding: 0 12px;
    font-size: 12px;
  }

  // 가입 대기 목록
  .pending-list {
    gap: 16px;
  }

  .pending-card {
    padding: 16px;
    border-radius: 10px;
  }

  .pending-header {
    margin-bottom: 12px;

    .member-details {
      .member-name {
        font-size: 16px;
      }

      .member-email {
        font-size: 12px;
      }
    }
  }

  .member-avatar-large-placeholder {
    width: 48px;
    height: 48px;
    font-size: 22px;
  }

  .pending-date {
    font-size: 12px;
  }

  .pending-message {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 12px;

    .message-label {
      font-size: 12px;
      margin-bottom: 6px;
    }

    .message-content {
      font-size: 13px;
    }
  }

  .pending-actions {
    gap: 8px;

    .action-btn {
      height: 36px;
      font-size: 13px;
    }
  }

  // 빈 상태
  .empty-container {
    padding: 60px 20px;
    font-size: 14px;
  }

  // 권한 관리
  .permissions-list {
    gap: 16px;
  }

  .permission-card {
    padding: 16px;
    border-radius: 10px;
  }

  .permission-header {
    margin-bottom: 16px;

    .member-details {
      .member-name {
        font-size: 16px;
      }

      .member-email {
        font-size: 12px;
      }
    }
  }

  .permission-tags {
    gap: 6px;
  }

  .permission-tag {
    padding: 4px 10px;
    font-size: 11px;
    border-radius: 4px;
  }

  .no-permission {
    font-size: 12px;
  }

  // 모달 공통 스타일
  ::v-deep .invite-modal,
  ::v-deep .survey-modal,
  ::v-deep .permission-modal {
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

  .form-section {
    gap: 16px;
  }

  .form-group {
    gap: 6px;
  }

  .form-label {
    font-size: 14px;
  }

  .form-input {
    height: 44px;
    padding: 0 12px;
    font-size: 14px;
    border-radius: 6px;
  }

  .form-textarea {
    padding: 10px 12px;
    font-size: 14px;
    border-radius: 6px;
  }

  .space-checkboxes {
    padding: 12px;
    gap: 10px;
    max-height: 160px;
  }

  .space-checkbox-item {
    gap: 10px;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
    }

    .space-checkbox-label {
      font-size: 14px;
    }
  }

  .submit-btn {
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
  }

  // 설문조사 모달
  .survey-modal {
    .survey-applicant {
      padding: 16px;
      gap: 12px;

      .member-avatar-large-placeholder {
        width: 48px;
        height: 48px;
        font-size: 22px;
      }

      .applicant-info {
        h4 {
          font-size: 16px;
        }

        p {
          font-size: 12px;
        }
      }
    }

    .survey-questions {
      gap: 20px;
      margin-bottom: 24px;
    }

    .survey-item {
      .survey-question {
        font-size: 14px;
        margin-bottom: 10px;
      }

      .survey-answer {
        font-size: 13px;
        padding: 12px;
        border-radius: 6px;
      }
    }

    .survey-actions {
      margin-top: 24px;
      padding-top: 20px;
      gap: 10px;

      .action-btn {
        height: 44px;
        font-size: 14px;
      }
    }
  }

  // 권한 모달
  .permission-member-info {
    padding: 16px;
    gap: 12px;
    margin-bottom: 20px;
    border-radius: 10px;

    .member-avatar-placeholder {
      width: 40px;
      height: 40px;
      font-size: 18px;
    }

    .member-details {
      h4 {
        font-size: 16px;
      }

      p {
        font-size: 12px;
      }
    }
  }

  .permission-section {
    margin-bottom: 20px;
  }

  .permission-section-label {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .permission-checkboxes {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 12px;
    max-height: 280px;
  }

  .permission-checkbox-item {
    padding: 10px;
    gap: 10px;
    border-radius: 6px;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
    }

    .permission-checkbox-label {
      font-size: 13px;
    }
  }
}
</style>
