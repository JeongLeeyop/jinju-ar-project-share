<template>
  <div class="aside">
    <div class="aside-logo">
      <router-link :to="{ name: 'Home' }">
        <img src="@/assets/images/logo.png" alt="">
      </router-link>
    </div>

    <div class="aside-content">
      <el-menu router="true" :default-active="activeMenu()">
        <slot v-for="(route, index) in routes">
          <slot v-if="route.meta && !route.meta.hidden">
            <el-submenu v-if="!route.redirect" :index="index">
              <template slot="title">
                <span>{{ route.meta.title }}</span>
              </template>
              <slot v-for="childRoute in route.children">
                <el-menu-item v-if="childRoute.meta && !childRoute.meta.hidden" :index="childRoute.name" :route="childRoute">
                  {{ childRoute.meta.title }}<i class="el-icon-arrow-right"></i>
                </el-menu-item>
              </slot>
            </el-submenu>
            <el-menu-item v-else :index="route.redirect">
              {{ route.meta.title }}
            </el-menu-item>
          </slot>
        </slot>
      </el-menu>
			<div class="aside-btn">
				<!-- <router-link :to="{ name: 'Setting' }"><img src="@/assets/images/setting.png" alt="">환경설정</router-link> -->
				<button @click="handleLogout()"><img src="@/assets/images/logout.png" alt="">로그아웃</button>
			</div>
    </div>

  </div>
</template>

<script lang="ts">
import { PermissionModule } from '@/store/modules/permission';
import { UserModule } from '@/store/modules/user';
import { Vue, Component, Watch } from 'vue-property-decorator';
import path from 'path';

@Component({
  components: {
  },
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

  private async handleLogout() {
    await UserModule.LogOut();
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
}
</script>
