<template>
	<div class="user-wrap">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">맞춤미션 관리</p>
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
            placeholder="검색할 맞춤미션명을 입력하세요."
            class="search"
            @keyup.enter.native="handleSearch()"
          />
					<el-button @click="handleSearch()">
            <img src="~@/assets/images/search.png" alt="">
          </el-button>
				</div>
				<router-link :to="{ name: 'MissionAdd' }">추가 +</router-link>
			</div>
		</div>
		<div v-loading="loading" class="user-content">
			<el-table
        :data="missionList"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        style="width: 100%;"
        border
      >
				<el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
				<el-table-column prop="title" width="" label="맞춤미션명" />
				<el-table-column prop="missionCategory.name" width="" label="카테고리" />
				<el-table-column prop="actualName" width="" label="유저명">
          <template slot-scope="scope" v-if="scope.row.missionUserList && scope.row.missionUserList.length > 0">
            {{ scope.row.missionUserList[0].actualName }}
          </template>
        </el-table-column>
				<el-table-column label="맞춤미션 기간" width="" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.startDate | parseDate('YYYY-MM-DD') }} ~ {{ scope.row.endDate | parseDate('YYYY-MM-DD') }}
          </template>
        </el-table-column>
        <el-table-column label="감량효과" width="100" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.achieveEffect }}Kg 감량
          </template>
        </el-table-column>
        <el-table-column label="지급포인트" width="100" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.dailyReward }}원
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
				<el-table-column label="유저 수락여부" width="100" class-name="sort">
          <template slot-scope="scope">
            {{ getUserApprovalStatus(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="현재상태" width="80" class-name="sort">
          <template slot-scope="scope">
              <span v-if="scope.row.missionUserList && scope.row.missionUserList.length > 0 && scope.row.missionUserList[0].abandonStatus">포기</span>
              <span v-else>{{ getMissionStatus(scope.row.status, scope.row.missionUserList && scope.row.missionUserList.length > 0 ? scope.row.missionUserList[0].approveStatus : undefined) }}</span>
          </template>
        </el-table-column>
				<el-table-column label="참여횟수" width="80" class-name="sort">
          <template slot-scope="scope">
            {{ scope.row.participantCnt || 0 }}
          </template>
        </el-table-column>
				<el-table-column label="관리" width="200">
					<template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'MissionEdit', params: { uid: scope.row.uid } }">수정</router-link>
						<el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getMissionList()"
      />
		</div>
	</div>
</template>

<script lang="ts">
import { deleteMission, getMissionList } from '@/api/mission';
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getMissionList();
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

  private missionList: any = [];

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

  private getMissionList() {
    this.loading = true;
    getMissionList(this.listQuery).then((res) => {
      this.missionList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getMissionList();
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private getUserApprovalStatus(mission: any) {
    if (!mission.missionUserList || mission.missionUserList.length === 0) {
      return '-';
    }

    const firstUser = mission.missionUserList[0];
    if (firstUser && typeof firstUser.approveStatus === 'boolean') {
      return firstUser.approveStatus ? 'Y' : 'N';
    }

    return '-';
  }

  private getMissionStatus(status: number, approveStatus?: boolean) {
    // 유저 승낙이 없으면 '수락대기'로 표시, 있으면 기존 로직
    if (status === 2) {
      if (approveStatus === true) {
        return '진행중';
      }
      return '수락대기';
    }
    switch (status) {
      case 0:
        return '실패';
      case 1:
        return '성공';
      case 3:
        return '시작전';
      default:
        return '-';
    }
  }

  private handleDelete(mission: any) {
    console.log(mission);
    this.$confirm(`정말 ${mission.title}을(를) 삭제하시겠습니까? 해당 맞춤미션에 기록된 모든 정보가 삭제됩니다.`, '맞춤미션 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteMission(mission.uid).then(() => {
        this.$message.success('성공적으로 맞춤미션이 삭제되었습니다.');
        this.getMissionList();
      });
    });
  }
}
</script>
