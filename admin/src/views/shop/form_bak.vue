<!-- <template>
  <div>
    <div class="useradd-wrap shop">
      <div class="useradd-title">
        <div>
          <p class="tl">상품 {{ $route.params.uid ? '수정' : '추가' }}</p>
        </div>

        <div class="setting__btn__box">
          <button @click="$router.push({ name: 'ShopItemList' })" class="cancel">취소</button>
          <button class="save" @click="handleSave()">저장</button>
        </div>
      </div>

      <el-form ref="form" :model="form" :rules="rules" onsubmit="return false" label-width="120px" style="text-align: left" v-loading="loading">
        <div class="useradd-section">
          <div class="useradd-content1">
            <p class="content-title">상품정보</p>
            <el-form-item prop="thumbUid" label="상품 이미지">
              <el-upload
                class="avatar-uploader"
                :action="`${apiUrl}/shop/item/upload`"
                :on-success="uploadSuccess"
                :before-upload="beforeUpload"
                :on-remove="removeFile"
                :show-file-list="false"
              >
                <img
                  v-if="form.thumbUid"
                  :src="`${apiUrl}/attached-file/${form.thumbUid}`"
                  class="avatar"
                >
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>
            <el-form-item prop="name" label="상품명">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item prop="description" label="상품 설명">
              <el-input type="textarea" v-model="form.description" />
            </el-form-item>
            <el-form-item prop="calorie" label="칼로리">
              <el-input-number v-model="form.calorie" :controls="false" :min="0" />
            </el-form-item>
            <el-form-item prop="weight" label="무게">
              <el-input-number v-model="form.weight" :controls="false" :min="0" />
            </el-form-item>
            <el-form-item prop="material" label="재료">
              <el-input v-model="form.material" />
            </el-form-item>
            <el-form-item prop="amount" label="재고수량">
              <el-input-number v-model="form.amount" :controls="false" :min="0" />
            </el-form-item>
            <el-form-item prop="price" label="판매가">
              <el-input-number v-model="form.price" :controls="false" :min="0" />
            </el-form-item>
            <el-form-item prop="dietType" label="식단형태">
              <el-radio-group v-model="form.dietType">
                <el-radio-button label="샐러드"></el-radio-button>
                <el-radio-button label="도시락"></el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="dietPurpose" label="식단 목적">
              <el-radio-group v-model="form.dietPurpose">
                <el-radio-button label="체중감량"></el-radio-button>
                <el-radio-button label="영양밸런스"></el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="dietPrecaution" label="식단 유의사항">
              <el-radio-group v-model="form.dietPrecaution">
                <el-radio-button label="없음"></el-radio-button>
                <el-radio-button label="비건"></el-radio-button>
                <el-radio-button label="알레르기"></el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item prop="sellStatus" label="판매여부">
              <el-switch v-model="form.sellStatus" />
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts">
import { addShopItem, getShopItemDetail, updateShopItem } from '@/api/product';
import { Form } from 'element-ui';
import { Vue, Component, Watch } from 'vue-property-decorator';

@Component({
  components: {
  },
})

export default class extends Vue {
  mounted() {
    this.setForm();
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private loading = true;

  private form = {
    name: '', // 상품명
    description: '', // 설명
    thumbUid: '', // 썸네일 파일 고유값
    calorie: 0, // 칼로리
    weight: 0, // 무게
    material: '', // 재료
    amount: 0, // 재고량
    price: 0, // 판매가
    dietType: '', // 식단형태
    dietPurpose: '', // 식단관리 목적
    dietPrecaution: '', // 식단 제공 시 유의사항
    sellStatus: true, // 판매상태
  };

  private rules = {
    name: [
      { required: true, message: '상품명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    thumbUid: [
      { required: true, message: '상품 이미지를 등록해주세요.', trigger: ['blur', 'change'] },
    ],
    dietType: [
      { required: true, message: '식단 형태를 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    dietPurpose: [
      { required: true, message: '식단 목적을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
    dietPrecaution: [
      { required: true, message: '식단 유의사항을 선택해주세요.', trigger: ['blur', 'change'] },
    ],
  }

  private setForm() {
    if (this.$route.params.uid) {
      getShopItemDetail(this.$route.params.uid).then((res) => {
        this.form = res.data;
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        if (this.$route.params.uid) {
          updateShopItem(this.$route.params.uid, this.form).then(() => {
            this.$message.success('상품이 수정되었습니다.');
            this.$router.push({ name: 'ShopItemList' });
          });
        } else {
          addShopItem(this.form).then(() => {
            this.$message.success('상품이 추가되었습니다.');
            this.$router.push({ name: 'ShopItemList' });
          });
        }
      }
    });
  }

  private beforeUpload(file: any) {
    const isImage = file.type.split('/')[0] === 'image';
    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isImage) this.$message.error('이미지 파일을 업로드해주세요.');
    if (!isLt5M) this.$message.error('이미지 용량이 5MB를 초과합니다.');
    return isImage && isLt5M;
  }

  private uploadSuccess(res: any, file: any) {
    this.form.thumbUid = res.uid;
  }

  private removeFile() {
    this.form.thumbUid = '';
  }
}
</script>

<style lang="scss">
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> -->
