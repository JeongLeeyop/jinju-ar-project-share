<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack" class="back-btn">목록으로</el-button>
        <h1 class="page-title">회원 상세 정보</h1>
      </div>
    </div>

    <div class="member-profile" v-loading="loading">
      <div class="profile-avatar">{{ getInitial(memberInfo.username) }}</div>
      <div class="profile-info">
        <h2 class="profile-name">{{ memberInfo.username || '-' }}</h2>
        <p class="profile-email">{{ memberInfo.email || '-' }}</p>
        <div class="profile-meta">
          <span><i class="el-icon-phone"></i> {{ formatPhone(memberInfo.phone) }}</span>
          <span><i class="el-icon-date"></i> {{ formatDate(memberInfo.createDate) }} 가입</span>
        </div>
      </div>
      <div class="profile-point">
        <span class="point-label">보유 포인트</span>
        <span class="point-value">{{ formatNumber(memberInfo.point) }}P</span>
      </div>
    </div>

    <div class="content-section">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="가입 커뮤니티" name="channels">
          <div class="tab-content" v-loading="channelLoading">
            <el-table :data="channelList" style="width: 100%" :header-cell-style="headerStyle">
              <el-table-column type="index" label="번호" width="70" align="center" />
              <el-table-column prop="name" label="커뮤니티명" min-width="200" />
              <el-table-column label="역할" width="100" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
                    {{ scope.row.role === 'ADMIN' ? '관리자' : '회원' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="가입일" width="150" align="center">
                <template slot-scope="scope">
                  {{ formatDate(scope.row.joinDate) }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="channelList.length === 0" class="empty-state">
              가입된 커뮤니티가 없습니다.
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="포인트 히스토리" name="points">
          <div class="tab-content" v-loading="pointLoading">
            <el-table :data="pointHistory" style="width: 100%" :header-cell-style="headerStyle">
              <el-table-column type="index" label="번호" width="70" align="center" />
              <el-table-column prop="description" label="내용" min-width="200" />
              <el-table-column label="변동" width="120" align="right">
                <template slot-scope="scope">
                  <span :class="scope.row.amount > 0 ? 'point-plus' : 'point-minus'">
                    {{ scope.row.amount > 0 ? '+' : '' }}{{ formatNumber(scope.row.amount) }}P
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="잔액" width="120" align="right">
                <template slot-scope="scope">
                  {{ formatNumber(scope.row.balance) }}P
                </template>
              </el-table-column>
              <el-table-column label="일시" width="180" align="center">
                <template slot-scope="scope">
                  {{ formatDateTime(scope.row.createDate) }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="pointHistory.length === 0" class="empty-state">
              포인트 내역이 없습니다.
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="활동 내역" name="activities">
          <div class="tab-content" v-loading="activityLoading">
            <el-table :data="activityList" style="width: 100%" :header-cell-style="headerStyle">
              <el-table-column type="index" label="번호" width="70" align="center" />
              <el-table-column label="활동유형" width="120">
                <template slot-scope="scope">
                  <el-tag :type="getActivityType(scope.row.type)" size="small">
                    {{ getActivityName(scope.row.type) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="title" label="내용" min-width="200" />
              <el-table-column prop="channelName" label="커뮤니티" width="150" />
              <el-table-column label="일시" width="180" align="center">
                <template slot-scope="scope">
                  {{ formatDateTime(scope.row.createDate) }}
                </template>
              </el-table-column>
            </el-table>
            <div v-if="activityList.length === 0" class="empty-state">
              활동 내역이 없습니다.
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getUserDetail, getUserPointHistory, getUserActivity, getUserChannels } from '@/api/user';

@Component({
  name: 'MemberDetail',
})
export default class extends Vue {
  private loading = false;
  private channelLoading = false;
  private pointLoading = false;
  private activityLoading = false;
  private activeTab = 'channels';
  
  private memberInfo: any = {};
  private channelList: any[] = [];
  private pointHistory: any[] = [];
  private activityList: any[] = [];

  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  get memberUid() {
    return this.$route.params.uid;
  }

  mounted() {
    this.loadMemberInfo();
    this.loadChannels();
  }

  private async loadMemberInfo() {
    this.loading = true;
    try {
      const res = await getUserDetail(this.memberUid);
      this.memberInfo = res.data || {};
    } catch (error) {
      console.error('Failed to load member info:', error);
      this.$message.error('회원 정보를 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private async loadChannels() {
    this.channelLoading = true;
    try {
      const res = await getUserChannels(this.memberUid);
      this.channelList = res.data || [];
    } catch (error) {
      console.error('Failed to load channels:', error);
    } finally {
      this.channelLoading = false;
    }
  }

  private async loadPointHistory() {
    this.pointLoading = true;
    try {
      const res = await getUserPointHistory(this.memberUid, { page: 1, size: 20 });
      this.pointHistory = res.data.content || res.data || [];
    } catch (error) {
      console.error('Failed to load point history:', error);
    } finally {
      this.pointLoading = false;
    }
  }

  private async loadActivityList() {
    this.activityLoading = true;
    try {
      const res = await getUserActivity(this.memberUid, { page: 1, size: 20 });
      this.activityList = res.data.content || res.data || [];
    } catch (error) {
      console.error('Failed to load activity list:', error);
    } finally {
      this.activityLoading = false;
    }
  }

  private handleTabChange(tab: any) {
    switch (tab.name) {
      case 'channels':
        if (this.channelList.length === 0) this.loadChannels();
        break;
      case 'points':
        if (this.pointHistory.length === 0) this.loadPointHistory();
        break;
      case 'activities':
        if (this.activityList.length === 0) this.loadActivityList();
        break;
    }
  }

  private goBack() {
    this.$router.push('/member');
  }

  private getInitial(name: string) {
    return (name || 'U').charAt(0).toUpperCase();
  }

  private formatPhone(phone: string) {
    if (!phone) return '-';
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
  }

  private formatNumber(num: number) {
    return (num || 0).toLocaleString();
  }

  private formatDate(date: string) {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('ko-KR');
  }

  private formatDateTime(date: string) {
    if (!date) return '-';
    return new Date(date).toLocaleString('ko-KR');
  }

  private getActivityName(type: string) {
    const types: { [key: string]: string } = {
      POST: '게시글',
      COMMENT: '댓글',
      LIKE: '좋아요',
      PURCHASE: '구매',
      SALE: '판매',
    };
    return types[type] || type || '활동';
  }

  private getActivityType(type: string) {
    const types: { [key: string]: string } = {
      POST: 'primary',
      COMMENT: 'success',
      LIKE: 'warning',
      PURCHASE: 'info',
      SALE: 'danger',
    };
    return types[type] || 'primary';
  }
}
</script>

<style scoped lang="scss">
.page-wrap {
  padding: 32px;
}

.page-header {
  margin-bottom: 24px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .back-btn {
    border-radius: 8px;
  }
  
  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #222;
  }
}

.member-profile {
  background: linear-gradient(135deg, #073DFF 0%, #5B7FFF 100%);
  border-radius: 16px;
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  
  .profile-avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    font-weight: 700;
  }
  
  .profile-info {
    flex: 1;
    
    .profile-name {
      font-size: 24px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 4px;
    }
    
    .profile-email {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.8);
      margin-bottom: 12px;
    }
    
    .profile-meta {
      display: flex;
      gap: 24px;
      
      span {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.8);
        
        i {
          margin-right: 6px;
        }
      }
    }
  }
  
  .profile-point {
    text-align: right;
    
    .point-label {
      display: block;
      font-size: 13px;
      color: rgba(255, 255, 255, 0.8);
      margin-bottom: 4px;
    }
    
    .point-value {
      font-size: 28px;
      font-weight: 700;
      color: #fff;
    }
  }
}

.content-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.tab-content {
  min-height: 300px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #888;
  font-size: 14px;
}

.point-plus {
  color: #10B981;
  font-weight: 600;
}

.point-minus {
  color: #EF4444;
  font-weight: 600;
}

::v-deep .el-tabs__nav-wrap::after {
  display: none;
}

::v-deep .el-tabs__item {
  font-size: 15px;
  font-weight: 500;
  
  &.is-active {
    color: #073DFF;
  }
}

::v-deep .el-tabs__active-bar {
  background-color: #073DFF;
}
</style>
