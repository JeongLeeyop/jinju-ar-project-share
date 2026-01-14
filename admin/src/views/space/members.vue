<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">공간 가입자 목록</p>
        <p class="usernumber">{{ spaceName }} - 총 {{ totalElements | parseKrw }}명</p>
        <el-button type="primary" @click="$router.push({ name: 'SpaceList' })" style="margin-left: auto;">
          <i class="el-icon-arrow-left"></i> 목록으로
        </el-button>
      </div>

      <div class="user__tab small"></div>
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
import { getSpace, getSpaceMembers } from '@/api/space';

@Component({
  components: {
    Pagination,
  },
})
export default class SpaceMembers extends Vue {
  private loading = false;
  private spaceName = '';
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
      
      // 공간 정보
      const { data: space } = await getSpace(uid);
      this.spaceName = space.name;

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
      const { data } = await getSpaceMembers(uid, this.listQuery);
      this.memberList = data.content || [];
      this.totalElements = data.totalElements || 0;
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
.tl__box {
  display: flex;
  align-items: center;
  gap: 15px;
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
