<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">회원 관리</h1>
        <p class="page-subtitle">총 {{ totalElements }}명의 회원</p>
      </div>
      <div class="header-right">
        <el-input
          v-model="listQuery.searchValue"
          placeholder="이름, 이메일로 검색"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter.native="handleSearch"
        />
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">검색</el-button>
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="memberList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column label="회원 정보" min-width="250">
          <template slot-scope="scope">
            <div class="member-info">
              <div class="avatar">{{ getInitial(scope.row.username) }}</div>
              <div class="member-text">
                <span class="member-name">{{ scope.row.username || '-' }}</span>
                <span class="member-email">{{ scope.row.email || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="연락처" width="140">
          <template slot-scope="scope">
            {{ formatPhone(scope.row.phone) }}
          </template>
        </el-table-column>
        <el-table-column label="가입 커뮤니티" width="120" align="center">
          <template slot-scope="scope">
            <span class="count-badge">{{ scope.row.channelCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="보유 포인트" width="120" align="right">
          <template slot-scope="scope">
            <span class="point">{{ formatNumber(scope.row.point) }}P</span>
          </template>
        </el-table-column>
        <el-table-column label="가입일" width="150" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createDate) }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="goToDetail(scope.row.uid)">상세</el-button>
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
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getUserList } from '@/api/user';

@Component({
  name: 'MemberList',
})
export default class extends Vue {
  private loading = false;
  private memberList: any[] = [];
  private totalElements = 0;
  private listQuery = {
    page: 1,
    size: 10,
    searchValue: '',
  };

  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  mounted() {
    this.loadMembers();
  }

  private async loadMembers() {
    this.loading = true;
    try {
      const res = await getUserList(this.listQuery);
      this.memberList = res.data.content || res.data || [];
      this.totalElements = res.data.totalElements || this.memberList.length;
    } catch (error) {
      console.error('Failed to load members:', error);
      this.$message.error('회원 목록을 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.loadMembers();
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadMembers();
  }

  private goToDetail(uid: string) {
    this.$router.push(`/member/detail/${uid}`);
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
  }
  
  .search-input {
    width: 250px;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, #073DFF 0%, #5B7FFF 100%);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: 16px;
  }
  
  .member-text {
    display: flex;
    flex-direction: column;
    gap: 2px;
    
    .member-name {
      font-weight: 600;
      color: #222;
    }
    
    .member-email {
      font-size: 12px;
      color: #888;
    }
  }
}

.count-badge {
  display: inline-block;
  background: #f0f4ff;
  color: #073DFF;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 13px;
}

.point {
  font-weight: 600;
  color: #073DFF;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
