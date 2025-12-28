<template>
  <div class="marketplace-detail-page">
    <CommunitySidebar :selectedSpaceId="'marketplace'" @space-select="handleSpaceSelect" />

    <div class="detail-main">
      <!-- 뒤로가기 버튼 -->
      <div class="back-button-wrapper">
        <button class="back-btn" @click="goBack">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>뒤로가기</span>
        </button>
      </div>

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
                <img v-if="product.iconFileUid" :src="`${apiUrl}/attached-file/${product.iconFileUid}`" alt="판매자 프로필" class="user-avatar-img">
                <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
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
              <!-- REQUEST 상품은 지원하기, 그 외는 거래하기 -->
              <button 
                v-if="!product.isMine && product.status !== 'TRADING' && product.status !== 'SOLD_OUT'" 
                class="trade-btn" 
                :class="{ 'apply-btn': isRequestType }"
                @click="handleTrade"
              >
                {{ isRequestType ? '지원하기' : '거래하기' }}
              </button>
              <!-- 거래중 상태 표시 -->
              <div v-else-if="product.status === 'TRADING'" class="status-badge trading">
                거래중
              </div>
              <!-- 품절 상태 표시 -->
              <div v-else-if="product.status === 'SOLD_OUT'" class="status-badge sold-out">
                거래완료
              </div>
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

    <!-- Trade Confirmation Modal (거래하기 - SALE/SHARE) -->
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
        <div class="trade-modal-price">{{ formattedPrice }}</div>

        <div class="trade-info-message">
          <i class="el-icon-info"></i>
          <span>거래가 시작되면 상품이 '거래중' 상태로 변경됩니다.<br/>판매자와 거래 완료 후 '지급완료하기'를 눌러 포인트를 지급해주세요.</span>
        </div>

        <div class="points-breakdown">
          <div class="breakdown-header">
            <h4>{{ currentUserName }}님의 R 포인트</h4>
          </div>
          <div class="breakdown-items">
            <div class="breakdown-item">
              <span class="item-label">사용 가능한 R 포인트</span>
              <span class="item-value">{{ currentPoints.toLocaleString() }}</span>
            </div>
            <div class="breakdown-item">
              <span class="item-label">예정 차감 R 포인트</span>
              <span class="item-value">{{ deductPoints }}</span>
            </div>
            <div class="breakdown-item">
              <span class="item-label">예상 잔액 R 포인트</span>
              <span class="item-value primary">{{ remainingPoints.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <button 
          class="confirm-trade-btn" 
          @click="confirmStartTrade"
          :disabled="purchasing || (product.productType !== 'SHARE' && product.category !== 'SHARE' && currentPoints < product.price)"
        >
          {{ purchasing ? '처리 중...' : (isShareType ? '나눔 받기' : '거래 시작하기') }}
        </button>
      </div>
    </el-dialog>

    <!-- Apply Modal (지원하기 - REQUEST) -->
    <el-dialog
      :visible.sync="applyModalVisible"
      width="540px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="true"
      custom-class="trade-modal"
    >
      <div v-if="product" class="trade-modal-content">
        <button class="modal-close-btn" @click="applyModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="trade-modal-title">{{ product.title }}</h3>
        <div class="trade-modal-price request">요청 상품</div>

        <div class="trade-info-message">
          <i class="el-icon-info"></i>
          <span>요청자가 제시한 가격: <strong>R {{ product.price?.toLocaleString() || 0 }}</strong><br/>지원 시 요청자에게 연락처가 전달됩니다.</span>
        </div>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">연락처</label>
            <input 
              v-model="applyForm.contact" 
              type="text" 
              placeholder="연락받을 연락처를 입력해주세요"
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label class="form-label">메시지 (선택)</label>
            <textarea 
              v-model="applyForm.message" 
              placeholder="요청자에게 전달할 메시지를 입력해주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>

        <button 
          class="confirm-trade-btn apply" 
          @click="confirmApply"
          :disabled="applying || !applyForm.contact"
        >
          {{ applying ? '처리 중...' : '지원하기' }}
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getProduct, MarketplaceProduct, startTrade, applyForRequest } from '@/api/marketplace';
import { getCurrentPoint } from '@/api/point';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';

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
  private applyModalVisible = false;
  private loading = false;
  private purchasing = false;
  private applying = false;

  // 포인트 관련
  private currentPoints = 0;
  private loadingPoints = false;

  // 지원 폼
  private applyForm = {
    contact: '',
    message: '',
  };

  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  // REQUEST 타입인지 확인
  get isRequestType(): boolean {
    if (!this.product) return false;
    const type = this.product.productType || (this.product as any).category;
    return type === 'REQUEST';
  }

  // SHARE 타입인지 확인
  get isShareType(): boolean {
    if (!this.product) return false;
    const type = this.product.productType || (this.product as any).category;
    return type === 'SHARE';
  }

  async mounted() {
    await this.loadProduct();
    await this.loadCurrentPoints();
  }

  @Watch('$route.params.productId')
  async onProductIdChange() {
    await this.loadProduct();
    await this.loadCurrentPoints();
  }

  // 뒤로가기
  private goBack() {
    this.$router.go(-1);
  }

  private async loadCurrentPoints() {
    // ChannelModule에서 실제 channelUid 가져오기
    const channelUid = ChannelModule.selectedChannel?.uid;
    if (!channelUid) return;

    try {
      this.loadingPoints = true;
      const response = await getCurrentPoint(channelUid);
      this.currentPoints = response.data?.currentBalance || 0;
    } catch (error) {
      console.error('포인트 조회 실패:', error);
      this.currentPoints = 0;
    } finally {
      this.loadingPoints = false;
    }
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
    if (this.isRequestType) {
      // REQUEST 상품은 지원 모달 열기
      this.applyForm = { contact: '', message: '' };
      this.applyModalVisible = true;
    } else {
      // SALE/SHARE 상품은 거래 모달 열기
      this.tradeModalVisible = true;
    }
  }

  // 거래 시작 (SALE/SHARE)
  private async confirmStartTrade() {
    if (!this.product) return;

    // 나눔 상품이 아닌 경우 포인트 체크
    const type = this.product.productType || (this.product as any).category;
    const totalPrice = type === 'SHARE' ? 0 : this.product.price;
    if (totalPrice > 0 && this.currentPoints < totalPrice) {
      this.$message.error('포인트가 부족합니다');
      return;
    }

    try {
      this.purchasing = true;
      await startTrade(this.product.uid, {
        quantity: 1,
      });
      
      this.$message.success('거래가 시작되었습니다! 판매자와 거래 완료 후 내 장터에서 지급완료하기를 눌러주세요.');
      this.tradeModalVisible = false;
      
      // 상품 정보 새로고침
      await this.loadProduct();
      
      // 내 장터로 이동
      this.$router.push({ 
        name: 'MyMarketplace',
        params: { domain: this.$route.params.domain || 'default' }
      });
    } catch (error: any) {
      const message = error.response?.data?.message || '거래 시작에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.purchasing = false;
    }
  }

  // REQUEST 상품 지원
  private async confirmApply() {
    if (!this.product) return;

    if (!this.applyForm.contact) {
      this.$message.warning('연락처를 입력해주세요');
      return;
    }

    try {
      this.applying = true;
      await applyForRequest(this.product.uid, {
        contact: this.applyForm.contact,
        message: this.applyForm.message,
      });
      
      this.$message.success('지원이 완료되었습니다! 요청자에게 연락처가 전달되었습니다.');
      this.applyModalVisible = false;
      
      // 상품 정보 새로고침
      await this.loadProduct();
    } catch (error: any) {
      const message = error.response?.data?.message || '지원에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.applying = false;
    }
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
    if (type === 'REQUEST') {
      return `R ${(this.product.price || 0).toLocaleString()} (요청)`;
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

  // 현재 사용자 이름
  get currentUserName(): string {
    return UserModule.actualName || '사용자';
  }

  // 차감 포인트 (나눔은 0)
  get deductPoints(): string {
    if (!this.product) return '0';
    const type = this.product.productType || (this.product as any).category;
    const amount = type === 'SHARE' ? 0 : this.product.price;
    return `-${amount.toLocaleString()}`;
  }

  // 잔액 포인트
  get remainingPoints(): number {
    if (!this.product) return this.currentPoints;
    const type = this.product.productType || (this.product as any).category;
    const deduct = type === 'SHARE' ? 0 : this.product.price;
    return Math.max(0, this.currentPoints - deduct);
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
  padding: 160px 40px 100px 40px;
  position: relative;
  max-width: calc(100vw - 270px);
  box-sizing: border-box;

  .back-button-wrapper {
    text-align: left;
  }
}

// 뒤로가기 버튼
.back-button-wrapper {
  margin-bottom: 24px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #F5F5F5;
  border: 1px solid #E0E0E0;
  border-radius: 8px;
  color: #333;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #E8E8E8;
  }

  svg {
    width: 20px;
    height: 20px;
  }
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
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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

  &.apply-btn {
    background: #FF9800;

    &:hover {
      background: #F57C00;
    }

    &:active {
      background: #E65100;
    }
  }
}

// 상태 배지
.status-badge {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px 24px;
  border-radius: 10px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;

  &.trading {
    background: #FFF3E0;
    color: #FF9800;
    border: 2px solid #FF9800;
  }

  &.sold-out {
    background: #F5F5F5;
    color: #888;
    border: 2px solid #CCC;
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

  &.request {
    background: rgba(255, 152, 0, 0.10);
    color: #FF9800;
  }
}

// 거래 안내 메시지
.trade-info-message {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #E3F2FD;
  border-radius: 10px;
  width: 100%;

  i {
    color: #1976D2;
    font-size: 20px;
    flex-shrink: 0;
    margin-top: 2px;
  }

  span {
    color: #1565C0;
    font-size: 14px;
    line-height: 1.6;

    strong {
      color: #073DFF;
    }
  }
}

// 폼 스타일
.form-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
}

.form-input {
  padding: 12px 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;

  &:focus {
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

.form-textarea {
  padding: 12px 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  outline: none;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
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

  &:hover:not(:disabled) {
    background: #0530CC;
  }

  &:active:not(:disabled) {
    background: #042099;
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
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
