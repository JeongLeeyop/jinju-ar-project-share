<template>
  <div class="useradd-wrap mission" style="overflow: scroll;">
    <div class="useradd-title">
      <div>
        <p class="tl">미션 {{ $route.params.uid ? '수정' : '추가' }}</p>
      </div>

      <div class="setting__btn__box">
        <button @click="handleCancel()" class="cancel">취소</button>
        <button class="save" @click="handleSave()">저장</button>
      </div>
    </div>
    <el-form v-loading="loading" ref="form" :model="form" :rules="rules" onsubmit="return false">
      <div class="useradd-section">
        <div class="useradd-content1">
          <p class="content-title">미션 관리</p>
          <div class="content1-input">
            <div class="shop-input-wr">
                <label for="shopOpenDate">템플릿 사용</label>
                <el-form-item>
                  <el-col>
                    <el-select style="width: 100%" v-model="form.templateUid" @change="handleTemplateChange">
                      <el-option label="사용안함" value="" />
                      <el-option v-for="template in templateList" :key="template.uid" :label="template.title" :value="template.uid" />
                    </el-select>
                  </el-col>
                </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="couponName">미션 사진</label>
              <el-form-item>
                <el-upload
                  style="max-width: 800px;"
                  :action="`${apiUrl}/mission/upload`"
                  :headers="requestHeaders"
                  :on-exceed="handleExceed"
                  :on-success="handleSuccessUpload"
                  :limit="5"
                  :file-list="form.mainImageList"
                  :on-remove="handleRemove"
                  list-type="picture"
                  :before-upload="(file) => handleBeforeUpload(file, true)"
                  :disabled="isTemplateSelected">
                  <el-button
                    size="small"
                    type="info"
                    icon="el-icon-upload2"
                    :disabled="isTemplateSelected">
                    파일 선택
                  </el-button>
                </el-upload>
              </el-form-item>
              <div slot="tip" class="el-upload__tip">
                <span v-if="isTemplateSelected" style="color: #f56c6c;">템플릿 선택 시 이미지 수정이 불가합니다.</span>
                <span v-else>최대 5개 까지 업로드 가능합니다.</span>
              </div>
            </div>
            <div class="shop-input-wr">
              <label for="couponName">미션명</label>
              <el-form-item prop="title">
                <el-input placeholder="" type="text" id="title" v-model="form.title" :disabled="isTemplateSelected"/>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
                <label for="shopOpenDate">카테고리</label>
                <el-form-item>
                  <el-col>
                    <el-select style="width: 100%" v-model="form.categoryUid" :disabled="isTemplateSelected">
                      <el-option v-for="category in categoryList" :key="category.uid" :label="category.name" :value="category.uid" />
                    </el-select>
                  </el-col>
                </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">내용</label>
              <el-form-item prop="content">
                <el-input type="textarea" :rows="5" size="medium" v-model="form.content" :disabled="isTemplateSelected"></el-input>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">인증방식</label>
              <el-form-item prop="authMethodInfo">
                <el-input type="textarea" :rows="5" size="medium" v-model="form.authMethodInfo" :disabled="isTemplateSelected"></el-input>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">인증방식 예시</label>
              <el-form-item>
                <el-upload
                  style="max-width: 800px;"
                  :action="`${apiUrl}/mission/upload`"
                  :headers="requestHeaders"
                  :on-exceed="handleExceed2"
                  :on-success="handleSuccessUpload2"
                  :limit="3"
                  :file-list="form.sampleImageList"
                  :on-remove="handleRemove2"
                  list-type="picture"
                  :before-upload="(file) => handleBeforeUpload(file, true)"
                  :disabled="isTemplateSelected">
                  <el-button
                    size="small"
                    type="info"
                    icon="el-icon-upload2"
                    :disabled="isTemplateSelected">
                    파일 선택
                  </el-button>
                </el-upload>
              </el-form-item>
              <div slot="tip" class="el-upload__tip">
                <span v-if="isTemplateSelected" style="color: #f56c6c;">템플릿 선택 시 이미지 수정이 불가합니다.</span>
                <span v-else>최대 3개 까지 업로드 가능합니다.</span>
              </div>
            </div>
          </div>
        </div>
        <div class="useradd-content1">
          <p class="content-title">미션 관리</p>
          <div class="content1-input">
            <div class="shop-input-wr">
              <label for="">유저선택</label>
              <el-form-item prop="userUid">
                <div style="display: flex; align-items: center; gap: 10px;">
                  <el-input
                    v-model="selectedUserName"
                    placeholder="유저를 선택해주세요"
                    readonly
                    style="flex: 1;"
                  />
                  <el-button
                    v-if="!$route.params.uid"
                    type="primary"
                    @click="openUserDialog"
                  >
                    선택
                  </el-button>
                </div>
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">미션 완료 보상</label>
              <el-form-item prop="dailyReward">
                <el-input-number v-model="form.dailyReward" id="dailyReward" :min="0" /> 포인트
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">감량효과</label>
              <el-form-item prop="achieveEffect">
                <el-input-number v-model="form.achieveEffect" id="achieveEffect" :min="0" :step="0.1" :precision="1" /> kg 감량 효과
              </el-form-item>
            </div>
            <div class="shop-date-wr">
              <label for="downloadDateRange">미션 기간</label>
              <el-form-item prop="missionRange">
                <el-date-picker v-model="form.missionRange" type="daterange" align="right" start-placeholder="시작일"
                  end-placeholder="종료일" value-format="yyyy-MM-dd" @change="handleChangeMissionRange" />
              </el-form-item>
            </div>
            <div class="shop-input-wr">
              <label for="">성공 기준 일수</label>
              <el-form-item prop="totalDay">
                <el-input-number v-model="form.totalDay" id="totalDay" :min="1" /> 일
              </el-form-item>
            </div>

            <div class="shop-date-wr">
              <label for="alarmTime">알람 사용</label>
              <el-switch v-model="form.alarmStatus"></el-switch>
            </div>
            <div class="shop-date-wr" v-if="form.alarmStatus">
              <label for="alarmTime">알람 시간</label>
              <el-form-item prop="alarmTime">
                <el-time-picker v-model="form.alarmTime" placeholder="알람 시간 선택"
                  value-format="HH:mm" format="HH:mm" />
              </el-form-item>
            </div>
            <div class="shop-date-wr" v-if="form.alarmStatus">
                <label>알람 요일</label>
                <div style="display: flex;">
                  <el-button size="small" @click="selectAllAlarms">전체 선택</el-button>
                  <el-button size="small" @click="deselectAllAlarms">전체 해제</el-button>
                </div>
                <div class="shop-checkbox-wr" style="margin:10px 0 0;padding: 10px 20px; border: 1px solid #efefef;">
                    <el-form-item>
                        <el-checkbox label="월" v-model="form.mon"></el-checkbox>
                        <el-checkbox label="화" v-model="form.tue"></el-checkbox>
                        <el-checkbox label="수" v-model="form.wed"></el-checkbox>
                        <el-checkbox label="목" v-model="form.thu"></el-checkbox>
                        <el-checkbox label="금" v-model="form.fri"></el-checkbox>
                        <el-checkbox label="토" v-model="form.sat"></el-checkbox>
                        <el-checkbox label="일" v-model="form.sun"></el-checkbox>
                    </el-form-item>
                </div>
            </div>
            <div class="shop-input-wr" v-if="form.alarmStatus">
              <label for="couponName">알람명</label>
              <el-form-item prop="alarmTitle">
                <el-input placeholder="" type="text" id="title" v-model="form.alarmTitle" />
              </el-form-item>
            </div>
            <div class="shop-input-wr" v-if="form.alarmStatus">
              <label for="">알람내용</label>
              <el-form-item prop="alarmMessage">
                <el-input type="textarea" :rows="5" size="medium" v-model="form.alarmMessage"></el-input>
              </el-form-item>
            </div>
            <div class="shop-radio-wr">
              <p class="content2-txt">사용여부</p>
              <el-radio-group v-model="form.useStatus">
                <el-radio :label="true">Y</el-radio>
                <el-radio :label="false">N</el-radio>
              </el-radio-group>
            </div>
          </div>
        </div>
      </div>
    </el-form>

    <!-- 유저 선택 다이얼로그 -->
    <el-dialog
      title="유저 선택"
      :visible.sync="userDialogVisible"
      width="50%"
      :before-close="handleCloseUserDialog"
      class="user-select-dialog"
    >
      <div class="search-section">
        <el-input
          v-model="userSearchQuery"
          placeholder="유저명으로 검색"
          @keyup.enter.native="searchUsers"
          class="search-input"
        >
          <el-button slot="append" @click="searchUsers" icon="el-icon-search"></el-button>
        </el-input>
      </div>

      <el-table
        :data="userList"
        style="width: 100%"
        v-loading="userLoading"
        @row-click="selectUser"
        :height="300"
        class="user-table"
      >
        <!-- <el-table-column prop="actualName" label="유저명" width="250" /> -->
        <el-table-column prop="actualName" label="이름">
          <template slot-scope="scope">
            {{ scope.row.actualName }}
            <el-tag v-if="scope.row.provider === 'KAKAO'" type="warning" effect="dark" size="mini">카카오</el-tag>
            <el-tag v-else-if="scope.row.provider === 'NAVER'" type="success" effect="dark" size="mini">네이버</el-tag>
            <el-tag v-else-if="scope.row.provider === 'APPLE'" type="info" effect="dark" size="mini">애플</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="concatNumber" label="전화번호" width="250" />
        <el-table-column prop="email" label="이메일"/>
      </el-table>

      <div class="pagination-section">
        <el-pagination
          @current-change="handleUserPageChange"
          :current-page="userQuery.page"
          :page-size="userQuery.size"
          layout="prev, pager, next"
          :total="userTotalElements"
          small
        />
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button @click="userDialogVisible = false">취소</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Form } from 'element-ui';
import { Component, Vue } from 'vue-property-decorator';
import { getToken } from '@/utils/cookies';
import {
  getMissionDetail,
  addMission,
  updateMission,
  getMissionCategory,
  getMissionTemplateList,
} from '@/api/mission';
import { getUserList } from '@/api/user';

@Component({
  components: {
  },
})
export default class extends Vue {
  async mounted() {
    this.getMissionCategory();
    this.getMissionTemplateList();
    if (this.$route.params.uid) {
      await this.setForm();
      this.form.mainImageList = this.getFileList();
      this.form.sampleImageList = this.getFileList2();
    }
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private handleSuccessUpload(res: any) {
    if (this.isTemplateSelected) {
      this.$message.warning('템플릿 선택 시 이미지 업로드가 불가합니다.');
      return;
    }
    (this.form.mainImageList as any[]).push({ fileUid: res.uid, file: res });
    this.form.mainImageList = this.getFileList();
  }

  private handleExceed(files: any, fileList: any[]) {
    this.$message.warning(`최대 첨부가능한 수는 5개 입니다, 현재 ${files.length}개의 파일이 업로드 되었습니다.`);
  }

  private getFileList() {
    const fileList: any[] = [];
    this.form.mainImageList.forEach((mainFile: any) => {
      if (mainFile.file) {
        fileList.push({ fileUid: mainFile.fileUid, name: mainFile.file.originalName, url: `${this.apiUrl}/attached-file/${mainFile.fileUid}` });
      } else {
        fileList.push(mainFile);
      }
    });
    return fileList;
  }

  private handleRemove(file: any) {
    if (this.isTemplateSelected) {
      this.$message.warning('템플릿 선택 시 이미지 삭제가 불가합니다.');
      return false;
    }
    const fileIndex = this.form.mainImageList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
    if (fileIndex > -1) this.form.mainImageList.splice(fileIndex, 1);
    return true;
  }

  private handleSuccessUpload2(res: any) {
    if (this.isTemplateSelected) {
      this.$message.warning('템플릿 선택 시 이미지 업로드가 불가합니다.');
      return;
    }
    (this.form.sampleImageList as any[]).push({ fileUid: res.uid, file: res });
    this.form.sampleImageList = this.getFileList2();
  }

  private handleExceed2(files: any) {
    this.$message.warning(`최대 첨부가능한 수는 3개 입니다, 현재 ${files.length}개의 파일이 업로드 되었습니다.`);
  }

  private getFileList2() {
    const fileList: any[] = [];
    this.form.sampleImageList.forEach((sampleFile: any) => {
      if (sampleFile.file) fileList.push({ fileUid: sampleFile.fileUid, name: sampleFile.file.originalName, url: `${this.apiUrl}/attached-file/${sampleFile.fileUid}` });
      else fileList.push(sampleFile);
    });
    return fileList;
  }

  private handleRemove2(file: any) {
    if (this.isTemplateSelected) {
      this.$message.warning('템플릿 선택 시 이미지 삭제가 불가합니다.');
      return false;
    }
    const fileIndex = this.form.sampleImageList.findIndex((postFile: any) => postFile.fileUid === file.fileUid);
    if (fileIndex > -1) this.form.sampleImageList.splice(fileIndex, 1);
    return true;
  }

  private handleBeforeUpload(uploadFile: File, isPhoto: boolean) {
    if (this.isTemplateSelected) {
      this.$message.warning('템플릿 선택 시 이미지 업로드가 불가합니다.');
      return false;
    }
    if (isPhoto) {
      const isImageFile = uploadFile.type.split('/')[0] === 'image';
      if (!isImageFile) {
        this.$message.warning('이미지 파일만 업로드 가능합니다.');
        return false;
      }
    }
    return true;
  }

  private loading = false;

  // 유저 선택 관련
  private selectedUserName = '';

  private selectedUserUid = '';

  private userDialogVisible = false;

  private userList: any[] = [];

  private userLoading = false;

  private userSearchQuery = '';

  private userQuery = {
    page: 1,
    size: 10,
    actualName: '',
    roleCode: 'ROLE_USER', // 일반 유저만 선택 가능
    searchType: 'actualName', // 검색 타입
    searchValue: '', // 검색 값
  };

  private userTotalElements = 0;

  private form: any = {
    uid: '',
    title: '',
    content: '',
    dailyReward: '',
    authMethodInfo: '',
    categoryUid: '',
    missionCategory: '',
    mainImageList: [],
    sampleImageList: [],
    useStatus: true,
    missionRange: '',
    achieveEffect: 0,
    alarmStatus: 0,
    alarmTime: '00:00',
    mon: 0,
    tue: 0,
    wed: 0,
    thu: 0,
    fri: 0,
    sat: 0,
    sun: 0,
    startDate: '',
    endDate: '',
    totalDay: 1,
    createDate: '',
    userUid: '', // 선택된 유저 UID
  }

  private categoryList: any = {}

  private templateList: any[] = []

  private isTemplateSelected = false

  /* eslint-disable */
  get rules() {
    const baseRules: any = {
      title: [
        { required: true, message: '미션명을 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      content: [
        { required: true, message: '내용을 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      alarmTitle: [
        { required: true, message: '알람명을 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      alarmMessage: [
        { required: true, message: '내용을 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      authMethodInfo: [
        { required: true, message: '인증방식을 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      dailyReward: [
        { required: true, message: '적립금을 입력해주세요.', trigger: ['blur', 'change'] },
        // { min: 1, max: 100, message: '할인율을 1에서 100퍼센트로 입력해주세요.', trigger: ['blur', 'change'] },
      ],
      totalDay: [
        { required: true, message: '성공 기준 일수를 입력해주세요.', trigger: ['blur', 'change'] },
        { type: 'number', min: 1, message: '성공 기준 일수는 1 이상이어야 합니다.', trigger: ['blur', 'change'] },
      ],
      missionRange: [
        { required: true, message: '미션 기간을 선택해주세요.', trigger: ['blur', 'change'] },
      ],
    };

    // 수정 모드가 아닐 때만 userUid 유효성 검사 추가
    if (!this.$route.params.uid) {
      baseRules.userUid = [
        { required: true, message: '유저를 선택해주세요.', trigger: ['blur', 'change'] },
      ];
    }

    return baseRules;
  }
  /* eslint-enable */

  private async setForm() {
    this.loading = true;
    await getMissionDetail(this.$route.params.uid).then((res) => {
      this.form = {
        ...res.data,
        missionRange: [res.data.startDate, res.data.endDate],
      };
      // 수정 모드일 때 선택된 유저 이름 설정
      if (res.data.missionUserList && res.data.missionUserList.length > 0) {
        this.selectedUserName = res.data.missionUserList[0].actualName;
        this.selectedUserUid = res.data.missionUserList[0].userUid;
      }
      // 템플릿이 선택되어 있다면 isTemplateSelected를 true로 설정
      if (res.data.templateUid) {
        this.isTemplateSelected = true;
      }
      this.loading = false;
    });
  }

  private getMissionCategory() {
    this.loading = true;
    getMissionCategory().then((res) => {
      this.categoryList = res.data;
      this.loading = false;
    });
  }

  private getMissionTemplateList() {
    this.loading = true;
    getMissionTemplateList().then((res) => {
      this.templateList = res.data.content;
      this.loading = false;
    }).catch(() => {
      this.loading = false;
      this.$message.error('템플릿 목록을 불러오는데 실패했습니다.');
    });
  }

  private handleTemplateChange(templateUid: string) {
    if (!templateUid) {
      this.isTemplateSelected = false;
      // 사용안함 선택 시 필드들 초기화
      this.form.title = '';
      this.form.content = '';
      this.form.authMethodInfo = '';
      this.form.categoryUid = '';
      this.form.mainImageList = [];
      this.form.sampleImageList = [];
      return;
    }
    this.isTemplateSelected = true;
    const selectedTemplate = this.templateList.find((template: any) => template.uid === templateUid);
    if (!selectedTemplate) return;
    // 먼저 기존 이미지 리스트 초기화
    this.form.mainImageList = [];
    this.form.sampleImageList = [];
    // 템플릿 데이터로 폼 필드들 업데이트
    this.form.title = selectedTemplate.title || '';
    this.form.content = selectedTemplate.content || '';
    this.form.authMethodInfo = selectedTemplate.authMethodInfo || '';
    this.form.categoryUid = selectedTemplate.categoryUid || '';
    // 미션 사진 업데이트
    if (selectedTemplate.mainImageList && selectedTemplate.mainImageList.length > 0) {
      this.form.mainImageList = selectedTemplate.mainImageList.map((image: any) => ({
        fileUid: image.fileUid,
        name: image.file?.originalName || image.originalName || image.name || `image_${image.fileUid}`,
        url: `${this.apiUrl}/attached-file/${image.fileUid}`,
      }));
    }
    // 인증방식 예시 사진 업데이트
    if (selectedTemplate.sampleImageList && selectedTemplate.sampleImageList.length > 0) {
      this.form.sampleImageList = selectedTemplate.sampleImageList.map((image: any) => ({
        fileUid: image.fileUid,
        name: image.file?.originalName || image.originalName || image.name || `image_${image.fileUid}`,
        url: `${this.apiUrl}/attached-file/${image.fileUid}`,
      }));
    }
    this.$message.success('템플릿이 적용되었습니다.');
  }

  private handleDatePickerChange() {
    if (this.form.missionRange === null) {
      this.form.holidayStartDate = null;
      this.form.holidayEndDate = null;
    }
    [this.form.holidayStartDate, this.form.holidayEndDate] = this.form.missionRange;
  }

  private handleCancel() {
    this.$router.push({ name: 'MissionList' });
  }

  private handleSave() {
    (this.$refs.form as Form).validate((valid: boolean) => {
      if (valid) {
        this.loading = true;
        if (this.$route.params.uid) {
          updateMission(this.$route.params.uid, this.form).then(() => {
            this.$message.success('미션이 수정되었습니다.');
            this.$router.push({ name: 'MissionList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        } else {
          addMission(this.form).then(() => {
            this.$message.success('미션이 추가되었습니다.');
            this.$router.push({ name: 'MissionList' });
            this.loading = false;
          }).catch(() => {
            this.loading = false;
          });
        }
      }
    });
  }

  /* eslint-disable */
  private handleChangeMissionRange() {
    if (this.form.missionRange && this.form.missionRange.length === 2) {
      this.form.startDate = this.form.missionRange[0];
      this.form.endDate = this.form.missionRange[1];
    } else {
      this.form.startDate = '';
      this.form.endDate = '';
    }
  }

  private selectAllAlarms() {
    this.form.mon = true;
    this.form.tue = true;
    this.form.wed = true;
    this.form.thu = true;
    this.form.fri = true;
    this.form.sat = true;
    this.form.sun = true;
  }

  private deselectAllAlarms() {
    this.form.mon = false;
    this.form.tue = false;
    this.form.wed = false;
    this.form.thu = false;
    this.form.fri = false;
    this.form.sat = false;
    this.form.sun = false;
  }

  // 유저 선택 관련 메서드
  private openUserDialog() {
    this.userDialogVisible = true;
    this.getUserList();
  }

  private handleCloseUserDialog() {
    this.userDialogVisible = false;
    this.userSearchQuery = '';
    this.userQuery.page = 1;
    this.userQuery.actualName = '';
    this.getUserList(); // 다이얼로그 닫을 때 검색 필터 초기화
  }

  private getUserList() {
    this.userLoading = true;
    getUserList(this.userQuery).then((res) => {
      this.userList = res.data.content;
      this.userTotalElements = res.data.totalElements;
      this.userLoading = false;
    }).catch(() => {
      this.userLoading = false;
      this.$message.error('유저 목록을 불러오는데 실패했습니다.');
    });
  }

  private searchUsers() {
    this.userQuery.searchType = 'actualName';
    this.userQuery.searchValue = this.userSearchQuery.trim();
    this.userQuery.page = 1;
    this.getUserList();
  }

  private selectUser(user: any) {
    this.selectedUserName = user.actualName;
    this.selectedUserUid = user.uid;
    this.form.userUid = user.uid; // form에 유저 UID 저장
    this.userDialogVisible = false;
    // 유저 선택 후 유효성 검사 필드 업데이트
    this.$nextTick(() => {
      (this.$refs.form as Form).validateField('userUid');
    });
  }

  private handleUserPageChange(page: number) {
    this.userQuery.page = page;
    this.getUserList();
  }
  /* eslint-enable */
}
</script>

<style scoped>
.user-select-dialog {
  border-radius: 8px;
}

.user-select-dialog ::v-deep .el-dialog {
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.user-select-dialog ::v-deep .el-dialog__header {
  background: #2d303b;
  color: white;
  padding: 20px 20px 15px;
  border-radius: 8px 8px 0 0;
}

.user-select-dialog ::v-deep .el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 16px;
}

.user-select-dialog ::v-deep .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 18px;
}

.user-select-dialog ::v-deep .el-dialog__body {
  padding: 20px;
  background-color: #fafafa;
}

.search-section {
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
  max-width: 350px;
}

.search-input ::v-deep .el-input__inner {
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

.search-input ::v-deep .el-input__inner:focus {
  border-color: #2d303b;
  box-shadow: 0 0 0 2px rgba(45, 48, 59, 0.2);
}

.search-input ::v-deep .el-input-group__append {
  border-radius: 0;
  border: 1px solid #e0e0e0;
  border-left: none;
  background: #2d303b;
}

.search-input ::v-deep .el-input-group__append .el-button {
  background: transparent;
  border: none;
  color: white;
  padding: 0 15px;
}

.user-table {
  margin: 15px 0;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-table ::v-deep .el-table__header {
  background: #f8f9fa;
}

.user-table ::v-deep .el-table__header th {
  background: #f8f9fa;
  color: #495057;
  font-weight: 600;
  border-bottom: 2px solid #e9ecef;
}

.user-table ::v-deep .el-table__row {
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-table ::v-deep .el-table__row:hover {
  background-color: #f0f7ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(45, 48, 59, 0.1);
}

.pagination-section {
  text-align: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e9ecef;
}

.pagination-section ::v-deep .el-pagination {
  justify-content: center;
}

.pagination-section ::v-deep .el-pager li {
  border-radius: 4px;
  margin: 0 2px;
}

.pagination-section ::v-deep .el-pager li.active {
  background: #2d303b;
  color: white;
}

.user-select-dialog ::v-deep .el-dialog__footer {
  padding: 15px 20px;
  background: #f8f9fa;
  border-radius: 0 0 8px 8px;
  text-align: right;
}

.user-select-dialog ::v-deep .dialog-footer .el-button {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
}
</style>
