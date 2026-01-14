<template>
	<div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-wrap shop">
				<div class="useradd-title">
					<p class="tl">정산금액관리</p>
					<div class="setting__btn__box">
						<!--
            <button @click="$router.push({ name: 'ShopUserList' })" class="cancel">취소</button>
            -->
						<button class="save" @click="handleSave()">저장</button>
					</div>
				</div>

				<div class="useradd-section shop-add" v-loading="loading">
					<div class="useradd-content1">
						<p class="content-title">정산금액 설정 (주문당)</p>
						<div class="content3-input">
							<div class="shop-input-wr">
								<label for="cost">정산금액</label>
								<el-form-item prop="amount">
									<el-input v-model="form.amount" type="number" :min="0" :controls="false" />
								</el-form-item>
							</div>
						</div>
					</div>
					<div class="useradd-content1">
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang="ts">
import { Form } from 'element-ui';
import { Vue, Component } from 'vue-property-decorator';
import { getSettleSetting, saveSettleSetting } from '@/api/settleSetting';

@Component({
	components: {
	},
})

export default class extends Vue {
  mounted() {
    this.getSettleSetting();
  }

  private loading = true;

	private form = {
		amount: 0,
	}

  private rules = {
    amount: [
      { required: true, message: '정산금액을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
  }

  private getSettleSetting() {
    getSettleSetting().then((res) => {
      if (res.data) {
        this.form = res.data;
      }
      this.loading = false;
    });
  }

	private handleSave() {
		(this.$refs.form as Form).validate((valid: boolean) => {
			if (valid) {
				saveSettleSetting(this.form).then(() => {
          this.$message.success('정산금액이 수정되었습니다.');
        });
			}
		});
	}
}
</script>
