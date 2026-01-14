<template>
  <div v-loading="loading">
    <div class="orderlist-wr">
      <div class="orderlist-info">
        <div class="username">
          <p class="orderlist-ttl">이름 : </p>
          <p class="orderlist-ttl">{{ orderDetail[0].group.user.actualName }}</p>
        </div>
        <div class="shopname">
          <p class="orderlist-ttl"> 매장명 : </p>
          <p class="orderlist-ttl"> {{ orderDetail[0].group.shop.name }}</p>
        </div>
        <div class="date">
          <p class="orderlist-ttl">[{{ orderDetail[0].weekNum }}주차]</p>
          <p class="orderlist-ttl">{{ orderDetail[0].pickupDate | parseDate('YYYY년 MM월 DD일 (ddd)') }}</p>
        </div>
      </div>
      <div class="orderlist" v-for="item in orderDetail" :key="item.idx">
        <div class="list">
          <p class="product">{{ item.productName }} x {{ item.productNum }}개</p>
          <p class="price">{{ item.amount | parseKrw }}원</p>
        </div>
        <!--
        <div class="totalCnt">
          <p class="price">x4개</p>
        </div>
        -->
      </div>
      <p class="orderlist-result">{{ amount | parseKrw }}원</p>
    </div>
    <el-form>
      <el-form-item label="주문상태">
        <el-select v-model="pickupStatus" placeholder="주문상태를 변경해주세요">
          <!--
          <el-option label="주문접수" :value="0"></el-option>
          <el-option label="배달중" :value="1"></el-option>
          -->
          <el-option label="픽업대기" :value="0"></el-option>
          <el-option label="픽업완료" :value="1"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div class="dialog-footer">
      <el-button @click="$emit('close')">취소</el-button>
      <el-button type="primary" @click="handleUpdateOrder()">저장</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import {
  getProductOrderDetail,
  updateProductOrderStatus,
  getProductOrderDetailByDay,
  updateProductOrderStatusByDay,
} from '@/api/productOrder';

@Component
export default class extends Vue {
  @Prop({ required: true }) private selectedOrder!: any;

  mounted() {
    this.getOrderDetail();
  }

  private loading = true;

  private orderDetail: any = [];

  private amount = 0;

  private pickupStatus = 0;

  private getOrderDetail() {
    this.loading = true;
    getProductOrderDetailByDay(this.selectedOrder.dayId).then((res) => {
      this.loading = false;
      this.orderDetail = res.data;
      this.amount = 0;
      if (this.orderDetail.length > 0) {
        for (let i = 0; i < this.orderDetail.length; i += 1) {
          this.amount += this.orderDetail[i].amount;
        }
        this.pickupStatus = this.orderDetail[0].pickupStatus;
      }
    }).catch((error) => {
      this.$emit('close');
    });
  }

  private handleUpdateOrder() {
    updateProductOrderStatusByDay(this.selectedOrder.dayId, { pickupStatus: this.pickupStatus }).then(() => {
      this.$message.success('주문상태가 변경되었습니다.');
      this.$emit('update');
    });
  }
}
</script>
