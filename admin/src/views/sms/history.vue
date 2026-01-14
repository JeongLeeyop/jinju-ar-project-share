<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">발송 내역</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="~"
            start-placeholder="시작일"
            end-placeholder="종료일"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            size="small"
            @change="handleSearch"
          ></el-date-picker>
          <router-link class="back-btn" :to="{ name: 'SmsSend' }">
            <i class="el-icon-arrow-left"></i> 발송으로
          </router-link>
        </div>
      </div>
    </div>

    <!-- 통계 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon"><i class="el-icon-message"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalCount | parseKrw }}</div>
          <div class="stat-label">전체 발송</div>
        </div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon"><i class="el-icon-check"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.successCount | parseKrw }}</div>
          <div class="stat-label">발송 성공</div>
        </div>
      </div>
      <div class="stat-card danger">
        <div class="stat-icon"><i class="el-icon-close"></i></div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.failCount | parseKrw }}</div>
          <div class="stat-label">발송 실패</div>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="historyList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column label="발송일시" width="180" align="center">
          <template slot-scope="scope">
            {{ scope.row.sentDate }}
          </template>
        </el-table-column>
        <el-table-column label="유형" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.msgType === 'LMS' ? 'warning' : 'primary'" size="small">
              {{ scope.row.msgType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiver" label="수신번호" width="140" />
        <el-table-column prop="content" label="메시지 내용">
          <template slot-scope="scope">
            <span class="message-preview">{{ scope.row.content }}</span>
          </template>
        </el-table-column>
        <el-table-column label="상태" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        v-if="totalElements > 0"
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.pageSize"
        @pagination="getHistoryList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getSmsHistory } from '@/api/sms';

@Component({
  components: {
    Pagination,
  },
})
export default class SmsHistory extends Vue {
  private loading = false;
  private historyList: any[] = [];
  private totalElements = 0;
  private dateRange: string[] = [];

  private stats = {
    totalCount: 0,
    successCount: 0,
    failCount: 0,
  };

  private listQuery = {
    page: 1,
    pageSize: 20,
    startDate: '',
  };

  created() {
    // 기본: 최근 30일
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 30);
    
    this.dateRange = [
      this.formatDate(start),
      this.formatDate(end),
    ];
    
    this.getHistoryList();
  }

  private formatDate(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private async getHistoryList() {
    this.loading = true;
    try {
      const params = {
        ...this.listQuery,
        startDate: this.dateRange[0],
      };
      
      const { data } = await getSmsHistory(params);
      this.historyList = data || [];
      this.totalElements = data.length || 0;

      // 통계 계산
      this.stats.totalCount = this.historyList.length;
      this.stats.successCount = this.historyList.filter((h: any) => h.status === '0' || h.status === 'SUCCESS').length;
      this.stats.failCount = this.historyList.filter((h: any) => h.status !== '0' && h.status !== 'SUCCESS').length;
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getHistoryList();
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.pageSize) - index;
  }

  private getStatusType(status: string) {
    if (status === '0' || status === 'SUCCESS') return 'success';
    return 'danger';
  }

  private getStatusText(status: string) {
    if (status === '0' || status === 'SUCCESS') return '성공';
    return '실패';
  }
}
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stat-card .stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-card .stat-icon i {
  font-size: 24px;
  color: #fff;
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #e04545 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 2px;
}

.message-preview {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.back-btn {
  font-size: 14px;
  color: #667eea;
  background: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  margin-left: 15px;
}
.back-btn:hover {
  background: #667eea;
  color: #fff;
}
</style>
