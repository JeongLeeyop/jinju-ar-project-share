<template>
	<div class="food-add-wrap product-category">
		<div class="food-add-title">
      <div class="product-category__header" style="display: flex;gap:30px;">
        <p class="tl">설문조사 항목관리</p>
        <el-button type="submit" style="color:#fff;background-color: #615f72;border-color: #615f72;" @click="handleAppendCategory(null)">추가</el-button>
      </div>
			<div class="setting__btn__box">
				<button @click="$router.push({ name: 'FoodList' })" class="cancel">취소</button>
				<button class="save" type="submit" @click="handleSave()">저장</button>
			</div>
		</div>
		<el-form ref="form" :model="form" :rules="rules" onsubmit="return false" v-loading="loading">
			<div class="food-add-section">
				<div class="food-add-content1 category-tree">
					<p class="content-title">기본정보</p>
					<div class="content1-input">
            <el-tree
              v-loading="loading"
              :data="categoryList"
              :indent="16"
              :expand-on-click-node="false"
              node-key="uid"
              draggable>
              <span
                class="category-node"
                slot-scope="{ node, data }"
                @click="handleClickCategory(data)"
              >
                <span class="category-node__name">{{ data.name }}</span>
                <span class="category-node__btns">
                  <el-button
                    v-if="node.level <= 2"
                    type="text"
                    size="mini"
                    class="category-node__btn"
                    @click="handleAppendCategory(data)">
                    추가
                  </el-button>
                  <el-button
                    type="text"
                    size="mini"
                    class="category-node__btn"
                    @click.stop="handleRemoveCategory(node, data)">
                    삭제
                  </el-button>
                </span>
              </span>
            </el-tree>
					</div>
				</div>
				<div class="food-add-content1">
					<p class="content-title">상세정보</p>
					<div class="content2-input">
						<!-- 1depth 선택 시 아무것도 표시하지 않음 -->
						<div v-if="currentCategory.uid && !currentCategory.uid.includes('-')">
							<p style="color: #999; text-align: center; padding: 20px;">
								대분류 항목입니다. 하위 항목을 선택해주세요.
							</p>
						</div>

						<!-- 2depth 선택 시 -->
						<div v-if="currentCategory.uid && currentCategory.uid.includes('-') && !isThirdDepth(currentCategory.uid)">
							<div class="shop-input-wr">
								<label for="categoryName">이름</label>
								<el-form-item>
									<el-input
										v-model="currentCategory.name"
										placeholder="항목 이름을 입력하세요"
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
										@change="updateCategoryType">
										<el-option label="텍스트 입력" value="text" />
										<el-option label="숫자 입력" value="number" />
										<el-option label="선택형" value="select" />
										<el-option label="다중선택" value="checkbox" />
										<el-option label="날짜" value="date" />
									</el-select>
								</el-form-item>
							</div>
							<div class="shop-radio-wr">
								<label>사용여부</label>
								<el-form-item>
									<el-radio-group v-model="currentCategory.useStatus" @change="updateCategoryStatus">
										<el-radio :label="true">사용</el-radio>
										<el-radio :label="false">미사용</el-radio>
									</el-radio-group>
								</el-form-item>
							</div>
						</div>

						<!-- 3depth 선택 시 -->
						<div v-if="currentCategory.uid && isThirdDepth(currentCategory.uid)">
							<div class="shop-input-wr">
								<label for="categoryName">이름</label>
								<el-form-item>
									<el-input
										v-model="currentCategory.name"
										placeholder="옵션 이름을 입력하세요"
										@change="updateCategoryName" />
								</el-form-item>
							</div>
						</div>

						<!-- 아무것도 선택되지 않은 경우 -->
						<div v-if="!currentCategory.uid">
							<p style="color: #999; text-align: center; padding: 20px;">
								항목을 선택해주세요.
							</p>
						</div>
					</div>
				</div>
			</div>
		</el-form>
	</div>
</template>

<script lang="ts">
import { addFood } from '@/api/food';
import { Vue, Component } from 'vue-property-decorator';

@Component({
})
export default class extends Vue {
  mounted() {
    this.getNormalProductCategoryList();
  }

  private loading = true;

	private form = {
    weightOnce: 0,
    weightType: 'g',
    totalG: 0,
  }

  private rules = {
    name: [
      { required: true, message: '음식명을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
    weightOnce: [
      { required: true, message: '중량을 입력해주세요.', trigger: ['blur', 'change'] },
    ],
  }

  private handleSave() {
    this.$confirm('일반상점 카테고리를 저장하시겠습니까?', '일반상점 카테고리 저장', {
      confirmButtonText: '저장',
      cancelButtonText: '취소',
      type: 'info',
    }).then(() => {
      this.loading = true;
      addFood(this.categoryList).then(() => {
        this.loading = false;
        this.$notify({
          title: '메세지',
          message: '일반상점 카테고리가 수정되었습니다.',
          type: 'success',
          duration: 2000,
        });
      }).catch(() => {
        this.loading = false;
      });
    });
  }

  private categoryList: any = [];

  private currentCategory: any = {};

  private handleAppendCategory(parent: any) {
    const newChild: any = {
      uid: this.generateNewUid(parent),
      name: '새 항목',
    };
    if (parent) {
      if (!parent.children) this.$set(parent, 'children', []);
      parent.children.push(newChild);
    } else {
      this.categoryList.push(newChild);
    }
    this.currentCategory = newChild;
  }

  private generateNewUid(parent: any) {
    if (!parent) {
      // 1depth - 최상위 레벨
      const maxId = Math.max(...this.categoryList.map((item: any) => parseInt(item.uid, 10) || 0));
      return String(maxId + 1);
    }
    if (!parent.uid.includes('-')) {
      // 2depth - parent가 1depth인 경우
      const existingChildren = parent.children || [];
      const maxId = existingChildren.length > 0
        ? Math.max(...existingChildren.map((item: any) => {
            const parts = item.uid.split('-');
            return parseInt(parts[1], 10) || 0;
          }))
        : 0;
      return `${parent.uid}-${maxId + 1}`;
    }
    // 3depth - parent가 2depth인 경우
    const existingChildren = parent.children || [];
    const maxId = existingChildren.length > 0
      ? Math.max(...existingChildren.map((item: any) => {
          const parts = item.uid.split('-');
          return parseInt(parts[2], 10) || 0;
        }))
      : 0;
    return `${parent.uid}-${maxId + 1}`;
  }

  private handleRemoveCategory(node: any, data: any) {
    this.$confirm(`'${data.name}' 항목을 삭제하시겠습니까?`, '항목 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    }).then(() => {
      const { parent } = node;
      const children = parent.data.children || parent.data;
      const index = children.findIndex((d: any) => d.uid === data.uid);
      if (index > -1) {
        children.splice(index, 1);
        this.currentCategory = {};
        this.$message.success('항목이 삭제되었습니다.');
      }
    });
  }

  private handleClickCategory(category: any) {
    this.currentCategory = { ...category };
    // 2depth 선택 시 API 호출 시뮬레이션
    if (category.uid && category.uid.includes('-') && !this.isThirdDepth(category.uid)) {
      this.loadCategoryDetails(category);
    }
    // 3depth 선택 시 API 호출 시뮬레이션
    if (category.uid && this.isThirdDepth(category.uid)) {
      this.loadOptionDetails(category);
    }
  }

  private isThirdDepth(uid: string): boolean {
    return uid.split('-').length === 3;
  }

  private loadCategoryDetails(category: any) {
    // 2depth API 호출 시뮬레이션
    this.loading = true;
    setTimeout(() => {
      // API에서 받아온 데이터로 설정
      this.currentCategory = {
        ...category,
        type: category.type || 'text',
        useStatus: category.useStatus !== undefined ? category.useStatus : true,
      };
      this.loading = false;
    }, 500);
  }

  private loadOptionDetails(category: any) {
    // 3depth API 호출 시뮬레이션
    this.loading = true;
    setTimeout(() => {
      // API에서 받아온 데이터로 설정
      this.currentCategory = {
        ...category,
      };
      this.loading = false;
    }, 300);
  }

  private updateCategoryName() {
    // 이름 변경 시 실제 트리 데이터도 업데이트
    this.updateTreeData();
  }

  private updateCategoryType() {
    // 타입 변경 시 처리
    console.log('타입 변경:', this.currentCategory.type);
  }

  private updateCategoryStatus() {
    // 사용여부 변경 시 처리
    console.log('사용여부 변경:', this.currentCategory.useStatus);
  }

  private updateTreeData() {
    // 트리 데이터에서 해당 항목을 찾아 업데이트
    const findAndUpdate = (items: any[], targetUid: string, newData: any): boolean => items.some((item) => {
      if (item.uid === targetUid) {
        item.name = newData.name;
        item.type = newData.type;
        item.useStatus = newData.useStatus;
        return true;
      }
      if (item.children && findAndUpdate(item.children, targetUid, newData)) {
        return true;
      }
      return false;
    });

    findAndUpdate(this.categoryList, this.currentCategory.uid, this.currentCategory);
  }

  private getNormalProductCategoryList() {
    // 설문조사 항목 샘플 데이터
    this.categoryList = [
      {
        uid: '1',
        name: '개인정보',
        children: [
          {
            uid: '1-1',
            name: '나이',
          },
          {
            uid: '1-2',
            name: '성별',
          },
          {
            uid: '1-3',
            name: '직업',
          },
          {
            uid: '1-4',
            name: '거주지역',
          },
          {
            uid: '1-5',
            name: '혼인상태',
          },
        ],
      },
      {
        uid: '2',
        name: '건강정보',
        children: [
          {
            uid: '2-1',
            name: '현재 체중',
            type: 'select',
            useStatus: true,
            children: [
              {
                uid: '2-1-1',
                name: '정확한 체중',
              },
              {
                uid: '2-1-2',
                name: '대략적인 체중',
              },
              {
                uid: '2-1-3',
                name: '체중 측정 방법',
              },
            ],
          },
          {
            uid: '2-2',
            name: '목표 체중',
            type: 'number',
            useStatus: true,
          },
          {
            uid: '2-3',
            name: '키',
            type: 'number',
            useStatus: true,
          },
          {
            uid: '2-4',
            name: '운동 경험',
            type: 'checkbox',
            useStatus: true,
            children: [
              {
                uid: '2-4-1',
                name: '헬스장 경험',
              },
              {
                uid: '2-4-2',
                name: '홈트레이닝 경험',
              },
              {
                uid: '2-4-3',
                name: '야외 운동 경험',
              },
              {
                uid: '2-4-4',
                name: '스포츠 경험',
              },
            ],
          },
          {
            uid: '2-5',
            name: '알레르기 유무',
            type: 'select',
            useStatus: true,
          },
          {
            uid: '2-6',
            name: '복용 약물',
            type: 'text',
            useStatus: false,
          },
          {
            uid: '2-7',
            name: '기존 질병',
            type: 'text',
            useStatus: true,
          },
        ],
      },
      {
        uid: '3',
        name: '식습관',
        children: [
          {
            uid: '3-1',
            name: '선호 음식',
            children: [
              { uid: '3-1-1', name: '한식' },
              { uid: '3-1-2', name: '중식' },
              { uid: '3-1-3', name: '일식' },
              { uid: '3-1-4', name: '양식' },
              { uid: '3-1-5', name: '기타' },
            ],
          },
          {
            uid: '3-2',
            name: '기피 음식',
            children: [
              { uid: '3-2-1', name: '매운 음식' },
              { uid: '3-2-2', name: '단 음식' },
              { uid: '3-2-3', name: '기름진 음식' },
              { uid: '3-2-4', name: '특정 재료' },
            ],
          },
          { uid: '3-3', name: '식사 횟수' },
          { uid: '3-4', name: '간식 습관' },
          { uid: '3-5', name: '외식 빈도' },
          { uid: '3-6', name: '음주 습관' },
          { uid: '3-7', name: '흡연 여부' },
        ],
      },
      {
        uid: '4',
        name: '운동습관',
        children: [
          { uid: '4-1', name: '현재 운동 빈도' },
          { uid: '4-2', name: '선호 운동 종류' },
          { uid: '4-3', name: '운동 시간대' },
          { uid: '4-4', name: '운동 장소' },
          { uid: '4-5', name: '운동 강도' },
        ],
      },
      {
        uid: '5',
        name: '생활패턴',
        children: [
          { uid: '5-1', name: '수면 시간' },
          { uid: '5-2', name: '기상 시간' },
          { uid: '5-3', name: '취침 시간' },
          { uid: '5-4', name: '스트레스 수준' },
          { uid: '5-5', name: '워라벨' },
        ],
      },
      {
        uid: '6',
        name: '목표설정',
        children: [
          { uid: '6-1', name: '감량 목표' },
          { uid: '6-2', name: '목표 달성 기간' },
          { uid: '6-3', name: '우선순위' },
          { uid: '6-4', name: '동기부여 요소' },
        ],
      },
    ];
    this.loading = false;
    // getNormalProductCategoryList().then((res) => {
    //   this.categoryList = res.data;
    //   this.loading = false;
    // });
  }
}
</script>
<style lang="scss" scoped>
$main-color : #615f72;
$border-color : #dedede;
  .el-tree {
    background: transparent;width: 100%;
  }
  .el-tree-node__content { padding: 0!important; }
  .el-tree-node__content, .sub-menu__item {
    border-radius: 3px; border: 1px solid $border-color; margin-bottom: 5px;
    box-sizing: border-box; height: 50px;
  }
  .el-tree-node__children {
    margin-left: 16px;
    .el-tree-node__children { margin-left: 16px; }
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
</style>
