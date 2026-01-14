<template>
  <div class="user-wrap">
    <div class="user-title">
			<div class="tl__box">
				<p class="tl">게시판 관리</p>
				<p class="usernumber"> 전체 게시판 {{ totalElements }} 개</p>
			</div>
			<div class="user__tab">
				<div class="user__subtab">
          <el-select class="board" v-model="listQuery.searchType">
           <el-option v-for="searchType in searchTypeList" :key="searchType.value" :value="searchType.value" :label="searchType.label"/>
          </el-select>
          <el-input v-model="listQuery.searchValue" @keypress.enter.native="handleSearch()"/>
          <el-button class="search-btn" @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
				</div>
        <button class="tool-btn" @click="handleModalEnable('')">+ 게시판 추가</button>
			</div>
    </div>

    <div class="user-content">
      <el-table :data="boardList" :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }" v-loading="loading" style="width: 100%;" border>
        <el-table-column width="70" label="번호">
          <template slot-scope="scope">
            {{ getIndex(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column
          label="게시판 명"
          prop="name"
          min-width="120"
        />
        <el-table-column
          label="게시판 스킨"
          prop="boardSkin.name"
          min-width="120"
        />
        <el-table-column
          label="생성일"
          min-width="160"
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
              @click="handleModalEnable(scope.row.uid)"
              @click.stop
            >
              수정
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteBoard(scope.row)"
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
        @pagination="getBoardList()"
      />
    </div>
    <el-dialog
      :title="boardUid ? '게시판 수정' : '게시판 추가'"
      :visible.sync="formModalVisible"
      :before-close="handleClose"
      width="900px"
      class="dialog-wrap"
    >
      <BoardForm
        v-if="formModalVisible"
        :boardUid="boardUid"
        @close="handleModalDisable()"
        @save="handleSave()"
      />
    </el-dialog>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getBoardList, deleteBoard, newBoard } from '@/api/board';
import { getBoardSkinList } from '@/api/boardSkin';
import { IBoard, IBoardSkin } from '@/types/board';
import Pagination from '@/components/Pagination/index.vue';
import BoardForm from './components/BoardForm.vue';
import { BoardModule } from '@/store/modules/board';

@Component({
  name: 'BoardManagement',
  components: {
    BoardForm,
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getBoardList();
    this.getBoardSkinList();
  }

  private boardList: IBoard[] = [];

  private listQuery = {
    searchType: 'name',
    searchValue: '',
    size: 10,
    page: 0,
  };

  private boardUid: string = '';

  private totalElements: number = 0;

  private totalPages: number = 0;

  private loading: boolean = true;

  private formModalVisible: boolean = false;

  private searchTypeList = [
    {
      label: '게시판명',
      value: 'name',
    },
    {
      label: '게시판 스킨',
      value: 'skin',
    },
  ];

  private boardSkinList: IBoardSkin[] = [];

  private getBoardList() {
    this.loading = true;
    getBoardList(this.listQuery).then((res) => {
      if (this.listQuery.page !== 1 && res.data.content.length === 0) {
        this.listQuery.page -= 1;
        this.getBoardList();
      }
      this.boardList = res.data.content;
      this.totalPages = res.data.totalPages;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private getBoardSkinList() {
    getBoardSkinList().then((res) => {
      this.boardSkinList = res.data;
    });
  }

  private handleSearch() {
    this.listQuery.page = 1;
    if (this.listQuery.searchType === 'skin' && this.listQuery.searchValue !== '') {
      this.boardSkinList.forEach((boardSkin: IBoardSkin, i: number) => {
        if (boardSkin.name === this.listQuery.searchValue) {
          this.listQuery.searchValue = this.boardSkinList[i].code;
          this.getBoardList();
          this.listQuery.searchValue = this.boardSkinList[i].name;
        }
      });
    } else this.getBoardList();
  }

  private handleModalEnable(uid: string) {
    this.formModalVisible = true;
    this.boardUid = uid;
  }

  private handleModalDisable() {
    this.formModalVisible = false;
    this.boardUid = '';
  }

  private handleDeleteBoard(board: IBoard) {
    this.$confirm(`정말 ${board.name}을(를) 삭제하시겠습니까?`, '게시판 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteBoard(board.uid).then(() => {
        this.$message.success('성공적으로 게시판이 삭제되었습니다.');
        this.getBoardList();
      }).catch((error) => {
        this.$message.warning(error.response.data);
      });
    });
  }

  private getIndex(index: number) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleSave() {
    this.handleModalDisable();
    this.getBoardList();
  }

  private handleClose(done: any) {
    BoardModule.setForm({ ...newBoard });
    done();
  }
}
</script>
<style lang="scss">
@import '@/assets/css/views/board.scss';
</style>
