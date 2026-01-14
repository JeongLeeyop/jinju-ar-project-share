<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">매니저관리 (홍길동)</p>
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
						<div class="shop-selectbox-wr">
							<label for="managerShopName">매장명</label>
							<el-select v-model="form.managerShopName">
								<el-option label="충무공점" value="1"></el-option>
								<el-option label="진주신안점" value="2"></el-option>
								<el-option label="윙스타워점" value="3"></el-option>
							</el-select>
						</div>
						<div class="shop-input-wr">
							<label for="managerName">매니저명</label>
							<el-input placeholder="" type="text" id="managerName" v-model="form.managerName" />
						</div>
						<div class="shop-input-wr">
							<label for="managerPhone">연락처</label>
							<el-input placeholder="" type="text" id="managerPhone" v-model="form.managerPhone" />
						</div>
						<div class="shop-input-wr">
							<div class="shop-date-wr">
								<label for="managerJoinDate">매니저등록일</label>
								<el-form-item>
									<el-date-picker type="date" placeholder="매니저등록일" v-model="form.managerJoinDate"></el-date-picker>
								</el-form-item>
							</div>
						</div>
					</div>
				</div>
				<div class="useradd-content2">
				</div>
			</div>
		</el-form>
	</div>
</template>

<script>
import orderInfo from '@/data/useradd.json';

export default {
	data() {
		return {
			form: {
				// 사용변수
				managerShopName: '',
				managerName: '',
				managerPhone: '',
				managerJoinDate: '',
			},
		};
	},
};
</script>
