<template>
	<div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-wrap shop">
				<div class="useradd-title">
					<div>
						<p class="tl">매장추가</p>
					</div>

					<div class="setting__btn__box">
						<button @click="$router.push({ name: 'ShopUserList' })" class="cancel">취소</button>
						<button class="save" @click="handleSave()">저장</button>
					</div>
				</div>

				<div class="useradd-section shop-add">
					<div class="useradd-content1">
						<p class="content-title">기본정보</p>
						<div class="content3-input">
							<div class="shop-input-wr">
								<label for="shopName">매장명</label>
								<el-form-item>
									<el-input v-model="form.shopName" type="text" />
								</el-form-item>
							</div>
							<div class="shop-address">
								<label for="shopAddress">매장 주소</label>
								<el-form-item>
									<el-input v-model="form.shopAddress" type="text" readonly />
									<el-button class="useradd-btn shop" @click="handleVisibleAddress()">검색</el-button>
									<div class="address-box">
										<label for="userAddress2"></label>
										<el-input v-model="form.shopAddressDetail" type="text" />
									</div>
								</el-form-item>
							</div>
							<div class="shop-input-wr">
								<label for="shopTel">전화번호</label>
								<el-form-item>
									<el-input v-model="form.shopTel" type="text" @input="filterConcatNumber" />
								</el-form-item>
							</div>
							<div class="shop-input-wr">
								<label for="shopOpenDate">개업일</label>
								<el-form-item prop="date">
									<el-col :span="11">
										<el-date-picker type="date" placeholder="Pick a date" v-model="form.shopOpenDate"
											style="width: 100%;"></el-date-picker>
									</el-col>
								</el-form-item>
							</div>
							<div class="shop-date-wr">
								<label for="shopPickUp">픽업가능요일</label>
								<el-form-item prop="openTime">
									<el-time-picker v-model="form.shopPickUp" value-format="HH:mm:ss" />
								</el-form-item>
							</div>
						</div>
					</div>
					<div class="useradd-content1"></div>
				</div>
			</div>
			<el-dialog width="600px" title="주소검색" :visible.sync="addressVisible">
				<vue-daum-postcode style="width: 100%" @complete="handleComplete" />
			</el-dialog>
		</el-form>
	</div>
</template>

<script lang="ts">
import { addShop, getShopDetail, updateShop } from '@/api/shop';
import { Form } from 'element-ui';
import { Vue, Component } from 'vue-property-decorator';
import { VueDaumPostcode } from 'vue-daum-postcode';

@Component({
	components: {
		VueDaumPostcode,
	},
})

export default class extends Vue {
	mounted() {
		// this.setForm();
	}

	private form = {
		shopName: '',
		shopAddress: '',
		shopAddressDetail: '',
		shopTel: '',
		shopPickUpDay: '',
	}

	private rules = {
		shopName: [
			{ required: true, message: '매장명을 입력하세요.', trigger: 'change' },
		],
		shopAddress: [
			{ required: true, message: '매장 주소를 입력하세요.', trigger: ['blur', 'change'] },
		],
		shopPickUpDay: [
			{ required: true, message: '픽업가능 요일을 입력하세요.', trigger: ['blur', 'change'] },
		],
	}

	private addressVisible = false;

	private handleSave() {
		(this.$refs.form as Form).validate((valid: boolean) => {
			if (valid) {
				if (this.$route.params.uid) {
					updateShop(this.$route.params.uid, this.form).then(() => {
						this.$message.success('픽업매장이 수정되었습니다.');
						this.$router.push({ name: 'ShopUserList' });
					});
				} else {
					addShop(this.form).then(() => {
						this.$message.success('픽업매장이 추가되었습니다.');
						this.$router.push({ name: 'ShopUserList' });
					});
				}
			}
		});
	}

	private handleVisibleAddress() {
		this.addressVisible = !this.addressVisible;
	}

	private handleComplete(result: any) {
		this.form.shopAddress = result.zonecode;
		this.form.shopAddressDetail = result.address;
		this.handleVisibleAddress();
	}
}
</script>
