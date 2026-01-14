<template>
  <div
    v-if="post"
    class="post-detail"
  >
    <div class="post-detail__header">
      Tfse 게시글 보기
    </div>
    <div class="post-detail__body" v-loading="loading">
      <table class="post-detail__table">
        <tr>
          <th>제목</th>
          <td>
            {{ post.createDate | parseDate }} {{ post.writer }} 고객님께서 작성하신 글입니다.
          </td>
        </tr>
        <tr>
          <th>포만감 지수</th>
          <td>
            <pre v-html="post.satietyScore" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>먹은 음식</th>
          <td>
            <pre v-html="post.foodText" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>느낀 감정</th>
          <td>
            <pre v-html="post.emotionText" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>작성자</th>
          <td>{{ post.writer }}</td>
        </tr>
        <tr>
          <th>작성 일시</th>
          <td>{{ post.createDate | parseDateTime }}</td>
        </tr>
        <tr>
          <th>좋아요 수</th>
          <td>{{ post.likeCount }}</td>
        </tr>
      </table>
    </div>
    <div class="community__comment" v-loading="commentLoading">
        <div class="community__comment-count bd7">댓글 <span class="num">{{ post.commentCount }}</span>
        </div>
        <div v-for="comment in commentList" :key="comment.uid" class="community__comment-list">
          <div class="community__comment-item">
            <!-- <div class="community__comment-item__profile"></div> -->
            <div class="community__comment-item__content">
              <!-- <p class="community__comment-item__best">베스트리뷰</p> -->
              <p class="community__comment-item__nickname bd6">{{ comment.writer }} 고객님</p>
              <p class="community__comment-item__txt">{{ comment.contents }}</p>
              <p class="community__comment-item__date bd3">{{ comment.createDate | parseDate }}</p>
              <button @click="replyHandler(comment.uid, $event)" class="community__comment-item__button reply">+
                대댓글 달기</button>
                <button @click="replyCancelHandler($event)" class="community__comment-item__button cancel none">
                  대댓글 취소</button>
              <button @click="deleteHandler(comment.uid)" class="community__comment-item__button delete">삭제</button>
            </div>
          </div>
          <div v-for="reply in comment.children" :key="reply.uid"
            class="community__comment-item community__comment-item--v2">
            <!-- <div class="community__comment-item__profile"></div> -->
            <div class="community__comment-item__content">
              <!-- <p class="community__comment-item__best">베스트리뷰</p> -->
              <p class="community__comment-item__nickname bd6">{{ reply.writer }} 고객님</p>
              <p class="community__comment-item__txt">{{ reply.contents }}</p>
              <p class="community__comment-item__date bd3">{{ reply.createDate | parseDate }}</p>
              <button @click="deleteHandler(reply.uid)" class="community__comment-item__date delete">삭제</button>
            </div>
          </div>
        </div>
        <Pagination :total="totalElements" :page.sync="listQuery.page" :limit.sync="listQuery.size"
							@pagination="getCommentList" />

        <div class="community__comment-input">
					<input v-on:keyup.enter="addComment" ref="contents" v-model="inputComment.contents" type="text" placeholder="댓글을 입력해주세요">
					<button @click="addComment" class="community__comment-input__send"></button>
				</div>
      </div>

    <div class="post-detail__footer align--center">
      <el-button
        type="info"
        size="small"
        @click="listPost"
      >
        목록
      </el-button>
      <!-- <el-button
        type="success"
        size="small"
        @click="updatePost"
      >
        수정
      </el-button> -->
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
import { deletePost, viewPost } from '@/api/tfse';
import { addComment, deleteComment, getCommentList } from '@/api/tfseComment';
import Pagination from '@/components/Pagination/index.vue';
import { IData, IPost } from '@/types/post';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  name: 'PostDetail',
  components: {
		Pagination,
	},
})
export default class extends Vue {
  mounted() {
    this.getTfse();
		this.getCommentList();
  }

  private post: IPost | null = null;

  private loading: boolean = true;

  private deletePost() {
    this.$confirm('정말 게시글을 삭제하시겠습니까?', '게시글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deletePost(Number(this.$route.params.postUid)).then(() => {
        this.$message.success('게시글이 삭제되었습니다.');
        this.$router.push({ name: 'Tfse' });
      });
    });
  }

  private getTfse() {
		this.loading = true;
		viewPost(Number(this.$route.params.postUid)).then((res) => {
			this.loading = false;
			this.post = res.data;
		}).catch(() => {
      this.$message.error('Tfse 게시글을 불러오는데 실패했습니다.');
      this.$router.push({ name: 'Tfse' });
    });
	}

  private listPost() {
    this.$router.push({ name: 'Tfse' });
  }

  private updatePost() {
    this.$router.push({ name: 'TfseUpdate', params: { postUid: this.$route.params.postUid } });
  }

  private getInputValue(boardFieldUid: string) {
    if (this.post !== null) {
      const dataIndex = (this.post as IPost).dataList.findIndex((data: IData) => data.fieldUid === boardFieldUid);
      if (dataIndex > -1) return (this.post as IPost).dataList[dataIndex].inputValue;
    }
    return '';
  }

  private replyPost() {
    this.$router.push({ name: 'TfseReply', params: { parentUid: this.$route.params.postUid } });
  }

  private deleteHandler(uid: string) {
		this.$confirm('정말 댓글을 삭제하시겠습니까?').then(() => {
			this.loading = true;
			deleteComment(uid).then(() => {
				this.$message.success('댓글이 삭제되었습니다.');
				this.getTfse();
				this.getCommentList();
				this.inputComment.contents = '';
				this.inputComment.parentUid = '';
				this.loading = false;
				this.resetBtn();
			}).catch((error) => {
				this.$message.error(error.response.data.message || '게시글을 삭제하는데 실패했습니다.');
			});
		});
	}

  private replyHandler(parentUid: String, event: any) {
		if (this.$refs.contents) {
			(this.$refs.contents as HTMLElement).focus();
			event.target.classList.add('none');
			event.target.nextSibling.classList.remove('none');
			if (event.target.nextSibling.nextSibling.classList) {
				event.target.nextSibling.nextSibling.classList.add('none');
			}
			this.inputComment.parentUid = parentUid;
		}
	}

	private replyCancelHandler(event:any) {
		if (this.$refs.contents) {
			event.target.previousSibling.classList.remove('none');
			event.target.classList.add('none');
			if (event.target.nextSibling.classList) {
				event.target.nextSibling.classList.remove('none');
			}
			this.inputComment.parentUid = '';
		}
	}

  private listQuery = {
		tfseIdx: this.$route.params.postUid,
		page: 0,
		size: 10,
	};

  private commentList: any = {
		uid: '',
		tfseIdx: '',
		userUid: '',
		writer: '',
		contents: '',
		depth: '',
		viewOrder: '',
		hide: '',
		createDate: '',
		children: {},
	}

	private totalElements = 0;

	private commentLoading = true;

  private getCommentList() {
		this.commentLoading = true;
		getCommentList(this.listQuery).then((res) => {
			this.commentList = res.data.content;
			this.totalElements = res.data.totalElements;
			this.commentLoading = false;
		});
	}

  private inputComment: any = {
		tfseIdx: this.$route.params.postUid,
		contents: '',
		parentUid: '',
	}

  private addComment() {
			this.loading = true;
			addComment(this.inputComment).then(() => {
				this.getTfse();
				this.getCommentList();
				this.inputComment.contents = '';
				this.inputComment.parentUid = '';
				this.loading = false;
				this.resetBtn();
			});
		}

  private resetBtn() {
		const cancelElements = document.querySelectorAll('.community__comment-item__button.cancel');
			cancelElements.forEach((element) => {
				element.classList.add('none');
			});
			const replyElements = document.querySelectorAll('.community__comment-item__button.reply');
			replyElements.forEach((element) => {
				element.classList.remove('none');
			});
			const deleteElements = document.querySelectorAll('.community__comment-item__button.delete');
			deleteElements.forEach((element) => {
				element.classList.remove('none');
			});
	}
}
</script>

<style lang="scss">
@import '@/assets/css/views/tfse.scss';
  .post-detail {
    padding: 80px 34px 20px 34px;
    // margin-left: 155px;
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
