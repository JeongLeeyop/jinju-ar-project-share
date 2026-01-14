<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">커뮤니티 관리</h1>
        <p class="page-subtitle">총 {{ totalElements }}개의 커뮤니티</p>
      </div>
      <div class="header-right">
        <el-input
          v-model="listQuery.searchValue"
          placeholder="커뮤니티명 검색"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter.native="handleSearch"
        />
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="communityList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column prop="name" label="커뮤니티명" min-width="200">
          <template slot-scope="scope">
            <div class="community-name">
              <img v-if="scope.row.iconFileUid" :src="getImageUrl(scope.row.iconFileUid)" class="community-icon" />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="domain" label="도메인" width="150" />
        <el-table-column label="가입자 수" width="120" align="center">
          <template slot-scope="scope">
            <span class="member-count">{{ scope.row.memberCount || 0 }}명</span>
          </template>
        </el-table-column>
        <el-table-column label="공개여부" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.privateStatus ? 'danger' : 'success'" size="small">
              {{ scope.row.privateStatus ? '비공개' : '공개' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="생성일" width="150" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createDate) }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="200" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="viewMembers(scope.row)">
              가입자 목록
            </el-button>
            <el-button type="danger" size="mini" @click="handleDelete(scope.row)">
              삭제
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
import { getAllChannels, deleteChannel } from '@/api/channel';

@Component({
  name: 'CommunityList',
})
export default class extends Vue {
  private loading = false;
  private communityList: any[] = [];
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
    this.loadCommunities();
  }

  private async loadCommunities() {
    this.loading = true;
    try {
      const res = await getAllChannels(this.listQuery);
      this.communityList = res.data.content || [];
      this.totalElements = res.data.totalElements || 0;
    } catch (error) {
      console.error('Failed to load communities:', error);
      this.$message.error('커뮤니티 목록을 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.loadCommunities();
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadCommunities();
  }

  private viewMembers(row: any) {
    this.$router.push({ name: 'CommunityMembers', params: { uid: row.uid } });
  }

  private async handleDelete(row: any) {
    try {
      await this.$confirm(`'${row.name}' 커뮤니티를 삭제하시겠습니까?`, '삭제 확인', {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
      });
      
      await deleteChannel(row.uid);
      this.$message.success('커뮤니티가 삭제되었습니다.');
      this.loadCommunities();
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error('삭제에 실패했습니다.');
      }
    }
  }

  private getImageUrl(uid: string) {
    return `${process.env.VUE_APP_BASE_API}/attached-file/${uid}`;
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

.community-name {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .community-icon {
    width: 36px;
    height: 36px;
    border-radius: 8px;
    object-fit: cover;
  }
}

.member-count {
  color: #073DFF;
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
