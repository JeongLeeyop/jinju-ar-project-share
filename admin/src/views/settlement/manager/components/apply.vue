<template>
  <div>
    <div class="orderlist-wr">
      <p class="orderlist-ttl">정산요청</p>
      <div class="orderlist">
        <div v-for="order in orderList" class="list" :key="order.idx">
          <p class="product">
            {{ order.pickupDate }} {{ order.productName }}
          </p>
          <p class="price">x {{ order.productNum }}건</p>
        </div>
      </div>
    </div>
    <div class="orderlist-wr">
      <p class="orderlist-ttl">정산금액</p>
      <div class="orderlist">
        <div class="list">
          <p class="product">기본정산금</p>
          <p class="price">{{ settleSetting.amount | parseKrw }} 원</p>
        </div>
        <div class="list">
          <p class="product">총 정산금</p>
          <p class="price">{{ settleSetting.amount | parseKrw }} 원 x {{ orderList.length | parseKrw}}건</p>
        </div>
        <div class="list">
          <p class="price"><b style="font-weight: 700"> = {{ settleSetting.amount * orderList.length | parseKrw }}원</b></p>
        </div>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose()">취소</el-button>
      <el-button type="primary" @click="handleApplySettle()">요청</el-button>
    </span>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { applySettle } from '@/api/manager/settleApply';

@Component({})
export default class extends Vue {
  @Prop({ required: true }) private orderList!: any

  @Prop({ required: true }) private settleSetting!: any

  private handleClose() {
    this.$emit('close');
  }

  private getTotalProductNum() {
    let total = 0;
    this.orderList.forEach((order: any) => {
      total += order.productNum;
    });
    return total;
  }

  private handleApplySettle() {
    this.$confirm('판매상품을 정말 정산신청 하시겠습니까?', '정산신청', {
      confirmButtonText: '신청',
      cancelButtonText: '취소',
    }).then(() => {
      const idxList = this.orderList.map((o: any) => o.dayId);
      applySettle({ dayIdxList: idxList }).then(() => {
        this.$message.success('정산신청 되었습니다.');
        this.$emit('success');
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }
}
</script>
