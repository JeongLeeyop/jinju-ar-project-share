<template>
	<div class="user-wrap">
		<div class="user-title">
			<div class="tl__box">
				<p class="tl">식단후기</p>
				<p class="usernumber">총 789건</p>
			</div>

			<div class="user__tab">
				<div class="user__subtab">
					<p class="subtab-title">검색기간</p>
					<el-date-picker v-model="date1" type="daterange" align="right" range-separator="~" start-placeholder=""
						end-placeholder="" default-value="2022-11-01">
					</el-date-picker>
					<el-select v-model="select" slot="prepend" placeholder="">
						<el-option label="주문번호" value="orderNum"></el-option>
						<el-option label="분야" value="reviewField"></el-option>
						<el-option label="내용" value="reviewText"></el-option>
						<el-option label="작성자" value="userName"></el-option>
					</el-select>
					<el-input placeholder="" v-model="input" class="search"></el-input>
					<el-button><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
			</div>
		</div>

		<div class="user-content board">
			<el-table :data="reviews" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
				:default-sort="{ prop: 'date', order: 'descending' }" style="width: 100%;" border>
				<el-table-column prop="id" label="번호" width="70"></el-table-column>
				<el-table-column prop="reviewField" label="분야" sortable width="150" class-name="sort"></el-table-column>
				<el-table-column prop="reviewText" label="내용" class-name="left"></el-table-column>
				<el-table-column prop="orderNum" label="주문번호" width="150"></el-table-column>
				<el-table-column prop="reviewFile" label="첨부파일" width="100"></el-table-column>
				<el-table-column prop="reviewRate" label="점수" width="130"></el-table-column>
				<el-table-column prop="userName" label="작성자" width="150"></el-table-column>
				<el-table-column prop="reviewDay" label="작성일" sortable width="150" class-name="sort"></el-table-column>
				<el-table-column label="관리" width="200">
					<template slot-scope="scope">
						<el-button class="detail-button" type="text" @click="dialogTableVisible = true">보기</el-button>
						<el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination background layout="prev, pager, next" :total="(this.tableData.length / this.pageSize * 10)"
				@current-change="setPage">
			</el-pagination>
		</div>
		<el-dialog title="식단후기" class="board-detail" :visible.sync="dialogTableVisible">
			<div class="board-wr">
				<div class="board-ttl">식단너무 만족합니다.</div>
				<div class="board-header">
					<ul class="board-header-list">
						<li>
							<div class="ttl">작성자</div>
							<div class="con"><span class="sv_member">홍길동</span></div>
						</li>
						<li>
							<div class="ttl">작성일</div>
							<div class="con">23-03-10</div>
						</li>
						<li>
							<div class="ttl">조회수</div>
							<div class="con">0</div>
						</li>
					</ul>
				</div>
				<div class="board-content">
					<div class="board-img"></div>
					<p>식단후기 입니다. 8주동안 5kg 감량 성공했어요</p>
					<p>식단후기 입니다. 8주동안 5kg 감량 성공했어요</p>
					<p>식단후기 입니다. 8주동안 5kg 감량 성공했어요</p>
					<p>식단후기 입니다. 8주동안 5kg 감량 성공했어요</p>
					<ul>
						<li class="board-button-wr">
							<router-link class="board-button btn01" :to="{ name: 'BoardReviewAdd' }">
								답변하기
							</router-link>
						</li>
					</ul>
				</div>
				<div class="board-footer">
					<section class="board-file">
						<ul>
							<li>
								<a href="#" class="view_file_download" download="">
									<strong>웨일리잇 첨부파일.hwp</strong> (322.5K)
								</a>
								<br>
								<span class="board-file-count">DATE : 2023-03-08 15:17:23</span>
							</li>
							<li>
								<a href="#" class="view_file_download" download="">
									<strong>웨일리잇 첨부파일.pdf</strong> (2.9M)
								</a>
								<br>
								<span class="board-file-count">DATE : 2023-03-08 15:17:23</span>
							</li>
						</ul>
					</section>
				</div>
			</div>
		</el-dialog>
	</div>
</template>

<script>
import review from '@/data/review.json';

export default {
	components: {
	},
	created() {
		for (let i = 0; i < 1000; i += 1) {
			this.tableData.push({
				id: `${i}`,
				orderNum: `${review[i].orderNum}`,
				reviewField: `${review[i].reviewField}`,
				reviewText: `${review[i].reviewText}`,
				reviewFile: `${review[i].reviewFile}`,
				reviewRate: `${review[i].reviewRate}`,
				userName: `${review[i].userName}`,
				reviewDay: `${review[i].reviewDay}`,
			});
		}
	},
	data() {
		return {
			date1: '',
			input: '',
			select: '',
			review,
			tableData: [],
			page: 1,
			pageSize: 14,
			dialogTableVisible: false,
		};
	},
	computed: {
		reviews() {
			return this.tableData.slice(
				this.pageSize * this.page - this.pageSize,
				this.pageSize * this.page,
			);
		},
	},
	methods: {
		handleEdit(index, row) {
			console.log(index, row);
		},
		handleDelete(index, row) {
			console.log(index, row);
		},
		setPage(val) {
			this.page = val;
		},
		handleCurrentChange(val) {
			this.page = val;
		},
	},
};
</script>
