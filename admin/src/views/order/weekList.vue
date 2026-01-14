<template>
	<div class="user-wrap calc">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">주차별 주문현황</p>
				<p class="usernumber">총 789건</p>
			</div>

			<div class="user__tab">
				<div class="user__subtab">
					<p class="subtab-title">주문일</p>
					<div class="search-wr">
						<el-date-picker v-model="date1" type="daterange" align="right" range-separator="~" start-placeholder=""
							end-placeholder="">
						</el-date-picker>
						<el-button class="button-bg01">이번주</el-button>
						<el-button class="button-bg01">이번달</el-button>
					</div>
					<el-select v-model="select" slot="prepend" placeholder="">
						<el-option label="매장명" value="shopName"></el-option>
						<el-option label="주문품목" value="orderItem"></el-option>
						<el-option label="주문등록일" value="calcCreate"></el-option>
						<el-option label="주문완료일" value="calcComplete"></el-option>
					</el-select>
					<el-input placeholder="" v-model="input" class="search"></el-input>
					<el-button><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
			</div>
		</div>

		<div class="user-content">
			<el-table :data="calcLists" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }" style="width: 100%;"
				@selection-change="handleSelectionChange" border>
				<el-table-column prop="id" label="번호" width="70"></el-table-column>
				<el-table-column prop="shopName" label="매장명" sortable class-name="sort"></el-table-column>
				<el-table-column prop="orderCash" label="결제금액" width="150"></el-table-column>
				<el-table-column prop="calcCash" label="정산금액" width="150"></el-table-column>
				<el-table-column prop="calcCreate" label="주문일" width="150"></el-table-column>
				<el-table-column prop="calcWeek" label="주차" sortable width="150" class-name="sort"></el-table-column>
				<el-table-column prop="calcComplete" label="현재 상태"></el-table-column>
				<el-table-column label="관리" width="200">
					<el-button class="detail-button" type="text" @click="dialogFormVisible = true">보기</el-button>
				</el-table-column>
			</el-table>
			<el-pagination background layout="prev, pager, next" :total="(this.tableData.length / this.pageSize * 10)"
				@current-change="setPage">
			</el-pagination>
		</div>
		<el-dialog class="order-dialog-wr" title="1주차 주문" :visible.sync="dialogFormVisible">
			<div class="orderlist-wr">
				<p class="orderlist-ttl">[1주차] 2023년 03월 19일 ~ 03월 25일</p>
				<div class="orderlist">
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
					<div class="list">
						<p class="product">연어샐러드 x 10개</p>
						<p class="price">80,000원</p>
					</div>
				</div>
			</div>
			<el-form :model="form">
				<el-form-item label="주문현황">
					<el-select v-model="form.orderStatus" placeholder="주문상태를 변경해주세요">
						<el-option label="주문접수" value="orderReceived"></el-option>
						<el-option label="배달중" value="deliveryInProgress"></el-option>
						<el-option label="픽업대기중" value="pickupWaiting"></el-option>
						<el-option label="픽업완료" value="pickupComplete"></el-option>
						<el-option label="부분취소" value="partialCancel"></el-option>
						<el-option label="전체취소" value="totalCancel"></el-option>
					</el-select>
				</el-form-item>
			</el-form>
			<span slot="footer" class="dialog-footer">
				<el-button @click="dialogFormVisible = false">취소</el-button>
				<el-button type="primary" @click="dialogFormVisible = false">저장</el-button>
			</span>
		</el-dialog>

		<el-dialog class="order-dialog-wr calc" title="정산하기" :visible.sync="calcFormVisible">
			<div class="orderlist-wr">
				<p class="orderlist-ttl">정산하기</p>
				<div class="orderlist">
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 10건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주초전점</p>
						<p class="price">x 15건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 윙스타워점</p>
						<p class="price">x 15건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주신안점</p>
						<p class="price">x 20건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 5건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 7건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 15건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 15건</p>
					</div>
					<div class="list">
						<p class="product">매장명 : 와로샐러드 진주중앙점</p>
						<p class="price">x 15건</p>
					</div>
				</div>
			</div>
			<div class="orderlist-wr">
				<p class="orderlist-ttl">정산금액</p>
				<div class="orderlist">
					<div class="list">
						<p class="product">기본정산금</p>
						<p class="price">800 원</p>
					</div>
					<div class="list">
						<p class="product">총 정산금</p>
						<p class="price">800 원 x 117건</p>
					</div>
					<div class="list">
						<p class="price"><b style="font-weight: 700"> = {{ 800*117 }}원</b></p>
					</div>
				</div>
			</div>
			<!-- <el-form :model="form">
				<el-form-item label="기본정산금액 변동시">
					<el-input placeholder="기본정산금을 입력하세요" type="num" id="settlePrice" v-model="form.settlePrice" />
				</el-form-item>
			</el-form> -->
			<span slot="footer" class="dialog-footer">
				<el-button @click="dialogFormVisible = false">취소</el-button>
				<el-button type="primary" @click="dialogFormVisible = false">저장</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
import calcList from '@/data/calc.json';

export default {
	components: {
	},
	created() {
		for (let i = 0; i < 10; i += 1) {
			this.tableData.push({
				id: `${i}`,
				shopName: `${calcList[i].shopName}`,
				orderCash: `${calcList[i].orderCash}`,
				calcCash: `${calcList[i].calcCash}`,
				calcCreate: `${calcList[i].calcCreate}`,
				calcWeek: `${calcList[i].calcWeek}`,
				calcComplete: `${calcList[i].calcComplete}`,
			});
		}
	},
	data() {
		return {
			date1: '',
			input: '',
			select: '',
			calcList,
			tableData: [],
			page: 1,
			pageSize: 14,
			dialogFormVisible: false,
			calcFormVisible: false,
			form: {
				settlePrice: '',
			},
		};
	},
	computed: {
		calcLists() {
			return this.tableData.slice(
				this.pageSize * this.page - this.pageSize,
				this.pageSize * this.page,
			);
		},
	},
	methods: {
		handleDelete(index, row) {
			console.log(index, row);
		},
		setPage(val) {
			this.page = val;
		},
		handleSelectionChange(val) {
			this.multipleSelection = val;
			console.log(val);
		},
	},
};
</script>
