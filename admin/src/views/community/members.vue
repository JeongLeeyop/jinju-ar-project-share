<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">가입자 목록</p>
        <p class="usernumber">{{ communityName }} - 총 {{ totalElements | parseKrw }}명</p>
      </div>

      <div class="user__tab small">
        <router-link class="back-btn" :to="{ name: 'CommunityList' }">
          <i class="el-icon-arrow-left"></i> 목록으로
        </router-link>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#667eea', color: '#fff', padding: '12px 0' }"
        :data="memberList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="actualName" label="이름" />
        <el-table-column prop="email" label="이메일" />
        <el-table-column prop="concatNumber" label="연락처" width="140" />
        <el-table-column label="가입 유형" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.provider === 'KAKAO'" type="warning" effect="dark" size="mini">카카오</el-tag>
            <el-tag v-else-if="scope.row.provider === 'NAVER'" type="success" effect="dark" size="mini">네이버</el-tag>
            <el-tag v-else-if="scope.row.provider === 'APPLE'" type="info" effect="dark" size="mini">애플</el-tag>
            <el-tag v-else type="primary" effect="plain" size="mini">일반</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="권한" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isAdmin ? 'danger' : 'info'" size="small">
              {{ scope.row.isAdmin ? '관리자' : '멤버' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="가입일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.joinDate | parseDate('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="100" align="center">
          <template slot-scope="scope">
            <router-link class="detail-button" :to="{ name: 'MemberDetail', params: { uid: scope.row.userUid } }">상세</router-link>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getMemberList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getChannel, getChannelMembers } from '@/api/channel';

@Component({
  components: {
    Pagination,
  },
})
export default class CommunityMembers extends Vue {
  private loading = false;
  private communityName = '';
  private memberList: any[] = [];
  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 10,
  };

  created() {
    this.loadData();
  }

  private async loadData() {
    this.loading = true;
    try {
      const uid = this.$route.params.uid;
      
      // 커뮤니티 이름
      const { data: community } = await getChannel(uid);
      this.communityName = community.name;

      // 멤버 목록
      await this.getMemberList();
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private async getMemberList() {
    try {
      const uid = this.$route.params.uid;
      const { data } = await getChannelMembers(uid, this.listQuery);
      this.memberList = data.content;
      this.totalElements = data.totalElements;
    } catch (error) {
      console.error(error);
    }
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.size) - index;
  }
}
</script>

<style scoped>
.back-btn {
  font-size: 14px;
  color: #667eea;
  background: #fff;
  padding: 8px 16px;
  border-radius: 6px;
}
.back-btn:hover {
  background: #667eea;
  color: #fff;
}
</style>
