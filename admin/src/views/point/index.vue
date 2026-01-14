<template>
	<div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-wrap shop">
				<div class="useradd-title">
					<p class="tl">적립률 관리</p>
					<div class="setting__btn__box">
						<button @click="$router.push({ name: 'ShopUserList' })" class="cancel">취소</button>
						<button class="save" @click="handleSave()">저장</button>
					</div>
				</div>

				<div class="useradd-section shop-add">
					<div class="useradd-content1">
						<p class="content-title">포인트 적립률 관리</p>
						<div class="content3-input">
							<div class="shop-input-wr">
								<label for="rate">적립률(%)</label>
								<el-form-item>
									<el-input v-model="form.rate" type="text" />
								</el-form-item>
							</div>
						</div>
					</div>
					<div class="useradd-content1">
					</div>
				</div>
			</div>
			<el-dialog width="600px" title="주소검색" :visible.sync="addressVisible">
				<vue-daum-postcode style="width: 100%" @complete="handleComplete" />
			</el-dialog>
		</el-form>
	</div>
</template>

<script lang="ts">
import { addShop, updateShop } from '@/api/shop';
import { Form } from 'element-ui';
import { Vue, Component } from 'vue-property-decorator';
import { VueDaumPostcode } from 'vue-daum-postcode';

@Component({
	components: {
		VueDaumPostcode,
	},
})

export default class extends Vue {
	private form = {
		rate: '',
	}

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
}
</script>
