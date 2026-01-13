<template>
<div class="comment-box" :class="isVisible ? 'active' : ''">
  <el-divider></el-divider>
    <div class="comment-item-group">
            <div class="comment-item" v-for="comment in commentList" :key="comment.uid">
            <div class="comment-item-inner">
                <div class="writer">{{ comment.writer }}</div>
            </div>
            <div class="txt" style="white-space: pre-wrap;">{{ comment.contents }}</div>
            <div class="date_wr">{{ comment.createDate | parseDate('YY.MM.DD') }}</div>
            <div class="btn_wr">
                <el-button v-if="comment.hasAuthority" @click="deleteComment(comment.uid)" type="danger" class="del_btn">삭제</el-button>
            </div>
            </div>
        </div>
    <div class="comment-input-wr">
        <textarea v-model="commentForm.contents" placeholder="댓글을 입력해주세요" rows="3" @keyup.ctrl.enter="addComment()" class="comment-textarea"></textarea>
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
        // 부모 컴포넌트에 댓글 수 증가 이벤트 emit
        this.$emit('comment-count-change', 1);
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
        // 부모 컴포넌트에 댓글 수 감소 이벤트 emit
        this.$emit('comment-count-change', -1);
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

<style scoped>
.comment-box {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-box.active {
  opacity: 1;
}

.comment-item-group {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.comment-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px 0;
  border-bottom: 1px solid #EBEBEB;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-item-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.writer {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
}

.txt {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  font-weight: 400;
  line-height: 160%;
  white-space: pre-wrap;
  word-break: break-word;
}

.date_wr {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 13px;
  font-weight: 400;
  line-height: 100%;
}

.btn_wr {
  margin-top: 4px;
}

.btn_wr .del_btn {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.comment-input-wr {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.comment-textarea {
  flex: 1;
  min-height: 52px;
  max-height: 120px;
  padding: 14px 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  transition: border-color 0.2s;
  resize: none;
  box-sizing: border-box;
}

.comment-textarea::placeholder {
  color: #888;
}

.comment-textarea:focus {
  border-color: #073DFF;
}

.comment-input-wr .btn {
  width: 52px;
  height: 52px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-size: 20px;
  cursor: pointer;
  transition: background 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.comment-input-wr .btn:hover {
  background: #0631CC;
}

.comment-input-wr .btn:active {
  background: #042299;
}

.el-divider {
  margin: 0 0 10px 0;
}

/* 반응형 스타일 */
@media screen and (max-width: 768px) {
  .comment-item {
    padding: 16px 0;
    gap: 10px;
  }

  .writer {
    font-size: 14px;
  }

  .txt {
    font-size: 14px;
  }

  .date_wr {
    font-size: 12px;
  }

  .comment-textarea {
    min-height: 44px;
    padding: 12px 14px;
    font-size: 14px;
  }

  .comment-input-wr .btn {
    width: 44px;
    height: 44px;
    font-size: 18px;
  }
}

@media screen and (max-width: 480px) {
  .comment-input-wr {
    gap: 8px;
  }

  .comment-textarea {
    padding: 10px 12px;
    font-size: 13px;
  }

  .comment-input-wr .btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}
</style>
