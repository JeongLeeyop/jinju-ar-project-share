<template>
 <el-popover v-model="showPopover" placement="bottom-end" width="450" trigger="click" popper-class="alarm" place-:popper-append-to-body="false" :title="alarmList.length > 0 ? '띵동! 알림이 도착했어요 🎶' : ''">
    <div @click="showPopover = false" class="alarm-close">
          <i class="el-icon-close"></i>
    </div>
    <div class="read-all-btn">
      <el-buttom @click="handleReadAll">모두 읽음</el-buttom>
    </div>
    <div v-if="alarmList.length > 0">
      <div class="alarm-item-day-wr" v-for="(item, index) in alarmList" :key="index">
        <div class="alarm-item-day">{{ item[0].createDate | parseDate('YYYY-MM-DD') }}</div>
        <div v-if="item.length > 0">
          <a :href="item2.link" class="alarm-item-wr" v-for="item2 in item" :key="item2.id">
            <div class="alarm-info">
              <div class="title">{{ item2.title }}</div>
              <div class="date">{{ item2.createDate | parseDate('YYYY-MM-DD HH:mm') }}</div>
            </div>
            <div class="alarm-content">{{ item2.content }}</div>
          </a>
        </div>
      </div>
    </div>
    <div class="empty" v-else>
      <div class="logo">
        <!-- <el-button @click="handleHome" class="alram-btn" icon="el-icon-bell"></el-button> -->
      </div>
      <div class="txt">도착한 알림이 없어요</div>
    </div>
    <!-- <el-button type="text" slot="reference" class="diary-header__user alarm"></el-button> -->
    <el-button type="text" slot="reference" class="diary-header__user alarm alram-btn" :icon="newCount > 0 ? 'el-icon-message-solid' : 'el-icon-bell'">
      <div class="cnt" v-if="newCount > 0">{{ newCount }}</div>
    </el-button>
  </el-popover>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { getAlarmList, readAll } from '@/api/pushAlarm';

@Component({
  name: 'Component1',
  components: {
  },
})
export default class extends Vue {
  mounted() {
    this.getAlarmList();
  }

  private showPopover = false;

  private data: any = [];

  private alarmList: any[][] = [];

  private newCount = 0;

  private async getAlarmList() {
    await getAlarmList({ page: 0, size: 5 }).then((res: any) => {
      this.data = res.data.alarmList;
      this.newCount = res.data.newCount;
    });
    const itemMap: Map<string, any[]> = new Map();
    this.data.content.forEach((item: any) => {
      const date: string = item.createDate.substring(0, 10); // 날짜 부분만 추출
      if (!itemMap.has(date)) {
        itemMap.set(date, []);
      }
      const itemList: any[] | undefined = itemMap.get(date);
      if (itemList) {
        itemList.push(item);
      }
    });
    this.alarmList = Array.from(itemMap.values());
  }

  private handleReadAll() {
    readAll().then((res) => {
      this.getAlarmList();
      this.showPopover = false;
    });
  }
}
</script>

<style>
</style>
