<template>
  <div class="video-page">
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="'lession'" @open-write-modal="handleWriteModal" />

    <!-- Main Content Area -->
    <div class="video-main">
      <div class="video-content">
        <!-- Back Button -->
        <div class="back-btn-wr">
          <el-button class="back-btn" @click="handleGoBack">
            <i class="el-icon-arrow-left"></i>
            <span>뒤로가기</span>
          </el-button>
        </div>

        <!-- Page Title -->
        <h1 class="page-title">{{ lessionTitle }}</h1>

        <!-- Lecture Count Badge -->
        <div class="lecture-count-badge">
          <span class="label">전체 강의</span>
          <span class="count-pill">{{ totalElements }}개</span>
        </div>

        <!-- Lecture Table -->
        <div class="lecture-table-container">
          <!-- Table Header -->
          <div class="table-header">
            <div class="header-cell col-number">번호</div>
            <div class="header-cell col-name">강의명</div>
            <div class="header-cell col-description">강의 내용</div>
            <div class="header-cell col-progress">진행 상태</div>
            <div class="header-cell col-date">마지막 시청일</div>
            <div class="header-cell col-status">진행 여부</div>
          </div>

          <!-- Table Body -->
          <div v-loading="loading" class="table-body">
            <div
            v-for="(video, index) in videoList"
            :key="video.idx"
            class="table-row"
            @click="handleClickVideo(video.idx)"
          >
            <!-- Desktop View -->
            <div class="body-cell col-number">{{ getIndex(index) }}</div>
            <div class="body-cell col-name">{{ video.title || '커뮤니티에 대한 이해' }}</div>
            <div class="body-cell col-description">{{ video.description || '커뮤니티의 기초에 대해 설명합니다. 커뮤니티의 기초에 대해 설명합니다. 커뮤니티의 기초에 대해 설명합니다. 커뮤니티의 기초에 대해 설명합니다.' }}</div>
            <div class="body-cell col-progress">
              <span class="desktop-progress">{{ video.myWatchPercent || 0 }}%</span>
              <!-- Mobile Progress Bar -->
              <div class="mobile-progress-wrapper">
                <div class="mobile-progress-bar">
                  <div
                    class="mobile-progress-fill"
                    :style="{ width: (video.myWatchPercent || 0) + '%' }"
                  ></div>
                </div>
                <div class="mobile-progress-info">
                  <span class="mobile-progress-date">{{ video.lastWatchUpdate || '2025.10.01' }}</span>
                  <span class="mobile-progress-percent">{{ video.myWatchPercent || 0 }}%</span>
                </div>
              </div>
            </div>
            <div class="body-cell col-date">{{ video.lastWatchUpdate | parseDate('YYYY년 MM월 DD일') }}</div>
            <div class="body-cell col-status">
              <span
                class="status-badge"
                :class="{ completed: video.myWatchPercent === 100 }"
              >
                {{ video.myWatchPercent === 100 ? '진행 완료' : '진행중' }}
              </span>
            </div>
          </div>

            <!-- Empty State -->
            <div v-if="!loading && videoList.length === 0" class="empty-state">
              등록된 강의가 없습니다.
            </div>
          </div>
        </div>

        <!-- Pagination -->
        <Pagination
          v-if="totalElements > 0"
          :total="totalElements"
          :page.sync="listQuery.page"
          :limit.sync="listQuery.size"
          @pagination="getVideoList()"
        />

        <!-- Floating Write Button -->
        <button class="write-post-btn-fixed" @click="handleWriteModal">
          <span class="btn-text">강의 추가</span>
          <svg class="btn-icon" width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M7.5 0V15M15 7.5H0" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- Write Video Modal -->
    <el-dialog
      :visible.sync="writeModalVisible"
      width="800px"
      center
      :show-close="true"
      :append-to-body="true"
      custom-class="write-video-modal"
      :close-on-click-modal="false"
    >
      <div class="modal-header" slot="title">
        <h3 class="modal-title">강의 영상 추가</h3>
      </div>
      
      <div class="modal-content">
        <el-form
          ref="videoForm"
          :model="videoData"
          :rules="videoRules"
          label-position="top"
          v-loading="formLoading"
        >
          <el-form-item label="제목" prop="title">
            <el-input 
              v-model="videoData.title" 
              placeholder="강의 제목을 입력하세요"
              :disabled="formLoading"
            />
          </el-form-item>

          <el-form-item label="강의 한줄 소개">
            <el-input 
              v-model="videoData.description" 
              placeholder="강의에 대한 간단한 설명을 입력하세요"
              :disabled="formLoading"
            />
          </el-form-item>

          <el-form-item label="유튜브 링크" prop="urlCode">
            <el-input 
              v-model="youtubeLink" 
              placeholder="https://www.youtube.com/watch?v=..."
              @input="handleYoutubeLink"
              :disabled="formLoading"
            />
            <div v-if="videoData.urlCode" class="youtube-preview">
              <span class="preview-label">영상 코드:</span>
              <span class="preview-code">{{ videoData.urlCode }}</span>
            </div>
          </el-form-item>

          <el-form-item label="내용 (선택사항)">
            <el-input
              v-model="videoData.content"
              type="textarea"
              :rows="4"
              placeholder="강의의 상세 내용을 입력하세요"
              :disabled="formLoading"
            />
          </el-form-item>
        </el-form>

        <div class="modal-actions">
          <el-button @click="closeVideoModal" :disabled="formLoading">취소</el-button>
          <el-button 
            type="primary" 
            @click="submitVideo"
            :loading="formLoading"
          >
            등록
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { getVideoList, addVideo } from '@/api/video';
import { getLession } from '@/api/lession';
import { IVideo, ILession } from '@/types/lession';
import Pagination from '@/components/Pagination/index.vue';
import { ChannelModule } from '@/store/modules/channel';
import HomeHeader from '@/layout/components/homeHeader.vue';
import CommunitySidebar from './components/communitySidebar.vue';

@Component({
  name: 'Video',
  components: {
    Pagination,
    HomeHeader,
    CommunitySidebar,
  },
})
export default class extends Vue {
  mounted() {
    this.initPage();
  }

  activated() {
    // 비디오 시청 후 돌아왔을 때 목록 새로고침
    this.getVideoList();
  }

  @Watch('$route.params.lessionUid', { immediate: true })
  onLessionUidChange(newVal: string, oldVal: string) {
    if (newVal && newVal !== oldVal) {
      this.lessionUid = newVal;
      this.initPage();
    }
  }

  private async initPage() {
    this.lessionUid = this.$route.params.lessionUid;
    if (!this.lessionUid) {
      this.$message.error('강좌 정보를 찾을 수 없습니다.');
      this.handleGoBack();
      return;
    }

    // 강좌 정보 로드
    await this.getLessionInfo();
    // 영상 목록 로드
    await this.getVideoList();
  }

  private videoList: IVideo[] = [];

  private listQuery = {
    searchType: 'name',
    searchValue: '',
    lessionUid: '',
    size: 10,
    page: 1,
  };

  private channelUid: string = ChannelModule.selectedChannel.uid;

  private lessionUid: string = '';

  private lessionTitle: string = '';

  private totalElements: number = 0;

  private totalPages: number = 0;

  private loading: boolean = true;

  private async getLessionInfo() {
    try {
      if (!this.lessionUid) return;
      
      const response = await getLession(this.lessionUid);
      if (response.data) {
        this.lessionTitle = response.data.name || '강좌';
        console.log('강좌 정보 로드:', {
          uid: this.lessionUid,
          name: this.lessionTitle,
        });
      }
    } catch (error) {
      console.error('강좌 정보를 불러오는데 실패했습니다:', error);
      this.lessionTitle = '강좌';
    }
  }

  private async getVideoList() {
    try {
      this.loading = true;
      
      if (!this.lessionUid) {
        console.warn('lessionUid가 없습니다.');
        this.loading = false;
        return;
      }

      const queryParams = {
        ...this.listQuery,
        lessionUid: this.lessionUid,
      };

      const res = await getVideoList(queryParams);
      
      if (this.listQuery.page !== 1 && res.data.content.length === 0) {
        this.listQuery.page -= 1;
        this.getVideoList();
        return;
      }
      
      this.videoList = res.data.content || [];
      this.totalPages = res.data.totalPages || 0;
      this.totalElements = res.data.totalElements || 0;
      
      console.log('영상 목록 로드 완료:', {
        lessionUid: this.lessionUid,
        count: this.videoList.length,
        sample: this.videoList.length > 0 ? {
          title: this.videoList[0].title,
          myWatchPercent: this.videoList[0].myWatchPercent,
          lastWatchSecond: this.videoList[0].lastWatchSecond,
          lastWatchUpdate: this.videoList[0].lastWatchUpdate,
        } : null,
      });
    } catch (error) {
      console.error('영상 목록 로드 실패:', error);
      this.$message.error('영상 목록을 불러올 수 없습니다.');
    } finally {
      this.loading = false;
    }
  }

  private getIndex(index: number) {
    const totalSize = (this.listQuery.page - 1) * this.listQuery.size;
    return totalSize + index + 1;
  }

  private handleClickVideo(idx: string | number) {
    this.$router.push({
      name: 'VideoDetail',
      params: { 
        lessionUid: this.lessionUid,
        videoIdx: String(idx),
      },
    });
  }

  private handleGoBack() {
    this.$router.push({ name: 'Lession' });
  }

  private writeModalVisible = false;
  private formLoading = false;

  private videoData: Partial<IVideo> = {
    idx: '',
    title: '',
    description: '',
    content: '',
    urlCode: '',
    lessionUid: null,
    fileList: [],
    timeLineList: [],
    viewOrder: 0,
    viewCount: 0,
    myWatchPercent: 0,
    lastWatchSecond: 0,
    lastWatchUpdate: null,
    createDate: '',
  };

  private youtubeLink = '';

  private videoRules = {
    title: [
      {
        required: true,
        message: '제목을 입력하세요.',
        trigger: 'blur',
      },
    ],
    urlCode: [
      {
        required: true,
        message: '유튜브 링크를 입력하세요.',
        trigger: 'blur',
      },
    ],
  };

  private handleWriteModal() {
    // Reset form data when opening modal
    this.videoData = {
      idx: '',
      title: '',
      description: '',
      content: '',
      urlCode: '',
      lessionUid: this.lessionUid,
      fileList: [],
      timeLineList: [],
      viewOrder: this.totalElements, // Set to last position
      viewCount: 0,
      myWatchPercent: 0,
      lastWatchSecond: 0,
      lastWatchUpdate: null,
      createDate: '',
    };
    this.youtubeLink = '';
    this.writeModalVisible = true;
  }

  private handleYoutubeLink() {
    try {
      const urlObj = new URL(this.youtubeLink);
      if (urlObj.hostname === 'www.youtube.com' || urlObj.hostname === 'youtube.com') {
        const params = new URLSearchParams(urlObj.search);
        if (params.has('v')) {
          this.videoData.urlCode = params.get('v');
        }
        const pathSegments = urlObj.pathname.split('/');
        if (pathSegments[1] === 'embed' || pathSegments[1] === 'v') {
          this.videoData.urlCode = pathSegments[2];
        }
        this.youtubeLink = `https://www.youtube.com/watch?v=${this.videoData.urlCode}`;
      } else if (urlObj.hostname === 'youtu.be') {
        const pathSegments = urlObj.pathname.split('/');
        this.videoData.urlCode = pathSegments[1];
        this.youtubeLink = `https://www.youtube.com/watch?v=${this.videoData.urlCode}`;
      } else {
        this.$message.warning('유효하지 않은 URL 형식입니다.');
        this.youtubeLink = '';
        this.videoData.urlCode = '';
      }
    } catch (e) {
      // 입력 중일 수 있으므로 에러 메시지는 제출 시에만 표시
      this.videoData.urlCode = '';
    }
  }

  private async submitVideo() {
    (this.$refs.videoForm as any).validate(async (valid: boolean) => {
      if (valid) {
        try {
          this.formLoading = true;
          
          // Validate lessionUid
          if (!this.lessionUid) {
            this.$message.error('강좌 정보를 찾을 수 없습니다.');
            return;
          }

          // Prepare video data for API
          const videoPayload: IVideo = {
            idx: this.videoData.idx || '',
            title: this.videoData.title || '',
            description: this.videoData.description || '',
            content: this.videoData.content || '',
            urlCode: this.videoData.urlCode || '',
            lessionUid: this.lessionUid,
            viewOrder: this.videoData.viewOrder || 0,
            viewCount: 0,
            myWatchPercent: 0,
            lastWatchSecond: 0,
            lastWatchUpdate: null,
            createDate: new Date().toISOString(),
            fileList: [],
            timeLineList: [],
          };
          
          // Call API to add video
          await addVideo(videoPayload);
          
          this.$message.success('강의 영상이 등록되었습니다.');
          this.writeModalVisible = false;
          
          // Reset form
          this.videoData = {
            idx: '',
            title: '',
            description: '',
            content: '',
            urlCode: '',
            lessionUid: this.lessionUid,
            fileList: [],
            timeLineList: [],
            viewOrder: 0,
            viewCount: 0,
            myWatchPercent: 0,
            lastWatchSecond: 0,
            lastWatchUpdate: null,
            createDate: '',
          };
          this.youtubeLink = '';
          
          // Refresh video list
          await this.getVideoList();
        } catch (error) {
          console.error('강의 영상 등록 실패:', error);
          this.$message.error('강의 영상 등록에 실패했습니다.');
        } finally {
          this.formLoading = false;
        }
      }
    });
  }

  private closeVideoModal() {
    this.writeModalVisible = false;
    this.videoData = {
      idx: '',
      title: '',
      description: '',
      content: '',
      urlCode: '',
      lessionUid: this.lessionUid,
      fileList: [],
      timeLineList: [],
      viewOrder: 0,
      viewCount: 0,
      myWatchPercent: 0,
      lastWatchSecond: 0,
      lastWatchUpdate: null,
      createDate: '',
    };
    this.youtubeLink = '';
  }
}
</script>

<style scoped lang="scss">
.video-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.video-main {
  margin-left: 270px;
  padding: 48px;
  padding-top: 150px; // 헤더 높이(120px) + 여유 공간(30px)
  flex: 1;

  @media screen and (max-width: 1024px) {
    margin-left: 0;
    padding: 40px 20px;
    padding-top: 100px; // 모바일 헤더 고려
  }
}

.video-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
  max-width: 100%;

  @media screen and (max-width: 768px) {
    gap: 32px;
  }
}

.back-btn-wr {
  width:100%;
  display: flex;
  align-items: center;

  @media screen and (max-width: 768px) {
    margin-bottom: 16px;
  }
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  background: #F5F5F5;
  border: none;
  border-radius: 8px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  i {
    font-size: 18px;
  }

  &:hover {
    background: #E0E0E0;
  }

  @media screen and (max-width: 768px) {
    padding: 12px 20px;
    font-size: 16px;

    i {
      font-size: 16px;
    }
  }
}

.page-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
  text-align: left;

  @media screen and (min-width: 1024px) {
    font-size: 42px;
  }
}

.lecture-count-badge {
  display: flex;
  align-items: center;
  gap: 8px;

  .label {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 100%;
  }

  .count-pill {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 8px 12px;
    border-radius: 38px;
    border: 1px solid #CECECE;
    color: #000;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 600;
    line-height: 100%;
  }
}

.lecture-table-container {
  display: flex;
  flex-direction: column;
  border-bottom: 2px solid #222;

  @media screen and (max-width: 768px) {
    border-bottom: none;
  }
}

.table-header {
  display: flex;
  align-items: center;
  height: 58px;
  border-top: 2px solid #222;
  border-bottom: 2px solid #222;

  @media screen and (max-width: 768px) {
    display: none;
  }
}

.header-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;

  &.col-number {
    width: 135px;
    justify-content: flex-start;
    padding-left: 20px;
  }

  &.col-name {
    width: 333px;
    text-align: left;
  }

  &.col-description {
    width: 520px;
    text-align: left;
  }

  &.col-progress {
    width: 140px;
  }

  &.col-date {
    width: 346px;
  }

  &.col-status {
    width: 99px;
  }
}

.table-body {
  display: flex;
  flex-direction: column;
  min-height: 200px;

  @media screen and (max-width: 768px) {
    gap: 0;
    min-height: unset;
  }
}

.table-row {
  display: flex;
  align-items: center;
  min-height: 92px;
  border-top: 1px solid #222;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #F8F9FB;
  }

  &:first-child {
    border-top: none;
  }

  @media screen and (max-width: 768px) {
    flex-direction: row;
    flex-wrap: wrap;
    align-items: stretch;
    gap: 32px;
    padding: 32px 0;
    border-top: 1px solid #222;
    min-height: unset;

    &:first-child {
      border-top: 2px solid #222;
    }

    &:hover {
      background: transparent;
    }
  }
}

.body-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  padding: 20px 10px;

  &.col-number {
    width: 135px;
    justify-content: flex-start;
    padding-left: 20px;
  }

  &.col-name {
    width: 333px;
  }

  &.col-description {
    width: 520px;
    text-align: left;
  }

  &.col-progress {
    width: 140px;
  }

  &.col-date {
    width: 346px;
    color: #222;
  }

  &.col-status {
    width: 99px;
  }

  @media screen and (max-width: 768px) {
    padding: 0;
    width: 100% !important;
    justify-content: flex-start;
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;

    &.col-number {
      width: auto !important;
      flex-shrink: 0;
      flex-direction: row;
      align-items: center;
      gap: 16px;
      font-size: 18px;
      font-weight: 400;
      line-height: 150%;
      padding: 0;

      &::after {
        content: '';
        width: 1px;
        height: 14px;
        background: #D9D9D9;
        margin: 0 -10px 0 0;
      }
    }

    &.col-name {
      width: auto !important;
      flex: 1;
      flex-direction: row;
      align-items: center;
      gap: 16px;
      font-size: 18px;
      font-weight: 700;
      line-height: 150%;
      padding: 0;

      &::after {
        display: none;
      }
    }

    &.col-description {
      color: #888;
      font-size: 12px;
      font-weight: 400;
      line-height: 150%;
      text-align: left;
    }

    &.col-progress {
      width: 100% !important;
      flex-direction: column;
      gap: 8px;
      align-items: stretch;
    }

    &.col-date {
      font-size: 12px;
      font-weight: 400;
      line-height: 100%;
    }

    &.col-status {
      width: 100% !important;
    }
  }
}

.status-badge {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid #073DFF;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
  white-space: nowrap;

  &.completed {
    background: #073DFF;
    color: #FFF;
    border-color: #073DFF;
  }

  @media screen and (max-width: 768px) {
    width: 100%;
    height: 54px;
    padding: 12px 0;
    border-radius: 10px;
    border: 1px solid #FFF;
    font-size: 16px;

    &:not(.completed) {
      border: 1px solid #073DFF;
      background: #FFF;
      color: #073DFF;
    }
  }
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
}

.write-post-btn-fixed {
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 43px;
  height: 43px;
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
    width: 43px;
    height: 43px;
  }
}

/* Write Video Modal Styles */
::v-deep .write-video-modal {
  .el-dialog {
    border-radius: 12px;
  }

  .el-dialog__header {
    padding: 24px 24px 0;
    border-bottom: 1px solid #EBEBEB;
  }

  .el-dialog__body {
    padding: 24px;
  }

  .modal-header {
    .modal-title {
      font-size: 20px;
      font-weight: 600;
      color: #000;
      margin: 0;
      padding-bottom: 16px;
    }
  }

  .modal-content {
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
        font-size: 16px;

        &:focus {
          border-color: #073DFF;
        }
      }

      .youtube-preview {
        margin-top: 8px;
        padding: 8px 12px;
        background: #F5F5F5;
        border-radius: 6px;
        display: flex;
        align-items: center;
        gap: 8px;

        .preview-label {
          font-size: 12px;
          color: #888;
          font-weight: 500;
        }

        .preview-code {
          font-size: 12px;
          color: #073DFF;
          font-weight: 600;
          font-family: monospace;
        }
      }
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid #EBEBEB;

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

  @media screen and (max-width: 768px) {
    width: 100% !important;
    margin: 0 !important;
    height: 100%;
    max-width: 100vw;

    .el-dialog {
      height: 100%;
      margin: 0;
    }

    .el-dialog__header {
      padding: 20px;
    }

    .el-dialog__body {
      padding: 20px;
      height: calc(100% - 60px);
      overflow-y: auto;
    }

    .modal-header {
      .modal-title {
        font-size: 18px;
      }
    }
  }
}

.desktop-progress {
  display: inline;

  @media screen and (max-width: 768px) {
    display: none;
  }
}

.mobile-lecture-header {
  display: none;

  @media screen and (max-width: 768px) {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 16px;
    margin-bottom: 8px;
  }
}

.mobile-lecture-number {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
}

.mobile-lecture-divider {
  width: 1px;
  height: 14px;
  background: #D9D9D9;
}

.mobile-lecture-title {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 150%;
}

.mobile-progress-wrapper {
  display: none;

  @media screen and (max-width: 768px) {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }
}

.mobile-progress-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  align-self: stretch;
  border-radius: 100px;
  border: 1px solid #FFF;
  background: #EBEBEB;
  overflow: hidden;
  height: 12px;
  padding: 0;
}

.mobile-progress-fill {
  height: 12px;
  border-radius: 100px;
  background: #073DFF;
  transition: width 0.3s ease;
}

.mobile-progress-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  align-self: stretch;
}

.mobile-progress-date,
.mobile-progress-percent {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 400;
  line-height: 100%;
}

/* Responsive Design */
@media screen and (max-width: 1600px) {
  .video-main {
    padding: 40px;
  }

  .header-cell,
  .body-cell {
    font-size: 16px;

    &.col-description {
      width: 400px;
    }

    &.col-date {
      width: 280px;
    }
  }

  .page-title {
    font-size: 36px;
  }
}

@media screen and (max-width: 1366px) {
  .header-cell,
  .body-cell {
    font-size: 15px;

    &.col-name {
      width: 280px;
    }

    &.col-description {
      width: 320px;
    }

    &.col-progress {
      width: 120px;
    }

    &.col-date {
      width: 240px;
    }
  }
}

@media screen and (max-width: 1024px) {
  .video-main {
    margin-left: 0;
    padding: 40px 20px;
  }

  .page-title {
    font-size: 18px;
  }

  .lecture-table-container {
    overflow-x: auto;
  }

  .table-header:not(.mobile-hide) {
    min-width: 1200px;
  }
}

@media screen and (max-width: 768px) {
  .video-main {
    padding: 40px 20px;
  }

  .video-content {
    gap: 32px;
  }

  .page-title {
    font-size: 18px;
  }

  .lecture-count-badge {
    .label {
      font-size: 16px;
    }

    .count-pill {
      font-size: 16px;
      font-weight: 600;
    }
  }

  .lecture-table-container {
    padding-bottom: 16px;
  }

  .table-row {
    &:last-child {
      border-bottom: 2px solid #222;
    }
  }
}
</style>
