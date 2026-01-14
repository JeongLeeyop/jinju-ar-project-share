<template>
  <div class="useradd-wrap challenge" style="overflow: scroll;">
    <div class="useradd-title">
      <div>
        <p class="tl">챌린지 {{ $route.params.uid ? '수정' : '추가' }}</p>
      </div>

      <div class="setting__btn__box">
        <button @click="handleCancel()" class="cancel">취소</button>
        <button class="save" @click="handleSave()">저장</button>
      </div>
    </div>
    <el-form v-loading="loading" ref="form" :model="form" :rules="rules" onsubmit="return false">
      <div class="useradd-section">
        <div class="useradd-content1">
          <p class="content-title">챌린지 관리</p>
          <div class="content1-input">
            <div class="shop-input-wr">
              <label for="couponName">챌린지 사진</label>
              <el-form-item>
                <el-upload style="max-width: 800px;" :action="`${apiUrl}/challenge/upload`" :headers="requestHeaders"
                  :on-exceed="handleExceed" :on-success="handleSuccessUpload" :limit="5" :file-list="form.mainImageList"
                  :on-remove="handleRemove" list-type="picture" :before-upload="(file) => handleBeforeUpload(file, true)">
                  <el-button size="small" type="info" icon="el-icon-upload2">
                    파일 선택
                  </el-button>
                </el-upload>
              </el-form-item>
              <div slot="tip" class="el-upload__tip">
                최대 5개 까지 업로드 가능합니다.
              </div>
            </div>
            <div class="shop-input-wr">
              <label for="couponName">챌린지명</label>
              <el-form-item prop="title">
                <el-input placeholder="" type="text" id="title" v-model="form.title" />
              </el-form-item>
            </div>
            <div class="shop-input-wr">
                <label for="shopOpenDate">카테고리</label>
                <el-form-item>
                  <el-col>
                    <el-select style="width: 100%" v-model="form.categoryUid">
                      <el-option v-for="category in categoryList" :key="category.uid" :label="category.name" :value="category.uid" />
                    </el-select>
                  </el-col>
                </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">내용</label>
              <el-form-item prop="content">
                <el-input type="textarea" :rows="5" size="medium" v-model="form.content"></el-input>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">인증방식</label>
              <el-form-item prop="authMethodInfo">
                <el-input type="textarea" :rows="5" size="medium" v-model="form.authMethodInfo"></el-input>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">인증방식 예시</label>
              <el-form-item>
                <el-upload style="max-width: 800px;" :action="`${apiUrl}/challenge/upload`" :headers="requestHeaders"
                  :on-exceed="handleExceed2" :on-success="handleSuccessUpload2" :limit="3"
                  :file-list="form.sampleImageList" :on-remove="handleRemove2" list-type="picture"
                  :before-upload="(file) => handleBeforeUpload(file, true)">
                  <el-button size="small" type="info" icon="el-icon-upload2">
                    파일 선택
                  </el-button>
                </el-upload>
              </el-form-item>
              <div slot="tip" class="el-upload__tip">
                최대 3개 까지 업로드 가능합니다.
              </div>
            </div>
          </div>
        </div>
        <div class="useradd-content1">
          <p class="content-title">챌린지 관리</p>
          <div class="content1-input">
            <div class="shop-input-wr">
              <label for="">적립금</label>
              <el-form-item prop="dailyReward">
                <el-input-number v-model="form.dailyReward" id="dailyReward" :min="0" /> 포인트
              </el-form-item>
            </div>
            <div class="shop-date-wr">
              <label for="downloadDateRange">챌린지 기간</label>
              <el-form-item prop="challengeRange">
                <el-date-picker v-model="form.challengeRange" type="datetimerange" align="right" start-placeholder="시작일"
                  end-placeholder="종료일" value-format="yyyy-MM-ddTHH:mm:ss" @change="handleChangeChallengeRange" />
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">총 일수</label>
              <el-form-item prop="totalDay">
                <el-input-number v-model="form.totalDay" id="totalDay" :min="0" /> 일
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
      </div>
    </el-form>
  </div>
</template>

<script lang="ts">
import { Form } from 'element-ui';
import { Component, Vue } from 'vue-property-decorator';
import { getToken } from '@/utils/cookies';
import {
  getChallengeDetail,
  addChallenge,
  updateChallenge,
  getChallengeCategory,
} from '@/api/challenge';

@Component({
  components: {
  },
})
export default class extends Vue {
  async mounted() {
    this.getChallengeCategory();
    if (this.$route.params.uid) {
      await this.setForm();
      this.form.mainImageList = this.getFileList();
      this.form.sampleImageList = this.getFileList2();
    }
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private handleSuccessUpload(res: any) {
    (this.form.mainImageList as any[]).push({ fileUid: res.uid, file: res });
    this.form.mainImageList = this.getFileList();
  }

  private handleExceed(files: any, fileList: any[]) {
    this.$message.warning(`최대 첨부가능한 수는 5개 입니다, 현재 ${files.length}개의 파일이 업로드 되었습니다.`);
  }

  private getFileList() {
    const fileList: any[] = [];
    this.form.mainImageList.forEach((mainFile: any) => {
      if (mainFile.file) {
        fileList.push({ fileUid: mainFile.fileUid, name: mainFile.file.originalName, url: `${this.apiUrl}/attached-file/${mainFile.fileUid}` });
      } else {
        fileList.push(mainFile);
      }
    });
    return fileList;
  }

  private handleRemove(file: any, fileList: any[]) {
    const fileIndex = this.form.mainImageList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
    if (fileIndex > -1) this.form.mainImageList.splice(fileIndex, 1);
  }

  private handleSuccessUpload2(res: any) {
    (this.form.sampleImageList as any[]).push({ fileUid: res.uid, file: res });
    this.form.sampleImageList = this.getFileList2();
  }

  private handleExceed2(files: any, fileList: any[]) {
    this.$message.warning(`최대 첨부가능한 수는 3개 입니다, 현재 ${files.length}개의 파일이 업로드 되었습니다.`);
  }

  private getFileList2() {
    const fileList: any[] = [];
    this.form.sampleImageList.forEach((sampleFile: any) => {
      if (sampleFile.file) fileList.push({ fileUid: sampleFile.fileUid, name: sampleFile.file.originalName, url: `${this.apiUrl}/attached-file/${sampleFile.fileUid}` });
      else fileList.push(sampleFile);
    });
    return fileList;
  }

  private handleRemove2(file: any, fileList: any[]) {
    const fileIndex = this.form.sampleImageList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
    if (fileIndex > -1) this.form.sampleImageList.splice(fileIndex, 1);
  }

  private handleBeforeUpload(uploadFile: File, isPhoto: boolean) {
    if (isPhoto) {
      const isImageFile = uploadFile.type.split('/')[0] === 'image';
      if (!isImageFile) {
        this.$message.warning('이미지 파일만 업로드 가능합니다.');
        return false;
      }
    }
    return true;
  }

  private loading = false;

  private form: any = {
    uid: '',
    title: '',
    content: '',
    dailyReward: '',
    authMethodInfo: '',
    categoryUid: '',
    challengeCategory: '',
    mainImageList: [],
    sampleImageList: [],
    useStatus: true,
    challengeRange: '',
    startDate: '',
    endDate: '',
    totalDay: '',
    createDate: '',
  }

  private categoryList: any = {}

  /* eslint-disable */
  private rules = {
    title: [
      { required: true, message: '챌린지명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    content: [
      { required: true, message: '내용을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    authMethodInfo: [
      { required: true, message: '인증방식을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    dailyReward: [
      { required: true, message: '적립금을 입력해주세요.', trigger: ['blur', 'change'] },
      // { min: 1, max: 100, message: '할인율을 1에서 100퍼센트로 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    // challengeRange: [
    // { required: true, message: '지급기간을 입력해주세요.', trigger: ['blur', 'change'] },
    // ],
  }
  /* eslint-enable */

  private async setForm() {
    this.loading = true;
    await getChallengeDetail(this.$route.params.uid).then((res) => {
      this.form = {
        ...res.data,
        challengeRange: [res.data.startDate, res.data.endDate],
      };
      this.loading = false;
    });
  }

  private getChallengeCategory() {
    this.loading = true;
    getChallengeCategory().then((res) => {
      this.categoryList = res.data;
      this.loading = false;
    });
  }

  private handleCancel() {
    this.$router.push({ name: 'ChallengeList' });
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        if (this.$route.params.uid) {
          updateChallenge(this.$route.params.uid, this.form).then(() => {
            this.$message.success('챌린지가 수정되었습니다.');
            this.$router.push({ name: 'ChallengeList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        } else {
          addChallenge(this.form).then(() => {
            this.$message.success('챌린지가 추가되었습니다.');
            this.$router.push({ name: 'ChallengeList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        }
      }
    });
  }

  /* eslint-disable */
  private handleChangeChallengeRange() {
    if (this.form.challengeRange && this.form.challengeRange.length === 2) {
      this.form.startDate = this.form.challengeRange[0];
      this.form.endDate = this.form.challengeRange[1];
    } else {
      this.form.startDate = '';
      this.form.endDate = '';
    }
  }
  /* eslint-enable */
}
</script>
