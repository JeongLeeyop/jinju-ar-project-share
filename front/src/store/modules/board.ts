import {
  VuexModule,
  Module,
  Action,
  Mutation,
  getModule,
} from 'vuex-module-decorators';
import store from '@/store';

@Module({ dynamic: true, store, name: 'board' })
class Board extends VuexModule {
  public form: any = {};

  @Mutation
  private SET_FORM(data: any) {
    this.form = data;
  }

  @Action
  public setForm(data: any) {
    this.SET_FORM(data);
  }
}

export const BoardModule = getModule(Board);
