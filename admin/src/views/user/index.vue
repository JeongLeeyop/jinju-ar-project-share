<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">고객관리</p>
        <p class="usernumber">총 {{ totalElements | parseKrw }}명</p>
      </div>

      <div class="user__tab small">
        <div class="user__subtab">
          <el-select v-model="listQuery.searchType" slot="prepend" placeholder="이름">
            <el-option label="이름" value="actualName"></el-option>
            <el-option label="연락처" value="concatNumber"></el-option>
            <el-option label="생년월일" value="birth"></el-option>
          </el-select>
          <el-input placeholder="" v-model="listQuery.searchValue" class="search" @keyup.enter.native="handleSearch()" />
          <el-button @click="handleSearch()"><img src="~@/assets/images/search.png" alt=""></el-button>
        </div>
        <!--
        <router-link :to="{ name: 'UserAdd' }">추가 +</router-link>
        -->
      </div>
    </div>

    <div v-loading="loading" class="user-content">
      <el-table
        border
        style="width: 100%"
        :header-cell-style="{ background: '#9097a4', color: '#fff', padding: '5px 0' }"
        :data="userList"
      >
        <el-table-column label="번호" width="70">
          <template slot-scope="scope">
            {{ getNumber(scope.$index) }}
          </template>
        </el-table-column>
        <el-table-column prop="actualName" label="이름">
          <template slot-scope="scope">
            {{ scope.row.actualName }}
            <el-tag v-if="scope.row.provider === 'KAKAO'" type="warning" effect="dark" size="mini">카카오</el-tag>
            <el-tag v-else-if="scope.row.provider === 'NAVER'" type="success" effect="dark" size="mini">네이버</el-tag>
            <el-tag v-else-if="scope.row.provider === 'APPLE'" type="info" effect="dark" size="mini">애플</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="concatNumber" label="연락처" />
        <el-table-column prop="birth" label="생년월일" />
        <el-table-column label="성별" width="100">
          <template slot-scope="scope">
            {{ scope.row.gender === 0 ? '남자' : '여자' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="이메일" />
        <el-table-column label="가입일" width="200">
          <template slot-scope="scope">
            {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column label="설문조사">
          <template slot-scope="scope">
            <a style="cursor: pointer;" class="detail-button" @click="handleViewSurvey(scope.row.uid)">보기</a>
          </template>
        </el-table-column>
        <el-table-column label="응답여부" width="80">
          <template slot-scope="scope">
            <i v-if="scope.row.missionInquiryStatus" class="el-icon-check" style="color: #67c23a; font-size: 16px;"></i>
            <i v-else class="el-icon-close" style="color: #f56c6c; font-size: 16px;"></i>
          </template>
        </el-table-column>
        <el-table-column label="관리">
          <template slot-scope="scope">
            <!-- <el-button size="mini" @click="handleEdit(scope.row)" type="view">보기</el-button> -->
						<router-link class="detail-button" :to="{ name: 'UserUpdate', params: { uid: scope.row.uid } }">수정</router-link>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)" icon="delete-button">탈퇴</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        :total="totalElements"
        :page.sync="listQuery.page"
        :limit.sync="listQuery.size"
        @pagination="getUserList()"
      />
    </div>

    <!-- 설문조사 보기 다이얼로그 -->
    <el-dialog
      title="설문조사 내용"
      :visible.sync="surveyDialogVisible"
      width="60%"
      :before-close="handleCloseSurveyDialog"
      class="survey-dialog"
      center
    >
      <div v-if="selectedUser" class="survey-container">
        <div class="user-info-section">
          <div class="user-avatar">
            <i class="el-icon-user"></i>
          </div>
          <div class="user-details">
            <h3 class="user-name">{{ selectedUser.actualName }}님의 설문조사</h3>
            <div class="user-meta">
              <div class="meta-item">
                <i class="el-icon-phone"></i>
                <span>{{ selectedUser.concatNumber }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-message"></i>
                <span>{{ selectedUser.email }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-date"></i>
                <span>{{ selectedUser.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}</span>
              </div>
            </div>
          </div>
        </div>
        <el-divider></el-divider>

        <!-- 설문조사 내용 -->
        <div class="survey-content">
          <div v-if="surveyQuestions && surveyQuestions.length > 0" class="survey-results">
            <h4 class="section-title">
              <i class="el-icon-document"></i>
              설문조사 결과 (총 {{ surveyQuestions.length }}개)
            </h4>
            <div class="survey-data">
              <!-- 질문-답변 매핑하여 표시 -->
              <div v-for="question in surveyQuestions" :key="question.idx" class="survey-item">
                <div class="question-header">
                  <span class="question-number">Q{{ question.idx }}</span>
                  <span class="page-info" v-if="question.pageNum">Page {{ question.pageNum }}</span>
                </div>
                <div class="question-content">
                  <div class="question">{{ question.question || question.title }}</div>
                  <div class="answer-section">
                    <div v-if="getAnswerForQuestion(question.idx)" class="answer" :class="{ 'likert-answer': isLikertAnswer(getRawAnswer(question.idx)) }">
                      <i class="el-icon-chat-line-round"></i>
                      <span>{{ getAnswerForQuestion(question.idx) }}</span>
                      <!-- <span v-if="isLikertAnswer(getRawAnswer(question.idx))" class="likert-badge">리커트 척도</span> -->
                    </div>
                    <div v-else class="answer empty">
                      <i class="el-icon-warning-outline"></i>
                      답변이 없습니다
                    </div>
                  </div>
                </div>
                <div class="question-meta" v-if="question.type">
                  <span class="question-type" :class="getQuestionTypeClass(question.type)">
                    {{ getQuestionTypeLabel(question.type) }}
                  </span>
                  <span class="page-title" v-if="question.pageTitle">{{ question.pageTitle }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="no-survey-data">
            <div class="empty-state">
              <i class="el-icon-document-delete"></i>
              <h4>설문조사 결과가 없습니다</h4>
              <p class="empty-description">아직 작성된 설문조사가 없거나 데이터를 불러올 수 없습니다.</p>
            </div>
          </div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="surveyDialogVisible = false" size="medium">닫기</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getUserList, deleteUser } from '@/api/user';
import { getUserInquiryDetail, getInquiryList } from '@/api/missionInquiry';

@Component({
  components: {
    Pagination,
  },
})
export default class extends Vue {
  mounted() {
    this.getUserList();
    this.getInquiryList();
  }

  private loading = true;

  private totalElements = 0;

  private listQuery = {
    page: 0,
    size: 10,
    searchType: 'actualName',
    searchValue: '',
  }

  private userList = [];

  private surveyDialogVisible = false;

  private selectedUser: any = null;

  private surveyData: any = null;

  private surveyQuestions: any[] = [];

  private userAnswers: any[] = [];

  private handleSearch() {
    this.listQuery.page = 0;
    this.getUserList();
  }

  private getInquiryList() {
    getInquiryList().then((res: any) => {
      // 중첩된 구조에서 모든 질문을 추출하여 flat한 배열로 만듦
      const allQuestions: any[] = [];
      if (res.data && res.data.length > 0) {
        res.data.forEach((page: any) => {
          if (page.inquiries && page.inquiries.length > 0) {
            page.inquiries.forEach((inquiry: any) => {
              // 페이지 제목과 페이지 번호를 포함하여 질문 객체 저장
              allQuestions.push({
                ...inquiry,
                pageTitle: page.title, // 페이지 제목 추가
              });
            });
          }
        });
      }
      this.surveyQuestions = allQuestions;
    }).catch(() => {
      this.$message.error('설문조사 질문 정보를 불러오는 데 실패했습니다.');
    });
  }

  private getUserList() {
    this.loading = true;
    getUserList(this.listQuery).then((res) => {
      this.loading = false;
      this.userList = res.data.content;
      this.totalElements = res.data.totalElements;
    });
  }

  private getNumber(index: any) {
    let totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    if (totalSize < 0) totalSize = 0;
    return this.totalElements - totalSize - index;
  }

  private handleDelete(user: any) {
    this.$confirm(`정말로 사용자(${user.actualName})를 탈퇴 처리하시겠습니까?`, { type: 'error' }).then(() => {
      deleteUser(user.uid).then(() => {
        this.$message.success('성공적으로 사용자를 삭제했습니다.');
        this.getUserList();
      });
    });
  }

  private handleViewSurvey(userUid: any) {
    // 선택된 사용자 정보 설정
    this.selectedUser = this.userList.find((user: any) => user.uid === userUid);
    this.surveyDialogVisible = true;

    // 사용자의 설문조사 답변 데이터 조회
    getUserInquiryDetail(userUid).then((res) => {
      this.userAnswers = res.data || [];
    }).catch(() => {
      this.$message.warning('설문조사 답변을 조회하는데 실패했습니다.');
      this.userAnswers = [];
    });
  }

  private handleCloseSurveyDialog() {
    this.surveyDialogVisible = false;
    this.selectedUser = null;
    this.surveyData = null;
    this.userAnswers = [];
  }

  // 질문 ID에 해당하는 답변을 찾는 메서드
  private getAnswerForQuestion(inquiryIdx: number): string | null {
    const userAnswer = this.userAnswers.find((answer: any) => answer.inquiryIdx === inquiryIdx);
    if (!userAnswer || !userAnswer.answer) return null;

    // 답변이 '1'~'5'이면 리커트 척도로 변환
    return this.convertLikertScale(userAnswer.answer);
  }

  // 리커트 척도 변환 메서드
  private convertLikertScale(answer: string): string {
    // 타입 3인 질문에 대한 숫자 답변은 리커트 척도로 변환
    const likertScale: { [key: string]: string } = {
      1: '전혀 아니다',
      2: '아니다',
      3: '보통',
      4: '그렇다',
      5: '매우 그렇다',
    };

    // 콤마로 구분된 여러 답변이 있는 경우 (다중 선택)
    if (answer.includes(',')) {
      return answer; // 다중 선택 답변은 그대로 반환
    }

    // 답변이 '1'~'5' 중 하나이면 치환, 아니면 원본 답변 반환
    return likertScale[answer] || answer;
  }

  // 원본 답변을 가져오는 메서드 (치환 전)
  private getRawAnswer(inquiryIdx: number): string | null {
    const userAnswer = this.userAnswers.find((answer: any) => answer.inquiryIdx === inquiryIdx);
    return userAnswer?.answer || null;
  }

  // 리커트 척도 답변인지 확인하는 메서드
  private isLikertAnswer(answer: string | null): boolean {
    if (!answer) return false;
    // 답변이 콤마를 포함하지 않고 1~5 중 하나일 때만 리커트 척도로 간주
    return !answer.includes(',') && ['1', '2', '3', '4', '5'].includes(answer);
  }

  // 질문 타입에 따른 클래스 반환
  private getQuestionTypeClass(type: string): string {
    const typeClasses: { [key: string]: string } = {
      1: 'type-single',
      2: 'type-multiple',
      3: 'type-likert',
    };
    return typeClasses[type] || 'type-default';
  }

  // 질문 타입에 따른 라벨 반환
  private getQuestionTypeLabel(type: string): string {
    const typeLabels: { [key: string]: string } = {
      1: '단일 선택',
      2: '다중 선택',
      3: '점수',
    };
    return typeLabels[type] || `타입 ${type}`;
  }
}
</script>

<style scoped>
/* 설문조사 다이얼로그 스타일 */
.survey-dialog {
  border-radius: 8px;
}

.survey-container {
  padding: 10px 0;
}

.user-info-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-avatar i {
  font-size: 24px;
  color: white;
}

.user-details {
  flex: 1;
}

.user-name {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

.user-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 14px;
}

.meta-item i {
  margin-right: 6px;
  color: #909399;
}

.section-title {
  display: flex;
  align-items: center;
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.section-title i {
  margin-right: 8px;
  color: #409eff;
}

.survey-results {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 16px;
}

.survey-data {
  display: grid;
  gap: 6px;
}

.survey-item {
  background: white;
  border-radius: 8px;
  padding: 12px 16px;
  border-left: 3px solid #409eff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 8px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid #f0f0f0;
}

.question-number {
  background: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.page-info {
  background: #f5f7fa;
  color: #909399;
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
}

.question-content {
  margin-bottom: 8px;
}

.question {
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  font-size: 13px;
  line-height: 1.4;
  background: #f8f9fb;
  padding: 8px 12px;
  border-radius: 6px;
  border-left: 2px solid #e6a23c;
}

.answer-section {
  margin-top: 6px;
}

.answer {
  color: #606266;
  line-height: 1.4;
  font-size: 12px;
  padding: 8px 12px;
  background: #f0f9ff;
  border-radius: 6px;
  border: 1px solid #e1f5fe;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}

.answer.likert-answer {
  background: linear-gradient(135deg, #e8f5e8, #f0f8f0);
  border: 1px solid #d4edda;
  font-weight: 600;
}

.answer.likert-answer span:first-of-type {
  color: #155724;
}

.likert-badge {
  background: #28a745;
  color: white;
  padding: 1px 4px;
  border-radius: 8px;
  font-size: 9px;
  font-weight: 500;
  margin-left: auto;
}

.answer i {
  color: #409eff;
  margin-top: 1px;
  font-size: 14px;
}

.answer.likert-answer i {
  color: #28a745;
}

.answer.empty {
  background: #fef7f0;
  border: 1px solid #fde2e2;
  color: #909399;
  font-style: italic;
}

.answer.empty i {
  color: #f56c6c;
}

.question-meta {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
  padding-top: 4px;
  border-top: 1px solid #f0f0f0;
}

.question-type {
  padding: 2px 6px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 500;
  margin-right: 8px;
}

.question-type.type-single {
  background: #f0f9eb;
  color: #67c23a;
}

.question-type.type-multiple {
  background: #ecf5ff;
  color: #409eff;
}

.question-type.type-likert {
  background: #fdf6ec;
  color: #e6a23c;
}

.question-type.type-default {
  background: #f4f4f5;
  color: #909399;
}

.page-title {
  color: #909399;
  font-style: italic;
  font-size: 12px;
  margin-left: 8px;
}

.no-survey-data {
  text-align: center;
  padding: 40px 20px;
}

.empty-state {
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
  display: block;
}

.empty-state h4 {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 16px;
  font-weight: 500;
}

.empty-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
  line-height: 1.5;
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}

/* 모바일 반응형 */
@media (max-width: 768px) {
  .survey-dialog {
    width: 90% !important;
  }
  .user-info-section {
    flex-direction: column;
    text-align: center;
  }
  .user-avatar {
    margin-right: 0;
    margin-bottom: 15px;
  }
  .user-meta {
    justify-content: center;
    gap: 15px;
  }
  .meta-item {
    font-size: 13px;
  }
}
</style>
