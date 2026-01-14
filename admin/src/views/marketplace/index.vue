<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">커뮤니티 장터</h1>
        <p class="page-subtitle">총 {{ totalElements }}개의 상품</p>
      </div>
      <div class="header-right">
        <el-select v-model="listQuery.channelUid" placeholder="커뮤니티 선택" clearable @change="handleSearch" class="channel-select">
          <el-option v-for="channel in channelList" :key="channel.uid" :label="channel.name" :value="channel.uid" />
        </el-select>
        <el-input
          v-model="listQuery.searchValue"
          placeholder="상품명 검색"
          prefix-icon="el-icon-search"
          class="search-input"
          @keyup.enter.native="handleSearch"
        />
      </div>
    </div>

    <div class="content-card" v-loading="loading">
      <el-table :data="productList" style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column type="index" label="번호" width="70" align="center" />
        <el-table-column label="상품" min-width="250">
          <template slot-scope="scope">
            <div class="product-info">
              <img v-if="scope.row.imageUid" :src="getImageUrl(scope.row.imageUid)" class="product-image" />
              <div class="product-text">
                <span class="product-name">{{ scope.row.name }}</span>
                <span class="product-channel">{{ scope.row.channelName || '-' }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="가격" width="120" align="right">
          <template slot-scope="scope">
            <span class="price">{{ formatPrice(scope.row.price) }}원</span>
          </template>
        </el-table-column>
        <el-table-column label="판매자" width="120">
          <template slot-scope="scope">
            {{ scope.row.sellerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="상태" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="등록일" width="150" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createDate) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="totalElements"
          :page-size="listQuery.size"
          :current-page.sync="listQuery.page"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getMarketplaceProducts, getAllMarketplaces } from '@/api/marketplace';
import { getAllChannels } from '@/api/channel';

@Component({
  name: 'MarketplaceList',
})
export default class extends Vue {
  private loading = false;
  private productList: any[] = [];
  private channelList: any[] = [];
  private totalElements = 0;
  private listQuery = {
    page: 1,
    size: 10,
    searchValue: '',
    channelUid: '',
  };

  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  mounted() {
    this.loadChannels();
    this.loadProducts();
  }

  private async loadChannels() {
    try {
      const res = await getAllChannels({ page: 1, size: 100 });
      this.channelList = res.data.content || [];
    } catch (error) {
      console.error('Failed to load channels:', error);
    }
  }

  private async loadProducts() {
    this.loading = true;
    try {
      const res = await getMarketplaceProducts(this.listQuery);
      this.productList = res.data.content || res.data || [];
      this.totalElements = res.data.totalElements || this.productList.length;
    } catch (error) {
      console.error('Failed to load products:', error);
      this.$message.error('상품 목록을 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    this.listQuery.page = 1;
    this.loadProducts();
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadProducts();
  }

  private getImageUrl(uid: string) {
    return `${process.env.VUE_APP_BASE_API}/attached-file/${uid}`;
  }

  private formatPrice(price: number) {
    return (price || 0).toLocaleString();
  }

  private formatDate(date: string) {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('ko-KR');
  }

  private getStatusName(status: string) {
    const statuses: { [key: string]: string } = {
      SALE: '판매중',
      SOLD: '판매완료',
      HIDDEN: '숨김',
    };
    return statuses[status] || status || '판매중';
  }

  private getStatusType(status: string) {
    const types: { [key: string]: string } = {
      SALE: 'success',
      SOLD: 'info',
      HIDDEN: 'warning',
    };
    return types[status] || 'primary';
  }
}
</script>

<style scoped lang="scss">
.page-wrap {
  padding: 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #222;
    margin-bottom: 4px;
  }
  
  .page-subtitle {
    font-size: 14px;
    color: #888;
  }
  
  .header-right {
    display: flex;
    gap: 12px;
  }
  
  .channel-select {
    width: 200px;
  }
  
  .search-input {
    width: 200px;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .product-image {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    object-fit: cover;
  }
  
  .product-text {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .product-name {
      font-weight: 600;
      color: #222;
    }
    
    .product-channel {
      font-size: 12px;
      color: #888;
    }
  }
}

.price {
  font-weight: 600;
  color: #073DFF;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
