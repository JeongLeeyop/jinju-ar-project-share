<template>
  <div class="user-wrap  post-manage">
    <div class="post-contents">

    <PostGroup :listQuery="listQuery" :boardList="boardList" @clickBoard="handleClickBoard"/>

    <div class="post-right">
    <div class="user-title">
			<div class="tl__box">
				<p class="tl">게시글 관리</p>
				<p class="usernumber"> 전체 게시글 {{ totalElements }} 개</p>
			</div>
			<div class="user__tab">
        <div class="user__subtab">
            <el-select class="board" v-model="listQuery.searchType">
            <el-option label="제목" value="title" />
            <el-option label="내용" value="content"/>
            <el-option label="작성자" value="writer"/>
            <el-option
              v-for="searchField in searchFieldList"
              :key="searchField.uid"
              :label="searchField.fieldName"
              :value="searchField.uid"
            />
           </el-select>
          <el-input v-model="listQuery.searchValue" @keypress.enter.native="handleSearch()"/>
          <el-button class="search-btn" @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
        <button class="tool-btn" @click="handleAddPost('')">+ 게시글 추가</button>
			</div>
    </div>

    <div class="post-contents">
        <div class="post-manage__body post-manage__body">
          <el-table
            v-loading="listLoading"
            class="click-table"
            row-key="uid"
            default-expand-all
            :tree-props="{children: 'children'}"
            :data="postList"
            @row-click="handleRowClick"
          >
            <el-table-column width="50" label="No.">
              <template slot-scope="scope">
                {{ getIndex(scope.$index) }}
              </template>
            </el-table-column>
            <el-table-column
              v-if="!listQuery.boardUid"
              label="게시판"
              width="150"
            >
              <template slot-scope="scope">
                {{ getBoardName(scope.row.boardUid) }}
              </template>
            </el-table-column>
            <el-table-column label="제목" min-width="200">
              <template slot-scope="scope">
                <b style="font-weight: 800;" v-if="scope.row.parentUid"> └[답변]</b> {{ scope.row.title }}
              <br><el-tag v-for="tag in scope.row.tags" :key="tag">#{{ tag.tag }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              label="작성자"
              width="150"
              prop="writer"
            />
            <el-table-column
              label="조회수"
              width="100"
              prop="viewCount"
            />
            <el-table-column
              label="작성일"
              width="200"
            >
              <template slot-scope="scope">
                {{ scope.row.createDate | parseDate }}
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
                  @click="updatePost(scope.row)"
                  @click.stop
                >
                  수정
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="deletePost(scope.row)"
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
      @pagination="getPostList()"
    />
        </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { deletePost, getPostList } from '@/api/post';
import { getBoardList } from '@/api/board';
import { IBoard, IField } from '@/types/board';
import { IPost } from '@/types/post';
import Pagination from '@/components/Pagination/index.vue';
import PostGroup from './components/PostGroup.vue';

@Component({
  name: 'PostList',
  components: {
    PostGroup,
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.init();
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private listLoading: boolean = true;

  private boardList: IBoard[] = [];

  private boardFieldList: IField[] = [];

  private searchFieldList: IField[] = [];

  private postList: IPost[] = [];

  private searchBoardUid: string = ''; // 검색 완료된 게시판 고유값

  private totalElements: number = 0;

  private listQuery = {
    boardUid: '',
    searchType: 'title',
    searchValue: '',
    page: 0,
    size: 10,
  };

  private defaultCategory = {
    uid: '',
    categoryName: '전체',
    categoryDescript: '',
    children: [],
    useState: true,
    depth: 0,
    viewOrder: 0,
    createDate: '',
  }

  private init() {
    getBoardList(this.listQuery).then((res) => {
      this.boardList = res.data.content;
      this.getPostList();
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getPostList();
  }

  private getPostList() {
    this.listLoading = true;
    getPostList(this.listQuery).then((res) => {
      if (res.data.content.length === 0 && this.listQuery.page !== 0) {
        this.listQuery.page -= 1;
        this.getPostList();
      }
      this.listLoading = false;
      this.postList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.setBoard(this.listQuery.boardUid);
      console.log(res);
    });
  }

  private searchPost() {
    this.listQuery.page = 1;
    this.searchBoardUid = this.listQuery.boardUid;
    this.getPostList();
  }

  private handleRowClick(post: IPost) {
    this.$router.push({ name: 'PostDetail', params: { postUid: post.uid } });
  }

  private handleChangeBoard(boardUid: string) {
    const boardIndex = this.boardList.findIndex((board: IBoard) => board.uid === boardUid);
    if (boardIndex > -1) {
      const board: IBoard = this.boardList[boardIndex];
      this.searchFieldList = board.fieldList.filter((bf: IField) => bf.searchState);
      this.listQuery.searchType = 'title';
    } else {
      this.listQuery.searchType = 'title';
      this.searchFieldList = [];
    }
  }

  private setBoard(boardUid: string) {
    const boardIndex = this.boardList.findIndex((board: IBoard) => board.uid === boardUid);
    if (boardIndex > -1) {
      const targetBoard: IBoard = this.boardList[boardIndex];
      this.boardFieldList = targetBoard.fieldList.filter((bf: IField, index: number) => index === 0);
      this.listQuery.searchType = 'title';
      if (this.searchBoardUid !== this.listQuery.boardUid) {
        this.searchFieldList = targetBoard.fieldList.filter((bf: IField) => bf.searchState);
      }
    }
  }

  private handleAddPost() {
    this.$router.push({ name: 'PostAdd' });
  }

  private updatePost(post: IPost) {
    this.$router.push({ name: 'PostUpdate', params: { postUid: post.uid } });
  }

  private deletePost(post: IPost) {
    this.$confirm('정말 게시글을 삭제하시겠습니까?', '게시글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deletePost(post.uid).then(() => {
        this.$message.success('게시글이 삭제되었습니다.');
        this.getPostList();
      });
    });
  }

  private getBoardName(boardUid: string) {
    const boardIndex = this.boardList.findIndex((board: IBoard) => board.uid === boardUid);
    if (boardIndex > -1) return (this.boardList[boardIndex] as IBoard).name;
    return '';
  }

  private handleClickBoard(boardUid: string) {
    this.listQuery.boardUid = boardUid;
    this.listQuery.page = 1;
    this.getPostList();
  }

  private getIndex(index: number) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }
}
</script>

<style lang="scss">
  @import '@/assets/css/views/board.scss';
  @import '@/assets/css/views/post.scss';
</style>
