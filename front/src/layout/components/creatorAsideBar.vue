<template>
  <!-- <div> -->
    <div class="aside" :class="mobileAsideClass()">
      <!-- <div class="aside-header"></div> -->
      <div class="aside-content">
        <el-menu
          default-active="2"
          class="el-menu-vertical-demo"
          @open="handleOpen"
          @close="handleClose"
          :collapse="sideHide == '1'"
          >
          <slot v-if="$route.name === 'CreateCommunity'">
            <el-menu-item index="1" @click="handleClick('CreateCommunity')">
            <i class="el-icon-chat-line-square"></i>
            <span>커뮤니티 생성</span>
          </el-menu-item>
          </slot>
          <slot v-else>
            <el-menu-item index="2" @click="handleClick('InviteLink')">
            <i class="el-icon-link"></i>
            <span>초대링크</span>
          </el-menu-item>
          <el-menu-item index="3" @click="handleClick('CommunitySetting')">
            <i class="el-icon-chat-line-square"></i>
            <span>커뮤니티</span>
          </el-menu-item>
            <el-menu-item index="4" @click="handleClick('CategorySetting')">
              <i class="el-icon-s-unfold"></i>
              <span>카테고리</span>
            </el-menu-item>
            <!-- <el-menu-item index="4" @click="handleClick('LessionSetting')">
              <i class="el-icon-video-play"></i>
              <span>강의관리</span>
            </el-menu-item> -->
            <el-submenu index="5">
            <template slot="title">
              <i class="el-icon-menu"></i>
              <span style="font-size:18px;">영상 콘텐츠</span>
            </template>
            <el-menu-item-group>
              <el-menu-item @click="handleClick('LessionSetting')" index="5">목차 관리</el-menu-item>
              <el-menu-item @click="handleClick('VideoSetting')" index="6">동영상 관리</el-menu-item>
            </el-menu-item-group>
          </el-submenu>
            <el-menu-item index="6" @click="handleClick('MemberSetting')">
              <i class="el-icon-user"></i>
              <span>회원관리</span>
            </el-menu-item>
            <el-menu-item index="6-1" @click="handleClick('PermissionSetting')">
              <i class="el-icon-lock"></i>
              <span>권한관리</span>
            </el-menu-item>
            <el-menu-item index="7" @click="handleClick('RankingSetting')">
              <i class="el-icon-s-flag"></i>
              <span>랭킹관리</span>
            </el-menu-item>
            <el-menu-item index="8" @click="handleClick('CalendarSetting')">
              <i class="el-icon-date"></i>
              <span>일정관리</span>
            </el-menu-item>
          <!-- <el-menu-item index="8" @click="handleClick('Dashboard')">
            <i class="el-icon-setting"></i>
            <span>데이터 분석</span>
          </el-menu-item> -->
          </slot>
        </el-menu>
        <div class="aside-btn" v-if="sideHide === ''">
          <slot v-if="$route.name === 'CreateCommunity'">
            <router-link :to="{ name: 'Home' }"><i class="el-icon-back"></i>돌아가기</router-link>
          </slot>
          <slot v-else>
            <router-link :to="{ name: 'CommunityMain' }"><i class="el-icon-back"></i>돌아가기</router-link>
          </slot>
        </div>
        <div v-else>
          <slot v-if="$route.name === 'CreateCommunity'">
            <router-link class="aside-btn-hide" :to="{ name: 'Home' }"><i class="el-icon-back"></i></router-link>
          </slot>
          <slot v-else>
            <router-link class="aside-btn-hide" :to="{ name: 'CommunityMain' }"><i class="el-icon-back"></i></router-link>
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
    </div>
    <!-- <div v-if="!isAsideHidden" class="aside-overlay" @click="hideAside"></div>
  </div> -->
</template>

<script lang="ts">
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

  private isAsideHidden = false;

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
    await UserModule.LogOut();
    this.$router.push({ name: 'Home' });
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
    padding:20px; cursor: pointer;    line-height: 40px;
  }

  .el-icon-d-arrow-left {}
</style>
