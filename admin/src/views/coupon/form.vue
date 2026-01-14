<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">쿠폰 {{ $route.params.idx ? '수정' : '추가' }}</p>
			</div>

			<div class="setting__btn__box">
				<button @click="handleCancel()" class="cancel">취소</button>
				<button class="save" @click="handleSave()">저장</button>
			</div>
		</div>
		<el-form
      v-loading="loading"
      ref="form"
      :model="form"
      :rules="rules"
      onsubmit="return false"
    >
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">쿠폰관리</p>
					<div class="content1-input">
						<div class="shop-input-wr">
							<label for="couponName">쿠폰명</label>
              <el-form-item prop="name">
                <el-input placeholder="" type="text" id="couponName" v-model="form.name" />
              </el-form-item>
						</div>
						<div class="shop-selectbox-wr">
							<label for="couponType">쿠폰타입</label>
              <el-form-item prop="type">
                <el-select v-model="form.type">
                  <el-option label="회원가입 시 지급" value="JOIN" />
                  <el-option label="특정금액 이상 구매 시 지급" value="BUY" />
                </el-select>
              </el-form-item>
						</div>
						<div class="shop-radio-wr">
							<p class="content2-txt">할인방식</p>
							<el-radio-group v-model="form.percentStatus">
								<el-radio :label="false">할인금액</el-radio>
								<el-radio :label="true">할인률</el-radio>
							</el-radio-group>
						</div>
            <div v-if="form.percentStatus" class="shop-input-wr">
              <label for="discountRate">할인률</label>
              <el-input-number
                v-model="form.discountPercent"
                id="discountRate"
                :min="1"
                :max="100"
                :controls="false"
              />
            </div>
						<div v-else class="shop-input-wr">
							<label for="discountAmount">할인금액</label>
              <el-form-item prop="discountPrice">
                <el-input-number
                  v-model="form.discountPrice"
                  id="couponName"
                  :min="0"
                  :controls="false"
                />
              </el-form-item>
						</div>
            <div v-if="form.type === 'BUY'" class="shop-input-wr">
							<label for="minimumAmount">지급 조건 금액</label>
							<el-input-number v-model="form.giveStandardPrice" id="minimumAmount" :min="0" />
						</div>
						<div class="shop-date-wr">
							<label for="downloadDateRange">지급 기간</label>
              <el-form-item prop="giveRange">
                <el-date-picker
                  v-model="form.giveRange"
                  type="datetimerange"
                  align="right"
                  start-placeholder="시작일"
                  end-placeholder="종료일"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                  @change="handleChangeGiveRange"
                />
              </el-form-item>
						</div>
            <div class="shop-input-wr">
							<label for="validationDate">쿠폰 만료일</label>
              <el-form-item prop="expiredDate">
                <el-date-picker
                  v-model="form.expiredDate"
                  type="datetime"
                  placeholder="쿠폰 만료일"
                  value-format="yyyy-MM-ddTHH:mm:ss"
                />
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

<script lang="ts">
import { addCoupon, getCouponDetail, updateCoupon } from '@/api/coupon';
import { Form } from 'element-ui';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {
  },
})
export default class extends Vue {
  mounted() {
    if (this.$route.params.idx) {
      this.setForm();
    }
  }

  private loading = false;

	private form:any = {
		name: '',
    type: '',
    percentStatus: false,
    discountPercent: 0,
    discountPrice: 0,
    giveStandardPrice: 0,
    startDate: '',
    endDate: '',
    expiredDate: '',
    giveRange: '',
	}

  /* eslint-disable */
  private rules = {
    name: [
      { required: true, message: '쿠폰명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    type: [
      { required: true, message: '쿠폰타입을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    discountPercent: [
      { required: true, message: '할인율을 입력해주세요.', trigger: ['blur', 'change'] },
      { min: 1, max: 100, message: '할인율을 1에서 100퍼센트로 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    discountPrice: [
      { required: true, message: '할인금액을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    giveStandardPrice: [
      { required: true, message: '지급 조건 금액을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    giveRange: [
      { required: true, message: '지급기간을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    expiredDate: [
      { required: true, message: '만료일을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
  }
  /* eslint-enable */

  private setForm() {
    this.loading = true;
    getCouponDetail(this.$route.params.idx).then((res) => {
      this.form = {
        ...res.data,
        giveRange: [res.data.startDate, res.data.endDate],
      };
      this.loading = false;
    });
  }

  private handleCancel() {
    this.$router.push({ name: 'CouponList' });
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        if (this.$route.params.idx) {
          updateCoupon(this.$route.params.idx, this.form).then(() => {
            this.$message.success('쿠폰이 수정되었습니다.');
            this.$router.push({ name: 'CouponList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        } else {
          addCoupon(this.form).then(() => {
            this.$message.success('쿠폰이 추가되었습니다.');
            this.$router.push({ name: 'CouponList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        }
      }
    });
  }

  /* eslint-disable */
  private handleChangeGiveRange() {
    if (this.form.giveRange && this.form.giveRange.length === 2) {
      this.form.startDate = this.form.giveRange[0];
      this.form.endDate = this.form.giveRange[1];
    } else {
      this.form.startDate = '';
      this.form.endDate = '';
    }
  }
  /* eslint-enable */
}
</script>
