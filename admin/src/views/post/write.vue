<template>
  <div class="post-write">
    <div class="post-write__header">
      게시글 {{ $route.params.postUid ? '수정' : '추가' }}
    </div>
    <el-form
      v-loading="formLoading"
      ref="postForm"
      class="post-form"
      label-width="auto"
      :model="formData"
      :rules="rules"
    >
      <el-form-item
        label="게시판"
        prop="boardUid"
      >
        <el-select
          v-model="formData.boardUid"
          :disabled="isBoardDisabled()"
          @change="handleChangeBoard"
        >
          <el-option
            v-for="board in boardList"
            :key="board.uid"
            :label="board.name"
            :value="board.uid"
          />
        </el-select>
      </el-form-item>
      <div v-if="formData.boardUid">
        <slot v-if="$route.name !== 'PostReply'">
          <el-form-item
            v-for="(useCategory, idx) in boardCategoryList"
            :key="idx"
            :label="useCategory.category.name"
          >
            <el-select
              v-model="formData.categoryList[idx]"
              value-key="categoryUid"
              :placeholder="`${useCategory.category.name} 선택`"
            >
              <el-option
                v-for="category in useCategory.category.children"
                :key="category.uid"
                :label="category.name"
                :value="{ categoryUid: category.uid }"
              />
            </el-select>
            <!--
            <span
              v-for="(parentCategory, index) in formCategoryList[idx]"
              :key="index"
            >
              <el-select
                v-if="formCategoryList[idx][index] && formCategoryList[idx][index].children.length > 0"
                v-model="formCategoryList[idx][index + 1]"
                value-key="uid"
                @change="(value) => handleChangeCategory(value, idx, index + 1)"
              >
                <el-option
                  label="전체"
                  :value="defaultCategory"
                />
                <el-option
                  v-for="category in parentCategory.children"
                  :key="category.uid"
                  :label="category.categoryName"
                  :value="category"
                />
              </el-select>
            </span>
            -->
          </el-form-item>
        </slot>
        <el-form-item label="제목" prop="title">
          <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="내용" prop="content">
          <!--
          <el-input type="textarea" v-model="formData.content" />
          -->
          <SummerNote
            :contents="formData.content"
            @change="handleChangeContents"
          />
        </el-form-item>
        <el-form-item label="태그">
          <el-tag
            v-for="(item, index) in formData.tags"
            :key="item"
            closable
            :disable-transitions="false"
            @close="handleRemoveTag(index)"
          >
            #{{ item.tag }}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="tagInputVisible"
            v-model="tagInput"
            ref="saveTagInput"
            size="mini"
            @keyup.enter.native="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else class="button-new-tag" size="small" @click="tagInputVisible = true">+ 태그 추가</el-button>
        </el-form-item>
        <el-form-item
          v-for="(data, index) in formData.dataList"
          :key="data.fieldUid"
          :label="data.field.fieldName"
          :prop="`dataList[${index}].inputValue`"
          :rules="parseRule(data.field)"
        >
          <field
            moduleName="post"
            :fieldUid="data.fieldUid"
            :fieldValue="data.inputValue"
            :inputLimit="data.field.inputLimit"
            :fileSizeLimit="data.field.fileSizeLimit"
            :typeCode="data.field.fieldTypeCode"
            :fileList="formData.fileList"
            @change="(inputValue) => handleChangeField(inputValue, data.fieldUid)"
            @upload="(res) => handleUpload(res, data.fieldUid)"
            @fileRemove="handleFileRemove"
          />
        </el-form-item>
        <el-form-item v-if="selectedBoard.fileUseState" label="첨부파일">
          <el-upload
            :action="`${apiUrl}/post/upload`"
            :headers="requestHeaders"
            :before-upload="handleBeforeUpload"
            :on-success="handleSuccessUpload"
            :limit="selectedBoard.fileCountLimit"
            :file-list="getFileList()"
            :on-remove="handleRemove"
          >
            <el-button
              size="small"
              type="info"
              icon="el-icon-upload2"
            >
              파일 선택
            </el-button>
            <div slot="tip" class="el-upload__tip">
              파일 당 용량 {{ selectedBoard.fileCountLimit }}MB, 최대 {{ selectedBoard.fileSizeLimit }}개 까지 업로드 가능합니다.
            </div>
          </el-upload>
        </el-form-item>
      </div>
      <div class="align--center">
        <el-button
          type="primary"
          @click="submitPost"
        >
          {{ $route.params.postUid ? '수정' : '작성' }}
        </el-button>
        <el-button
          type="info"
          @click="handleCancle"
        >
          취소
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import { Form } from 'element-ui';
import { getBoardListAll, newBoard } from '@/api/board';
import {
  addPost,
  getPost,
  updatePost,
} from '@/api/post';
import { IBoard, IField } from '@/types/board';
import { IPost } from '@/types/post';
import Field from './components/Field.vue';
import { getToken } from '@/utils/cookies';
import SummerNote from '@/components/SummerNote/index.vue';

@Component({
  name: 'PostAdd',
  components: {
    Field,
    SummerNote,
  },
})
export default class extends Vue {
  mounted() {
    this.getBoardListAll().then(() => {
      this.setForm();
    });
  }

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private boardList: IBoard[] = [];

  private boardCategoryList = [];

  private boardFieldList: IField[] = [];

  private selectedBoard: IBoard = newBoard;

  private formData: any = {
    uid: '',
    title: '',
    content: '',
    boardUid: '',
    parentUid: '',
    writer: '',
    categoryList: [],
    fileList: [],
    dataList: [],
    tags: [],
  };

  private formLoading: boolean = true;

  private rules = {
    boardUid: [
      {
        required: true,
        message: '게시판을 선택하세요.',
        trigger: 'blur',
      },
    ],
    title: [
      {
        required: true,
        message: '제목을 입력하세요.',
        trigger: 'blur',
      },
    ],
  };

  private tagInputVisible = false;

  private tagInput = '';

  private getBoardListAll() {
    return new Promise((resolve) => {
      getBoardListAll().then((res) => {
        this.boardList = res.data;
        resolve('');
      });
    });
  }

  private handleChangeBoard(boardUid: string) {
    const boardIndex = this.boardList.findIndex((board: IBoard) => board.uid === boardUid);
    if (boardIndex > -1) {
      const board: IBoard = this.boardList[boardIndex];
      this.selectedBoard = board;
      this.boardFieldList = board.fieldList;
      this.boardCategoryList = board.categoryList;
      if (!this.$route.params.postUid) {
        this.formData.dataList = [];
        this.boardFieldList.forEach((boardField: IField) => {
          const postData: any = {
            postUid: '',
            fieldUid: boardField.uid,
            inputValue: '',
            field: boardField,
          };
          (this.formData.dataList as any[]).push({ ...postData });
        });
      } else if (this.formData.dataList.length !== this.boardFieldList.length) {
        this.$nextTick(() => {
          this.boardFieldList.forEach((boardField: IField) => {
            if (this.formData.dataList.findIndex((data: any) => data.fieldUid === boardField.uid) < 0) {
              const postData: any = {
                postUid: '' || this.$route.params.postUid,
                fieldUid: boardField.uid,
                inputValue: '',
                field: boardField,
              };
              (this.formData.dataList as any[]).push({ ...postData });
            }
          });
          this.formData.dataList.sort((a: any, b: any) => {
            const viewOrderAsc = a.field.viewOrder - b.field.viewOrder;
            return viewOrderAsc;
          });
        });
      } else {
        const postDataList: any[] = [];
        this.boardFieldList.forEach((boardField: IField) => {
          const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === boardField.uid);
          let postDataValue = '';
          if (dataIndex > -1) postDataValue = this.formData.dataList[dataIndex].inputValue;
          const postData: any = {
            postUid: this.$route.params.postUid,
            fieldUid: boardField.uid,
            inputValue: postDataValue,
            field: boardField,
          };
          postDataList.push({ ...postData });
        });
        this.formData.dataList = postDataList;
      }
    }
  }

  private parseRule(boardField: any) {
    let inputMessage = '입력';
    if (boardField.fieldTypeCode === 'FILE') inputMessage = '업로드';
    if (boardField.requiredState) {
      return [
        {
          required: true,
          message: `${boardField.fieldName}을(를) ${inputMessage}하세요.`,
          trigger: 'blur',
        },
      ];
    }
    return [];
  }

  private handleChangeField(inputValue: string, uid: string) {
    const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === uid);
    (this.formData.dataList[dataIndex] as any).inputValue = inputValue;
  }

  private handleUpload(res: any, boardFieldUid: string) {
    const postFile: any = {
      postUid: '' || this.$route.params.postUid,
      fileUid: res.uid,
      fieldUid: boardFieldUid,
    };
    (this.formData.fileList as any[]).push({ fileUid: res.uid, file: res, fieldUid: boardFieldUid });
  }

  private submitPost() {
    (this.$refs.postForm as Form).validate((valid: boolean) => {
      if (valid) {
        if (this.$route.params.postUid) {
          updatePost(this.$route.params.postUid, this.formData).then((res) => {
            this.$router.push({ name: 'PostDetail', params: { postUid: res.data.uid } });
          });
        } else {
          addPost(this.formData).then((res) => {
            this.$router.push({ name: 'PostDetail', params: { postUid: res.data.uid } });
          });
        }
      }
    });
  }

  private setForm() {
    this.formData.boardUid = '';
    if (this.$route.params.postUid) {
      getPost(this.$route.params.postUid).then((res) => {
        this.formData = res.data;
        this.formLoading = false;
        this.handleChangeBoard(res.data.boardUid);
      }).catch(() => {
        this.$message.error('게시글을 불러오는데 실패했습니다.');
        this.$router.push({ name: 'Post' });
      });
    } else if (this.$route.params.parentUid) {
      getPost(this.$route.params.parentUid).then((res) => {
        this.formData = {
          ...this.formData,
          parentUid: res.data.uid,
          boardUid: res.data.boardUid,
        };
        this.handleChangeBoard(res.data.boardUid);
        this.formLoading = false;
      });
    } else {
      this.formLoading = false;
    }
  }

  private getInputValue(boardField: any, index: number) {
    if (this.$route.params.postUid) {
      const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === boardField.uid);
      if (dataIndex > -1) return (this.formData.dataList[dataIndex] as any).inputValue;
      return '';
    }
    return (this.formData.dataList[index] as any).inputValue;
  }

  private getFileList() {
    const fileList: any[] = [];
    this.formData.fileList.forEach((postFile: any) => {
      if (!postFile.fieldUid) fileList.push({ fileUid: postFile.fileUid, name: postFile.file.originalName });
    });
    return fileList;
  }

  private handleFileRemove(fileUid: string, fieldUid: string) {
    const fileIndex = this.formData.fileList.findIndex((file: any) => file.fileUid === fileUid);
    if (fileIndex > -1) this.formData.fileList.splice(fileIndex, 1);
    const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === fieldUid);
    if (dataIndex > -1) (this.formData.dataList[dataIndex] as any).inputValue = '';
  }

  private handleCancle() {
    if (this.$route.params.postUid) {
      this.$router.push({ name: 'PostDetail', params: { postUid: this.$route.params.postUid } });
    } else if (this.$route.params.parentUid) {
      this.$router.push({ name: 'PostDetail', params: { postUid: this.$route.params.parentUid } });
    } else {
      this.$router.push({ name: 'Post' });
    }
  }

  private setCategory(category: any, useCategory: any) {
    return {
      ...category,
    };
  }

  private isBoardDisabled() {
    if (this.$route.params.postUid || this.$route.params.parentUid) return true;
    return false;
  }

  private handleBeforeUpload(uploadFile: File) {
    const fileSizeLimitByMb = (this.selectedBoard as any).fileSizeLimit * 1024 * 1024;
    if (uploadFile.size > fileSizeLimitByMb) {
      this.$message.warning(`파일 업로드 최대용량은 ${fileSizeLimitByMb}MB입니다.`);
      return false;
    }
    return true;
  }

  private handleSuccessUpload(res: any) {
    (this.formData.fileList as any[]).push({ fileUid: res.uid, file: res, fieldUid: null });
  }

  private handleRemove(file: any, fileList: any[]) {
    const fileIndex = this.formData.fileList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
    if (fileIndex > -1) this.formData.fileList.splice(fileIndex, 1);
  }

  private handleChangeContents(value: string) {
    this.formData.content = value;
  }

  private handleInputConfirm() {
    if (this.tagInput) {
      if (this.formData.tags.findIndex((item: any) => item.tag === this.tagInput) < 0) {
        this.formData.tags.push({
          tag: this.tagInput,
        });
        this.tagInput = '';
        this.tagInputVisible = false;
      } else {
        this.$message.warning('이미 추가된 태그입니다.');
      }
    } else {
      this.tagInputVisible = false;
      this.tagInput = '';
    }
  }

  private handleRemoveTag(index: any) {
    this.formData.tags.splice(index, 1);
  }
}
</script>

<style lang="scss">
  .post-write {
    overflow-y: scroll;
    padding: 80px 34px 20px 34px;
    margin-left: 155px;
    background-color: rgba(97, 95, 114, 0.08);
    &__header {
      font-size: 18px;
      font-weight: bold;
      text-align: left;
      width: 70%;
      margin-bottom: 20px;
    }
  }
  .post-form {
    padding: 20px;
    border: 1px solid #e1e1e1;
    border-radius: 5px;
    background: #f9f9f9;
    width: 70%;
    .el-form-item {
      text-align: left;
    }
  }
</style>
