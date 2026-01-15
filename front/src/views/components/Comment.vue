<template>
<div class="comment-box" :class="isVisible ? 'active' : ''">
  <el-divider></el-divider>
    <div class="comment-item-group">
            <div class="comment-item" v-for="comment in commentList" :key="comment.uid">
            <div class="comment-item-inner">
                <div class="comment-header-left">
                  <div class="user-avatar">
                    <img v-if="comment.iconFileUid" :src="`${apiUrl}/attached-file/${comment.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                    <svg v-else width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <circle cx="14" cy="14" r="14" fill="#D9D9D9"/>
                      <mask :id="`mask-comment-${comment.uid}`" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="28" height="28">
                        <circle cx="14" cy="14" r="14" fill="#D9D9D9"/>
                      </mask>
                      <g :mask="`url(#mask-comment-${comment.uid})`">
                        <rect x="3" y="16" width="22" height="25" rx="11" fill="#F5F5F5"/>
                        <circle cx="14" cy="9" r="5.5" fill="#F5F5F5"/>
                      </g>
                    </svg>
                  </div>
                  <div class="writer">{{ comment.writer }}</div>
                </div>
                <div class="comment-header-right">
                  <div class="date_wr">{{ comment.createDate | parseDate('YY.MM.DD') }}</div>
                  <el-dropdown v-if="comment.hasAuthority || isChannelManager" trigger="click" @command="handleCommentAction">
                    <span class="el-dropdown-link">
                      <i class="el-icon-more"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item :command="{action: 'edit', comment: comment}">수정</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'delete', commentUid: comment.uid}">삭제</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
            </div>
            <!-- 일반 모드 -->
            <div v-if="!editingCommentUid || editingCommentUid !== comment.uid" class="txt">{{ comment.contents }}</div>
            <!-- 수정 모드 -->
            <div v-else class="comment-edit-area">
              <textarea v-model="editingCommentContent" class="comment-edit-textarea" rows="3"></textarea>
              <div class="comment-edit-actions">
                <el-button size="small" @click="cancelEdit">취소</el-button>
                <el-button size="small" type="primary" @click="saveEdit(comment.uid)">저장</el-button>
              </div>
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
import { addComment, deleteComment, getCommentList, updateComment } from '@/api/comment';
import { Component, Prop, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { ChannelModule } from '@/store/modules/channel';
import { ChannelPermissionModule } from '@/store/modules/channelPermission';

@Component({
    name: 'Comment',
    components: {
        Pagination,
    },
})
export default class extends Vue {
    @Prop({ required: false }) private postUid!: any;

    private apiUrl = process.env.VUE_APP_BASE_API;

    mounted() {
        // 채널 권한 확인
        this.isChannelManager = ChannelPermissionModule.isChannelAdmin;
        console.log('📝 Comment 마운트 - isChannelAdmin:', this.isChannelManager);
        this.getCommentList();
    }

  private loading = true;

  private isVisible = false;

  private commentTotalElements: number = 0;

  // ✅ 채널 관리자 여부 (로컬 data로 관리)
  private isChannelManager = false;

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

  // 수정 모드 관련
  private editingCommentUid: string = '';
  private editingCommentContent: string = '';

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

  private deleteComment(commentUid: string) {
    this.$confirm('정말 댓글을 삭제하시겠습니까?', '댓글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      deleteComment(commentUid).then(() => {
        this.$message.info('댓글을 삭제했습니다.');
        this.getCommentList();
        // 부모 컴포넌트에 댓글 수 감소 이벤트 emit
        this.$emit('comment-count-change', -1);
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  // 댓글 액션 처리 (수정/삭제)
  private handleCommentAction(command: any) {
    const { action, commentUid, comment } = command;
    if (action === 'delete') {
      this.deleteComment(commentUid);
    } else if (action === 'edit') {
      this.startEdit(comment);
    }
  }

  // 수정 모드 시작
  private startEdit(comment: any) {
    this.editingCommentUid = comment.uid;
    this.editingCommentContent = comment.contents;
  }

  // 수정 취소
  private cancelEdit() {
    this.editingCommentUid = '';
    this.editingCommentContent = '';
  }

  // 수정 저장
  private saveEdit(commentUid: string) {
    if (!this.editingCommentContent.trim()) {
      this.$message.warning('댓글 내용을 입력해주세요.');
      return;
    }
    updateComment(commentUid, { contents: this.editingCommentContent }).then(() => {
      this.$message.success('댓글이 수정되었습니다.');
      this.cancelEdit();
      this.getCommentList();
    }).catch((error) => {
      this.$message.warning(error.response?.data || '댓글 수정에 실패했습니다.');
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

.comment-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  flex-shrink: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

.comment-edit-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-edit-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px 14px;
  border: 1px solid #073DFF;
  border-radius: 8px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  resize: none;
  box-sizing: border-box;
}

.comment-edit-actions {
  display: flex;
  justify-content: flex-end;
}

.date_wr {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 13px;
  font-weight: 400;
  line-height: 100%;
}

.comment-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right:clamp(20px, 5vw, 60px);
}

.el-dropdown-link {
  cursor: pointer;
  color: #888;
  font-size: 18px;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.el-dropdown-link:hover {
  color: #222;
}

.el-dropdown-link i {
  font-size: 18px;
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
