<template>
  <el-dialog
    width="550px"
    class="dialog-wrap"
    :visible.sync="localModalVisible"
    :show-close="true"
    >
    <button class="mobile-close-btn" @click="localModalVisible = false">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>
    <div class="section step1 register active" v-if="localActiveStep === 'Register'">
        <el-form ref="joinForm" class="join-form" :rules="joinRules" :model="joinForm" onsubmit="return false;">
            <fieldset class="login__fs">
            <div class="logo">
                <img src="@/assets/images/logo.png" alt="">
            </div>
            <div class="title">진주알 커뮤니티 회원가입을 위한 계정 생성을 진행하세요.
                <br>회원 가입 후, 커뮤니티에 참여가 가능합니다.
            </div>
            <div class="button-wrap">
                <el-form-item prop="email">
                  <div class="btn-wr email">
                    <el-input @input="handleEmailInput" v-model="joinForm.email" placeholder="이메일"/>
                    <span v-if="joinDuplicateCheckStatus.email === 'checking'" class="status-text checking">...</span>
                    <span v-if="joinDuplicateCheckStatus.email === 'duplicate'" class="status-text error">이미 사용중인 이메일입니다</span>
                    <span v-if="joinDuplicateCheckStatus.email === 'available'" class="status-text success">사용 가능</span>
                  </div>
                </el-form-item>
                <el-form-item prop="actualName">
                  <div class="btn-wr name">
                    <el-input v-model="joinForm.actualName" @input="handleNameInput" placeholder="성함"/>
                    <span v-if="joinDuplicateCheckStatus.name === 'checking'" class="status-text checking">...</span>
                    <span v-if="joinDuplicateCheckStatus.name === 'duplicate'" class="status-text error">이미 등록된 정보입니다</span>
                    <span v-if="joinDuplicateCheckStatus.name === 'available'" class="status-text success">사용 가능</span>
                  </div>
                </el-form-item>
                <el-form-item prop="concatNumber">
                  <div class="btn-wr phone">
                    <el-input v-model="joinForm.concatNumber" @input="formatPhoneNumber" placeholder="핸드폰 번호 (예: 010-1234-5678)" maxlength="13"/>
                    <span v-if="joinDuplicateCheckStatus.phone === 'checking'" class="status-text checking">...</span>
                    <span v-if="joinDuplicateCheckStatus.phone === 'duplicate'" class="status-text error">이미 등록된 정보입니다</span>
                    <span v-if="joinDuplicateCheckStatus.phone === 'available'" class="status-text success">사용 가능</span>
                  </div>
                </el-form-item>
                <el-form-item prop="userPassword"><div class="btn-wr password"><el-input @input="removeSpaceFormData" v-model="joinForm.userPassword" type="password" placeholder="패스워드"/></div></el-form-item>
                <el-form-item prop="passwordCheck"><div class="btn-wr password"><el-input @input="removeSpaceFormData" v-model="joinForm.passwordCheck" type="password" placeholder="패스워드 확인"/></div></el-form-item>
                <el-form-item prop="iconFileUid">
                  <div class="icon-upload-section">
                    <div class="icon-label">아이콘 등록 (선택사항)</div>
                    <div class="icon-preview-wrapper">
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
                      <el-button type="primary" size="small" @click="triggerIconUpload">아이콘 선택</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item prop="userType">
                <el-radio-group v-model="joinForm.role" style="display: none;">
                    <el-radio-button key="ROLE_USER" label="ROLE_USER">일반사용자</el-radio-button>
                    <el-radio-button key="ROLE_CREATOR" label="ROLE_CREATOR">크리에이터</el-radio-button>
                </el-radio-group>
                </el-form-item>
              </div>
              <div class="register-btn"><el-button @click="handleRegister()">회원가입</el-button></div>
              <div class="legacy-wr">
                <div class="txt">가입하면 <a @click="handleReady()">약관</a> 및 <a @click="handleReady()">개인정보 보호정책</a>에 동의합니다.</div>
                <div v-if="!registerOnly" class="txt2">이미 계정이 있으신가요? <a @click="viewLogin()">로그인</a></div>
            </div>
            </fieldset>
        </el-form>
    </div>
    <div class="section step2 login active" v-if="localActiveStep === 'Login'">
        <el-form ref="loginForm" class="login-form" :rules="loginRules" :model="loginForm" onsubmit="return false;">
            <fieldset class="login__fs">
            <div class="logo">
                <img src="@/assets/images/logo.png" alt="">
            </div>
            <div class="title">진주알 커뮤니티에 로그인 하세요.
            </div>
            <div class="button-wrap">
                <el-form-item prop="username">
                <div class="btn-wr email"><el-input placeholder="이메일 주소" @input="removeSpaceFormData" v-model="loginForm.username"/></div>
                </el-form-item>
                <el-form-item prop="password">
                <div class="btn-wr password"><el-input placeholder="패스워드" @input="removeSpaceFormData" v-model="loginForm.password" type="password"/></div>
                </el-form-item>
            </div>
            <div class="txt2"><a @click="viewFindPassword()">비밀번호를 잊으셨나요?</a></div>
            <div class="legacy-wr">
                <!-- TODO 아이디/비밀번호 찾기 기능 -->
            </div>
            <div class="register-btn"><el-button @click="handleLogin()">로그인</el-button></div>
            <div class="legacy-wr">
                <div class="txt2">아직 계정이 없다면? <a @click="viewRegister()">회원가입을 하세요.</a></div>
            </div>
            </fieldset>
        </el-form>
    </div>
    <div class="section step3 find-password active" v-if="localActiveStep === 'FindPassword'">
        <el-form ref="findPasswordForm" class="find-password-form" :rules="findPasswordRules" :model="findPasswordForm" onsubmit="return false;">
            <fieldset class="login__fs">
            <div class="logo">
                <img src="@/assets/images/logo.png" alt="">
            </div>
            <div class="title">비밀번호 찾기
            </div>
            <div class="subtitle">가입 시 등록한 이메일과 핸드폰 번호를 입력하면<br>임시 비밀번호를 SMS로 보내드립니다.</div>
            <div class="button-wrap">
                <el-form-item prop="email">
                <div class="btn-wr email"><el-input placeholder="이메일 주소" @input="removeSpaceFormData" v-model="findPasswordForm.email"/></div>
                </el-form-item>
                <el-form-item prop="concatNumber">
                <div class="btn-wr phone"><el-input v-model="findPasswordForm.concatNumber" @input="formatFindPasswordPhone" placeholder="핸드폰 번호 (예: 010-1234-5678)" maxlength="13"/></div>
                </el-form-item>
            </div>
            <div class="register-btn"><el-button :loading="loading" @click="handleSendTempPassword()">임시 비밀번호 발급</el-button></div>
            <div class="legacy-wr">
                <div class="txt2">이메일(아이디)을 잊으셨나요? <a @click="viewFindEmail()">이메일 찾기</a></div>
                <div class="txt2">이미 계정이 있으신가요? <a @click="viewLogin()">로그인</a></div>
            </div>
            </fieldset>
        </el-form>
    </div>
    <!-- 이메일(아이디) 찾기 -->
    <div class="section step4 find-email active" v-if="localActiveStep === 'FindEmail'">
        <el-form ref="findEmailForm" class="find-email-form" :rules="findEmailRules" :model="findEmailForm" onsubmit="return false;">
            <fieldset class="login__fs">
            <div class="logo">
                <img src="@/assets/images/logo.png" alt="">
            </div>
            <div class="title">이메일(아이디) 찾기
            </div>
            <div class="subtitle">가입 시 등록한 이름과 핸드폰 번호를 입력해주세요.</div>
            <div class="button-wrap">
                <el-form-item prop="actualName">
                <div class="btn-wr name"><el-input placeholder="이름" v-model="findEmailForm.actualName"/></div>
                </el-form-item>
                <el-form-item prop="concatNumber">
                <div class="btn-wr phone"><el-input v-model="findEmailForm.concatNumber" @input="formatFindEmailPhone" placeholder="핸드폰 번호 (예: 010-1234-5678)" maxlength="13"/></div>
                </el-form-item>
            </div>
            <div class="register-btn"><el-button :loading="loading" @click="handleFindEmail()">이메일 찾기</el-button></div>
            <div class="legacy-wr">
                <div class="txt2">비밀번호를 잊으셨나요? <a @click="viewFindPassword()">비밀번호 찾기</a></div>
                <div class="txt2">이미 계정이 있으신가요? <a @click="viewLogin()">로그인</a></div>
            </div>
            </fieldset>
        </el-form>
    </div>
    <!-- 이메일 찾기 결과 -->
    <div class="section step5 find-email-result active" v-if="localActiveStep === 'FindEmailResult'">
        <fieldset class="login__fs">
        <div class="logo">
            <img src="@/assets/images/logo.png" alt="">
        </div>
        <div class="title">이메일(아이디) 찾기 결과
        </div>
        <div class="email-result-box">
            <div class="result-label">가입된 이메일(아이디)</div>
            <div class="result-email">{{ foundEmail }}</div>
        </div>
        <div class="register-btn"><el-button @click="viewLogin()">로그인하기</el-button></div>
        <div class="legacy-wr">
            <div class="txt2">비밀번호를 잊으셨나요? <a @click="viewFindPassword()">비밀번호 찾기</a></div>
        </div>
        </fieldset>
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
  addUser,
  getUserInfo,
  requestTempPassword,
  findEmail,
  checkNameAndPhoneDuplicate,
} from '@/api/user';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { Form } from 'element-ui';
import { ElForm } from 'element-ui/types/form';

@Component({
  name: 'UserModal',
})
export default class extends Vue {
mounted() {
    this.localModalVisible = this.userModalVisible;
    this.localActiveStep = this.activeStep;
    // this.getUserInfo();
}

@Prop({ required: true, default: false }) private userModalVisible!: boolean;

@Prop({ required: false, default: false }) private registerOnly!: boolean;

@Prop({ required: false, default: 'Login' }) private activeStep!: string;

@Watch('userModalVisible')
  private onUserModalVisibleChange(val: boolean) {
    this.localModalVisible = val;
    // 모달이 열릴 때 로그인 폼 초기화 및 로그인 화면으로 이동
    if (val === true) {
      this.localActiveStep = 'Login';
      this.loginForm = {
        username: '',
        password: '',
      };
    }
}

@Watch('localModalVisible')
  private onLocalModalVisibleChange(val: boolean) {
    if (val === false) {
      this.$emit('close');
    }
}

@Watch('activeStep')
  private onActiveStepChange(val: string) {
    this.localActiveStep = val;
}

get isLogin() {
    return UserModule.isLogin;
}

private localModalVisible = false;

private localActiveStep = 'Login';

private loading: boolean = false;

private handleReady() {
    alert('준비중 입니다.');
}

/*
private getUserInfo() {
    if (UserModule.isLogin) {
        getUserInfo().then((res) => {
            this.userInfo = res.data;
        });
    }
}
 */

private loginForm = {
    username: '',
    password: '',
  };

  private joinForm: any = {
    userId: '',
    userPassword: '',
    passwordCheck: '',
    actualName: '',
    concatNumber: '',
    role: 'ROLE_USER',
    privacy1: '',
    privacy2: '',
    privacy3: '',
    iconFileUid: '',
  }

  private joinDuplicateCheckStatus = {
    email: '', // 'checking', 'duplicate', 'available'
    name: '', // 'checking', 'duplicate', 'available'
    phone: '', // 'checking', 'duplicate', 'available'
  };

  private joinCheckDuplicateTimer: number | null = null;
  private joinCheckEmailTimer: number | null = null;

  private iconPreviewUrl: string = '';

  private iconFile: File | null = null;

  private findPasswordForm = {
    email: '',
    concatNumber: '',
  }

  private findEmailForm = {
    actualName: '',
    concatNumber: '',
  }

  private foundEmail: string = '';

  private loginRules = {
    username: [{ required: true, message: '아이디를 입력하세요.', trigger: 'blur' }],
    password: [{ required: true, message: '비밀번호를 입력하세요.', trigger: 'blur' }],
  }

  private findPasswordRules = {
    email: [
      { required: true, message: '이메일을 입력하세요.', trigger: 'blur' },
      { type: 'email', message: '올바른 이메일 형식이 아닙니다.', trigger: ['blur', 'change'] },
    ],
    concatNumber: [
      { required: true, message: '핸드폰 번호를 입력하세요.', trigger: 'blur' },
    ],
  }

  private findEmailRules = {
    actualName: [
      { required: true, message: '이름을 입력하세요.', trigger: 'blur' },
    ],
    concatNumber: [
      { required: true, message: '핸드폰 번호를 입력하세요.', trigger: 'blur' },
    ],
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

  private validatePasswordCheck = (rule:any, value:any, callback:any) => {
    if (value !== (this.$refs.joinForm as any).model.userPassword) {
      callback(new Error('비밀번호가 일치하지 않습니다.'));
    } else {
      callback();
    }
  };

  private validatePhoneNumber = (rule:any, value:any, callback:any) => {
    if (value === '') {
      callback(new Error('핸드폰 번호를 입력하세요.'));
    } else {
      const regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
      if (!regPhone.test(value.replace(/-/g, ''))) {
        callback(new Error('올바른 핸드폰 번호를 입력하세요. (예: 010-1234-5678)'));
      } else {
        callback();
      }
    }
  };

  private joinRules = {
    email: [
      { required: true, message: '아이디를 입력해주세요.', trigger: ['change', 'blur'] },
      { validator: this.validateUserId, trigger: 'blur' },
    ],
    userPassword: [
      { required: true, message: '패스워드를 입력하세요.', trigger: ['change', 'blur'] },
      {
        min: 8,
        max: 20,
        message: '패스워드는 최소 8자, 최대 20자입니다.',
        trigger: ['change', 'blur'],
      },
    ],
    passwordCheck: [
      { required: true, message: '패스워드를 확인해주세요.', trigger: ['change', 'blur'] },
      { validator: this.validatePasswordCheck, trigger: ['change', 'blur'] },
    ],
    actualName: [
      { required: true, message: '성명을 입력하세요.', trigger: ['change', 'blur'] },
    ],
    concatNumber: [
      { required: true, message: '핸드폰 번호를 입력하세요.', trigger: ['change', 'blur'] },
      { validator: this.validatePhoneNumber, trigger: ['change', 'blur'] },
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

  private handleLogin() {
    /* TODO 로그인 로직 */
    (this.$refs.loginForm as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        UserModule.Login(this.loginForm).then(async () => {
          // 로그인 성공 시 즉시 페이지 새로고침 (Vue 반응성 업데이트 전에)
          // 사용자 정보와 채널 정보는 새로고침 후 자동으로 로드됨
          this.$message.info('로그인에 성공하셨습니다.');
          window.location.reload();
        }).catch((err: any) => {
          this.loading = false;
          this.$message.warning('로그인에 실패하였습니다. 아이디나 패스워드를 확인하세요.');
        });
      }
    });
  }

  // 이메일 입력 핸들러
  private handleEmailInput() {
    // 공백 제거
    this.joinForm.email = this.joinForm.email.replace(/\s/g, '');
    this.checkJoinDuplicateEmail();
  }

  // 이메일 중복 체크
  private async checkJoinDuplicateEmail() {
    // debounce 초기화
    if (this.joinCheckEmailTimer) {
      clearTimeout(this.joinCheckEmailTimer);
    }

    // 이메일이 입력되지 않은 경우
    if (!this.joinForm.email) {
      this.joinDuplicateCheckStatus.email = '';
      return;
    }

    // 이메일 형식 검증
    const regEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!regEmail.test(this.joinForm.email)) {
      this.joinDuplicateCheckStatus.email = '';
      return;
    }

    this.joinDuplicateCheckStatus.email = 'checking';

    this.joinCheckEmailTimer = window.setTimeout(async () => {
      try {
        const response = await userIdCheck(this.joinForm.email);
        // userIdCheck는 사용 가능하면 true, 이미 사용중이면 false 반환
        if (response.data === true) {
          this.joinDuplicateCheckStatus.email = 'available';
        } else {
          this.joinDuplicateCheckStatus.email = 'duplicate';
        }
      } catch (error: any) {
        this.joinDuplicateCheckStatus.email = '';
        console.error('이메일 중복 확인 오류:', error);
      }
    }, 500);
  }

  private async checkJoinDuplicateNameAndPhone() {
    console.log('checkJoinDuplicateNameAndPhone called', {
      name: this.joinForm.actualName,
      phone: this.joinForm.concatNumber
    });
    
    // debounce 초기화
    if (this.joinCheckDuplicateTimer) {
      clearTimeout(this.joinCheckDuplicateTimer);
    }

    // 이름과 전화번호 모두 입력된 경우에만 체크
    if (!this.joinForm.actualName || !this.joinForm.concatNumber) {
      this.joinDuplicateCheckStatus = { email: this.joinDuplicateCheckStatus.email, name: '', phone: '' };
      console.log('Skip check: missing fields');
      return;
    }

    // 전화번호 형식 검증
    const phoneWithoutDash = this.joinForm.concatNumber.replace(/-/g, '');
    const regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
    console.log('Phone validation:', {
      original: this.joinForm.concatNumber,
      withoutDash: phoneWithoutDash,
      testResult: regPhone.test(phoneWithoutDash),
      phoneLength: phoneWithoutDash.length
    });
    
    if (!regPhone.test(phoneWithoutDash)) {
      this.joinDuplicateCheckStatus = { email: this.joinDuplicateCheckStatus.email, name: this.joinDuplicateCheckStatus.name, phone: '' };
      console.log('Skip check: invalid phone format');
      return;
    }

    this.joinDuplicateCheckStatus = { email: this.joinDuplicateCheckStatus.email, name: 'checking', phone: 'checking' };
    console.log('Starting duplicate check... will check in 500ms');

    this.joinCheckDuplicateTimer = window.setTimeout(async () => {
      try {
        console.log('Making API call...');
        const response = await checkNameAndPhoneDuplicate({
          actualName: this.joinForm.actualName,
          concatNumber: this.joinForm.concatNumber,
        });
        
        console.log('=== API 응답 전체 ===');
        console.log('response.data:', response.data);
        console.log('response.data.duplicate:', response.data.duplicate);
        console.log('==================');
        
        // Java에서 isDuplicate 필드는 JSON으로 직렬화 시 "duplicate"로 변환됨
        const isDuplicate = response.data.duplicate;
        if (isDuplicate === true) {
          console.log('→ 중복 있음! duplicate 설정');
          // Vue 반응성을 위해 객체 전체를 새로 할당
          this.joinDuplicateCheckStatus = {
            email: this.joinDuplicateCheckStatus.email,
            name: 'duplicate',
            phone: 'duplicate'
          };
        } else {
          console.log('→ 중복 없음! available 설정');
          this.joinDuplicateCheckStatus = {
            email: this.joinDuplicateCheckStatus.email,
            name: 'available',
            phone: 'available'
          };
        }
        
        console.log('최종 status:', this.joinDuplicateCheckStatus);
      } catch (error: any) {
        this.joinDuplicateCheckStatus = { email: this.joinDuplicateCheckStatus.email, name: '', phone: '' };
        console.error('중복 확인 오류:', error);
      }
    }, 500); // 500ms debounce
  }

  private async handleRegister() {
    /* TODO 회원가입 로직 */
    (this.$refs.joinForm as ElForm).validate(async (valid: boolean) => {
      if (valid) {
        // 이메일 중복 체크
        if (this.joinDuplicateCheckStatus.email === 'duplicate') {
          this.$message.warning('이미 사용중인 이메일입니다.');
          return;
        }
        
        if (this.joinDuplicateCheckStatus.email === 'checking') {
          this.$message.warning('이메일 중복 확인 중입니다. 잠시만 기다려주세요.');
          return;
        }
        
        // 이름+전화번호 중복 체크
        if (this.joinDuplicateCheckStatus.name === 'duplicate' || this.joinDuplicateCheckStatus.phone === 'duplicate') {
          this.$message.warning('이미 등록된 이름과 전화번호입니다.');
          return;
        }
        
        if (this.joinDuplicateCheckStatus.name === 'checking' || this.joinDuplicateCheckStatus.phone === 'checking') {
          this.$message.warning('중복 확인 중입니다. 잠시만 기다려주세요.');
          return;
        }

        this.loading = true;
        
        try {
          // 아이콘이 있으면 먼저 업로드
          if (this.iconFile) {
            const iconUid = await this.uploadIcon();
            this.joinForm.iconFileUid = iconUid;
          }
          
          // 회원가입 처리
          await addUser(this.joinForm);
          this.$message.success('회원가입이 완료되었습니다.');
          
          if (this.$route.name === 'Home') {
            this.$router.go(0);
          } else {
            this.$router.push({ name: 'Home' });
          }
        } catch (error) {
          this.$message.error('회원가입 요청에 실패했습니다.');
        } finally {
          this.loading = false;
        }
      }
    });
  }

  private removeSpaceFormData() {
    this.loginForm.username = this.loginForm.username.replace(/(\s*)/g, '');
    this.loginForm.password = this.loginForm.password.replace(/(\s*)/g, '');
    this.joinForm.userId = this.joinForm.userId.replace(/(\s*)/g, '');
    this.joinForm.userPassword = this.joinForm.userPassword.replace(/(\s*)/g, '');
    this.joinForm.passwordCheck = this.joinForm.passwordCheck.replace(/(\s*)/g, '');
  }

  private handleNameInput() {
    console.log('handleNameInput called');
    // 이름 입력 시 중복 체크 트리거
    this.$nextTick(() => {
      this.checkJoinDuplicateNameAndPhone();
    });
  }

  private formatPhoneNumber() {
    console.log('formatPhoneNumber called');
    // 숫자만 추출
    let numbers = this.joinForm.concatNumber.replace(/[^\d]/g, '');
    
    // 최대 11자리로 제한
    if (numbers.length > 11) {
      numbers = numbers.slice(0, 11);
    }
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      this.joinForm.concatNumber = numbers;
    } else if (numbers.length <= 7) {
      this.joinForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3);
    } else {
      this.joinForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7);
    }
    
    // 중복 체크 트리거
    this.$nextTick(() => {
      this.checkJoinDuplicateNameAndPhone();
    });
  }

  private formatFindPasswordPhone() {
    // 숫자만 추출
    let numbers = this.findPasswordForm.concatNumber.replace(/[^\d]/g, '');
    
    // 최대 11자리로 제한
    if (numbers.length > 11) {
      numbers = numbers.slice(0, 11);
    }
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      this.findPasswordForm.concatNumber = numbers;
    } else if (numbers.length <= 7) {
      this.findPasswordForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3);
    } else {
      this.findPasswordForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7);
    }
  }

  private formatFindEmailPhone() {
    // 숫자만 추출
    let numbers = this.findEmailForm.concatNumber.replace(/[^\d]/g, '');
    
    // 최대 11자리로 제한
    if (numbers.length > 11) {
      numbers = numbers.slice(0, 11);
    }
    
    // 하이픈 추가
    if (numbers.length <= 3) {
      this.findEmailForm.concatNumber = numbers;
    } else if (numbers.length <= 7) {
      this.findEmailForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3);
    } else {
      this.findEmailForm.concatNumber = numbers.slice(0, 3) + '-' + numbers.slice(3, 7) + '-' + numbers.slice(7);
    }
  }

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
    this.joinForm.iconFileUid = '';
    const input = this.$refs.iconInput as HTMLInputElement;
    if (input) {
      input.value = '';
    }
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

  private viewLogin() {
    // 데이터 초기화
    this.loginForm.username = '';
    this.loginForm.password = '';
    this.findPasswordForm.email = '';
    this.findPasswordForm.concatNumber = '';
    this.findEmailForm.actualName = '';
    this.findEmailForm.concatNumber = '';
    this.foundEmail = '';
    this.localActiveStep = 'Login';
    // 로그인 폼 초기화
    this.loginForm = {
      username: '',
      password: '',
    };
  }

  private viewRegister() {
    // 데이터 초기화
    this.loginForm.username = '';
    this.loginForm.password = '';
    this.findPasswordForm.email = '';
    this.findPasswordForm.concatNumber = '';
    this.findEmailForm.actualName = '';
    this.findEmailForm.concatNumber = '';
    this.foundEmail = '';
    this.localActiveStep = 'Register';
  }

  private viewFindPassword() {
    // 데이터 초기화
    this.findPasswordForm.email = '';
    this.findPasswordForm.concatNumber = '';
    this.foundEmail = '';
    this.localActiveStep = 'FindPassword';
  }

  private viewFindEmail() {
    // 데이터 초기화
    this.findEmailForm.actualName = '';
    this.findEmailForm.concatNumber = '';
    this.foundEmail = '';
    this.localActiveStep = 'FindEmail';
  }

  @Watch('joinForm.actualName')
  @Watch('joinForm.concatNumber')
  private onJoinNameOrPhoneChange() {
    console.log('onJoinNameOrPhoneChange called via Watch');
    // 이름이나 전화번호가 변경되면 중복체크 실행
    this.checkJoinDuplicateNameAndPhone();
  }

  private async handleSendTempPassword() {
    (this.$refs.findPasswordForm as Form).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true;
        try {
          const response = await requestTempPassword({
            email: this.findPasswordForm.email,
            concatNumber: this.findPasswordForm.concatNumber,
          });
          
          if (response.data.success) {
            this.$message.success(response.data.message);
            this.findPasswordForm.email = '';
            this.findPasswordForm.concatNumber = '';
            this.viewLogin();
          } else {
            this.$message.error(response.data.message);
          }
        } catch (error: any) {
          const message = error.response?.data?.message || '임시 비밀번호 발급에 실패했습니다.';
          this.$message.error(message);
        } finally {
          this.loading = false;
        }
      }
    });
  }

  private async handleFindEmail() {
    (this.$refs.findEmailForm as Form).validate(async (valid: boolean) => {
      if (valid) {
        this.loading = true;
        try {
          const response = await findEmail({
            actualName: this.findEmailForm.actualName,
            concatNumber: this.findEmailForm.concatNumber,
          });
          
          if (response.data.found) {
            // 찾은 이메일을 메시지로 표시
            this.$message.success(`${response.data.message}\n이메일: ${response.data.maskedEmail}`);
            this.foundEmail = response.data.maskedEmail;
          } else {
            this.foundEmail = '';
            this.$message.error(response.data.message);
          }
        } catch (error: any) {
          const message = error.response?.data?.message || '이메일 찾기에 실패했습니다.';
          this.$message.error(message);
        } finally {
          this.loading = false;
        }
      }
    });
  }
}
</script>

<style scoped lang="scss">
.mobile-close-btn {
  display: none;
  
  @media (max-width: 768px) {
    display: flex;
    align-items: center;
    justify-content: center;
    position: fixed;
    top: 20px;
    right: 20px;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.9);
    border: none;
    border-radius: 50%;
    cursor: pointer;
    z-index: 2001;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 1);
      transform: scale(1.1);
    }
    
    &:active {
      transform: scale(0.95);
    }
    
    svg {
      color: #222;
    }
  }
}

::v-deep .el-dialog {
  @media (max-width: 768px) {
    width: 100% !important;
    max-width: 100% !important;
    margin: 0 !important;
    height: 100vh;
    max-height: 100vh;
    border-radius: 0;
    display: flex;
    flex-direction: column;
    position: fixed !important;
    top: 0 !important;
    left: 0 !important;
    z-index: 2000 !important;

    .el-dialog__header {
      padding: 0;
      display: none;
    }

    .el-dialog__body {
      flex: 1;
      padding: 80px 20px 40px 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow-y: auto;
    }

    .el-dialog__close {
      display: none;
    }
  }
}

::v-deep .dialog-wrap {
  @media (max-width: 768px) {
    width: 100% !important;
    max-width: 100% !important;
    margin: 0 !important;

    .el-dialog {
      width: 100% !important;
      max-width: 100% !important;
      margin: 0 !important;
      border-radius: 0;
    }
  }
}

.section {
  width: 100%;
  max-width: 320px;
  margin: 0 auto;

  @media (max-width: 768px) {
    max-width: 420px;
    width: 100%;
    padding: 0;
  }

  .logo {
    text-align: center;
    margin-bottom: 32px;

    img {
      width: 120px;
      height: auto;
    }

    @media (max-width: 768px) {
      margin-bottom: 40px;
    }
  }

  .title {
    color: #222;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 26px;
    font-weight: 700;
    line-height: 150%;
    margin-bottom: 6px;

    @media (max-width: 768px) {
      font-size: 22px;
    }
  }

  .subtitle {
    color: #888;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 600;
    line-height: 150%;
    margin-bottom: 32px;

    @media (max-width: 768px) {
      font-size: 14px;
    }
  }

  .button-wrap {
    display: flex;
    flex-direction: column;
    gap: 0px;
    margin-bottom: 16px;

    .btn-wr {
      width: 100%;
      position: relative;

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

      .status-text {
        position: absolute;
        right: 16px;
        top: 50%;
        transform: translateY(-50%);
        font-size: 12px;
        font-weight: 500;
        pointer-events: none;

        &.checking {
          color: #666;
        }

        &.error {
          color: #F56C6C;
        }

        &.success {
          color: #67C23A;
        }
      }
    }

    .icon-upload-section {
      width: 100%;
      
      .icon-label {
        color: #666;
        font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 12px;
      }
      
      .icon-preview-wrapper {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .icon-preview {
          position: relative;
          width: 80px;
          height: 80px;
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
            width: 24px;
            height: 24px;
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
          width: 80px;
          height: 80px;
          border-radius: 50%;
          background: #F5F5F5;
          border: 2px dashed #B2B2B2;
          display: flex;
          align-items: center;
          justify-content: center;
        }
        
        .el-button {
          height: 40px;
          padding: 0 20px;
          border-radius: 8px;
        }
      }
    }
  }

  .txt2 {
    color: #111;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 400;
    line-height: 150%;
    margin: 8px 0;

    a {
      color: #073DFF;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .txt {
    color: #111;
    text-align: center;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 400;
    line-height: 150%;

    a {
      color: #073DFF;
      cursor: pointer;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
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

  .legacy-wr {
    display: flex;
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }

  .question-wrap {
    margin-bottom: 16px;

    .question {
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
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
  }
}

.login__fs {
  border: none;
  padding: 0;
}

.form_wr {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.thumbnail_wr {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;

  .thumbnail {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    background: #F0F0F0;
  }
}

.title_wr {
  text-align: center;
  margin-bottom: 24px;

  .profile {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 8px;
  }

  .ttl {
    color: #666;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 400;
    line-height: 150%;
  }
}
</style>
