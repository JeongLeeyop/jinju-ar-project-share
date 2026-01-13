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
                <el-form-item prop="actualName">
                  <div class="btn-wr name">
                    <el-input v-model="userInfo.actualName" placeholder="성함"/>
                  </div>
                </el-form-item>
                <el-form-item prop="email">
                  <div class="btn-wr email">
                    <el-input v-model="userInfo.email" @input="removeSpaceFormData" placeholder="이메일"/>
                  </div>
                </el-form-item>
                <el-form-item prop="concatNumber">
                  <div class="btn-wr phone">
                    <el-input v-model="userInfo.concatNumber" @input="formatPhoneNumber" placeholder="핸드폰 번호 (예: 010-1234-5678)" maxlength="13"/>
                  </div>
                </el-form-item>
                <el-form-item prop="currentPassword">
                  <div class="btn-wr password">
                    <el-input v-model="userInfo.currentPassword" type="password" placeholder="현재 비밀번호 (변경 시에만 입력)"/>
                  </div>
                </el-form-item>
                <el-form-item prop="newPassword">
                  <div class="btn-wr password">
                    <el-input v-model="userInfo.newPassword" type="password" placeholder="새 비밀번호 (변경 시에만 입력)"/>
                  </div>
                </el-form-item>
                <el-form-item prop="newPasswordCheck">
                  <div class="btn-wr password">
                    <el-input v-model="userInfo.newPasswordCheck" type="password" placeholder="새 비밀번호 확인 (변경 시에만 입력)"/>
                  </div>
                </el-form-item>
                <el-form-item prop="thumbnail">
                   <div class="txt-box icon-section">
                    <div class="ttl">Icon 등록 (200 X 200)</div>
                    <div class="icon-upload-wrapper">
                      <div v-if="iconPreviewUrl" class="icon-preview">
                        <img :src="iconPreviewUrl" alt="아이콘 미리보기">
                        <button type="button" class="remove-icon-btn" @click="handleRemoveIcon">
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                          </svg>
                        </button>
                      </div>
                      <div v-else class="icon-placeholder">
                        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M12 12C14.21 12 16 10.21 16 8C16 5.79 14.21 4 12 4C9.79 4 8 5.79 8 8C8 10.21 9.79 12 12 12ZM12 14C9.33 14 4 15.34 4 18V20H20V18C20 15.34 14.67 14 12 14Z" fill="#B2B2B2"/>
                        </svg>
                      </div>
                      <input 
                        type="file" 
                        ref="iconInput" 
                        accept="image/*" 
                        style="display: none;" 
                        @change="handleIconFileChange"
                      >
                      <el-button type="primary" size="small" class="btn" @click="triggerIconUpload">아이콘 선택</el-button>
                    </div>
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
import axios from 'axios';
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
            // 기존 아이콘이 있으면 미리보기 설정
            if (this.userInfo.iconFileUid) {
              this.iconPreviewUrl = `${this.apiUrl}/attached-file/${this.userInfo.iconFileUid}`;
            }
            // 핸드폰 번호가 없으면 빈 문자열로 초기화
            if (!this.userInfo.concatNumber) {
              this.userInfo.concatNumber = '';
            }
        });
    }
}

  private userInfo: any = {
    uid: '',
    userId: '',
    userPassword: '',
    actualName: '',
    email: '',
    concatNumber: '',
    privacy1: '',
    privacy2: '',
    privacy3: '',
    iconFileUid: '',
    currentPassword: '',
    newPassword: '',
    newPasswordCheck: '',
  }

  private iconImageLimit = 1;

  private apiUrl = process.env.VUE_APP_BASE_API;

  private iconFile: File | null = null;

  private iconPreviewUrl: string = '';

  private iconFileToUpload: any = {};

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

  private triggerIconUpload() {
    const input = this.$refs.iconInput as HTMLInputElement;
    if (input) {
      input.click();
    }
  }

  private handleIconFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    
    if (!file) return;
    
    // 파일 크기 체크 (10MB)
    if (file.size > 10 * 1024 * 1024) {
      this.$message.warning('파일 용량은 10MB를 초과할 수 없습니다.');
      return;
    }
    
    // 이미지 파일 체크
    if (!file.type.startsWith('image/')) {
      this.$message.warning('이미지 파일만 업로드 가능합니다.');
      return;
    }
    
    this.iconFile = file;
    
    // 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      this.iconPreviewUrl = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  private handleRemoveIcon() {
    this.iconFile = null;
    this.iconPreviewUrl = '';
    this.userInfo.iconFileUid = '';
    const input = this.$refs.iconInput as HTMLInputElement;
    if (input) {
      input.value = '';
    }
  }

  private handleRemoveFile(file: any) {
    this.iconFile = null;
    this.iconFileToUpload = {};
    this.iconPreviewUrl = '';
    this.userInfo.iconFileUid = '';
  }

  private handleUploadSuccess(res: any) {
      this.iconFileToUpload = {
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      };
      this.userInfo.iconFileUid = res.uid;
  }

  private async uploadIcon(): Promise<string> {
    if (!this.iconFile) {
      return '';
    }
    
    const formData = new FormData();
    formData.append('file', this.iconFile);
    
    try {
      const response = await axios.post('/api/user/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      return response.data.uid || '';
    } catch (error) {
      console.error('Icon upload failed:', error);
      throw error;
    }
  }

  private validateUserId = (rule:any, value:any, callback:any) => {
    if (value === '') {
      callback(new Error('아이디를 입력하세요.'));
    } else {
      const regEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!regEmail.test(value)) {
        callback(new Error('이메일형식으로 입력하세요.'));
      } else {
        callback();
      }
    }
  };

  private validatePhoneNumber = (rule:any, value:any, callback:any) => {
    if (!value || value === '') {
      // 핸드폰 번호는 선택사항으로 변경
      callback();
    } else {
      const regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
      if (!regPhone.test(value.replace(/-/g, ''))) {
        callback(new Error('올바른 핸드폰 번호를 입력하세요. (예: 010-1234-5678)'));
      } else {
        callback();
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

  private validateNewPassword = (rule:any, value:any, callback:any) => {
    const currentPassword = this.userInfo.currentPassword;
    const newPassword = this.userInfo.newPassword;
    const newPasswordCheck = this.userInfo.newPasswordCheck;

    // 비밀번호 변경을 원하는 경우
    if (currentPassword || newPassword || newPasswordCheck) {
      // 현재 비밀번호 입력 확인
      if (!currentPassword) {
        callback(new Error('현재 비밀번호를 입력해주세요.'));
        return;
      }
      
      // 새 비밀번호 입력 확인
      if (!newPassword) {
        callback(new Error('새 비밀번호를 입력해주세요.'));
        return;
      }
      
      // 새 비밀번호 길이 확인
      if (newPassword.length < 8 || newPassword.length > 20) {
        callback(new Error('새 비밀번호는 8~20자로 입력해주세요.'));
        return;
      }
    }
    
    callback();
  };

  private validateNewPasswordCheck = (rule:any, value:any, callback:any) => {
    const newPassword = this.userInfo.newPassword;
    const newPasswordCheck = this.userInfo.newPasswordCheck;

    // 비밀번호 변경을 원하는 경우
    if (newPassword) {
      if (!newPasswordCheck) {
        callback(new Error('새 비밀번호 확인을 입력해주세요.'));
        return;
      }
      
      if (newPassword !== newPasswordCheck) {
        callback(new Error('새 비밀번호가 일치하지 않습니다.'));
        return;
      }
    }
    
    callback();
  };

  private joinRules = {
    email: [
      { required: true, message: '이메일을 입력해주세요.', trigger: ['change', 'blur'] },
      { validator: this.validateUserId, trigger: 'blur' },
    ],
    actualName: [
      { required: true, message: '성명을 입력하세요.', trigger: ['change', 'blur'] },
    ],
    concatNumber: [
      { validator: this.validatePhoneNumber, trigger: ['change', 'blur'] },
    ],
    currentPassword: [
      { validator: this.validateNewPassword, trigger: ['change', 'blur'] },
    ],
    newPassword: [
      { validator: this.validateNewPassword, trigger: ['change', 'blur'] },
    ],
    newPasswordCheck: [
      { validator: this.validateNewPasswordCheck, trigger: ['change', 'blur'] },
    ],
  }

  private async handleUserUpdate() {
    /* TODO 회원정보 수정 로직 */
    (this.$refs.userInfo as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true;
        
        try {
          // 아이콘이 있으면 먼저 업로드
          if (this.iconFile) {
            const iconUid = await this.uploadIcon();
            this.userInfo.iconFileUid = iconUid;
          }
          
          // 회원정보 수정 데이터 준비
          const updateData: any = {
            actualName: this.userInfo.actualName,
            email: this.userInfo.email,
            concatNumber: this.userInfo.concatNumber,
            iconFileUid: this.userInfo.iconFileUid,
          };
          
          // 비밀번호 변경이 있는 경우에만 포함
          if (this.userInfo.currentPassword) {
            updateData.currentPassword = this.userInfo.currentPassword;
            updateData.newPassword = this.userInfo.newPassword;
            updateData.newPasswordCheck = this.userInfo.newPasswordCheck;
          }
          
          // 회원정보 수정
          await updateUser(this.userInfo.uid, updateData);
          this.$message.success('회원정보가 수정되었습니다.');
          
          // 비밀번호가 변경된 경우 재로그인 안내
          if (this.userInfo.currentPassword) {
            this.$message.info('비밀번호가 변경되었습니다. 새로운 비밀번호로 다시 로그인해주세요.');
            // 로그아웃 처리는 별도로 진행
          }
          
          // 모든 password 필드 초기화
          this.userInfo.currentPassword = '';
          this.userInfo.newPassword = '';
          this.userInfo.newPasswordCheck = '';
          
          this.userModalVisible = false;
          this.$router.go(0);
        } catch (error: any) {
          const message = error.response?.data?.message || '회원정보 수정에 실패했습니다.';
          this.$message.error(message);
        } finally {
          this.loading = false;
        }
      }
    });
  }

  private removeSpaceFormData() {
    if (this.userInfo.email) {
      this.userInfo.email = this.userInfo.email.replace(/(\s*)/g, '');
    }
  }

  private formatPhoneNumber() {
    // concatNumber가 undefined이거나 null인 경우 빈 문자열로 초기화
    if (!this.userInfo.concatNumber) {
      this.userInfo.concatNumber = '';
      return;
    }
    
    // 숫자만 추출
    let numbers = this.userInfo.concatNumber.replace(/[^\d]/g, '');
    
    // 최대 11자리로 제한
    if (numbers.length > 11) {
      numbers = numbers.slice(0, 11);
    }
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      this.userInfo.concatNumber = numbers;
    } else if (numbers.length <= 7) {
      this.userInfo.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3);
    } else {
      this.userInfo.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7);
    }
  }
}
</script>

<style scoped lang="scss">
::v-deep .el-dialog {
  max-width: 550px;
  border-radius: 12px;
  
  @media (max-width: 768px) {
    width: 100% !important;
    max-width: 100% !important;
    margin: 0 !important;
    // height: 100vh;
    // max-height: 100vh;
    border-radius: 0;
  }
}

.section {
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  padding: 20px;

  .logo {
    text-align: center;
    margin-bottom: 20px;

    img {
      max-width: 100%;
      height: auto;
    }
  }

  .title {
    color: #222;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 24px;
    font-weight: 700;
    line-height: 150%;
    margin-bottom: 20px;
  }

  .button-wrap {
    display: flex;
    flex-direction: column;
    margin-bottom: 16px;
    .el-form-item {
      margin-bottom: 13px;
    }

    .btn-wr {
      width: 100%;

      input {
        width: 100%;
        height: 50px;
        padding: 0 16px;
        border: 1px solid #B2B2B2;
        border-radius: 10px;
        background: #FFF;
        color: #222;
        font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
        font-size: 16px;
        font-weight: 400;
        outline: none;
        box-sizing: border-box;

        &::placeholder {
          color: #B2B2B2;
        }

        &:focus {
          border-color: #073DFF;
        }
      }
    }

    .icon-section {
      width: 100%;
      
      .ttl {
        color: #666;
        font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 10px;
        text-align: center;
      }
      
      .icon-upload-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 12px;
        
        .icon-preview {
          position: relative;
          width: 100px;
          height: 100px;
          border-radius: 50%;
          overflow: hidden;
          border: 2px solid #E0E0E0;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
          
          .remove-icon-btn {
            position: absolute;
            top: 4px;
            right: 4px;
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: rgba(0, 0, 0, 0.7);
            border: none;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s;
            
            svg {
              color: #FFF;
            }
            
            &:hover {
              background: rgba(0, 0, 0, 0.9);
              transform: scale(1.1);
            }
          }
        }
        
        .icon-placeholder {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          background: #F5F5F5;
          border: 2px dashed #B2B2B2;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        .btn {
          height: 40px;
          padding: 0 24px;
          border-radius: 8px;
        }
      }
    }
  }

  .register-btn {
    display: flex;
    justify-content: center;
    margin: 16px 0;

    button {
      width: 222px;
      height: 54px;
      padding: 0 38px;
      justify-content: center;
      align-items: center;
      border-radius: 10px;
      background: #073DFF;
      border: none;
      color: #FFF;
      text-align: center;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 16px;
      font-weight: 600;
      line-height: 150%;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: #0530CC;
      }

      &:active {
        background: #042099;
      }

      @media (max-width: 768px) {
        width: 100%;
      }
    }
  }
}

.login__fs {
  border: none;
  padding: 0;
}
</style>
