<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">대시보드</h1>
      <p class="dashboard-subtitle">진주알 커뮤니티 관리 시스템에 오신 것을 환영합니다.</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon community">
          <i class="el-icon-s-home"></i>
        </div>
        <div class="stat-info">
          <h3 class="stat-value">{{ stats.communityCount }}</h3>
          <p class="stat-label">전체 커뮤니티</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon space">
          <i class="el-icon-office-building"></i>
        </div>
        <div class="stat-info">
          <h3 class="stat-value">{{ stats.spaceCount }}</h3>
          <p class="stat-label">전체 공간</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon member">
          <i class="el-icon-user"></i>
        </div>
        <div class="stat-info">
          <h3 class="stat-value">{{ stats.memberCount }}</h3>
          <p class="stat-label">전체 회원</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon sales">
          <i class="el-icon-shopping-cart-2"></i>
        </div>
        <div class="stat-info">
          <h3 class="stat-value">{{ formatNumber(stats.totalSales) }}원</h3>
          <p class="stat-label">총 매출</p>
        </div>
      </div>
    </div>
    
    <div class="quick-links">
      <h2 class="section-title">빠른 메뉴</h2>
      <div class="links-grid">
        <router-link :to="{ name: 'CommunityList' }" class="link-card">
          <i class="el-icon-s-home"></i>
          <span>커뮤니티 관리</span>
        </router-link>
        <router-link :to="{ name: 'SpaceList' }" class="link-card">
          <i class="el-icon-office-building"></i>
          <span>공간 조회</span>
        </router-link>
        <router-link :to="{ name: 'MarketplaceList' }" class="link-card">
          <i class="el-icon-shopping-cart-2"></i>
          <span>장터 조회</span>
        </router-link>
        <router-link :to="{ name: 'MemberList' }" class="link-card">
          <i class="el-icon-user"></i>
          <span>회원 관리</span>
        </router-link>
        <router-link :to="{ name: 'SmsSend' }" class="link-card">
          <i class="el-icon-message"></i>
          <span>문자 발송</span>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  name: 'Dashboard',
})
export default class extends Vue {
  private stats = {
    communityCount: 0,
    spaceCount: 0,
    memberCount: 0,
    totalSales: 0,
  };

  mounted() {
    this.loadStats();
  }

  private async loadStats() {
    // TODO: API 연동 시 실제 데이터 조회
    this.stats = {
      communityCount: 12,
      spaceCount: 45,
      memberCount: 1234,
      totalSales: 5678900,
    };
  }

  private formatNumber(num: number) {
    return num.toLocaleString();
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 32px;
  
  .dashboard-title {
    font-size: 28px;
    font-weight: 700;
    color: #222;
    margin-bottom: 8px;
  }
  
  .dashboard-subtitle {
    font-size: 16px;
    color: #666;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 40px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: 600px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  i {
    font-size: 28px;
    color: #fff;
  }
  
  &.community {
    background: linear-gradient(135deg, #073DFF 0%, #5B7FFF 100%);
  }
  
  &.space {
    background: linear-gradient(135deg, #10B981 0%, #34D399 100%);
  }
  
  &.member {
    background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
  }
  
  &.sales {
    background: linear-gradient(135deg, #EF4444 0%, #F87171 100%);
  }
}

.stat-info {
  .stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #222;
    margin-bottom: 4px;
  }
  
  .stat-label {
    font-size: 14px;
    color: #888;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #222;
  margin-bottom: 20px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  
  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }
}

.link-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  
  i {
    font-size: 32px;
    color: #073DFF;
  }
  
  span {
    font-size: 14px;
    font-weight: 600;
    color: #444;
  }
  
  &:hover {
    background: #073DFF;
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(7, 61, 255, 0.3);
    
    i, span {
      color: #fff;
    }
  }
}
</style>
