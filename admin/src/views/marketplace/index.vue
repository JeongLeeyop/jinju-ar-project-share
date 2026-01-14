<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">장터 조회</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.channelUid" placeholder="커뮤니티 선택" clearable @change="handleSearch">
            <el-option
              v-for="channel in channelList"
              :key="channel.uid"
              :label="channel.name"
              :value="channel.uid"
            ></el-option>
          </el-select>
          <el-select v-model="listQuery.status" placeholder="상태" clearable @change="handleSearch">
            <el-option label="판매중" value="ACTIVE"></el-option>
            <el-option label="거래중" value="TRADING"></el-option>
            <el-option label="판매완료" value="SOLD_OUT"></el-option>
            <el-option label="숨김" value="HIDDEN"></el-option>
          </el-select>
          <el-input placeholder="상품명 검색" v-model="listQuery.keyword" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>
      </div>
    </div>

    <!-- 통계 카드 -->
    <div class="stats-row">
      <div class="stat-card">
        <!-- <div class="stat-icon"><i class="el-icon-goods"></i></div> -->
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalProducts | parseKrw }}</div>
          <div class="stat-label">총 상품 수</div>
        </div>
      </div>
      <div class="stat-card">
        <!-- <div class="stat-icon sale"><i class="el-icon-sell"></i></div> -->
        <div class="stat-info">
          <div class="stat-value">{{ stats.saleCount | parseKrw }}</div>
          <div class="stat-label">판매중</div>
        </div>
      </div>
      <div class="stat-card">
        <!-- <div class="stat-icon sold"><i class="el-icon-check"></i></div> -->
        <div class="stat-info">
          <div class="stat-value">{{ stats.soldCount | parseKrw }}</div>
          <div class="stat-label">판매완료</div>
        </div>
      </div>
      <div class="stat-card primary">
        <!-- <div class="stat-icon"><i class="el-icon-money"></i></div> -->
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalSales | parseKrw }}원</div>
          <div class="stat-label">총 거래액</div>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="marketplaceList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column label="이미지" width="80" align="center">
          <template slot-scope="scope">
            <img v-if="scope.row.thumbnailUrl" :src="scope.row.thumbnailUrl" class="product-thumb" />
            <div v-else class="no-image"><i class="el-icon-picture"></i></div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="상품명" />
        <el-table-column prop="channelName" label="커뮤니티" width="150" />
        <el-table-column label="가격" width="120" align="right">
          <template slot-scope="scope">
            {{ scope.row.price | parseKrw }}원
          </template>
        </el-table-column>
        <el-table-column label="상태" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sellerName" label="판매자" width="100" />
        <el-table-column label="등록일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.createdAt | parseDate('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getMarketplaceList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getMarketplaceList, getMarketplaceStats } from '@/api/marketplace';
import { getChannelList } from '@/api/channel';

@Component({
  components: {
    Pagination,
  },
})
export default class MarketplaceIndex extends Vue {
  private loading = false;
  private marketplaceList: any[] = [];
  private channelList: any[] = [];
  private totalElements = 0;
  private stats = {
    totalProducts: 0,
    saleCount: 0,
    soldCount: 0,
    totalSales: 0,
  };

  private listQuery = {
    page: 1,
    size: 10,
    channelUid: '',
    status: '',
    keyword: '',
  };

  created() {
    if (this.$route.query.channelUid) {
      this.listQuery.channelUid = this.$route.query.channelUid as string;
    }
    this.loadData();
  }

  private async loadData() {
    await Promise.all([
      this.getChannelList(),
      this.getMarketplaceList(),
      this.getStats(),
    ]);
  }

  private async getChannelList() {
    try {
      const { data } = await getChannelList({ page: 1, size: 100 });
      this.channelList = data.content || [];
    } catch (error) {
      console.error(error);
    }
  }

  private async getStats() {
    try {
      const { data } = await getMarketplaceStats();
      this.stats = data;
    } catch (error) {
      console.error(error);
    }
  }

  private async getMarketplaceList() {
    this.loading = true;
    try {
      const { data } = await getMarketplaceList(this.listQuery);
      this.marketplaceList = data.content || [];
      this.totalElements = data.totalElements || 0;
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getMarketplaceList();
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.size) - index;
  }

  private getStatusType(status: string) {
    const types: any = {
      ACTIVE: 'success',
      TRADING: 'warning',
      SOLD_OUT: 'info',
      HIDDEN: 'danger',
    };
    return types[status] || 'info';
  }

  private getStatusText(status: string) {
    const texts: any = {
      ACTIVE: '판매중',
      TRADING: '거래중',
      SOLD_OUT: '판매완료',
      HIDDEN: '숨김',
    };
    return texts[status] || status;
  }
}
</script>

<style scoped>
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

.stat-card.primary {
  background: #409EFF;
  color: #fff;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  background: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 24px;
  color: #fff;
}

.stat-card.primary .stat-icon {
  background: rgba(255, 255, 255, 0.2);
}

.stat-icon.sale {
  background: #67C23A;
}

.stat-icon.sold {
  background: #909399;
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
  opacity: 0.8;
  margin-top: 2px;
}

.product-thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

.no-image {
  width: 80px;
  height: 80px;
  background: #f5f7fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}
</style>
