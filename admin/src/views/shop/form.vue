<template>
  <div>
    <el-form ref="form" :model="form" :rules="rules" onsubmit="return false;">
      <div class="useradd-wrap shop">
        <div class="useradd-title">
          <div>
            <p class="tl">매장 {{ $route.params.idx ? '수정' : '추가' }}</p>
          </div>

          <div class="setting__btn__box">
            <button @click="$router.push({ name: 'ShopList' })" class="cancel">취소</button>
            <button class="save" @click="handleSave()">저장</button>
          </div>
        </div>

        <div v-loading="loading" class="useradd-section shop-add">
          <div class="useradd-content1">
            <p class="content-title">기본정보</p>
            <div class="content3-input">
              <div class="shop-input-wr">
                <label for="shopName">매장명</label>
                <el-form-item prop="name">
                  <el-input v-model="form.name" type="text" />
                </el-form-item>
              </div>
              <div class="shop-address">
                <label for="shopAddress">매장 주소</label>
                <el-form-item prop="address">
                  <el-input v-model="form.address" type="text" readonly />
                  <el-button class="useradd-btn shop" @click="handleVisibleAddress()">검색</el-button>
                  <div class="address-box">
                    <label for="shopAddress2"></label>
                    <el-input v-model="form.addressDetail" type="text" />
                  </div>
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopTel">전화번호</label>
                <el-form-item prop="tel">
                  <el-input v-model="form.tel" type="text" @input="filterConcatNumber" />
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopOpenDate">개업일</label>
                <el-form-item prop="openingDate">
                  <el-col :span="11">
                    <el-date-picker v-model="form.openingDate" type="date" style="width: 100%;"
                      value-format="yyyy-MM-dd" />
                  </el-col>
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopOpenDate">정산 은행</label>
                <el-form-item prop="bankCode">
                  <el-col :span="18">
                    <el-select v-model="form.bankCode">
                      <el-option v-for="bank in bankList" :key="bank.code" :label="bank.name" :value="bank.code" />
                    </el-select>
                  </el-col>
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopOpenDate">정산 계좌번호</label>
                <el-form-item prop="accountNumber">
                  <el-col :span="11">
                    <el-input v-model="form.accountNumber" placeholder="'-'를 빼고 입력해주세요." />
                  </el-col>
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopOpenDate">예금주</label>
                <el-form-item prop="holderName">
                  <el-col :span="11">
                    <el-input v-model="form.holderName" />
                  </el-col>
                </el-form-item>
              </div>
              <div class="shop-input-wr">
                <label for="shopOpenDate">최대 판매 수량</label>
                <el-form-item prop="maxHoldCnt">
                  <el-col :span="11">
                    <el-input type="number" v-model="form.maxHoldCnt" />
                  </el-col>
                </el-form-item>
              </div>
            </div>
          </div>

          <div class="useradd-content1">
            <div class="content3-input">
              <div class="shop-tab-wr">
                <label>픽업가능시간</label>
                <!--
                <el-form-item prop="openTime">
                  <el-time-picker v-model="form.shopPickUp" value-format="HH:mm:ss" />
                </el-form-item>
                -->
                <!--
                <el-tabs type="border-card">
                  <el-tab-pane label="월요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="monAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'mon')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.mon">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="화요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="tueAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'tue')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.tue">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="수요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="wedAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'wed')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.wed">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="목요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="thuAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'thu')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.thu">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="금요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="friAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'fri')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.fri">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="토요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="satAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'sat')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.sat">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="일요일">
                    <div class="shop-checkbox-wr pickup">
                      <el-checkbox v-model="sunAllChecked" class="all" label="전체선택" name="all"
                        @change="(e) => handleChangeAllChecked(e, 'sun')" />
                      <el-form-item>
                        <el-checkbox-group v-model="form.pickupTimes.sun">
                          <el-checkbox v-for="(item, index) in pickupTimes" :key="index" :label="item.value">{{ item.label
                          }}</el-checkbox>
                        </el-checkbox-group>
                      </el-form-item>
                    </div>
                  </el-tab-pane>
                </el-tabs>
                -->
                <div class="shop-checkbox-wr pickup" style="padding: 20px; border: 1px solid #efefef;">
                  <el-checkbox
                    v-model="monAllChecked"
                    class="all"
                    label="전체선택"
                    name="all"
                    @change="(e) => handleChangeAllChecked(e, 'mon')"
                  />
                  <el-form-item>
                    <el-checkbox-group v-model="form.pickupTimes.mon">
                      <el-checkbox
                        v-for="(item, index) in pickupTimes"
                        :key="index"
                        :label="item.value"
                      >
                        {{ item.label}}
                      </el-checkbox>
                    </el-checkbox-group>
                  </el-form-item>
                </div>
              </div>
              <div class="shop-input-wr">
                <label for="date">휴무일 설정 (기간)</label>
								<el-date-picker
                  type="daterange"
                  v-model="dateRange"
                  @change="handleDatePickerChange"
                  align="right"
                  range-separator="~"
                  value-format="yyyy-MM-dd"
                />
                <div class="date-info"> {{ curDateInfo }}</div>
							</div>
              <div class="shop-tab-wr">
                <label>휴무일 설정 (요일)</label>
                <div class="shop-checkbox-wr" style="padding: 20px; border: 1px solid #efefef;">
                    <el-form-item>
                        <el-checkbox label="월" v-model="form.holidays.mon"></el-checkbox>
                        <el-checkbox label="화" v-model="form.holidays.tue"></el-checkbox>
                        <el-checkbox label="수" v-model="form.holidays.wed"></el-checkbox>
                        <el-checkbox label="목" v-model="form.holidays.thu"></el-checkbox>
                        <el-checkbox label="금" v-model="form.holidays.fri"></el-checkbox>
                        <!-- <el-checkbox label="토" v-model="form.holidays.sat"></el-checkbox> -->
                        <!-- <el-checkbox label="일" v-model="form.holidays.sun"></el-checkbox> -->
                    </el-form-item>
                </div>
              </div>
            </div>
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
import { addShop, getShopDetail, updateShop } from '@/api/shop';
import { Form } from 'element-ui';
import { Vue, Component, Watch } from 'vue-property-decorator';
import { VueDaumPostcode } from 'vue-daum-postcode';
import { parseConcatNumber } from '@/utils/filters';
import { getBankList } from '@/api/bank';
import moment from 'moment';

@Component({
  components: {
    VueDaumPostcode,
  },
})

export default class extends Vue {
  @Watch('form.pickupTimes', { deep: true })
  private handleChangePickupTimes() {
    this.monAllChecked = this.form.pickupTimes.mon.length === this.pickupTimes.length;
    this.tueAllChecked = this.form.pickupTimes.tue.length === this.pickupTimes.length;
    this.wedAllChecked = this.form.pickupTimes.wed.length === this.pickupTimes.length;
    this.thuAllChecked = this.form.pickupTimes.thu.length === this.pickupTimes.length;
    this.friAllChecked = this.form.pickupTimes.fri.length === this.pickupTimes.length;
    this.satAllChecked = this.form.pickupTimes.sat.length === this.pickupTimes.length;
    this.sunAllChecked = this.form.pickupTimes.sun.length === this.pickupTimes.length;
  }

  mounted() {
    this.setForm();
    this.getBankList();
  }

  private dateRange: any[]= [];

  private loading = true;

  private form: any = {
    name: '',
    postCode: '',
    address: '',
    addressDetail: '',
    tel: '',
    openDate: '',
    bankCode: '',
    accountNumber: '',
    holderName: '',
    maxHoldCnt: '',
    pickupTimes: {
      mon: [],
      tue: [],
      wed: [],
      thu: [],
      fri: [],
      sat: [],
      sun: [],
    },
    holidayStartDate: '',
    holidayEndDate: '',
    holidays: {
      mon: false,
      tue: false,
      wed: false,
      thu: false,
      fri: false,
      sat: false,
      sun: false,
    },
  }

  private rules = {
    name: [
      { required: true, message: '매장명을 입력하세요.', trigger: ['blur', 'change'] },
    ],
    address: [
      { required: true, message: '매장 주소를 입력하세요.', trigger: ['blur', 'change'] },
    ],
    tel: [
      { required: true, message: '전화번호 입력하세요.', trigger: ['blur', 'change'] },
    ],
    /*
    openTime: [
      { required: true, message: '오픈 시간을 입력하세요.', trigger: ['blur', 'change'] },
    ],
    closeTime: [
      { required: true, message: '마감 시간을 입력하세요.', trigger: ['blur', 'change'] },
    ],
    */
    openingDate: [
      { required: true, message: '개업일을 입력하세요.', trigger: ['blur', 'change'] },
    ],
    bankCode: [
      { required: true, message: '은행사를 선택하세요.', trigger: ['blur', 'change'] },
    ],
    accountNumber: [
      { required: true, message: '계좌번호를 입력하세요.', trigger: ['blur', 'change'] },
    ],
    holderName: [
      { required: true, message: '예금주를 입력하세요.', trigger: ['blur', 'change'] },
    ],
  }

  private addressVisible = false;

  private monAllChecked = false;

  private tueAllChecked = false;

  private wedAllChecked = false;

  private thuAllChecked = false;

  private friAllChecked = false;

  private satAllChecked = false;

  private sunAllChecked = false;

  private pickupTimes = [
    { value: '08:00:00', label: '8시' },
    { value: '08:30:00', label: '8시 30분' },
    { value: '09:00:00', label: '9시' },
    { value: '09:30:00', label: '9시 30분' },
    { value: '10:00:00', label: '10시' },
    { value: '10:30:00', label: '10시 30분' },
    { value: '11:00:00', label: '11시' },
    { value: '11:30:00', label: '11시 30분' },
    { value: '12:00:00', label: '12시' },
    { value: '12:30:00', label: '12시 30분' },
    { value: '13:00:00', label: '13시' },
    { value: '13:30:00', label: '13시 30분' },
    { value: '14:00:00', label: '14시' },
    { value: '14:30:00', label: '14시 30분' },
    { value: '15:00:00', label: '15시' },
    { value: '15:30:00', label: '15시 30분' },
    { value: '16:00:00', label: '16시' },
    { value: '16:30:00', label: '16시 30분' },
    { value: '17:00:00', label: '17시' },
    { value: '17:30:00', label: '17시 30분' },
    { value: '18:00:00', label: '18시' },
    { value: '18:30:00', label: '18시 30분' },
    { value: '19:00:00', label: '19시' },
    { value: '19:30:00', label: '19시 30분' },
  ]

  private bankList = [];

  private handleDatePickerChange() {
    if (this.dateRange === null) {
      this.form.holidayStartDate = null;
      this.form.holidayEndDate = null;
    }
    [this.form.holidayStartDate] = this.dateRange;
    [, this.form.holidayEndDate] = this.dateRange;
  }

  private setForm() {
    if (this.$route.params.idx) {
      getShopDetail(this.$route.params.idx).then((res) => {
        this.form = res.data;
        if (this.form.holidays === null) this.form.holidays = {};
        if (res.data) this.dateRange = [moment(res.data.holidayStartDate).format('YYYY-MM-DD'), moment(res.data.holidayEndDate).format('YYYY-MM-DD')];
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        if (this.$route.params.idx) {
          updateShop(this.$route.params.idx, this.form).then(() => {
            this.$message.success('픽업매장이 수정되었습니다.');
            this.$router.push({ name: 'ShopList' });
            this.loading = false;
          });
        } else {
          addShop(this.form).then(() => {
            this.$message.success('픽업매장이 추가되었습니다.');
            this.$router.push({ name: 'ShopList' });
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

  private handleChangeAllChecked(allChecked: boolean, weekDay: string) {
    if (allChecked) {
      this.form.pickupTimes[`${weekDay}`] = this.pickupTimes.map((item: any) => item.value);
    } else {
      this.form.pickupTimes[`${weekDay}`] = [];
    }
  }

  private filterConcatNumber() {
    this.form.tel = parseConcatNumber(this.form.tel);
  }

  private getBankList() {
    getBankList().then((res) => {
      this.bankList = res.data;
    });
  }
}
</script>
