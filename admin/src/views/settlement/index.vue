<template>
	<div class="user-wrap calc">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">정산관리</p>
				<p class="usernumber">총 {{ totalElements | parseKrw }}건</p>
			</div>

			<div class="user__tab">
				<div class="user__subtab">
					<p class="subtab-title">정산완료일</p>
					<div class="search-wr">
						<el-date-picker style="max-width: 500px;"
              v-model="dateRange"
              type="daterange"
              align="right"
              range-separator="~"
              value-format="yyyy-MM-dd"
            />
						<el-button class="button-bg01" @click="handleLastRange('week')">이번주</el-button>
						<el-button class="button-bg01" @click="handleLastRange('month')">이번달</el-button>
					</div>
					<el-select style="flex:0 2 30%" v-model="listQuery.searchType" slot="prepend" placeholder="">
						<el-option label="매장명" value="shopName" />
					</el-select>
					<el-input
          style="flex: 0 2 50%"
            v-model="listQuery.searchValue"
            class="search"
            placeholder="검색어를 입력하세요."
            @keyup.enter.native="handleSearch()"
          />
					<el-button @click="handleSearch()">
            <img src="~@/assets/images/search.png" alt="">
          </el-button>
				</div>

				<el-button class="calc" type="text" @click="handleVisibleForm()">정산하기</el-button>
				<!-- <button class="calc" @click="handleClickReadyContents()">영수증발행</button> -->
			</div>
		</div>

		<div class="user-content">
			<el-table
        v-loading="loading"
        ref="applyList"
        border
        style="width: 100%;"
        :data="applyList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }"
      >
				<el-table-column type="selection" width="55" :selectable="getSelectable" />
				<el-table-column prop="id" label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="shopName" label="매장명"></el-table-column>
        <el-table-column label="판매건수" width="110">
          <template slot-scope="scope">
            {{ scope.row.totalOrder | parseKrw }}건
          </template>
        </el-table-column>
				<el-table-column label="결제금액" width="140">
          <template slot-scope="scope">
            {{ scope.row.totalSale | parseKrw }}원
          </template>
        </el-table-column>
				<el-table-column label="정산금액" width="140">
          <template slot-scope="scope">
            {{ scope.row.totalSettle | parseKrw }}원
          </template>
        </el-table-column>
        <el-table-column label="상태" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.approvalStatus">정산완료</el-tag>
            <el-tag v-else type="info">정산대기</el-tag>
          </template>
        </el-table-column>
				<el-table-column label="정산신청일">
          <template slot-scope="scope">
            {{ scope.row.applyDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="정산완료일">
          <template slot-scope="scope">
            {{ scope.row.approvalDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="관리" width="110">
					<template slot-scope="scope">
            <el-button class="detail-button" type="text" @click="handleViewApply(scope.row.idx)">보기</el-button>
          </template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getApplyList()"
      />
		</div>
		<el-dialog
      class="order-dialog-wr calc"
      title="정산하기"
      :visible.sync="formVisible"
    >
			<SettleForm
        v-if="formVisible"
        :applyList="selectedApplyList"
        @close="handleVisibleForm()"
        @success="handleSuccessSettle()"
      />
		</el-dialog>
    <el-dialog
      class="order-dialog-wr calc"
      title="정산신청 상세내역"
      :visible.sync="detailVisible"
    >
      <ApplyDetail
        v-if="detailVisible"
        :idx="selectedIdx"
        @close="handleViewApply(null)"
      />
    </el-dialog>
	</div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getSettleApplyList } from '@/api/settleApply';
import ApplyDetail from './components/detail.vue';
import SettleForm from './components/form.vue';
import moment from 'moment';

@Component({
  components: {
    Pagination,
    ApplyDetail,
    SettleForm,
  },
})
export default class extends Vue {
  mounted() {
    this.getApplyList();
  }

	private listQuery = {
    page: 1,
    size: 20,
    startDate: '',
    endDate: '',
    searchType: 'shopName',
    searchValue: '',
  }

  private applyList = [];

  private totalElements = 0;

  private loading = true;

  private selectedIdx = null;

  private detailVisible = false;

  private formVisible = false;

  private selectedApplyList = [];

  private dateRange: any = [];

  private getApplyList() {
    this.loading = true;
    /* eslint-disable */
    if (this.dateRange && this.dateRange.length === 2) {
      this.listQuery.startDate = this.dateRange[0];
      this.listQuery.endDate = this.dateRange[1];
    } else {
      this.listQuery.startDate = '';
      this.listQuery.endDate = '';
    }
    /* eslint-enable */
    getSettleApplyList(this.listQuery).then((res) => {
      this.loading = false;
      this.totalElements = res.data.totalElements;
      this.applyList = res.data.content;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getApplyList();
  }

  private getNumber(index: number) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleViewApply(idx: any) {
    this.selectedIdx = idx;
    this.detailVisible = !this.detailVisible;
  }

  private handleVisibleForm() {
    const table = (this.$refs.applyList as any);
    if (table.selection.length > 0) {
      this.selectedApplyList = table.selection;
      this.formVisible = !this.formVisible;
    } else {
      this.$message.warning('정산할 건들을 선택해주세요.');
    }
  }

  private getSelectable(row: any) {
    return !row.approvalStatus;
  }

  private handleSuccessSettle() {
    this.handleVisibleForm();
    this.getApplyList();
  }

  private handleLastRange(type: any) {
    if (type === 'week') {
      this.dateRange = [moment().day(7).add(-1, 'week').format('YYYY-MM-DD'), moment().day(6).format('YYYY-MM-DD')];
      this.handleSearch();
    } else if (type === 'month') {
      this.dateRange = [moment().startOf('month').format('YYYY-MM-DD'), moment().endOf('month').format('YYYY-MM-DD')];
      this.handleSearch();
    }
  }

  private handleClickReadyContents() {
    this.$message.info('준비중인 기능입니다.');
  }
}
</script>
