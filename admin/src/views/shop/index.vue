<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">매장관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" slot="prepend" placeholder="이름">
            <el-option label="매장명" value="name"></el-option>
            <el-option label="매장전화번호" value="tel"></el-option>
          </el-select>
          <el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>

        <router-link :to="{ name: 'ShopAdd' }">추가 +</router-link>
      </div>
    </div>

    <div class="user-content">
      <el-table :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }" :data="shopList" border style="width: 100%">
        <el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="매장명" width=""></el-table-column>
        <el-table-column label="매장주소" width="">
          <template slot-scope="scope">
            {{ scope.row.address }} {{ scope.row.addressDetail }}
          </template>
        </el-table-column>
        <el-table-column prop="tel" label="매장전화번호" width=""></el-table-column>
        <el-table-column prop="openingDate" label="매장개업일" width=""></el-table-column>
        <el-table-column label="관리">
          <template slot-scope="scope">
            <!-- <el-button size="mini" @click="handleEdit(scope.row)" type="view">보기</el-button> -->
						<router-link class="detail-button" :to="{ name: 'ShopUpdate', params: { idx: scope.row.idx } }">보기</router-link>
            <el-button size="mini" type="danger" @click="handleWithdraw(scope.row)" icon="delete-button">삭제</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalElements"
        @current-change="setPage"
      />
    </div>
  </div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import { getShopList, withdrawShop } from '@/api/shop';

@Component({
  name: 'ShopIndex',
})
export default class extends Vue {
  mounted() {
    this.getShopList();
  }

  private shopList: any = [];

  private loading = true;

  private listQuery = {
    searchType: 'name',
    searchValue: '',
    page: 1,
    size: 10,
  }

  private totalElements = 0;

  private getShopList() {
    this.loading = true;
    getShopList(this.listQuery).then((res) => {
      this.shopList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private setPage(page: number) {
    this.listQuery.page = page;
    this.getShopList();
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getShopList();
  }

  private handleWithdraw(shop: any) {
    this.$confirm(`정말 픽업매장(${shop.name})을 탈퇴 처리하시겠습니까?`, '가맹점 탈퇴', {
      confirmButtonText: '탈퇴',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      withdrawShop(shop.idx).then(() => {
        this.$message.success('픽업매장이 탈퇴 처리되었습니다.');
        this.getShopList();
      });
    });
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }
}
</script>
