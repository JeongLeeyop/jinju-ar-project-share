<template>
  <div class="community-settings-page">
    <!-- CommunitySidebar -->
    <CommunitySidebar :selectedSpaceId="'community-settings'" />

    <!-- Main Content Area -->
    <div class="settings-main">
      <h2 class="section-title">커뮤니티 설정</h2>
      
      <div class="settings-content">
        <p class="placeholder-text">커뮤니티 설정 페이지는 준비 중입니다.</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

@Component({
  name: 'CommunitySettings',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
}
</script>

<style scoped lang="scss">
.community-settings-page {
  display: flex;
  min-height: 100vh;
  background: #FFF;
}

.settings-main {
  margin-left: 267px;
  padding: 50px 80px;
  flex: 1;
  min-height: calc(100vh - 124px);
}

.section-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 600;
  line-height: 100%;
  margin: 0 0 40px 0;
}

.settings-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.placeholder-text {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  text-align: center;
}

/* Responsive Design */
@media screen and (max-width: 1024px) {
  .settings-main {
    margin-left: 0;
    padding: 40px;
  }
}

@media screen and (max-width: 768px) {
  .settings-main {
    padding: 32px 24px;
  }

  .section-title {
    font-size: 28px;
  }
}

@media screen and (max-width: 600px) {
  .settings-main {
    padding: 20px 16px;
  }

  .section-title {
    font-size: 24px;
  }
}
</style>
