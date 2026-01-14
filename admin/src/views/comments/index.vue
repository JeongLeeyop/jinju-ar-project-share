<template>

<div class="user-wrap">
    <div class="user-title">
			<div class="tl__box">
				<p class="tl">댓글 관리</p>
				<p class="usernumber"> 전체 댓글 {{ totalElements }} 개</p>
			</div>
			<div class="user__tab">
				<div class="user__subtab">
          <el-select class="board" v-model="listQuery.searchType">
           <el-option v-for="searchType in searchTypeList" :key="searchType.value" :value="searchType.value" :label="searchType.label"/>
          </el-select>
          <el-input v-model="listQuery.searchValue" @keypress.enter.native="handleSearch()"/>
          <el-button class="search-btn" @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
        <button class="tool-btn" @click="handleSearch('')">검색</button>
			</div>
    </div>
    <div class="user-content">
      <el-table :data="commentsList" :highlight-current-row="false" v-loading="loading" class="comments-table" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }" border>
        <el-table-column
          label="게시글 명"
          prop="postTitle"
          min-width="120"
        />
        <el-table-column
          label="댓글 내용"
          min-width="200"
        >
          <template slot-scope="scope" >
            <a href="javascript:void(0);" class="comments-table__comments">{{ scope.row.contents}}</a>
          </template>
        </el-table-column>
        <el-table-column
          label="작성자"
          prop="writer"
          min-width="100"
        />
        <el-table-column
          label="작성일"
          min-width="120"
        >
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate }}
          </template>
        </el-table-column>
         <el-table-column
          label="숨기기"
          width="120"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.hide">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          label="관리"
          width="200"
        >
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
            >
              바로가기
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteComment(scope.row)"
              @click.stop
            >
              삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getCommentList()"
      />
    </div>
    <el-dialog
      title="작성자 정보"
      :visible.sync="writerInfoVisible"
      width="350px"
      class="dialog-wrap"
    >
      <WriterInfo
        v-if="writerInfoVisible"
        @close="writerInfoClose()"
      />
    </el-dialog>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import {
  getCommentList,
  deleteComment,
} from '@/api/comment';
import { IComment } from '@/types/board';
import Pagination from '@/components/Pagination/index.vue';
import WriterInfo from './components/writerInfo.vue';

@Component({
  name: 'CommentsManagement',
  components: {
    WriterInfo,
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getCommentList();
  }

  private commentsList: IComment[] = [];

  private listQuery = {
    searchType: 'contents',
    searchValue: '',
    size: 10,
    page: 0,
  };

  private loading: boolean = false;

  private writerInfoVisible: boolean = false;

  private totalElements: number = 0;

  private totalPages: number = 0;

  private searchTypeList = [
    {
      label: '댓글 내용',
      value: 'contents',
    },
    {
      label: '작성자',
      value: 'writer',
    },
  ];

  private getCommentList() {
    this.loading = true;
    getCommentList(this.listQuery).then((res) => {
      if (this.listQuery.page !== 1 && res.data.content.length === 0) {
        this.listQuery.page -= 1;
        this.getCommentList();
      }
      this.commentsList = res.data.content;
      this.totalPages = res.data.totalPages;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private deleteComment(comment: IComment) {
    this.$confirm('정말 댓글을 삭제하시겠습니까?', '댓글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteComment(comment.uid).then(() => {
        this.$message.success('성공적으로 댓글이 삭제되었습니다.');
        this.getCommentList();
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getCommentList();
  }

  private ViewWriterInfo() {
    this.writerInfoVisible = true;
  }

  private writerInfoClose() {
    this.writerInfoVisible = false;
  }
}
</script>
<style lang="scss">
@import '@/assets/css/views/board.scss';
</style>
