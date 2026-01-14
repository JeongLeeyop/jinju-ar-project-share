<template>
  <div class="user-wrap product">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">윙스타워점 판매상품관리</p>
        <p class="usernumber">총 789개</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="select" slot="prepend" placeholder="">
            <el-option label="음식명" value="음식명"></el-option>
            <el-option label="판매가능" value="판매가능"></el-option>
            <el-option label="판매불가능" value="판매불가능"></el-option>
          </el-select>
          <el-input placeholder="" v-model="input" class="search"></el-input>
          <el-button><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>

        <router-link :to="{ name: 'ShopProductAdd' }">추가 +</router-link>
      </div>
    </div>

    <div class="user-content">
      <el-table :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }" :data="dietLists" border
        style="width: 100%; height: 100%;">
        <el-table-column prop="id" label="번호" width="70"></el-table-column>
        <el-table-column prop="dietImage" label="썸네일" width="130"></el-table-column>
        <el-table-column prop="dietName" label="상품명" width=""></el-table-column>
        <el-table-column prop="dietPrice" label="가격" width="130"></el-table-column>
        <el-table-column prop="dietKcal" label="칼로리" width="130"></el-table-column>
        <el-table-column prop="dietDetail" label="재료"></el-table-column>
        <el-table-column prop="dietSale" label="판매여부" width="100"></el-table-column>
        <el-table-column label="관리" width="200">
          <template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'ShopProductEdit' }">수정</router-link>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination background layout="prev, pager, next" :total="(this.tableData.length / this.pageSize * 10)"
        @current-change="setPage">
      </el-pagination>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getShopItemList } from '@/api/product';

@Component({
  name: 'ProductIndex',
})
export default class extends Vue {
  private query = {
    page: 0,
    limit: 10,
  };

  private items = [];

  mounted() {
    this.handleProductList();
  }

  handleProductList() {
    getShopItemList(this.query).then((res) => {
      this.items = res.data.content;
    });
  }
}
</script>
