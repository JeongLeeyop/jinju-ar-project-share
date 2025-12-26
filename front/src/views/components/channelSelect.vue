<template>
    <div class="header-profile">
      <slot v-if="selectedChannel?.iconImageList?.length > 0">
          <div class="header-profile-logo"><img :src="apiUrl + '/attached-file/' + selectedChannel.iconImageList[0].fileUid"></div>
        </slot>
        <slot v-else>
          <el-avatar class="header-profile-logo" shape="square" size="medium" src="https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png"></el-avatar>
        </slot>
        <el-select v-model="selectedChannel" value-key="uid" placeholder="채널 이동하기" @change="handlePickChannel">
            <el-option
                v-if="$route.name !== 'Home'"
                label="홈으로 이동"
                value="Home"
                class="home-btn"
              />
            <el-option-group
              :key="group.label"
              :label="group.label"
              v-for="group in ($route.name === 'Home' ? channelList.slice(1) : channelList)"
            >
              <el-option
                v-for="item in group.options"
                :key="item.uid"
                :label="item.name"
                :value="item"
              />
            </el-option-group>
        </el-select>
    </div>
</template>
<script lang="ts">
import { Component, Watch, Vue } from 'vue-property-decorator';
import { ChannelModule } from '@/store/modules/channel';
import { getMyChannelList } from '@/api/channel';
import { UserModule } from '@/store/modules/user';
import Channel from '../channel.vue';

@Component({
    name: 'ChannelSelect',
    components: {
    },
})
export default class extends Vue {
  async mounted() {
      await ChannelModule.InitChannelList();
      this.channelList = ChannelModule.channelList;
    }

    private selectedChannel: any = ChannelModule.selectedChannel;

    private channelList = ChannelModule.channelList;

    private apiUrl = process.env.VUE_APP_BASE_API;

    private handlePickChannel() {
      if (this.selectedChannel !== 'Home') {
        ChannelModule.setSelectedChannel({ name: this.selectedChannel.name, uid: this.selectedChannel.uid });
        this.$router.push({ name: 'CommunityMain', params: { domain: this.selectedChannel.domain } });
        this.selectedChannel = { ...this.selectedChannel };
      } else {
        this.$router.push({ name: 'Home' });
      }
    }

    private async update(channel: any) {
      ChannelModule.setSelectedChannel({ name: channel.name, uid: channel.uid, iconImageList: channel.iconImageList });
      this.selectedChannel = ChannelModule.selectedChannel;
      this.selectedChannel = { ...this.selectedChannel };
    }
}
</script>
