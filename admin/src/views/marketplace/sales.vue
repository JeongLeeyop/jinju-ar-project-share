<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">매출 현황</p>
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
            @change="handleSearch"
          ></el-date-picker>
          <el-select v-model="listQuery.channelUid" placeholder="커뮤니티 선택" clearable @change="handleSearch">
            <el-option
              v-for="channel in channelList"
              :key="channel.uid"
              :label="channel.name"
              :value="channel.uid"
            ></el-option>
          </el-select>
        </div>
      </div>
    </div>

    <!-- 매출 통계 -->
    <div class="sales-stats">
      <div class="stat-card large primary">
        <div class="stat-content">
          <div class="stat-label">총 매출액</div>
          <div class="stat-value">{{ salesStats.totalAmount | parseKrw }}원</div>
        </div>
        <div class="stat-icon"><i class="el-icon-money"></i></div>
      </div>
      <div class="stat-card large">
        <div class="stat-content">
          <div class="stat-label">거래 건수</div>
          <div class="stat-value">{{ salesStats.totalCount | parseKrw }}건</div>
        </div>
        <div class="stat-icon blue"><i class="el-icon-document"></i></div>
      </div>
      <div class="stat-card large">
        <div class="stat-content">
          <div class="stat-label">평균 거래액</div>
          <div class="stat-value">{{ salesStats.averageAmount | parseKrw }}원</div>
        </div>
        <div class="stat-icon green"><i class="el-icon-data-line"></i></div>
      </div>
    </div>

    <!-- 커뮤니티별 매출 -->
    <div class="sales-section">
      <h3 class="section-title"><i class="el-icon-pie-chart"></i> 커뮤니티별 매출</h3>
      <el-table
        v-loading="loading"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="channelSales"
      >
        <el-table-column label="순위" width="70" align="center">
          <template slot-scope="scope">
            <span :class="['rank', scope.$index < 3 ? 'top' : '']">{{ scope.$index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="channelName" label="커뮤니티명" />
        <el-table-column label="거래 건수" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.transactionCount | parseKrw }}건
          </template>
        </el-table-column>
        <el-table-column label="매출액" width="150" align="right">
          <template slot-scope="scope">
            <span class="amount">{{ scope.row.totalAmount | parseKrw }}원</span>
          </template>
        </el-table-column>
        <el-table-column label="비율" width="120" align="center">
          <template slot-scope="scope">
            <el-progress
              :percentage="getPercentage(scope.row.totalAmount)"
              :stroke-width="10"
              :show-text="false"
              color="#667eea"
            ></el-progress>
            <span class="percentage">{{ getPercentage(scope.row.totalAmount) }}%</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 일별 매출 -->
    <div class="sales-section">
      <h3 class="section-title"><i class="el-icon-date"></i> 일별 매출</h3>
      <el-table
        v-loading="loading"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="dailySales"
      >
        <el-table-column prop="date" label="날짜" width="150" align="center" />
        <el-table-column label="거래 건수" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.transactionCount | parseKrw }}건
          </template>
        </el-table-column>
        <el-table-column label="매출액" align="right">
          <template slot-scope="scope">
            <span class="amount">{{ scope.row.totalAmount | parseKrw }}원</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getMarketplaceSales } from '@/api/marketplace';
import { getChannelList } from '@/api/channel';

@Component
export default class MarketplaceSales extends Vue {
  private loading = false;
  private dateRange: string[] = [];
  private channelList: any[] = [];
  
  private salesStats = {
    totalAmount: 0,
    totalCount: 0,
    averageAmount: 0,
  };

  private channelSales: any[] = [];
  private dailySales: any[] = [];

  private listQuery = {
    channelUid: '',
    startDate: '',
    endDate: '',
  };

  created() {
    // 기본 날짜 범위: 최근 30일
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 30);
    
    this.dateRange = [
      this.formatDate(start),
      this.formatDate(end),
    ];
    
    this.loadData();
  }

  private formatDate(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private async loadData() {
    await this.getChannelList();
    await this.getSalesData();
  }

  private async getChannelList() {
    try {
      const { data } = await getChannelList({ page: 1, size: 100 });
      this.channelList = data.content || [];
    } catch (error) {
      console.error(error);
    }
  }

  private async getSalesData() {
    this.loading = true;
    try {
      const params = {
        ...this.listQuery,
        startDate: this.dateRange[0],
        endDate: this.dateRange[1],
      };
      
      const { data } = await getMarketplaceSales(params);
      
      this.salesStats = data.stats || { totalAmount: 0, totalCount: 0, averageAmount: 0 };
      this.channelSales = data.channelSales || [];
      this.dailySales = data.dailySales || [];
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.getSalesData();
  }

  private getPercentage(amount: number) {
    if (!this.salesStats.totalAmount) return 0;
    return Math.round((amount / this.salesStats.totalAmount) * 100);
  }
}
</script>

<style scoped>
.sales-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card.large {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.stat-card.large.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-card.large .stat-label {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 8px;
}

.stat-card.large .stat-value {
  font-size: 32px;
  font-weight: 700;
}

.stat-card.large .stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.large .stat-icon i {
  font-size: 32px;
  color: #fff;
}

.stat-card.large .stat-icon.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.large .stat-icon.green {
  background: linear-gradient(135deg, #67c23a 0%, #5daf34 100%);
}

.sales-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #667eea;
}

.rank {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background: #f5f7fa;
  font-size: 12px;
  font-weight: 600;
}

.rank.top {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.amount {
  font-weight: 600;
  color: #667eea;
}

.percentage {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}
</style>
