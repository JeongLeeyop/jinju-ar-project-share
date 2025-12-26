<template>
  <div class="aside" :class="mobileAsideClass()">
    <div class="aside-content">
      <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        @open="handleOpen"
        @close="handleClose"
        :collapse="sideHide == '1'">
        <el-menu-item index="1" @click="handleClick('CommunityMain')">
          <span slot="title">커뮤니티 공간</span>
        </el-menu-item>
        <el-submenu index="2">
          <template slot="title">
            <span style="font-size:18px;">공간 개설하기</span>
          </template>
          <el-menu-item-group>
            <el-menu-item @click="handleClick('CommunityChat')" index="all">산청의료협동조합</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
      </el-menu>
      <slot v-if="sideHide === ''">
        <div class="aside-btn">
          <router-link v-if="isCreator" :to="{ name: 'InviteLink', params: { activeTab: '1' } }"><i class="el-icon-setting"></i>환경설정</router-link>
          <button v-if="isLogin" @click="handleLogout"><i class="el-icon-lock"></i>로그아웃</button>
        </div>
      </slot>
      <slot v-else>
        <router-link class="aside-btn-hide" v-if="isCreator" :to="{ name: 'InviteLink', params: { activeTab: '1' } }"><i class="el-icon-setting"></i></router-link>
      </slot>
    </div>
    <div :class="sideHide ? 'side-hide-btn--hide' : 'side-hide-btn'" @click="handleActiveSide()" v-if="!isMobile">
        <slot v-if="sideHide == '1'">
          <i class="el-icon-d-arrow-right" />
        </slot>
        <slot v-else>
          <i class="el-icon-d-arrow-left" />
        </slot>
    </div>
    <div :class="mobileSideHide ? 'mobile-side-hide-btn--hide' : 'mobile-side-hide-btn'" @click="handleActiveMobileSide()" v-else>
        <slot v-if="mobileSideHide == '1'">
          <i class="el-icon-d-arrow-right" />
        </slot>
        <slot v-else>
          <i class="el-icon-d-arrow-left" />
        </slot>
    </div>
  </div>
</template>

<script lang="ts">
import { ChannelModule } from '@/store/modules/channel';
import { PermissionModule } from '@/store/modules/permission';
import { UserModule } from '@/store/modules/user';
import ChannelSelect from '@/views/components/channelSelect.vue';
import path from 'path';
import { Component, Vue, Watch } from 'vue-property-decorator';
import {
  getSideHide,
  setSideHide,
  getMobileSideHide,
  setMobileSideHide,
} from '@/utils/cookies';

@Component({
  components: {
    ChannelSelect,
  },
})

export default class extends Vue {
  created() {
    if (this.isMobile) {
      this.sideHide = '1';
      if (getMobileSideHide() === '1') {
        this.mobileSideHide = '1';
      }
    } else if (getSideHide() === '1') {
      this.sideHide = '1';
    }
    window.addEventListener('resize', this.handleResize);
  }

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize);
  }

  private handleResize() {
    this.pageSize = (window as any).innerWidth;
  }

  private pageSize: any = (window as any).innerWidth;

  private isMobile: any = this.pageSize < 425;

  get routes() {
    return PermissionModule.routes;
  }

  private activeChannel = '1';

  private sideHide = '';

  private mobileSideHide = '';

  private handleActiveSide() {
    if (this.sideHide === '1') {
      this.sideHide = '';
      setSideHide('');
    } else {
      this.sideHide = '1';
      setSideHide('1');
    }
  }

  private handleActiveMobileSide() {
    if (this.mobileSideHide === '1') {
      this.mobileSideHide = '';
      setMobileSideHide('');
    } else {
      this.mobileSideHide = '1';
      setMobileSideHide('1');
    }
  }

  private handleOpen(key: any, keyPath: any) {
      // console.log(key, keyPath);
  }

  private handleClose(key: any, keyPath: any) {
    // console.log(key, keyPath);
  }

  private handleClick(key: any, keyPath: any) {
    this.$router.push({ name: key });
  }

  @Watch('$route.path')
  private handlechangepath() {
    // console.log(this.$route);
  }

  private activeMenu() {
    const route: any = this.$route;
    if (route.redirect) return route.redirect;
    return route.name;
  }

  private async handleLogout() {
    UserModule.LogOut();
    this.$router.push({ name: 'Login' });
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

  get isCreator() {
    return (UserModule.roles.some((role: any) => ['ROLE_CREATOR'].indexOf(role) > -1) && ChannelModule.selectedChannel.myChannelStatus);
  }

  private mobileAsideClass() {
    let str = '';
    if (this.isMobile && this.mobileSideHide === '1') {
      str = 'mobile hide';
    } else if (this.isMobile) {
      str = 'mobile';
    }
    return str;
  }
}
</script>

<style scoped>
  .side-hide-btn {
    position: absolute;
    top: 40vh;
    left: 300px;
    background: #615f72;
    color: #fff;
    padding: 5px 0;
    cursor: pointer;
  }

  .side-hide-btn--hide {
    position: absolute;
    top: 40vh;
    left: 65px;
    background: #615f72;
    color: #fff;
    padding: 5px 0;
    cursor: pointer;
  }

  .aside-btn-hide {
    padding:20px; cursor: pointer;
  }

  .el-icon-d-arrow-left {}
</style>
