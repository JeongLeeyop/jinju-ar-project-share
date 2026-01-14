<template>
  <div class="admin-sidebar" :class="{ 'mobile-hidden': !sidebarOpen }">
    <div class="sidebar-overlay" v-if="sidebarOpen" @click="closeSidebar"></div>
    <div class="sidebar-content">
      <nav class="sidebar-menu">
        <router-link 
          v-for="item in menuItems" 
          :key="item.path" 
          :to="item.path"
          class="menu-item"
          :class="{ active: isActive(item.path) }"
        >
          <component :is="item.icon" class="menu-icon" />
          <span>{{ item.title }}</span>
        </router-link>
      </nav>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  name: 'AdminSidebar',
})
export default class extends Vue {
  private sidebarOpen = false;

  private menuItems = [
    { 
      path: '/community', 
      title: '커뮤니티 관리',
      icon: {
        template: `
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M2.5 5.83333C2.5 4.91286 3.24619 4.16667 4.16667 4.16667H15.8333C16.7538 4.16667 17.5 4.91286 17.5 5.83333V14.1667C17.5 15.0871 16.7538 15.8333 15.8333 15.8333H4.16667C3.24619 15.8333 2.5 15.0871 2.5 14.1667V5.83333Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M2.5 7.5H17.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        `,
      },
    },
    { 
      path: '/space', 
      title: '커뮤니티 공간',
      icon: {
        template: `
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M5.83333 7.5H14.1667M5.83333 10.8333H14.1667M5.83333 14.1667H10M4.16667 2.5H15.8333C16.7538 2.5 17.5 3.24619 17.5 4.16667V15.8333C17.5 16.7538 16.7538 17.5 15.8333 17.5H4.16667C3.24619 17.5 2.5 16.7538 2.5 15.8333V4.16667C2.5 3.24619 3.24619 2.5 4.16667 2.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        `,
      },
    },
    { 
      path: '/marketplace', 
      title: '커뮤니티 장터',
      icon: {
        template: `
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M14.1667 8.33333V5.83333C14.1667 4.72876 13.7277 3.66957 12.9463 2.88817C12.1649 2.10677 11.1057 1.66667 10 1.66667C8.89432 1.66667 7.83514 2.10677 7.05374 2.88817C6.27235 3.66957 5.83325 4.72876 5.83325 5.83333V8.33333M3.33325 6.66667H16.6666L17.4999 17.5H2.49992L3.33325 6.66667Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        `,
      },
    },
    { 
      path: '/member', 
      title: '회원 관리',
      icon: {
        template: `
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M10 10C12.0711 10 13.75 8.32107 13.75 6.25C13.75 4.17893 12.0711 2.5 10 2.5C7.92893 2.5 6.25 4.17893 6.25 6.25C6.25 8.32107 7.92893 10 10 10ZM10 10C6.27208 10 3.25 12.0221 3.25 14.5V17.5H16.75V14.5C16.75 12.0221 13.7279 10 10 10Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        `,
      },
    },
    { 
      path: '/sms', 
      title: '문자 전송',
      icon: {
        template: `
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M2.5 3.33333H17.5V13.3333H5.83333L2.5 16.6667V3.33333Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M5.83325 7.5H14.1666M5.83325 10.8333H11.6666" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        `,
      },
    },
  ];

  private isActive(path: string) {
    return this.$route.path.startsWith(path);
  }

  private closeSidebar() {
    this.sidebarOpen = false;
  }
}
</script>

<style lang="scss" scoped>
.admin-sidebar {
  position: fixed;
  top: 70px;
  left: 0;
  bottom: 0;
  width: 250px;
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  z-index: 100;
  transition: transform 0.3s;

  @media (max-width: 768px) {
    transform: translateX(-100%);
    
    &:not(.mobile-hidden) {
      transform: translateX(0);
    }
  }
}

.sidebar-overlay {
  display: none;
  
  @media (max-width: 768px) {
    display: block;
    position: fixed;
    top: 70px;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 99;
  }
}

.sidebar-content {
  height: 100%;
  overflow-y: auto;
  padding: 24px 0;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: #444;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s;
  border-left: 3px solid transparent;

  .menu-icon {
    width: 20px;
    height: 20px;
    color: #888;
    transition: color 0.2s;
  }

  &:hover {
    background: #F5F7FA;
    color: #073DFF;

    .menu-icon {
      color: #073DFF;
    }
  }

  &.active {
    background: #EEF2FF;
    color: #073DFF;
    border-left-color: #073DFF;
    font-weight: 600;

    .menu-icon {
      color: #073DFF;
    }
  }
}
</style>
