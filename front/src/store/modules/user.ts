import {
  VuexModule,
  Module,
  Action,
  Mutation,
  getModule,
} from 'vuex-module-decorators';
// import { getAuthorizationCode, REDIRECT_URL, SERVER_URL } from '@/api/login';
import {
  getToken,
  getTokenDecode,
  setToken,
  removeToken,
} from '@/utils/cookies';
import store from '@/store';
import { login, logout } from '@/api/user';
import { Message } from 'element-ui';
import router from '@/router';

export interface IUserState {
  token: string
  userId: string
  actualName: string
  roles: string[]
}

@Module({ dynamic: true, store, name: 'user' })
class User extends VuexModule implements IUserState {
  public isLogin = false;

  public token = getToken() || ''

  public userId = ''

  public roles: string[] = []

  public actualName = '';

  @Mutation
  private SET_TOKEN(token: string) {
    this.token = token;
  }

  @Mutation
  private SET_ACTUAL_NAME(userName: string) {
    this.actualName = userName;
  }

  @Mutation
  private SET_USER_ID(userId: string) {
    this.userId = userId;
  }

  @Mutation
  private SET_ROLES(roles: string[]) {
    this.roles = roles;
  }

  @Mutation
  private SET_IS_LOGIN(status: boolean) {
    this.isLogin = status;
  }

  @Action
  public async Login(userInfo: { username: string, password: string}) {
    const { username, password } = userInfo;
    await login({ username, password }).then((res) => {
      setToken(res.data.access_token);
      this.SET_TOKEN(res.data.access_token);
    }).catch((error : any) => {
      /* eslint-disable */
      const data = error.response.data;
      Message.error('로그인에 실패하셨습니다.');
      // Message.error(data.error_description || data.message);
      /* eslint-enable */
    });
  }

  @Action
  public ResetToken() {
    removeToken();
    this.SET_TOKEN('');
    this.SET_USER_ID('');
    this.SET_ACTUAL_NAME('');
    this.SET_ROLES([]);
  }

  @Action
  public async GetUserInfo() {
    // if (this.token === '') {
    //   this.ResetToken();
    //   router.push({ name: 'Login' });
    // }
    // tokenCheck(this.token).catch(() => {
        // throw Error('Verification failed, please Login again.');
      // });
    if (this.token) {
      const data: any = await getTokenDecode();
      this.SET_ROLES(data.authorities);
      this.SET_USER_ID(data.user_name);
      this.SET_ACTUAL_NAME(data.actualName);
      this.SET_IS_LOGIN(true);
    }
  }

  @Action
  public async LogOut() {
    if (this.token === '') {
      throw Error('LogOut: token is undefined!');
    }

    await logout().then((res) => {
      console.log(res);
    }).catch((error : any) => {
      console.log(error);
    });

    this.SET_IS_LOGIN(false);
    removeToken();

    // Reset visited views and cached views
    this.SET_TOKEN('');
    this.SET_ROLES([]);
    this.SET_USER_ID('');
    this.SET_ACTUAL_NAME('');
  }
}

export const UserModule = getModule(User);
