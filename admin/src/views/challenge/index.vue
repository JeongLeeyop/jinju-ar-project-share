<template>
	<div class="user-wrap">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">챌린지 관리</p>
				<p class="usernumber">총 {{ totalElements }}개</p>
			</div>

			<div class="user__tab">
				<div class="user__subtab">
					<p class="subtab-title">검색기간</p>
					<el-date-picker
            v-model="range"
            style="width:100%"
            type="daterange"
            align="right"
            value-format="yyyy-MM-dd"
            range-separator="~"
            start-placeholder="시작일"
						end-placeholder="종료일"
            @change="handleChangeRange"
          />
					<el-input
            v-model="listQuery.title"
            placeholder="검색할 챌린지명을 입력하세요."
            class="search"
            @keyup.enter.native="handleSearch()"
          />
					<el-button @click="handleSearch()">
            <img src="~@/assets/images/search.png" alt="">
          </el-button>
				</div>
				<router-link :to="{ name: 'ChallengeAdd' }">추가 +</router-link>
			</div>
		</div>
		<div v-loading="loading" class="user-content">
			<el-table
        :data="challengeList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        style="width: 100%;"
        border
      >
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="title" width="" label="챌린지명" />
				<el-table-column prop="challengeCategory.name" width="" label="카테고리" />
				<el-table-column label="챌린지 기간" width="" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.startDate | parseDate('YYYY-MM-DD hh:mm') }} ~ {{ scope.row.endDate | parseDate('YYYY-MM-DD hh:mm') }}
          </template>
        </el-table-column>
        <el-table-column label="총 일수" width="70" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.totalDay }}일
          </template>
        </el-table-column>
				<el-table-column label="생성일" width="170" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
				<el-table-column label="사용여부" width="70" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.useStatus === true ? 'Y' : 'N' }}
          </template>
        </el-table-column>
				<el-table-column label="관리" width="200">
					<template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'ChallengeEdit', params: { uid: scope.row.uid } }">수정</router-link>
						<el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getChallengeList()"
      />
		</div>
	</div>
</template>

<script lang="ts">
import { deleteChallenge, getChallengeList } from '@/api/challenge';
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getChallengeList();
  }

  private loading = true;

  private range: any = '';

  private listQuery = {
    page: 1,
    size: 10,
    title: '',
    startDate: '',
    endDate: '',
  }

  private totalElements = 0;

  private challengeList: any = [];

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

  private getChallengeList() {
    this.loading = true;
    getChallengeList(this.listQuery).then((res) => {
      this.challengeList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getChallengeList();
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleDelete(challenge: any) {
    console.log(challenge);
    this.$confirm(`정말 ${challenge.title}을(를) 삭제하시겠습니까? 해당 챌린지에 기록된 모든 정보가 삭제됩니다.`, '챌린지 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteChallenge(challenge.uid).then(() => {
        this.$message.success('성공적으로 챌린지가 삭제되었습니다.');
        this.getChallengeList();
      });
    });
  }
}
</script>
