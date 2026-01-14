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
								<el-button @click="$router.push({ name: 'OrderList2' })" class="button-bg01">요일별</el-button>
								<el-button @click="$router.push({ name: 'Item2' })" class="button-bg01">상품별</el-button>
								<el-button @click="$router.push({ name: 'Shop2' })" class="button-bg01 active">매장별</el-button>
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
							<div class="btn" style="display: flex">
								<el-button class="button-bg01 modify" @click="handleStatusModify()">일괄수정</el-button>
                <el-button class="button-bg01 modify" @click="handleExcel()">엑셀출력</el-button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="user-content order" v-loading="loading">
			<el-table
        ref="shopList"
        style="width: 100%;"
        border
        :data="shopList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }"
      >
				<el-table-column type="selection" width="100"></el-table-column>
				<el-table-column label="매장번호" width="">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="shopName" label="매장명" width=""></el-table-column>
				<el-table-column label="매장위치" width="">
          <template slot-scope="scope">
            {{ scope.row.address }} {{ scope.row.addressDetail }}
          </template>
        </el-table-column>
				<el-table-column label="주문 수량" width="">
          <template slot-scope="scope">
            {{ scope.row.totalProductNum | parseKrw }}개
          </template>
        </el-table-column>
				<el-table-column label="결제금액" width="">
          <template slot-scope="scope">
            {{ scope.row.totalAmount | parseKrw }}개
          </template>
        </el-table-column>
				<el-table-column label="요약보기" width="">
          <template slot-scope="scope">
            <el-button class="detail-button" type="text" @click="handleViewShop(scope.row)">요약보기</el-button>
          </template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getProductOrderListByShop()"
      />
		</div>

		<el-dialog class="order-dialog-wr" :title="`요약보기 : ${selectedShop ? selectedShop.shopName : ''}`" :visible.sync="viewVisible">
      <ShopDetail
        v-if="viewVisible"
        :selectedShop="selectedShop"
        :listQuery="listQuery"
        @close="handleViewShop(null)"
      />
		</el-dialog>

    <el-dialog class="order-dialog-wr" width="400px" title="입고상태 일괄수정" :visible.sync="receiveStatusFormVisible">
      <div style="margin-bottom: 20px;">
        <el-radio-group v-model="receiveStatus">
          <el-radio-button :label="0">주문접수</el-radio-button>
          <el-radio-button :label="1">배달중</el-radio-button>
          <el-radio-button :label="2">배달완료</el-radio-button>
        </el-radio-group>
      </div>
      <el-button type="primary" @click="handleUpdateReceiveStatus()">일괄수정</el-button>
      <el-button @click="receiveStatusFormVisible = false">취소</el-button>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import moment from 'moment';
import {
  getExcel,
  getProductOrderTotalStatistics,
  getProductOrderListByShop,
  updateProductReceiveStatus,
} from '@/api/productOrder';
import ShopDetail from './components/shopDetail.vue';

@Component({
	components: {
		Pagination,
    ShopDetail,
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
    this.getProductOrderListByShop();
    this.setSearchDate();
    this.getProductOrderTotalStatistics();
  }

	private loading = true;

  private totalElements = 0;

  private shopList = [];

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

  private selectedShop = null;

  private viewVisible = false;

	private dateRange: any = [];

	private curDateInfo = '1주차';

	private activeButton: number | null = null;

  private selectedShopIdxList: any = [];

  private receiveStatusFormVisible = false;

  private receiveStatus = null;

  /* eslint-disable */
	private handleStatusModify() {
    const selection = (this.$refs.shopList as any).selection;
		if (selection.length < 1) {
      this.$message.warning('일괄수정을 원하는 매장을 선택해주세요.');
      return;
    }
    this.selectedShopIdxList = selection.map((item: any) => item.shopId);
    this.receiveStatusFormVisible = !this.receiveStatusFormVisible;
	}
  /* eslint-enable */

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

  private getProductOrderListByShop() {
    this.loading = true;
    getProductOrderListByShop(this.listQuery).then((res) => {
      this.loading = false;
      this.shopList = res.data.content;
      this.totalElements = res.data.totalElements;
    });
  }

  private getProductOrderTotalStatistics() {
    getProductOrderTotalStatistics(this.listQuery).then((res) => {
      this.totalStatistics = res.data;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getProductOrderListByShop();
    this.getProductOrderTotalStatistics();
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleViewShop(order: any) {
    this.selectedShop = order;
    this.viewVisible = !this.viewVisible;
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

  private handleUpdateReceiveStatus() {
    if (this.receiveStatus == null) {
      this.$message.warning('입고상태를 선택해주세요.');
      return;
    }
    this.$confirm('정말 입고상태를 일괄 수정하시겠습니까?', '입고상태 일괄 수정', {
      confirmButtonText: '수정',
      cancelButtonText: '취소',
    }).then(() => {
      updateProductReceiveStatus(this.listQuery, { idxList: this.selectedShopIdxList, receiveStatus: this.receiveStatus }).then(() => {
        this.$message.success('입고상태가 수정되었습니다.');
        this.selectedShopIdxList = [];
        this.receiveStatus = null;
        this.receiveStatusFormVisible = false;
        (this.$refs.shopList as any).clearSelection();
      });
    });
  }

  private handleExcel() {
    this.loading = true;
    getExcel({ ...this.listQuery, excelType: 'shop' }).then((res) => {
      const fileURL = window.URL.createObjectURL(new Blob([res.data], { type: 'application/csv;charset=utf-8;' }));
      const fileLink = document.createElement('a');
      const message = res.headers['content-disposition'];
      const fileName = decodeURI(message.split('=')[1]).replaceAll('+', ' ');
      if (window.navigator && (window as any).navigator.msSaveOrOpenBlob) {
        (navigator as any).msSaveBlob(new Blob([res.data], { type: 'application/csv;charset=utf-8;' }), fileName);
      } else {
        fileLink.href = fileURL;
        fileLink.setAttribute('download', fileName);
        document.body.appendChild(fileLink);
        fileLink.click();
      }
      this.loading = false;
    }).catch(() => {
      this.$message.error('엑셀데이터를 내려받는데 실패했습니다.');
      this.loading = false;
    });
  }
}
</script>
