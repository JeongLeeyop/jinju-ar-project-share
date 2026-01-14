<template>
	<div class="useradd-wrap">
		<div class="useradd-title">
			<div>
				<p class="tl">매니저 {{ $route.params.uid ? '수정' : '추가' }}</p>
			</div>
			<div class="setting__btn__box">
				<button @click="handleCancel()" class="cancel">취소</button>
				<button class="save" type="submit" @click="handleSave()">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false">
			<div class="useradd-section">
				<div class="useradd-content1">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
						<div class="shop-selectbox-wr">
							<label for="managerShopName">매장명</label>
              <el-form-item prop="shopIdx">
                <el-select v-model="form.shopIdx">
                  <el-option
                    v-for="(shop, index) in shopList"
                    :key="index"
                    :label="shop.name"
                    :value="shop.idx"
                  />
                </el-select>
              </el-form-item>
						</div>
            <div class="shop-input-wr">
							<label for="userId">아이디</label>
              <p v-if="$route.params.uid">{{ form.userId }}</p>
              <el-form-item v-else prop="userId">
                <el-input type="text" id="userId" v-model="form.userId" />
              </el-form-item>
						</div>
            <slot v-if="passwordVisible">
              <div class="shop-input-wr">
                <label for="userPassword">패스워드</label>
                <el-form-item prop="userPassword">
                  <el-input type="password" id="userPassword" v-model="form.userPassword" />
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="passwordCheck">패스워드 확인</label>
                <el-form-item prop="passwordCheck">
                  <el-input type="password" id="passwordCheck" v-model="form.passwordCheck" />
                </el-form-item>
              </div>
            </slot>
            <div v-else class="shop-input-wr">
              <label for="userPassword">패스워드</label>
              <el-button size="small" @click="passwordVisible = true">패스워드 변경</el-button>
            </div>
						<div class="shop-input-wr">
							<label for="managerName">매니저명</label>
              <el-form-item prop="actualName">
                <el-input type="text" id="managerName" v-model="form.actualName" />
              </el-form-item>
						</div>
						<div class="shop-input-wr">
							<label for="managerPhone">연락처</label>
              <el-form-item prop="concatNumber">
                <el-input type="text" id="managerPhone" v-model="form.concatNumber" @input="formatConcatNumber()" />
              </el-form-item>
						</div>
						<!--
            <div class="shop-input-wr">
							<div class="shop-date-wr">
								<label for="managerJoinDate">매니저등록일</label>
								<el-form-item>
									<el-date-picker type="date" placeholder="매니저등록일" v-model="form.managerJoinDate"></el-date-picker>
								</el-form-item>
							</div>
						</div>
            -->
					</div>
				</div>
				<div class="useradd-content2">
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang="ts">
import { parseConcatNumber } from '@/utils/filters';
import { Component, Vue } from 'vue-property-decorator';
import { getShopListAll } from '@/api/shop';
import { Form } from 'element-ui';
import {
  addManager,
  getUser,
  updateManager,
  userIdCheck,
} from '@/api/user';

@Component({
})
export default class extends Vue {
  mounted() {
    this.getShopList();
    this.setForm();
  }

  private shopList = [];

  private passwordVisible = false;

  private form = {
    userId: '',
    userPassword: '',
    passwordCheck: '',
    actualName: '',
    concatNumber: '',
    shopIdx: null,
  }

  private validatePasswordCheck = (rule:any, value:any, callback:any) => {
    if (value !== (this.$refs.form as any).model.userPassword) {
      callback(new Error('비밀번호가 일치하지 않습니다.'));
    } else {
      callback();
    }
  };

  private validateConcatNumber = (rule:any, value:any, callback:any) => {
    const regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
    const regPhone2 = /^(0(2|3[1-3]|4[1-4]|5[1-5]|6[1-4]))-(\d{3,4})-(\d{4})$/;
    if (!regPhone.test(value) && !regPhone2.test(value)) {
      callback(new Error('연락처 형식에 맞지 않습니다.'));
    } else {
      callback();
    }
  };

  private validateUserId = (rule:any, value:any, callback:any) => {
    userIdCheck(value).then((res) => {
      if (!res.data) {
        callback(new Error('중복된 아이디입니다.'));
      } else {
        callback();
      }
    });
  };

  /* eslint-disable */
  private rules = {
    userId: [
      { required: true, message: '아이디를 입력하세요.', trigger: ['blur', 'change'] },
      { min: 4, max: 16, message: '아이디는 최소 4자, 최대 16자입니다.', trigger: ['blur', 'change'] },
      { validator: this.validateUserId, trigger: 'blur' },
    ],
    userPassword: [
      { required: true, message: '패스워드를 입력하세요.', trigger: ['blur', 'change'] },
      { min: 8, max: 20, message: '패스워드는 최소 8자, 최대 20자입니다.', trigger: ['blur', 'change'] },
    ],
    passwordCheck: [
      { validator: this.validatePasswordCheck, trigger: ['blur', 'change'] },
    ],
    actualName: [
      { required: true, message: '매니저 성명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    concatNumber: [
      { required: true, message: '연락처를 입력해주세요.', trigger: ['blur', 'change'] },
      { validator: this.validateConcatNumber, trigger: ['blur', 'change'] },
    ],
    shopIdx: [
      { required: true, message: '관리 매장을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
  }
  /* eslint-enable */

  private handleCancel() {
    this.$router.push({ name: 'ShopManager' });
  }

  private formatConcatNumber() {
    this.form.concatNumber = parseConcatNumber(this.form.concatNumber);
  }

  private getShopList() {
    getShopListAll().then((res) => {
      this.shopList = res.data;
    });
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        if (this.$route.params.uid) {
          updateManager(this.$route.params.uid, this.form).then(() => {
            this.$message.success('매니저 정보가 수정되었습니다.');
            this.$router.push({ name: 'ShopManager' });
          });
        } else {
          addManager(this.form).then(() => {
            this.$message.success('매니저가 추가되었습니다.');
            this.$router.push({ name: 'ShopManager' });
          });
        }
      }
    });
  }

  private setForm() {
    if (this.$route.params.uid) {
      getUser(this.$route.params.uid).then((res) => {
        this.form = { ...res.data };
      });
    } else {
      this.passwordVisible = true;
    }
  }
}
</script>
