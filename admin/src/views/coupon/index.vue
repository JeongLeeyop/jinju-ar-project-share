<template>
	<div class="user-wrap">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">쿠폰관리</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
			</div>

			<div class="user__tab">
				<div class="user__subtab">
					<p class="subtab-title">검색기간</p>
					<el-date-picker
            v-model="range"
            type="daterange"
            align="right"
            value-format="yyyy-MM-dd"
            range-separator="~"
            start-placeholder="지급 시작일"
						end-placeholder="지급 종료일"
            @change="handleChangeRange"
          />
					<el-input
            v-model="listQuery.name"
            placeholder="검색할 쿠폰명을 입력하세요."
            class="search"
            @keyup.enter.native="handleSearch()"
          />
					<el-button @click="handleSearch()">
            <img src="~@/assets/images/search.png" alt="">
          </el-button>
				</div>
				<router-link :to="{ name: 'CouponAdd' }">추가 +</router-link>
			</div>
		</div>

		<div v-loading="loading" class="user-content">
			<el-table
        :data="couponList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        style="width: 100%;"
        border
      >
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column label="쿠폰타입" width="150">
          <template slot-scope="scope">
            {{ getTypeText(scope.row.type) }}
          </template>
        </el-table-column>
				<el-table-column prop="name" label="쿠폰명" />
				<el-table-column label="할인금액/할인률" width="130">
          <template slot-scope="scope">
            <slot v-if="scope.row.percentStatus">{{ scope.row.discountPercent }}%</slot>
            <slot v-else>{{ scope.row.discountPrice | parseKrw }}원</slot>
          </template>
        </el-table-column>
				<el-table-column label="지급기간" width="330" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.startDate | parseDate('YYYY-MM-DD hh:mm:ss') }} ~ {{ scope.row.endDate | parseDate('YYYY-MM-DD hh:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column label="만료일" width="170" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.expiredDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="생성일" width="170" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="관리" width="200">
					<template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'CouponEdit', params: { idx: scope.row.idx } }">수정</router-link>
						<el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getCouponList()"
      />
		</div>
	</div>
</template>

<script lang="ts">
import { deleteCoupon, getCouponList } from '@/api/coupon';
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getCouponList();
  }

  private loading = true;

  private range: any = '';

  private listQuery = {
    page: 1,
    size: 10,
    name: '',
    startDate: '',
    endDate: '',
  }

  private totalElements = 0;

  private couponList: any = [];

  /* eslint-disable */
  private handleChangeRange() {
    if (this.range && this.range.length === 2) {
      this.listQuery.startDate = this.range[0];
      this.listQuery.endDate = this.range[1];
    } else {
      this.listQuery.startDate = '';
      this.listQuery.endDate = '';
    }
  }
  /* eslint-enable */

  private getCouponList() {
    this.loading = true;
    getCouponList(this.listQuery).then((res) => {
      this.couponList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getCouponList();
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private getTypeText(type: any) {
    if (type === 'JOIN') return '회원가입';
    if (type === 'BUY') return '특정금액 이상 구매';
    return '-';
  }

  private handleDelete(coupon: any) {
    this.$confirm(`정말 ${coupon.name}을(를) 삭제하시겠습니까?`, '쿠폰 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteCoupon(coupon.idx).then(() => {
        this.$message.success('성공적으로 쿠폰이 삭제되었습니다.');
        this.getCouponList();
      });
    });
  }
}
</script>
