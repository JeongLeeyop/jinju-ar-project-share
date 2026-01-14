<template>
  <div v-loading="loading">
    <div class="orderlist-wr">
      <div class="orderlist-info">
        <div class="username">
          <p class="orderlist-ttl">이름 : </p>
          <p class="orderlist-ttl">{{ orderDetail.group.user.actualName }}</p>
        </div>
        <div class="shopname">
          <p class="orderlist-ttl"> 매장명 : </p>
          <p class="orderlist-ttl"> {{ orderDetail.group.shop.name }}</p>
        </div>
        <div class="date">
          <p class="orderlist-ttl">[{{ orderDetail.weekNum }}주차]</p>
          <p class="orderlist-ttl">{{ orderDetail.pickupDate | parseDate('YYYY년 MM월 DD일 (ddd)') }}</p>
        </div>
      </div>
      <div class="orderlist">
        <div class="list">
          <p class="product">{{ orderDetail.productName }} x {{ orderDetail.productNum }}개</p>
          <!--<p class="price">{{ selectedOrder.amount | parseKrw }}원</p>-->
        </div>
        <!--
        <div class="totalCnt">
          <p class="price">x4개</p>
        </div>
        -->
      </div>
      <p class="orderlist-result">{{ orderDetail.amount | parseKrw }}원</p>
    </div>
    <div class="dialog-footer">
      <el-button @click="$emit('close')">확인</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { getProductOrderDetail } from '@/api/productOrder';

@Component
export default class extends Vue {
  @Prop({ required: true }) private selectedOrder!: any;

  mounted() {
    this.getOrderDetail();
  }

  private loading = true;

  private orderDetail = null;

  private getOrderDetail() {
    this.loading = true;
    getProductOrderDetail(this.selectedOrder.idx).then((res) => {
      this.loading = false;
      this.orderDetail = res.data;
    }).catch((error) => {
      this.$emit('close');
    });
  }
}
</script>
