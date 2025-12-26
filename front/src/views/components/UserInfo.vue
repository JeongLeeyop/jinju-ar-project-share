<template>
  <el-dialog
    width="550px"
    class="dialog-wrap"
    :visible.sync="userModalVisible"
    >
    <div class="section step1 register active">
        <el-form ref="userInfo" class="join-form" :rules="joinRules" :model="userInfo" onsubmit="return false;">
            <fieldset class="login__fs">
            <div class="logo">
                <img src="@/assets/images/logo.png" style="width:300px;" alt="">
            </div>
            <div class="title">회원정보 수정</div>
            <div class="button-wrap">
                <el-form-item prop="actualName"><div class="btn-wr name"><el-input v-model="userInfo.actualName" placeholder="성함"/></div></el-form-item>
                <!-- <el-form-item prop="email"><div class="btn-wr email"><el-input @input="removeSpaceFormData" v-model="userInfo.email" placeholder="이메일"/></div></el-form-item> -->
                <!-- <el-form-item prop="userPassword"><div class="btn-wr password"><el-input @input="removeSpaceFormData" v-model="userInfo.userPassword" type="password" placeholder="패스워드"/></div></el-form-item> -->
                <!-- <el-form-item prop="passwordCheck"><div class="btn-wr password"><el-input @input="removeSpaceFormData" v-model="userInfo.passwordCheck" type="password" placeholder="패스워드 확인"/></div></el-form-item> -->
                <el-form-item prop="thumbnail">
                   <div class="txt-box">
                    <div class="ttl">Icon 등록 (200 X 200)</div>
                      <el-upload
                        class="upload-demo"
                        action='/api/user/upload'
                        ref="iconImageUpload"
                        :before-upload="(file) => handleBeforeUpload(file)"
                        :on-success="(file) => handleUploadSuccess(file)"
                        :on-remove="(file) => handleRemoveFile(file)"
                        :limit="iconImageLimit"
                        :on-exceed="handleExceedIcon"
                        :file-list="iconFile"
                        >
                        <el-button type="primary" class="btn">등록하기</el-button>
                      </el-upload>
                    </div>
                </el-form-item>
            </div>
            <div class="register-btn"><el-button @click="handleUserUpdate()">수정하기</el-button></div>
            <div class="legacy-wr">
            </div>
            </fieldset>
        </el-form>
    </div>
    </el-dialog>
</template>

<script lang="ts">
import { active } from 'sortablejs';
import {
    Component,
    Vue,
    Prop,
    Watch,
} from 'vue-property-decorator';
import {
  userIdCheck,
  updateUser,
  getUserInfo,
} from '@/api/user';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { Form } from 'element-ui';
import { ElForm } from 'element-ui/types/form';

@Component({
  name: 'UserModal',
})
export default class extends Vue {
  async mounted() {
    await this.getUserInfo();
}

@Prop({ required: true, default: false }) private userModalVisible!: boolean;

@Watch('userModalVisible')
  private onStatusChange(val: boolean) {
    if (val === false) this.$emit('close');
}

get isLogin() {
    return UserModule.isLogin;
}

private handleCloseUserModal() {
    this.userModalVisible = false;
}

private loading: boolean = false;

private async getUserInfo() {
    if (UserModule.isLogin) {
        await getUserInfo().then((res) => {
            this.userInfo = res.data;
        });
    }
}

  private userInfo: any = {
    uid: '',
    userId: '',
    userPassword: '',
    // passwordCheck: '',
    actualName: '',
    privacy1: '',
    privacy2: '',
    privacy3: '',
    iconFileUid: '',
  }

  private iconImageLimit = 1;

  private apiUrl = process.env.VUE_APP_BASE_API;

  private iconFile: any = {};

    /* eslint-disable */
    private handleBeforeUpload(file: File) {
    const isLimit = file.size / 1024 / 1024 < 10;
    const isImage = file.type.startsWith('image');
    if (!isLimit) {
      this.$message.warning('파일 용량은 10MB 를 초과 할 수 없습니다.');
      return false;
    }
    {
      if (!isImage) {
        this.$message.warning('이미지 파일만 업로드 가능합니다.');
        return false;
      }
    }
    return true;
  }
  /* eslint-enable */

  private handleRemoveFile(file: any) {
    this.userInfo.iconFileUid = '';
    this.iconFile = {};
  }

  private handleUploadSuccess(res: any) {
      this.iconFile = {
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      };
      this.userInfo.iconFileUid = res.uid;
  }

  private validateUserId = (rule:any, value:any, callback:any) => {
    if (value === '') {
      callback(new Error('아이디를 입력하세요.'));
    } else {
      // const regUserId = /^[a-z0-9_-]{3,19}$/g;
      const regEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      // if (!regUserId.test(value)) {
        // callback(new Error('영문, 숫자, 특수문자(-, _)로 4~20자로 입력하세요.'));
      // } else
      if (!regEmail.test(value)) {
        callback(new Error('이메일형식으로 입력하세요.'));
      } else {
        userIdCheck(value).then((res: any) => {
          if (!res.data) callback(new Error('이미 사용 중인 아이디입니다.'));
          else callback();
        });
      }
    }
  };

  private handleExceedIcon(type: string) {
    this.$message.warning(`아이콘 파일개수는 ${this.iconImageLimit}개를 초과할 수 없습니다.`);
  }

  private validatePasswordCheck = (rule:any, value:any, callback:any) => {
    if (value !== (this.$refs.userInfo as any).model.userPassword) {
      callback(new Error('비밀번호가 일치하지 않습니다.'));
    } else {
      callback();
    }
  };

  private joinRules = {
    email: [
      { required: true, message: '아이디를 입력해주세요.', trigger: ['change', 'blur'] },
      { validator: this.validateUserId, trigger: 'blur' },
    ],
    /* userPassword: [
      { required: true, message: '패스워드를 입력하세요.', trigger: ['change', 'blur'] },
      {
        min: 8,
        max: 20,
        message: '패스워드는 최소 8자, 최대 20자입니다.',
        trigger: ['change', 'blur'],
      },
    ], */
    /* passwordCheck: [
      { required: true, message: '패스워드를 확인해주세요.', trigger: ['change', 'blur'] },
      { validator: this.validatePasswordCheck, trigger: ['change', 'blur'] },
    ], */
    actualName: [
      { required: true, message: '성명을 입력하세요.', trigger: ['change', 'blur'] },
    ],
    privacy1: [
      { required: true, message: '아래 내용에 동의함 체크해주세요.', trigger: ['change', 'blur'] },
    ],
    privacy2: [
      { required: true, message: '개인정보처리방침에 체크해주세요.', trigger: ['change', 'blur'] },
    ],
    privacy3: [
      { required: true, message: '가맹수칙에 체크해주세요.', trigger: ['change', 'blur'] },
    ],
  }

  private handleUserUpdate() {
    /* TODO 회원가입 로직 */
    (this.$refs.userInfo as ElForm).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        updateUser(this.userInfo.uid, this.userInfo).then(() => {
          this.$message.success('회원정보가 수정되었습니다.');
          this.userModalVisible = false;
          this.$router.go(0);
          this.loading = false;
        }).catch(() => {
          this.$message.error('회원정보가 수정에 실패했습니다.');
          this.userModalVisible = false;
          this.loading = false;
        });
      }
    });
  }

  private removeSpaceFormData() {
    this.userInfo.username = this.userInfo.username.replace(/(\s*)/g, '');
    this.userInfo.password = this.userInfo.password.replace(/(\s*)/g, '');
    this.userInfo.userId = this.userInfo.userId.replace(/(\s*)/g, '');
    this.userInfo.userPassword = this.userInfo.userPassword.replace(/(\s*)/g, '');
    this.userInfo.passwordCheck = this.userInfo.passwordCheck.replace(/(\s*)/g, '');
  }
}
</script>

<style>
/* @import url("~@/assets/css/style2.css"); */
</style>
