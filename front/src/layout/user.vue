<template>
  <div>
    <div class="header-container">
      <HomeHeader @search="(searchValue) => handleSearch(searchValue)"/>
    </div>
    <div class="content-container">
      <!-- <AsideBar v-if="!isCommunityPage"/> -->
      <router-view class="content-wrap" :key="$route.params.domain" :class="{ 'full-width': isCommunityPage }"/>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import AsideBar from './components/asideBar.vue';
import HomeHeader from './components/homeHeader.vue';

@Component({
  components: {
    AsideBar,
    HomeHeader,
  },
})
export default class extends Vue {
  private handleSearch(searchValue: any) {
    this.$router.push({
      name: 'CommunityMain',
      query: { searchValue },
    });
  }

  get isCommunityPage() {
    return ['CommunityMain', 'CommunitySpace'].includes(this.$route.name as string);
  }
}
</script>

<style scoped>
.content-wrap.full-width {
  margin-left: 0 !important;
  width: 100%;
}
</style>
