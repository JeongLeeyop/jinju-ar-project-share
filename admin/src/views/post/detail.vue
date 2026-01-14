<template>
  <div
    v-if="post"
    class="post-detail"
  >
    <div class="post-detail__header">
      게시글 보기 - {{ board ? board.name : '' }}
    </div>
    <div class="post-detail__body" v-loading="loading">
      <table class="post-detail__table">
        <tr
          v-for="(boardUseCategory, index) in boardUseCategoryList"
          :key="index"
        >
          <th>{{ boardUseCategory.category.name }}</th>
          <td>
            <span
              v-for="(postCategory, index) in post.categoryList"
              :key="index"
            >
              <el-tag
                v-if="boardUseCategory.category.children.findIndex((child) => child.uid === postCategory.categoryUid) > -1"
                style="margin-right: 5px;"
              >
                {{ postCategory.category.name }}
              </el-tag>
            </span>
          </td>
        </tr>
        <tr>
          <th>제목</th>
          <td><b style="font-weight: 800;" v-if="post.parentUid">[답변]</b> {{ post.title }}</td>
        </tr>
        <tr>
          <th>내용</th>
          <td>
            <pre v-html="post.content" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>태그</th>
          <td>
            <el-tag v-for="tag in post.tags" :key="tag">#{{ tag.tag }}</el-tag>
          </td>
        </tr>
        <tr
          v-for="boardField in boardFieldList"
          :key="boardField.uid"
        >
          <th>{{ boardField.fieldName }}</th>
          <td>
            <div
              v-if="boardField.fieldTypeCode === 'EDITOR'"
              class="note-editable"
              v-html="getInputValue(boardField.uid)"
            />
            <div v-else-if="boardField.fieldTypeCode === 'PHOTO'">
              <template v-for="postFile in post.fileList">
                <img
                  v-if="boardField.uid === postFile.fieldUid"
                  :key="postFile.fileUid"
                  :src="`${apiUrl}/attached-file/${postFile.fileUid}`"
                  width="200"
                >
              </template>
            </div>
            <div v-else-if="boardField.fieldTypeCode !== 'FILE'">
              {{ getInputValue(boardField.uid) }}
            </div>
            <div v-else>
              <a
                v-for="(postFile, index) in post.fileList"
                :key="index"
                style="display: block; color: #409EFF;"
                :href="`${apiUrl}attached-file/${postFile.fileUid}`"
              >
                {{ getFile(boardField.uid, postFile).originalName }}
              </a>
            </div>
          </td>
        </tr>
        <tr v-if="board && board.fileUseState">
          <th>첨부파일</th>
          <td>
            <div v-for="postFile in post.fileList" :key="postFile.fileUid">
              <a v-if="!postFile.fieldUid" :href="`${apiUrl}attached-file/${postFile.fileUid}`" style="color: #409EFF;">
                {{ postFile.file.originalName }}
              </a>
            </div>
          </td>
        </tr>
        <tr>
          <th>작성자</th>
          <td>{{ post.writer }}</td>
        </tr>
        <tr>
          <th>작성일</th>
          <td>{{ post.createDate | parseDate }}</td>
        </tr>
        <tr>
          <th>조회</th>
          <td>{{ post.viewCount }}</td>
        </tr>
      </table>
    </div>
    <div class="post-detail__footer align--center">
      <el-button
        type="info"
        size="small"
        @click="listPost"
      >
        목록
      </el-button>
      <el-button
        v-if="board && board.replyState"
        type="primary"
        size="small"
        @click="replyPost"
      >
        답글
      </el-button>
      <el-button
        type="success"
        size="small"
        @click="updatePost"
      >
        수정
      </el-button>
      <el-button
        type="danger"
        size="small"
        @click="deletePost"
      >
        삭제
      </el-button>
    </div>
  </div>
</template>

<script lang="ts">
import { Vue, Component } from 'vue-property-decorator';
import { deletePost, viewPost } from '@/api/post';
import { getBoard } from '@/api/board';
import { IBoard, IField } from '@/types/board';
import { IPost, IData } from '@/types/post';

@Component({
  name: 'PostDetail',
})
export default class extends Vue {
  mounted() {
    viewPost(this.$route.params.postUid).then((res) => {
      this.post = res.data;
      getBoard(res.data.boardUid).then((response) => {
        this.board = response.data;
        this.boardFieldList = response.data.fieldList;
        this.boardUseCategoryList = response.data.categoryList;
        this.loading = false;
      });
    }).catch(() => {
      this.$message.error('게시글을 불러오는데 실패했습니다.');
      this.$router.push({ name: 'Post' });
    });
  }

  private post: IPost | null = null;

  private boardFieldList: IField[] = [];

  private boardUseCategoryList = [];

  private board: IBoard | null = null;

  private loading: boolean = true;

  private apiUrl = process.env.VUE_APP_BASE_API;

  private deletePost() {
    this.$confirm('정말 게시글을 삭제하시겠습니까?', '게시글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deletePost(this.$route.params.postUid).then(() => {
        this.$message.success('게시글이 삭제되었습니다.');
        this.$router.push({ name: 'Post' });
      });
    });
  }

  private listPost() {
    this.$router.push({ name: 'Post' });
  }

  private updatePost() {
    this.$router.push({ name: 'PostUpdate', params: { postUid: this.$route.params.postUid } });
  }

  private getInputValue(boardFieldUid: string) {
    if (this.post !== null) {
      const dataIndex = (this.post as IPost).dataList.findIndex((data: IData) => data.fieldUid === boardFieldUid);
      if (dataIndex > -1) return (this.post as IPost).dataList[dataIndex].inputValue;
    }
    return '';
  }

  private replyPost() {
    this.$router.push({ name: 'PostReply', params: { parentUid: this.$route.params.postUid } });
  }
}
</script>

<style lang="scss">
  .post-detail {
    padding: 80px 34px 20px 34px;
    margin-left: 155px;
    background-color: rgba(97, 95, 114, 0.08);
    &__header {
      font-size: 18px;
      font-weight: bold;
      color: #4c4c4c;
      text-align: left;
      margin-bottom: 15px;
    }
    &__table {
      width: 100%;
      border-collapse: collapse;
      text-align: left;
      margin-bottom: 20px;
      tr {
        border: 1px solid #606266;
      }
      th {
        width: 10%;
        min-width: 150px;
        font-size: 0.9em;
        padding: 15px;
        background-color: #615f72;
        color: #fff;
        border: 1px solid #4c4c4c;
      }
      td {
        font-size: 0.9em;
        padding: 15px;
        background: #fff;
      }
    }
    &__footer {
      margin-bottom: 20px;
    }
  }
</style>
