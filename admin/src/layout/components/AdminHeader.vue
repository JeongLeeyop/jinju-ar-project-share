<template>
  <div class="admin-header">
    <div class="header-container">
      <div class="header-left">
        <button class="mobile-menu-btn" @click="toggleSidebar">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M3 12H21M3 6H21M3 18H21" stroke="white" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
        <div class="header-logo">
          <img src="@/assets/images/logo.png" alt="진주알 관리자" />
          <span class="admin-badge">관리자</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M10 10C12.0711 10 13.75 8.32107 13.75 6.25C13.75 4.17893 12.0711 2.5 10 2.5C7.92893 2.5 6.25 4.17893 6.25 6.25C6.25 8.32107 7.92893 10 10 10ZM10 10C6.27208 10 3.25 12.0221 3.25 14.5V17.5H16.75V14.5C16.75 12.0221 13.7279 10 10 10Z" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>{{ userName }}</span>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M13.75 14.375L17.5 10.625M17.5 10.625L13.75 6.875M17.5 10.625H7.5M7.5 2.5H5.625C4.58947 2.5 3.75 3.33947 3.75 4.375V16.875C3.75 17.9105 4.58947 18.75 5.625 18.75H7.5" stroke="white" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          로그아웃
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { UserModule } from '@/store/modules/user';

@Component({
  name: 'AdminHeader',
})
export default class extends Vue {
  get userName() {
    return UserModule.actualName || '관리자';
  }

  private toggleSidebar() {
    this.$emit('toggle-sidebar');
  }

  private handleLogout() {
    this.$confirm('로그아웃 하시겠습니까?', '확인', {
      confirmButtonText: '확인',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      UserModule.LogOut();
      this.$router.push('/login');
      this.$message.success('로그아웃 되었습니다');
    });
  }
}
</script>

<style lang="scss" scoped>
.admin-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: linear-gradient(135deg, #073DFF 0%, #0529B8 100%);
  box-shadow: 0 2px 8px rgba(7, 61, 255, 0.15);
  z-index: 1000;
}

.header-container {
  height: 100%;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1920px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.mobile-menu-btn {
  display: none;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 8px;

  @media (max-width: 768px) {
    display: flex;
  }
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 12px;

  img {
    height: 40px;
    filter: brightness(0) invert(1);
  }

  .admin-badge {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 14px;
  
  @media (max-width: 768px) {
    display: none;
  }
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}
</style>
