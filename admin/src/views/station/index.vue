<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">거점관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}개</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" slot="prepend" placeholder="이름">
            <el-option label="거점명" value="name"></el-option>
            <el-option label="거점주소" value="address"></el-option>
            <el-option label="우편번호" value="postcode"></el-option>
          </el-select>
          <el-input v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>

        <router-link :to="{ name: 'StationAdd' }">추가 +</router-link>
      </div>
    </div>

    <div class="user-content">
      <el-table :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }" :data="shopList" border style="width: 100%">
        <el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="거점명" width=""></el-table-column>
        <el-table-column label="거점주소" width="700">
          <template slot-scope="scope">
            {{ scope.row.address }} {{ scope.row.addressDetail }}
          </template>
        </el-table-column>
        <el-table-column prop="postCode" label="우편번호" width=""></el-table-column>
        <el-table-column prop="useStatus" label="사용유무" width="100">
        <template slot-scope="scope">
          {{ scope.row.useStatus === true ? 'Y' : 'N' }}
        </template>
        </el-table-column>
        <el-table-column label="등록일" width="">
          <template slot-scope="scope">
            {{ scope.row.createdDate | parseDate }}
          </template>
        </el-table-column>
        <el-table-column label="관리">
          <template slot-scope="scope">
						<router-link class="detail-button" :to="{ name: 'StationUpdate', params: { idx: scope.row.idx } }">보기</router-link>
            <!-- <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">삭제</el-button> -->
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalElements"
        @current-change="setPage"
      />
    </div>
  </div>
</template>

<script lang='ts'>
import { Component, Vue } from 'vue-property-decorator';
import { getStationList, deleteStation } from '@/api/station';

@Component({
  name: 'StationList',
})
export default class extends Vue {
  mounted() {
    this.getStationList();
  }

  private shopList: any = [];

  private loading = true;

  private listQuery = {
    searchType: 'name',
    searchValue: '',
    page: 1,
    size: 10,
  }

  private totalElements = 0;

  private getStationList() {
    this.loading = true;
    getStationList(this.listQuery).then((res) => {
      this.shopList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private setPage(page: number) {
    this.listQuery.page = page;
    this.getStationList();
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.getStationList();
  }

  private handleDelete(shop: any) {
    this.$confirm('정말 해당 거점을 삭제 하시겠습니까?', '거점 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'error',
    }).then(() => {
      deleteStation(shop.idx).then(() => {
        this.$message.success('거점이 삭제 되었습니다.');
        this.getStationList();
      });
    });
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }
}
</script>
