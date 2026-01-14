<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">매니저 관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}명</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" slot="prepend" placeholder="">
            <el-option label="이름" value="actualName"></el-option>
            <el-option label="생년월일" value="birth"></el-option>
            <el-option label="연락처" value="concatNumber"></el-option>
          </el-select>
          <el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()">
            <img src="~@/assets/images/search.png" alt="">
          </el-button>
        </div>

        <router-link :to="{ name: 'ShopManagerAdd' }">추가 +</router-link>
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        :data="managerList"
      >
        <el-table-column prop="id" label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="매장명" />
        <el-table-column prop="userId" label="아이디" />
        <el-table-column prop="actualName" label="매니저명" />
        <el-table-column prop="concatNumber" label="연락처" />
        <el-table-column prop="managerJoinDate" label="매니저등록일" width="200">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column label="관리">
          <template slot-scope="scope">
            <!-- <el-button size="mini" @click="handleEdit(scope.row)" type="view">보기</el-button> -->
						<router-link class="detail-button col02" :to="{ name: 'ShopManagerUpdate', params: { uid: scope.row.uid } }">수정</router-link>
            <el-button size="mini" type="danger" @click="handleWithdraw(scope.row)" icon="delete-button">탈퇴
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getManagerList()"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { deleteUser, getManagerList } from '@/api/user';
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getManagerList();
  }

  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 10,
    searchType: 'actualName',
    searchValue: '',
    role: 'ROLE_SHOP_ADMIN',
  }

  private managerList = [];

  private loading = true;

  private getManagerList() {
    this.loading = true;
    getManagerList(this.listQuery).then((res) => {
      this.loading = false;
      this.managerList = res.data.content;
      this.totalElements = res.data.totalElements;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getManagerList();
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleWithdraw(manager: any) {
    this.$confirm(`정말 매니저(${manager.actualName})를 탈퇴 처리하시겠습니까?`, '매니저 탈퇴', {
      confirmButtonText: '탈퇴',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteUser(manager.uid).then(() => {
        this.$message.success('매니저가 탈퇴되었습니다.');
        this.getManagerList();
      });
    });
  }
}
</script>
