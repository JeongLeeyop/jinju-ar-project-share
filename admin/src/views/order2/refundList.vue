<template>
	<div class="user-wrap">
		<div class="user-head">
			<div class="user-title">
			<div class="tl__box">
				<p class="tl">취소목록</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
			</div>
			<div class="user__tab refund">
				<div class="user__subtab">
					<p class="subtab-title">환불일시</p>
					<div class="search-wr">
							<el-date-picker
							v-model="dateRange"
							type="daterange"
							align="right"
							range-separator="~"
							value-format="yyyy-MM-dd"
							/>
						<el-button class="button-bg01" @click="handleLastRange('week')">이번주</el-button>
						<el-button class="button-bg01" @click="handleLastRange('month')">이번달</el-button>
					</div>
					<el-select v-model="listQuery.searchType" slot="prepend" placeholder="검색분류">
						<el-option label="매장명" value="shopName"></el-option>
						<el-option label="상품명" value="productName"></el-option>
					</el-select>
					<el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
					<el-button @click="handleSearch()">
						<img src="~@/assets/images/search.png" alt="">
					</el-button>
				</div>
			</div>
		</div>
		</div>
		<div v-loading="loading" class="user-content refund">
			<el-table :data="orderList" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }" style="width: 100%;" border>
				<el-table-column label="주문번호" width="">
				<template slot-scope="scope">
					{{ getNumber(scope.$index) }}
				</template>
				</el-table-column>

				<el-table-column prop="group.user.actualName" label="이름" width="" />
				<el-table-column prop="group.shop.name" label="픽업매장" width="" />
				<el-table-column label="결제금액" width="">
          <template slot-scope="scope">
            {{ scope.row.amount | parseKrw }}원
          </template>
        </el-table-column>
				<el-table-column label="상품 수량" width="">
          <template slot-scope="scope">
            {{ scope.row.productNum | parseKrw }}개
          </template>
        </el-table-column>
		<el-table-column prop="group.user.concatNumber" label="연락처" width="" />
				<el-table-column label="상태" width="">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.pickupStatus">픽업 완료</el-tag>
            <el-tag v-else type="info">픽업 대기</el-tag>
          </template>
        </el-table-column>
		<el-table-column label="주문날짜" width="200">
          <template slot-scope="scope">
            {{ scope.row.orderDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
		<el-table-column label="취소날짜" width="200">
          <template slot-scope="scope">
            {{ scope.row.refundDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
		<el-table-column label="상세보기" width="190">
			<template slot-scope="scope">
            <el-button class="detail-button" type="text" @click="handleViewOrder(scope.row)">보기</el-button>
          </template>
		</el-table-column>
			</el-table>
			<Pagination
			:total="totalElements"
			:page.sync="listQuery.page"
			:limit.sync="listQuery.size"
			@pagination="getProductOrderList()"
		/>
		</div>

		<el-dialog
      class="order-dialog-wr"
      :title="`${selectedOrder ? selectedOrder.weekNum : '1'}주차 주문`"
      :visible.sync="viewVisible"
    >
			<RefundDetail
        v-if="viewVisible"
        :selectedOrder="selectedOrder"
        @close="handleViewOrder(null)"
      />
		</el-dialog>

		<!-- <el-dialog class="order-dialog-wr" title="1주차 주문" :visible.sync="dialogFormVisible">
			<div class="orderlist-wr">

				<div class="orderlist-info">
					<div class="username">
						<p class="orderlist-ttl">이름 : </p>
						<p class="orderlist-ttl">홍길동</p>
					</div>
					<div class="shopname">
						<p class="orderlist-ttl"> 매장명 : </p>
						<p class="orderlist-ttl"> 와로샐러드</p>
					</div>
					<div class="date">
						<p class="orderlist-ttl">[1주차]</p>
						<p class="orderlist-ttl">2023년 03월 19일</p>
					</div>
				</div>
				<div class="orderlist">
					<div class="list">
						<p class="product">연어샐러드 x 1개</p>
						<p class="price">8,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 1개</p>
						<p class="price">8,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 1개</p>
						<p class="price">8,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 1개</p>
						<p class="price">8,000원</p>
					</div>
					<div class="totalCnt">
						<p class="price">x4개</p>
					</div>
				</div>
				<p class="orderlist-result">32,000원</p>
			</div>
			<span slot="footer" class="dialog-footer">
				<el-button type="primary" @click="dialogFormVisible = false">확인</el-button>
			</span>
		</el-dialog> -->
	</div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { IOrder } from '@/types/order';
import moment from 'moment';
import { getProductOrderList } from '@/api/productOrder';
import RefundDetail from './components/refundDetail.vue';

@Component({
  components: {
    Pagination,
    RefundDetail,
  },
})

export default class extends Vue {
@Watch('dateRange')
  private handleChangeDateRange() {
    const [startDate, endDate] = this.dateRange;
    if (startDate && endDate) {
      this.listQuery.startDate = startDate;
      this.listQuery.endDate = endDate;
      this.handleSearch();
    }
  }

	mounted() {
		this.getProductOrderList();
		const startDate = moment().day(1).format('YYYY-MM-DD');
		this.dateRange = [startDate, moment().day(5).format('YYYY-MM-DD')];
	}

	private getProductOrderList() {
    this.loading = true;
    getProductOrderList(this.listQuery).then((res) => {
      this.loading = false;
      this.orderList = res.data.content;
      this.totalElements = res.data.totalElements;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getProductOrderList();
  }

	private listQuery = {
		page: 1,
		size: 10,
		orderType: 'PICKUP',
		searchType: 'shopName',
		searchValue: '',
		orderStatus: -1,
		startDate: moment().day(1).format('YYYY-MM-DD'),
		endDate: moment().day(5).format('YYYY-MM-DD'),
	}

	private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleViewOrder(order: any) {
    this.selectedOrder = order;
    this.viewVisible = !this.viewVisible;
  }

	private viewVisible = false;

	private selectedOrder = null;

	private totalElements = 0;

	private loading = true;

	private orderList = [];

	private date1 = '';

	private input = '';

	private select = '';

	private tableData: IOrder[] = [];

	private page = 1;

	private pageSize = 14;

	private dialogFormVisible = false;

	private dateRange: any = [];

	private form = {
		name: '',
		region: '',
		date1: '',
		date2: '',
		delivery: false,
		type: [],
		resource: '',
		desc: '',
	};

	private orderLists() {
			return this.tableData.slice(
				this.pageSize * this.page - this.pageSize,
				this.pageSize * this.page,
			);
	}

	private handleLastRange(type: any) {
    if (type === 'week') {
		this.dateRange = [moment().day(1).format('YYYY-MM-DD'), moment().day(5).format('YYYY-MM-DD')];
    } else if (type === 'month') {
		this.dateRange = [moment().startOf('month').format('YYYY-MM-DD'), moment().endOf('month').format('YYYY-MM-DD')];
    }
  }

	private handleEdit(index : any, row: any) {
		console.log(index, row);
	}

	private handleDelete(index : any, row : any) {
		console.log(index, row);
	}

	private setPage(val : any) {
		this.page = val;
	}

	private handleCurrentChange(val : any) {
		this.page = val;
	}
}
</script>
