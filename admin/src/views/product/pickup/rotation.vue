<template>
	<div>
    <div class="useradd-wrap shop">
      <div class="useradd-title">
        <div>
          <p class="tl"> 로테이션 관리</p>
        </div>
        <div class="setting__btn__box">
          <button @click="handleGoList" class="cancel">취소</button>
          <button class="save" type="submit" @click="saveRotation">저장</button>
        </div>
      </div>

      <div v-loading="loading" class="rotation-section">
        <div class="useradd-content1 rotation1">
          <p class="content-title">로테이션 관리</p>
          <div class="content3-input">
            <el-row class="tac">
              <el-col :span="8">
                <el-menu default-active="1" class="el-menu-vertical-demo">
                  <el-menu-item index="1" @click="handleWeekNum(1)"><i class="el-icon-arrow-right"></i>1주차</el-menu-item>
                  <el-menu-item index="2" @click="handleWeekNum(2)"><i class="el-icon-arrow-right"></i>2주차</el-menu-item>
                  <el-menu-item index="3" @click="handleWeekNum(3)"><i class="el-icon-arrow-right"></i>3주차</el-menu-item>
                  <el-menu-item index="4" @click="handleWeekNum(4)"><i class="el-icon-arrow-right"></i>4주차</el-menu-item>
                  <el-menu-item index="5" @click="handleWeekNum(5)"><i class="el-icon-arrow-right"></i>5주차</el-menu-item>
                </el-menu>
              </el-col>
            </el-row>
          </div>
        </div>
        <div class="useradd-content1 rotation2">
          <p class="content-title">상품 관리</p>
          <div class="content3-input">
            <el-transfer
              filterable
              filter-placeholder="상품검색"
              v-model="rotationProducts"
              :data="products"
              fit
              :props="{
                key: 'idx',
                label: 'name'
              }"
              >
            </el-transfer>
          </div>
        </div>
      </div>
    </div>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getProductList, saveRotation } from '@/api/productRotation';

@Component({
  name: 'ProductRotation',
})

export default class extends Vue {
  private products = [];

  private rotationProducts: any = [];

  private weekNum = 1;

  private loading = true;

  mounted() {
    this.handleProductList();
  }

  handleProductList() {
    this.loading = true;
    this.rotationProducts = [];

    getProductList(this.weekNum).then((res) => {
      this.loading = false;
      this.products = res.data.products;
      res.data.rotationProducts.forEach((element: any) => {
        this.rotationProducts.push(element.idx);
      });
    });
  }

  private handleGoList() {
		this.$router.push({ name: 'ProductIndex2' });
	}

  private handleWeekNum(weekNum: number) {
    this.weekNum = weekNum;
    this.handleProductList();
  }

  private saveRotation() {
    saveRotation(this.weekNum, {
      productsIdx: this.rotationProducts,
    }).then(() => {
      this.$message({
        message: '저장 완료',
        type: 'success',
      });
    });
  }
}
</script>
