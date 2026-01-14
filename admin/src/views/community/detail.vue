<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack">뒤로</el-button>
        <div class="header-info">
          <h1 class="page-title">커뮤니티 상세</h1>
          <p class="page-subtitle">{{ community.name }}</p>
        </div>
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <div class="detail-section">
        <h3 class="section-title">기본 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">커뮤니티명</span>
            <span class="value">{{ community.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">도메인</span>
            <span class="value">{{ community.domain }}</span>
          </div>
          <div class="info-item">
            <span class="label">공개여부</span>
            <el-tag :type="community.privateStatus ? 'danger' : 'success'">
              {{ community.privateStatus ? '비공개' : '공개' }}
            </el-tag>
          </div>
          <div class="info-item">
            <span class="label">생성일</span>
            <span class="value">{{ formatDate(community.createDate) }}</span>
          </div>
          <div class="info-item full-width">
            <span class="label">소개</span>
            <span class="value">{{ community.introduce || '-' }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getChannelDetail } from '@/api/channel';

@Component({
  name: 'CommunityDetail',
})
export default class extends Vue {
  private loading = false;
  private community: any = {};

  mounted() {
    this.loadCommunity();
  }

  private async loadCommunity() {
    const uid = this.$route.params.uid;
    if (!uid) return;
    
    this.loading = true;
    try {
      const res = await getChannelDetail(uid);
      this.community = res.data;
    } catch (error) {
      this.$message.error('커뮤니티 정보를 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private goBack() {
    this.$router.back();
  }

  private formatDate(date: string) {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('ko-KR');
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
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
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
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #073DFF;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  &.full-width {
    grid-column: span 2;
  }
  
  .label {
    font-size: 13px;
    color: #888;
    font-weight: 500;
  }
  
  .value {
    font-size: 15px;
    color: #222;
  }
}
</style>
