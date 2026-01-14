<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">커뮤니티 상세</p>
        <el-button type="primary" @click="$router.push({ name: 'CommunityList' })">
          <i class="el-icon-arrow-left"></i> 목록으로
        </el-button>
      </div>
    </div>

    <div v-loading="loading" class="detail-content">
      <!-- 기본 정보 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-info"></i> 기본 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>커뮤니티명</label>
            <span>{{ community.name }}</span>
          </div>
          <div class="info-item">
            <label>도메인</label>
            <span>{{ community.domain }}</span>
          </div>
          <div class="info-item">
            <label>관리자</label>
            <span>{{ community.creatorName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>공개여부</label>
            <el-tag :type="community.privateStatus ? 'danger' : 'success'" size="small">
              {{ community.privateStatus ? '비공개' : '공개' }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>생성일</label>
            <span>{{ community.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}</span>
          </div>
          <div class="info-item full">
            <label>소개</label>
            <span>{{ community.introduce || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 통계 정보 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-data-analysis"></i> 통계</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-value">{{ community.memberCount || 0 }}</div>
            <div class="stat-label">가입자 수</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ community.spaceCount || 0 }}</div>
            <div class="stat-label">공간 수</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ community.marketplaceProductCount || 0 }}</div>
            <div class="stat-label">장터 상품</div>
          </div>
          <div class="stat-card primary">
            <div class="stat-value">{{ community.totalSales | parseKrw }}원</div>
            <div class="stat-label">총 매출</div>
          </div>
        </div>
      </div>

      <!-- 공간 목록 -->
      <div class="detail-section">
        <h3 class="section-title">
          <i class="el-icon-folder"></i> 공간 목록
          <router-link class="view-all" :to="{ name: 'SpaceList', query: { channelUid: community.uid } }">전체보기</router-link>
        </h3>
        <el-table
          :data="spaceList"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column prop="name" label="공간명" />
          <el-table-column label="타입" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.spaceType === 'BOARD' ? 'primary' : 'success'" size="small">
                {{ scope.row.spaceType === 'BOARD' ? '게시판' : '채팅' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="memberCount" label="멤버 수" width="100" align="center" />
          <el-table-column label="관리" width="120" align="center">
            <template slot-scope="scope">
              <router-link class="detail-button" :to="{ name: 'SpaceMembers', params: { uid: scope.row.uid } }">가입자</router-link>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 장터 현황 -->
      <div class="detail-section">
        <h3 class="section-title">
          <i class="el-icon-shopping-cart-2"></i> 장터 현황
          <router-link class="view-all" :to="{ name: 'MarketplaceList', query: { channelUid: community.uid } }">전체보기</router-link>
        </h3>
        <el-table
          :data="marketplaceList"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column prop="title" label="상품명" />
          <el-table-column prop="price" label="가격" width="120" align="right">
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
          <el-table-column label="등록일" width="160" align="center">
            <template slot-scope="scope">
              {{ scope.row.createdAt | parseDate('YYYY-MM-DD HH:mm') }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getChannel } from '@/api/channel';
import { getSpacesByChannel } from '@/api/space';
import { getMarketplaceByChannel, getMarketplaceStats } from '@/api/marketplace';

@Component
export default class CommunityDetail extends Vue {
  private loading = false;
  private community: any = {};
  private spaceList: any[] = [];
  private marketplaceList: any[] = [];
  private marketplaceStats: any = { totalProducts: 0, totalSales: 0 };

  created() {
    this.loadData();
  }

  private async loadData() {
    this.loading = true;
    try {
      const uid = this.$route.params.uid;
      
      // 커뮤니티 정보
      const { data: community } = await getChannel(uid);
      this.community = community;

      // 공간 목록
      try {
        const { data: spaces } = await getSpacesByChannel(uid, { page: 1, size: 5 });
        this.spaceList = spaces.content || [];
      } catch (e) {
        this.spaceList = [];
      }

      // 장터 목록
      try {
        const { data: marketplace } = await getMarketplaceByChannel(uid, { page: 1, size: 5 });
        this.marketplaceList = marketplace.content || [];
      } catch (e) {
        this.marketplaceList = [];
      }

    } catch (error) {
      console.error(error);
      this.$message.error('데이터를 불러오는 중 오류가 발생했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private getStatusType(status: string): string {
    const statusMap: Record<string, string> = {
      'ACTIVE': 'success',
      'TRADING': 'warning',
      'SOLD_OUT': 'info',
      'HIDDEN': 'danger'
    };
    return statusMap[status] || 'info';
  }

  private getStatusText(status: string): string {
    const statusMap: Record<string, string> = {
      'ACTIVE': '판매중',
      'TRADING': '거래중',
      'SOLD_OUT': '판매완료',
      'HIDDEN': '숨김'
    };
    return statusMap[status] || status;
  }
}
</script>

<style scoped>
.tl__box {
  display: flex;
  align-items: center;
  gap: 15px;
}

.detail-content {
  margin-top: 20px;
}

.detail-section {
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
  padding-bottom: 10px;
  border-bottom: 2px solid #667eea;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #667eea;
}

.view-all {
  margin-left: auto;
  font-size: 14px;
  color: #667eea;
  font-weight: normal;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item.full {
  grid-column: 1 / -1;
}

.info-item label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 5px;
}

.info-item span {
  font-size: 15px;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 10px;
  padding: 20px;
  text-align: center;
}

.stat-card.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}
</style>
