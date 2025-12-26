<template>
  <div class="header home-header">
    <div class="header-left">
      <div class="header-logo">
        <!-- <a v-if="$route.name === 'Home'" @click="moveHome" href="">
          <span class="logo-text">
            <span class="logo-primary">진주알 </span><span class="logo-secondary">커뮤니티</span>
          </span>
        </a> -->
        <a v-if="$route.name === 'Home'" @click="moveHome" href=""><img src="@/assets/images/logo.png" alt=""></a>
        <ChannelSelect v-else ref="channelSelect"/>
      </div>
    </div>

    <div v-if="$route.name === 'Home'" class="header-nav">
      <router-link to="/" class="nav-item active">
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M14.6249 17.4686C14.6249 17.2446 14.4426 17.0624 14.2187 17.0624H11.7812C11.5572 17.0624 11.3749 17.2446 11.3749 17.4686V21.9374H14.6249V17.4686ZM21.9374 21.5311C21.9374 22.6526 21.0276 23.5624 19.9062 23.5624H6.09365C4.97217 23.5624 4.0624 22.6526 4.0624 21.5311V12.5227L3.01187 13.5743C2.69458 13.8917 2.18026 13.8916 1.86294 13.5743C1.54569 13.257 1.54568 12.7427 1.86294 12.4254L11.5632 2.72407V2.72301C12.3566 1.93141 13.6423 1.93095 14.4355 2.72407L24.1369 12.4254C24.4542 12.7427 24.4541 13.257 24.1369 13.5743C23.8196 13.8916 23.3052 13.8916 22.9879 13.5743L21.9374 12.5238V21.5311ZM16.2499 21.9374H19.9062C20.1302 21.9374 20.3124 21.7551 20.3124 21.5311V10.8988L13.2866 3.873C13.1287 3.7152 12.8709 3.71458 12.7111 3.87406L5.6874 10.8977V21.5311C5.6874 21.7551 5.86964 21.9374 6.09365 21.9374H9.7499V17.4686C9.74994 16.3472 10.6597 15.4374 11.7812 15.4374H14.2187C15.3401 15.4374 16.2499 16.3472 16.2499 17.4686V21.9374Z" fill="#073DFF"/>
        </svg>
        <span>홈</span>
      </router-link>
      <router-link to="/lession" class="nav-item">
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M13 6.77404V20.8574M13 6.77404C11.7347 5.93338 10.0165 5.41663 8.125 5.41663C6.2335 5.41663 4.51533 5.93338 3.25 6.77404V20.8574C4.51533 20.0167 6.2335 19.5 8.125 19.5C10.0165 19.5 11.7347 20.0167 13 20.8574M13 6.77404C14.2653 5.93338 15.9835 5.41663 17.875 5.41663C19.7676 5.41663 21.4847 5.93338 22.75 6.77404V20.8574C21.4847 20.0167 19.7676 19.5 17.875 19.5C15.9835 19.5 14.2653 20.0167 13 20.8574" stroke="#444444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>강좌</span>
      </router-link>
      <router-link to="/calendar" class="nav-item">
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M8.66667 7.58333V3.25M17.3333 7.58333V3.25M7.58333 11.9167H18.4167M5.41667 22.75H20.5833C21.158 22.75 21.7091 22.5217 22.1154 22.1154C22.5217 21.7091 22.75 21.158 22.75 20.5833V7.58333C22.75 7.0087 22.5217 6.4576 22.1154 6.05127C21.7091 5.64494 21.158 5.41667 20.5833 5.41667H5.41667C4.84203 5.41667 4.29093 5.64494 3.8846 6.05127C3.47827 6.4576 3.25 7.0087 3.25 7.58333V20.5833C3.25 21.158 3.47827 21.7091 3.8846 22.1154C4.29093 22.5217 4.84203 22.75 5.41667 22.75Z" stroke="#444444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>일정</span>
      </router-link>
      <a href="#" class="nav-item">
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M17.333 11.9167V7.58333C17.333 6.43406 16.8765 5.33186 16.0638 4.5192C15.2511 3.70655 14.1489 3.25 12.9997 3.25C11.8504 3.25 10.7482 3.70655 9.93555 4.5192C9.12289 5.33186 8.66634 6.43406 8.66634 7.58333V11.9167M5.41634 9.75H20.583L21.6663 22.75H4.33301L5.41634 9.75Z" stroke="#444444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>장터</span>
      </a>
      <router-link to="/member" class="nav-item">
        <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M18.417 21.6667H23.8337V19.5C23.8336 18.8246 23.6231 18.166 23.2315 17.6157C22.8398 17.0654 22.2865 16.6509 21.6483 16.4296C21.0101 16.2083 20.3189 16.1914 19.6707 16.3811C19.0225 16.5709 18.4495 16.9579 18.0313 17.4883M18.417 21.6667H7.58366M18.417 21.6667V19.5C18.417 18.7894 18.2805 18.1101 18.0313 17.4883M18.0313 17.4883C17.629 16.4829 16.9347 15.6211 16.038 15.0141C15.1413 14.4071 14.0832 14.0826 13.0003 14.0826C11.9174 14.0826 10.8594 14.4071 9.96266 15.0141C9.06592 15.6211 8.37163 16.4829 7.96933 17.4883M7.58366 21.6667H2.16699V19.5C2.16704 18.8246 2.37752 18.166 2.76918 17.6157C3.16083 17.0654 3.71419 16.6509 4.35235 16.4296C4.9905 16.2083 5.68173 16.1914 6.32996 16.3811C6.97818 16.5709 7.55119 16.9579 7.96933 17.4883M7.58366 21.6667V19.5C7.58366 18.7894 7.72016 18.1101 7.96933 17.4883M16.2503 7.58337C16.2503 8.44533 15.9079 9.27198 15.2984 9.88147C14.6889 10.491 13.8623 10.8334 13.0003 10.8334C12.1384 10.8334 11.3117 10.491 10.7022 9.88147C10.0927 9.27198 9.75032 8.44533 9.75032 7.58337C9.75032 6.72142 10.0927 5.89477 10.7022 5.28528C11.3117 4.67578 12.1384 4.33337 13.0003 4.33337C13.8623 4.33337 14.6889 4.67578 15.2984 5.28528C15.9079 5.89477 16.2503 6.72142 16.2503 7.58337ZM22.7503 10.8334C22.7503 11.408 22.5221 11.9591 22.1157 12.3654C21.7094 12.7718 21.1583 13 20.5837 13C20.009 13 19.4579 12.7718 19.0516 12.3654C18.6453 11.9591 18.417 11.408 18.417 10.8334C18.417 10.2587 18.6453 9.70764 19.0516 9.30131C19.4579 8.89498 20.009 8.66671 20.5837 8.66671C21.1583 8.66671 21.7094 8.89498 22.1157 9.30131C22.5221 9.70764 22.7503 10.2587 22.7503 10.8334ZM7.58366 10.8334C7.58366 11.408 7.35539 11.9591 6.94906 12.3654C6.54273 12.7718 5.99163 13 5.41699 13C4.84236 13 4.29126 12.7718 3.88493 12.3654C3.4786 11.9591 3.25033 11.408 3.25033 10.8334C3.25033 10.2587 3.4786 9.70764 3.88493 9.30131C4.29126 8.89498 4.84236 8.66671 5.41699 8.66671C5.99163 8.66671 6.54273 8.89498 6.94906 9.30131C7.35539 9.70764 7.58366 10.2587 7.58366 10.8334Z" stroke="#444444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span>회원</span>
      </router-link>
    </div>

    <div class="header-content">
      <div class="aside-btn">
        <!-- 포인트 표시 (로그인 시에만) -->
        <div v-if="isLogin" class="user-points" @click="handlePointHistory">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 18.3333C14.6024 18.3333 18.3334 14.6023 18.3334 9.99996C18.3334 5.39759 14.6024 1.66663 10 1.66663C5.39765 1.66663 1.66669 5.39759 1.66669 9.99996C1.66669 14.6023 5.39765 18.3333 10 18.3333Z" stroke="#073DFF" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M8.33331 6.66663H11.6666C12.1087 6.66663 12.5326 6.8422 12.8452 7.15476C13.1577 7.46732 13.3333 7.89127 13.3333 8.33329C13.3333 8.77532 13.1577 9.19927 12.8452 9.51183C12.5326 9.82439 12.1087 9.99996 11.6666 9.99996H8.33331V13.3333H12.5M10 4.16663V6.66663V4.16663ZM10 13.3333V15.8333V13.3333Z" stroke="#073DFF" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span class="point-text">알포인트: <strong>{{ formatPoint(currentPoint) }}</strong></span>
        </div>
        
        <Alram></Alram>
        <slot v-if="!isLogin">
          <button class="login-button" @click="handleLogin">로그인</button>
        </slot>
        <slot v-else>
          <el-popover v-model="showProfile" placement="bottom-end" width="450" trigger="click" popper-class="alarm"
            :popper-append-to-body="false" :title='`${userInfo.actualName}님 환영합니다.`'>
            <div @click="showProfile = false" class="alarm-close">
              <i class="el-icon-close"></i>
            </div>
            <div class="empty">
              <el-button @click="handleLogout()">로그아웃</el-button>
              <el-button @click="handleUserInfo()">내정보수정</el-button>
            </div>
            <el-button type="text" slot="reference" class="alarm">
              <img v-if="userInfo.iconFileUid" :src="apiUrl + '/attached-file/' + userInfo.iconFileUid"
                style="border-radius: 10px;width:60px;height:60px;">
              <img v-else src="@/assets/images/face2.png" />
            </el-button>
          </el-popover>
        </slot>
      </div>
      <div class="component-container">
        <div class="component">
          <UserInfo :userModalVisible="userModalVisible" @close="userModalVisible = false;"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { PermissionModule } from '@/store/modules/permission';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import ChannelSelect from '@/views/components/channelSelect.vue';
import Alram from '@/views/components/alram.vue';
import UserInfo from '@/views/components/UserInfo.vue';
import { getUserInfo, updateOnline } from '@/api/user';
import { getCurrentPoint } from '@/api/point';
import path from 'path';
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component({
  components: {
    ChannelSelect,
    UserInfo,
    Alram,
  },
})

export default class extends Vue {
  async mounted() {
    if (UserModule.isLogin) {
      await getUserInfo().then((res) => {
          this.userInfo = res.data;
          if (!this.userInfo.isOnline) {
            this.userInfo = { ...this.userInfo, isOnline: true };
            updateOnline().then(() => {
              console.log();
            });
          }
        });
      
      // 포인트 조회
      this.loadCurrentPoint();
    }
  }

  get routes() {
    return PermissionModule.routes;
  }

  private userInfo: any = {};

  private showProfile = false;

  private userModalVisible = false;

  private actualName = UserModule.actualName;

  private activeChannel = '1';

  private apiUrl = process.env.VUE_APP_BASE_API;

  private currentPoint = 0;

  @Watch('$route.path')
  private handlechangepath() {
  }

  // 현재 포인트 조회
  private async loadCurrentPoint() {
    try {
      const channelUid = ChannelModule.selectedChannel?.uid || 'default';
      const response = await getCurrentPoint(channelUid);
      this.currentPoint = response.data.currentPoint || 0;
    } catch (error) {
      console.error('Failed to load current point:', error);
      this.currentPoint = 0;
    }
  }

  // 포인트 포맷팅
  private formatPoint(point: number): string {
    return point.toLocaleString();
  }

  // 포인트 히스토리 페이지로 이동
  private handlePointHistory() {
    const domain = this.$route.params.domain || 'default';
    this.$router.push(`/community/${domain}/rpoint-history`);
  }

  private handleLogin() {
    this.$router.push({ name: 'Login' });
  }

  private handleCommunity() {
    this.$router.push({ name: 'CommunityMain' });
  }

  private handleHome() {
    this.$router.push({ name: 'Home' });
  }

  private activeMenu() {
    const route: any = this.$route;
    if (route.redirect) return route.redirect;
    return route.name;
  }

  private async handleLogout() {
    UserModule.LogOut();
    this.$message.info('로그아웃 되셨습니다');
    this.$router.push({ name: 'Home' });
  }

  private async handleUserInfo() {
    this.showProfile = false;
    this.userModalVisible = true;
  }

  private resolvePath(route: any, childRoute: any) {
    if (childRoute.path) {
      if (childRoute.path.indexOf('/') === 0) {
        return childRoute.path;
      }
      return path.resolve(route.path, childRoute.path);
    }
    return route.path;
  }

  get isLogin() {
    return UserModule.isLogin;
  }

  private moveHome() {
    this.$router.go(0);
  }
}
</script>

<style scoped lang="scss">
.home-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 44px 40px;
  border-bottom: 2px solid #EBEBEB;
  background: #FFF;
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-logo {
  display: flex;
  justify-content: center;
  align-items: center;

  a {
    text-decoration: none;
    display: flex;
    align-items: center;
  }

  .logo-text {
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 32px;
    font-weight: 800;
    line-height: 100%;
  }

  .logo-primary {
    color: #073DFF;
  }

  .logo-secondary {
    color: #222;
  }
}

.header-nav {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 32px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 16px;
  height: 32px;
  border-radius: 32px;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }

  span {
    color: #444;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    line-height: 100%;
  }

  &.active {
    svg path {
      fill: #073DFF;
    }

    span {
      color: #073DFF;
    }
  }
}

.header-content {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.aside-btn {
  display: flex;
  align-items: center;
  gap: 32px;
}

.user-points {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 8px;
  background: #F8F9FB;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #E8E9EB;
    transform: translateY(-1px);
  }

  svg {
    flex-shrink: 0;
  }

  .point-text {
    color: #073DFF;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 500;
    line-height: 100%;

    strong {
      font-weight: 700;
    }
  }
}

.login-button {
  color: #444;
  text-align: right;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: color 0.3s ease;

  &:hover {
    color: #073DFF;
  }
}

@media (max-width: 1200px) {
  .nav-item {
    padding: 0 12px;

    span {
      font-size: 18px;
    }

    svg {
      width: 22px;
      height: 22px;
    }
  }
}

@media (max-width: 1024px) {
  .header-nav {
    position: static;
    transform: none;
    gap: 16px;
  }

  .nav-item {
    padding: 0 10px;
    gap: 4px;

    span {
      font-size: 16px;
    }

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .header-logo .logo-text {
    font-size: 28px;
  }

  .login-button {
    font-size: 18px;
  }

  .aside-btn {
    gap: 20px;
  }
}

@media screen and (max-width: 768px) {
  .header-nav {
    display: none;
  }

  .home-header {
    padding: 24px 20px;
    height: fit-content; 
  }

  .header-logo .logo-text {
    font-size: 24px;
  }

  .login-button {
    font-size: 16px;
  }

  .aside-btn {
    gap: 16px;
  }
}

@media (max-width: 600px) {
  .header-logo .logo-text {
    font-size: 20px;
  }

  .login-button {
    font-size: 14px;
  }

  .aside-btn {
    gap: 12px;
  }
}
</style>
