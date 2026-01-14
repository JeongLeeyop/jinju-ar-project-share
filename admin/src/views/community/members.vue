<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <el-button icon="el-icon-arrow-left" @click="goBack">뒤로</el-button>
        <div class="header-info">
          <h1 class="page-title">가입자 목록</h1>
          <p class="page-subtitle">{{ communityName }} - 총 {{ totalElements }}명</p>
        </div>
      </div>
      <div class="header-right">
        <el-input
          v-model="listQuery.searchValue"
          placeholder="이름 또는 이메일 검색"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter.native="handleSearch"
        />
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="memberList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column prop="actualName" label="이름" width="120" />
        <el-table-column prop="email" label="이메일" min-width="200" />
        <el-table-column prop="concatNumber" label="연락처" width="150" />
        <el-table-column label="가입일" width="150" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.joinDate || scope.row.createDate) }}
          </template>
        </el-table-column>
        <el-table-column label="상태" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.approved ? 'success' : 'warning'" size="small">
              {{ scope.row.approved ? '승인됨' : '대기중' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="관리" width="120" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="viewMemberDetail(scope.row)">
              상세
            </el-button>
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
import { getChannelMembers, getChannelDetail } from '@/api/channel';

@Component({
  name: 'CommunityMembers',
})
export default class extends Vue {
  private loading = false;
  private memberList: any[] = [];
  private totalElements = 0;
  private communityName = '';
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
    this.loadCommunityInfo();
    this.loadMembers();
  }

  private async loadCommunityInfo() {
    const uid = this.$route.params.uid;
    try {
      const res = await getChannelDetail(uid);
      this.communityName = res.data.name;
    } catch (error) {
      console.error('Failed to load community info:', error);
    }
  }

  private async loadMembers() {
    const uid = this.$route.params.uid;
    if (!uid) return;
    
    this.loading = true;
    try {
      const res = await getChannelMembers(uid, this.listQuery);
      this.memberList = res.data.content || [];
      this.totalElements = res.data.totalElements || 0;
    } catch (error) {
      console.error('Failed to load members:', error);
      this.$message.error('가입자 목록을 불러오는데 실패했습니다.');
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

  private viewMemberDetail(row: any) {
    this.$router.push({ name: 'MemberDetail', params: { uid: row.userUid || row.uid } });
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
  
  .search-input {
    width: 280px;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
