<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">고객관리 (홍길동0)</p>
			</div>

			<div class="setting__btn__box">
				<button @click="$router.go(-1)" class="cancel">취소</button>
				<button class="save" type="submit">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
						<div class="shop-input-wr">
							<label for="userName">이름</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userName" />
							</el-form-item>
						</div>
						<div class="shop-date-wr">
							<label for="userBirth">생년월일</label>
							<el-form-item>
								<el-date-picker type="date" placeholder="생년월일" v-model="form.userBirth"></el-date-picker>
							</el-form-item>
						</div>
						<div class="shop-radio-wr">
							<p class="content2-txt">성별</p>
							<el-form-item>
								<el-radio-group v-model="form.userGender">
									<el-radio :label="11">남</el-radio>
									<el-radio :label="12">여</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userTel">연락처</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userTel" />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userEmail">이메일</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userEmail" />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userPassword">비밀번호</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userPassword" />
							</el-form-item>
						</div>

						<div class="shop-address">
							<label for="userAddress">배송지 주소</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userAddress" />
								<el-button class="useradd-btn">검색</el-button>

								<div class="address-box">
									<label for="userAddressDetail"></label>
									<el-input placeholder="" type="text" v-model="form.userAddressDetail" />
								</div>
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userPoint" class="useradd-txt color1">포인트</label>
							<el-form-item>
								<el-input :disabled="true" placeholder="" type="text" v-model="form.userPoint" />
							</el-form-item>
						</div>

					</div>
				</div>
				<div class="useradd-content2">
					<p class="content-title">식단정보</P>
					<div class="content2-input">
						<div class="shop-radio-wr">
							<p class="content2-txt">식단관리 경험</p>
							<el-form-item>
								<el-radio-group v-model="form.userExperience">
									<el-radio :label="11">네</el-radio>
									<el-radio :label="12">아니오</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userHeight">키</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userHeight" />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userWeight">몸무게</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userWeight" />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userGoalWeight">목표 몸무게</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userGoalWeight" />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userGoalDate">목표달성 날짜</label>
							<el-form-item>
								<el-input placeholder="" type="text" v-model="form.userGoalDate" />
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
							<p class="content2-txt">활동 수준</p>
							<el-form-item>
								<el-radio-group v-model="form.userActivityLevel">
									<el-radio :label="21">비활동적</el-radio>
									<el-radio :label="22">저활동적</el-radio>
									<el-radio :label="23">활동적</el-radio>
									<el-radio :label="24">매우동적</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
							<p class="content2-txt">식단관리 목적</p>
							<el-form-item>
								<el-radio-group v-model="form.userPurpose">
									<el-radio :label="31">다이어트</el-radio>
									<el-radio :label="32">영양균형</el-radio>
									<el-radio :label="33">체중증량</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
							<p class="content2-txt">식단 유의사항</p>
							<el-form-item>
								<el-radio-group v-model="form.userPrecaution">
									<el-radio :label="41">없음</el-radio>
									<el-radio :label="42">비건</el-radio>
									<el-radio :label="43">알레르기</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script>
import orderInfo from '@/data/useradd.json';

export default {
	components: {
	},
	created() {
		for (let i = 0; i < 1000; i += 1) {
			this.tableData.push({
				id: `${i}`,
				orderNum: `${orderInfo[i].orderNum}`,
				orderDay: `${orderInfo[i].orderDay}`,
				orderList: `${orderInfo[i].orderList}`,
				orderState: `${orderInfo[i].orderState}`,
			});
		}
	},
	data() {
		return {
			form: {
				// 사용변수
				userName: '',
				userBirth: '',
				userGender: '',
				userTel: '',
				userEmail: '',
				userPassword: '',
				userAddress: '',
				userAddressDetail: '',
				userPoint: '',
				userExperience: '',
				userHeight: '',
				userWeight: '',
				userGoalWeight: '',
				userGoalDate: '',
				userActivityLevel: '',
				userPurpose: '',
				userPrecaution: '',
			},
			// 더미데이터용 변수
			orderInfo,
			tableData: [],
			page: 1,
			pageSize: 4,
			rules: {
				userName: [
					{ required: true, message: '이름을 입력하세요.', trigger: ['blur', 'change'] },
				],
				userBirth: [
					{
						required: true, type: 'date', message: 'Please pick a date', trigger: 'change',
					},
				],
				userGender: [
					{ required: true, message: '성별을 입력하세요.', trigger: ['blur', 'change'] },
				],
				userTel: [
					{ required: true, message: '전화번호를 입력하세요.', trigger: ['blur', 'change'] },
				],
				userEmail: [
					{ required: true, message: '이메일을 입력하세요.', trigger: ['blur', 'change'] },
				],
				userPassword: [
					{ required: true, message: '비밀번호를 입력하세요.', trigger: ['blur', 'change'] },
				],
				userAddress: [
					{ required: true, message: '주소를 입력하세요.', trigger: ['blur', 'change'] },
				],
				userAddressDetail: [
					{ required: true, message: '상세주소를 입력하세요.', trigger: ['blur', 'change'] },
				],
				userPoint: [
					{ required: true, message: '포인트를 입력하세요.', trigger: ['blur', 'change'] },
				],
			},
		};
	},
	computed: {
		orderInfos() {
			return this.tableData.slice(
				this.pageSize * this.page - this.pageSize,
				this.pageSize * this.page,
			);
		},
	},
	methods: {
		setPage(val) {
			this.page = val;
		},
	},
};
</script>
