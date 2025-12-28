<template>
  <div>
    <div class="header-container">
      <HomeHeader v-if="isInitCommunity"/>
      <Header v-else/>
    </div>
    <div class="content-container">
      <CreatorAsideBar v-if="!isInitCommunity"/>
      <router-view class="content-wrap" :class="{ 'full-width': isInitCommunity }"/>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CreatorAsideBar from './components/creatorAsideBar.vue';
import Header from './components/header.vue';
import HomeHeader from './components/homeHeader.vue';

@Component({
  components: {
    CreatorAsideBar,
    Header,
    HomeHeader,
  },
})
export default class extends Vue {
  get isInitCommunity() {
    return this.$route.name === 'InitCommunity' || this.$route.name === 'CreateCommunity';
  }
}
</script>

<style scoped lang="scss">
.content-container {
  display: flex;
  min-height: calc(100vh - 120px);
  background: #fff;
  padding:50px;
}

.content-wrap {
  flex: 1;
  
  &.full-width {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
}

@media (max-width: 425px) {
  .content-container {
    padding: 50px 0px;
  }
}
</style>
