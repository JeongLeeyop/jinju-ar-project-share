<template>
  <div class="community-setting-page">
    <el-form
      ref="form"
      class="community-form"
      :model="form"
      :rules="rules"
      onsubmit="return false;"
    >
      <div class="form-grid">
        <!-- Left Column -->
        <div class="form-column">
          <!-- Icon Image Upload -->
          <div class="form-group">
            <label class="form-label">커뮤니티 아이콘 이미지 등록</label>
            <!-- Upload Box - shown when no image -->
            <div v-if="form.iconImageList.length === 0" class="upload-box">
              <div class="upload-content">
                <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M6.25 34.375V5.625C6.25 3.89964 7.64964 2.5 9.375 2.5H17.5C21.8098 2.5 25.9434 4.21165 28.9909 7.25911C32.0384 10.3066 33.75 14.4402 33.75 18.75V34.375C33.75 36.1004 32.3504 37.5 30.625 37.5H9.375C7.64964 37.5 6.25 36.1004 6.25 34.375ZM18.75 28.75V21.7676L15.8838 24.6338C15.3956 25.1219 14.6044 25.1219 14.1162 24.6338C13.6281 24.1456 13.6281 23.3544 14.1162 22.8662L19.1162 17.8662L19.2106 17.7799C19.7016 17.3794 20.4261 17.4085 20.8838 17.8662L25.8838 22.8662C26.3719 23.3544 26.3719 24.1456 25.8838 24.6338C25.3956 25.1219 24.6044 25.1219 24.1162 24.6338L21.25 21.7676V28.75C21.25 29.4404 20.6904 30 20 30C19.3096 30 18.75 29.4404 18.75 28.75ZM23.75 11.875C23.75 12.0408 23.8151 12.2005 23.9323 12.3177C24.0495 12.4349 24.2092 12.5 24.375 12.5H26.875C28.0209 12.5 29.1372 12.7861 30.1286 13.3187C29.4463 11.7322 28.4681 10.2715 27.2233 9.02669C25.978 7.78136 24.5169 6.80213 22.9297 6.11979C23.4629 7.11167 23.75 8.2284 23.75 9.375V11.875ZM8.75 34.375C8.75 34.7196 9.03036 35 9.375 35H30.625C30.9696 35 31.25 34.7196 31.25 34.375V19.375C31.25 18.2147 30.7895 17.1014 29.9691 16.2809C29.1486 15.4605 28.0353 15 26.875 15H24.375C23.5462 15 22.7508 14.6713 22.1647 14.0853C21.5787 13.4992 21.25 12.7038 21.25 11.875V9.375C21.25 8.21468 20.7895 7.1014 19.9691 6.28092C19.1486 5.46045 18.0353 5 16.875 5H9.375C9.03036 5 8.75 5.28036 8.75 5.625V34.375Z" fill="#6B7280"/>
                </svg>
                <span class="upload-hint">추천 이미지 사이즈: 128 x 128</span>
                <el-upload
                  :action="`${apiUrl}/channel/upload`"
                  :headers="requestHeaders"
                  ref="iconImageUpload"
                  :before-upload="(file) => handleBeforeUpload(file, 'icon')"
                  :on-success="(file) => handleUploadSuccess(file, 'icon')"
                  :on-remove="(file) => handleRemoveFile(file, 'icon')"
                  :limit="iconImageLimit"
                  :on-exceed="handleExceedIcon"
                  :show-file-list="false"
                  :file-list="form.iconImageList"
                >
                  <button type="button" class="upload-btn">등록하기</button>
                </el-upload>
              </div>
            </div>
            <!-- Image Preview - shown when image is uploaded -->
            <div v-else class="image-preview-container">
              <div v-for="(image, index) in form.iconImageList" :key="index" class="image-preview-item">
                <img :src="image.url" :alt="image.name" class="preview-image" />
                <div class="image-info">
                  <span class="image-name">{{ image.name }}</span>
                  <button type="button" class="remove-image-btn" @click="handleRemoveFile(image, 'icon')">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 4L4 12M4 4L12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    삭제
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Community Name -->
          <div class="form-group">
            <label class="form-label">커뮤니티 이름</label>
            <el-form-item prop="name">
              <input
                type="text"
                class="form-input"
                placeholder="이름을 입력하세요"
                v-model="form.name"
                maxlength="30"
              />
            </el-form-item>
          </div>

          <!-- Category -->
          <div class="form-group">
            <label class="form-label">커뮤니티 카테고리</label>
            <el-form-item prop="category">
              <el-select 
                v-model="form.categoryList" 
                multiple
                placeholder="해당 커뮤니티의 카테고리를 선택해주세요" 
                value-key="categoryUid"
                class="category-select"
                popper-class="category-dropdown"
              >
                <el-option 
                  v-for="category in categoryList" 
                  :key="category.uid" 
                  :label="category.name" 
                  :value="{categoryUid: category.uid}"
                >
                  {{ category.name }}
                </el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>

        <!-- Right Column -->
        <div class="form-column">
          <!-- Cover Image Upload -->
          <div class="form-group">
            <label class="form-label">커뮤니티 대표 이미지 등록</label>
            <!-- Upload Box - shown when no image -->
            <div v-if="form.coverImageList.length === 0" class="upload-box">
              <div class="upload-content">
                <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M6.25 34.375V5.625C6.25 3.89964 7.64964 2.5 9.375 2.5H17.5C21.8098 2.5 25.9434 4.21165 28.9909 7.25911C32.0384 10.3066 33.75 14.4402 33.75 18.75V34.375C33.75 36.1004 32.3504 37.5 30.625 37.5H9.375C7.64964 37.5 6.25 36.1004 6.25 34.375ZM18.75 28.75V21.7676L15.8838 24.6338C15.3956 25.1219 14.6044 25.1219 14.1162 24.6338C13.6281 24.1456 13.6281 23.3544 14.1162 22.8662L19.1162 17.8662L19.2106 17.7799C19.7016 17.3794 20.4261 17.4085 20.8838 17.8662L25.8838 22.8662C26.3719 23.3544 26.3719 24.1456 25.8838 24.6338C25.3956 25.1219 24.6044 25.1219 24.1162 24.6338L21.25 21.7676V28.75C21.25 29.4404 20.6904 30 20 30C19.3096 30 18.75 29.4404 18.75 28.75ZM23.75 11.875C23.75 12.0408 23.8151 12.2005 23.9323 12.3177C24.0495 12.4349 24.2092 12.5 24.375 12.5H26.875C28.0209 12.5 29.1372 12.7861 30.1286 13.3187C29.4463 11.7322 28.4681 10.2715 27.2233 9.02669C25.978 7.78136 24.5169 6.80213 22.9297 6.11979C23.4629 7.11167 23.75 8.2284 23.75 9.375V11.875ZM8.75 34.375C8.75 34.7196 9.03036 35 9.375 35H30.625C30.9696 35 31.25 34.7196 31.25 34.375V19.375C31.25 18.2147 30.7895 17.1014 29.9691 16.2809C29.1486 15.4605 28.0353 15 26.875 15H24.375C23.5462 15 22.7508 14.6713 22.1647 14.0853C21.5787 13.4992 21.25 12.7038 21.25 11.875V9.375C21.25 8.21468 20.7895 7.1014 19.9691 6.28092C19.1486 5.46045 18.0353 5 16.875 5H9.375C9.03036 5 8.75 5.28036 8.75 5.625V34.375Z" fill="#6B7280"/>
                </svg>
                <span class="upload-hint">추천 이미지 사이즈: 1084 x 576</span>
                <el-upload
                  :action="`${apiUrl}/channel/upload`"
                  :headers="requestHeaders"
                  ref="coverImageUpload"
                  :before-upload="(file) => handleBeforeUpload(file, 'cover')"
                  :on-success="(file) => handleUploadSuccess(file, 'cover')"
                  :on-remove="(file) => handleRemoveFile(file, 'cover')"
                  :limit="coverImageLimit"
                  :on-exceed="handleExceedCover"
                  :show-file-list="false"
                  :file-list="form.coverImageList"
                >
                  <button type="button" class="upload-btn">등록하기</button>
                </el-upload>
              </div>
            </div>
            <!-- Image Preview - shown when images are uploaded -->
            <div v-else class="image-preview-container cover-preview">
              <div v-for="(image, index) in form.coverImageList" :key="index" class="image-preview-item cover-item">
                <img :src="image.url" :alt="image.name" class="preview-image cover-preview-image" />
                <div class="image-info">
                  <span class="image-name">{{ image.name }}</span>
                  <button type="button" class="remove-image-btn" @click="handleRemoveFile(image, 'cover')">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M12 4L4 12M4 4L12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    </svg>
                    삭제
                  </button>
                </div>
              </div>
              <!-- Add more button if under limit -->
              <div v-if="form.coverImageList.length < coverImageLimit" class="add-more-container">
                <el-upload
                  :action="`${apiUrl}/channel/upload`"
                  :headers="requestHeaders"
                  ref="coverImageUploadMore"
                  :before-upload="(file) => handleBeforeUpload(file, 'cover')"
                  :on-success="(file) => handleUploadSuccess(file, 'cover')"
                  :limit="coverImageLimit"
                  :on-exceed="handleExceedCover"
                  :show-file-list="false"
                >
                  <button type="button" class="add-more-btn">+ 추가</button>
                </el-upload>
              </div>
            </div>
          </div>

          <!-- Domain -->
          <div class="form-group">
            <label class="form-label">커뮤니티 도메인(영문)</label>
            <p class="form-hint">*영문 소문자, 숫자, 특수문자(-, _)만 사용 가능하며, 대문자는 자동으로 소문자로 변환됩니다.</p>
            <el-form-item prop="domain">
              <input
                type="text"
                class="form-input"
                placeholder="커뮤니티 도메인 명을 영문으로 작성해주세요 (예: my-community)"
                v-model="form.domain"
                @input="handleDomainInput"
                maxlength="30"
                :readonly="form.uid ? true : false"
              />
            </el-form-item>
          </div>

          <!-- Access Control -->
          <div class="form-group">
            <label class="form-label">공간 접근</label>
            <div class="access-control">
              <button 
                type="button"
                class="access-btn" 
                :class="{ active: !form.privateStatus }"
                @click="handleAccessChange(false)"
              >
                공개
              </button>
              <button 
                type="button"
                class="access-btn" 
                :class="{ active: form.privateStatus }"
                @click="handleAccessChange(true)"
              >
                비공개
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Full Width Description -->
      <div class="form-group form-group-full">
        <label class="form-label">커뮤니티 설명</label>
        <textarea
          class="form-textarea"
          placeholder="커뮤니티에 대해서 설명해주세요"
          v-model="form.introduce"
          maxlength="500"
          rows="6"
        ></textarea>
      </div>

      <!-- Password Field (Conditional) -->
      <div v-if="passwordVisible" class="form-group form-group-full">
        <label class="form-label">암호 설정</label>
        <el-form-item prop="password">
          <input
            type="password"
            class="form-input"
            placeholder="암호를 입력해주세요"
            v-model="form.password"
            maxlength="15"
          />
        </el-form-item>
      </div>

      <!-- Join Questions -->
      <div class="form-group form-group-full">
        <label class="form-label">가입 질문 항목 설정</label>
        <p class="form-hint">*최대 5개까지 설정 가능합니다.</p>
        <div class="questions-list">
          <div v-for="(question, index) in form.questionList" :key="index" class="question-item">
            <input
              type="text"
              class="form-input"
              placeholder="질문을 입력해주세요."
              v-model="question.title"
              maxlength="100"
            />
            <button type="button" class="remove-btn" @click="handleDeleteQuestion(index)">제거</button>
          </div>
          <button type="button" class="add-question-btn" @click="handleAddQuestion()">+ 추가</button>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="form-actions">
        <button type="button" class="save-btn" @click="handleSave()">
          {{ form.uid ? '저장하기' : '커뮤니티 생성하기' }}
        </button>
      </div>
    </el-form>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import {
  addChannel,
  updateChannel,
  checkDomain,
  getChannelCategory,
} from '@/api/channel';
import { ChannelModule } from '@/store/modules/channel';
import { Form } from 'element-ui';
import { getToken } from '@/utils/cookies';

@Component({
  name: 'CommunitySetting',
})
export default class extends Vue {
  @Watch('form.questionList', { deep: true })
  private handleChangeQuestion() {
    if (!this.isSetForm) {
      this.form.questionChangeFlag = true;
    }
  }

  async mounted() {
    this.getCategoryList();
    if (this.$route.name !== 'InitCommunity') {
      this.isSetForm = true;
      await this.setForm();
      this.isSetForm = false;
    }
  }

  private isSetForm = false;

  private apiUrl = process.env.VUE_APP_BASE_API;

  private clientApiUrl = process.env.VUE_APP_CLIENT_API;

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private iconImageLimit = 1;

  private coverImageLimit = 4;

  private form: any = {
    uid: '',
    name: '',
    introduce: '',
    privateStatus: false,
    password: '',
    domain: '',
    iconImageList: [],
    coverImageList: [],
    categoryList: [],
    questionList: [],
    questionChangeFlag: false,
  }

  private categoryList = [];

  private getCategoryList() {
    getChannelCategory().then((res) => {
      this.categoryList = res.data;
    });
  }

  private handleDomainInput(event: any) {
    // 대문자를 소문자로 자동 변환
    this.form.domain = event.target.value.toLowerCase();
  }

  private validateDomain = (rule: any, value: any, callback: any) => {
    if (value === '') {
      callback(new Error('도메인을 입력하세요.'));
    } else if (ChannelModule.selectedChannel.uid) {
      callback();
    } else {
      const regUserId = /^[a-z0-9_-]{3,19}$/g;
      if (!regUserId.test(value)) {
        callback(new Error('영문 소문자, 숫자, 특수문자(-, _)로 4~20자로 입력하세요.'));
      } else {
        checkDomain(value).then((res) => {
          if (!res.data) callback(new Error('이미 사용 중인 도메인입니다.'));
          else callback();
        });
      }
    }
  };

  private rules = {
    name: [
      { required: true, message: '커뮤니티 이름을 입력해주세요.', trigger: ['change', 'blur'] },
    ],
    password: [
      { required: true, message: '패스워드를 입력하세요.', trigger: ['change', 'blur'] },
      {
        min: 8,
        max: 20,
        message: '패스워드는 최소 8자, 최대 20자입니다.',
        trigger: ['change', 'blur'],
      },
    ],
    domain: [
      { required: true, message: '도메인을 입력하세요.', trigger: ['change', 'blur'] },
      { validator: this.validateDomain, trigger: 'blur' },
    ],
  }

  private passwordVisible = false;

  private setForm() {
    this.form = {
      ...this.form,
      ...ChannelModule.selectedChannel,
    };
    if (this.form.privateStatus) {
      this.passwordVisible = true;
    }
    this.form.iconImageList.forEach((image: any) => {
      image.url = `${this.apiUrl}/attached-file/${image.fileUid}`;
    });
    this.form.coverImageList.forEach((image: any) => {
      image.url = `${this.apiUrl}/attached-file/${image.fileUid}`;
    });
  }

  private async handleSave() {
    (this.$refs.form as Form).validate().then((valid: boolean) => {
      if (valid) {
        this.$confirm('정말 저장하시겠습니까?', '저장', {
          confirmButtonText: '저장',
          cancelButtonText: '취소',
        }).then(() => {
          if (this.form.uid) {
            updateChannel(this.form.uid, this.form).then(() => {
              this.$message.info('저장이 완료되었습니다');
            }).catch(() => {
              this.$message.warning('저장에 실패했습니다.');
            });
          } else {
            addChannel(this.form).then(() => {
              this.$message.info('저장이 완료되었습니다');
              this.$router.push({ name: 'CommunityMain', params: { domain: this.form.domain } });
            }).catch(() => {
              this.$message.warning('저장에 실패했습니다.');
            });
          }
        });
      }
    });
  }

  private handleAccessChange(isPrivate: boolean) {
    this.form.privateStatus = isPrivate;
    if (isPrivate) {
      this.passwordVisible = true;
    } else {
      this.passwordVisible = false;
    }
  }

  private handleRadio() {
    if (this.form.privateStatus) this.passwordVisible = true;
    else this.passwordVisible = false;
  }

  private handleUploadSuccess(res: any, type: string) {
    if (type === 'icon') {
      this.form.iconImageList.push({
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      });
      this.$message.success('아이콘 이미지가 업로드되었습니다.');
    } else if (type === 'cover') {
      this.form.coverImageList.push({
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      });
      this.$message.success('대표 이미지가 업로드되었습니다.');
    }
  }

  private handleBeforeUpload(file: File, type: string) {
    const isLimit = file.size / 1024 / 1024 < 10;
    const isImage = file.type.startsWith('image');
    if (!isLimit) {
      this.$message.warning('파일 용량은 10MB 를 초과 할 수 없습니다.');
      return false;
    }
    if (!isImage) {
      this.$message.warning('이미지 파일만 업로드 가능합니다.');
      return false;
    }
    return true;
  }

  private handleRemoveFile(file: any, type: string) {
    if (type === 'icon') {
      const index = this.form.iconImageList.findIndex((imgFile: any) => imgFile.fileUid === file.fileUid);
      if (index > -1) this.form.iconImageList.splice(index, 1);
    } else if (type === 'cover') {
      const index = this.form.coverImageList.findIndex((imgFile: any) => imgFile.fileUid === file.fileUid);
      if (index > -1) this.form.coverImageList.splice(index, 1);
    }
  }

  private handleExceedIcon(type: string) {
    this.$message.warning(`아이콘 파일개수는 ${this.iconImageLimit}개를 초과할 수 없습니다.`);
  }

  private handleExceedCover(type: string) {
    this.$message.warning(`커버 파일개수는 ${this.coverImageLimit}개를 초과할 수 없습니다.`);
  }

  private handleAddQuestion() {
    if (this.form.questionList.length < 5) {
      this.form.questionList.push({ title: '' });
    } else this.$message.warning('질문은 5개까지만 생성하실 수 있습니다.');
  }

  private handleDeleteQuestion(index: number) {
    if (index >= 0 && index < this.form.questionList.length) {
      this.form.questionList.splice(index, 1);
    }
  }
}
</script>

<style scoped lang="scss">
.community-setting-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 40px;
  background: #FFF;
  min-height: 100vh;
}

.community-form {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 16px;

  &.form-group-full {
    grid-column: 1 / -1;
  }
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.form-hint {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 100%;
  margin: 0;
}

.upload-box {
  width: 100%;
  height: 219px;
  border: 1px dashed #CECECE;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FB;
  transition: all 0.3s;

  &:hover {
    border-color: #073DFF;
    background: #F0F4FF;
  }
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-hint {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 150%;
  text-align: center;
}

.upload-btn {
  display: flex;
  width: 126px;
  height: 54px;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #FFF;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 150%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0530CC;
    border-color: #FFF;
  }

  &:active {
    background: #042099;
  }
}

.form-input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }

  &:read-only {
    background: #F5F5F5;
    cursor: not-allowed;
  }
}

.form-textarea {
  width: 100%;
  min-height: 156px;
  padding: 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }
}

::v-deep .category-select {
  width: 100%;

  .el-input__inner {
    height: 52px;
    padding: 0 16px;
    border: 1px solid #CECECE;
    border-radius: 10px;
    background: #FFF;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 150%;

    &::placeholder {
      color: #888;
    }
  }

  .el-input__suffix {
    display: flex;
    align-items: center;
    padding-right: 16px;
  }
}

.access-control {
  display: flex;
  align-items: center;
  gap: 20px;
}

.access-btn {
  display: flex;
  height: 54px;
  padding: 12px 0;
  justify-content: center;
  align-items: center;
  flex: 1;
  border-radius: 10px;
  border: 1px solid #FFF;
  background: #D2D2D2;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    background: #073DFF;
    border-color: #FFF;
  }

  &:hover {
    opacity: 0.9;
  }
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.remove-btn {
  height: 52px;
  padding: 0 24px;
  border: 1px solid #E74C3C;
  border-radius: 10px;
  background: #E74C3C;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  &:hover {
    background: #C0392B;
    border-color: #C0392B;
  }
}

.add-question-btn {
  width: 100%;
  height: 52px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    background: #F8F9FF;
  }
}

.form-actions {
  display: flex;
  justify-content: center;
  padding-top: 24px;
}

.save-btn {
  min-width: 200px;
  height: 56px;
  padding: 0 48px;
  border: none;
  border-radius: 10px;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0530CC;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
}

::v-deep .el-form-item {
  margin-bottom: 0;
}

::v-deep .el-form-item__error {
  color: #E74C3C;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  padding-top: 8px;
}

.image-preview-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  
  &.cover-preview {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
}

.image-preview-item {
  border: 1px solid #CECECE;
  border-radius: 10px;
  overflow: hidden;
  background: #FFF;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.1);
  }

  &.cover-item {
    display: flex;
    flex-direction: column;
  }
}

.preview-image {
  width: 100%;
  height: 128px;
  object-fit: cover;
  display: block;
  
  &.cover-preview-image {
    height: 180px;
  }
}

.image-info {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  background: #F8F9FB;
}

.image-name {
  flex: 1;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.remove-image-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid #E74C3C;
  border-radius: 6px;
  background: #FFF;
  color: #E74C3C;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  svg {
    flex-shrink: 0;
  }

  &:hover {
    background: #E74C3C;
    color: #FFF;
  }
}

.add-more-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 240px;
  border: 1px dashed #CECECE;
  border-radius: 10px;
  background: #F8F9FB;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    background: #F0F4FF;
  }
}

.add-more-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 48px;
  border: 1px solid #073DFF;
  border-radius: 8px;
  background: #FFF;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #073DFF;
    color: #FFF;
  }
}


@media (min-width: 769px) and (max-width: 1024px) {
  .community-setting-page {
    padding: 40px 30px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .form-column {
    gap: 32px;
  }

  .form-group {
    gap: 16px;
  }

  .form-label {
    font-size: 18px;
  }
}

@media (max-width: 768px) {
  .community-setting-page {
    padding: 16px 20px 70px;
    min-height: calc(100vh - 54px);
  }

  .community-form {
    gap: 32px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .form-column {
    gap: 32px;
  }

  .form-group {
    gap: 16px;
  }

  .form-label {
    font-size: 16px;
  }

  .upload-box {
    height: 179px;
  }

  .upload-hint {
    font-size: 14px;
  }

  .upload-btn {
    width: 126px;
    height: 54px;
    font-size: 16px;
  }

  .form-input {
    height: 52px;
    font-size: 16px;
  }

  .form-textarea {
    min-height: 156px;
    font-size: 16px;
  }

  ::v-deep .category-select .el-input__inner {
    height: 52px;
    font-size: 16px;
  }

  .access-control {
    gap: 16px;
  }

  .access-btn {
    height: 54px;
    font-size: 16px;
  }

  .question-item {
    flex-direction: column;
    align-items: stretch;
  }

  .remove-btn {
    width: 100%;
    height: 52px;
  }

  .add-question-btn {
    height: 52px;
    font-size: 16px;
  }

  .save-btn {
    width: 100%;
    height: 54px;
    font-size: 16px;
    min-width: unset;
  }

  .form-actions {
    padding-top: 16px;
  }

  .image-preview-container.cover-preview {
    grid-template-columns: 1fr;
  }

  .preview-image.cover-preview-image {
    height: 150px;
  }

  .add-more-container {
    min-height: 180px;
  }
}

@media (max-width: 375px) {
  .community-setting-page {
    padding: 16px;
  }

  .upload-hint {
    font-size: 13px;
  }

  .form-input,
  .form-textarea {
    font-size: 15px;
  }
}
</style>
