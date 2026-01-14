<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">판매상품 추가</p>
			</div>

			<div class="setting__btn__box">
				<button @click="$router.go(-1)" class="cancel">취소</button>
				<button class="save" type="submit">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">쿠폰관리</p>
					<div class="content1-input">
						<div class="shop-input-wr">
							<label for="couponName">쿠폰명</label>
							<el-input placeholder="" type="text" id="couponName" v-model="form.couponName" />
						</div>
						<div class="shop-selectbox-wr">
							<label for="couponType">쿠폰형식</label>
							<el-select v-model="form.couponType">
								<el-option label="고객 다운로드" value="down"></el-option>
								<el-option label="자동발행" value="auto"></el-option>
							</el-select>
						</div>
						<div class="shop-selectbox-wr">
							<label for="eventName">이벤트명</label>
							<el-select v-model="form.eventName">
								<el-option label="신규가입" value="down"></el-option>
								<el-option label="첫구매" value="first"></el-option>
								<el-option label="생일쿠폰" value="birthday"></el-option>
								<el-option label="일정금액 이상시" value="goalPrice"></el-option>
							</el-select>
						</div>
						<div class="shop-radio-wr">
							<p class="content2-txt">할인방식</p>
							<el-radio-group v-model="form.discountType">
								<el-radio :label="amount">할인금액</el-radio>
								<el-radio :label="rate">할인률</el-radio>
							</el-radio-group>
						</div>
						<div class="shop-input-wr">
							<label for="discountAmount">할인금액</label>
							<el-input placeholder="" type="number" id="couponName" v-model="form.discountAmount" />
						</div>
						<div class="shop-input-wr">
							<label for="discountRate">할인률</label>
							<el-input placeholder="" type="number" id="discountRate" v-model="form.discountRate" />
						</div>
						<div class="shop-input-wr">
							<label for="validationDate">쿠폰유효기간</label>
							<el-input placeholder="" type="number" id="validationDate" v-model="form.validationDate" />
						</div>
						<div class="shop-input-wr">
							<label for="minimumAmount">최소주문금액</label>
							<el-input placeholder="" type="number" id="minimumAmount" v-model="form.minimumAmount" />
						</div>
						<div class="shop-date-wr">
							<label for="downloadDateRange">다운로드가능기간</label>
							<el-date-picker v-model="form.downloadDateRange" type="downloadDateRange" align="right" start-placeholder="시작일"
								end-placeholder="종료일" default-value="2023-01-01">
							</el-date-picker>
						</div>
						<div class="shop-date-wr">
							<label for="daterange">사용기간</label>
							<el-date-picker v-model="form.dateRange" type="daterange" align="right" start-placeholder="시작일"
								end-placeholder="종료일" default-value="2023-01-01">
							</el-date-picker>
						</div>
					</div>
				</div>
				<div class="useradd-content1">
				</div>
			</div>
		</el-form>
	</div>
</template>

<script>

export default {
	data() {
		const generateData = () => {
			const data = [];
			const states = ['충무공점', '신안점', '윙스타워점', '중앙점', '본사점', '기타점', '부산점'];
			const initials = ['CA', 'IL', 'MD', 'TX', 'FL', 'CO', 'CT'];
			states.forEach((city, index) => {
				data.push({
					label: city,
					key: index,
					initial: initials[index],
				});
			});
			return data;
		};

		return {
			form: {
				couponName: '',
				couponType: '',
				eventName: '',
				discountType: '',
				discountAmount: '',
				discountRate: '',
				useDate: '',
				validationDate: '',
				minimumAmount: '',
				downloadDateRange: '',
				dateRange: '',
			},
			data: generateData(),
			value: [],
			filterMethod(query, item) {
				return item.initial.toLowerCase().indexOf(query.toLowerCase()) > -1;
			},
		};
	},
};
</script>
