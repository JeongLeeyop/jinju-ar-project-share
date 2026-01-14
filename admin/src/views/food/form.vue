<template>
	<div class="food-add-wrap">
		<div class="food-add-title">
			<div>
				<p class="tl">음식 {{ $route.params.idx ? '수정' : '추가' }}</p>
			</div>
			<div class="setting__btn__box">
				<button @click="$router.push({ name: 'FoodList' })" class="cancel">취소</button>
				<button class="save" type="submit" @click="handleSave()">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false" v-loading="loading">
			<div class="food-add-section">
				<div class="food-add-content1">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
						<div class="shop-input-wr">
							<label for="foodName">음식명</label>
              <el-form-item prop="name">
                <el-input type="text" id="foodName" v-model="form.name" />
              </el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="foodWeight">중량</label>
              <el-form-item>
                <el-input-number v-model="form.weightOnce" id="foodWeight" :min="0" :controls="false" />
                <el-select v-model="form.weightType" style="width: 100px;">
                  <el-option value="g" label="g" />
                  <el-option value="ml" label="ml" />
                </el-select>
              </el-form-item>
						</div>
					</div>
				</div>
				<div class="food-add-content1">
					<p class="content-title">영양정보 등록</p>
					<div class="content2-input">
						<div class="shop-input-wr small">
							<label for="foodKcal">칼로리(Kcal)</label>
							<el-form-item>
								<el-input-number v-model="form.kcal" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodCarbo">탄수화물(g)</label>
							<el-form-item>
								<el-input-number v-model="form.carbohydrate" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodProtein">단백질(g)</label>
							<el-form-item>
								<el-input-number v-model="form.protein" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFat">지방(g)</label>
							<el-form-item>
								<el-input-number v-model="form.fat" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSodium">나트륨(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.natrium" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSugar">당(g)</label>
							<el-form-item>
								<el-input-number v-model="form.sugar" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodSatfat">포화지방산(g)</label>
							<el-form-item>
								<el-input-number v-model="form.stFattyAcid" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodTransfat">트랜스지방(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.transFat" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodChol">콜레스테롤(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.cholesterol" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodCal">칼슘(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.calcium" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFe">철(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.iron2" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodPota">칼륨(mg)</label>
							<el-form-item>
								<el-input-number v-model="form.potassium" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodFiber">식이섬유(g)</label>
							<el-form-item>
								<el-input-number v-model="form.dietaryFiber" :min="0" :controls="false" />
							</el-form-item>
						</div>
						<div class="shop-input-wr small">
							<label for="foodVitaC">비타민C(mg)</label>
							<el-form-item>
								<el-input-number v-model.number="form.vitaminC" :min="0" :controls="false" />
							</el-form-item>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang="ts">
import { addFood, getFood, updateFood } from '@/api/food';
import { Form } from 'element-ui';
import { Vue, Component } from 'vue-property-decorator';

@Component({
})
export default class extends Vue {
  mounted() {
    this.setForm();
  }

  private loading = true;

	private form = {
    weightOnce: 0,
    weightType: 'g',
    totalG: 0,
    totalML: 0,
    name: '',
    kcal: 0,
    carbohydrate: 0,
    protein: 0,
    fat: 0,
    natrium: 0,
    sugar: 0,
    stFattyAcid: 0,
    transFat: 0,
    cholesterol: 0,
    calcium: 0,
    iron: 0,
    iron2: 0,
    potassium: 0,
    dietaryFiber: 0,
    vitaminC: 0,
    moisture: 0,
    caffeine: 0,
  }

  private rules = {
    name: [
      { required: true, message: '음식명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    weightOnce: [
      { required: true, message: '중량을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        if (this.$route.params.idx) {
          updateFood(this.$route.params.idx, this.form).then((res) => {
            this.$message.success('음식정보가 수정되었습니다.');
            this.$router.push({ name: 'FoodList' });
          });
        } else {
          addFood(this.form).then((res) => {
            this.$message.success('음식정보가 추가되었습니다.');
            this.$router.push({ name: 'FoodList' });
          });
        }
      }
    });
  }

  private setForm() {
    if (this.$route.params.idx) {
      getFood(this.$route.params.idx).then((res) => {
        this.form = res.data;
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }
}
</script>
