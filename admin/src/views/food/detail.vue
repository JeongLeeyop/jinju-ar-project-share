<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">연어 샐러드 수정</p>
			</div>

			<div class="setting__btn__box">
				<button @click="$router.go(-1)" class="cancel">취소</button>
				<button class="save" type="submit">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">영양정보 등록</p>
					<div class="content2-input">
						<div class="shop-input-wr small">
							<label for="foodKcal">칼로리(Kcal)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodCalorie" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodCarbo">탄수화물(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodCarbo" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodProtein">단백질(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodProtein" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFat">지방(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodFat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSodium">나트륨(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodSodium" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSugar">당(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodSugar" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSatfat">포화지방산(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodSatfat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodTransfat">트랜스지방(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodTransfat" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodChol">콜레스테롤(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodChol" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodCal">칼슘(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodCal" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFe">철(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodFe" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodPota">칼륨(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodPota" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFiber">식이섬유(g)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodFiber" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodVitaC">비타민C(mg)</label>
							<el-form-item>
								<el-input placeholder="" v-model.number="form.foodVitaC" />
							</el-form-item>
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
import orderInfo from '@/data/useradd.json';

export default {
	data() {
		return {
			form: {
				// 사용변수
				foodName: '',
				foodWeight: '',
				foodSource1: '',
			},
		};
	},
};
</script>
