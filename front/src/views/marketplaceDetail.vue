<template>
  <div class="marketplace-detail-page">
    <CommunitySidebar :selectedSpaceId="'marketplace'" @space-select="handleSpaceSelect" />

    <div class="detail-main">
      <div v-if="product" class="detail-content">
        <div class="detail-layout">
          <div class="detail-left">
            <div class="main-image-wrapper">
              <img :src="productImages[currentImageIndex]" alt="Product Image" class="main-product-image" />
            </div>
            <div class="thumbnail-row">
              <img 
                v-for="(image, index) in productImages" 
                :key="index"
                :src="image" 
                :alt="`Thumbnail ${index + 1}`" 
                class="thumbnail-image"
                :class="{ active: currentImageIndex === index }"
                @click="currentImageIndex = index"
              />
            </div>
            <div class="image-indicators">
              <span 
                v-for="(image, index) in productImages" 
                :key="`dot-${index}`"
                class="indicator-dot"
                :class="{ active: currentImageIndex === index }"
              ></span>
            </div>
          </div>

          <div class="detail-right">
            <div class="seller-info">
              <div class="user-avatar">
                <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  <mask id="mask0_seller" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0_seller)">
                    <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                    <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                  </g>
                </svg>
              </div>
              <span class="seller-name">{{ sellerName }}</span>
            </div>

            <div class="product-header">
              <div class="header-text">
                <h2 class="detail-title">{{ product.title }}</h2>
                <p class="detail-location">{{ locationText }}</p>
              </div>
              <button class="trade-btn" @click="handleTrade">거래하기</button>
            </div>

            <div class="detail-price">{{ formattedPrice }}</div>

            <div class="product-description">
              {{ product.description }}
            </div>
          </div>
        </div>
      </div>

      <div v-else class="loading-state">
        <p>상품 정보를 불러오는 중입니다...</p>
      </div>
    </div>

    <!-- Trade Confirmation Modal -->
    <el-dialog
      :visible.sync="tradeModalVisible"
      width="540px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="true"
      custom-class="trade-modal"
    >
      <div v-if="product" class="trade-modal-content">
        <button class="modal-close-btn" @click="tradeModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="trade-modal-title">{{ product.title }}</h3>
        <div class="trade-modal-price">{{ product.price }}</div>

        <div class="points-breakdown">
          <div class="breakdown-header">
            <h4>이명관 님의 R 포인트</h4>
          </div>
          <div class="breakdown-items">
            <div class="breakdown-item">
              <span class="item-label">사용 가능한 R 포인트</span>
              <span class="item-value">58,500</span>
            </div>
            <div class="breakdown-item">
              <span class="item-label">차감 R 포인트</span>
              <span class="item-value">-15,000</span>
            </div>
            <div class="breakdown-item">
              <span class="item-label">잔액 R 포인트</span>
              <span class="item-value primary">43,500</span>
            </div>
          </div>
        </div>

        <button class="confirm-trade-btn" @click="confirmTrade">R 포인트 지급하기</button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getProduct, MarketplaceProduct } from '@/api/marketplace';

@Component({
  name: 'MarketplaceDetail',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private product: MarketplaceProduct | null = null;
  private currentImageIndex = 0;
  private tradeModalVisible = false;
  private loading = false;

  async mounted() {
    await this.loadProduct();
  }

  @Watch('$route.params.productId')
  async onProductIdChange() {
    await this.loadProduct();
  }

  private async loadProduct() {
    const productId = this.$route.params.productId;
    console.log('MarketplaceDetail productId:', productId);
    
    if (!productId) {
      this.$message.error('상품 ID가 없습니다.');
      this.$router.push({ 
        name: 'Marketplace',
        params: { domain: this.$route.params.domain || 'default' }
      });
      return;
    }

    this.loading = true;
    try {
      const response = await getProduct(productId);
      this.product = response.data;
    } catch (error: any) {
      console.error('getProduct error:', error);
      const message = error.response?.data?.message || '상품을 찾을 수 없습니다.';
      this.$message.error(message);
      this.$router.push({ 
        name: 'Marketplace',
        params: { domain: this.$route.params.domain || 'default' }
      });
    } finally {
      this.loading = false;
    }
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private handleTrade() {
    this.tradeModalVisible = true;
  }

  private confirmTrade() {
    this.$message.success('거래가 완료되었습니다!');
    this.tradeModalVisible = false;
    this.$router.push({ 
      name: 'Marketplace',
      params: { domain: this.$route.params.domain || 'default' }
    });
  }

  // 이미지 URL 생성
  get productImages(): string[] {
    if (!this.product) return [];
    
    if (this.product.imageUids && this.product.imageUids.length > 0) {
      return this.product.imageUids.map(uid => `/api/attached-file/${uid}`);
    }
    
    if (this.product.images && this.product.images.length > 0) {
      return this.product.images.map(img => `/api/attached-file/${img.fileUid}`);
    }
    
    return ['https://via.placeholder.com/678x404?text=No+Image'];
  }

  // 가격 표시
  get formattedPrice(): string {
    if (!this.product) return '';
    
    const type = this.product.productType || (this.product as any).category;
    if (type === 'SHARE') {
      return '나눔';
    }
    
    const price = this.product.price || 0;
    return `R ${price.toLocaleString()}`;
  }

  // 판매자 이름
  get sellerName(): string {
    return this.product?.sellerName || '알 수 없음';
  }

  // 위치 정보
  get locationText(): string {
    return this.product?.location || '';
  }
}
</script>

<style scoped lang="scss">
.marketplace-detail-page {
  display: flex;
  min-height: calc(100vh - 120px);
  background: #FFF;
}

.detail-main {
  flex: 1;
  margin-left: 270px;
  padding: 40px;
  position: relative;
  max-width: calc(100vw - 270px);
  box-sizing: border-box;
}

.detail-content {
  max-width: 100%;
  margin: 0 auto;
}

.detail-layout {
  display: flex;
  gap: 64px;
  width: 100%;
}

.detail-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 30px;
  min-width: 0;
}

.main-image-wrapper {
  width: 100%;
  aspect-ratio: 678/404;
  border-radius: 10px;
  overflow: hidden;
  max-width: 100%;

  @media screen and (max-width: 768px) {
    border-radius: 0;
  }
}

.main-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-row {
  display: flex;
  gap: 30px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #F5F5F5;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #CECECE;
    border-radius: 4px;

    &:hover {
      background: #999;
    }
  }

  @media screen and (max-width: 768px) {
    display: none;
  }
}

.thumbnail-image {
  width: 206px;
  height: 191px;
  border-radius: 10px;
  object-fit: cover;
  cursor: pointer;
  transition: all 0.2s;
  border: 3px solid transparent;
  flex-shrink: 0;

  &:hover {
    opacity: 0.8;
  }

  &.active {
    border-color: #073DFF;
  }
}

.image-indicators {
  display: none;
  align-items: center;
  justify-content: center;
  gap: 17px;

  @media screen and (max-width: 768px) {
    display: flex;
  }
}

.indicator-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.4);
  transition: background 0.2s;
  cursor: pointer;

  &.active {
    background: #000;
  }
}

.detail-right {
  width: 600px;
  max-width: 600px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 50px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
}

.seller-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.product-header {
  display: flex;
  align-items: flex-start;
  gap: 30px;
  text-align: left;
}

.header-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 42px;
  font-weight: 800;
  line-height: 100%;
  margin: 0;
}

.detail-location {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  margin: 0;
}

.trade-btn {
  width: 178px;
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 28px;
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
}

.detail-price {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 800;
  line-height: 100%;
  text-align: left;
}

.product-description {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  white-space: pre-wrap;
  text-align: left;
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;

  p {
    color: #888;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
  }
}

::v-deep .trade-modal {
  border-radius: 10px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 64px 40px;
    position: relative;
  }

  @media screen and (max-width: 768px) {
    width: 100% !important;
    margin: 0 !important;
    margin-top: auto !important;
    border-radius: 10px 10px 0 0;
    max-width: 100vw;
    position: fixed;
    bottom: 0;
    left: 0;
    animation: slideUp 0.3s ease-out;

    .el-dialog__body {
      padding: 64px 40px;
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

::v-deep .el-dialog__wrapper {
  @media screen and (max-width: 768px) {
    .trade-modal {
      display: flex;
      align-items: flex-end;
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

.trade-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;

  @media screen and (max-width: 768px) {
    gap: 32px;
  }
}

.trade-modal-title {
  color: #222;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;

  @media screen and (max-width: 768px) {
    font-size: 24px;
  }
}

.trade-modal-price {
  padding: 10px 12px;
  border-radius: 20px;
  background: rgba(7, 61, 255, 0.10);
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
}

.points-breakdown {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: stretch;
  border: 1px solid #CECECE;
  border-radius: 10px;
  padding: 20px;
  width: 100%;

  @media screen and (max-width: 768px) {
    padding: 20px;
  }
}

.breakdown-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #CECECE;

  h4 {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 700;
    line-height: 150%;
    margin: 0;
  }
}

.breakdown-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
}

.item-value {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 22px;
  font-weight: 700;
  line-height: 150%;

  &.primary {
    color: #073DFF;
  }

  @media screen and (max-width: 768px) {
    font-size: 22px;
  }
}

.confirm-trade-btn {
  width: 100%;
  height: 54px;
  display: flex;
  padding: 12px 0;
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

@media screen and (max-width: 1400px) {
  .detail-layout {
    gap: 40px;
  }

  .detail-right {
    width: 500px;
    max-width: 500px;
  }

  .detail-title {
    font-size: 36px;
  }

  .detail-main {
    padding: 30px;
  }
}

@media screen and (max-width: 1024px) {
  .detail-main {
    margin-left: 0;
    max-width: 100vw;
  }

  .detail-layout {
    flex-direction: column;
    gap: 32px;
  }

  .detail-right {
    width: 100%;
    max-width: 100%;
    gap: 32px;
  }

  .product-header {
    flex-direction: column;
  }

  .trade-btn {
    width: 100%;
  }

  @media screen and (max-width: 768px) {
    .detail-layout {
      gap: 32px;
    }

    .detail-right {
      gap: 32px;
    }
  }
}

@media screen and (max-width: 768px) {
  .detail-main {
    padding: 20px;
  }

  .detail-layout {
    gap: 30px;
  }

  .detail-left {
    gap: 16px;
  }

  .detail-right {
    gap: 16px;
  }

  .seller-info {
    margin-bottom: 16px;
  }

  .product-header {
    gap: 16px;
  }

  .detail-title {
    font-size: 18px;
  }

  .detail-location {
    font-size: 12px;
  }

  .detail-price {
    font-size: 18px;
  }

  .trade-btn {
    width: 100%;
    height: 54px;
    padding: 24px 100px;
    font-size: 16px;
    border-radius: 10px;
  }

  .thumbnail-row {
    gap: 8px;
  }

  .thumbnail-image {
    width: 72px;
    height: 72px;
  }

  .product-description {
    font-size: 12px;
    line-height: 150%;
  }
}

@media screen and (max-width: 600px) {
  .detail-main {
    padding: 0;
  }

  .detail-content {
    padding: 0 20px 40px;
  }

  .main-image-wrapper {
    border-radius: 0;
    margin-left: -20px;
    margin-right: -20px;
    width: calc(100% + 40px);
  }

  .detail-title {
    font-size: 18px;
  }

  .detail-location {
    font-size: 12px;
  }

  .product-description {
    font-size: 12px;
  }

  .detail-price {
    font-size: 18px;
  }

  .trade-btn {
    height: 54px;
    font-size: 16px;
  }

  .thumbnail-image {
    width: 72px;
    height: 72px;
  }
}
</style>
