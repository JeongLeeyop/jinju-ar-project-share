<template>
  <div v-loading="loading">
    <div class="orderlist-wr">
      <div v-for="(orderDetail, index) in orderList" :key ="index" class="orderlist-info">
        <!-- <div class="username"> -->
          <!-- <p class="orderlist-ttl">상품명 : </p> -->
          <!-- <p class="orderlist-ttl">{{ orderDetail.productName }}</p> -->
        <!-- </div> -->
        <!-- <div class="username">
          <p class="orderlist-ttl"> 수량 : </p>
          <p class="orderlist-ttl">{{ orderDetail.productNum }}</p>
        </div>
        <div class="username">
          <p class="orderlist-ttl"> 가격 : </p>
          <p class="orderlist-ttl">{{ orderDetail.amount }}</p>
        </div> -->
        <div class="orderlist">
        <div class="list" style="display: flex;" >
          <p style="flex:0 1 50%" class="product">{{ orderDetail.productName }} x {{ orderDetail.productNum }}개</p>
          <p class="price">{{ orderDetail.amount | parseKrw }}원</p>
        </div>
        <!--
        <div class="totalCnt">
          <p class="price">x4개</p>
        </div>
        -->
      </div>
      <br>
    </div>
    <p class="orderlist-result">{{ selectedOrder.amount | parseKrw }}원</p>
    </div>
    <div class="dialog-footer">
      <el-button @click="$emit('close')">확인</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { getProductOrderDetailByDay } from '@/api/productOrder';

@Component
export default class extends Vue {
  @Prop({ required: true }) private selectedOrder!: any;

  mounted() {
    this.getSettleList();
  }

  private loading = true;

  private orderList = [];

  private getSettleList() {
    this.loading = true;
    getProductOrderDetailByDay(this.selectedOrder.dayId).then((res) => {
      this.loading = false;
      this.orderList = res.data;
    }).catch((error) => {
      this.$emit('close');
    });
  }
}
</script>
