<template>
  <div class="marketplace-page">
    <CommunitySidebar 
      :selectedSpaceId="currentMarketplaceUid || 'marketplace'" 
      @space-select="handleSpaceSelect"
      @open-write-modal="openWriteModal"
    />

    <div class="marketplace-main">
      <!-- 상품 타입 필터 -->
      <div class="filter-section">
        <button 
          class="filter-btn" 
          :class="{ active: activeFilter === 'SALE' }"
          @click="setFilter('SALE')"
        >
          판매
        </button>
        <button 
          class="filter-btn" 
          :class="{ active: activeFilter === 'SHARE' }"
          @click="setFilter('SHARE')"
        >
          나눔
        </button>
        <button 
          class="filter-btn" 
          :class="{ active: activeFilter === 'REQUEST' }"
          @click="setFilter('REQUEST')"
        >
          요청
        </button>
      </div>

      <!-- 로딩 -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <p>로딩 중...</p>
      </div>

      <!-- 상품 그리드 -->
      <div v-else-if="filteredProducts.length > 0" class="products-grid">
        <div
          v-for="product in filteredProducts"
          :key="product.uid"
          class="product-card"
          @click="openProductDetail(product)"
        >
          <div class="product-image-wrapper" :class="{ 'has-status': getStatusBadge(product) }">
            <img :src="getProductImage(product)" :alt="product.title" class="product-image" />
            <div v-if="getStatusBadge(product)" class="status-overlay"></div>
            <div v-if="getStatusBadge(product)" class="status-badge">{{ getStatusBadge(product) }}</div>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
            <p class="product-location">{{ product.location }}</p>
            <p class="product-price">{{ formatPrice(product) }}</p>
          </div>
        </div>
      </div>

      <!-- 상품 없음 -->
      <div v-else class="empty-container">
        <p>등록된 상품이 없습니다</p>
      </div>
    </div>

    <!-- Floating Write Button (Mobile Only) -->
    <button class="write-post-btn-fixed" @click="openWriteModal">
      <span class="btn-text">상품 등록하기</span>
      <svg class="btn-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 5V19M5 12H19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <!-- 상품 등록 모달 -->
    <el-dialog
      :visible.sync="writeModalVisible"
      width="740px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="write-post-modal"
    >
      <div class="write-modal-content">
        <button class="modal-close-btn" @click="closeWriteModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="write-modal-title">장터에 상품을 등록해 보세요.</h3>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">상품 제목</label>
            <input 
              v-model="newPost.title" 
              type="text" 
              placeholder="상품 제목을 작성해주세요."
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">상품 설명</label>
            <textarea 
              v-model="newPost.description" 
              placeholder="상품에 대한 설명을 작성해 주세요."
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">장터 게시 유형</label>
            <div class="type-buttons">
              <button
                class="type-btn"
                :class="{ active: newPost.productType === 'SALE' }"
                @click="newPost.productType = 'SALE'"
              >
                판매
              </button>
              <button
                class="type-btn"
                :class="{ active: newPost.productType === 'SHARE' }"
                @click="newPost.productType = 'SHARE'"
              >
                나눔
              </button>
              <button
                class="type-btn"
                :class="{ active: newPost.productType === 'REQUEST' }"
                @click="newPost.productType = 'REQUEST'"
              >
                요청
              </button>
            </div>
            <input 
              v-if="newPost.productType === 'SALE' || newPost.productType === 'REQUEST'"
              v-model.number="newPost.price" 
              type="number" 
              placeholder="R포인트를 입력해 주세요."
              class="form-input"
              min="0"
            />
          </div>

          <div class="form-group">
            <label class="form-label">진행 장소</label>
            <input 
              v-model="newPost.location" 
              type="text" 
              placeholder="어디에서 진행되는 거래인가요?"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <div class="image-label">
              <label class="form-label">이미지 등록</label>
              <span class="image-count">({{ newPost.images.length }}/4)</span>
            </div>
            <div class="image-upload-area">
              <label class="upload-box" v-if="newPost.images.length < 4">
                <input type="file" accept="image/*" @change="handleImageUpload" hidden />
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H5.93C6.25918 7.00005 6.58329 6.91884 6.87357 6.76359C7.16384 6.60834 7.4113 6.38383 7.594 6.11L8.406 4.89C8.5887 4.61617 8.83616 4.39166 9.12643 4.23641C9.41671 4.08116 9.74082 3.99995 10.07 4H13.93C14.2592 3.99995 14.5833 4.08116 14.8736 4.23641C15.1638 4.39166 15.4113 4.61617 15.594 4.89L16.406 6.11C16.5887 6.38383 16.8362 6.60834 17.1264 6.76359C17.4167 6.91884 17.7408 7.00005 18.07 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V18C21 18.5304 20.7893 19.0391 20.4142 19.4142C20.0391 19.7893 19.5304 20 19 20H5C4.46957 20 3.96086 19.7893 3.58579 19.4142C3.21071 19.0391 3 18.5304 3 18V9Z" stroke="#888888" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M15 13C15 13.7956 14.6839 14.5587 14.1213 15.1213C13.5587 15.6839 12.7956 16 12 16C11.2044 16 10.4413 15.6839 9.87868 15.1213C9.31607 14.5587 9 13.7956 9 13C9 12.2044 9.31607 11.4413 9.87868 10.8787C10.4413 10.3161 11.2044 10 12 10C12.7956 10 13.5587 10.3161 14.1213 10.8787C14.6839 11.4413 15 12.2044 15 13Z" stroke="#888888" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="upload-text">이미지 등록</span>
              </label>
              <div v-for="(imageUrl, index) in newPost.imageUrls" :key="index" class="image-preview">
                <img :src="imageUrl" alt="Preview" />
                <button class="remove-image-btn" @click="removeImage(index)">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 4L4 12M4 4L12 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <button 
            class="submit-post-btn" 
            @click="submitPost"
            :disabled="loading"
          >
            {{ loading ? '등록 중...' : '장터 상품 등록하기' }}
          </button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { 
  getMainMarketplaceProducts,
  getOfflineMarketplaceProducts,
  createProduct, 
  MarketplaceProduct,
  MarketplaceProductCreateRequest 
} from '@/api/marketplace';
import { uploadFile } from '@/api/post';

@Component({
  name: 'Marketplace',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeFilter: 'SALE' | 'SHARE' | 'REQUEST' = 'SALE';
  private writeModalVisible = false;
  private loading = false;

  private newPost = {
    title: '',
    description: '',
    productType: 'SALE' as 'SALE' | 'SHARE' | 'REQUEST',
    price: 0,
    location: '',
    stock: 1,
    images: [] as File[],
    imageUrls: [] as string[],
  };

  private products: MarketplaceProduct[] = [];
  private channelUid = '';
  private canWrite = false;

  // 오프라인 장터 UID (라우트 파라미터로 전달)
  get currentMarketplaceUid(): string | null {
    return this.$route.params.marketplaceUid || null;
  }

  get isOfflineMarketplace(): boolean {
    return !!this.currentMarketplaceUid;
  }

  async mounted() {
    this.channelUid = this.$route.params.domain || '';
    if (this.channelUid) {
      await this.checkPermission();
      await this.loadProducts();
    }
  }

  @Watch('$route.params.marketplaceUid')
  async onMarketplaceChange() {
    await this.loadProducts();
  }

  get filteredProducts() {
    // API에서 category 또는 productType으로 반환될 수 있음
    return this.products.filter(product => 
      (product.productType || (product as any).category) === this.activeFilter
    );
  }

  private async checkPermission() {
    // TODO: 권한 체크 API 호출
    this.canWrite = true;
  }

  private async loadProducts() {
    this.loading = true;
    try {
      let response;
      if (this.isOfflineMarketplace && this.currentMarketplaceUid) {
        // 오프라인 장터 상품
        response = await getOfflineMarketplaceProducts(this.currentMarketplaceUid);
      } else {
        // 메인 장터 상품
        response = await getMainMarketplaceProducts(this.channelUid);
      }
      this.products = response.data.content || response.data;
    } catch (error: any) {
      const message = error.response?.data?.message || '상품 목록을 불러오는데 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  private setFilter(filter: 'SALE' | 'SHARE' | 'REQUEST') {
    this.activeFilter = filter;
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private openProductDetail(product: MarketplaceProduct) {
    console.log('Clicking product:', product);
    this.$router.push({
      name: 'MarketplaceDetail',
      params: {
        domain: this.$route.params.domain || 'default',
        productId: product.uid,
      },
    });
  }

  private openWriteModal() {
    if (!this.canWrite) {
      this.$message.warning('상품 등록 권한이 없습니다');
      return;
    }
    this.writeModalVisible = true;
  }

  private closeWriteModal() {
    this.writeModalVisible = false;
    this.newPost = {
      title: '',
      description: '',
      productType: 'SALE',
      price: 0,
      location: '',
      stock: 1,
      images: [],
      imageUrls: [],
    };
  }

  private handleImageUpload(event: Event) {
    const target = event.target as HTMLInputElement;
    const files = target.files;
    
    if (files && files.length > 0) {
      const file = files[0];
      
      if (this.newPost.images.length >= 4) {
        this.$message.warning('이미지는 최대 4개까지 등록할 수 있습니다');
        return;
      }
      
      this.newPost.images.push(file);
      
      const reader = new FileReader();
      reader.onload = (e) => {
        if (e.target && typeof e.target.result === 'string') {
          this.newPost.imageUrls.push(e.target.result);
        }
      };
      reader.readAsDataURL(file);
    }
  }

  private removeImage(index: number) {
    this.newPost.images.splice(index, 1);
    this.newPost.imageUrls.splice(index, 1);
  }

  private async submitPost() {
    if (!this.newPost.title || !this.newPost.description || !this.newPost.location) {
      this.$message.warning('모든 필수 정보를 입력해주세요');
      return;
    }

    if (this.newPost.productType === 'SALE' && this.newPost.price <= 0) {
      this.$message.warning('판매 가격을 입력해주세요');
      return;
    }

    if (this.newPost.images.length === 0) {
      this.$message.warning('최소 1개 이상의 이미지를 등록해주세요');
      return;
    }

    this.loading = true;
    try {
      // 이미지 업로드
      const imageUids: string[] = [];
      for (const imageFile of this.newPost.images) {
        const formData = new FormData();
        formData.append('file', imageFile);
        const uploadResponse = await uploadFile(formData);
        // uploadResponse.data는 AttachedFileDto.detail 객체이므로 uid만 추출
        imageUids.push(uploadResponse.data.uid);
      }

      // 상품 등록
      const request: MarketplaceProductCreateRequest = {
        channelUid: this.channelUid,
        title: this.newPost.title,
        description: this.newPost.description,
        productType: this.newPost.productType,
        price: this.newPost.price,
        location: this.newPost.location,
        stock: 1, // 항상 1개로 고정
        imageUids,
        offlineMarketplaceUid: this.currentMarketplaceUid || undefined,
      };

      await createProduct(request);
      this.$message.success('상품이 등록되었습니다');
      this.closeWriteModal();
      await this.loadProducts();
    } catch (error: any) {
      const message = error.response?.data?.message || '상품 등록에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  private getProductImage(product: MarketplaceProduct): string {
    if (product.thumbnailUid) {
      return `/api/attached-file/${product.thumbnailUid}`;
    }
    if (product.imageUids && product.imageUids.length > 0) {
      return `/api/attached-file/${product.imageUids[0]}`;
    }
    if (product.images && product.images.length > 0) {
      return `/api/attached-file/${product.images[0].fileUid}`;
    }
    return 'https://via.placeholder.com/300x300?text=No+Image';
  }

  private formatPrice(product: MarketplaceProduct): string {
    const type = product.productType || (product as any).category;
    if (type === 'SHARE') {
      return '나눔';
    }
    const price = product.price || 0;
    return `R ${price.toLocaleString()}`;
  }

  private getStatusBadge(product: MarketplaceProduct): string {
    // 거래중 상태 체크
    if (product.status === 'TRADING') {
      return '거래중';
    }
    
    // 완료된 상태 체크
    if (product.status === 'SOLD_OUT') {
      const type = product.productType || (product as any).category;
      switch (type) {
        case 'SALE':
          return '판매 완료';
        case 'SHARE':
          return '나눔 완료';
        case 'REQUEST':
          return '요청 완료';
        default:
          return '거래 완료';
      }
    }
    
    // 재고 기반 체크 (하위 호환)
    const stock = product.stock !== undefined ? product.stock : product.stockQuantity;
    if (stock === 0) {
      const type = product.productType || (product as any).category;
      return type === 'SALE' ? '판매 완료' : '나눔 완료';
    }
    return '';
  }
}
</script>

<style scoped lang="scss">
.marketplace-page {
  display: flex;
  min-height: calc(100vh - 120px);
  background: #FFF;
}

.marketplace-main {
  flex: 1;
  margin: 120px 0 0 270px;
  padding: 40px;
  position: relative;
  background: #F8F9FB;
}

.tab-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.tab-btn {
  padding: 12px 24px;
  border-radius: 10px;
  background: #F5F5F5;
  border: none;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #E0E0E0;
  }

  &.active {
    background: #073DFF;
    color: #FFF;
  }
}

.offline-marketplace-section {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.offline-marketplace-btn {
  padding: 10px 20px;
  border-radius: 8px;
  background: #FFF;
  border: 1px solid #CECECE;
  color: #444;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    color: #073DFF;
  }

  &.active {
    background: #073DFF;
    border-color: #073DFF;
    color: #FFF;
  }
}

.filter-section {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #888;
  font-size: 18px;
  gap: 16px;

  i {
    font-size: 32px;
  }
}

.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: #888;
  font-size: 18px;
}

// Floating Write Button (initially hidden on desktop, shown on mobile)
.write-post-btn-fixed {
  position: fixed;
  left: 30px;
  bottom: 40px;
  width: 210px;
  height: 50px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  display: none; // Hidden on desktop (sidebar has write button)
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-icon {
    display: none;
  }

  &:hover {
    background: #0530CC;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: translateY(0);
  }
}

.filter-btn {
  flex: 0 1 100%;
  width: 100%;
  max-width: 100px;
  height: 42px;
  align-items: center;
  border-radius: 10px;
  background: #F5F5F5;
  border: none;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #E0E0E0;
  }

  &.active {
    background: #073DFF;
    color: #FFF;
  }
}

.products-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  margin-bottom: 60px;
}

.product-card {
  flex: 0 1 calc(100% / 4 - 37px);
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-4px);
  }
}

.product-image-wrapper {
  position: relative;
  flex: 0 1 100%;
  max-width: 300px;
  height: 300px;
  // aspect-ratio: 34/27;
  border-radius: 10px;
  overflow: hidden;

  &.has-status {
    .product-image {
      filter: brightness(0.6);
    }
  }
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  transition: filter 0.2s;
}

.status-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(0deg, rgba(0, 0, 0, 0.60) 0%, rgba(0, 0, 0, 0.60) 100%);
  pointer-events: none;
}

.status-badge {
  position: absolute;
  top: 17px;
  left: 19px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px;
  border-radius: 10px;
  background: #FFF;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  z-index: 1;
}

.product-info {
  flex: 0 1 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.product-title {
  flex: 0 1 100%;
  font-size: 24px;
  font-weight: 800;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  color: #222;
  line-height: 100%;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-location {
  flex: 0 1 100%;
  font-size: 18px;
  font-weight: 400;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  color: #888;
  line-height: 100%;
  margin: 0;
}

.product-price {
  flex: 0 1 100%;
  font-size: 24px;
  font-weight: 800;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  color: #073DFF;
  line-height: 100%;
  margin: 0;
}

.write-post-btn-fixed {
  position: fixed;
  left: 30px;
  bottom: 40px;
  width: 210px;
  height: 50px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  display: none; /* 기본적으로 숨김 (데스크톱에서는 사이드바 버튼 사용) */
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-icon {
    display: none;
  }

  &:hover {
    background: #0530CC;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: translateY(0);
  }

  /* 모바일에서만 표시 */
  @media screen and (max-width: 1024px) {
    display: flex;
  }
}

.write-post-btn {
  position: fixed;
  left: 30px;
  bottom: 40px;
  width: 210px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  cursor: pointer;
  transition: background 0.2s;
  z-index: 100;

  &:hover {
    background: #0530CC;
  }
}

::v-deep .write-post-modal {
  border-radius: 10px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }

  @media screen and (max-width: 768px) {
    width: 100% !important;
    height: 100%;
    margin: 0 !important;
    border-radius: 16px;
    max-width: 100vw;
    max-height: 100vh;
    overflow-y: auto;

    .el-dialog__body {
      padding: 64px 20px;
      height: 100%;
      overflow-y: auto;
    }
  }
}

.modal-close-btn {
  position: absolute;
  right: 16px;
  top: 16px;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #444;
  transition: color 0.2s;
  z-index: 10;

  &:hover {
    color: #000;
  }

  @media screen and (max-width: 768px) {
    right: 16px;
    top: 16px;
  }
}

.write-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
}

.write-modal-title {
  color: #444;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;

  @media screen and (max-width: 768px) {
    font-size: 18px;
    line-height: 150%;
  }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;

  @media screen and (max-width: 768px) {
    font-size: 18px;
  }
}

.form-input {
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  outline: none;
  transition: border-color 0.2s;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }

  @media screen and (max-width: 768px) {
    font-size: 16px;
  }
}

.form-textarea {
  padding: 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  outline: none;
  resize: vertical;
  transition: border-color 0.2s;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }

  @media screen and (max-width: 768px) {
    font-size: 16px;
  }
}

.type-buttons {
  display: flex;
  gap: 8px;
  align-self: stretch;

  @media screen and (max-width: 768px) {
    gap: 8px;
  }
}

.type-btn {
  flex: 1;
  height: 54px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  background: #D2D2D2;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #C0C0C0;
  }

  &.active {
    background: #073DFF;
    color: #FFF;
  }

  @media screen and (max-width: 768px) {
    padding: 12px 0;
  }
}

.image-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.image-count {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

.image-upload-area {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;

  @media screen and (max-width: 768px) {
    justify-content: space-between;
  }
}

.upload-box {
  width: 72px;
  height: 72px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 6px;
  border-radius: 10px;
  background: #D9D9D9;
  cursor: pointer;
  transition: all 0.2s;

  svg {
    width: 40px;
    height: 40px;

    path {
      fill: #FFF;
    }
  }

  &:hover {
    background: #C0C0C0;
  }

  @media screen and (max-width: 768px) {
    width: 72px;
    height: 72px;
  }
}

.upload-text {
  display: none;
}

.image-preview {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 10px;
  overflow: hidden;
  background: #D9D9D9;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  @media screen and (max-width: 768px) {
    width: 72px;
    height: 72px;
  }
}

.remove-image-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: rgba(0, 0, 0, 0.8);
  }
}

.submit-post-btn {
  width: 100%;
  height: 54px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  background: #073DFF;
  border: 1px solid #FFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #0530CC;
  }

  &:active {
    background: #042099;
  }

  @media screen and (max-width: 768px) {
    padding: 12px 0;
  }
}

@media screen and (max-width: 1600px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 1200px) {
  .products-grid {gap: 30px;}
  .product-card {flex: 0 1 calc(100% / 3 - 20px);}
  .product-image-wrapper {height: 250px;}
  .marketplace-main { padding: 30px;}
}

@media screen and (max-width: 1024px) {
  .marketplace-main {margin:120px 0 0 240px}

  // Show floating write button on mobile (circular style)
  .write-post-btn-fixed {
    display: flex; // Show button on mobile
    width: 50px;
    height: 50px;
    border-radius: 50%;
    left: auto;
    right: 24px;
    bottom: 24px;

    .btn-text {
      display: none;
    }

    .btn-icon {
      display: block;
    }
  }

  .products-grid {gap: 20px;}
  .product-card {flex: 0 1 calc(100% / 2 - 10px);}
  .product-image-wrapper {height: 200px;}

  .detail-layout{ flex-direction: column;}
  .detail-right{ width: 100%;}
}

@media screen and (max-width: 768px) {
  .products-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .marketplace-main {
    padding: 40px 20px;
    margin: 100px 0 0 0;
  }

  .filter-section {
    gap: 8px;
    margin-bottom: 32px;
  }

  .filter-btn {
    flex: 1;
    height: 54px;
    font-size: 16px;
    border-radius: 10px;
  }

  .product-title {
    font-size: 18px;
  }

  .product-location {
    font-size: 12px;
  }

  .product-price {
    font-size: 18px;
  }

  .status-badge {
    font-size: 16px;
    padding: 12px;
  }

  .write-post-btn-fixed {
    width: 50px;
    height: 50px;
    right: 20px;
    bottom: 20px;
    padding: 14px;
    border-radius: 50%;

    .btn-text {
      display: none;
    }

    .btn-icon {
      display: block;
    }
  }

  .modal-close-btn {
    right: 20px;
    top: 20px;
  }
}

@media screen and (max-width: 500px) {
  .marketplace-main {margin:80px 0 0 0}
  .filter-section{ justify-content: center;}
}
</style>
