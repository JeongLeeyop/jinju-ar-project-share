<!-- <template>
  <div class="main login-container">
    <div class="main-wrap">
      <div class="main-logo" @click="handleHome()">
        <img src="@/assets/images/logo.png" alt="">
      </div>

      <div class="login-wrap">
        <div class="login-title">
          <p>LOGIN</p>
        </div>

        <el-form ref="loginForm" class="login-form" :rules="rules" :model="loginForm" onsubmit="return false;">
          <fieldset class="login__fs">
            <div class="login-form__inner">
              <legend>회원로그인</legend>

              <div class="input-box__login">
                <el-form-item prop="username">
                  <label for="username">ID</label>
                  <el-input placeholder="아이디를 입력해주세요" type="text" id="username" v-model="loginForm.username" />
                </el-form-item>
              </div>

              <div class="input-box__login">
                <el-form-item prop="password">
                  <label for="password">PW</label>
                  <el-input placeholder="비밀번호를 입력해주세요" type="password" id="password" v-model="loginForm.password" />
                </el-form-item>
              </div>
              <div class="register-wr">
                <div class="txt">아직 계정이 없다면? <a @click="handleRegister()">회원가입을 하세요.</a></div>
              </div>
              <button class="login-submit" type="submit" @click="handleLogin()">로그인</button>
            </div>
          </fieldset>
        </el-form>
      </div>
    </div>
    <div class="component-container">
      <div class="component">
        <UserModal :userModalVisible="userModalVisible" activeStep="Register" registerOnly=true @close="userModalVisible = false;"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import { Form } from 'element-ui';
import { Component, Vue } from 'vue-property-decorator';
import UserModal from '@/views/components/userModal.vue';

@Component({
  name: 'LoginIndex',
  components: {
    UserModal,
  },
})
export default class extends Vue {
  created() {
    // if (UserModule.token) this.$router.push({ name: 'Menu' });
  }

  private userModalVisible = false;

  private loginForm = {
    username: '',
    password: '',
  };

  private rules = {
    username: [{ required: true, message: '아이디를 입력하세요.', trigger: 'blur' }],
    password: [{ required: true, message: '비밀번호를 입력하세요.', trigger: 'blur' }],
  }

  private loading: boolean = false;

  private handleRegister() {
    this.userModalVisible = true;
  }

  private handleHome() {
    this.$router.push({ name: 'Home' });
  }

  private handleLogin() {
    (this.$refs.loginForm as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        UserModule.Login(this.loginForm).then(async () => {
          this.loading = false;
          await UserModule.GetUserInfo();
        //   if (UserModule.userId === 'master') this.$router.push({ name: 'Shop' });
        //   else if (UserModule.userId === 'board' || UserModule.userId === 'board01') this.$router.push({ name: 'Post' });
        //   else this.$router.push({ name: 'ManagerOrderList' });
          if (UserModule.isLogin) {
            await ChannelModule.InitChannelList();
            this.$message.info('로그인에 성공하셨습니다.');
            this.$router.push({ name: 'Home' });
          }
        }).catch(() => {
          this.loading = false;
        });
      }
    });
  }
}
</script>
<style>
  .login-container{
    position: fixed;
    width: 100vw;
    height: 100vh;
    background: url("~@/assets/login-bg.jpg") no-repeat 100%;
    overflow-y: auto;
  }
  .register-wr{
      a {cursor: pointer;color:#2a7fc6}
      .txt{font-size: 18px;margin-bottom: 15px;}
  }
</style>
 -->