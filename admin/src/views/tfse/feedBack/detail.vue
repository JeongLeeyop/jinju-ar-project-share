<template>
  <div
    v-if="post"
    class="post-detail"
  >
    <div class="post-detail__header">
      피드백 보기
    </div>
    <div class="post-detail__body" v-loading="loading">
      <table class="post-detail__table">
        <tr>
          <th>제목</th>
          <td>
            <!-- {{ post.createDate | parseDate }} {{ post.writer }} 고객님께서 작성하신 글입니다. -->
            {{ post.createDate | parseDate }} 작성된 글입니다.
          </td>
        </tr>
        <tr>
          <th>질문1</th>
          <td>
            <pre v-html="post.question1" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>질문2</th>
          <td>
            <pre v-html="post.question2" style="font-size: 1em" />
          </td>
        </tr>
        <tr>
          <th>질문3</th>
          <td>
            <pre v-html="post.question3" style="font-size: 1em" />
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
import { Vue, Component } from 'vue-property-decorator';
import { deleteFeedback, getFeedback } from '@/api/tfse';
import { IBoard, IField } from '@/types/board';
import { IPost, IData } from '@/types/post';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  name: 'FeedBackDetail',
  components: {
		Pagination,
	},
})
export default class extends Vue {
  mounted() {
    this.getTfse();
  }

  private post: IPost | null = null;

  private board: IBoard | null = null;

  private loading: boolean = true;

  private apiUrl = process.env.VUE_APP_BASE_API;

  private deletePost() {
    this.$confirm('정말 피드백을 삭제하시겠습니까?', '게시글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteFeedback(Number(this.$route.params.postUid)).then(() => {
        this.$message.success('해당 피드백이 삭제되었습니다.');
        this.$router.push({ name: 'FeedBack' });
      });
    });
  }

  private getTfse() {
		this.loading = true;
		getFeedback(Number(this.$route.params.postUid)).then((res) => {
			this.loading = false;
			this.post = res.data;
		}).catch(() => {
      this.$message.error('Tfse 게시글을 불러오는데 실패했습니다.');
      this.$router.push({ name: 'FeedBack' });
    });
	}

  private listPost() {
    this.$router.push({ name: 'FeedBack' });
  }

  private updatePost() {
    this.$router.push({ name: 'FeedBackUpdate', params: { postUid: this.$route.params.postUid } });
  }

  private getInputValue(boardFieldUid: string) {
    if (this.post !== null) {
      const dataIndex = (this.post as IPost).dataList.findIndex((data: IData) => data.fieldUid === boardFieldUid);
      if (dataIndex > -1) return (this.post as IPost).dataList[dataIndex].inputValue;
    }
    return '';
  }

  private replyPost() {
    this.$router.push({ name: 'FeedBackReply', params: { parentUid: this.$route.params.postUid } });
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
