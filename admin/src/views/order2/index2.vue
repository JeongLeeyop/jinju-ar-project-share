<template>
	<div class="user-wrap">
		<div class="user-head order">
			<div class="user-title-wr section01">
        <div class="user-title jcl">
          <div class="tl__box">
            <p class="tl">주문현황</p>
						<p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
					</div>
          <div class="summary">
            <div class="txt">
              <p class="tl">총 상품 수</p>
							<p class="value">{{ totalStatistics.totalProductNum | parseKrw }}건</p>
						</div>
						<div class="txt">
              <p class="tl">총 금액</p>
							<p class="value">{{ totalStatistics.totalAmount | parseKrw }}원</p>
						</div>
						<div class="txt">
              <p class="tl">총 매장 수</p>
							<p class="value">{{ totalStatistics.totalShop | parseKrw }} 지점</p>
						</div>
					</div>
        </div>
      </div>
			<div class="user-title-wr section02">
				<div class="user-title">
					<div class="user__tab">
						<div class="user__subtab">
							<div class="type-wr">
								<el-button @click="$router.push({ name: 'OrderList2' })" class="button-bg01 active">요일별</el-button>
								<el-button @click="$router.push({ name: 'Item2' })" class="button-bg01">상품별</el-button>
								<el-button @click="$router.push({ name: 'Shop2' })" class="button-bg01">매장별</el-button>
							</div>
							<div class="search-wr">
								<el-select v-model="listQuery.searchType" slot="prepend" placeholder="검색분류">
									<el-option label="이름" value="actualName" />
									<el-option label="픽업매장" value="shopName" />
									<el-option label="주문품목" value="productName" />
								</el-select>
								<el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
                  <el-button @click="handleSearch()">
                    <img src="~@/assets/images/search.png" alt="">
                  </el-button>
							</div>
						</div>
						<!-- <router-link :to="{ name: 'OrderAdd' }">추가 +</router-link> -->
					</div>
				</div>
				<div class="user-title2">
					<div class="user__tab">
						<div class="user__subtab">
              <div class="date-wr">
                <el-button class="button-bg01" @click="handleLastRange('week','prev')">&lt;</el-button>
								<el-date-picker
                  @change="handleDatePickerChange"
                  v-model="dateRange"
                  type="daterange"
                  align="right"
                  range-separator="~"
                  value-format="yyyy-MM-dd"
                />
                <div class="date-info"> {{ curDateInfo }}</div>
								<el-button class="button-bg01" @click="handleLastRange('week','next')">&gt;</el-button>
							</div>
							<div class="search-wr">
								<el-button class="button-bg01" :class="{ active: activeButton === 1 }" @click="handleLastRange('day','1',1)">어제</el-button>
								<el-button class="button-bg01" :class="{ active: activeButton === 2 }" @click="handleLastRange('day','2',2)">오늘</el-button>
								<el-button class="button-bg01" :class="{ active: activeButton === 3 }" @click="handleLastRange('day','3',3)">내일</el-button>
								<el-button class="button-bg01" :class="{ active: activeButton === 4 }" @click="handleLastRange('month','1',4)">저번달</el-button>
								<el-button class="button-bg01" :class="{ active: activeButton === 5 }" @click="handleLastRange('month','2',5)">이번달</el-button>
								<el-button class="button-bg01" :class="{ active: activeButton === 6 }" @click="handleLastRange('month','3',6)">다음달</el-button>
							</div>
						</div>
						<!-- <router-link :to="{ name: 'OrderAdd' }">추가 +</router-link> -->
					</div>
				</div>
			</div>
		</div>
		<div v-loading="loading" class="user-content order">
			<el-table
        style="width: 100%;"
        border
        :data="orderList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }"
      >
				<!-- <el-table-column type="selection" width="100"></el-table-column> -->
				<el-table-column label="주문번호" width="">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column label="이름" width="">
          <template slot-scope="scope">
            {{ scope.row.name }}
          </template>
        </el-table-column>
				<el-table-column label="픽업매장" width="">
          <template slot-scope="scope">
            {{ scope.row.shopName }}
          </template>
          </el-table-column>
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
				<el-table-column label="픽업날짜" width="">
          <template slot-scope="scope">
            {{ scope.row.pickupDate | parseDate('YYYY-MM-DD(ddd)') }}
          </template>
        </el-table-column>
				<el-table-column label="픽업시간" prop="pickupTime" width="" />
				<el-table-column label="연락처" width="">
          <template slot-scope="scope">
            {{ scope.row.number }}
          </template>
          </el-table-column>
				<el-table-column label="상태" width="">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.pickupStatus === 1">픽업 완료</el-tag>
            <el-tag v-else>픽업 대기</el-tag>
          </template>
        </el-table-column>
				<el-table-column label="주문날짜" width="200">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="관리" width="">
          <template slot-scope="scope">
            <el-button class="detail-button" type="text" @click="handleViewOrder(scope.row)">보기</el-button>
          </template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getProductOrderListByDay()"
      />
		</div>

		<el-dialog
      class="order-dialog-wr"
      :title="`${selectedOrder ? selectedOrder.weekNum : '1'}주차 주문`"
      :visible.sync="viewVisible"
    >
			<OrderDetail
        v-if="viewVisible"
        :selectedOrder="selectedOrder"
        @close="handleViewOrder(null)"
        @update="handleUpdateOrder()"
      />
		</el-dialog>
	</div>
</template>

<script lang="ts">
import moment from 'moment';
import orderList from '@/data/order.json';
import { Component, Vue, Watch } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getProductOrderList, getProductOrderTotalStatistics, getProductOrderListByDay } from '@/api/productOrder';
import OrderDetail from './components/orderDetail.vue';

@Component({
  components: {
    Pagination,
    OrderDetail,
  },
})
export default class extends Vue {
  /* eslint-disable */
  @Watch('dateRange')
  private handleChangeDateRange() {
    if (this.dateRange && this.dateRange.length === 2) {
      this.listQuery.startDate = this.dateRange[0];
      this.listQuery.endDate = this.dateRange[1];
      this.handleSearch();
    }
  }
  /* eslint-enable */

  mounted() {
    // this.getProductOrderList();
    this.getProductOrderListByDay();
    this.setSearchDate();
    this.getProductOrderTotalStatistics();
  }

	private loading = true;

  private totalElements = 0;

  private orderList = [];

  private totalStatistics = {
    totalShop: 0,
    totalProductNum: 0,
    totalAmount: 0,
  }

  private listQuery = {
    page: 1,
    size: 10,
    orderType: 'PICKUP',
    searchType: 'actualName',
    searchValue: '',
    startDate: moment().day(1).format('YYYY-MM-DD'),
    endDate: moment().day(5).format('YYYY-MM-DD'),
  }

  private viewVisible = false;

  private form = {};

  private dateRange: any[]= [];

  private curDateInfo = '1주차';

  private selectedOrder = null;

  private activeButton: number | null = null;

  private handleLastRange(type: any, type2: any, buttonNumber: number) {
    if (this.activeButton === buttonNumber) this.activeButton = null;
    else this.activeButton = buttonNumber;
		if (type === 'day') {
			if (type2 === '1') {
        this.dateRange = [moment().add(-1, 'day').format('YYYY-MM-DD'), moment().add(-1, 'day').format('YYYY-MM-DD')];
        this.curDateInfo = '어제';
      } else if (type2 === '2') {
        this.dateRange = [moment().format('YYYY-MM-DD'), moment().format('YYYY-MM-DD')];
        this.curDateInfo = '오늘';
      } else if (type2 === '3') {
        this.dateRange = [moment().add(1, 'day').format('YYYY-MM-DD'), moment().add(1, 'day').format('YYYY-MM-DD')];
        this.curDateInfo = '내일';
      }
		} else if (type === 'week') {
      const today = new Date(this.dateRange[0]);
      if (type2 === 'prev') {
        const lastMonday = moment(today).subtract(1, 'weeks').startOf('isoWeek');
        const friday = moment(lastMonday).add(4, 'days');
        this.dateRange = [lastMonday.format('YYYY-MM-DD'), friday.format('YYYY-MM-DD')];
      } else if (type2 === 'next') {
        const lastMonday = moment(today).add(1, 'weeks').startOf('isoWeek');
        const friday = moment(lastMonday).add(4, 'days');
        this.dateRange = [lastMonday.format('YYYY-MM-DD'), friday.format('YYYY-MM-DD')];
      }
      const tmpDate = moment(this.dateRange[0]);
      const firstDayOfMonth = moment(tmpDate).startOf('month');
      const firstMondayOfMonth = moment(firstDayOfMonth).startOf('isoWeek');
      const numberOfWeeks = tmpDate.diff(firstMondayOfMonth, 'weeks');
      if (tmpDate.isSameOrAfter(firstMondayOfMonth)) {
        this.curDateInfo = numberOfWeeks.toString().concat('', '주차');
      }
		} else if (type === 'month') {
			if (type2 === '1') {
       this.dateRange = [moment().add(-1, 'month').startOf('month').format('YYYY-MM-DD'), moment().add(-1, 'month').endOf('month').format('YYYY-MM-DD')];
       this.curDateInfo = '저번달';
      } else if (type2 === '2') {
        this.dateRange = [moment().startOf('month').format('YYYY-MM-DD'), moment().endOf('month').format('YYYY-MM-DD')];
        this.curDateInfo = '이번달';
      } else if (type2 === '3') {
        this.dateRange = [moment().add(1, 'month').startOf('month').format('YYYY-MM-DD'), moment().add(1, 'month').endOf('month').format('YYYY-MM-DD')];
        this.curDateInfo = '다음달';
      }
		}
	}

  private handleDatePickerChange() {
    this.curDateInfo = '선택';
  }

  private getProductOrderListByDay() {
    this.loading = true;
    getProductOrderListByDay(this.listQuery).then((res) => {
      this.totalElements = res.data.totalElements;
      this.orderList = res.data.content;
      this.loading = false;
    });
  }

  private getProductOrderTotalStatistics() {
    getProductOrderTotalStatistics(this.listQuery).then((res) => {
      this.totalStatistics = res.data;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getProductOrderListByDay();
    this.getProductOrderTotalStatistics();
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

  private handleUpdateOrder() {
    this.handleViewOrder(null);
    this.getProductOrderListByDay();
  }

  private setSearchDate() {
    const startDate = moment().day(1).format('YYYY-MM-DD');
    this.dateRange = [startDate, moment().day(5).format('YYYY-MM-DD')];
    const tmpDate = moment(startDate);
    const firstDayOfMonth = moment(tmpDate).startOf('month');
    const firstMondayOfMonth = moment(firstDayOfMonth).startOf('isoWeek');
    const numberOfWeeks = tmpDate.diff(firstMondayOfMonth, 'weeks');
    if (tmpDate.isSameOrAfter(firstMondayOfMonth)) {
      this.curDateInfo = numberOfWeeks.toString().concat('', '주차');
    }
  }
}
</script>
