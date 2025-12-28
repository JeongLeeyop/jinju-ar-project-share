<template>
  <div>
    <HomeHeader ref="homeHeader"/>
    <router-view class="content-wrap" @select-channel="handleSelectChannel" />
  </div>
</template>

<script lang="ts">
import AsideBar from './components/asideBar.vue';
import HomeHeader from './components/homeHeader.vue';
import { Vue, Component, Watch } from 'vue-property-decorator';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';

@Component({
  components: {
    AsideBar,
    HomeHeader,
  },
})
export default class extends Vue {
  private handleSelectChannel(channel: any) {
    const homeHeader = this.$refs.homeHeader as any;
    if (homeHeader && homeHeader.$refs && homeHeader.$refs.channelSelect) {
      homeHeader.$refs.channelSelect.update(channel);
    }
  }
}
</script>

<style scoped>
</style>
