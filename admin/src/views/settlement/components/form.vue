<template>
  <div>
    <div class="orderlist-wr">
      <p class="orderlist-ttl">정산하기</p>
      <div class="orderlist">
        <div v-for="(apply, index) in applyList" :key="index" class="list">
          <p class="product">매장명 : {{ apply.shopName }}</p>
          <p class="price">x {{ apply.totalProductNum | parseKrw }}개 = {{ apply.totalSettle | parseKrw }}원</p>
        </div>
      </div>
    </div>
    <div class="orderlist-wr">
      <p class="orderlist-ttl">정산금액</p>
      <div class="orderlist">
        <!--
        <div class="list">
          <p class="product">기본정산금</p>
          <p class="price">800 원</p>
        </div>
        <div class="list">
          <p class="product">총 정산금</p>
          <p class="price">800 원 x 117건</p>
        </div>
        <div class="list">
          <p class="price"><b style="font-weight: 700"> = {{ 800*117 }}원</b></p>
        </div>
        -->
        <div class="list">
          <p style="font-weight: bold; font-size: 16px;">총 상품판매: {{ getTotalProductNum() | parseKrw }}개</p>
          <p style="font-weight: bold; font-size: 16px;">총 정산액: {{ getTotalSettle() | parseKrw }}원</p>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <el-button @click="$emit('close')">취소</el-button>
      <el-button type="primary" @click="handleSettle()">정산하기</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { approvalSettle } from '@/api/settleApply';
import { parseKrw } from '@/utils/filters';

@Component({
  components: {
  },
})
export default class extends Vue {
  @Prop({ required: true }) private applyList!: any

  private handleSettle() {
    this.$confirm(`정말 ${this.getTotalSettle()}원(${this.getTotalProductNum()}개)을 정산하시겠습니까?`, '정산', {
      confirmButtonText: '정산하기',
      cancelButtonText: '취소',
    }).then(() => {
      const idxList = this.applyList.map((apply: any) => apply.idx);
      approvalSettle({ applyIdxList: idxList }).then(() => {
        this.$message.success('성공적으로 정산이 처리되었습니다.');
        this.$emit('success');
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  private getTotalSettle() {
    let total = 0;
    this.applyList.forEach((apply: any) => {
      total += apply.totalSettle;
    });
    return total;
  }

  private getTotalProductNum() {
    let total = 0;
    this.applyList.forEach((apply: any) => {
      total += apply.totalProductNum;
    });
    return total;
  }
}
</script>
