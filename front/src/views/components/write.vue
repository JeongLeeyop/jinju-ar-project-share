<template>
    <div class="write_wr">
        <el-form v-loading="formLoading" ref="postForm" class="post-form" label-width="auto" :model="formData"
            :rules="rules">
            <!-- <el-form-item label="카테고리" prop="category" style="text-align: left;">
                <el-select v-model="formData.categoryList[0]" value-key="categoryUid">
                    <el-option v-for="useCategory in boardUseCategoryList" :key="useCategory.categoryUid"
                        :value="{ categoryUid: useCategory.categoryUid }" :label="useCategory.category.name" />
                </el-select>
            </el-form-item> -->
            <el-form-item label="공지글 여부" prop="noticeStatus" style="text-align: left;" v-if="selectedChannel.myChannelStatus">
                <el-switch v-model="formData.noticeStatus"></el-switch>
            </el-form-item>
            <!-- <el-form-item label="제목" prop="title">
                <el-input v-model="formData.title" placeholder="제목을 입력하세요" />
            </el-form-item> -->
            <el-form-item label="내용" prop="content">
                <SummerNote :contents="formData.content" @change="handleChangeContents" />
            </el-form-item>
            <el-form-item v-for="(data, index) in formData.dataList" :key="data.fieldUid" :label="data.field.fieldName"
                :prop="`dataList[${index}].inputValue`" :rules="parseRule(data.field)">
                <field moduleName="post" :fieldUid="data.fieldUid" :fieldValue="data.inputValue"
                    :inputLimit="data.field.inputLimit" :fileSizeLimit="data.field.fileSizeLimit"
                    :typeCode="data.field.fieldTypeCode" :fileList="formData.fileList"
                    @change="(inputValue) => handleChangeField(inputValue, data.fieldUid)"
                    @upload="(res) => handleUpload(res, data.fieldUid)" @fileRemove="handleFileRemove" />
            </el-form-item>
            <el-form-item v-if="board.fileUseState" :label="!formData.noticeStatus ? '첨부파일' : ''">
                <el-upload :action="`${apiUrl}/post/upload`" :headers="requestHeaders"
                    :before-upload="handleBeforeUpload" :on-success="handleSuccessUpload" :limit="board.fileCountLimit"
                    :file-list="getFileList()" :on-remove="handleRemove" v-if="!formData.noticeStatus">
                    <el-button size="small" type="info" icon="el-icon-upload2">파일 선택</el-button>
                    <div slot="tip" class="el-upload__tip" style="margin-bottom:10px;">
                        이미지 파일만 업로드 가능하며, 파일 당 용량 {{ board.fileSizeLimit }}MB, 최대 {{ board.fileCountLimit }}개 까지 업로드 가능합니다.
                    </div>
                </el-upload>
            </el-form-item>
            <div class="align--center">
                <el-button type="primary" @click="submitPost"> {{ this.postUid ? '수정' : '작성' }}</el-button>
                <el-button type="info" @click="handleCancel"> 취소 </el-button>
            </div>
        </el-form>
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { ref } from 'vue';
import { addPost, updatePost } from '@/api/post';
import { IPost } from '@/types/post';
import { IBoard, IField } from '@/types/board';
import { Form } from 'element-ui';
import Field from './Field.vue';
import { getToken } from '@/utils/cookies';
import SummerNote from '@/components/SummerNote/index.vue';
import { ChannelModule } from '@/store/modules/channel';

@Component({
    name: 'write',
    components: {
        Field,
        SummerNote,
    },
})
export default class extends Vue {
    @Prop({ required: true }) private board!: IBoard;

    @Prop({ required: false }) private post!: IPost;

    @Prop({ required: true }) private boardUseCategoryList!: any;

    @Prop({ required: true }) private channelUid!: any;

    @Prop({ required: false }) private postUid!: any;

    mounted() {
        this.formData.boardUid = this.boardUid;
        this.formData.channelUid = this.channelUid;
        this.boardFieldList = this.board.fieldList;
        if (!this.postUid) {
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
        } else {
            this.formData = {
                ...this.formData,
                ...this.post,
            };
        }
        this.formLoading = false;
    }

    private selectedChannel = ChannelModule.selectedChannel;

    private formLoading: boolean = true;

    private apiUrl = process.env.VUE_APP_BASE_API;

    private boardUid = process.env.VUE_APP_COMMUNITY_BOARD_UID;

    get requestHeaders() {
        return {
            Authorization: `Bearer ${getToken()}`,
        };
    }

    private boardFieldList: IField[] = [];

    private formData: IPost = {
        uid: '',
        title: '',
        content: '',
        boardUid: '',
        channelUid: '',
        parentUid: '',
        writer: '',
        categoryList: [],
        fileList: [],
        dataList: [],
        noticeStatus: false,
    };

    private parseRule(boardField: any) {
        let inputMessage = '입력';
        if (boardField.fieldTypeCode === 'FILE') inputMessage = '업로드';
        return [
            {
                required: true,
                message: `${boardField.fieldName}을(를) ${inputMessage}하세요.`,
                trigger: 'blur',
            },
        ];
    }

    private handleBeforeUpload(uploadFile: File) {
        const fileSizeLimitByMb = (this.board as any).fileSizeLimit * 1024 * 1024;
        if (uploadFile.size > fileSizeLimitByMb) {
            this.$message.warning(`파일 업로드 최대용량은 ${fileSizeLimitByMb}MB입니다.`);
            return false;
        }
        return true;
    }

    private handleUpload(res: any, boardFieldUid: string) {
        const postFile: any = {
            postUid: '' || this.postUid,
            fileUid: res.uid,
            fieldUid: boardFieldUid,
        };
        (this.formData.fileList as any[]).push({ fileUid: res.uid, file: res, fieldUid: boardFieldUid });
    }

    private handleFileRemove(fileUid: string, fieldUid: string) {
        const fileIndex = this.formData.fileList.findIndex((file: any) => file.fileUid === fileUid);
        if (fileIndex > -1) this.formData.fileList.splice(fileIndex, 1);
        const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === fieldUid);
        if (dataIndex > -1) (this.formData.dataList[dataIndex] as any).inputValue = '';
    }

    private handleRemove(file: any, fileList: any[]) {
        const fileIndex = this.formData.fileList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
        if (fileIndex > -1) this.formData.fileList.splice(fileIndex, 1);
     }

    private handleChangeField(inputValue: string, uid: string) {
        const dataIndex = this.formData.dataList.findIndex((data: any) => data.fieldUid === uid);
        (this.formData.dataList[dataIndex] as any).inputValue = inputValue;
    }

    private getFileList() {
        const fileList: any[] = [];
        this.formData.fileList.forEach((postFile: any) => {
            if (!postFile.fieldUid) fileList.push({ fileUid: postFile.fileUid, name: postFile.file.originalName });
        });
        return fileList;
    }

    private submitPost() {
        (this.$refs.postForm as Form).validate((valid: boolean) => {
            if (valid) {
                if (this.postUid) {
                    updatePost(this.postUid, this.formData).then((res) => {
                        this.$message.info('게시글 수정이 완료되었습니다.');
                        this.$emit('success');
                        // this.$router.push({ name: 'PostDetail', params: { postUid: res.data.uid } });
                    }).catch(() => {
                        this.$message.info('게시글 수정에 실패하였습니다.');
                    });
                } else {
                    addPost(this.formData).then((res) => {
                        this.$message.info('게시글 쓰기가 완료되었습니다.');
                        this.$emit('success');
                        // this.$router.push({ name: 'PostDetail', params: { postUid: res.data.uid } });
                    }).catch(() => {
                        this.$message.info('게시글 쓰기에 실패하였습니다.');
                    });
                }
            }
        });
    }

    private handleSuccessUpload(res: any) {
        (this.formData.fileList as any[]).push({ fileUid: res.uid, file: res, fieldUid: null });
    }

    private rules = {
        boardUid: [
            {
                required: true,
                message: '게시판을 선택하세요.',
                trigger: 'blur',
            },
        ],
        content: [
            {
                required: true,
                message: '내용을 입력하세요.',
                trigger: 'blur',
            },
        ],
    };

    private handleChangeContents(value: string) {
        this.formData.content = value;
    }

    private handleCancel() {
        this.$emit('cancel');
  }
}
</script>
<style>
    .setting-wrap .dialog-wrap .write_wr .el-form-item .el-form-item__content{margin-right:60px;}
    @media (max-width: 768px) {
        .el-form-item__content {
            margin-left: 0 !important;
        }
    }
</style>
