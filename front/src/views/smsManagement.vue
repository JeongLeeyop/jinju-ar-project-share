<template>
  <div class="sms-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'sms-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="sms-management-main">
      <div class="page-header">
        <h1 class="page-title">문자 발송 관리</h1>
        <p class="page-description">오프라인 장터 회원들에게 문자를 발송할 수 있습니다.</p>
      </div>

    <!-- 회원 검색 섹션 -->
    <div class="search-section">
      <h2 class="section-title">
        <i class="el-icon-search"></i>
        회원 검색
      </h2>
      <div class="search-form">
        <el-input
          v-model="searchQuery"
          placeholder="회원 이름, 전화번호, 이메일로 검색"
          prefix-icon="el-icon-search"
          clearable
          @input="handleSearch"
        />
        <el-button
          type="primary"
          icon="el-icon-search"
          @click="handleSearch"
        >
          검색
        </el-button>
      </div>
    </div>

    <!-- 검색 결과 -->
    <div class="search-results">
      <div class="results-header">
        <h3 class="results-title">
          검색 결과 ({{ selectedMembers.length }} / {{ searchResults.length }}명 선택)
        </h3>
        <div class="results-actions">
          <el-button
            size="small"
            @click="selectAllMembers"
            :disabled="searchResults.length === 0"
          >
            전체 선택
          </el-button>
          <el-button
            size="small"
            @click="clearSelection"
            :disabled="selectedMembers.length === 0"
          >
            선택 해제
          </el-button>
        </div>
      </div>

      <div class="member-list">
        <div
          v-for="member in searchResults"
          :key="member.uid"
          class="member-item"
          :class="{ selected: isSelected(member.uid) }"
        >
          <el-checkbox
            :value="isSelected(member.uid)"
            @change="() => toggleMember(member)"
            @click.native.stop
          />
          <div class="member-info" @click="toggleMember(member)">
            <div class="member-avatar">
              <img v-if="member.iconFileUid" :src="`${apiUrl}/attached-file/${member.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
              <svg v-else width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                <mask id="mask0_sms_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="40" height="40">
                  <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_sms_member)">
                  <rect x="4.44" y="23.33" width="31.11" height="35.56" rx="15.56" fill="#F5F5F5"/>
                  <circle cx="20" cy="12.22" r="7.78" fill="#F5F5F5"/>
                </g>
              </svg>
            </div>
            <div class="member-details">
              <h4 class="member-name">{{ member.name }}</h4>
              <p class="member-contact">
                <span class="phone">{{ formatPhone(member.phone) }}</span>
                <span class="divider">|</span>
                <span class="email">{{ member.email }}</span>
              </p>
            </div>
          </div>
        </div>

        <div v-if="searchResults.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
            <mask id="mask0_empty_sms" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
              <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
            </mask>
            <g mask="url(#mask0_empty_sms)">
              <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
              <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
            </g>
          </svg>
          <p>검색 결과가 없습니다.</p>
        </div>
      </div>
    </div>

    <!-- 메시지 작성 섹션 -->
    <div class="message-section">
      <h2 class="section-title">
        <i class="el-icon-chat-line-square"></i>
        문자 메시지 작성
      </h2>

      <!-- 템플릿 선택 -->
      <div class="template-selector">
        <label class="field-label">메시지 템플릿</label>
        <el-select
          v-model="selectedTemplate"
          placeholder="템플릿을 선택하세요"
          @change="handleTemplateChange"
        >
          <el-option
            v-for="template in messageTemplates"
            :key="template.id"
            :label="template.name"
            :value="template.id"
          />
        </el-select>
      </div>

      <!-- 메시지 입력 -->
      <div class="message-input">
        <label class="field-label">
          메시지 내용
          <span class="char-count">
            {{ messageContent.length }} / 2000
          </span>
        </label>
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="8"
          placeholder="문자 메시지 내용을 입력하세요&#10;&#10;예시:&#10;[와로 커뮤니티]&#10;안녕하세요, 오프라인 장터 안내 드립니다.&#10;자세한 내용은 앱을 확인해주세요."
          maxlength="2000"
        />
      </div>

      <!-- 발송 예상 정보 -->
      <div class="send-info">
        <div class="info-item">
          <span class="info-label">발송 대상:</span>
          <span class="info-value">{{ selectedMembers.length }}명</span>
        </div>
      </div>

      <!-- 발송 버튼 -->
      <div class="send-actions">
        <el-button
          type="primary"
          size="large"
          icon="el-icon-s-promotion"
          @click="handleSendSms"
          :disabled="!canSend"
          :loading="sending"
        >
          문자 발송
        </el-button>
        <el-button
          size="large"
          @click="handleReset"
        >
          초기화
        </el-button>
      </div>
    </div>

    <!-- 발송 내역 -->
    <div class="history-section">
      <h2 class="section-title">
        <i class="el-icon-document"></i>
        최근 발송 내역
      </h2>
      <div class="history-list">
        <div
          v-for="history in sendHistory"
          :key="history.mid"
          class="history-item"
        >
          <div class="history-header">
            <span class="history-date">{{ formatDateTime(history.regDate) }}</span>
            <span class="history-status" :class="getStatusClass(history.reserveState)">
              {{ getStatusText(history.reserveState) }}
            </span>
          </div>
          <div class="history-content">
            <p class="history-message">{{ history.msg }}</p>
            <div class="history-info">
              <span>발송: {{ history.smsCount - history.failCount }}명</span>
              <span v-if="history.failCount > 0" class="fail-count">
                실패: {{ history.failCount }}명
              </span>
            </div>
          </div>
        </div>

        <div v-if="sendHistory.length === 0" class="empty-state">
          <i class="el-icon-document"></i>
          <p>발송 내역이 없습니다.</p>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="historyTotal > 0" class="history-pagination">
        <el-pagination
          :current-page="historyPage"
          :page-size="historyPageSize"
          :total="historyTotal"
          layout="prev, pager, next"
          @current-change="handleHistoryPageChange"
        />
      </div>
    </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { sendSms, getSmsHistory, getRemainCount, SmsHistory, SmsRemain } from '@/api/sms';
import { getChannelMemberList } from '@/api/channelMember';

interface Member {
  uid: string;
  name: string;
  phone: string;
  email: string;
}

interface MessageTemplate {
  id: string;
  name: string;
  content: string;
}

@Component({
  name: 'SmsManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private searchQuery = '';
  private selectedMembers: Member[] = [];
  private messageContent = '';
  private selectedTemplate = '';
  private sending = false;
  private loading = false;
  private channelUid = '';
  private remainCount: SmsRemain | null = null;

  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  // 회원 목록
  private searchResults: Member[] = [];
  private allMembers: Member[] = [];

  // 발송 내역 페이지네이션
  private sendHistory: SmsHistory[] = [];
  private historyPage = 1;
  private historyPageSize = 10;
  private historyTotal = 0;

  private messageTemplates: MessageTemplate[] = [
    {
      id: '1',
      name: '오프라인 장터 안내',
      content: '[와로 커뮤니티]\n오프라인 장터가 오픈되었습니다.\n다양한 상품을 확인해보세요!',
    },
    {
      id: '2',
      name: '신규 상품 등록 알림',
      content: '[와로 커뮤니티]\n새로운 상품이 등록되었습니다.\n지금 바로 확인해보세요!',
    },
    {
      id: '3',
      name: '이벤트 안내',
      content: '[와로 커뮤니티]\n특별 이벤트가 진행중입니다.\n자세한 내용은 앱을 확인해주세요.',
    },
  ];

  async mounted() {
    this.channelUid = this.$route.params.domain || '';
    if (this.channelUid) {
      await this.loadMembers();
      await this.loadSmsHistory();
      await this.loadRemainCount();
    }
  }

  get canSend() {
    return this.selectedMembers.length > 0 && this.messageContent.trim().length > 0;
  }

  private isSelected(uid: string) {
    return this.selectedMembers.some((m) => m.uid === uid);
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private async loadMembers() {
    this.loading = true;
    try {
      const response = await getChannelMemberList({ 
        channelUid: this.channelUid,
        page: 0,
        size: 1000,
      });
      
      // API 응답 구조 확인
      console.log('Members API Response:', response.data);
      
      let memberList = [];
      
      // response.data.content (Spring Data Page 형식)
      if (response.data && response.data.content && Array.isArray(response.data.content)) {
        memberList = response.data.content;
      } 
      // response.data가 배열 형식
      else if (Array.isArray(response.data)) {
        memberList = response.data;
      }
      
      const members = memberList.map((member: any) => {
        // user 객체에서 실제 사용자 정보 추출
        const user = member.user || member;
        
        return {
          uid: user.uid || member.userUid || member.uid || '',
          name: user.actualName || user.name || user.userName || '이름 없음',
          phone: user.concatNumber || user.phoneNumber || user.phone || '',
          email: user.email || '',
        };
      });
      
      console.log('Processed members:', members);
      
      this.allMembers = members;
      this.searchResults = members;
    } catch (error: any) {
      console.error('회원 목록 로딩 실패:', error);
      this.$message.error('회원 목록을 불러오는데 실패했습니다');
    } finally {
      this.loading = false;
    }
  }

  private async loadSmsHistory() {
    try {
      const response = await getSmsHistory({ 
        page: this.historyPage, 
        pageSize: this.historyPageSize,
      });
      
      // API 응답이 배열인 경우
      if (Array.isArray(response.data)) {
        this.sendHistory = response.data;
        this.historyTotal = response.data.length;
      } else if (response.data && response.data.content) {
        // 페이지된 응답 (Spring Data Page 형태)
        this.sendHistory = response.data.content || [];
        this.historyTotal = response.data.totalElements || 0;
      } else {
        this.sendHistory = response.data || [];
        this.historyTotal = 0;
      }
    } catch (error: any) {
      console.error('발송 내역 로딩 실패:', error);
    }
  }

  private handleHistoryPageChange(page: number) {
    this.historyPage = page;
    this.loadSmsHistory();
  }

  private async loadRemainCount() {
    try {
      const response = await getRemainCount();
      this.remainCount = response.data;
    } catch (error: any) {
      console.error('발송 가능 건수 조회 실패:', error);
    }
  }

  private toggleMember(member: Member) {
    const index = this.selectedMembers.findIndex((m) => m.uid === member.uid);
    if (index > -1) {
      this.selectedMembers.splice(index, 1);
    } else {
      this.selectedMembers.push(member);
    }
  }

  private selectAllMembers() {
    this.selectedMembers = [...this.searchResults];
  }

  private clearSelection() {
    this.selectedMembers = [];
  }

  private handleSearch() {
    if (!this.searchQuery.trim()) {
      this.searchResults = [...this.allMembers];
      return;
    }
    
    const query = this.searchQuery.toLowerCase();
    this.searchResults = this.allMembers.filter((member) => 
      member.name.toLowerCase().includes(query) ||
      member.phone.includes(query) ||
      member.email.toLowerCase().includes(query),
    );
  }

  private handleTemplateChange() {
    const template = this.messageTemplates.find((t) => t.id === this.selectedTemplate);
    if (template) {
      this.messageContent = template.content;
    }
  }

  private handleSendSms() {
    if (!this.canSend) return;

    this.$confirm(
      `${this.selectedMembers.length}명에게 문자를 발송하시겠습니까?`,
      '문자 발송 확인',
      {
        confirmButtonText: '발송',
        cancelButtonText: '취소',
        type: 'warning',
      },
    ).then(async () => {
      this.sending = true;
      try {
        const receivers = this.selectedMembers.map((m) => m.phone);
        
        const response = await sendSms({
          receivers,
          message: this.messageContent,
          testMode: false,
        });
        
        if (response.data.isSuccess) {
          this.$message.success(
            `문자가 발송되었습니다. (성공: ${response.data.successCnt}명, 실패: ${response.data.errorCnt}명)`,
          );
          this.handleReset();
          await this.loadSmsHistory();
          await this.loadRemainCount();
        } else {
          this.$message.error(response.data.message || '문자 발송에 실패했습니다');
        }
      } catch (error: any) {
        const message = error.response?.data?.message || '문자 발송에 실패했습니다';
        this.$message.error(message);
      } finally {
        this.sending = false;
      }
    }).catch(() => {
      // 취소
    });
  }

  private handleReset() {
    this.selectedMembers = [];
    this.messageContent = '';
    this.selectedTemplate = '';
    this.searchQuery = '';
  }

  private formatPhone(phone: string) {
    if (!phone) return '-';
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
  }

  private formatDateTime(datetime: string) {
    if (!datetime) return '-';
    return new Date(datetime).toLocaleString('ko-KR');
  }

  private getStatusClass(status: string) {
    if (status.includes('완료') || status.includes('발송완료')) return 'success';
    if (status.includes('실패')) return 'fail';
    if (status.includes('중') || status.includes('대기')) return 'pending';
    return '';
  }

  private getStatusText(status: string) {
    const statusMap: Record<string, string> = {
      '전송완료': '발송 완료',
      '발송완료': '발송 완료',
      '발송실패': '발송 실패',
      '전송중': '발송 중',
      '예약대기중': '예약 대기',
      '취소완료': '취소됨',
    };
    return statusMap[status] || status;
  }
}
</script>

<style scoped lang="scss">
.sms-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.sms-management-main {
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
  margin: 0 0 8px 0;
}

.page-description {
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  margin: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: #1890ff;
  }
}

// 검색 섹션
.search-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-form {
  display: flex;
  gap: 12px;

  .el-input {
    flex: 1;
  }
}

// 검색 결과
.search-results {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.results-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.results-actions {
  display: flex;
  gap: 8px;
}

.member-list {
  max-height: 400px;
  overflow-y: auto;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f8f9fa;
  }

  &.selected {
    background: #e6f4ff;
    border: 1px solid #91caff;
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.member-details {
  flex: 1;
}

.member-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 4px;
}

.member-contact {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;

  .divider {
    color: #ddd;
  }
}

// 메시지 작성 섹션
.message-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.template-selector {
  margin-bottom: 24px;

  .el-select {
    width: 100%;
  }
}

.field-label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.char-count {
  font-size: 13px;
  font-weight: 400;
  color: #666;

  &.exceeded {
    color: #ff4d4f;
    font-weight: 600;
  }
}

.message-input {
  margin-bottom: 24px;
}

.send-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
}

.info-label {
  font-size: 13px;
  color: #666;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
}

.send-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

// 발송 내역
.history-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.history-list {
  margin-bottom: 20px;
}

.history-pagination {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.history-item {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 12px;

  &:last-child {
    margin-bottom: 0;
  }
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-date {
  font-size: 13px;
  color: #666;
}

.history-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;

  &.success {
    background: #f6ffed;
    color: #52c41a;
  }

  &.fail {
    background: #fff2f0;
    color: #ff4d4f;
  }

  &.pending {
    background: #e6f4ff;
    color: #1890ff;
  }
}

.history-content {
  .history-message {
    font-size: 14px;
    color: #1a1a1a;
    margin-bottom: 8px;
    line-height: 1.5;
    white-space: pre-wrap;
  }

  .history-info {
    display: flex;
    gap: 16px;
    font-size: 13px;
    color: #666;

    .fail-count {
      color: #ff4d4f;
    }
  }
}

// 빈 상태
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
  }

  p {
    font-size: 14px;
  }
}

// 반응형
@media (max-width: 1024px) {
  .sms-management-main {
    margin-left: 0;
    padding: 30px 20px;
  }
}

@media (max-width: 768px) {
  .sms-management-main {
    padding: 20px 16px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
    margin-bottom: 6px;
  }

  .page-description {
    font-size: 13px;
  }

  .section-title {
    font-size: 16px;
    margin-bottom: 16px;
    gap: 6px;
  }

  // 검색 섹션
  .search-section {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .search-form {
    gap: 10px;

    ::v-deep .el-input__inner {
      height: 40px;
      font-size: 14px;
    }

    ::v-deep .el-button {
      padding: 10px 16px;
      font-size: 13px;
    }
  }

  // 검색 결과
  .search-results {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 16px;
    padding-bottom: 12px;
  }

  .results-title {
    font-size: 14px;
  }

  .results-actions {
    width: 100%;
    display: flex;

    ::v-deep .el-button {
      flex: 1;
      padding: 8px 12px;
      font-size: 12px;
    }
  }

  .member-list {
    max-height: 280px;
  }

  .member-item {
    padding: 12px;
    gap: 10px;

    &.selected {
      border-width: 1px;
    }
  }

  .member-avatar {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }

  .member-name {
    font-size: 14px;
    margin-bottom: 2px;
  }

  .member-contact {
    font-size: 12px;
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;

    .divider {
      display: none;
    }
  }

  // 메시지 작성 섹션
  .message-section {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .template-selector {
    margin-bottom: 20px;

    ::v-deep .el-select {
      .el-input__inner {
        height: 40px;
        font-size: 14px;
      }
    }
  }

  .field-label {
    font-size: 13px;
    margin-bottom: 6px;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .char-count {
    font-size: 12px;
  }

  .message-input {
    margin-bottom: 20px;

    ::v-deep .el-textarea__inner {
      font-size: 14px;
      padding: 12px;
    }
  }

  .send-info {
    padding: 16px;
    margin-bottom: 20px;
  }

  .info-item {
    flex-direction: column;
    align-items: center;
  }

  .info-label {
    font-size: 12px;
  }

  .info-value {
    font-size: 14px;
  }

  .send-actions {
    gap: 10px;

    ::v-deep .el-button {
      padding: 12px 20px;
      font-size: 14px;

      &--large {
        height: 44px;
      }
    }
  }

  // 발송 내역
  .history-section {
    padding: 16px;
    border-radius: 10px;
  }

  .history-list {
    max-height: 350px;
  }

  .history-item {
    padding: 14px;
    margin-bottom: 10px;
    border-radius: 8px;
  }

  .history-header {
    margin-bottom: 10px;
  }

  .history-date {
    font-size: 12px;
  }

  .history-status {
    padding: 3px 10px;
    font-size: 11px;
  }

  .history-content {
    .history-message {
      font-size: 13px;
      margin-bottom: 10px;
    }

    .history-info {
      font-size: 12px;
      gap: 12px;
      flex-wrap: wrap;
    }
  }

  .history-pagination {
    padding-top: 16px;
  }

  // 빈 상태
  .empty-state {
    padding: 40px 16px;

    i {
      font-size: 36px;
      margin-bottom: 12px;
    }

    p {
      font-size: 13px;
    }
  }

  // Element UI 체크박스 조정
  ::v-deep .el-checkbox {
    .el-checkbox__inner {
      width: 16px;
      height: 16px;
    }
  }
}

// 425px 이하 모바일 반응형
@media (max-width: 425px) {
  .sms-management-main {
    padding: 20px 16px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
    margin-bottom: 6px;
  }

  .page-description {
    font-size: 13px;
  }

  .section-title {
    font-size: 16px;
    margin-bottom: 16px;
    gap: 6px;
  }

  // 검색 섹션
  .search-section {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .search-form {
    gap: 10px;

    ::v-deep .el-input__inner {
      height: 40px;
      font-size: 14px;
    }

    ::v-deep .el-button {
      padding: 10px 16px;
      font-size: 13px;
    }
  }

  // 검색 결과
  .search-results {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 16px;
    padding-bottom: 12px;
  }

  .results-title {
    font-size: 14px;
  }

  .results-actions {
    width: 100%;
    display: flex;

    ::v-deep .el-button {
      flex: 1;
      padding: 8px 12px;
      font-size: 12px;
    }
  }

  .member-list {
    max-height: 280px;
  }

  .member-item {
    padding: 12px;
    gap: 10px;

    &.selected {
      border-width: 1px;
    }
  }

  .member-avatar {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }

  .member-name {
    font-size: 14px;
    margin-bottom: 2px;
  }

  .member-contact {
    font-size: 12px;
    flex-direction: column;
    align-items: flex-start;
    gap: 2px;

    .divider {
      display: none;
    }
  }

  // 메시지 작성 섹션
  .message-section {
    padding: 16px;
    margin-bottom: 16px;
    border-radius: 10px;
  }

  .template-selector {
    margin-bottom: 20px;

    ::v-deep .el-select {
      .el-input__inner {
        height: 40px;
        font-size: 14px;
      }
    }
  }

  .field-label {
    font-size: 13px;
    margin-bottom: 6px;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .char-count {
    font-size: 12px;
  }

  .message-input {
    margin-bottom: 20px;

    ::v-deep .el-textarea__inner {
      font-size: 14px;
      padding: 12px;
    }
  }

  .send-info {
    padding: 16px;
    margin-bottom: 20px;
  }

  .info-item {
    flex-direction: column;
    align-items: center;
  }

  .info-label {
    font-size: 12px;
  }

  .info-value {
    font-size: 14px;
  }

  .send-actions {
    gap: 10px;

    ::v-deep .el-button {
      padding: 12px 20px;
      font-size: 14px;

      &--large {
        height: 44px;
      }
    }
  }

  // 발송 내역
  .history-section {
    padding: 16px;
    border-radius: 10px;
  }

  .history-list {
    margin-bottom: 16px;
  }

  .history-item {
    padding: 14px;
    margin-bottom: 10px;
    border-radius: 8px;
  }

  .history-header {
    margin-bottom: 10px;
  }

  .history-date {
    font-size: 12px;
  }

  .history-status {
    padding: 3px 10px;
    font-size: 11px;
  }

  .history-content {
    .history-message {
      font-size: 13px;
      margin-bottom: 10px;
    }

    .history-info {
      font-size: 12px;
      gap: 12px;
      flex-wrap: wrap;
    }
  }

  .history-pagination {
    padding-top: 16px;
  }

  // 빈 상태
  .empty-state {
    padding: 40px 16px;

    i {
      font-size: 36px;
      margin-bottom: 12px;
    }

    p {
      font-size: 13px;
    }
  }

  // Element UI 체크박스 조정
  ::v-deep .el-checkbox {
    .el-checkbox__inner {
      width: 16px;
      height: 16px;
    }
  }
}
</style>
