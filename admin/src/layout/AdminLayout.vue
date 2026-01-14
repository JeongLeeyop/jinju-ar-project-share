<template>
  <div class="admin-layout">
    <AdminHeader />
    <div class="admin-container">
      <AdminSidebar />
      <div class="admin-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import AdminHeader from './components/AdminHeader.vue';
import AdminSidebar from './components/AdminSidebar.vue';

@Component({
  name: 'AdminLayout',
  components: {
    AdminHeader,
    AdminSidebar,
  },
})
export default class extends Vue {}
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  background: #F5F7FA;
}

.admin-container {
  display: flex;
  padding-top: 70px;
}

.admin-main {
  flex: 1;
  padding: 24px;
  margin-left: 250px;
  transition: margin-left 0.3s;
  
  @media (max-width: 768px) {
    margin-left: 0;
    padding: 16px;
  }
}
</style>
