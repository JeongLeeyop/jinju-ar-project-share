<template>
<div class="comment-box" :class="isVisible ? 'active' : ''">
  <el-divider></el-divider>
    <div class="comment-item-group">
            <div class="comment-item" v-for="comment in commentList" :key="comment.uid">
            <div class="comment-item-inner">
                <div class="writer">{{ comment.writer }}</div>
            </div>
            <div class="txt">{{ comment.contents }}</div>
            <div class="date_wr">{{ comment.createDate | parseDate('YY.MM.DD') }}</div>
            <div class="btn_wr">
                <el-button v-if="comment.hasAuthority" @click="deleteComment(comment.uid)" type="danger" class="del_btn">삭제</el-button>
            </div>
            </div>
        </div>
    <div class="comment-input-wr">
        <input v-model="commentForm.contents" type="text" placeholder="댓글을 입력해주세요" @keyup.enter="addComment()">
        <el-button icon="el-icon-s-promotion" @click="addComment()" class="btn"></el-button>
    </div>
    <Pagination :total="commentTotalElements" :page.sync="commentListQuery.page" :limit.sync="commentListQuery.size" @pagination="getCommentList" />
</div>
</template>

<script lang="ts">
import { addComment, deleteComment, getCommentList } from '@/api/comment';
import { Component, Prop, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { ChannelModule } from '@/store/modules/channel';

@Component({
    name: 'Comment',
    components: {
        Pagination,
    },
})
export default class extends Vue {
    @Prop({ required: false }) private postUid!: any;

    mounted() {
        this.getCommentList();
    }

  private loading = true;

  private isVisible = false;

  private commentTotalElements: number = 0;

  private commentListQuery: any = {
    postUid: '',
    page: 1,
    size: 10,
  }

  private commentForm = {
    contents: '',
    postUid: '',
    parentUid: '',
    writer: '',
    hide: false,
    channelUid: ChannelModule.selectedChannel.uid,
  }

  private commentList = [];

  private getCommentList() {
    this.commentListQuery = {
        ...this.commentListQuery,
        postUid: this.postUid,
    };
    getCommentList(this.commentListQuery).then((res) => {
      this.commentList = res.data.content;
      this.commentTotalElements = res.data.totalElements;
      this.loading = false;
      this.isVisible = true;
    }).catch(() => {
      this.$message.warning('댓글조회에 실패했습니다.');
      this.loading = false;
      this.isVisible = true;
    });
  }

  private addComment() {
    this.$confirm('정말 댓글을 작성하시겠습니까?', '댓글 작성', {
      confirmButtonText: '작성',
      cancelButtonText: '취소',
    }).then(() => {
      this.commentForm = {
        ...this.commentForm,
        postUid: this.postUid,
      };
      addComment(this.commentForm).then(() => {
        this.$message.info('댓글을 작성했습니다.');
        this.getCommentList();
        this.commentForm.contents = '';
        // 부모 컴포넌트에 refresh 이벤트 emit (댓글 수 업데이트)
        this.$emit('refresh');
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  private deleteComment(postUid: string) {
    this.$confirm('정말 댓글을 삭제하시겠습니까?', '댓글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      deleteComment(postUid).then(() => {
        this.$message.info('댓글을 삭제했습니다.');
        this.getCommentList();
        // 부모 컴포넌트에 refresh 이벤트 emit (댓글 수 업데이트)
        this.$emit('refresh');
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  private handleCancle() {
        this.$emit('success');
  }
}
</script>
