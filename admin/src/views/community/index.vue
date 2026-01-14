<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">커뮤니티 관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" placeholder="검색조건">
            <el-option label="커뮤니티명" value="name"></el-option>
            <el-option label="도메인" value="domain"></el-option>
            <!-- <el-option label="관리자" value="adminName"></el-option> -->
          </el-select>
          <el-input placeholder="검색어 입력" v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="communityList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="커뮤니티명">
          <template slot-scope="scope">
            <router-link class="link-text" :to="{ name: 'CommunityDetail', params: { uid: scope.row.uid } }">
              {{ scope.row.name }}
            </router-link>
          </template>
        </el-table-column>
        <el-table-column prop="domain" label="도메인" />
        <el-table-column label="멤버 수" width="100" align="center">
          <template slot-scope="scope">
            <span class="member-count">{{ scope.row.memberCount || 0 }}명</span>
          </template>
        </el-table-column>
        <el-table-column label="공간 수" width="100" align="center">
          <template slot-scope="scope">
            {{ scope.row.spaceCount || 0 }}개
          </template>
        </el-table-column>
        <el-table-column label="장터 상품" width="100" align="center">
          <template slot-scope="scope">
            {{ scope.row.marketplaceProductCount || 0 }}개
          </template>
        </el-table-column>
        <el-table-column label="공개여부" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.privateStatus ? 'danger' : 'success'" size="small">
              {{ scope.row.privateStatus ? '비공개' : '공개' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="생성일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="가입자" width="80" align="center">
          <template slot-scope="scope">
            <router-link class="action-button" :to="{ name: 'CommunityMembers', params: { uid: scope.row.uid } }">
              보기
            </router-link>
          </template>
        </el-table-column>
        <el-table-column label="관리" width="150" align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              <router-link class="action-button" :to="{ name: 'CommunityDetail', params: { uid: scope.row.uid } }">상세</router-link>
              <a class="action-button danger" @click="handleDelete(scope.row)">삭제</a>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getCommunityList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getChannelList, deleteChannel } from '@/api/channel';

@Component({
  components: {
    Pagination,
  },
})
export default class CommunityIndex extends Vue {
  private loading = false;
  private communityList: any[] = [];
  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 10,
    searchType: 'name',
    searchValue: '',
  };

  created() {
    this.getCommunityList();
  }

  private async getCommunityList() {
    this.loading = true;
    try {
      const { data } = await getChannelList(this.listQuery);
      this.communityList = data.content;
      this.totalElements = data.totalElements;
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getCommunityList();
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.size) - index;
  }

  private async handleDelete(row: any) {
    try {
      await this.$confirm('해당 커뮤니티를 삭제하시겠습니까?', '확인', {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning',
      });
      await deleteChannel(row.uid);
      this.$message.success('삭제되었습니다.');
      this.getCommunityList();
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error('삭제 중 오류가 발생했습니다.');
      }
    }
  }
}
</script>

<style scoped>
.link-text {
  color: #409EFF;
  font-weight: 500;
}
.link-text:hover {
  text-decoration: underline;
}
.link-button {
  display: inline-block;
  padding: 6px 12px;
  background: #409EFF;
  color: white;
  border-radius: 3px;
  font-size: 12px;
  text-decoration: none;
  transition: all 0.3s;
  border: none;
}
.link-button:hover {
  background: #66b1ff;
}
.member-count {
  color: #409EFF;
  font-weight: 600;
}
.action-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.action-button {
  display: inline-block;
  padding: 6px 12px;
  background: #409EFF;
  color: white;
  border-radius: 3px;
  font-size: 12px;
  text-decoration: none;
  transition: all 0.3s;
  white-space: nowrap;
  border: none;
  cursor: pointer;
}

.action-button:hover {
  background: #66b1ff;
}

.action-button.danger {
  background: #F56C6C;
}

.action-button.danger:hover {
  background: #f78989;
}
</style>
