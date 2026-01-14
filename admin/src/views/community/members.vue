<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">가입자 목록</p>
        <p class="usernumber">{{ communityName }} - 총 {{ totalElements | parseKrw }}명</p>
        <el-button type="primary" @click="$router.push({ name: 'CommunityList' })" style="margin-left: auto;">
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
        <el-table-column label="권한" width="350">
          <template slot-scope="scope">
            <div class="permissions-list">
              <el-tag
                v-for="perm in scope.row.permissions"
                :key="perm.permissionType"
                :type="perm.hasPermission ? 'success' : 'info'"
                size="mini"
                effect="plain"
                class="permission-tag"
              >
                {{ perm.description }}
              </el-tag>
              <span v-if="!scope.row.permissions || scope.row.permissions.length === 0" class="no-permission">
                권한 없음
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="가입일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm') }}
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
.tl__box {
  display: flex;
  align-items: center;
  gap: 15px;
}
.permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  justify-content: flex-start;
  align-items: center;
}
.permission-tag {
  margin: 0;
}
.no-permission {
  color: #999;
  font-size: 12px;
}
</style>
