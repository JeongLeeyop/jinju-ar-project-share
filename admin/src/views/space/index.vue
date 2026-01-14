<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">공간 조회</p>
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
          <el-select v-model="listQuery.spaceType" placeholder="타입" clearable @change="handleSearch">
            <el-option label="게시판" value="BOARD"></el-option>
            <el-option label="채팅" value="CHAT"></el-option>
          </el-select>
          <el-input placeholder="공간명 검색" v-model="listQuery.keyword" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="spaceList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="공간명" />
        <el-table-column prop="channelName" label="소속 커뮤니티" />
        <el-table-column label="타입" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.spaceType === 'BOARD' ? 'primary' : 'success'" size="small">
              {{ scope.row.spaceType === 'BOARD' ? '게시판' : '채팅' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="멤버 수" width="100" align="center">
          <template slot-scope="scope">
            <span class="member-count">{{ scope.row.memberCount || 0 }}명</span>
          </template>
        </el-table-column>
        <el-table-column label="공개여부" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isPublic ? 'success' : 'info'" size="small">
              {{ scope.row.isPublic ? '공개' : '비공개' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="생성일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.createdAt | parseDate('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="120" align="center">
          <template slot-scope="scope">
            <router-link class="detail-button" :to="{ name: 'SpaceMembers', params: { uid: scope.row.uid } }">가입자</router-link>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getSpaceList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getSpaceList } from '@/api/space';
import { getChannelList } from '@/api/channel';

@Component({
  components: {
    Pagination,
  },
})
export default class SpaceIndex extends Vue {
  private loading = false;
  private spaceList: any[] = [];
  private channelList: any[] = [];
  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 10,
    channelUid: '',
    spaceType: '',
    keyword: '',
  };

  created() {
    // URL에서 channelUid 파라미터 확인
    if (this.$route.query.channelUid) {
      this.listQuery.channelUid = this.$route.query.channelUid as string;
    }
    this.loadData();
  }

  private async loadData() {
    await this.getChannelList();
    await this.getSpaceList();
  }

  private async getChannelList() {
    try {
      const { data } = await getChannelList({ page: 1, size: 100 });
      this.channelList = data.content || [];
    } catch (error) {
      console.error(error);
    }
  }

  private async getSpaceList() {
    this.loading = true;
    try {
      const { data } = await getSpaceList(this.listQuery);
      this.spaceList = data.content || [];
      this.totalElements = data.totalElements || 0;
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getSpaceList();
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.size) - index;
  }
}
</script>

<style scoped>
.member-count {
  color: #409EFF;
  font-weight: 600;
}
.detail-button {
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
}
.detail-button:hover {
  background: #66b1ff;
}
</style>
