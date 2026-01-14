<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">발송 내역</h1>
        <p class="page-subtitle">총 {{ totalElements }}건의 발송 내역</p>
      </div>
      <div class="header-right">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="~"
          start-placeholder="시작일"
          end-placeholder="종료일"
          @change="handleSearch"
          value-format="yyyy-MM-dd"
          class="date-picker"
        />
        <el-button type="primary" @click="goToSend">
          <i class="el-icon-s-promotion"></i> 새 문자 발송
        </el-button>
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="historyList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column label="유형" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.messageType === 'SMS' ? 'primary' : 'warning'" size="small">
              {{ scope.row.messageType || 'SMS' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="내용" min-width="250">
          <template slot-scope="scope">
            <div class="message-content">
              <span class="message-title" v-if="scope.row.title">{{ scope.row.title }}</span>
              <span class="message-text">{{ truncate(scope.row.content, 50) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="수신자" width="100" align="center">
          <template slot-scope="scope">
            <span class="receiver-count">{{ scope.row.receiverCount || 1 }}명</span>
          </template>
        </el-table-column>
        <el-table-column label="발송결과" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="발송일시" width="180" align="center">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.createDate) }}
          </template>
        </el-table-column>
        <el-table-column label="상세" width="80" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="showDetail(scope.row)">보기</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalElements"
          :page-size="listQuery.size"
          :current-page.sync="listQuery.page"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <el-dialog title="발송 상세" :visible.sync="detailVisible" width="500px">
      <div class="detail-content" v-if="selectedItem">
        <div class="detail-row">
          <span class="detail-label">발송일시</span>
          <span class="detail-value">{{ formatDateTime(selectedItem.createDate) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">유형</span>
          <span class="detail-value">{{ selectedItem.messageType || 'SMS' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">발신번호</span>
          <span class="detail-value">{{ formatPhone(selectedItem.senderPhone) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">수신자</span>
          <span class="detail-value">{{ selectedItem.receiverCount || 1 }}명</span>
        </div>
        <div class="detail-row" v-if="selectedItem.title">
          <span class="detail-label">제목</span>
          <span class="detail-value">{{ selectedItem.title }}</span>
        </div>
        <div class="detail-row full">
          <span class="detail-label">내용</span>
          <div class="detail-message">{{ selectedItem.content }}</div>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="detailVisible = false">닫기</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getSmsHistory } from '@/api/sms';

@Component({
  name: 'SmsHistory',
})
export default class extends Vue {
  private loading = false;
  private historyList: any[] = [];
  private totalElements = 0;
  private dateRange: string[] = [];
  private detailVisible = false;
  private selectedItem: any = null;
  
  private listQuery = {
    page: 1,
    size: 10,
    startDate: '',
    endDate: '',
  };

  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  mounted() {
    this.loadHistory();
  }

  private async loadHistory() {
    this.loading = true;
    try {
      if (this.dateRange && this.dateRange.length === 2) {
        this.listQuery.startDate = this.dateRange[0];
        this.listQuery.endDate = this.dateRange[1];
      } else {
        this.listQuery.startDate = '';
        this.listQuery.endDate = '';
      }
      
      const res = await getSmsHistory(this.listQuery);
      this.historyList = res.data.content || res.data || [];
      this.totalElements = res.data.totalElements || this.historyList.length;
    } catch (error) {
      console.error('Failed to load SMS history:', error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.loadHistory();
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadHistory();
  }

  private showDetail(item: any) {
    this.selectedItem = item;
    this.detailVisible = true;
  }

  private goToSend() {
    this.$router.push('/sms');
  }

  private truncate(text: string, length: number) {
    if (!text) return '-';
    return text.length > length ? text.substring(0, length) + '...' : text;
  }

  private formatPhone(phone: string) {
    if (!phone) return '-';
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
  }

  private formatDateTime(date: string) {
    if (!date) return '-';
    return new Date(date).toLocaleString('ko-KR');
  }

  private getStatusName(status: string) {
    const statuses: { [key: string]: string } = {
      SUCCESS: '성공',
      FAIL: '실패',
      PENDING: '대기중',
    };
    return statuses[status] || status || '성공';
  }

  private getStatusType(status: string) {
    const types: { [key: string]: string } = {
      SUCCESS: 'success',
      FAIL: 'danger',
      PENDING: 'warning',
    };
    return types[status] || 'success';
  }
}
</script>

<style scoped lang="scss">
.page-wrap {
  padding: 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #222;
    margin-bottom: 4px;
  }
  
  .page-subtitle {
    font-size: 14px;
    color: #888;
  }
  
  .header-right {
    display: flex;
    gap: 12px;
    align-items: center;
  }
  
  .date-picker {
    width: 280px;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  
  .message-title {
    font-weight: 600;
    color: #222;
    font-size: 14px;
  }
  
  .message-text {
    font-size: 13px;
    color: #666;
  }
}

.receiver-count {
  color: #073DFF;
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.detail-content {
  .detail-row {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    &.full {
      flex-direction: column;
      gap: 8px;
    }
    
    .detail-label {
      width: 80px;
      color: #888;
      flex-shrink: 0;
    }
    
    .detail-value {
      color: #222;
      font-weight: 500;
    }
    
    .detail-message {
      background: #f5f7fa;
      padding: 16px;
      border-radius: 8px;
      color: #333;
      line-height: 1.6;
      white-space: pre-wrap;
    }
  }
}
</style>
