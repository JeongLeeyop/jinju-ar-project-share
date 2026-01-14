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
import { login } from '@/api/user';
import { Message } from 'element-ui';
import router from '@/router';

export interface IUserState {
  token: string
  userId: string
  roles: string[]
}

@Module({ dynamic: true, store, name: 'user' })
class User extends VuexModule implements IUserState {
  public token = getToken() || ''

  public userId = ''

  public roles: string[] = []

  @Mutation
  private SET_TOKEN(token: string) {
    this.token = token;
  }

  @Mutation
  private SET_USER_ID(userId: string) {
    this.userId = userId;
  }

  @Mutation
  private SET_ROLES(roles: string[]) {
    console.log(roles);
    this.roles = roles;
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
      Message.error(data.error_description || data.message);
      /* eslint-enable */
    });
  }

  @Action
  public ResetToken() {
    removeToken();
    this.SET_TOKEN('');
    this.SET_USER_ID('');
    this.SET_ROLES([]);
  }

  @Action
  public async GetUserInfo() {
    if (this.token === '') {
      this.ResetToken();
      router.push({ name: 'Login' });
    }
    // tokenCheck(this.token).catch(() => {
    //   throw Error('Verification failed, please Login again.');
    // });
    const data: any = await getTokenDecode();
    const whiteRoles = ['ROLE_ADMIN', 'ROLE_SHOP_ADMIN', 'ROLE_BOARD'];
    let hasRole = false;
    hasRole = data.authorities.some((role: any) => whiteRoles.indexOf(role) > -1);
    if (!data.authorities || data.authorities.length <= 0 || !hasRole) {
      this.ResetToken();
      router.push({ name: 'Login' });
    }
    this.SET_ROLES(data.authorities);
    this.SET_USER_ID(data.user_name);
  }

  @Action
  public async LogOut() {
    if (this.token === '') {
      throw Error('LogOut: token is undefined!');
    }
    removeToken();

    // Reset visited views and cached views
    this.SET_TOKEN('');
    this.SET_ROLES([]);
    this.SET_USER_ID('');
  }
}

export const UserModule = getModule(User);
