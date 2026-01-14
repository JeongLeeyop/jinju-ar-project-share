<template>
	<div class="food-add-wrap product-category">
		<div class="food-add-title">
      <div class="product-category__header" style="display: flex;gap:30px;align-items: center;">
        <p class="tl">설문조사 항목관리</p>
      </div>
		</div>
		<el-form ref="form" onsubmit="return false" v-loading="loading">
			<div class="food-add-section">
				<div class="food-add-content1 category-tree">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
            <el-tabs v-model="currentPage" tab-position="left" style="height: 90%; width:100%;" @tab-click="handleTabClick">
              <el-tab-pane label="Page1" name="page1">
                <div class="tab-content-wrapper" v-loading="pageLoading.page1">
                  <div class="custom-tree">
                    <div
                      v-for="item in pageData.page1.inquiries"
                      :key="item.uid"
                      class="category-item"
                      @click="handleClickCategory(item)"
                    >
                      <span class="category-node__name">{{ item.name }}</span>
                      <span class="category-node__btns">
                        <el-button
                          type="text"
                          size="mini"
                          class="category-node__btn"
                          @click.stop="handleRemoveCategoryDiv(item, 'page1')">
                          삭제
                        </el-button>
                      </span>
                    </div>
                  </div>
                  <!-- 추가 버튼을 하단에 배치 -->
                  <div style="padding: 20px; text-align: center; border-top: 1px solid #f0f0f0; margin-top: 20px;">
                    <el-button type="primary" @click="handleAppendCategory('page1')">추가</el-button>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Page2" name="page2">
                <div class="tab-content-wrapper" v-loading="pageLoading.page2">
                  <div class="custom-tree">
                    <div
                      v-for="item in pageData.page2.inquiries"
                      :key="item.uid"
                      class="category-item"
                      @click="handleClickCategory(item)"
                    >
                      <span class="category-node__name">{{ item.name }}</span>
                      <span class="category-node__btns">
                        <el-button
                          type="text"
                          size="mini"
                          class="category-node__btn"
                          @click.stop="handleRemoveCategoryDiv(item, 'page2')">
                          삭제
                        </el-button>
                      </span>
                    </div>
                  </div>
                  <!-- 추가 버튼을 하단에 배치 -->
                  <div style="padding: 20px; text-align: center; border-top: 1px solid #f0f0f0; margin-top: 20px;">
                    <el-button type="primary" @click="handleAppendCategory('page2')">추가</el-button>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="Page3" name="page3">
                <div class="tab-content-wrapper" v-loading="pageLoading.page3" >
                  <div class="custom-tree">
                    <div
                      v-for="item in pageData.page3.inquiries"
                      :key="item.uid"
                      class="category-item"
                      @click="handleClickCategory(item)"
                    >
                      <span class="category-node__name">{{ item.name }}</span>
                      <span class="category-node__btns">
                        <el-button
                          type="text"
                          size="mini"
                          class="category-node__btn"
                          @click.stop="handleRemoveCategoryDiv(item, 'page3')">
                          삭제
                        </el-button>
                      </span>
                    </div>
                  </div>
                  <!-- 추가 버튼을 하단에 배치 -->
                  <div style="padding: 20px; text-align: center; border-top: 1px solid #f0f0f0; margin-top: 20px;">
                    <el-button type="primary" @click="handleAppendCategory('page3')">추가</el-button>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
					</div>
				</div>
				<div class="food-add-content1">
					<p class="content-title">상세정보</p>
					<div class="content2-input">
						<div v-if="currentCategory.uid || currentCategory.new">
							<div class="shop-input-wr">
								<label for="categoryName">이름</label>
								<el-form-item>
									<el-input
										v-model="currentCategory.name"
										placeholder="대분류 이름을 입력하세요"
										@change="updateCategoryName" />
								</el-form-item>
							</div>
							<div class="shop-input-wr">
								<label for="categoryType">타입</label>
								<el-form-item>
									<el-select
										v-model="currentCategory.type"
										placeholder="타입을 선택하세요"
										style="width: 100%"
										>
										<el-option label="단일선택" value="select" />
										<el-option label="다중선택" value="multiSelect" />
										<el-option label="점수" value="score" />
										<!-- <el-option label="입력형" value="select" /> -->
										<!-- <el-option label="날짜" value="date" /> -->
									</el-select>
								</el-form-item>
							</div>
							<div v-if="currentCategory.type === 'select' || currentCategory.type === 'multiSelect'" class="shop-input-wr">
								<label>선택옵션</label>
								<el-form-item>
									<div class="option-tags">
										<el-tag
											v-for="(option, index) in currentCategory.options"
											:key="index"
											closable
											@close="removeOption(index)"
											style="margin-right: 8px; margin-bottom: 8px;">
											{{ option }}
										</el-tag>
										<el-input
											v-if="showNewOptionInput"
											v-model="newOptionValue"
											@keyup.enter.native="addOption"
											@blur="addOption"
											size="small"
											style="width: 120px; margin-right: 8px;"
											placeholder="옵션 입력" />
										<el-button v-else @click="showNewOptionInput = true" size="small" type="primary">+ 추가</el-button>
									</div>
								</el-form-item>
							</div>
              <div class="shop-radio-wr">
								<label>사용여부</label>
								<el-form-item>
									<el-radio-group v-model="currentCategory.useStatus">
										<el-radio :label="true">사용</el-radio>
										<el-radio :label="false">미사용</el-radio>
									</el-radio-group>
								</el-form-item>
							</div>
							<div class="shop-btn-wr" style="margin-top: 20px;">
								<el-button type="primary" @click="saveCategoryDetails">저장</el-button>
							</div>
						</div>
						<div v-else-if="!currentCategory.uid">
							<div class="shop-input-wr">
								<label for="pageTitle">페이지 타이틀</label>
								<el-form-item>
									<el-input
										v-model="pageData[currentPage].settings.title"
										placeholder="페이지 타이틀을 입력하세요"
										/>
								</el-form-item>
							</div>
							<div class="shop-radio-wr">
								<label>페이지 사용여부</label>
								<el-form-item>
									<el-radio-group v-model="pageData[currentPage].settings.useStatus">
										<el-radio :label="true">사용</el-radio>
										<el-radio :label="false">미사용</el-radio>
									</el-radio-group>
								</el-form-item>
							</div>
							<div class="shop-btn-wr" style="margin-top: 20px;">
								<el-button type="primary" @click="savePageSettings">페이지 설정 저장</el-button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang="ts">
import {
  getInquiryList,
  addInquiry,
  updateInquiry,
  updatePage,
  deleteMissionInquiry,
} from '@/api/missionInquiry';
import { Vue, Component } from 'vue-property-decorator';

@Component({
})
export default class extends Vue {
  mounted() {
    getInquiryList().then((response) => {
      this.loadApiData(response.data);
    }).catch((error) => {
      console.error('API 호출 실패:', error);
      this.loading = false;
      this.$message.error('설문조사 데이터를 불러오는데 실패했습니다.');
    });
  }

  private loading = true;

  // 페이지별 데이터 관리 (설정 + 설문 항목 통합)
  private pageData: Record<string, any> = {
    page1: {
      settings: {
        idx: null,
        title: '',
        useStatus: true,
        pageNum: 1,
      },
      inquiries: [] as any[],
    },
    page2: {
      settings: {
        idx: null,
        title: '',
        useStatus: true,
        pageNum: 2,
      },
      inquiries: [] as any[],
    },
    page3: {
      settings: {
        idx: null,
        title: '',
        useStatus: true,
        pageNum: 3,
      },
      inquiries: [] as any[],
    },
  };

  // 페이지별 로딩 상태 관리
  private pageLoading: Record<string, boolean> = {
    page1: false,
    page2: false,
    page3: false,
  };

  private currentPage = 'page1';

  private categoryList: any = []; // 호환성을 위해 유지

  private currentCategory: any = {};

  private currentOption: any = {};

  private showNewOptionInput = false;

  private newOptionValue = '';

  // 탭 클릭 이벤트 핸들러
  private handleTabClick(tab: any) {
    this.currentPage = tab.name;
    this.loadPageData(tab.name);
    this.currentCategory = {}; // 탭 변경 시 선택된 카테고리 초기화
  }

  // 페이지별 데이터 로딩
  private loadPageData(pageName: string) {
    // API 데이터만 사용하므로 추가 로딩 불필요
    if (this.pageData[pageName].inquiries.length === 0) {
      this.pageLoading[pageName] = false;
    }
  }

  // API 데이터를 페이지별로 분류하여 로드
  private loadApiData(apiData: any[]) {
    this.loading = false;
    // API 데이터를 페이지별로 분류
    apiData.forEach((pageData: any) => {
      const pageName = `page${pageData.pageNum}`;
      // 페이지 설정 정보 저장
      if (pageData.idx) {
        this.pageData[pageName].settings = {
          idx: pageData.idx,
          title: pageData.title,
          useStatus: pageData.useStatus !== undefined ? pageData.useStatus : true,
          pageNum: pageData.pageNum,
        };
      }
      // 설문 항목 데이터 저장
      if (pageData.inquiries && pageData.inquiries.length > 0) {
        this.pageData[pageName].inquiries = pageData.inquiries.map((inquiry: any) => ({
          uid: inquiry.idx.toString(),
          name: inquiry.question,
          type: this.mapApiTypeToLocalType(inquiry.type),
          useStatus: inquiry.useStatus !== undefined ? inquiry.useStatus : true,
          options: inquiry.options ? inquiry.options.split(',').filter((opt: string) => opt.trim()) : [],
          viewOrder: inquiry.viewOrder,
          pageNum: inquiry.pageNum,
        }));
      }
    });
    // 현재 페이지의 설정은 이미 pageData에 저장되어 있음
  }

  // API 타입을 로컬 타입으로 변환
  private mapApiTypeToLocalType(apiType: string): string {
    switch (apiType) {
      case '1':
        return 'select';
      case '2':
        return 'multiSelect';
      case '3':
        return 'score';
      default:
        return 'score';
    }
  }

  // 로컬 타입을 API 타입으로 변환
  private mapLocalTypeToApiType(localType: string): string {
    switch (localType) {
      case 'select':
        return '1';
      case 'multiSelect':
        return '2';
      case 'score':
        return '3';
      default:
        return '3';
    }
  }

  private handleAppendCategory(pageName?: string) {
    const targetPage = pageName || this.currentPage;
    const targetData = this.pageData[targetPage].inquiries;
    const newChild: any = {
      new: true,
      uid: null,
      name: '새 항목',
      type: 'score',
      useStatus: true,
      options: [],
    };
    targetData.push(newChild);
    this.currentCategory = newChild;
  }

  private handleRemoveCategoryDiv(data: any, pageName?: string) {
    const targetPage = pageName || this.currentPage;
    const targetData = this.pageData[targetPage].inquiries;

    this.$confirm(`'${data.name}' 항목을 삭제하시겠습니까?`, '항목 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      deleteMissionInquiry(data.uid).then(() => {
        // API 호출 성공 시 로컬 데이터에서 제거
        const index = targetData.findIndex((item: any) => item.uid === data.uid);
        if (index > -1) {
          targetData.splice(index, 1);
          this.currentCategory = {};
          this.$message.success('항목이 삭제되었습니다.');
        }
      }).catch((error) => {
        console.error('항목 삭제 실패:', error);
        this.$message.error('항목 삭제에 실패했습니다.');
      });
    });
  }

  private handleClickCategory(category: any) {
    this.currentCategory = { ...category };
    this.currentCategory = {
        ...category,
        type: category.type || 'text',
        useStatus: category.useStatus !== undefined ? category.useStatus : true,
        options: category.options || [],
      };
  }

  private loadCategoryDetails(category: any) {
    // 2depth API 호출 시뮬레이션
    this.loading = true;
    setTimeout(this.handleCategoryDetailsLoad.bind(this, category), 500);
  }

  private handleCategoryDetailsLoad(category: any) {
    // API에서 받아온 데이터로 설정
    this.currentCategory = {
      ...category,
      type: category.type || 'text',
      useStatus: category.useStatus !== undefined ? category.useStatus : true,
    };
    this.loading = false;
  }

  private loadOptionDetails(option: any) {
    // 3depth API 호출 시뮬레이션
    this.loading = true;
    setTimeout(this.handleOptionDetailsLoad.bind(this, option), 500);
  }

  private handleOptionDetailsLoad(option: any) {
    // API에서 받아온 데이터로 설정
    this.currentOption = {
      ...option,
      useStatus: option.useStatus !== undefined ? option.useStatus : true,
    };
    this.loading = false;
  }

  private updateCategoryName() {
    // 이름 변경 시 실제 트리 데이터도 업데이트
    this.updateTreeData();
  }

  private updateTreeData() {
    // 현재 페이지의 트리 데이터에서 해당 항목을 찾아 업데이트
    const currentData = this.pageData[this.currentPage].inquiries;
    const findAndUpdate = (items: any[], targetUid: string, newData: any): boolean => items.some((item) => {
      if (item.uid === targetUid) {
        item.name = newData.name;
        item.type = newData.type;
        item.useStatus = newData.useStatus;
        item.options = newData.options;
        return true;
      }
      if (item.children && findAndUpdate(item.children, targetUid, newData)) {
        return true;
      }
      return false;
    });

    findAndUpdate(currentData, this.currentCategory.uid, this.currentCategory);
  }

  private addOption() {
    if (this.newOptionValue.trim()) {
      if (!this.currentCategory.options) {
        this.$set(this.currentCategory, 'options', []);
      }
      this.currentCategory.options.push(this.newOptionValue.trim());
      this.newOptionValue = '';
      this.showNewOptionInput = false;
      this.updateTreeData();
    }
  }

  private removeOption(index: number) {
    if (this.currentCategory.options) {
      this.currentCategory.options.splice(index, 1);
      this.updateTreeData();
    }
  }

  private saveCategoryDetails() {
    this.$confirm('설문조사 항목을 저장하시겠습니까?', '항목 저장', {
      confirmButtonText: '저장',
      cancelButtonText: '취소',
      type: 'info',
    }).then(() => {
      this.loading = true;
      // 현재 카테고리 데이터를 API 형식으로 변환
      const apiData = {
        idx: this.currentCategory.uid ? parseInt(this.currentCategory.uid, 10) : null,
        pageNum: this.pageData[this.currentPage].settings.pageNum,
        question: this.currentCategory.name,
        type: this.mapLocalTypeToApiType(this.currentCategory.type),
        options: Array.isArray(this.currentCategory.options)
          ? this.currentCategory.options.join(',')
          : (this.currentCategory.options || ''),
        useStatus: this.currentCategory.useStatus,
      };
      // idx가 있으면 업데이트, 없으면 새로 추가
      const apiCall = apiData.idx ? updateInquiry(apiData) : addInquiry(apiData);
      apiCall.then((response) => {
        this.loading = false;
        this.$notify({
          title: '메세지',
          message: '항목이 저장되었습니다.',
          type: 'success',
          duration: 2000,
        });
        // 새로 추가된 경우 UID 업데이트
        if (!apiData.idx && response.data && response.data.idx) {
          this.currentCategory.uid = response.data.idx.toString();
        }
        this.updateTreeData();
        // 데이터 새로고침
        this.refreshInquiryData();
      }).catch((error) => {
        this.loading = false;
        console.error('항목 저장 실패:', error);
        this.$message.error('항목 저장에 실패했습니다.');
      });
    });
  }

  // 설문 데이터 새로고침
  private refreshInquiryData() {
    getInquiryList().then((response) => {
      this.loadApiData(response.data);
    }).catch((error) => {
      console.error('데이터 새로고침 실패:', error);
    });
  }

  private savePageSettings() {
    this.$confirm('페이지 설정을 저장하시겠습니까?', '페이지 설정 저장', {
      confirmButtonText: '저장',
      cancelButtonText: '취소',
      type: 'info',
    }).then(() => {
      this.loading = true;
      // 현재 페이지 설정 정보 준비
      const pageSettingData = {
        idx: this.pageData[this.currentPage].settings.idx,
        pageNum: this.pageData[this.currentPage].settings.pageNum,
        title: this.pageData[this.currentPage].settings.title,
        useStatus: this.pageData[this.currentPage].settings.useStatus,
      };
      // updatePage API 호출
      updatePage(pageSettingData).then(() => {
        this.loading = false;
        this.$notify({
          title: '메세지',
          message: '페이지 설정이 저장되었습니다.',
          type: 'success',
          duration: 2000,
        });

        // 로컬 데이터는 이미 v-model로 업데이트됨

        // 데이터 새로고침
        this.refreshInquiryData();
      }).catch((error) => {
        this.loading = false;
        console.error('페이지 설정 저장 실패:', error);
        this.$message.error('페이지 설정 저장에 실패했습니다.');
      });
    });
  }
}
</script>
<style lang="scss" scoped>
$main-color : #615f72;
$border-color : #dedede;

  // 탭 스타일 개선
  .el-tabs {overflow: scroll;
    .el-tabs__header {
      .el-tabs__nav-wrap {
        .el-tabs__nav-scroll {
          .el-tabs__nav {
            .el-tabs__item {
              padding: 15px 20px;
              border-right: 1px solid #e4e7ed;
              cursor: pointer;
              transition: all 0.2s ease;
              font-size: 14px;

              &:hover {
                background-color: rgba($main-color, 0.05);
                color: $main-color;
              }

              &.is-active {
                background-color: $main-color;
                color: #ffffff;
                border-right-color: $main-color;
              }
            }
          }
        }
      }
    }

    .el-tabs__content {
      padding: 0;
      height: calc(100% - 60px);
      overflow: hidden;
    }
  }

  // 탭 내용 래퍼 스타일
  .tab-content-wrapper {
    height: 100%;
    overflow-y: auto;
    padding: 20px;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;

      &:hover {
        background: #a8a8a8;
      }
    }
  }
  // 전체 레이아웃
  .food-add-wrap {
    background-color: #f8f9fa;
    min-height: 100vh;

    .food-add-title {
      background-color: #ffffff;
      border-bottom: 1px solid #e9ecef;
      padding: 20px;
      margin-bottom: 20px;

      .product-category__header {
        .tl {
          font-size: 18px;
          font-weight: 600;
          color: #333;
          margin: 0;
        }

        .el-button {
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 8px rgba($main-color, 0.3);
          }
        }
      }

      .setting__btn__box {
        button {
          padding: 10px 20px;
          border-radius: 4px;
          font-size: 14px;
          cursor: pointer;
          transition: all 0.2s ease;
          border: 1px solid #dcdfe6;

          &.cancel {
            background-color: #ffffff;
            color: #606266;

            &:hover {
              background-color: #f5f7fa;
              border-color: #c0c4cc;
            }
          }

          &.save {
            background-color: $main-color;
            color: #ffffff;
            border-color: $main-color;

            &:hover {
              background-color: lighten($main-color, 10%);
              transform: translateY(-1px);
              box-shadow: 0 4px 8px rgba($main-color, 0.3);
            }
          }
        }
      }
    }

    .food-add-section {
      padding: 0 20px 20px;

      .food-add-content1 {
        background-color: #ffffff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 20px;

        .content-title {
          padding: 20px 20px 15px;
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #333;
          border-bottom: 1px solid #f0f0f0;
        }
      }
    }
  }
  // 기존 스타일 유지 및 개선
  .el-tree {
    background: transparent;
    width: 100%;
  }

  .el-tree-node__content {
    padding: 0!important;
  }

  .el-tree-node__content, .sub-menu__item {
    border-radius: 3px;
    border: 1px solid $border-color;
    margin-bottom: 5px;
    box-sizing: border-box;
    height: 50px;
  }

  .el-tree-node__children {
    margin-left: 16px;
    .el-tree-node__children {
      margin-left: 16px;
    }
  }

  .content1-input {
    height: calc(100% - 60px);
    width: 100%;
    padding: 0;
  }
  .custom-tree {
    background: transparent;
    width: 100%;
    padding: 0;
  }
  .category-item {
    display: flex;
    width: 100%;
    height: 50px;
    justify-content: space-between;
    align-items: center;
    border-radius: 3px;
    border: 1px solid $border-color;
    margin-bottom: 5px;
    box-sizing: border-box;
    cursor: pointer;
    padding: 0 15px;
    background-color: #ffffff;
    transition: all 0.2s ease;

    &:hover {
      background-color: rgba($color: $main-color, $alpha: 0.05);
      border-color: lighten($main-color, 20%);
    }

    &:active {
      background-color: rgba($color: $main-color, $alpha: 0.1);
    }

    .category-node__name {
      font-size: 14px;
      color: #333;
      font-weight: 500;
      user-select: none;
    }

    .category-node__btns {
      height: 100%;
      display: flex;
      align-items: center;
    }

    .category-node__btn {
      height: 32px;
      border-radius: 4px;
      padding: 0 12px;
      margin: 0;
      border: 1px solid #dcdfe6;
      background-color: #ffffff;
      color: #606266;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background-color: #f56c6c;
        border-color: #f56c6c;
        color: #ffffff;
      }

      span {
        font-size: 12px;
      }
    }
  }
  .category-node {
    display: flex;
    width: 100%;
    height: 100%;
    justify-content: space-between;
    align-items: center;
    &__btns {
      height: 100%;
    }
    &__btn {
      height: 100%;
      border-radius: 0;
      padding: 0 0.9em;
      margin: 0px;
      border-left: 1px solid $border-color;
      span {
        font-size: 1rem;
      }
      &:hover {
        background-color: rgba($color: $main-color, $alpha: 0.05);
      }
    }
  }
  .category-detail {
    width: 49%;
    max-height: 390px;
    border: 1px solid #dfdfdf;
    border-radius: 3px;
    text-align: left;
    padding: 15px 0;
    background: #ffffff;
  }
  .option-tags {
    min-height: 32px;
    padding: 12px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background-color: #fafafa;
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    gap: 8px;

    .el-tag {
      margin: 0;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      }
    }

    .el-input {
      margin: 0;
      .el-input__inner {
        border-radius: 4px;
        border: 1px solid #dcdfe6;
        transition: border-color 0.2s ease;

        &:focus {
          border-color: $main-color;
          box-shadow: 0 0 0 2px rgba($main-color, 0.2);
        }
      }
    }

    .el-button {
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        transform: translateY(-1px);
      }
    }
  }

  .content2-input > div {
    flex: 0 1 100%;

    .shop-input-wr, .shop-radio-wr {
      margin-bottom: 20px;

      label {
        display: block;
        margin-bottom: 8px;
        font-weight: 500;
        color: #333;
        font-size: 14px;
      }

      .el-form-item {
        margin-bottom: 0;
      }

      .el-input__inner {
        border-radius: 4px;
        padding: 12px 15px;
        transition: all 0.2s ease;

        &:focus {
          border-color: $main-color;
          box-shadow: 0 0 0 2px rgba($main-color, 0.2);
        }
      }

      .el-select {
        .el-input__inner {
          cursor: pointer;
        }
      }

      .el-radio-group {
        .el-radio {
          margin-right: 20px;
          cursor: pointer;

          .el-radio__label {
            color: #606266;
            font-size: 14px;
          }

          &.is-checked .el-radio__label {
            color: $main-color;
          }
        }
      }
    }

    .shop-btn-wr {
      text-align: center;
      padding-top: 20px;
      border-top: 1px solid #f0f0f0;

      .el-button {
        padding: 12px 30px;
        font-size: 14px;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 8px rgba($main-color, 0.3);
        }
      }
    }
  }
</style>
