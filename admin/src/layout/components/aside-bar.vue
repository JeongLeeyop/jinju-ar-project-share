<template>
  <div class="aside">
    <div class="aside-logo">
      <router-link :to="{ name: 'Home' }">
        <img src="@/assets/images/logo.png" alt="진주알 관리자">
        <span class="logo-text">관리자</span>
      </router-link>
    </div>

    <div class="aside-content">
      <el-menu router="true" :default-active="activeMenu()" :unique-opened="true">
        <slot v-for="(route, index) in routes">
          <slot v-if="route.meta && !route.meta.hidden">
            <el-submenu v-if="!route.redirect" :index="String(index)" :key="index">
              <template slot="title">
                <i :class="getMenuIcon(route.meta.title)"></i>
                <span>{{ route.meta.title }}</span>
              </template>
              <slot v-for="childRoute in route.children">
                <el-menu-item
                  v-if="childRoute.meta && !childRoute.meta.hidden"
                  :index="childRoute.name"
                  :route="childRoute"
                  :key="childRoute.name"
                >
                  {{ childRoute.meta.title }}
                </el-menu-item>
              </slot>
            </el-submenu>
            <el-menu-item v-else :index="route.redirect" :key="index">
              <i :class="getMenuIcon(route.meta.title)"></i>
              <span>{{ route.meta.title }}</span>
            </el-menu-item>
          </slot>
        </slot>
      </el-menu>
      <div class="aside-btn">
        <button @click="handleLogout()">
          <i class="el-icon-switch-button"></i>
          로그아웃
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { PermissionModule } from '@/store/modules/permission';
import { UserModule } from '@/store/modules/user';
import { Vue, Component, Watch } from 'vue-property-decorator';

@Component({
  components: {},
})
export default class extends Vue {
  get routes() {
    return PermissionModule.routes;
  }

  @Watch('$route.path')
  private handlechangepath() {
    console.log(this.$route);
  }

  private activeMenu() {
    const route: any = this.$route;
    if (route.redirect) return route.redirect;
    return route.name;
  }

  private getMenuIcon(title: string) {
    const iconMap: { [key: string]: string } = {
      '커뮤니티 관리': 'el-icon-s-home',
      '커뮤니티 공간': 'el-icon-office-building',
      '커뮤니티 장터': 'el-icon-shopping-cart-2',
      회원관리: 'el-icon-user',
      문자전송: 'el-icon-message',
    };
    return iconMap[title] || 'el-icon-menu';
  }

  private async handleLogout() {
    await UserModule.LogOut();
    this.$router.push({ name: 'Login' });
  }
}
</script>

<style scoped lang="scss">
.aside {
  position: fixed;
  left: 0;
  top: 0;
  width: 240px;
  height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  display: flex;
  flex-direction: column;
}

.aside-logo {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);

  a {
    display: flex;
    align-items: center;
    gap: 12px;
    text-decoration: none;

    img {
      height: 36px;
      width: auto;
    }

    .logo-text {
      color: #073DFF;
      font-size: 14px;
      font-weight: 700;
      background: rgba(7, 61, 255, 0.15);
      padding: 4px 10px;
      border-radius: 20px;
    }
  }
}

.aside-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  padding: 16px 0;
}

::v-deep .el-menu {
  border: none;
  background: transparent;
  
  .el-submenu__title,
  .el-menu-item {
    height: 48px;
    line-height: 48px;
    color: rgba(255, 255, 255, 0.8);
    padding-left: 24px !important;
    transition: all 0.3s ease;
    
    i {
      color: rgba(255, 255, 255, 0.6);
      margin-right: 12px;
      font-size: 18px;
    }
    
    &:hover {
      background: rgba(7, 61, 255, 0.15);
      color: #fff;
      
      i {
        color: #073DFF;
      }
    }
  }
  
  .el-submenu__title {
    font-weight: 600;
    font-size: 15px;
    
    .el-submenu__icon-arrow {
      color: rgba(255, 255, 255, 0.5);
    }
  }
  
  .el-submenu.is-opened {
    .el-submenu__title {
      background: rgba(7, 61, 255, 0.1);
      color: #fff;
      
      i {
        color: #073DFF;
      }
    }
  }
  
  .el-menu-item {
    padding-left: 56px !important;
    font-size: 14px;
    
    &.is-active {
      background: linear-gradient(90deg, #073DFF 0%, rgba(7, 61, 255, 0.6) 100%);
      color: #fff;
      font-weight: 600;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        width: 4px;
        height: 100%;
        background: #fff;
        border-radius: 0 4px 4px 0;
      }
    }
  }
  
  .el-submenu .el-menu {
    background: rgba(0, 0, 0, 0.2);
  }
}

.aside-btn {
  margin-top: auto;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  
  button {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px 20px;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 8px;
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    
    i {
      font-size: 18px;
    }
    
    &:hover {
      background: rgba(255, 255, 255, 0.15);
      color: #fff;
      border-color: rgba(255, 255, 255, 0.3);
    }
  }
}
</style>
