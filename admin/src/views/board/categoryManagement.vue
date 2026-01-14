<template>
  <div class="user-wrap">
    <div class="user-title">
			<div class="tl__box">
				<p class="tl">카테고리 관리</p>
				<p class="usernumber"> 전체 카테고리 {{ totalElements }} 개</p>
			</div>
			<div class="user__tab">
				<div class="user__subtab">
          <el-select class="board" v-model="listQuery.searchType">
           <el-option v-for="searchType in searchTypeList" :key="searchType.value" :value="searchType.value" :label="searchType.label"/>
          </el-select>
          <el-input v-model="listQuery.searchValue" @keypress.enter.native="handleSearch()"/>
          <el-button class="search-btn" @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
        <button class="tool-btn" @click="addCategory('')">+ 카테고리 추가</button>
			</div>
    </div>

    <div class="user-content">
      <el-table
        ref="boardCategoryTable"
        class="click-table"
        v-loading="listLoading"
        :data="boardCategoryList"
        @row-click="expandRow"
      >
        <el-table-column
          label="#"
          type="expand"
          width="30"
        >
          <template slot-scope="scope">
            <el-tree
              :data="scope.row.children"
              class="category-tree"
              default-expand-all
            >
              <span class="custom-tree-node" slot-scope="{data}">
                {{ data.name }}
              </span>
            </el-tree>
          </template>
        </el-table-column>
        <el-table-column
          label="카테고리 이름"
          prop="name"
          min-width="150"
        />
        <el-table-column
          label="카테고리 설명"
          prop="descript"
          min-width="200"
        />
        <el-table-column
          label="생성일"
          width="200"
        >
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="200">
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="small"
              @click="updateCategory(scope.row.uid)"
              @click.stop
            >
              수정
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="deleteCategory(scope.row)"
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
        @pagination="getBoardCategoryList()"
      />
    </div>
    <el-dialog
      width="750px"
      :title="updateCategoryUid ? '카테고리 수정' : '카테고리 추가'"
      :visible.sync="formVisible"
      class="dialog-wrap"
    >
      <CategoryForm
        v-if="formVisible"
        :uid="updateCategoryUid"
        @closeDialog="closeDialog"
        @finishSave="finishSave"
      />
    </el-dialog>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { listBoardCategory, deleteBoardCategory } from '@/api/boardCategory';
import { ICategory } from '@/types/board';
import Pagination from '@/components/Pagination/index.vue';
import CategoryForm from './components/CategoryForm.vue';

@Component({
  name: 'categoryManagement',
  components: {
    Pagination,
    CategoryForm,
  },
})
export default class extends Vue {
  mounted() {
    this.getBoardCategoryList();
  }

  private boardCategoryList: ICategory[] = [];

  private listLoading: boolean = true;

  private totalElements: number = 0;

  private listQuery = {
    page: 1,
    size: 10,
    searchType: 'name',
    searchValue: '',
  };

  private formVisible: boolean = false;

  private updateCategoryUid: string | null = null;

  private searchTypeList = [
    {
      label: '카테고리 명',
      value: 'name',
    },
    {
      label: '설명',
      value: 'descript',
    },
  ];

  private getBoardCategoryList() {
    this.listLoading = true;
    listBoardCategory(this.listQuery).then((res) => {
      if (res.data.content.length === 0 && this.listQuery.page !== 0) {
        this.listQuery.page -= 1;
        this.getBoardCategoryList();
      }
      this.boardCategoryList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.listLoading = false;
    });
  }

  private addCategory() {
    this.formVisible = true;
    this.updateCategoryUid = null;
  }

  private updateCategory(uid: string) {
    this.formVisible = true;
    this.updateCategoryUid = uid;
  }

  private deleteCategory(category: ICategory) {
    this.$confirm(`정말 카테고리 ${category.name}을(를) 삭제하시겠습니까?`, '카테고리 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteBoardCategory(category.uid).then(() => {
        this.$message.success('카테고리가 삭제되었습니다.');
        this.getBoardCategoryList();
      }).catch((error) => {
        this.$message.error(error.response.data.message);
      });
    });
  }

  private closeDialog() {
    this.formVisible = false;
    this.updateCategoryUid = '';
  }

  private finishSave() {
    this.closeDialog();
    this.listQuery.page = 0;
    this.getBoardCategoryList();
  }

  private expandRow(row: ICategory) {
    (this.$refs.boardCategoryTable as any).toggleRowExpansion(row);
  }

  private handleSearch() {
    this.listQuery.page = 0;
    this.getBoardCategoryList();
  }
}
</script>
<style lang="scss">
@import '@/assets/css/views/board.scss';
@import '@/assets/css/views/boardCategory.scss';
</style>
