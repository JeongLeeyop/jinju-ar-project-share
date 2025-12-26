import {
  VuexModule,
  Module,
  Action,
  Mutation,
  getModule,
} from 'vuex-module-decorators';
import store from '@/store';
import { storageKey } from '@/enums/localStorage';
import { getMyChannelList } from '@/api/channel';
import { UserModule } from '@/store/modules/user';

@Module({ dynamic: true, store, name: 'channel' })
class Channel extends VuexModule {
  public form: any = {};

  public selectedChannel: any = {
    name: '',
    uid: '',
  };

  public channelList: any = [];

  public currentChannel: any = {
    label: '현재 선택된 커뮤니티',
    options: [],
  };

  public myChannelList: any = {
      label: '내 커뮤니티',
      options: [],
  };

  public joinChannelList: any = {
      label: '가입한 커뮤니티',
      options: [],
  };

  public joinChannelList2: any = [
    {
      label: '유료커뮤니티',
      options: [
        {
          name: '',
          uid: '',
        },
      ],
    },
    {
      label: '일반커뮤니티',
      options: [
        {
          name: '',
          uid: '',
        },
      ],
    },
  ];

  @Mutation
  private SET_FORM(data: any) {
    this.form = data;
  }

  @Mutation
  private SET_SELECTED_CHANNEL(data: any) {
    this.selectedChannel = data;
    this.channelList = [];
    this.currentChannel.options[0] = this.selectedChannel;
    this.channelList[0] = this.currentChannel;
    if (this.myChannelList.options.length > 0) this.channelList.push(this.myChannelList);
    this.channelList.push(this.joinChannelList);
  }

  @Mutation
  private SET_CHANNEL_LIST(data: any) {
    this.myChannelList.options = data.myList;
    this.joinChannelList.options = data.joinList;
    this.channelList = [];
    this.currentChannel.options[0] = this.selectedChannel;
    this.channelList.push(this.currentChannel);
    if (this.myChannelList.options.length > 0) this.channelList.push(this.myChannelList);
    this.channelList.push(this.joinChannelList);
    // if (window.localStorage.getItem(storageKey.selectedChannel) != null) {}
  }

  @Mutation
  private SET_GUEST_LIST(data: any) {
    this.myChannelList.options = data;
    this.channelList = [];
    this.currentChannel.options[0] = this.selectedChannel;
    this.channelList.push(this.currentChannel);
  }

  @Action
  public setSelectedChannel(data: any) {
    this.SET_SELECTED_CHANNEL(data);
  }

  @Action
  public setChannelList(data: any) {
    this.SET_CHANNEL_LIST(data);
  }

  @Action
  public setGuestList(data: any) {
    this.SET_GUEST_LIST(data);
  }

  @Action
  public async InitChannelList() {
    if (UserModule.isLogin) {
      await getMyChannelList().then((res) => {
          this.setChannelList(res.data);
      });
    } else {
      this.setGuestList([]);
    }
  }
}

export const ChannelModule = getModule(Channel);
