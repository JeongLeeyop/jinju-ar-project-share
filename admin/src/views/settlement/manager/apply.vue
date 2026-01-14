<template>
	<div class="user-wrap calc">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">정산가능 내역</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
			</div>

			<div class="user__tab">
				<el-button class="calc" @click="handleVisibleModal()">정산요청</el-button>
			</div>
		</div>

		<div class="user-content">
			<el-table
        v-loading="loading"
        ref="orderTable"
        style="width: 100%;"
        border
        :data="orderList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
      >
				<el-table-column type="selection" width="55" />
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="orderCash" label="결제금액" width="150">
          <template slot-scope="scope">
            {{ scope.row.amount | parseKrw }}원
          </template>
        </el-table-column>
        <el-table-column label="상품개수" width="90">
          <template slot-scope="scope">
            {{ scope.row.productNum | parseKrw }}개
          </template>
        </el-table-column>
        <el-table-column label="예상 정산 금액">
            {{ settleSetting.amount | parseKrw }}원
        </el-table-column>
				<el-table-column label="주문일" width="220">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate }}
          </template>
        </el-table-column>
        <el-table-column label="픽업일" width="220">
          <template slot-scope="scope">
            {{ scope.row.pickupDate | parseDate }}
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
        @pagination="getProductOrderList()"
      />
		</div>
		<el-dialog
      class="order-dialog-wr calc"
      title="정산하기"
      :visible.sync="modalVisible"
    >
			<ApplyForm
        v-if="modalVisible"
        :settleSetting="settleSetting"
        :orderList="selectedOrderList"
        @close="handleCloseModal()"
        @success="handleSuccessApply()"
      />
		</el-dialog>
    <el-dialog
      class="order-dialog-wr"
      :title="`${selectedOrder ? selectedOrder.pickupDate : ' - '} 주문`"
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
import { Component, Vue } from 'vue-property-decorator';
import { getProductOrderList } from '@/api/manager/productOrder';
import { getProductOrderListByDay } from '@/api/productOrder';
import Pagination from '@/components/Pagination/index.vue';
import ApplyForm from './components/apply.vue';
import { getSettleSetting } from '@/api/settleSetting';
import OrderDetail from './components/orderDetail.vue';

@Component({
  components: {
    Pagination,
    ApplyForm,
    OrderDetail,
  },
})
export default class extends Vue {
  mounted() {
    this.getSettleSetting();
    this.getProductOrderListByDay();
  }

  private settleSetting = {
    amount: 0,
  }

  private listQuery = {
    page: 1,
    size: 20,
    receiveStatus: 2,
    pickupStatus: 1,
    groupOrderStatus: 2,
    settleStatus: 0,
  }

  private loading = true;

  private totalElements = 0;

  private modalVisible = false;

  private selectedOrder = null;

  private viewVisible = false;

  private orderList = []; // 픽업완료된 주문리스트

  private selectedOrderList = [];

  private getSettleSetting() {
    getSettleSetting().then((res) => {
      if (res.data) {
        this.settleSetting = res.data;
      }
    });
  }

  private getProductOrderListByDay() {
    this.loading = true;
    getProductOrderListByDay(this.listQuery).then((res) => {
      this.totalElements = res.data.totalElements;
      this.orderList = res.data.content;
      this.loading = false;
    });
  }

  private handleVisibleModal() {
    const table = (this.$refs.orderTable as any);
    if (table.selection.length > 0) {
      this.selectedOrderList = table.selection;
      this.modalVisible = !this.modalVisible;
    } else {
      this.$message.warning('정산할 내역을 선택해주세요.');
    }
  }

  private handleViewOrder(order: any) {
    this.selectedOrder = order;
    this.viewVisible = !this.viewVisible;
  }

  private handleCloseModal() {
    this.modalVisible = !this.modalVisible;
    this.selectedOrderList = [];
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleSuccessApply() {
    this.handleCloseModal();
    this.getProductOrderListByDay();
  }
}
</script>
