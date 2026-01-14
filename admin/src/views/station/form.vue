<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" onsubmit="return false;">
      <div class="useradd-wrap shop">
        <div class="useradd-title">
          <div>
            <p class="tl">거점 {{ $route.params.idx ? '수정' : '추가' }}</p>
          </div>

          <div class="setting__btn__box">
            <button @click="$router.push({ name: 'StationList' })" class="cancel">취소</button>
            <button class="save" @click="handleSave()">저장</button>
          </div>
        </div>

        <div v-loading="loading" class="useradd-section shop-add">
          <div class="useradd-content1">
            <p class="content-title">기본정보</p>
            <div class="content3-input">
              <div class="shop-input-wr">
                <label for="shopName">거점명</label>
                <el-form-item prop="name">
                  <el-input v-model="form.name" type="text" />
                </el-form-item>
              </div>
              <div class="shop-address">
                <label for="shopAddress">거점 주소</label>
                <el-form-item prop="address">
                  <el-input v-model="form.postCode" type="text" style="flex: 0 1 calc(100% - 335px);" readonly />
                  <el-button class="useradd-btn shop" @click="handleVisibleAddress()">검색</el-button>
                  <div class="address-box">
                  <el-input v-model="form.address" type="text" readonly />
                  </div>
                </el-form-item>
              </div>
              <div class="shop-radio-wr">
              <p class="content2-txt">사용여부</p>
              <el-radio-group v-model="form.useStatus">
                <el-radio :label="true">Y</el-radio>
                <el-radio :label="false">N</el-radio>
              </el-radio-group>
            </div>
            </div>
          </div>
          <div class="useradd-content1">
          </div>
        </div>
      </div>
      <el-dialog width="600px" title="주소검색" :visible.sync="addressVisible">
        <vue-daum-postcode style="width: 100%" @complete="handleComplete" />
      </el-dialog>
    </el-form>
  </div>
</template>

<script lang="ts">
import { addStation, getStationDetail, updateStation } from '@/api/station';
import { Form } from 'element-ui';
import { Vue, Component, Watch } from 'vue-property-decorator';
import { VueDaumPostcode } from 'vue-daum-postcode';
import { parseConcatNumber } from '@/utils/filters';
import moment from 'moment';

@Component({
  components: {
    VueDaumPostcode,
  },
})

export default class extends Vue {
  mounted() {
    this.setForm();
  }

  private loading = true;

  private form: any = {
    name: '',
    postCode: '',
    address: '',
    useStatus: '',
  }

  private rules = {
    name: [
      { required: true, message: '매장명을 입력하세요.', trigger: ['blur', 'change'] },
    ],
    address: [
      { required: true, message: '매장 주소를 입력하세요.', trigger: ['blur', 'change'] },
    ],
  }

  private addressVisible = false;

  private setForm() {
    if (this.$route.params.idx) {
      getStationDetail(this.$route.params.idx).then((res) => {
        this.form = res.data;
        this.loading = false;
      });
    } else {
      this.loading = false;
      this.form.useStatus = true;
    }
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        if (this.$route.params.idx) {
          updateStation(this.$route.params.idx, this.form).then(() => {
            this.$message.success('거점이 수정되었습니다.');
            this.$router.push({ name: 'StationList' });
            this.loading = false;
          });
        } else {
          addStation(this.form).then(() => {
            this.$message.success('거점이 추가되었습니다.');
            this.$router.push({ name: 'StationList' });
            this.loading = false;
          });
        }
      }
    });
  }

  private handleVisibleAddress() {
    this.addressVisible = !this.addressVisible;
  }

  private handleComplete(result: any) {
    this.form.postCode = result.zonecode;
    this.form.address = result.address;
    this.handleVisibleAddress();
  }

  private filterConcatNumber() {
    this.form.tel = parseConcatNumber(this.form.tel);
  }
}
</script>
