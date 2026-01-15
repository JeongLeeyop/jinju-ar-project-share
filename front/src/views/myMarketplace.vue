<template>
  <div class="my-marketplace-page">
    <CommunitySidebar 
      :selectedSpaceId="'my-marketplace'" 
      @space-select="handleSpaceSelect"
    />

    <div class="my-marketplace-main">
      <div class="page-header">
        <h1 class="page-title">내 장터 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'registered' }"
          @click="activeTab = 'registered'; onTabChange();"
        >
          내 등록 상품
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'purchased' }"
          @click="activeTab = 'purchased'; onTabChange();"
        >
          내 구매 상품
        </button>
      </div>

      <!-- 온라인/오프라인 필터 -->
      <div class="filter-section">
        <button 
          class="filter-btn" 
          :class="{ active: marketplaceFilter === 'all' }"
          @click="marketplaceFilter = 'all'; onFilterChange();"
        >
          전체
        </button>
        <button 
          class="filter-btn" 
          :class="{ active: marketplaceFilter === 'online' }"
          @click="marketplaceFilter = 'online'; onFilterChange();"
        >
          온라인 장터
        </button>
        <button 
          class="filter-btn" 
          :class="{ active: marketplaceFilter === 'offline' }"
          @click="marketplaceFilter = 'offline'; onFilterChange();"
        >
          오프라인 장터
        </button>
      </div>

      <!-- 내 등록 상품 -->
      <div v-if="activeTab === 'registered'" class="tab-content">
        <div v-if="loadingRegistered" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="registeredProducts.length > 0" class="products-grid">
          <div
            v-for="product in registeredProducts"
            :key="product.uid"
            class="product-card registered-card"
          >
            <div class="product-image-wrapper" :class="{ 'has-status': getRegisteredStatusBadge(product) }">
              <img :src="getProductImage(product)" :alt="product.title" class="product-image" />
              <div v-if="getRegisteredStatusBadge(product)" class="status-overlay"></div>
              <div v-if="getRegisteredStatusBadge(product)" class="status-badge" :class="getStatusBadgeClass(product)">{{ getRegisteredStatusBadge(product) }}</div>
            </div>
            <div class="product-info">
              <h3 class="product-title" @click="openProductDetail(product)">{{ product.title }}</h3>
              <p class="product-type-badge">{{ getProductTypeLabel(product.category || product.productType) }}</p>
              <p v-if="product.offlineMarketplaceName" class="product-marketplace-name">
                <i class="el-icon-office-building"></i> {{ product.offlineMarketplaceName }}
              </p>
              <p class="product-location">{{ product.location }}</p>
              <p class="product-price">{{ formatPrice(product) }}</p>
              
              <!-- 구매자 정보 표시 (거래중 또는 거래완료 상태) -->
              <div v-if="(product.status === 'TRADING' || product.status === 'SOLD_OUT') && product.currentBuyerName" class="buyer-info">
                <div class="info-title"><i class="el-icon-user"></i> {{ getTransactionLabel(product) }} 정보</div>
                <div class="buyer-profile">
                  <img 
                    v-if="product.currentBuyerIconFileUid" 
                    :src="`/api/attached-file/${product.currentBuyerIconFileUid}`" 
                    alt="buyer" 
                    class="buyer-avatar"
                  />
                  <i v-else class="el-icon-user buyer-avatar-icon"></i>
                  <span class="buyer-name">{{ product.currentBuyerName }}</span>
                </div>
              </div>
              
              <!-- 거래 진행 버튼들 (판매자용) -->
              <div class="action-buttons">
                <!-- 거래중 상태일 때 확정/취소 버튼 -->
                <button 
                  v-if="product.status === 'TRADING' && product.currentPurchaseUid" 
                  class="action-btn primary"
                  @click.stop="sellerConfirmTransaction(product)"
                  :disabled="confirmingProduct === product.uid"
                >
                  {{ confirmingProduct === product.uid ? '처리 중...' : getConfirmButtonLabel(product) }}
                </button>
                
                <button 
                  v-if="product.status === 'TRADING' && product.currentPurchaseUid" 
                  class="action-btn secondary"
                  @click.stop="sellerCancelTransaction(product)"
                  :disabled="cancellingProduct === product.uid"
                >
                  {{ cancellingProduct === product.uid ? '취소 중...' : '거래 취소' }}
                </button>
                
                <!-- 오프라인 상품인 경우 회원번호 입력 버튼 -->
                <button 
                  v-if="(product.isOffline || product.offlineMarketplaceUid) && product.status === 'ACTIVE'" 
                  class="action-btn primary" 
                  @click.stop="openPointDeductModal(product)"
                >
                  이름/연락처 입력
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>등록한 상품이 없습니다</p>
        </div>
      </div>

      <!-- 내 구매 상품 -->
      <div v-if="activeTab === 'purchased'" class="tab-content">
        <div v-if="loadingPurchased" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="purchasedProducts.length > 0" class="products-grid">
          <div
            v-for="product in purchasedProducts"
            :key="product.uid"
            class="product-card purchased-card"
          >
            <div class="product-image-wrapper" :class="{ 'has-status': getPurchaseStatusBadge(product) }">
              <img :src="getProductImage(product)" :alt="product.productTitle" class="product-image" />
              <div v-if="getPurchaseStatusBadge(product)" class="status-overlay"></div>
              <div v-if="getPurchaseStatusBadge(product)" class="status-badge" :class="getPurchaseStatusClass(product)">{{ getPurchaseStatusBadge(product) }}</div>
            </div>
            <div class="product-info">
              <h3 class="product-title" @click="openPurchaseDetail(product)">{{ product.productTitle }}</h3>
              <p class="product-type-badge">{{ getProductTypeLabel(product.productCategory) }}</p>
              <p v-if="product.offlineMarketplaceName" class="product-marketplace-name">
                <i class="el-icon-office-building"></i> {{ product.offlineMarketplaceName }}
              </p>
              <p class="product-price">{{ formatPrice(product) }}</p>
              <p class="purchase-date">구매일: {{ formatDate(product.purchasedAt) }}</p>
              
              <!-- 판매자 정보 -->
              <div class="seller-info">
                <div class="info-title"><i class="el-icon-goods"></i> 판매자 정보</div>
                <div class="seller-profile">
                  <img 
                    v-if="product.iconFileUid" 
                    :src="`/api/attached-file/${product.iconFileUid}`" 
                    alt="seller" 
                    class="seller-avatar"
                  />
                  <i v-else class="el-icon-user seller-avatar-icon"></i>
                  <span class="seller-name">{{ product.sellerName }}</span>
                </div>
              </div>
              
              <!-- 거래 진행 버튼들 -->
              <div class="action-buttons">
                <!-- 거래중 상태일 때 대기 안내 메시지 -->
                <div v-if="product.status === 'IN_PROGRESS'" class="waiting-message">
                  <i class="el-icon-time"></i>
                  <span>판매자 확정 대기중</span>
                </div>
                
                <!-- 거래중 상태일 때 취소 버튼 -->
                <button 
                  v-if="product.status === 'IN_PROGRESS'" 
                  class="action-btn secondary"
                  @click.stop="cancelTransaction(product)"
                  :disabled="cancellingPurchase === product.uid"
                >
                  {{ cancellingPurchase === product.uid ? '취소 중...' : '거래 취소' }}
                </button>
                
                <!-- 환불 요청 버튼 (완료된 거래) -->
                <button 
                  v-if="product.status === 'COMPLETED'" 
                  class="action-btn secondary" 
                  @click.stop="requestRefund(product)"
                >
                  환불 요청
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>구매한 상품이 없습니다</p>
        </div>
      </div>
    </div>

    <!-- 포인트 차감 모달 (오프라인 상품) -->
    <el-dialog
      :visible.sync="pointDeductModalVisible"
      width="460px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="point-deduct-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closePointDeductModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">포인트 차감</h3>
        
        <div v-if="selectedProduct" class="product-summary">
          <p class="summary-item">
            <span class="summary-label">상품명:</span>
            <span class="summary-value">{{ selectedProduct.title }}</span>
          </p>
          <p class="summary-item">
            <span class="summary-label">판매가:</span>
            <span class="summary-value">{{ formatPrice(selectedProduct) }}</span>
          </p>
        </div>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">구매자 이름</label>
            <input 
              v-model="deductForm.buyerName" 
              type="text" 
              placeholder="구매자 이름을 입력해주세요"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">구매자 연락처</label>
            <input 
              v-model="deductForm.buyerContact" 
              type="text" 
              placeholder="연락처를 입력해주세요 (예: 010-1234-5678)"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">차감 포인트</label>
            <input 
              v-model.number="deductForm.deductPoints" 
              type="number" 
              placeholder="차감할 포인트를 입력해주세요"
              class="form-input"
              min="0"
            />
          </div>
        </div>

        <button 
          class="submit-btn" 
          @click="submitPointDeduct"
          :disabled="!deductForm.buyerName || !deductForm.buyerContact || !deductForm.deductPoints || submittingDeduct"
        >
          {{ submittingDeduct ? '처리 중...' : '포인트 차감하기' }}
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getMyRegisteredProducts, getMyPurchasedProducts, MarketplaceProduct, MarketplacePurchase, deductPointForOfflineProduct, cancelTrade, sellerConfirmTrade } from '@/api/marketplace';

@Component({
  name: 'MyMarketplace',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeTab: 'registered' | 'purchased' = 'registered';
  private marketplaceFilter: 'all' | 'online' | 'offline' = 'all';
  private loadingRegistered = false;
  private loadingPurchased = false;
  
  // 내 등록 상품
  private registeredProducts: MarketplaceProduct[] = [];
  
  // 내 구매 상품
  private purchasedProducts: MarketplacePurchase[] = [];

  // 포인트 차감 모달
  private pointDeductModalVisible = false;
  private selectedProduct: MarketplaceProduct | null = null;
  private submittingDeduct = false;
  private deductForm = {
    buyerName: '',
    buyerContact: '',
    deductPoints: 0,
  };
  
  // 거래 취소 로딩 상태 (구매자)
  private cancellingPurchase: string | null = null;
  
  // 판매자 확정/취소 로딩 상태
  private confirmingProduct: string | null = null;
  private cancellingProduct: string | null = null;

  mounted() {
    this.loadRegisteredProducts();
  }

  private get channelDomain(): string {
    return this.$route.params.domain || '';
  }

  private async loadRegisteredProducts() {
    try {
      this.loadingRegistered = true;
      const marketplaceType = this.marketplaceFilter === 'all' ? undefined : this.marketplaceFilter;
      const response = await getMyRegisteredProducts(this.channelDomain, marketplaceType);
      this.registeredProducts = response.data.content || [];
    } catch (error: any) {
      const message = error.response?.data?.message || '등록 상품 조회에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loadingRegistered = false;
    }
  }

  private async loadPurchasedProducts() {
    try {
      this.loadingPurchased = true;
      const marketplaceType = this.marketplaceFilter === 'all' ? undefined : this.marketplaceFilter;
      const response = await getMyPurchasedProducts(this.channelDomain, marketplaceType);
      this.purchasedProducts = response.data.content || [];
    } catch (error: any) {
      const message = error.response?.data?.message || '구매 상품 조회에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loadingPurchased = false;
    }
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private onTabChange() {
    this.marketplaceFilter = 'all';
    if (this.activeTab === 'registered') {
      this.loadRegisteredProducts();
    } else {
      this.loadPurchasedProducts();
    }
  }

  private onFilterChange() {
    if (this.activeTab === 'registered') {
      this.loadRegisteredProducts();
    } else {
      this.loadPurchasedProducts();
    }
  }

  private getProductImage(product: MarketplaceProduct | MarketplacePurchase): string {
    // MarketplacePurchase의 경우 thumbnailUid 사용
    if ('thumbnailUid' in product && product.thumbnailUid) {
      return `/api/attached-file/${product.thumbnailUid}`;
    }
    
    // MarketplaceProduct의 경우 imageUids 사용
    if ('imageUids' in product && product.imageUids && product.imageUids.length > 0) {
      return `/api/attached-file/${product.imageUids[0]}`;
    }
    
    return 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=300&fit=crop';
  }

  private getStatusBadge(product: MarketplaceProduct): string {
    if (product.status === 'SOLD_OUT') return '품절';
    if (product.status === 'HIDDEN') return '비공개';
    return '';
  }

  private getProductTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      SALE: '판매',
      SHARE: '나눔',
      REQUEST: '요청',
    };
    return labels[type] || type;
  }

  private formatPrice(product: MarketplaceProduct | MarketplacePurchase): string {
    // MarketplaceProduct - 나눔 상품
    if ('productType' in product && product.productType === 'SHARE') {
      return '나눔';
    }
    
    // MarketplaceProduct - category가 SHARE인 경우
    if ('category' in product && product.category === 'SHARE') {
      return '나눔';
    }
    
    // MarketplacePurchase - productCategory가 SHARE인 경우
    if ('productCategory' in product && product.productCategory === 'SHARE') {
      return '나눔';
    }
    
    // MarketplacePurchase - totalPrice
    if ('totalPrice' in product) {
      if (product.totalPrice === 0) {
        return '나눔';
      }
      return `${product.totalPrice.toLocaleString()} R포인트`;
    }
    
    // MarketplaceProduct - price
    if ('price' in product) {
      if (product.price === 0) {
        return '나눔';
      }
      return `${product.price.toLocaleString()} R포인트`;
    }
    
    return '나눔';
  }

  private formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
  }

  private openProductDetail(product: MarketplaceProduct) {
    this.$router.push({
      name: 'MarketplaceDetail',
      params: {
        domain: this.channelDomain,
        productId: product.uid,
      },
    });
  }

  // 포인트 차감 모달 열기
  private openPointDeductModal(product: MarketplaceProduct) {
    this.selectedProduct = product;
    this.deductForm = {
      buyerName: '',
      buyerContact: '',
      deductPoints: product.price,
    };
    this.pointDeductModalVisible = true;
  }

  // 포인트 차감 모달 닫기
  private closePointDeductModal() {
    this.pointDeductModalVisible = false;
    this.selectedProduct = null;
    this.deductForm = {
      buyerName: '',
      buyerContact: '',
      deductPoints: 0,
    };
  }

  // 포인트 차감 처리
  private async submitPointDeduct() {
    if (!this.selectedProduct) return;

    try {
      this.submittingDeduct = true;
      
      await deductPointForOfflineProduct(this.selectedProduct.uid, {
        buyerName: this.deductForm.buyerName,
        buyerContact: this.deductForm.buyerContact,
        deductPoints: this.deductForm.deductPoints,
      });

      this.$message.success('포인트가 차감되었습니다');
      this.closePointDeductModal();
      // 목록 새로고침
      this.loadRegisteredProducts();
    } catch (error: any) {
      const message = error.response?.data?.message || '포인트 차감에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingDeduct = false;
    }
  }

  // 환불 요청
  private async requestRefund(product: MarketplacePurchase) {
    this.$message.warning('환불을 원하시면 관리자에게 요청하세요');
  }

  // 등록 상품 상태 배지 (판매자 관점)
  private getRegisteredStatusBadge(product: MarketplaceProduct): string {
    if (product.status === 'SOLD_OUT') return '품절';
    if (product.status === 'HIDDEN') return '비공개';
    if (product.status === 'TRADING') return '거래중';
    return '';
  }
  
  // 등록 상품 상태 배지 클래스
  private getStatusBadgeClass(product: MarketplaceProduct): string {
    if (product.status === 'TRADING') return 'status-trading';
    if (product.status === 'SOLD_OUT') return 'status-soldout';
    if (product.status === 'HIDDEN') return 'status-hidden';
    return '';
  }
  
  // 구매 상품 상태 배지 (구매자 관점)
  private getPurchaseStatusBadge(product: MarketplacePurchase): string {
    if (product.status === 'IN_PROGRESS') return '확정 대기중';
    if (product.status === 'COMPLETED') return '거래완료';
    if (product.status === 'CANCELLED') return '취소됨';
    if (product.status === 'REFUNDED') return '환불됨';
    return '';
  }
  
  // 구매 상품 상태 배지 클래스
  private getPurchaseStatusClass(product: MarketplacePurchase): string {
    if (product.status === 'IN_PROGRESS') return 'status-trading';
    if (product.status === 'COMPLETED') return 'status-completed';
    if (product.status === 'CANCELLED') return 'status-cancelled';
    if (product.status === 'REFUNDED') return 'status-refunded';
    return '';
  }
  
  // 구매 상품 상세로 이동
  private openPurchaseDetail(product: MarketplacePurchase) {
    this.$router.push({
      name: 'MarketplaceDetail',
      params: {
        domain: this.channelDomain,
        productId: product.productUid,
      },
    });
  }
  
  // 거래 취소 (구매자)
  private async cancelTransaction(product: MarketplacePurchase) {
    try {
      await this.$confirm(
        '거래를 취소하시겠습니까?',
        '거래 취소',
        {
          confirmButtonText: '취소하기',
          cancelButtonText: '돌아가기',
          type: 'warning',
        }
      );
      
      this.cancellingPurchase = product.uid;
      
      await cancelTrade(product.uid);
      
      this.$message.success('거래가 취소되었습니다');
      // 목록 새로고침
      this.loadPurchasedProducts();
    } catch (error: any) {
      if (error === 'cancel') return;
      const message = error.response?.data?.message || '거래 취소에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.cancellingPurchase = null;
    }
  }
  
  // 상품 유형에 따른 거래 라벨 반환
  private getTransactionLabel(product: MarketplaceProduct): string {
    const type = product.category || product.productType;
    switch (type) {
      case 'SALE':
        return '구매자';
      case 'SHARE':
        return '수혜자';
      case 'REQUEST':
        return '지원자';
      default:
        return '구매자';
    }
  }
  
  // 상품 유형에 따른 확정 버튼 라벨 반환
  private getConfirmButtonLabel(product: MarketplaceProduct): string {
    const type = product.category || product.productType;
    switch (type) {
      case 'SALE':
        return '판매 확정';
      case 'SHARE':
        return '나눔 확정';
      case 'REQUEST':
        return '지원 수락';
      default:
        return '거래 확정';
    }
  }
  
  // 판매자 확정 (판매자가 거래 완료 확정)
  private async sellerConfirmTransaction(product: MarketplaceProduct) {
    if (!product.currentPurchaseUid) {
      this.$message.error('거래 정보를 찾을 수 없습니다');
      return;
    }
    
    const type = product.category || product.productType;
    const priceText = product.price > 0 ? `${product.price.toLocaleString()} R포인트` : '무료';
    
    let confirmMessage = '';
    let title = '';
    
    switch (type) {
      case 'SALE':
        confirmMessage = `${product.currentBuyerName}님에게 ${priceText}를 받고 판매를 확정하시겠습니까?`;
        title = '판매 확정';
        break;
      case 'SHARE':
        confirmMessage = `${product.currentBuyerName}님에게 나눔을 확정하시겠습니까?`;
        title = '나눔 확정';
        break;
      case 'REQUEST':
        confirmMessage = `${product.currentBuyerName}님의 지원을 수락하시겠습니까? 수락 시 ${priceText}가 지급됩니다.`;
        title = '지원 수락';
        break;
      default:
        confirmMessage = '거래를 확정하시겠습니까?';
        title = '거래 확정';
    }

    try {
      await this.$confirm(confirmMessage, title, {
        confirmButtonText: '확정하기',
        cancelButtonText: '취소',
        type: 'warning',
      });
      
      this.confirmingProduct = product.uid;
      
      await sellerConfirmTrade(product.currentPurchaseUid);
      
      const successMessage = type === 'SHARE' ? '나눔이 완료되었습니다' :
                             type === 'REQUEST' ? '지원을 수락하였습니다' : 
                             '판매가 완료되었습니다';
      this.$message.success(successMessage);
      
      // 목록 새로고침
      this.loadRegisteredProducts();
    } catch (error: any) {
      if (error === 'cancel') return;
      const message = error.response?.data?.message || '거래 확정에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.confirmingProduct = null;
    }
  }
  
  // 판매자 거래 취소
  private async sellerCancelTransaction(product: MarketplaceProduct) {
    if (!product.currentPurchaseUid) {
      this.$message.error('거래 정보를 찾을 수 없습니다');
      return;
    }

    try {
      await this.$confirm(
        '거래를 취소하시겠습니까?',
        '거래 취소',
        {
          confirmButtonText: '취소하기',
          cancelButtonText: '돌아가기',
          type: 'warning',
        }
      );
      
      this.cancellingProduct = product.uid;
      
      await cancelTrade(product.currentPurchaseUid);
      
      this.$message.success('거래가 취소되었습니다');
      // 목록 새로고침
      this.loadRegisteredProducts();
    } catch (error: any) {
      if (error === 'cancel') return;
      const message = error.response?.data?.message || '거래 취소에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.cancellingProduct = null;
    }
  }
}
</script>

<style scoped lang="scss">
.my-marketplace-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.my-marketplace-main {
  flex: 1;
  margin-left: 270px;
  padding: 160px 40px 40px;
  background: #F8F9FB;
}

.page-header {
  margin-bottom: 40px;
}

.page-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 32px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
}

// 탭 메뉴
.tabs-section {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  border-bottom: 2px solid #EBEBEB;
}

.tab-btn {
  padding: 16px 24px;
  background: none;
  border: none;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  position: relative;

  &.active {
    color: #073DFF;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 3px;
      background: #073DFF;
    }
  }
}

// 필터 섹션
.filter-section {
  display: flex;
  gap: 12px;
  margin-bottom: 40px;
}

.filter-btn {
  padding: 10px 20px;
  background: #F5F5F5;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;

  &.active {
    background: #073DFF;
    color: #FFF;
    border-color: #073DFF;
  }
}

// 탭 컨텐츠
.tab-content {
  min-height: 400px;
}

// 로딩
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;

  i {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 상품 그리드
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.product-card {
  background: #FFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &.purchased-card {
    cursor: default;
  }
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
  height: 300px;

  &.has-status .product-image {
    opacity: 0.5;
  }
}

@media screen and (max-width: 1400px) {
  .product-image-wrapper {height: 300px;}
}

@media screen and (max-width: 1024px) {
  .product-image-wrapper {height: 220px;}
}

@media screen and (max-width: 768px) {
  .product-image-wrapper {height: 200px;}
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
}

.status-badge {
  position: absolute;
  top: 5%;
  left: 65%;
//   transform: translate(-50%, -50%);
  background: rgba(255, 255, 255, 0.95);
  color: #333;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 700;
  z-index: 1;
  
  &.status-trading {
    background: #FFF3E0;
    color: #E65100;
  }
  
  &.status-completed {
    background: #E8F5E9;
    color: #2E7D32;
  }
  
  &.status-cancelled {
    background: #FFEBEE;
    color: #C62828;
  }
  
  &.status-refunded {
    background: #FCE4EC;
    color: #AD1457;
  }
  
  &.status-soldout {
    background: #F5F5F5;
    color: #666;
  }
  
  &.status-hidden {
    background: #ECEFF1;
    color: #546E7A;
  }
}

.product-info {
  padding: 20px;
}

.product-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-type-badge {
  display: inline-block;
  padding: 4px 12px;
  background: #E8F0FF;
  color: #073DFF;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
}

.product-marketplace-name {
  color: #666;
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  i {
    font-size: 14px;
  }
}

.product-location {
  color: #888;
  font-size: 14px;
  margin: 0 0 8px 0;
}

.product-price {
  color: #073DFF;
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.product-stock {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.purchase-date {
  color: #888;
  font-size: 14px;
  margin: 8px 0 16px 0;
}

// 구매자/판매자 정보 스타일
.buyer-info, .seller-info {
  margin: 12px 0;
  padding: 12px;
  background: #F8F9FA;
  border-radius: 8px;
  border: 1px solid #EBEBEB;
}

.info-title {
  font-size: 13px;
  font-weight: 600;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  
  i {
    font-size: 14px;
    color: #073DFF;
  }
}

.buyer-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.buyer-profile {
  display: flex;
  align-items: center;
  gap: 8px;
}

.buyer-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  background: #E0E0E0;
}

.buyer-avatar-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #E0E0E0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #666;
}

.seller-profile {
  display: flex;
  align-items: center;
  gap: 8px;
}

.seller-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  background: #E0E0E0;
}

.seller-avatar-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #E0E0E0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #666;
}

.seller-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

// 액션 버튼
.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.waiting-message {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #FFF3E0;
  border: 1px solid #FF9800;
  border-radius: 8px;
  color: #E65100;
  font-size: 14px;
  font-weight: 600;
  width: 100%;
  justify-content: center;
  
  i {
    font-size: 16px;
  }
}

.action-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
//   transition: all 0.2s;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &.primary {
    background: #073DFF;
    color: #FFF;

    &:hover:not(:disabled) {
      background: #0535e6;
    }
  }

  &.secondary {
    background: #F5F5F5;
    color: #666;
    border: 1px solid #CECECE;

    &:hover:not(:disabled) {
      background: #E0E0E0;
    }
  }
}

// 환불 안내
.refund-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #FFF8E1;
  border: 1px solid #FFD54F;
  border-radius: 8px;
  margin-top: 16px;

  i {
    color: #FFA000;
    font-size: 18px;
  }

  span {
    color: #F57C00;
    font-size: 13px;
    font-weight: 500;
    line-height: 1.4;
  }
}

// 빈 상태
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;
  font-size: 16px;
}

// 포인트 차감 모달
::v-deep .point-deduct-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.modal-close-btn {
  position: absolute;
  right: 20px;
  top: 20px;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #666;
//   transition: color 0.2s;

  &:hover {
    color: #333;
  }
}

.modal-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0;
  text-align: center;
}

.product-summary {
  background: #F8F9FA;
  padding: 20px;
  border-radius: 10px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 0 12px 0;
  font-size: 16px;

  &:last-child {
    margin-bottom: 0;
  }
}

.summary-label {
  color: #666;
  font-weight: 500;
}

.summary-value {
  color: #222;
  font-weight: 600;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
}

.form-input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  box-sizing: border-box;
//   transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;

  &:hover:not(:disabled) {
    background: #0535e6;
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
  }
}

@media screen and (max-width: 1024px) {
  .my-marketplace-main {
    margin-left: 240px;
    padding: 160px 20px 40px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }
}

@media screen and (max-width: 768px) {
  .my-marketplace-main {
    padding: 140px 20px 40px;
    margin-left: 0;
  }
  .page-title {
    font-size: 24px;
  }

  .tabs-section {
    gap: 10px;
  }

  .tab-btn {
    padding: 12px 16px;
    font-size: 16px;
  }

  .products-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }
}

@media screen and (max-width: 500px) {
  .my-marketplace-main{
    padding: 120px 20px 40px;
  }


  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .tabs-section {
    gap: 0;
    margin-bottom: 24px;
  }

  .tab-btn {
    flex: 1;
    padding: 12px 8px;
    text-align: center;
  }

  .products-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .product-card {
    border-radius: 10px;
  }

  .product-image-wrapper {
    padding-top: 75%;
  }

  .status-badge {
    top: 12px;
    left: auto;
    right: 12px;
    padding: 8px 16px;
    font-size: 14px;
    border-radius: 6px;
  }

  .product-info {
    padding: 16px;
  }

  .product-title {
    font-size: 16px;
    margin-bottom: 6px;
  }

  .product-type-badge {
    padding: 3px 10px;
    font-size: 11px;
    margin-bottom: 6px;
  }

  .product-location {
    font-size: 12px;
    margin-bottom: 6px;
  }

  .product-price {
    font-size: 18px;
    margin-bottom: 6px;
  }

  .product-stock {
    font-size: 12px;
  }

  .purchase-date {
    font-size: 12px;
    margin: 6px 0 12px 0;
  }

  .action-buttons {
    margin-top: 12px;
    gap: 8px;
  }

  .action-btn {
    height: 36px;
    font-size: 13px;
    border-radius: 6px;
  }

  .loading-container {
    padding: 60px 20px;

    i {
      font-size: 36px;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
    }
  }

  .empty-container {
    padding: 60px 20px;
    font-size: 14px;
  }

  // 포인트 차감 모달 모바일
  ::v-deep .point-deduct-modal {
    width: 100% !important;
    max-width: 100vw;
    margin: 0 !important;
    height: 100%;
    border-radius: 0;

    .el-dialog__body {
      padding: 60px 20px 20px;
      height: 100%;
      overflow-y: auto;
    }
  }

  .modal-content {
    gap: 20px;
  }

  .modal-close-btn {
    right: 16px;
    top: 16px;
  }

  .modal-title {
    font-size: 20px;
  }

  .product-summary {
    padding: 16px;
    border-radius: 8px;
  }

  .summary-item {
    margin-bottom: 10px;
    font-size: 14px;
  }

  .form-section {
    gap: 16px;
  }

  .form-group {
    gap: 6px;
  }

  .form-label {
    font-size: 14px;
  }

  .form-input {
    height: 44px;
    padding: 0 12px;
    font-size: 14px;
    border-radius: 6px;
  }

  .submit-btn {
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
  }
}
</style>
