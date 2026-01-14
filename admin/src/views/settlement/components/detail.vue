<template>
  <div>
    <div v-loading="loading" class="orderlist-wr">
      <p class="orderlist-ttl">{{ apply.shopName }}</p>
      <div class="orderlist">
        <div v-for="(item, index) in apply.items" :key="index" class="list">
          <p class="product">{{ item.productName }} x {{ item.productNum | parseKrw }}개</p>
          <p class="price">{{ item.amount | parseKrw }}원</p>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <el-button @click="$emit('close')">닫기</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { getSettleApply } from '@/api/settleApply';

@Component({
  components: {
  },
})
export default class extends Vue {
  @Prop({ required: true }) private idx!: any

  mounted() {
    this.getSettleApply();
  }

  private loading = true;

  private apply = {
    idx: null,
    shopName: '',
    totalSale: '',
    totalSettle: '',
    totalProductNum: '',
    applyDate: '',
    approvalStatus: '',
    approvalDate: '',
    items: [],
  };

  private getSettleApply() {
    this.loading = true;
    getSettleApply(this.idx).then((res) => {
      this.apply = res.data;
      this.loading = false;
    });
  }
}
</script>
