<template>
  <div class="marketplace-detail-page">
    <CommunitySidebar :selectedSpaceId="'marketplace'" @space-select="handleSpaceSelect" />

    <div class="detail-main">
      <div v-if="product" class="detail-content">
        <div class="detail-layout">
          <div class="detail-left">
            <div class="main-image-wrapper">
              <img :src="product.images[currentImageIndex]" alt="Product Image" class="main-product-image" />
            </div>
            <div class="thumbnail-row">
              <img 
                v-for="(image, index) in product.images" 
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
                v-for="(image, index) in product.images" 
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
              <span class="seller-name">{{ product.seller }}</span>
            </div>

            <div class="product-header">
              <div class="header-text">
                <h2 class="detail-title">{{ product.title }}</h2>
                <p class="detail-location">{{ product.location }}</p>
              </div>
              <button class="trade-btn" @click="handleTrade">거래하기</button>
            </div>

            <div class="detail-price">{{ product.price }}</div>

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
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';

interface Product {
  id: number;
  title: string;
  location: string;
  price: string;
  image: string;
  images: string[];
  type: 'sale' | 'share' | 'request';
  status?: string;
  seller: string;
  description: string;
}

@Component({
  name: 'MarketplaceDetail',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private product: Product | null = null;
  private currentImageIndex = 0;
  private tradeModalVisible = false;

  private mockProducts: Product[] = [
    {
      id: 1,
      title: '산청 사과 1박스',
      location: '산청군 금서면 수철리',
      price: 'R 15,000',
      image: 'https://api.builder.io/api/v1/image/assets/TEMP/4b83b9d31f97483c9f8603f324ff13c84b7989bf?width=748',
      images: [
        'https://api.builder.io/api/v1/image/assets/TEMP/0a778705249fc98de958f817fb6ea84ab8272da7?width=1356',
        'https://api.builder.io/api/v1/image/assets/TEMP/ce305e4980b3e7fdf5b79e95fbb95125104a41f6?width=412',
        'https://api.builder.io/api/v1/image/assets/TEMP/6426a993b126866c2e5564179cf9220821960dab?width=412',
        'https://api.builder.io/api/v1/image/assets/TEMP/b034aee7bc291d31d641928c561ef97d7d7dbe3b?width=412',
      ],
      type: 'sale',
      seller: '오형래 님',
      description: '직접 어머님께서 수확하신 산청 꿀 사과 15kg 1박스를 판매합니다. 직접 어머님께서 수확하신 산청 꿀 사과 15kg 1박스를 판매합니다.\n\n정말 꿀처럼 달아서 후회하지 않으실 겁니다!! 정말 꿀처럼 달아서 후회하지 않으실 겁니다!! 정말 꿀처럼 달아서 후회하지 않으실 겁니다!!',
    },
    {
      id: 2,
      title: '아기 나무 침대',
      location: '산청군 금서면 평촌리',
      price: '나눔',
      image: 'https://api.builder.io/api/v1/image/assets/TEMP/a6c13dafa3522e88e32f24430f0883a3d59fdc16?width=748',
      images: [
        'https://api.builder.io/api/v1/image/assets/TEMP/ac0d6ac00deec0aa053fdbc676e2f50e69a61d99?width=748',
      ],
      type: 'share',
      seller: '오형래 님',
      description: '사용하지 않는 아기 나무 침대를 나눔합니다.',
    },
    {
      id: 3,
      title: '고구마 수확 도움고구마 수확 도움고구마 수확 도움',
      location: '산청군 금서면 수철리',
      price: 'R 15,000',
      image: 'https://api.builder.io/api/v1/image/assets/TEMP/55b515e0762e05c335280c77113e93295c372099?width=748',
      images: [
        'https://api.builder.io/api/v1/image/assets/TEMP/49f3e25ed596ae5d8d6db7ab0e5f2dc67e9b29af?width=748',
      ],
      type: 'request',
      seller: '오형래 님',
      description: '고구마 수확을 도와주실 분을 찾습니다.',
    },
    {
      id: 4,
      title: '산청 사과 1박스',
      location: '산청군 금서면 수철리',
      price: 'R 15,000',
      image: 'https://api.builder.io/api/v1/image/assets/TEMP/4b83b9d31f97483c9f8603f324ff13c84b7989bf?width=748',
      images: [
        'https://api.builder.io/api/v1/image/assets/TEMP/9f30af96a00761f86f15d1316d03f9423aa00b1b?width=748',
      ],
      type: 'sale',
      status: '판매 완료',
      seller: '오형래 님',
      description: '직접 어머님께서 수확하신 산청 꿀 사과 15kg 1박스를 판매합니다.',
    },
  ];

  mounted() {
    this.loadProduct();
  }

  private loadProduct() {
    const productId = parseInt(this.$route.params.productId);
    
    this.product = this.mockProducts.find(p => p.id === productId) || null;
    
    if (!this.product) {
      this.$message.error('상품을 찾을 수 없습니다.');
      this.$router.push({ name: 'Marketplace' });
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
    this.$router.push({ name: 'Marketplace' });
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
