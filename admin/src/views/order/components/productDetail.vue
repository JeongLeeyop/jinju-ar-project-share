<template>
  <div>
    <div class="orderlist-wr item-summary" v-loading="loading">
      <div class="orderlist">
        <div v-for="(item, index) in datas" :key="index" class="list">
          <p class="product">{{ item.stationName }}</p>
          <p class="price"> X {{ item.totalProductNum | parseKrw }}개 </p>
        </div>
      </div>
    </div>
    <div slot="footer" class="dialog-footer">
      <el-button @click="$emit('close')">닫기</el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { getProductOrderDetailByProduct } from '@/api/productOrder';

@Component({
	components: {
	},
})

export default class extends Vue {
  @Prop({ required: true }) private selectedProduct!: any;

  @Prop({ required: true }) private listQuery!: any;

  mounted() {
    this.getProductOrderDetailByProduct();
  }

  private datas = [];

  private loading = true;

  private getProductOrderDetailByProduct() {
    this.loading = true;
    getProductOrderDetailByProduct(this.selectedProduct.productId, this.listQuery).then((res) => {
      this.datas = res.data;
      this.loading = false;
    });
  }
}
</script>
