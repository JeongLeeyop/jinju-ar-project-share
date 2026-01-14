<template>
  <div class="user-wrap  post-manage">
    <div class="post-contents">
    <div class="post-manage__left post-group">
    <ul>
      <li @click="handleClickTab()" class="post-group__item">TFSE</li>
      <li class="post-group__item post-group--active">피드백</li>
    </ul>
  </div>
    <div class="post-right">
    <div class="user-title">
			<div class="tl__box">
				<p class="tl">피드백 관리</p>
				<p class="usernumber"> 전체 게시글 {{ totalElements }}개</p>
			</div>
			<div class="user__tab">
        <div class="user__subtab">
            <el-select class="board" v-model="listQuery.searchType">
            <!-- <el-option label="제목" value="title" /> -->
            <el-option label="내용" value="content"/>
            <el-option label="작성자" value="writer"/>
            <el-option
              v-for="searchField in searchFieldList"
              :key="searchField.uid"
              :label="searchField.fieldName"
              :value="searchField.uid"
            />
           </el-select>
          <el-input v-model="listQuery.searchValue" @keyup.native.enter="handleSearch()"/>
          <el-button class="search-btn" @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
        <!-- <button class="tool-btn" @click="handleAddPost('')">+ 게시글 추가</button> -->
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
            <el-table-column label="제목" min-width="200">
              <template slot-scope="scope">
                {{ scope.row.selfFeedbackDate }} {{ scope.row.writer }} 고객님께서 작성하신 글입니다.
              </template>
            </el-table-column>
            <el-table-column
              label="작성자"
              width="150"
              prop="writer"
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
                <!-- <el-button
                  type="primary"
                  size="small"
                  @click="updatePost(scope.row)"
                  @click.stop
                >
                  수정
                </el-button> -->
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
import { deleteFeedback, getFeedbackList } from '@/api/tfse';
import { IBoard, IField } from '@/types/board';
import { ITfse } from '@/types/tfse';
import Pagination from '@/components/Pagination/index.vue';

@Component({
  name: 'PostList',
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getPostList();
  }

  private listLoading: boolean = true;

  private searchFieldList: IField[] = [];

  private postList: ITfse[] = [];

  private totalElements: number = 0;

  private listQuery = {
    searchType: 'content',
    searchValue: '',
    page: 0,
    size: 10,
  };

  private handleSearch() {
    this.listQuery.page = 1;
    this.getPostList();
  }

  private getPostList() {
    this.listLoading = true;
    getFeedbackList(this.listQuery).then((res) => {
      if (res.data.content.length === 0 && this.listQuery.page !== 0) {
        this.listQuery.page -= 1;
        this.getPostList();
      }
      this.listLoading = false;
      this.postList = res.data.content;
      this.totalElements = res.data.totalElements;
    });
  }

  private handleRowClick(post: ITfse) {
    this.$router.push({ name: 'FeedBackDetail', params: { postUid: post.idx.toString() } });
  }

  private handleAddPost() {
    this.$router.push({ name: 'FeedBackAdd' });
  }

  private updatePost(post: ITfse) {
    this.$router.push({ name: 'FeedBackUpdate', params: { postUid: post.idx.toString() } });
  }

  private deletePost(post: ITfse) {
    this.$confirm('정말 피드백을 삭제하시겠습니까?', '게시글 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteFeedback(post.idx).then(() => {
        this.$message.success('해당 피드백이 삭제되었습니다.');
        this.getPostList();
      });
    });
  }

  private getIndex(index: number) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleClickTab() {
    this.$router.push({ name: 'Tfse' });
  }
}
</script>

<style lang="scss">
  @import '@/assets/css/views/board.scss';
  @import '@/assets/css/views/post.scss';
</style>
