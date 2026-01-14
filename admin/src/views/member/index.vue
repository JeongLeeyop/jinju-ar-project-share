<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">회원관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}명</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" placeholder="검색조건">
            <el-option label="이름" value="actualName"></el-option>
            <el-option label="연락처" value="concatNumber"></el-option>
            <el-option label="이메일" value="email"></el-option>
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
        :data="memberList"
      >
        <el-table-column label="번호" width="70" align="center">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="actualName" label="이름">
          <template slot-scope="scope">
            <router-link class="link-text" :to="{ name: 'MemberDetail', params: { uid: scope.row.uid } }">
              {{ scope.row.actualName }}
            </router-link>
            <el-tag v-if="scope.row.provider === 'KAKAO'" type="warning" effect="dark" size="mini" style="margin-left: 5px">카카오</el-tag>
            <el-tag v-else-if="scope.row.provider === 'NAVER'" type="success" effect="dark" size="mini" style="margin-left: 5px">네이버</el-tag>
            <el-tag v-else-if="scope.row.provider === 'APPLE'" type="info" effect="dark" size="mini" style="margin-left: 5px">애플</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="이메일" />
        <el-table-column prop="concatNumber" label="연락처" width="140" />
        <el-table-column label="가입 커뮤니티" width="120" align="center">
          <template slot-scope="scope">
            <span class="count-badge">{{ scope.row.channelCount || 0 }}개</span>
          </template>
        </el-table-column>
        <el-table-column label="포인트" width="100" align="right">
          <template slot-scope="scope">
            <span class="point">{{ scope.row.point | parseKrw }}P</span>
          </template>
        </el-table-column>
        <el-table-column label="가입일" width="160" align="center">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="120" align="center">
          <template slot-scope="scope">
            <router-link class="detail-button" :to="{ name: 'MemberDetail', params: { uid: scope.row.uid } }">상세</router-link>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">탈퇴</el-button>
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
import { getMemberList, deleteMember } from '@/api/member';

@Component({
  components: {
    Pagination,
  },
})
export default class MemberIndex extends Vue {
  private loading = false;
  private memberList: any[] = [];
  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 10,
    searchType: 'actualName',
    searchValue: '',
  };

  created() {
    this.getMemberList();
  }

  private async getMemberList() {
    this.loading = true;
    try {
      const { data } = await getMemberList(this.listQuery);
      this.memberList = data.content || [];
      this.totalElements = data.totalElements || 0;
    } catch (error) {
      console.error(error);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getMemberList();
  }

  private getNumber(index: number) {
    return this.totalElements - ((this.listQuery.page - 1) * this.listQuery.size) - index;
  }

  private async handleDelete(row: any) {
    try {
      await this.$confirm('해당 회원을 탈퇴 처리하시겠습니까?', '확인', {
        confirmButtonText: '탈퇴',
        cancelButtonText: '취소',
        type: 'warning',
      });
      await deleteMember(row.uid);
      this.$message.success('탈퇴 처리되었습니다.');
      this.getMemberList();
    } catch (error) {
      if (error !== 'cancel') {
        this.$message.error('처리 중 오류가 발생했습니다.');
      }
    }
  }
}
</script>

<style scoped>
.link-text {
  color: #667eea;
  font-weight: 500;
}
.link-text:hover {
  text-decoration: underline;
}
.count-badge {
  background: #667eea;
  color: #fff;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
}
.point {
  color: #667eea;
  font-weight: 600;
}
</style>
