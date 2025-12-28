import {
  VuexModule,
  Module,
  Action,
  Mutation,
  getModule,
} from 'vuex-module-decorators';
import store from '@/store';

@Module({ dynamic: true, store, name: 'reservationItem' })
class ReservationItem extends VuexModule {
  public form: any = {};

  public updateScheduleDate = '0';

  public isNew = false;

  public scheduleType: number|null = null;

  @Mutation
  private SET_FORM(data: any) {
    this.form = data;
  }

  @Mutation
  private SET_SCHEDULETYPE(data: number | null) {
    this.scheduleType = data;
  }

  @Mutation
  private SET_ISNEW(data: boolean) {
    this.isNew = data;
  }

  @Mutation
  private SET_UPDATESCHEDULEDATE(date: string) {
    this.updateScheduleDate = date;
  }

  @Action
  public setUpdateScheduleDate(date: string) {
    this.SET_UPDATESCHEDULEDATE(date);
  }

  @Action
  public setIsNew(data: any) {
    this.SET_ISNEW(data);
  }

  @Action
  public setScheduleType(data: number | null) {
    this.SET_SCHEDULETYPE(data);
  }

  @Action
  public setForm(data: any) {
    this.SET_FORM(data);
  }
}

export const ReservationItemModule = getModule(ReservationItem);
