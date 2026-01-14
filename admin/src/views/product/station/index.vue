<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">거점 판매상품</p>
        <p class="usernumber">총 {{ totalElements }}개</p>
      </div>
      <div class="user__tab small">
        <div class="user__subtab">
          <div class="user__subtab">
            <el-select v-model="listQuery.searchType" slot="prepend" placeholder="검색타입">
              <el-option label="음식명" value="name" />
            </el-select>
            <el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
            <el-button @click="handleSearch()">
              <img src="~@/assets/images/search.png" alt="">
            </el-button>
          </div>
        </div>
        <router-link :to="{ name: 'ProductAdd' }">추가 +</router-link>
      </div>
    </div>

    <div class="user-content">
      <el-table v-loading="loading" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        :data="items" border style="width: 100%; height: 100%;">
        <el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="thumbUid" label="썸네일" width="130">
          <template slot-scope="scope">
            <img v-if="scope.row.thumbUid" :src="`${apiUrl}/attached-file/${scope.row.thumbUid}`" width="80" height="80">
          </template>
        </el-table-column>
        <el-table-column label="상품명" width="">
          <template slot-scope="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column prop="sellStatus" label="판매가능" width="100">
          <template slot-scope="scope">
            {{ scope.row.sellStatus ? '판매중' : '판매중지' }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="가격" width="130"></el-table-column>
        <el-table-column label="관리" width="200">
          <template slot-scope="scope">
            <router-link class="detail-button"
              :to="{ name: 'ProductUpdate', params: { idx: scope.row.idx } }">보기</router-link>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="totalElements" :page.sync="listQuery.page" :limit.sync="listQuery.size"
        @pagination="handleProductList()" />
    </div>
  </div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import { deleteProduct, getProductList } from '@/api/product';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  name: 'ProductIndex',
  components: {
    Pagination,
  },
})
export default class extends Vue {
  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 20,
    searchType: 'name',
    searchValue: '',
    productType: 'STATION',
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private items = [];

  private loading = true;

  mounted() {
    this.handleProductList();
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.handleProductList();
  }

  handleProductList() {
    this.loading = true;
    getProductList(this.listQuery).then((res) => {
      this.totalElements = res.data.totalElements;
      this.items = res.data.content;
      this.loading = false;
    });
  }

  handleDelete(product: any) {
    this.$confirm(`정말 상품(${product.name})을삭제하시겠습니까?`, '경고', {
      confirmButtonText: '확인',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      deleteProduct(product.idx).then(() => {
        this.handleProductList();
        this.$message({
          type: 'success',
          message: '성공적으로 삭제되었습니다.',
        });
      });
    });
  }

  private getNumber(index: number) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }
}
</script>
