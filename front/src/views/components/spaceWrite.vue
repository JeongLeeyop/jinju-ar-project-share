<template>
  <div class="write_wr">
    <el-form 
      v-loading="formLoading" 
      ref="postForm" 
      class="post-form" 
      label-width="auto" 
      :model="formData"
      :rules="rules"
    >
      <!-- <el-form-item label="제목" prop="title">
        <el-input v-model="formData.title" placeholder="제목을 입력하세요" />
      </el-form-item> -->

      <!-- 공지사항 체크박스 (관리자만) -->
      <el-form-item v-if="isAdmin" label="공지사항">
        <el-checkbox v-model="formData.isNotice">
          공지사항으로 등록 (상단 3개까지 고정)
        </el-checkbox>
      </el-form-item>

      <el-form-item label="내용" prop="content">
        <SummerNote :contents="formData.content" @change="handleChangeContents" />
      </el-form-item>
      
      <el-form-item label="첨부 이미지">
        <el-upload
          :action="`${apiUrl}/post/upload`"
          :headers="requestHeaders"
          list-type="picture-card"
          :file-list="formData.attachments"
          :on-success="handleSuccessUpload"
          :on-remove="handleRemove"
          :limit="5"
          accept="image/*"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <div slot="tip" class="el-upload__tip">
          이미지 파일만 업로드 가능하며, 파일 당 용량 5MB, 최대 5개 까지 업로드 가능합니다.
        </div>
      </el-form-item>

      <div class="align--center">
        <el-button type="primary" @click="submitPost">{{ postUid ? '수정' : '작성' }}</el-button>
        <el-button type="info" @click="handleCancel">취소</el-button>
      </div>
    </el-form>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Form } from 'element-ui';
import { getToken } from '@/utils/cookies';
import SummerNote from '@/components/SummerNote/index.vue';
import { createSpacePost, updateSpacePost } from '@/api/spacePost';

@Component({
  name: 'SpaceWrite',
  components: {
    SummerNote,
  },
})
export default class extends Vue {
  @Prop({ required: true }) private spaceUid!: string;
  @Prop({ required: false }) private postUid!: string;
  @Prop({ required: false }) private post!: any;
  @Prop({ default: false }) private isAdmin!: boolean;  // 관리자 여부

  mounted() {
    if (this.postUid && this.post) {
      // 수정 모드: 기존 게시글 데이터 로드
      this.formData = {
        title: this.post.title || '',
        content: this.post.content || '',
        isNotice: this.post.isNotice || false,  // 공지사항 여부
        attachments: (this.post.attachments || []).map((attachmentUid: string, index: number) => ({
          uid: `${Date.now()}-${index}`,
          name: `image-${index}.jpg`,
          status: 'success',
          url: `/api/attached-file/${attachmentUid}`,  // ✅ UID를 URL로 변환
        })),
      };
    }
    this.formLoading = false;
  }

  private formLoading = true;
  private apiUrl = process.env.VUE_APP_BASE_API;

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private formData = {
    title: '',
    content: '',
    isNotice: false,  // 공지사항 여부
    attachments: [] as any[],
  };

  private rules = {
    title: [
      { required: true, message: '제목을 입력해주세요', trigger: 'blur' },
      { min: 2, max: 200, message: '제목은 2자 이상 200자 이하로 입력해주세요', trigger: 'blur' },
    ],
    content: [
      { required: true, message: '내용을 입력해주세요', trigger: 'blur' },
    ],
  };

  private handleChangeContents(content: string) {
    this.formData.content = content;
  }

  private handleSuccessUpload(response: any, file: any, fileList: any[]) {
    // 백엔드 응답 구조: { uid, originalName, fileSize, fileType, url, createDate }
    console.log('Upload success:', response);
    
    // fileList의 각 항목을 업데이트하여 url이 포함되도록 함
    this.formData.attachments = fileList.map((f: any) => {
      if (f.uid === file.uid) {
        // 방금 업로드된 파일: response에서 url 추출
        return {
          ...f,
          url: response.url,
          status: 'success',
        };
      }
      // 기존 파일: 그대로 유지
      return f;
    });
  }

  private handleRemove(file: any, fileList: any[]) {
    this.formData.attachments = fileList;
  }

  private async submitPost() {
    const form = this.$refs.postForm as Form;
    form.validate(async (valid: boolean) => {
      if (!valid) {
        return false;
      }

      try {
        this.formLoading = true;

        // 첨부파일 URL 추출
        // handleSuccessUpload에서 file.url을 설정했으므로 file.url을 우선 사용
        const attachmentUrls = this.formData.attachments
          .map((file: any) => {
            // 1. file.url (handleSuccessUpload에서 설정)
            // 2. file.response?.url (백엔드 응답 직접)
            // 3. file.response?.data?.url (중첩 응답)
            return file.url || file.response?.url || file.response?.data?.url;
          })
          .filter(Boolean);

        console.log('Submitting attachments:', attachmentUrls);

        if (this.postUid) {
          // 게시글 수정
          await updateSpacePost(this.spaceUid, this.postUid, {
            title: this.formData.title,
            content: this.formData.content,
            isNotice: this.formData.isNotice,
            attachments: attachmentUrls,
          });
          this.$message.success('게시글이 수정되었습니다.');
        } else {
          // 게시글 작성
          await createSpacePost(this.spaceUid, {
            title: this.formData.title,
            content: this.formData.content,
            isNotice: this.formData.isNotice,
            attachments: attachmentUrls,
          });
          this.$message.success('게시글이 작성되었습니다.');
        }

        this.$emit('success');
      } catch (error: any) {
        console.error('Failed to save post:', error);
        const message = error.response?.data?.message || '게시글 저장에 실패했습니다.';
        this.$message.error(message);
      } finally {
        this.formLoading = false;
      }
    });
  }

  private handleCancel() {
    this.$emit('cancel');
  }
}
</script>

<style scoped lang="scss">
.write_wr {
  .post-form {
    .el-form-item {
      margin-bottom: 22px;
      text-align: left;

      ::v-deep .el-form-item__label {
        color: #222;
        font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
        font-size: 16px;
        font-weight: 600;
        line-height: 1.5;
        padding-right: 12px;
      }

      ::v-deep .el-form-item__content {
        line-height: normal;
      }

      // 제목 input 스타일
      ::v-deep .el-input__inner {
        height: 40px;
        line-height: 40px;
        font-size: 16px;
        border-radius: 6px;
        border: 1px solid #DCDFE6;
        padding: 0 15px;
        
        &:focus {
          border-color: #073DFF;
        }

        &::placeholder {
          color: #C0C4CC;
        }
      }
    }

    .el-upload__tip {
      color: #888;
      font-size: 14px;
      margin-top: 8px;
      line-height: 1.5;
    }

    ::v-deep .el-upload-list--picture-card {
      .el-upload-list__item {
        width: 148px;
        height: 148px;
        border-radius: 6px;
        border: 1px solid #DCDFE6;
        margin: 0 8px 8px 0;
      }
    }

    ::v-deep .el-upload--picture-card {
      width: 148px;
      height: 148px;
      border-radius: 6px;
      border: 1px dashed #DCDFE6;
      line-height: 148px;
      background-color: #FBFDFF;

      &:hover {
        border-color: #073DFF;
        color: #073DFF;
      }

      i {
        font-size: 28px;
        color: #8C939D;
      }
    }

    .align--center {
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 10px;
      margin-top: 30px;
      padding-top: 20px;
      border-top: 1px solid #EBEBEB;

      .el-button {
        min-width: 120px;
        height: 40px;
        padding: 0 20px;
        font-size: 16px;
        font-weight: 600;
        border-radius: 6px;
        transition: all 0.3s;

        &--primary {
          background-color: #073DFF;
          border-color: #073DFF;
          color: #FFF;

          &:hover {
            background-color: #0530CC;
            border-color: #0530CC;
          }

          &:active {
            background-color: #042099;
            border-color: #042099;
          }
        }

        &--info {
          background-color: #FFF;
          border-color: #DCDFE6;
          color: #606266;

          &:hover {
            color: #073DFF;
            border-color: #C6D1F8;
            background-color: #ECF5FF;
          }
        }
      }
    }
  }
}

/* Match global dialog styles from write.vue */
::v-deep .setting-wrap .dialog-wrap .write_wr .el-form-item .el-form-item__content {
  margin-right: 60px;
}

@media screen and (max-width: 768px) {
  .write_wr {
    .post-form {
      .el-form-item {
        ::v-deep .el-form-item__label {
          font-size: 15px;
        }

        ::v-deep .el-input__inner {
          height: 38px;
          line-height: 38px;
          font-size: 15px;
        }
      }

      ::v-deep .el-upload-list--picture-card .el-upload-list__item,
      ::v-deep .el-upload--picture-card {
        width: 120px;
        height: 120px;
        line-height: 120px;
      }

      .align--center {
        flex-direction: column;
        gap: 8px;

        .el-button {
          width: 100%;
          min-width: auto;
        }
      }
    }
  }
}
</style>
