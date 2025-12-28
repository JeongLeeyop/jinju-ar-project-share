<template>
  <div>
    <!-- Create Space Modal -->
    <el-dialog
      :visible.sync="localVisible"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      custom-class="create-space-modal"
      @close="handleClose"
    >
      <div class="modal-content">
        <button class="modal-close-btn-main" @click="handleClose">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <h3 class="modal-title">커뮤니티 및 공간 개설을 진행하세요</h3>
        <p class="modal-subtitle">원하시는 커뮤니티 공간 유형을 선택해주세요</p>
        <div class="modal-buttons">
          <button class="modal-btn group-chat-btn" @click="handleGroupChat">
            그룹 채팅
          </button>
          <button class="modal-btn board-btn" @click="handleBoard">
            게시판
          </button>
        </div>
      </div>
    </el-dialog>

    <!-- Create Space Details Modal -->
    <el-dialog
      :visible.sync="detailsVisible"
      width="460px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      custom-class="create-space-modal-details"
    >
      <div class="modal-details-content">
        <button class="modal-close-btn" @click="closeDetailsModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-main-title">게시판 커뮤니티 공간을 만듭니다.</h3>

        <div class="form-section">
          <div class="form-group-spacing">
            <label class="form-label-large">커뮤니티 공간 이름</label>
            <div class="input-wrapper">
              <div class="input-icon"></div>
              <input
                v-model="spaceName"
                type="text"
                placeholder="커뮤니티 공간 이름"
                class="space-name-input"
                maxlength="7"
              />
            </div>
          </div>

          <div class="access-control-section">
            <label class="form-label-large">공간 접근</label>
            <div class="access-buttons">
              <button
                class="access-btn"
                :class="{ active: spaceAccess === 'public' }"
                @click="spaceAccess = 'public'"
              >
                공개
              </button>
              <button
                class="access-btn"
                :class="{ active: spaceAccess === 'private' }"
                @click="spaceAccess = 'private'"
              >
                비공개
              </button>
            </div>
            <p class="access-note">
              *커뮤니티 내 모든 회원들이 해당 그룹 채팅에 참여할 수 있습니다.<br>
              *초대된 회원만 해당 그룹 채팅에 참여할 수 있습니다.
            </p>
          </div>
        </div>

        <button class="create-submit-btn" @click="createSpace">
          게시판 공간 개설하기
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import { createSpace } from '@/api/space';

@Component({
  name: 'CreateSpaceModal',
})
export default class extends Vue {
  @Prop({ default: false }) visible!: boolean;

  private localVisible = false;
  private detailsVisible = false;
  private selectedSpaceType: 'group-chat' | 'board' = 'group-chat';
  private spaceName = '';
  private spaceAccess: 'public' | 'private' = 'public';
  private loading = false;

  @Watch('visible')
  private onVisibleChange(val: boolean) {
    this.localVisible = val;
  }

  @Watch('localVisible')
  private onLocalVisibleChange(val: boolean) {
    if (!val) {
      this.$emit('update:visible', false);
      this.$emit('close');
    }
  }

  private handleClose() {
    this.localVisible = false;
  }

  private handleGroupChat() {
    this.localVisible = false;
    this.selectedSpaceType = 'group-chat';
    this.detailsVisible = true;
  }

  private handleBoard() {
    this.localVisible = false;
    this.selectedSpaceType = 'board';
    this.detailsVisible = true;
  }

  private closeDetailsModal() {
    this.detailsVisible = false;
    this.spaceName = '';
    this.spaceAccess = 'public';
  }

  private async createSpace() {
    if (!this.spaceName.trim()) {
      this.$message.warning('커뮤니티 공간 이름을 입력해주세요.');
      return;
    }

    if (this.spaceName.trim().length > 7) {
      this.$message.warning('공간 이름은 최대 7자까지 가능합니다.');
      return;
    }

    if (this.loading) return;

    try {
      this.loading = true;

      // Get channelUid from route params
      const channelUid = this.$route.params.domain || '';
      if (!channelUid) {
        this.$message.error('커뮤니티 정보를 찾을 수 없습니다.');
        return;
      }

      // Call API to create space
      const response = await createSpace({
        channelUid: channelUid,
        name: this.spaceName.trim(),
        description: '',
        spaceType: this.selectedSpaceType === 'group-chat' ? 'CHAT' : 'BOARD',
        invitationRequired: this.spaceAccess === 'private',
        isPublic: this.spaceAccess === 'public',
        maxMembers: 0, // 0 = unlimited
      });

      this.$message.success('커뮤니티 공간이 개설되었습니다!');
      
      // Emit event to parent to refresh space list
      this.$emit('space-created', response.data);
      
      this.closeDetailsModal();
    } catch (error: any) {
      console.error('공간 생성 실패:', error);
      
      // 백엔드 에러 메시지 추출
      let errorMessage = '공간 개설에 실패했습니다.';
      
      if (error.response) {
        // 서버가 응답을 보낸 경우
        if (error.response.data && error.response.data.message) {
          errorMessage = error.response.data.message;
        } else if (error.response.statusText) {
          errorMessage = error.response.statusText;
        }
        console.log('Error response:', error.response.data);
      } else if (error.message) {
        // 네트워크 에러 등
        errorMessage = error.message;
      }
      
      this.$message.error(errorMessage);
    } finally {
      this.loading = false;
    }
  }
}
</script>

<style lang="scss">
// Global styles for modals (applied outside #app)
.el-dialog.create-space-modal {
  border-radius: 12px;

  .el-dialog__header {
    padding: 0;
  }

  .el-dialog__body {
    padding: 40px;
  }

  .el-dialog__close {
    font-size: 20px;
    color: #666;
  }
}

.el-dialog.create-space-modal-details {
  border-radius: 10px;

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

// Mobile fullscreen modal styles
@media (max-width: 768px) {
  .el-dialog.create-space-modal {
    width: 100% !important;
    max-width: 100% !important;
    margin: 0 !important;
    height: 100vh !important;
    max-height: 100vh !important;
    border-radius: 0 !important;
    display: flex !important;
    flex-direction: column !important;
    position: fixed !important;
    top: 0 !important;
    left: 0 !important;
    z-index: 2000 !important;

    .el-dialog__header {
      padding: 0 !important;
      display: none !important;
    }

    .el-dialog__body {
      flex: 1 !important;
      padding: 80px 20px 40px 20px !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      overflow-y: auto !important;
    }

    .el-dialog__close {
      display: none !important;
    }
  }

  .el-dialog.create-space-modal-details {
    width: 100% !important;
    max-width: 100% !important;
    margin: 0 !important;
    height: 100vh !important;
    max-height: 100vh !important;
    border-radius: 0 !important;
    display: flex !important;
    flex-direction: column !important;
    position: fixed !important;
    top: 0 !important;
    left: 0 !important;
    z-index: 2000 !important;

    .el-dialog__header {
      padding: 0 !important;
      display: none !important;
    }

    .el-dialog__body {
      flex: 1 !important;
      padding: 80px 20px 40px 20px !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      overflow-y: auto !important;
    }

    .el-dialog__close {
      display: none !important;
    }
  }
}

.el-dialog__wrapper {
  z-index: 3000 !important;
}
</style>

<style scoped lang="scss">

.modal-content {
  text-align: center;
  position: relative;

  @media (max-width: 768px) {
    width: 100%;
    max-width: 420px;
    padding: 0;
  }
}

.modal-close-btn-main {
  position: absolute;
  right: 0;
  top: 0;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #444;
  transition: color 0.2s;

  &:hover {
    color: #000;
  }

  @media (max-width: 768px) {
    position: fixed;
    top: 20px;
    right: 20px;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    z-index: 2001;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: rgba(255, 255, 255, 1);
      transform: scale(1.1);
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

.modal-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 140%;
  margin: 0 0 12px 0;
  letter-spacing: -0.5px;
}

.modal-subtitle {
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 140%;
  margin: 0 0 30px 0;
  letter-spacing: -0.3px;
}

.modal-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-btn {
  width: 100%;
  height: 50px;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  letter-spacing: -0.5px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &:active {
    transform: translateY(0);
  }
}

.group-chat-btn {
  background: #073DFF;
  color: #FFF;

  &:hover {
    background: #0535e6;
  }
}

.board-btn {
  background: #073DFF;
  color: #FFF;

  &:hover {
    background: #0535e6;
  }
}

.modal-details-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
  position: relative;

  @media (max-width: 768px) {
    width: 100%;
    max-width: 420px;
    gap: 30px;
    padding: 0;
  }
}

.modal-close-btn {
  position: absolute;
  right: 0;
  top: 0;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #444;
  transition: color 0.2s;

  &:hover {
    color: #000;
  }

  @media (max-width: 768px) {
    position: fixed;
    top: 20px;
    right: 20px;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    z-index: 2001;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: rgba(255, 255, 255, 1);
      transform: scale(1.1);
    }

    &:active {
      transform: scale(0.95);
    }
  }
}

.modal-main-title {
  color: #444;
  text-align: center;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;
  letter-spacing: -2px;

  @media (max-width: 768px) {
    font-size: 22px;
  }
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
}

.form-group-spacing {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-label-large {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
  text-align: left;

  @media (max-width: 768px) {
    font-size: 18px;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 52px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  padding: 0 16px;
  background: #FFF;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #073DFF;
  }
}

.input-icon {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #D9D9D9;
  flex-shrink: 0;
}

.space-name-input {
  flex: 1;
  border: none;
  outline: none;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  background: transparent;

  &::placeholder {
    color: #888;
  }
}

.access-control-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.access-buttons {
  display: flex;
  gap: 20px;

  @media (max-width: 768px) {
    gap: 12px;
  }
}

.access-btn {
  flex: 1;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #F5F5F5;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
  }

  &.active {
    background: #073DFF;
    border-color: #073DFF;
    color: #FFF;
  }
}

.access-note {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 150%;
  letter-spacing: 0;
  text-align: left;
  margin: 0;
}

.create-submit-btn {
  width: 100%;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #073DFF;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0535e6;
    border-color: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
}
</style>
