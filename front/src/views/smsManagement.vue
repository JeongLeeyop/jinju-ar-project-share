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
          @click="toggleMember(member)"
        >
          <el-checkbox
            :value="isSelected(member.uid)"
            @change="toggleMember(member)"
          />
          <div class="member-info">
            <div class="member-avatar">
              <i class="el-icon-user"></i>
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
          <i class="el-icon-user"></i>
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
          <span class="char-count" :class="{ exceeded: messageContent.length > 90 }">
            {{ messageContent.length }} / 90자 (장문 시 추가 요금 발생)
          </span>
        </label>
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="8"
          placeholder="문자 메시지 내용을 입력하세요&#10;&#10;예시:&#10;[와로 커뮤니티]&#10;안녕하세요, 오프라인 장터 안내 드립니다.&#10;자세한 내용은 앱을 확인해주세요."
          maxlength="2000"
        />
        <p class="message-info">
          <i class="el-icon-warning"></i>
          90자 초과 시 장문 문자(LMS) 요금이 부과됩니다.
        </p>
      </div>

      <!-- 발송 예상 정보 -->
      <div class="send-info">
        <div class="info-item">
          <span class="info-label">발송 대상:</span>
          <span class="info-value">{{ selectedMembers.length }}명</span>
        </div>
        <div class="info-item">
          <span class="info-label">메시지 타입:</span>
          <span class="info-value">{{ messageType }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">예상 비용:</span>
          <span class="info-value estimated-cost">{{ estimatedCost }}원</span>
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
          :key="history.id"
          class="history-item"
        >
          <div class="history-header">
            <span class="history-date">{{ formatDateTime(history.sentAt) }}</span>
            <span class="history-status" :class="history.status">
              {{ getStatusText(history.status) }}
            </span>
          </div>
          <div class="history-content">
            <p class="history-message">{{ history.message }}</p>
            <div class="history-info">
              <span>발송: {{ history.successCount }}명</span>
              <span v-if="history.failCount > 0" class="fail-count">
                실패: {{ history.failCount }}명
              </span>
              <span>비용: {{ history.cost }}원</span>
            </div>
          </div>
        </div>

        <div v-if="sendHistory.length === 0" class="empty-state">
          <i class="el-icon-document"></i>
          <p>발송 내역이 없습니다.</p>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

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

interface SendHistory {
  id: string;
  message: string;
  sentAt: string;
  status: 'success' | 'fail' | 'pending';
  successCount: number;
  failCount: number;
  cost: number;
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

  // Mock 데이터 (실제로는 API 연동 필요)
  private searchResults: Member[] = [
    {
      uid: '1',
      name: '정이욥',
      phone: '01012345678',
      email: 'hong@example.com',
    },
    {
      uid: '2',
      name: '오형래',
      phone: '01087654321',
      email: 'kim@example.com',
    },
    {
      uid: '3',
      name: '배은별',
      phone: '01011112222',
      email: 'lee@example.com',
    },
  ];

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

  private sendHistory: SendHistory[] = [
    {
      id: '1',
      message: '[와로 커뮤니티]\n오프라인 장터가 오픈되었습니다.',
      sentAt: '2024-03-15 14:30:00',
      status: 'success',
      successCount: 45,
      failCount: 0,
      cost: 4500,
    },
    {
      id: '2',
      message: '[와로 커뮤니티]\n신규 상품이 등록되었습니다.',
      sentAt: '2024-03-14 10:15:00',
      status: 'success',
      successCount: 38,
      failCount: 2,
      cost: 4000,
    },
  ];

  get canSend() {
    return this.selectedMembers.length > 0 && this.messageContent.trim().length > 0;
  }

  get messageType() {
    if (this.messageContent.length === 0) return '-';
    return this.messageContent.length <= 90 ? 'SMS (단문)' : 'LMS (장문)';
  }

  get estimatedCost() {
    if (this.selectedMembers.length === 0) return 0;
    const pricePerMessage = this.messageContent.length <= 90 ? 100 : 200;
    return this.selectedMembers.length * pricePerMessage;
  }

  private isSelected(uid: string) {
    return this.selectedMembers.some((m) => m.uid === uid);
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
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
    // 실제로는 API 호출
    console.log('Searching:', this.searchQuery);
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
      `${this.selectedMembers.length}명에게 문자를 발송하시겠습니까?\n예상 비용: ${this.estimatedCost}원`,
      '문자 발송 확인',
      {
        confirmButtonText: '발송',
        cancelButtonText: '취소',
        type: 'warning',
      },
    ).then(() => {
      this.sending = true;
      // 실제로는 API 호출
      setTimeout(() => {
        this.sending = false;
        this.$message.success('문자가 발송되었습니다.');
        this.handleReset();
      }, 2000);
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

  private getStatusText(status: string) {
    const statusMap: Record<string, string> = {
      success: '발송 완료',
      fail: '발송 실패',
      pending: '발송 중',
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

.message-info {
  margin-top: 8px;
  font-size: 13px;
  color: #faad14;
  display: flex;
  align-items: center;
  gap: 4px;
}

.send-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #666;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;

  &.estimated-cost {
    color: #1890ff;
  }
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
  max-height: 500px;
  overflow-y: auto;
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

  .page-title {
    font-size: 24px;
  }

  .search-form {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .send-info {
    grid-template-columns: 1fr;
  }

  .send-actions {
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .member-contact {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;

    .divider {
      display: none;
    }
  }
}
</style>
