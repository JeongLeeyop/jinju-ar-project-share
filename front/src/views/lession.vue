<template>
  <div class="lession-page">
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="'lession'" @open-write-modal="openWriteModal" />

    <!-- Main Content Area -->
    <div class="lession-main">

      <!-- Loading State -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading" style="font-size: 40px; color: #073DFF;"></i>
        <p>레슨을 불러오는 중...</p>
      </div>

      <!-- Empty State -->
      <div v-else-if="!lessionList || lessionList.length === 0" class="empty-state">
        <svg width="120" height="120" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="60" cy="60" r="50" fill="#F5F5F5"/>
          <path d="M60 35V70M60 80V85" stroke="#888" stroke-width="4" stroke-linecap="round"/>
        </svg>
        <h3>등록된 레슨이 없습니다</h3>
        <p>새로운 레슨을 작성해보세요!</p>
      </div>

      <!-- Lession Cards Grid -->
      <div v-else class="lession-content">
        <div
          v-for="(lession, index) in lessionList"
          :key="lession.uid || index"
          class="lession-card"
          @click="handleLessionClick(lession)"
        >
          <div class="card-image">
            <img
              :src="getLessionImage(lession)"
              :alt="lession.name"
              @error="$event.target.src='https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=985&h=620&fit=crop'"
            />
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ lession.name }}</h3>
            <p class="card-description">
              {{ lession.description }}
              <span class="read-more">더보기</span>
            </p>
          </div>
        </div>
      </div>

      <!-- Floating Write Button -->
      <button class="write-post-btn-fixed" @click="openWriteModal">
        <span class="btn-text">글 작성하기</span>
        <svg class="btn-icon" width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M7.5 0V15M15 7.5H0" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>

    <!-- Write Lesson Modal -->
    <el-dialog
      :visible.sync="writeModalVisible"
      width="600px"
      center
      :show-close="true"
      :append-to-body="true"
      custom-class="write-lesson-modal"
    >
      <div class="modal-content">
        <h3 class="modal-title">새 레슨 작성</h3>
        <el-form :model="lessonForm" label-position="top">
          <el-form-item label="레슨 제목">
            <el-input 
              v-model="lessonForm.name" 
              placeholder="레슨 제목을 입력하세요"
              :disabled="loading"
            ></el-input>
          </el-form-item>
          <el-form-item label="레슨 설명">
            <el-input
              v-model="lessonForm.description"
              type="textarea"
              :rows="4"
              placeholder="레슨 설명을 입력하세요"
              :disabled="loading"
            ></el-input>
          </el-form-item>
          <el-form-item label="썸네일 이미지">
            <div class="thumbnail-upload-container">
              <!-- 썸네일 미리보기 -->
              <div v-if="thumbnailPreview" class="thumbnail-preview">
                <img :src="thumbnailPreview" alt="썸네일 미리보기" />
                <div class="thumbnail-overlay" @click="removeThumbnail">
                  <i class="el-icon-delete"></i>
                  <span>삭제</span>
                </div>
              </div>
              
              <!-- 업로드 버튼 영역 -->
              <div v-else class="thumbnail-upload-area" @click="triggerFileInput">
                <i class="el-icon-plus"></i>
                <p>썸네일 이미지 추가</p>
                <span class="upload-hint">권장 크기: 985x620px</span>
              </div>
              
              <!-- 숨겨진 파일 입력 -->
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleFileChange"
                :disabled="loading"
              />
            </div>
          </el-form-item>
        </el-form>
        <div class="modal-actions">
          <el-button @click="closeModal" :disabled="loading">취소</el-button>
          <el-button 
            type="primary" 
            @click="submitLesson" 
            :loading="loading"
          >작성하기</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getLessionList, addLession } from '@/api/lession';
import request from '@/utils/request';
import { ILession } from '@/types/lession';
import { ChannelModule } from '@/store/modules/channel';
import { ChannelPermissionModule } from '@/store/modules/channelPermission';

@Component({
  name: 'Lession',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private writeModalVisible = false;
  private loading = false;
  
  private lessonForm = {
    name: '',
    description: '',
    privateState: false,
    secretState: false,
    channelUid: '',
  };

  private thumbnailFile: File | null = null;
  private thumbnailPreview: string = '';
  private uploadedFileUid: string = '';

  private listQuery: any = {
    searchType: 'name',
    searchValue: '',
    channelUid: ChannelModule.selectedChannel.uid,
    size: 10,
    page: 1, // Changed from 0 to 1 for proper pagination
  }

  private lessionList: ILession[] = [];

  async created() {
    // 권한 로드
    const channelUid = ChannelModule.selectedChannel.uid;
    if (channelUid) {
      await ChannelPermissionModule.loadPermissions(channelUid);
    }
    this.fetchLessionList();
  }

  async activated() {
    // 다른 페이지에서 돌아왔을 때 권한 및 목록 새로고침
    const channelUid = ChannelModule.selectedChannel.uid;
    if (channelUid) {
      await ChannelPermissionModule.loadPermissions(channelUid);
    }
    this.fetchLessionList();
  }

  private async fetchLessionList() {
    try {
      this.loading = true;
      
      // Use getCommunityLession to fetch lessons for current community/channel
      const channelUid = ChannelModule.selectedChannel.uid;
      if (!channelUid) {
        this.$message.warning('커뮤니티를 선택해주세요.');
        this.loading = false;
        return;
      }

      const response = await getLessionList({
        ...this.listQuery,
        channelUid,
      });
      
      // API 응답에서 content 배열 추출
      this.lessionList = response.data?.content || [];
      
      console.log('레슨 목록 로드 완료:', {
        count: this.lessionList.length,
        channelUid,
      });
    } catch (error) {
      console.error('레슨 목록을 불러오는데 실패했습니다:', error);
      this.$message.error('레슨 목록을 불러올 수 없습니다.');
    } finally {
      this.loading = false;
    }
  }

  private openWriteModal() {
    // 권한 체크: 커뮤니티 관리자만 강좌 생성 가능
    if (!ChannelPermissionModule.loaded) {
      this.$message.error('권한 정보를 불러오는 중입니다');
      return;
    }
    
    // 추방된 사용자는 접근 불가
    if (ChannelPermissionModule.isBanned) {
      this.$message.error('추방된 사용자는 강좌를 생성할 수 없습니다');
      return;
    }
    
    // 멤버가 아니면 권한 없음
    if (!ChannelPermissionModule.isMember) {
      this.$message.error('커뮤니티 멤버만 강좌를 생성할 수 있습니다');
      return;
    }
    
    // 커뮤니티 관리자만 강좌 생성 가능
    if (!ChannelPermissionModule.isChannelAdmin) {
      this.$message.error('강좌 생성은 커뮤니티 관리자만 가능합니다');
      return;
    }
    
    this.writeModalVisible = true;
  }

  private triggerFileInput() {
    if (this.loading) return;
    (this.$refs.fileInput as HTMLInputElement).click();
  }

  private handleFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const file = target.files?.[0];
    
    if (!file) return;

    // 이미지 파일인지 확인
    if (!file.type.startsWith('image/')) {
      this.$message.error('이미지 파일만 업로드 가능합니다.');
      return;
    }

    // 파일 크기 확인 (5MB 제한)
    if (file.size > 5 * 1024 * 1024) {
      this.$message.error('파일 크기는 5MB 이하여야 합니다.');
      return;
    }

    this.thumbnailFile = file;

    // 미리보기 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      this.thumbnailPreview = e.target?.result as string;
    };
    reader.readAsDataURL(file);

    // 파일 입력 초기화 (같은 파일 재선택 가능하도록)
    target.value = '';
  }

  private removeThumbnail() {
    if (this.loading) return;
    
    this.thumbnailFile = null;
    this.thumbnailPreview = '';
    this.uploadedFileUid = '';
    
    // 파일 입력 초기화
    const fileInput = this.$refs.fileInput as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  private async uploadThumbnail(): Promise<string | null> {
    if (!this.thumbnailFile) return null;

    try {
      const formData = new FormData();
      formData.append('file', this.thumbnailFile);

      // /client/lession/upload 엔드포인트로 업로드
      const response = await request({
        url: '/client/lession/upload',
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      console.log('업로드 응답:', response);
      
      // 응답 구조에 따라 fileUid 추출
      let fileUid = null;
      if (response.data) {
        // AttachedFileDto.detail 구조: { uid, ... }
        if (response.data.uid) {
          fileUid = response.data.uid;
        }
        // 배열로 반환되는 경우
        else if (Array.isArray(response.data) && response.data.length > 0) {
          fileUid = response.data[0].uid || response.data[0].fileUid || response.data[0];
        }
        // fileUid로 반환되는 경우
        else if (response.data.fileUid) {
          fileUid = response.data.fileUid;
        }
        // 문자열로 반환되는 경우
        else if (typeof response.data === 'string') {
          fileUid = response.data;
        }
      }
      
      if (fileUid) {
        this.uploadedFileUid = fileUid;
        console.log('썸네일 업로드 성공:', fileUid);
        return fileUid;
      }

      console.error('fileUid를 찾을 수 없습니다:', response);
      return null;
    } catch (error) {
      console.error('썸네일 업로드 실패:', error);
      throw error;
    }
  }

  private async submitLesson() {
    try {
      // 유효성 검사
      if (!this.lessonForm.name || !this.lessonForm.description) {
        this.$message.warning('레슨 제목과 설명을 입력해주세요.');
        return;
      }

      this.loading = true;

      // 현재 선택된 채널 UID 사용
      const channelUid = this.lessonForm.channelUid || ChannelModule.selectedChannel.uid;
      
      if (!channelUid) {
        this.$message.warning('채널을 선택해주세요.');
        this.loading = false;
        return;
      }

      // 썸네일이 있으면 먼저 업로드
      let fileUid = null;
      if (this.thumbnailFile) {
        try {
          fileUid = await this.uploadThumbnail();
          if (!fileUid) {
            this.$message.warning('썸네일 업로드 결과를 받지 못했습니다. 썸네일 없이 계속합니다.');
          }
        } catch (error) {
          console.error('썸네일 업로드 에러:', error);
          this.$message.warning('썸네일 업로드에 실패했습니다. 썸네일 없이 계속합니다.');
        }
      }

      // API 호출
      const newLessionData: ILession = {
        uid: '',
        name: this.lessonForm.name,
        description: this.lessonForm.description,
        privateState: this.lessonForm.privateState || false,
        secretState: this.lessonForm.secretState || false,
        createDate: '',
        channelUid,
        myWatchPercent: 0,
        video: [],
        fileList: fileUid ? [{
          fileUid,
          viewOrder: 0,
        }] : [],
      };

      console.log('레슨 생성 데이터:', newLessionData);

      const response = await addLession(newLessionData);
      
      console.log('레슨 생성 응답:', response);
      
      // 성공 시 목록 새로고침
      await this.fetchLessionList();
      
      this.$message.success('레슨이 작성되었습니다.');
      this.closeModal();
    } catch (error) {
      console.error('레슨 작성 실패:', error);
      this.$message.error('레슨 작성에 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private closeModal() {
    this.writeModalVisible = false;
    
    // 폼 초기화
    this.lessonForm = {
      name: '',
      description: '',
      privateState: false,
      secretState: false,
      channelUid: '',
    };
    
    // 썸네일 초기화
    this.removeThumbnail();
  }

  private handleLessionClick(lession: ILession) {
    if (lession.uid) {
      this.$router.push({
        name: 'Video',
        params: { lessionUid: lession.uid },
      });
    }
  }

  // 레슨 썸네일 이미지 가져오기
  private getLessionImage(lession: ILession): string {
    console.log('레슨 이미지 조회:', {
      name: lession.name,
      fileList: lession.fileList,
      fileListLength: lession.fileList?.length,
    });

    // fileList에서 첫 번째 이미지 사용
    if (lession.fileList && lession.fileList.length > 0) {
      const firstFile = lession.fileList[0];
      
      console.log('첫 번째 파일:', firstFile);
      
      // fileUid가 있으면 파일 API 엔드포인트 사용
      if (firstFile.fileUid) {
        const imageUrl = `/api/attached-file/${firstFile.fileUid}`;
        console.log('생성된 이미지 URL:', imageUrl);
        return imageUrl;
      }
      
      // file 객체가 있고 path가 있으면 그대로 사용
      if (firstFile.file && firstFile.file.path) {
        console.log('파일 경로 사용:', firstFile.file.path);
        return firstFile.file.path;
      }
      
      // name이 URL인 경우
      if (firstFile.name && (firstFile.name.startsWith('http://') || firstFile.name.startsWith('https://'))) {
        console.log('name에서 URL 사용:', firstFile.name);
        return firstFile.name;
      }
    }
    
    // 기본 이미지
    console.log('기본 이미지 사용');
    return 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=985&h=620&fit=crop';
  }
}
</script>

<style scoped lang="scss">
.lession-page {
  display: flex;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.lession-main {
  flex: 1;
  margin-left: 270px;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #F8F9FB;

  @media screen and (max-width: 1024px) {
    margin-left: 240px;
  }
  @media screen and (max-width: 768px) {
    margin-left: 0px;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 16px;
  
  p {
    color: #888;
    font-size: 16px;
    font-weight: 500;
  }
}

.empty-state {
  margin-top:100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 16px;
  padding: 40px;
  
  h3 {
    color: #222;
    font-size: 20px;
    font-weight: 600;
    margin: 0;
  }
  
  p {
    color: #888;
    font-size: 16px;
    margin: 0;
  }
}

.lession-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 124px;
  border-bottom: 2px solid #EBEBEB;
  padding: 0 40px;
  gap: 40px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.lession-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 32px;
  font-weight: 800;
  line-height: 100%;
  margin: 0;

  .title-brand {
    color: #073DFF;
  }

  .title-text {
    color: #222;
  }
}

.member-count {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
}

.lession-nav {
  display: flex;
  align-items: center;
  gap: 32px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 32px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;

  svg {
    flex-shrink: 0;
  }

  span {
    color: #444;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    line-height: 100%;
  }

  &.active {
    span {
      color: #073DFF;
    }
  }

  &:hover:not(.active) {
    opacity: 0.7;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-shrink: 0;

  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.points {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;

  svg {
    flex-shrink: 0;
  }
}

.user-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.lession-content {
  display: flex;
  flex-wrap: wrap;
  gap: 32px;
  padding: 140px 40px 100px 40px;
}

.lession-card {
  flex: 0 1 calc(100% / 3 - 32px);
  display: flex;
  flex-direction: column;
  gap: 16px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-2px);
  }
}

.card-image {
  width: 100%;
  height: 300px;
  border-radius: 10px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.card-description {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 400;
  line-height: 150%;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;

  .read-more {
    color: #073DFF;
    font-weight: 700;
    cursor: pointer;
    margin-left: 6px;
    transition: opacity 0.2s;

    &:hover {
      opacity: 0.8;
    }
  }
}

.write-post-btn-fixed {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 50px;
  height: 50px;
  padding: 14px;
  background: #073DFF;
  border: none;
  border-radius: 50%;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 999; // 헤더보다 높은 z-index
  display: none; // PC에서는 숨김
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-text {
    display: none;
  }

  .btn-icon {
    display: block;
  }

  &:hover {
    background: #0530CC;
    transform: scale(1.1);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: scale(0.95);
  }

  @media screen and (max-width: 768px) {
    display: flex; // 모바일에서만 보임
    right: 20px;
    bottom: 20px;
    width: 50px;
    height: 50px;
  }
}

// Write Lesson Modal Styles
::v-deep .write-lesson-modal {
  .el-dialog {
    border-radius: 12px;
  }

  .el-dialog__header {
    padding: 0;
  }

  .el-dialog__body {
    padding: 32px;
  }

  .modal-content {
    .modal-title {
      font-size: 24px;
      font-weight: 600;
      color: #000;
      margin: 0 0 24px 0;
      text-align: center;
    }

    .el-form {
      .el-form-item__label {
        font-size: 14px;
        font-weight: 500;
        color: #333;
        margin-bottom: 8px;
      }

      .el-input__inner,
      .el-textarea__inner {
        border: 1px solid #E0E0E0;
        border-radius: 8px;
        font-size: 14px;

        &:focus {
          border-color: #073DFF;
        }
      }
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 24px;

      .el-button {
        padding: 10px 24px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 500;

        &:first-child {
          background: #F5F5F5;
          border: none;
          color: #666;

          &:hover {
            background: #E0E0E0;
          }
        }

        &.el-button--primary {
          background: #073DFF;
          border: none;

          &:hover {
            background: #0530CC;
          }
        }
      }
    }
  }

  .thumbnail-upload-container {
    width: 100%;
  }

  .thumbnail-preview {
    position: relative;
    width: 100%;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;
    border: 2px solid #E0E0E0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .thumbnail-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;
      opacity: 0;
      transition: opacity 0.2s;
      cursor: pointer;

      i {
        font-size: 32px;
        color: #FFF;
      }

      span {
        color: #FFF;
        font-size: 14px;
        font-weight: 500;
      }

      &:hover {
        opacity: 1;
      }
    }
  }

  .thumbnail-upload-area {
    width: 100%;
    height: 200px;
    border: 2px dashed #D0D0D0;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    cursor: pointer;
    transition: all 0.2s;
    background: #FAFAFA;

    i {
      font-size: 48px;
      color: #888;
    }

    p {
      color: #333;
      font-size: 16px;
      font-weight: 500;
      margin: 0;
    }

    .upload-hint {
      color: #888;
      font-size: 12px;
    }

    &:hover {
      border-color: #073DFF;
      background: #F5F8FF;

      i {
        color: #073DFF;
      }

      p {
        color: #073DFF;
      }
    }
  }

  @media screen and (max-width: 768px) {
    width: 100% !important;
    margin: 0 !important;
    height: 100%;
    max-width: 100vw;

    .el-dialog {
      height: 100%;
      margin: 0;
    }

    .el-dialog__body {
      padding: 20px;
      height: calc(100% - 40px);
      overflow-y: auto;
    }
  }
}

/* Responsive Design */
@media screen and (max-width: 1440px) {
  .card-image{ height: 250px;}
}

@media screen and (max-width: 1024px) {
  .lession-content {gap: 20px;padding: 120px 20px 50px 20px;}
  .lession-card {flex: 0 1 calc(100% / 3 - 14px);}
  .card-image{ height: 200px;}
}

@media screen and (max-width: 768px) {
  .lession-content { padding: 140px 20px 50px 20px;}
  .lession-card {flex: 0 1 calc(100% / 2 - 10px);gap: 16px;}
  .card-image { height: 170px;}
  .card-title { font-size: 18px;}
  .card-description{ font-size: 12px; line-height: 150%;}
}

@media screen and (max-width: 500px) {
  .lession-content { padding: 100px 20px 50px 20px;}
  .lession-card {flex: 0 1 calc(100%);}
  .card-image { height: 180px;}
  .card-content { gap: 8px;}
}

@media screen and (max-width: 425px) {
}
</style>
