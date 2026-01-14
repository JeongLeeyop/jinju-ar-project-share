<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">커뮤니티 공간</h1>
        <p class="page-subtitle">총 {{ totalElements }}개의 공간</p>
      </div>
      <div class="header-right">
        <el-select v-model="listQuery.channelUid" placeholder="커뮤니티 선택" clearable @change="handleSearch" class="channel-select">
          <el-option v-for="channel in channelList" :key="channel.uid" :label="channel.name" :value="channel.uid" />
        </el-select>
        <el-input
          v-model="listQuery.searchValue"
          placeholder="공간명 검색"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter.native="handleSearch"
        />
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="spaceList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column prop="name" label="공간명" min-width="180" />
        <el-table-column label="소속 커뮤니티" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.channelName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="공간 유형" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getSpaceTypeTag(scope.row.spaceType)" size="small">
              {{ getSpaceTypeName(scope.row.spaceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="가입자 수" width="100" align="center">
          <template slot-scope="scope">
            <span class="member-count">{{ scope.row.memberCount || 0 }}명</span>
          </template>
        </el-table-column>
        <el-table-column label="생성일" width="150" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createDate) }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="120" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click="viewMembers(scope.row)">
              가입자 목록
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
import { getSpaceList, getAllSpaces } from '@/api/space';
import { getAllChannels } from '@/api/channel';

@Component({
  name: 'SpaceList',
})
export default class extends Vue {
  private loading = false;
  private spaceList: any[] = [];
  private channelList: any[] = [];
  private totalElements = 0;
  private listQuery = {
    page: 1,
    size: 10,
    searchValue: '',
    channelUid: '',
  };

  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  mounted() {
    this.loadChannels();
    this.loadSpaces();
  }

  private async loadChannels() {
    try {
      const res = await getAllChannels({ page: 1, size: 100 });
      this.channelList = res.data.content || [];
    } catch (error) {
      console.error('Failed to load channels:', error);
    }
  }

  private async loadSpaces() {
    this.loading = true;
    try {
      let res;
      if (this.listQuery.channelUid) {
        res = await getSpaceList(this.listQuery.channelUid, this.listQuery);
      } else {
        res = await getAllSpaces(this.listQuery);
      }
      this.spaceList = res.data.content || res.data || [];
      this.totalElements = res.data.totalElements || this.spaceList.length;
    } catch (error) {
      console.error('Failed to load spaces:', error);
      this.$message.error('공간 목록을 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.loadSpaces();
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadSpaces();
  }

  private viewMembers(row: any) {
    this.$router.push({ name: 'SpaceMembers', params: { uid: row.uid } });
  }

  private getSpaceTypeName(type: string) {
    const types: { [key: string]: string } = {
      FEED: '피드',
      BOARD: '게시판',
      VIDEO: '영상',
      LESSION: '강좌',
    };
    return types[type] || type || '일반';
  }

  private getSpaceTypeTag(type: string) {
    const tags: { [key: string]: string } = {
      FEED: 'success',
      BOARD: 'primary',
      VIDEO: 'warning',
      LESSION: 'danger',
    };
    return tags[type] || 'info';
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
  
  .channel-select {
    width: 200px;
  }
  
  .search-input {
    width: 200px;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
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
