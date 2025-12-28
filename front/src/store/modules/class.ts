import {
  VuexModule,
  Module,
  Action,
  Mutation,
  getModule,
} from 'vuex-module-decorators';
import store from '@/store';

@Module({ dynamic: true, store, name: 'reservationItem' })
class LectureItem extends VuexModule {
  public searchTypeUid: string = '1';

  @Mutation
  private SET_SEARCH_TYPE(data: string) {
    this.searchTypeUid = data;
  }

  @Action
  public setSearchType(data: string) {
    this.SET_SEARCH_TYPE(data);
  }
}

export const LectureItemModule = getModule(LectureItem);
