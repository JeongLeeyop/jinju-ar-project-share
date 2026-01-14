<template>
  <div>

    <div class="orderlist-wr">
      <div class="item">
        <div class="orderlist">
          <div v-for="(item, index) in datas" :key="index" class="list">
            <p class="product">{{ item.productName }} x {{ item.totalProductNum | parseKrw }}개</p>
            <p class="price">{{ item.totalAmount | parseKrw }}원</p>
            <p>주문접수: {{ item.totalHoldCount | parseKrw }}개 | 배달중: {{ item.totalDeliveryCount | parseKrw }}개 | 배달완료: {{ item.totalDoneCount | parseKrw }}개</p>
          </div>
          <div class="totalCnt">
            <p class="price">x{{ getTotalProductNum() | parseKrw }}개</p>
          </div>
        </div>
        <p class="orderlist-result">{{ getTotalAmount() | parseKrw }}원</p>
      </div>
    </div>
    <el-form :model="form">
      <el-form-item label="입고상태">
        <el-select v-model="form.receiveStatus" placeholder="입고상태 선택">
          <el-option label="주문접수" :value="0"></el-option>
          <el-option label="배달중" :value="1"></el-option>
          <el-option label="배달완료" :value="2"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="$emit('close')">닫기</el-button>
      <el-button type="primary" @click="handleUpdateStatus()">저장</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { getProductOrderDetailByShop, updateProductReceiveStatus } from '@/api/productOrder';

@Component({
  components: {
  },
})
export default class extends Vue {
  @Prop({ required: true }) private selectedShop!: any;

  @Prop({ required: true }) private listQuery!: any;

  mounted() {
    this.getProductOrderDetailByProduct();
  }

  private datas = [];

  private loading = true;

  private form = {
    receiveStatus: null,
  }

  private getProductOrderDetailByProduct() {
    this.loading = true;
    getProductOrderDetailByShop(this.selectedShop.shopId, this.listQuery).then((res) => {
      this.datas = res.data;
      this.loading = false;
    });
  }

  private getTotalProductNum() {
    let productNum = 0;
    this.datas.forEach((data: any) => {
      productNum += data.totalProductNum;
    });
    return productNum;
  }

  private getTotalAmount() {
    let amount = 0;
    this.datas.forEach((data: any) => {
      amount += data.totalAmount;
    });
    return amount;
  }

  private handleUpdateStatus() {
    if (this.form.receiveStatus == null) {
      this.$message.warning('입고상태를 선택해주세요.');
      return;
    }
    this.$confirm('정말 입고상태를 수정하시겠습니까?', '입고상태 수정', {
      confirmButtonText: '수정',
      cancelButtonText: '취소',
    }).then(() => {
      updateProductReceiveStatus(this.listQuery, { ...this.form, idxList: [this.selectedShop.shopId] }).then(() => {
        this.$message.success('입고상태가 수정되었습니다.');
        this.$emit('close');
      });
    });
  }
}
</script>
