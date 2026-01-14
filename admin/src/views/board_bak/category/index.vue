<template>
	<div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-wrap shop">
				<div class="useradd-title">
					<p class="tl">게시판 항목관리</p>
					<div class="setting__btn__box">
						<button @click="$router.push({ name: 'ShopUserList' })" class="cancel">취소</button>
						<button class="save" @click="handleSave()">저장</button>
					</div>
				</div>

				<div class="useradd-section shop-add">
					<div class="useradd-content1">
						<p class="content-title">게시판별 항목관리</p>
						<p class="title-add">* <b>추가</b>할 항목을 적고 우측 상단에 저장을 누르세요</p>
						<div class="content3-input">
							<div class="shop-input-wr">
								<label for="dietType">영양상담</label>
								<el-form-item>
									<el-input v-model="form.boardCounsel" type="text" />
								</el-form-item>
							</div>
							<div class="shop-input-wr">
								<label for="dietPurpose">건강피드</label>
								<el-form-item>
									<el-input v-model="form.boardHealthFeed" type="text" />
								</el-form-item>
							</div>
							<div class="shop-input-wr">
								<label for="dietPrecaution">서비스문의</label>
								<el-form-item>
									<el-input v-model="form.boardSupport" type="text" />
								</el-form-item>
							</div>
						</div>
					</div>
					<div class="useradd-content1">
						<p class="content-title">맞춤식단 항목</p>
						<p class="title-add">* <b>삭제</b>할 항목을 체크한 후 우측 상단에 저장을 누르세요</p>
						<div class="custom-diet-wr">
							<div class="sort">
								<div class="ttl">영양상담 게시판 항목</div>
								<el-form-item>
									<el-checkbox-group v-model="form.boardCounsel">
										<el-checkbox label="식단관리" name="dietManage"></el-checkbox>
										<el-checkbox label="다이어트" name="diet"></el-checkbox>
										<el-checkbox label="식습관" name="habits"></el-checkbox>
										<el-checkbox label="영양관리" name="nutritionManage"></el-checkbox>
										<el-checkbox label="기타" name="etc"></el-checkbox>
									</el-checkbox-group>
								</el-form-item>
							</div>
							<div class="sort">
								<div class="ttl">건강피드 게시판 항목</div>
								<el-form-item>
									<el-checkbox-group v-model="form.boardHealthFeed">
										<el-checkbox label="영양정보" name="nutritionInfo"></el-checkbox>
										<el-checkbox label="식재료" name="ingredient"></el-checkbox>
										<el-checkbox label="레시피" name="recipe"></el-checkbox>
										<el-checkbox label="다이어트" name="diet"></el-checkbox>
										<el-checkbox label="기타" name="etc"></el-checkbox>
									</el-checkbox-group>
								</el-form-item>
							</div>
							<div class="sort">
								<div class="ttl">서비스문의 게시판 항목</div>
								<el-checkbox-group v-model="form.boardCounsel">
									<el-checkbox label="픽업문의" name="pickup"></el-checkbox>
									<el-checkbox label="서비스문의" name="service"></el-checkbox>
									<el-checkbox label="기타" name="etc"></el-checkbox>
								</el-checkbox-group>
							</div>
						</div>
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
		boardCounsel: '',
		boardHealthFeed: '',
		boardSupport: '',
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
