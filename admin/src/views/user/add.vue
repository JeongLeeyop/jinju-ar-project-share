<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">고객정보 수정</p>
			</div>
			<div class="setting__btn__box">
				<button @click="$router.push('UserList')" class="cancel">취소</button>
				<button class="save" type="submit" @click="handleSave()">저장</button>
			</div>
		</div>
		<el-form v-loading="loading" ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
						<div class="shop-input-wr">
							<label for="userName">이름</label>
							<el-form-item prop="actualName">
								<el-input placeholder="" type="text" v-model="form.actualName" />
							</el-form-item>
						</div>
						<div class="shop-date-wr">
							<label for="userBirth">생년월일</label>
							<el-form-item prop="birth">
								<el-date-picker
                  v-model="form.birth"
                  type="date"
                  placeholder="생년월일"
                  value-format="yyyy-MM-dd"
                />
							</el-form-item>
						</div>
						<div class="shop-radio-wr">
							<p class="content2-txt">성별</p>
							<el-form-item prop="gender">
								<el-radio-group v-model="form.gender">
									<el-radio :label="0">남</el-radio>
									<el-radio :label="1">여</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userTel">연락처</label>
							<el-form-item prop="concatNumber">
								<el-input
                  v-model="form.concatNumber"
                  type="text"
                  maxlength="13"
                  @input="formatTel()"
                />
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userEmail">이메일</label>
							<el-form-item prop="email">
								<el-input v-model="form.email" type="text" />
							</el-form-item>
						</div>

						<div class="shop-address">
							<label for="userAddress">배송지 주소</label>
							<el-form-item prop="address">
								<el-input type="text" v-model="form.address" readonly />
								<el-button class="useradd-btn" @click="handleVisibleAddressSearch()">검색</el-button>
								<div class="address-box">
									<label for="userAddressDetail"></label>
									<el-input v-model="form.addressDetail" type="text" />
								</div>
							</el-form-item>
						</div>
					</div>
				</div>
				<div class="useradd-content2">
					<p class="content-title">식단정보</P>
					<div class="content2-input">
						<div class="shop-radio-wr">
							<p class="content2-txt">식단관리 경험</p>
							<el-form-item prop="dietExperience">
								<el-radio-group v-model="form.dietExperience">
									<el-radio :label="true">네</el-radio>
									<el-radio :label="false">아니오</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="userHeight">키</label>
							<el-form-item prop="height">
								<el-input-number
                  v-model="form.height"
                  :min="0"
                  :controls="false"
                />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userWeight">몸무게</label>
							<el-form-item prop="weight">
								<el-input-number
                  v-model="form.weight"
                  :min="0"
                  :controls="false"
                />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userGoalWeight">목표 몸무게</label>
							<el-form-item prop="goalWeight">
								<el-input-number
                  v-model="form.goalWeight"
                  :min="0"
                  :controls="false"
                />
							</el-form-item>
						</div>

						<div class="shop-input-wr">
							<label for="userGoalDate">목표달성 날짜</label>
							<el-form-item prop="goalDate">
								<el-date-picker
									v-model="form.goalDate"
									type="date"
									value-format="yyyy-MM-dd"
                />
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
						<p class="content2-txt">활동 수준</p>
							<el-form-item prop="activityLevel">
								<el-radio-group v-model="form.activityLevel">
									<el-radio :label="1">비활동적</el-radio>
									<el-radio :label="2">저활동적</el-radio>
									<el-radio :label="3">활동적</el-radio>
									<el-radio :label="4">매우동적</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
						<p class="content2-txt">식단관리 목적</p>
							<el-form-item prop="dietPurpose">
								<el-radio-group v-model="form.dietPurpose">
									<el-radio label="다이어트">다이어트</el-radio>
									<el-radio label="영양균형">영양균형</el-radio>
									<el-radio label="체중증량">체중증량</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>

						<div class="shop-radio-wr">
							<p class="content2-txt">식단 유의사항</p>
							<el-form-item prop="precaution">
								<el-radio-group v-model="form.dietPrecaution">
									<el-radio label="없음">없음</el-radio>
									<el-radio label="비건">비건</el-radio>
									<el-radio label="알레르기">알레르기</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
					</div>
				</div>
			</div>
		</el-form>
    <el-dialog :visible.sync="searchAddressVisible" width="700px">
      <VueDaumPostcode @complete="handleSelectAddress" />
    </el-dialog>
	</div>
</template>

<script lang="ts">
import { getUser, updateUser } from '@/api/user';
import { Component, Vue } from 'vue-property-decorator';
import { VueDaumPostcode } from 'vue-daum-postcode';
import { parseConcatNumber } from '@/utils/filters';
import { Form } from 'element-ui';

@Component({
  components: {
    VueDaumPostcode,
  },
})
export default class extends Vue {
  mounted() {
    this.setForm();
  }

  private loading = false;

  private searchAddressVisible = false;

  private rules = {
    actualName: [
      { required: true, message: '이름을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    birth: [
      { required: true, message: '생년월일을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    gender: [
      { required: true, message: '성별을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    concatNumber: [
      { required: true, message: '연락처를 입력해주세요', trigger: ['blur', 'change'] },
    ],
    email: [
      { required: true, message: '이메일을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    address: [
      { required: true, message: '주소를 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    height: [
      { required: true, message: '키를 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    weight: [
      { required: true, message: '몸무게를 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    goalWeight: [
      { required: true, message: '목표 몸무게를 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    goalDate: [
      { required: true, message: '목표 달성일를 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    activityLevel: [
      { required: true, message: '활동 수준을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    dietPurpose: [
      { required: true, message: '식단관리 목적을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    dietPrecaution: [
      { required: true, message: '식단 유의사항을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
  }

  private form = {
    actualName: '',
    birth: '',
    gender: 0,
    concatNumber: '',
    email: '',
    postCode: '',
    address: '',
    addressDetail: '',
    dietExperience: false,
    height: 0,
    weight: 0,
    goalWeight: 0,
    goalDate: '',
    activityLevel: 0,
    dietPurpose: '',
    dietPrecaution: '',
  }

  private setForm() {
    if (this.$route.params.uid) {
      this.loading = true;
      getUser(this.$route.params.uid).then((res) => {
        this.form = res.data;
        this.loading = false;
      });
    }
  }

  private handleVisibleAddressSearch() {
    this.searchAddressVisible = !this.searchAddressVisible;
  }

  private handleSelectAddress(data: any) {
    this.handleVisibleAddressSearch();
    this.form.postCode = data.zonecode;
    this.form.address = data.address;
    this.form.addressDetail = '';
  }

  private formatTel() {
    this.form.concatNumber = parseConcatNumber(this.form.concatNumber);
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        updateUser(this.$route.params.uid, this.form).then(() => {
          this.$message.success('고객정보가 수정되었습니다.');
          this.$router.push({ name: 'UserList' });
        });
      }
    });
  }
}
</script>
