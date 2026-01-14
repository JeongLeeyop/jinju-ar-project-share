<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">문자 발송</p>
      </div>

      <div class="user__tab small">
        <router-link class="history-btn" :to="{ name: 'SmsHistory' }">
          <i class="el-icon-document"></i> 발송 내역
        </router-link>
      </div>
    </div>

    <!-- 잔여 건수 -->
    <div class="remain-info">
      <div class="remain-card">
        <i class="el-icon-message"></i>
        <div class="remain-content">
          <span class="remain-label">잔여 발송 건수</span>
          <span class="remain-value">{{ remainCount | parseKrw }}건</span>
        </div>
      </div>
    </div>

    <div class="sms-container">
      <!-- 좌측: 수신자 선택 -->
      <div class="sms-left">
        <div class="panel">
          <h3 class="panel-title"><i class="el-icon-user"></i> 수신자 선택</h3>
          
          <!-- 검색 -->
          <div class="search-box">
            <el-select v-model="memberQuery.searchType" placeholder="검색조건" size="small">
              <el-option label="이름" value="actualName"></el-option>
              <el-option label="연락처" value="concatNumber"></el-option>
            </el-select>
            <el-input v-model="memberQuery.searchValue" placeholder="검색어" size="small" @keyup.enter.native="getMemberList()" />
            <el-button size="small" type="primary" @click="getMemberList()">검색</el-button>
          </div>

          <!-- 회원 목록 -->
          <el-table
            v-loading="memberLoading"
            :data="memberList"
            border
            size="small"
            height="350"
            @selection-change="handleSelectionChange"
            :header-cell-style="{ background: '#667eea', color: '#fff', padding: '8px 0' }"
          >
            <el-table-column type="selection" width="40" />
            <el-table-column prop="actualName" label="이름" />
            <el-table-column prop="concatNumber" label="연락처" width="130" />
          </el-table>
          
          <div class="selection-info">
            선택된 수신자: <strong>{{ selectedMembers.length }}명</strong>
          </div>
        </div>
      </div>

      <!-- 우측: 메시지 작성 -->
      <div class="sms-right">
        <div class="panel">
          <h3 class="panel-title"><i class="el-icon-edit"></i> 메시지 작성</h3>
          
          <!-- 수신자 목록 -->
          <div class="receiver-list">
            <label>수신자 목록</label>
            <div class="receiver-tags">
              <el-tag
                v-for="(member, index) in selectedMembers"
                :key="member.uid"
                closable
                size="small"
                @close="removeReceiver(index)"
              >
                {{ member.actualName }} ({{ member.concatNumber }})
              </el-tag>
              <span v-if="selectedMembers.length === 0" class="no-receiver">수신자를 선택해주세요</span>
            </div>
          </div>

          <!-- 직접 입력 -->
          <div class="direct-input">
            <label>직접 입력</label>
            <div class="input-row">
              <el-input v-model="directNumber" placeholder="010-0000-0000" size="small" />
              <el-button size="small" type="primary" @click="addDirectNumber">추가</el-button>
            </div>
          </div>

          <!-- 메시지 내용 -->
          <div class="message-box">
            <label>메시지 내용</label>
            <el-input
              type="textarea"
              v-model="message"
              :rows="8"
              placeholder="메시지 내용을 입력하세요"
              maxlength="2000"
              show-word-limit
            />
            <div class="message-info">
              <span>{{ getByteLength(message) }} / 2,000 bytes</span>
              <span :class="messageType">{{ messageTypeText }}</span>
            </div>
          </div>

          <!-- 발송 버튼 -->
          <el-button 
            type="primary" 
            class="send-btn" 
            :loading="sending"
            :disabled="!canSend"
            @click="handleSend"
          >
            <i class="el-icon-s-promotion"></i> 문자 발송
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { sendSms, getSmsRemainCount, getMemberListForSms } from '@/api/sms';

@Component
export default class SmsIndex extends Vue {
  private memberLoading = false;
  private sending = false;
  private memberList: any[] = [];
  private selectedMembers: any[] = [];
  private directReceivers: any[] = [];
  private directNumber = '';
  private message = '';
  private remainCount = 0;

  private memberQuery = {
    page: 1,
    size: 50,
    searchType: 'actualName',
    searchValue: '',
  };

  get receivers() {
    return [...this.selectedMembers, ...this.directReceivers];
  }

  get canSend() {
    return this.receivers.length > 0 && this.message.trim().length > 0;
  }

  get messageType() {
    const bytes = this.getByteLength(this.message);
    return bytes > 90 ? 'lms' : 'sms';
  }

  get messageTypeText() {
    return this.messageType === 'lms' ? 'LMS (장문)' : 'SMS (단문)';
  }

  created() {
    this.loadData();
  }

  private async loadData() {
    await Promise.all([
      this.getMemberList(),
      this.getRemainCount(),
    ]);
  }

  private async getMemberList() {
    this.memberLoading = true;
    try {
      const { data } = await getMemberListForSms(this.memberQuery);
      this.memberList = data.content || [];
    } catch (error) {
      console.error(error);
    } finally {
      this.memberLoading = false;
    }
  }

  private async getRemainCount() {
    try {
      const { data } = await getSmsRemainCount();
      this.remainCount = data.remainCount || 0;
    } catch (error) {
      console.error(error);
    }
  }

  private handleSelectionChange(selection: any[]) {
    this.selectedMembers = selection;
  }

  private removeReceiver(index: number) {
    this.selectedMembers.splice(index, 1);
  }

  private addDirectNumber() {
    if (!this.directNumber.trim()) return;
    
    const phone = this.directNumber.replace(/-/g, '');
    if (!/^01[016789]\d{7,8}$/.test(phone)) {
      this.$message.warning('올바른 휴대폰 번호를 입력해주세요.');
      return;
    }

    this.directReceivers.push({
      uid: `direct_${Date.now()}`,
      actualName: '직접입력',
      concatNumber: this.directNumber,
    });
    this.directNumber = '';
  }

  private getByteLength(str: string) {
    let bytes = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      bytes += char > 127 ? 2 : 1;
    }
    return bytes;
  }

  private async handleSend() {
    if (!this.canSend) return;

    try {
      await this.$confirm(
        `${this.receivers.length}명에게 문자를 발송하시겠습니까?`,
        '문자 발송',
        {
          confirmButtonText: '발송',
          cancelButtonText: '취소',
          type: 'info',
        }
      );

      this.sending = true;

      const request = {
        receivers: this.receivers.map(r => ({
          phone: r.concatNumber.replace(/-/g, ''),
          name: r.actualName,
        })),
        message: this.message,
        messageType: this.messageType.toUpperCase(),
      };

      const { data } = await sendSms(request);

      if (data.success) {
        this.$message.success(`${data.successCount}건 발송 완료`);
        this.message = '';
        this.selectedMembers = [];
        this.directReceivers = [];
        await this.getRemainCount();
      } else {
        this.$message.error(data.message || '발송 중 오류가 발생했습니다.');
      }
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error('발송 중 오류가 발생했습니다.');
      }
    } finally {
      this.sending = false;
    }
  }
}
</script>

<style scoped>
.remain-info {
  margin-bottom: 20px;
}

.remain-card {
  background: #409EFF;
  color: #fff;
  border-radius: 12px;
  padding: 20px 30px;
  display: inline-flex;
  align-items: center;
  gap: 15px;
}

.remain-card i {
  font-size: 32px;
}

.remain-content {
  display: flex;
  flex-direction: column;
}

.remain-label {
  font-size: 13px;
  opacity: 0.9;
}

.remain-value {
  font-size: 24px;
  font-weight: 700;
}

.sms-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.panel-title i {
  margin-right: 8px;
  color: #409EFF;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.search-box .el-select {
  width: 120px;
}

.search-box .el-input {
  flex: 1;
}

.selection-info {
  margin-top: 15px;
  text-align: right;
  color: #606266;
}

.receiver-list,
.direct-input,
.message-box {
  margin-bottom: 20px;
}

.receiver-list label,
.direct-input label,
.message-box label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #606266;
}

.receiver-tags {
  min-height: 60px;
  max-height: 100px;
  overflow-y: auto;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.receiver-tags .el-tag {
  margin: 2px 4px;
}

.no-receiver {
  color: #909399;
  font-size: 13px;
}

.input-row {
  display: flex;
  gap: 10px;
}

.input-row .el-input {
  flex: 1;
}

.message-info {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.message-info .sms {
  color: #67c23a;
}

.message-info .lms {
  color: #e6a23c;
}

.send-btn {
  width: 100%;
  padding: 15px;
  font-size: 16px;
  background: #409EFF;
  border: none;
}

.send-btn:hover {
  background: #66b1ff;
}

.history-btn {
  background: #fff;
  color: #409EFF;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 500;
  border: 1px solid #409EFF;
}

.history-btn:hover {
  background: #409EFF;
  color: #fff;
}
</style>
