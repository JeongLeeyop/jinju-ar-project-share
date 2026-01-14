<template>
  <div>
    <el-input
      v-if="typeCode === 'TEXT'"
      v-model="inputValue"
      type="text"
      :maxlength="inputLimit"
    />
    <el-input
      v-if="typeCode === 'NUMBER'"
      v-model="inputValue"
      type="number"
      :maxlength="inputLimit"
    />
    <el-input
      v-if="typeCode === 'TEXT_AREA'"
      v-model="inputValue"
      type="textarea"
      :maxlength="inputLimit"
    />
    <el-input
      v-if="typeCode === 'EMAIL'"
      v-model="inputValue"
      type="email"
      :maxlength="inputLimit"
    />
    <el-date-picker
      v-if="typeCode === 'DATE'"
      v-model="inputValue"
      type="date"
      value-format="yyyy-MM-dd"
    />
    <el-input
      v-if="typeCode === 'TEL'"
      v-model="inputValue"
      type="text"
      placeholder="000-000-0000"
    />
    <slot v-if="typeCode === 'ADDRESS'">
      <el-button @click="handleAddressClick">
        주소 검색
      </el-button>
      <el-input
        disabled
        v-model="inputValue"
        :maxlength="inputLimit"
      />
      <el-dialog
        title="주소 검색"
        center
        class="dialog--no-padding dialog-wrap"
        :visible.sync="addressDialogVisible"
        :before-close="handleAddressClick"
        append-to-body
      >
        <span>
          <!--
          <daum-post-code :on-complete="handleAddressComplete"/>
          -->
        </span>
      </el-dialog>
    </slot>
    <editor
      v-if="typeCode === 'EDITOR'"
      :inputValue="inputValue"
      :moduleName="moduleName"
      @changeValue="handleChange"
    />
    <el-upload
      v-if="typeCode === 'FILE'"
      :action="`${apiUrl}/${moduleName}/upload`"
      :headers="requestHeaders"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccessUpload"
      :limit="inputLimit"
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
        파일 당 용량 {{ fileSizeLimit }}MB, 최대 {{ inputLimit }}개 까지 업로드 가능합니다.
      </div>
    </el-upload>
    <el-upload
      v-if="typeCode === 'PHOTO'"
      list-type="picture"
      :on-success="handleSuccessUpload"
      :action="`${apiUrl}/${moduleName}/upload`"
      :headers="requestHeaders"
      :before-upload="(file) => handleBeforeUpload(file, true)"
      :limit="inputLimit"
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
        파일 당 용량 {{ fileSizeLimit }}MB, 최대 {{ inputLimit }}개 까지 업로드 가능합니다.
      </div>
    </el-upload>
  </div>
</template>

<script lang="ts">
import {
  Vue,
  Component,
  Prop,
  Watch,
} from 'vue-property-decorator';
import { IFile } from '@/types/post';
import Editor from '@/components/Editor/index.vue';
import { getToken } from '@/utils/cookies';

@Component({
  name: 'Field',
  components: {
    Editor,
  },
})
export default class extends Vue {
  @Prop({ required: true }) private typeCode!: string

  @Prop({ required: true }) private fieldUid!: string

  @Prop({ default: 0 }) private inputLimit!: number

  @Prop({ default: 0 }) private fileSizeLimit!: number

  @Prop({ default: '' }) private fieldValue!: string | number

  @Prop({ default: '' }) private moduleName!: string

  @Prop({ default: [] }) private fileList!: IFile[]

  @Watch('inputValue')
  private onInputValueChange() {
    this.$emit('change', this.inputValue);
  }

  mounted() {
    this.inputValue = this.fieldValue;
  }

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private addressDialogVisible = false;

  private inputValue: string | number = '';

  private apiUrl = process.env.VUE_APP_BASE_API;

  private handleChange(value: string) {
    this.inputValue = value;
  }

  private handleAddressClick() {
    this.addressDialogVisible = !this.addressDialogVisible;
  }

  private handleAddressComplete(data: any) {
    this.inputValue = data.address;
    this.handleAddressClick();
  }

  private handleBeforeUpload(uploadFile: File, isPhoto: boolean) {
    const fileSizeLimitByMb = this.fileSizeLimit * 1024 * 1024;
    if (isPhoto) {
      const isImageFile = uploadFile.type.split('/')[0] === 'image';
      if (!isImageFile) {
        this.$message.warning('이미지 파일만 업로드 가능합니다.');
        return false;
      }
    }
    if (uploadFile.size > fileSizeLimitByMb) {
      this.$message.warning(`파일 업로드 최대용량은 ${this.fileSizeLimit}MB입니다.`);
      return false;
    }
    return true;
  }

  private handleSuccessUpload(res: any) {
    if (this.moduleName === 'post') {
      this.inputValue = res.uid;
      this.$emit('upload', res);
    }
  }

  private getFileList() {
    const fileList: any[] = [];
    this.fileList.forEach((file) => {
      if (this.fieldUid === file.fieldUid && file.file) {
        fileList.push(
          {
            id: file.fileUid,
            name: file.file.originalName,
            url: `${this.apiUrl}/attached-file/${file.fileUid}`,
          },
        );
      }
    });
    return fileList;
  }

  private handleRemove(file: any) {
    this.$emit('fileRemove', file.id, this.fieldUid);
  }
}
</script>
