<template>
	<div class="user-wrap calc">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">정산내역</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
			</div>
		</div>

		<div class="user-content">
			<el-table
        v-loading="loading"
        ref="orderTable"
        style="width: 100%;"
        border
        :data="settleApplyList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
      >
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="매장명" />
        <el-table-column label="총 구매건" width="150">
          <template slot-scope="scope">
            {{ scope.row.totalProductNum | parseKrw }}건
          </template>
        </el-table-column>
				<el-table-column label="총 결제금액" width="220">
          <template slot-scope="scope">
            {{ scope.row.totalSale | parseKrw }}원
          </template>
        </el-table-column>
        <el-table-column label="총 정산액" width="220">
          <template slot-scope="scope">
            {{ scope.row.totalSettle | parseKrw }}원
          </template>
        </el-table-column>
				<el-table-column label="정산신청일" width="250">
          <template slot-scope="scope">
            {{ scope.row.applyDate | parseDate }}
          </template>
        </el-table-column>
				<el-table-column label="정산완료일" width="250">
          <template slot-scope="scope">
            {{ scope.row.approvalDate | parseDate }}
          </template>
        </el-table-column>
				<el-table-column label="정산상태" width="150">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.approvalStatus" type="success">정산완료</el-tag>
            <el-tag v-else type="info">정산대기</el-tag>
          </template>
        </el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getSettleApplyList()"
      />
		</div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getSettleApplyList } from '@/api/manager/settleApply';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getSettleApplyList();
  }

  private listQuery = {
    page: 1,
    size: 20,
  }

  private loading = true;

  private totalElements = 0;

  private settleApplyList = []; // 픽업완료된 주문리스트

  private applyForm = {
    idxList: [],
  }

  private getSettleApplyList() {
    this.loading = true;
    getSettleApplyList(this.listQuery).then((res) => {
      this.totalElements = res.data.totalElements;
      this.settleApplyList = res.data.content;
      this.loading = false;
    });
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }
}
</script>
