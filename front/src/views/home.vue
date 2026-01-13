<template>
  <div id="container" class="home-main">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">커뮤니티를 찾아보세요!</h1>
        <p class="hero-subtitle">
          <a class="link" @click="handleChannelCreate">나만의 커뮤니티를 만들어보세요!</a>
        </p>
      </div>
      <div class="search-container">
        <div class="search-input-wrapper">
          <svg class="search-icon" width="34" height="34" viewBox="0 0 34 34" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24.4379 14.8739C24.4378 12.3379 23.4295 9.90618 21.6364 8.11292C19.843 6.31952 17.4103 5.3114 14.874 5.3114C12.3379 5.31151 9.90633 6.31962 8.11304 8.11292C6.31974 9.90621 5.31163 12.3378 5.31152 14.8739C5.31152 17.4101 6.31965 19.8429 8.11304 21.6363C9.90631 23.4294 12.3381 24.4377 14.874 24.4378C17.4103 24.4378 19.843 23.4297 21.6364 21.6363C23.4298 19.8429 24.4379 17.4101 24.4379 14.8739ZM26.5629 14.8739C26.5629 17.6175 25.5956 20.2603 23.8541 22.3515L30.5016 28.9991C30.9163 29.414 30.9165 30.0867 30.5016 30.5015C30.0868 30.9163 29.4141 30.9162 28.9992 30.5015L22.3516 23.854C20.2604 25.5954 17.6176 26.5628 14.874 26.5628C11.7743 26.5627 8.80102 25.3305 6.60921 23.1387C4.41756 20.9468 3.18652 17.9735 3.18652 14.8739C3.18663 11.7742 4.4174 8.8009 6.60921 6.60909C8.80102 4.41728 11.7743 3.18651 14.874 3.1864C17.9737 3.1864 20.947 4.41743 23.1388 6.60909C25.3306 8.8009 26.5628 11.7742 26.5629 14.8739Z" fill="#888888"/>
          </svg>
          <input
            type="text"
            placeholder="어떤 커뮤니티를 찾고 계시나요?"
            v-model="listQuery.searchValue"
            @keyup.enter="handleSearch"
          >
        </div>
      </div>
    </div>

    <div class="category-section">
      <div class="category-wrapper">
        <button class="mobile-filter-btn" @click="handleFilter()" v-if="filterVisible">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10.5 6H20.25M10.5 6C10.5 6.39782 10.342 6.77936 10.0607 7.06066C9.77936 7.34196 9.39782 7.5 9 7.5C8.60218 7.5 8.22064 7.34196 7.93934 7.06066C7.65804 6.77936 7.5 6.39782 7.5 6M10.5 6C10.5 5.60218 10.342 5.22064 10.0607 4.93934C9.77936 4.65804 9.39782 4.5 9 4.5C8.60218 4.5 8.22064 4.65804 7.93934 4.93934C7.65804 5.22064 7.5 5.60218 7.5 6M7.5 6H3.75M10.5 18H20.25M10.5 18C10.5 18.3978 10.342 18.7794 10.0607 19.0607C9.77936 19.342 9.39782 19.5 9 19.5C8.60218 19.5 8.22064 19.342 7.93934 19.0607C7.65804 18.7794 7.5 18.3978 7.5 18M10.5 18C10.5 17.6022 10.342 17.2206 10.0607 16.9393C9.77936 16.658 9.39782 16.5 9 16.5C8.60218 16.5 8.22064 16.658 7.93934 16.9393C7.65804 17.2206 7.5 17.6022 7.5 18M7.5 18H3.75M16.5 12H20.25M16.5 12C16.5 12.3978 16.342 12.7794 16.0607 13.0607C15.7794 13.342 15.3978 13.5 15 13.5C14.6022 13.5 14.2206 13.342 13.9393 13.0607C13.658 12.7794 13.5 12.3978 13.5 12M16.5 12C16.5 11.6022 16.342 11.2206 16.0607 10.9393C15.7794 10.658 15.3978 10.5 15 10.5C14.6022 10.5 14.2206 10.658 13.9393 10.9393C13.658 11.2206 13.5 11.6022 13.5 12M13.5 12H3.75" stroke="black" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
          <div class="category-pills">
            <button
              :class="['category-pill', { active: listQuery.categoryUid === 'all' }]"
              @click="handleChangeCategory('all')"
            >
              전체보기
            </button>
            <button
              v-for="category in categoryList.slice(0, 8)"
              :key="category.uid"
              :class="['category-pill', { active: listQuery.categoryUid === category.uid }]"
              @click="handleChangeCategory(category.uid)"
            >
              {{ category.name }}
            </button>
          </div>
        <div class="filter-controls">
          <button class="filter-button" @click="handleFilter()">
            <span>필터</span>
            <svg width="29" height="29" viewBox="0 0 29 29" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10.875 19.0312C11.5961 19.0312 12.2874 19.3179 12.7972 19.8278C13.0864 20.1169 13.3005 20.4655 13.4344 20.8438H24.4688C24.9693 20.8438 25.375 21.2495 25.375 21.75C25.375 22.2505 24.9693 22.6562 24.4688 22.6562H13.4344C13.3005 23.0345 13.0864 23.3831 12.7972 23.6722C12.2874 24.1821 11.5961 24.4688 10.875 24.4688C10.1539 24.4688 9.46262 24.1821 8.95276 23.6722C8.6636 23.3831 8.44949 23.0345 8.31555 22.6562H4.53125C4.03074 22.6562 3.625 22.2505 3.625 21.75C3.625 21.2495 4.03074 20.8438 4.53125 20.8438H8.31555C8.44949 20.4655 8.6636 20.1169 8.95276 19.8278C9.46262 19.3179 10.1539 19.0312 10.875 19.0312ZM10.875 20.8438C10.6346 20.8438 10.4042 20.9393 10.2343 21.1093C10.0643 21.2792 9.96875 21.5096 9.96875 21.75C9.96875 21.9904 10.0643 22.2208 10.2343 22.3907C10.4042 22.5607 10.6346 22.6562 10.875 22.6562C11.1154 22.6562 11.3458 22.5607 11.5157 22.3907C11.6857 22.2208 11.7812 21.9904 11.7812 21.75L11.7635 21.5718C11.7287 21.3978 11.6431 21.2366 11.5157 21.1093C11.3458 20.9393 11.1154 20.8438 10.875 20.8438ZM18.394 11.7942C19.0163 11.8561 19.6012 12.1317 20.0472 12.5778C20.3364 12.8669 20.5505 13.2155 20.6844 13.5938H24.4688C24.9693 13.5938 25.375 13.9995 25.375 14.5C25.375 15.0005 24.9693 15.4062 24.4688 15.4062H20.6844C20.5505 15.7845 20.3364 16.1331 20.0472 16.4222C19.6012 16.8683 19.0163 17.1439 18.394 17.2058L18.125 17.2188C17.4039 17.2188 16.7126 16.9321 16.2028 16.4222C15.9136 16.1331 15.6995 15.7845 15.5656 15.4062H4.53125C4.03074 15.4062 3.625 15.0005 3.625 14.5C3.625 13.9995 4.03074 13.5938 4.53125 13.5938H15.5656C15.6995 13.2155 15.9136 12.8669 16.2028 12.5778C16.7126 12.0679 17.4039 11.7813 18.125 11.7812L18.394 11.7942ZM17.9468 13.6115C17.7728 13.6463 17.6116 13.7319 17.4843 13.8593C17.3143 14.0292 17.2188 14.2596 17.2188 14.5L17.2365 14.6782C17.2713 14.8522 17.3569 15.0134 17.4843 15.1407C17.6542 15.3107 17.8846 15.4062 18.125 15.4062L18.3032 15.3885C18.4772 15.3537 18.6384 15.2681 18.7657 15.1407C18.9357 14.9708 19.0313 14.7404 19.0313 14.5C19.0313 14.2596 18.9357 14.0292 18.7657 13.8593C18.5958 13.6893 18.3654 13.5938 18.125 13.5938L17.9468 13.6115ZM10.875 4.53125C11.5961 4.53125 12.2874 4.81789 12.7972 5.32776C13.0864 5.61691 13.3005 5.96549 13.4344 6.34375H24.4688C24.9693 6.34375 25.375 6.74949 25.375 7.25C25.375 7.75051 24.9693 8.15625 24.4688 8.15625H13.4344C13.3005 8.53451 13.0864 8.88309 12.7972 9.17224C12.2874 9.68211 11.5961 9.96875 10.875 9.96875C10.1539 9.96875 9.46262 9.68211 8.95276 9.17224C8.6636 8.88309 8.44949 8.53451 8.31555 8.15625H4.53125C4.03074 8.15625 3.625 7.75051 3.625 7.25C3.625 6.74949 4.03074 6.34375 4.53125 6.34375H8.31555C8.44949 5.96549 8.6636 5.61691 8.95276 5.32776C9.46262 4.81789 10.1539 4.53125 10.875 4.53125ZM10.875 6.34375C10.6346 6.34375 10.4042 6.4393 10.2343 6.60925C10.0643 6.77921 9.96875 7.00965 9.96875 7.25C9.96875 7.49035 10.0643 7.72079 10.2343 7.89075C10.4042 8.0607 10.6346 8.15625 10.875 8.15625C11.1154 8.15625 11.3458 8.0607 11.5157 7.89075C11.6857 7.72079 11.7812 7.49035 11.7812 7.25L11.7635 7.07182C11.7287 6.89784 11.6431 6.73658 11.5157 6.60925C11.3458 6.4393 11.1154 6.34375 10.875 6.34375Z" fill="#222222"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
    <div class="community-grid-wrapper">
      <div class="community-grid-section" v-loading="loading">
        <!-- show a friendly message when no communities are found for the selected category -->
        <div v-if="!loading && channelList.length === 0" class="no-results">
          <div class="no-results-box">
            <p class="no-results-title">해당 카테고리에 커뮤니티가 없습니다.</p>
            <p class="no-results-sub">다른 카테고리를 선택하거나 검색어를 변경해보세요.</p>
          </div>
        </div>
        <div v-else class="community-grid">
          <div class="community-card" v-for="item in channelList" :key="item.uid" @click="handleContent(0, item)">
            <div class="card-image">
              <img v-if="item.coverImageList.length > 0" :src="apiUrl + '/attached-file/' + item.coverImageList[0]?.fileUid" alt="">
              <img v-else src="@/assets/images/logo.png" alt="">
            </div>
            <div class="card-content">
              <h3 class="card-title">{{ item.name }}</h3>
              <p class="card-description">{{ item.introduce }}</p>
            </div>
          </div>
        </div>
        <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="handleChangePaging"
      />
      </div>
    </div>

    <el-dialog
      title="카테고리 더보기"
      width="600px"
      class="dialog-wrap"
      :visible.sync="moreVisible"
    >
      <el-radio-group v-model="listQuery.categoryUid" @change="handleChangeCategory">
        <el-radio-button key="all" label="all" class="home-btn mb-only">ALL</el-radio-button>
        <el-radio-button v-for="category in categoryList.slice(0, 7)" :key="category.uid" :label="category.uid" class="mb-only">
          {{ category.name }}
        </el-radio-button>
        <el-radio-button v-for="category in categoryList.slice(8, 12)" :key="category.uid" :label="category.uid">
          {{ category.name }}
        </el-radio-button>
      </el-radio-group>
    </el-dialog>

    <el-dialog
      title="필터"
      width="500px"
      class="filter-dialog"
      :visible.sync="filterVisible"
      :close-on-click-modal="true"
      :close-on-press-escape="true">
      <div class="filter-content">
        <div class="filter-section">
          <h3 class="filter-section-title">공개 여부</h3>
          <div class="filter-options">
            <button
              v-for="(item, index) in filterList.privateStatus"
              :key="index"
              :class="['filter-option-btn', { active: listQuery.privateStatus === item.value }]"
              @click="handleFilterChange('privateStatus', item.value)"
            >
              {{ item.label }}
            </button>
          </div>
        </div>

        <div class="filter-divider"></div>

        <div class="filter-section">
          <h3 class="filter-section-title">가격</h3>
          <div class="filter-options">
            <button
              v-for="(item, index) in filterList.price"
              :key="index"
              :class="['filter-option-btn', { active: listQuery.minPrice === item.value }]"
              @click="handleFilterChange('minPrice', item.value)"
            >
              {{ item.label }}
            </button>
          </div>
        </div>
      </div>

      <div slot="footer" class="filter-footer">
        <button class="filter-reset-btn" @click="handleResetFilter">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M2.5 5.83333H17.5M8.33333 9.16667V14.1667M11.6667 9.16667V14.1667M3.33333 5.83333L4.16667 15.8333C4.16667 16.2754 4.34226 16.6993 4.65482 17.0118C4.96738 17.3244 5.39131 17.5 5.83333 17.5H14.1667C14.6087 17.5 15.0326 17.3244 15.3452 17.0118C15.6577 16.6993 15.8333 16.2754 15.8333 15.8333L16.6667 5.83333M7.5 5.83333V3.33333C7.5 3.11232 7.5878 2.90036 7.74408 2.74408C7.90036 2.5878 8.11232 2.5 8.33333 2.5H11.6667C11.8877 2.5 12.0996 2.5878 12.2559 2.74408C12.4122 2.90036 12.5 3.11232 12.5 3.33333V5.83333" stroke="#888888" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          초기화
        </button>
        <button class="filter-apply-btn" @click="handleApplyFilter">
          적용하기
        </button>
      </div>
    </el-dialog>

    <div class="component-container">
      <div class="component">
        <UserModal :userModalVisible="userModalVisible" @close="userModalVisible = false;"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { getChannelList, getChannelCategory, canCreateChannel } from '@/api/channel';
import UserModal from '@/views/components/userModal.vue';
import Pagination from '@/components/Pagination/index.vue';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';

@Component({
  name: '',
  components: {
    Pagination, UserModal,
  },
})

export default class extends Vue {
  mounted() {
    this.getCategoryList();
    this.getChannelList();
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private loading = true;

  private totalElements = 0;

  private getCategoryList() {
    this.loading = true;
    getChannelCategory().then((res) => {
      this.categoryList = res.data;
      this.loading = false;
    });
  }

  private getChannelList() {
    this.loading = true;
    getChannelList(this.listQuery).then((res) => {
      this.channelList = res.data.content;
      this.totalElements = res.data.totalElements;
      this.loading = false;
    });
  }

  private userModalVisible = false;

  private filterVisible = false;

  private moreVisible = false;

  private listQuery: any = {
    typeUid: '',
    categoryUid: 'all',
    privateStatus: '',
    minPrice: '',
    searchType: 'nameOrintroduce',
    searchValue: this.$route.query.searchValue ? this.$route.query.searchValue : '',
    page: 0,
    size: 6,
  }

  private categoryList: any = [];

  private channelList: any = [];

  private filterList: any = {
    privateStatus: [
      {
        label: '전체',
        value: '',
      },
      {
        label: '공개',
        value: false,
      },
      {
        label: '비공개',
        value: true,
      },
    ],
    price: [
      {
        label: '전체',
        value: '',
      },
      {
        label: '무료',
        value: 0,
      },
      {
        label: '유료',
        value: 1,
      },
    ],
  };

  private async handleContent(status: any, channel: any) {
    this.$emit('select-channel', channel);
    this.$router.push({ name: 'Channel', params: { status, domain: channel.domain } });
  }

  private handleMore() {
    this.moreVisible = true;
  }

  private handleFilter() {
    this.filterVisible = true;
  }

  private async handleChannelCreate() {
    if (!UserModule.isLogin) {
      this.userModalVisible = true;
    } else {
      // 커뮤니티 생성 가능 여부 확인
      try {
        const response = await canCreateChannel();
        if (response.data === true) {
          this.$router.push({ name: 'CreateCommunity' });
        } else {
          this.$message.warning('커뮤니티는 최대 3개까지만 생성할 수 있습니다.');
        }
      } catch (error: any) {
        const message = error.response?.data?.message || '커뮤니티 생성 가능 여부를 확인할 수 없습니다.';
        this.$message.error(message);
      }
    }
  }

  private handleSearch() {
    this.getChannelList();
  }

  private handleChangeCategory(uid: string) {
    this.listQuery = {
      ...this.listQuery,
      categoryUid: uid,
    };
    this.getChannelList();
    this.moreVisible = false;
  }

  private handleChangePaging() {
    this.getChannelList();
  }

  private handleFilterChange(filterType: string, value: any) {
    if (filterType === 'privateStatus') {
      this.listQuery.privateStatus = value;
    } else if (filterType === 'minPrice') {
      this.listQuery.minPrice = value;
    }
  }

  private handleResetFilter() {
    this.listQuery.privateStatus = '';
    this.listQuery.minPrice = '';
    this.getChannelList();
  }

  private handleApplyFilter() {
    this.getChannelList();
    this.filterVisible = false;
  }
}
</script>

<style scoped lang="scss">
.home-main {
  background: #fff;
}

.hero-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 76px 20px 48px;
  gap: 48px;
  border-bottom: 2px solid #EBEBEB;
}

.hero-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.hero-title {
  color: #222;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 52px;
  font-weight: 800;
  line-height: 1.4em;
}

.hero-subtitle {
  color: #083DFE;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;

  .link {
    color: #083DFE;
    text-decoration: none;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

@media screen and (max-width: 768px) {
  .hero-section {padding: 65px 15px 45px;}
  .hero-title {font-size: 40px;}
  .hero-subtitle {font-size: 20px;}
}

@media screen and (max-width: 500px) {
  .hero-title {font-size: clamp(30px, 2vw, 50px);}
  .hero-subtitle {font-size: 18px;}
}


.search-container {
  width: 100%;
  max-width: 484px;
  padding: 0 20px;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  // background: #F8F9FB;

  .search-icon {
    flex-shrink: 0;
  }

  input {
    flex: 1;
    border: none;
    background: transparent;
    color: #888;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 22px;
    letter-spacing: -2px;
    font-weight: 400;
    line-height: 100%;
    outline: none;

    &::placeholder {
      color: #888;
    }
  }
}

@media screen and (max-width: 768px) {
  .search-input-wrapper input {font-size: clamp(16px, 2vw, 22px)}
}

@media screen and (max-width: 500px) {
  .search-container{padding: 0;}
  .search-input-wrapper {padding: 10px 15px}
  .search-input-wrapper input {font-size: clamp(14px, 2vw, 22px)}
}

.category-section {
  display: flex;
  justify-content: center;
  padding: 24px 0;
  border-bottom: 2px solid #EBEBEB;
  background: #FFF;
}

.category-wrapper {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  max-width: 1200px;
  padding: 0 20px;
}

.mobile-filter-btn {
  display: none;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;

  &:hover {
    opacity: 0.7;
  }

  svg {
    width: 24px;
    height: 24px;
  }
}

.category-pills { flex: 0 1 calc(100% - 110px) ;display: flex; align-items: center; gap: 10px; flex-wrap: wrap; overflow-x: auto; -webkit-overflow-scrolling: touch; overflow-y: hidden;}
.category-pills::-webkit-scrollbar{display: none;}
.category-pills::-webkit-scrollbar-thumb{background-color:#272c8f;border-radius:10px}
.category-pills::-webkit-scrollbar-track{background-color:#d2d2d2;border-radius:10px}

.category-pill { 
  padding: 14px 24px;
  border-radius: 100px;
  color: #8A8A8A;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: clamp(16px, 2vw, 22px);
  font-weight: 700;
  line-height: 100%;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;

  &:nth-child(2) {
    background: rgba(255, 242, 238, 0.50);
  }

  &:nth-child(3) {
    background: rgba(251, 243, 255, 0.50);
  }

  &:nth-child(4) {
    background: rgba(230, 255, 251, 0.50);
  }

  &:nth-child(5) {
    background: rgba(255, 251, 234, 0.80);
  }

  &:nth-child(6) {
    background: rgba(255, 239, 252, 0.50);
  }

  &:nth-child(7) {
    background: rgba(243, 255, 217, 0.50);
  }

  &:nth-child(8) {
    background: rgba(232, 248, 255, 0.50);
  }

  &:nth-child(9) {
    background: rgba(242, 242, 242, 0.50);
  }

  &:hover {
    opacity: 0.8;
  }

  &.active {
    background: #0F64EF;
    color: #FFF;
  }
}

.filter-controls { display: flex; align-items: center; gap: 8px;}
.filter-button { display: flex; justify-content: center; align-items: center; gap: 8px; height: 46px; padding: 0 16px; background: transparent; border: none; cursor: pointer; transition: opacity 0.3s ease; &:hover{ opacity: 0.7;}

  span {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 100%;
    min-width: 40px;
  }

  svg {
    flex-shrink: 0;
  }
}

@media screen and (max-width: 1200px) {
  .category-pill {flex: 0 1 calc(20% - 8px);}
}
@media screen and (max-width: 768px) {
  .category-section {padding: 20px 0 30px;}
  .category-wrapper {padding: 0 0 0 20px; flex-direction: column-reverse;position: relative; gap: 3px;}
  .filter-button { padding: 0;}
  .category-pills {flex: 0 1 calc(100% - 50px);width: 100%;}
  
}

@media screen and (max-width: 600px) {
  .category-section {padding: 10px 0 30px;}
  .category-pills {flex: 0 1 calc(100% - 50px); flex-wrap: unset;width: 100%;padding: 0 0 15px;overflow-x: scroll;}
  .category-pills::-webkit-scrollbar{height:6px;}
  .category-pills::-webkit-scrollbar-thumb{background-color:#0F64EF;border-radius:100px}
  .category-pills::-webkit-scrollbar-track{background-color:#d2d2d2;border-radius:100px}
  .category-pill {flex: 0 1 100%;}
}

@media screen and (max-width: 500px) {
  .category-pills {padding-right:20px;}
  .category-pill {padding: 10px; font-size: 15px;}
}

.community-grid-wrapper { background: #F8F9FB;}
.community-grid-section { width: 100%; max-width: 1200px; margin: 0 auto; padding: 40px 0px;}
.community-grid { display: flex; flex-wrap: wrap; gap: 50px; width: 100%; max-width: 1200px;}
.community-card { flex: 0 1 calc(100% / 3 - 34px); display: flex; flex-direction: column; gap: 28px; cursor: pointer; transition: transform 0.3s ease; &:hover{ transform: translateY(-4px);}}

@media screen and (max-width: 1200px) {
  .community-grid-section {padding: 40px 20px;}
}

@media screen and (max-width: 768px) {
  .community-grid {gap: 20px;}
  .community-card {flex: 0 1 calc(100% / 3 - 14px);}
}

@media screen and (max-width: 600px) {
  .community-grid {gap: 20px 15px;}
  .community-card {flex: 0 1 calc(100% / 2 - 8px);}
}

@media screen and (max-width: 425px) {
  .community-grid {gap: 20px;}
  .community-card {flex: 0 1 100%;}
}

.card-image { width: 100%; aspect-ratio: 366 / 231.07; border-radius: 10px; overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.card-title { color: #222; font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif; font-size: 26px; font-weight: 700; line-height: 100%; margin: 0; text-align: left;;}
.card-description { color: #888; font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif; font-size: 22px; font-weight: 500; line-height: 105%; margin: 0; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; text-align: left;}

@media screen and (max-width: 1024px) {
  .card-title {font-size: clamp(16px, 2vw, 22px);line-height: 1.4em;}
  .card-description {font-size: clamp(16px, 2vw, 18px); line-height: 1.4em;}
}

@media screen and (max-width: 768px) {
  .community-card {gap: 20px;}
  .card-content {gap: 10px;}
}

.no-results {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;

  .no-results-box {
    padding: 36px 48px;
    text-align: center;
  }

  .no-results-title {
    margin: 0 0 8px 0;
    color: #222;
    font-size: 22px;
    font-weight: 700;
  }

  .no-results-sub {
    margin: 0;
    color: #888;
    font-size: 16px;
  }
}

/* Filter Dialog Styles - Modern & Clean */
.filter-dialog {
  ::v-deep .el-dialog {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }

  ::v-deep .el-dialog__header {
    position: relative;
    padding: 28px 32px;
    border-bottom: 2px solid #EBEBEB;
    background: #FFF;

    .el-dialog__title {
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 24px;
      font-weight: 700;
      line-height: 100%;
    }

    .el-dialog__headerbtn {
      top: 50%;
      right: 32px;
      transform: translateY(-50%);
      padding-top: 2px;

      .el-dialog__close {
        color: #888;
        font-size: 24px;
        font-weight: 400;

        &:hover {
          color: #222;
        }
      }
    }
  }

  ::v-deep .el-dialog__body {
    padding: 32px;
    background: #FFF;
  }

  ::v-deep .el-dialog__footer {
    padding: 0;
    border-top: 2px solid #EBEBEB;
  }
}

.filter-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.filter-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-section-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
}

.filter-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-option-btn {
  display: flex;
  padding: 12px 24px;
  justify-content: center;
  align-items: center;
  border-radius: 100px;
  background: #F2F2F2;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #E5E5E5;
    color: #444;
  }

  &.active {
    background: #073DFF;
    color: #FFF;
    border-color: #073DFF;
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.25);
  }
}

.filter-divider {
  width: 100%;
  height: 1px;
  background: #EBEBEB;
}

.filter-footer {
  display: flex;
  gap: 12px;
  padding: 20px 32px;
  background: #FFF;
}

.filter-reset-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex: 1;
  height: 52px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  svg {
    flex-shrink: 0;
  }

  &:hover {
    background: #F8F9FB;
    border-color: #CECECE;
    color: #444;
  }

  &:active {
    transform: translateY(1px);
  }
}

.filter-apply-btn {
  flex: 2;
  height: 52px;
  border-radius: 10px;
  border: none;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #0535e6;
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.3);
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0);
  }
}

/* Dialog z-index fix */
::v-deep .el-dialog__wrapper {
  z-index: 10001 !important;
}

@media (max-width: 768px) {
  .filter-dialog {
    ::v-deep .el-dialog { width: 90% !important; border-radius: 16px;}

    ::v-deep .el-dialog__header { padding: 24px; 
      .el-dialog__title{ font-size: 20px;} 
    }

    ::v-deep .el-dialog__body {
      padding: 24px;
    }
  }

  .filter-content {
    gap: 24px;
  }

  .filter-section-title {
    font-size: 16px;
  }

  .filter-option-btn {
    padding: 10px 20px;
    font-size: 14px;
  }

  .filter-footer {
    padding: 16px 24px;
    flex-direction: column;
  }

  .filter-reset-btn,
  .filter-apply-btn {
    flex: 1;
    width: 100%;
  }
  .filter-reset-btn {padding: 10px 0;}
  .filter-apply-btn {line-height: 35px;padding: 5px 0;}
}

@media (max-width: 600px) {
  .filter-dialog {
    ::v-deep .el-dialog {
      width: 95% !important;
      margin: 20px auto !important;
    }

    ::v-deep .el-dialog__header {
      padding: 20px;

      .el-dialog__title {
        font-size: 18px;
      }
    }

    ::v-deep .el-dialog__body {
      padding: 20px;
    }
  }

  .filter-content {
    gap: 20px;
  }

  .filter-section {
    gap: 12px;
  }

  .filter-section-title {
    font-size: 15px;
  }

  .filter-options {
    gap: 8px;
  }

  .filter-footer {
    padding: 16px 20px;
    gap: 8px;
  }

  @media screen and (max-width: 500px) {
    .filter-reset-btn {padding: 10px 0;}
  }

  .header-nav {visibility: hidden;}
}
</style>