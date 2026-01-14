<template>
	<div class="user-wrap">
		<div class="user-head order detail">
			<div class="user-title-wr">
				<div class="user-title">
					<div class="tl__box">
						<p class="tl txt1">상품별 정렬 :</p>
						<p class="tl txt2">연어샐러드</p>
						<p class="usernumber">총 13건</p>
					</div>
					<div class="user__tab">
						<div class="user__subtab">
							<div class="type-wr">
								<div class="setting__btn__box">
									<button @click="$router.push({ name: 'Item2' })" class="cancel">뒤로가기</button>
								</div>
							</div>
							<div class="search-wr">
								<el-select v-model="select" slot="prepend" placeholder="검색분류">
									<el-option label="주문번호" value="id" />
									<el-option label="이름" value="userName" />
									<el-option label="픽업매장" value="shopName" />
								</el-select>
								<el-input placeholder="" v-model="input" class="search"></el-input>
							</div>
						</div>
						<!-- <router-link :to="{ name: 'OrderAdd' }">추가 +</router-link> -->
					</div>
				</div>
				<div class="user-title2">
					<div class="left">
						<div class="summary">
							<div class="txt">
								<p class="tl">총 상품 수</p>
								<p class="value">46건</p>
							</div>
							<div class="txt">
								<p class="tl">총 금액</p>
								<p class="value">144,500원</p>
							</div>
							<div class="txt">
								<p class="tl">총 매장 수</p>
								<p class="value">13 지점</p>
							</div>
						</div>
						</div>
					<div class="user__tab">
						<div class="user__subtab">
							<div class="search-wr">
								<el-button class="button-bg01" :class="{active: isActive1}" @click="setToggle('1')">월</el-button>
								<el-button class="button-bg01" :class="{active: isActive2}" @click="setToggle('2')">화</el-button>
								<el-button class="button-bg01" :class="{active: isActive3}" @click="setToggle('3')">수</el-button>
								<el-button class="button-bg01" :class="{active: isActive4}" @click="setToggle('4')">목</el-button>
								<el-button class="button-bg01" :class="{active: isActive5}" @click="setToggle('5')">금</el-button>
								<el-button class="button-bg01" :class="{active: isActiveAll}" @click="setToggleAll()">전체</el-button>
							</div>
							<div class="search-wr">
								<p class="subtab-title">검색기간</p>
								<el-date-picker v-model="dateRange" type="daterange" align="right" readonly
									range-separator="~" value-format="yyyy-MM-dd" />
							</div>
						</div>
						<!-- <router-link :to="{ name: 'OrderAdd' }">추가 +</router-link> -->
					</div>
				</div>
			</div>
			<div class="user-search-wr">
				<el-button><img src="~@/assets/images/search.png" alt=""></el-button>
			</div>
		</div>
		<div class="user-content order">
			<el-table :data="tableData" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }" style="width: 100%;" border>
				<el-table-column prop="id" label="주문번호" width=""></el-table-column>
				<el-table-column prop="userName" label="이름" width=""></el-table-column>
				<el-table-column prop="shopName" label="픽업매장" width=""></el-table-column>
				<el-table-column prop="totalPrice" label="결제금액" sortable width=""></el-table-column>
				<el-table-column prop="qtyPick" label="선택된 상품 수량" width=""></el-table-column>
				<el-table-column prop="qty" label="모든 상품 수량" width=""></el-table-column>
				<el-table-column prop="pickupDay" label="픽업날짜" sortable width="" class-name="sort"></el-table-column>
				<el-table-column prop="pickupTime" label="픽업시간" sortable width="" class-name="sort"></el-table-column>
				<el-table-column prop="currentWeek" label="현재 주차" sortable width=""></el-table-column>
				<el-table-column prop="userPhone" label="연락처" width=""></el-table-column>
				<el-table-column prop="status" label="상태" sortable width=""></el-table-column>
				<el-table-column prop="orderDay" label="주문날짜" sortable width="" class-name="sort"></el-table-column>
				<el-table-column label="관리" width="">
					<el-button class="detail-button" type="text" @click="dialogFormVisible = true">보기</el-button>
				</el-table-column>
			</el-table>
			<el-pagination background layout="prev, pager, next" :total="1" @current-change="setPage">
			</el-pagination>
		</div>

		<el-dialog class="order-dialog-wr" title="1주차 주문" :visible.sync="dialogFormVisible">
			<div class="orderlist-wr">

				<div class="orderlist-info">
					<div class="username">
						<p class="orderlist-ttl">이름 : </p>
						<p class="orderlist-ttl">홍길동</p>
					</div>
					<div class="shopname">
						<p class="orderlist-ttl">매장명 : </p>
						<p class="orderlist-ttl">와로샐러드</p>
					</div>
					<div class="date">
						<p class="orderlist-ttl">[금요일]</p>
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
			<el-form :model="form">
				<el-form-item label="주문현황">
					<el-select v-model="form.orderStatus" placeholder="주문상태를 변경해주세요">
						<el-option label="주문접수" :value="0"></el-option>
						<el-option label="배달중" :value="1"></el-option>
						<el-option label="픽업대기중" :value="2"></el-option>
						<el-option label="픽업완료" :value="3"></el-option>
					</el-select>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button @click="dialogFormVisible = false">취소</el-button>
				<el-button type="primary" @click="dialogFormVisible = false">저장</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script lang="ts">
import orderList from '@/data/order.json';
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { IOrder } from '@/types/order';
import moment from 'moment';

@Component({
	components: {
		Pagination,
	},
})

export default class extends Vue {
	mounted() {
		for (let i = 0; i < 10; i += 1) {
			if (`${orderList[i].orderState}` === '배송중') {
				console.log();
			}
			if (`${orderList[i].orderState}` === '배송완료') {
				console.log();
			}
			console.log(`${orderList[i].orderState}`);

			const obj: IOrder = {
				id: `${i}`,
				orderDay: `${orderList[i].orderDay}`,
				orderItem: `${orderList[i].orderItem}`,
				// orderTime: `${orderList[i].orderTime}`,
				pickupDay: `${orderList[i].pickupDay}`,
				pickupTime: `${orderList[i].pickupTime}`,
				orderState: `${orderList[i].orderState}`,
				userName: `${orderList[i].userName}`,
				userPhone: `${orderList[i].userPhone}`,
				shopName: `${orderList[i].shopName}`,
				status: `${orderList[i].status}`,
				totalPrice: `${orderList[i].totalPrice}`,
				qty: `${orderList[i].qty}`,
				currentWeek: `${orderList[i].currentWeek}`,
			};
			this.tableData.push(obj);
		}
	}

	private isActive1 = true;

	private isActive2 = true;

	private isActive3 = true;

	private isActive4 = true;

	private isActive5 = true;

	private isActiveAll = true;

	private setToggle(type: any) {
		if (type === '1') this.isActive1 = !this.isActive1;
		else if (type === '2') this.isActive2 = !this.isActive2;
		else if (type === '3') this.isActive3 = !this.isActive3;
		else if (type === '4') this.isActive4 = !this.isActive4;
		else if (type === '5') this.isActive5 = !this.isActive5;

		if (this.isActive1 && this.isActive2 && this.isActive3 && this.isActive4 && this.isActive5) this.isActiveAll = true;
		else this.isActiveAll = false;
	}

	private setToggleAll(type: any) {
		this.isActiveAll = !this.isActiveAll;
		if (this.isActiveAll === false) {
			this.isActive1 = false;
			this.isActive2 = false;
			this.isActive3 = false;
			this.isActive4 = false;
			this.isActive5 = false;
		} else {
			this.isActive1 = true;
			this.isActive2 = true;
			this.isActive3 = true;
			this.isActive4 = true;
			this.isActive5 = true;
		}
	}

	private date1 = '';

	private input = '';

	private select = '';

	private tableData: IOrder[] = [];

	private page = 1;

	private pageSize = 14;

	private dialogFormVisible = false;

	private dateRange: any = ['2023-05-22', '2023-05-26'];

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

	private handleLastRange(type: any, type2: any) {
		if (type === 'day') {
			if (type2 === '1') this.dateRange = [moment().add(-1, 'day').format('YYYY-MM-DD'), moment().add(-1, 'day').format('YYYY-MM-DD')];
			else if (type2 === '2') this.dateRange = [moment().format('YYYY-MM-DD'), moment().format('YYYY-MM-DD')];
			else if (type2 === '3') this.dateRange = [moment().add(1, 'day').format('YYYY-MM-DD'), moment().add(1, 'day').format('YYYY-MM-DD')];
		} else if (type === 'week') {
			if (type2 === '1') this.dateRange = [moment().day(7).add(-2, 'week').format('YYYY-MM-DD'), moment().day(6).add(-1, 'week').format('YYYY-MM-DD')];
			else if (type2 === '2') this.dateRange = [moment().day(7).add(-1, 'week').format('YYYY-MM-DD'), moment().day(6).format('YYYY-MM-DD')];
			else if (type2 === '3') this.dateRange = [moment().day(7).add(1, 'week').format('YYYY-MM-DD'), moment().day(6).add(2, 'week').format('YYYY-MM-DD')];
		} else if (type === 'month') {
			if (type2 === '1') this.dateRange = [moment().startOf('month').add(-1, 'month').format('YYYY-MM-DD'), moment().endOf('month').add(-1, 'month').format('YYYY-MM-DD')];
			else if (type2 === '2') this.dateRange = [moment().startOf('month').format('YYYY-MM-DD'), moment().endOf('month').format('YYYY-MM-DD')];
			else if (type2 === '3') this.dateRange = [moment().startOf('month').add(1, 'month').format('YYYY-MM-DD'), moment().endOf('month').add(1, 'month').format('YYYY-MM-DD')];
		}
	}

	private handleEdit(index: any, row: any) {
		console.log(index, row);
	}

	private handleDelete(index: any, row: any) {
		console.log(index, row);
	}

	private setPage(val: any) {
		this.page = val;
	}

	private handleCurrentChange(val: any) {
		this.page = val;
	}
}
</script>
