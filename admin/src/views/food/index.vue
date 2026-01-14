<template>
	<div class="user-wrap">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">음식관리</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
			</div>

			<div class="user__tab small">
				<div class="user__subtab">
					<el-select v-model="listQuery.searchType" slot="prepend" placeholder="검색타입">
						<el-option label="음식명" value="name" />
						<el-option label="출처" value="ref" />
					</el-select>
					<el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
					<el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>

				<router-link :to="{ name: 'FoodAdd' }">추가 +</router-link>
			</div>
		</div>

		<div class="user-content">
			<el-table
        v-loading="loading"
        border
        style="width: 100%"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        :data="foodList"
      >
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="name" label="음식명" min-width="200"></el-table-column>
				<el-table-column label="중량" width="100">
          <template slot-scope="scope">
            <slot v-if="scope.row.weightType === 'g'">{{ scope.row.totalg | parseKrw }}g</slot>
            <slot v-else-if="scope.row.weightType === 'ml'">{{ scope.row.totalml | parseKrw }}ml</slot>
            <slot v-else>-</slot>
          </template>
        </el-table-column>
        <el-table-column label="칼로리" width="100">
          <template slot-scope="scope">
            <slot v-if="scope.row.kcal">{{ scope.row.kcal | parseKrw }}kcal</slot>
            <slot v-else>-</slot>
          </template>
        </el-table-column>
        <el-table-column label="탄/단/지" width="260">
          <template slot-scope="scope">
            {{ scope.row.carbohydrate }}g / {{ scope.row.protein }}g / {{ scope.row.fat }}g
          </template>
        </el-table-column>
				<el-table-column prop="ref" label="츨처" min-width="200"></el-table-column>
				<el-table-column label="관리" width="220">
					<template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'FoodUpdate', params: { idx: scope.row.idx } }">수정</router-link>
						<el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제</el-button>
					</template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getFoodList()"
      />
		</div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getFoodList, deleteFood } from '@/api/food';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getFoodList();
  }

  private totalElements = 0;

  private listQuery = {
    page: 1,
    size: 20,
    searchType: 'name',
    searchValue: '',
  }

  private foodList = [];

  private loading = true;

  private getFoodList() {
    this.loading = true;
    getFoodList(this.listQuery).then((res) => {
      this.totalElements = res.data.totalElements;
      this.foodList = res.data.content;
      this.loading = false;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getFoodList();
  }

  private handleDelete(food: any) {
    this.$confirm(`정말 ${food.name}을(를) 삭제하시겠습니까?`, '음식 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteFood(food.idx).then(() => {
        this.$message.success('음식이 삭제되었습니다.');
        this.getFoodList();
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
