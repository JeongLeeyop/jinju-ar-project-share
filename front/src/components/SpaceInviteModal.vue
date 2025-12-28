<template>
  <el-dialog
    title="참여자 초대하기"
    :visible.sync="dialogVisible"
    width="600px"
    :before-close="handleClose"
    class="space-invite-modal"
  >
    <div class="invite-modal-content">
      <!-- 검색창 -->
      <el-input
        v-model="searchQuery"
        placeholder="이름 또는 이메일로 검색"
        prefix-icon="el-icon-search"
        clearable
        @input="handleSearch"
        class="search-input"
      />

      <!-- 초대 가능한 사용자 목록 -->
      <div class="invitable-users-section">
        <div class="section-header">
          <span class="section-title">초대 가능한 사용자</span>
          <span class="user-count">{{ filteredUsers.length }}명</span>
        </div>

        <div v-if="loading" class="loading-container">
          <i class="el-icon-loading"></i>
          <span>사용자 목록을 불러오는 중...</span>
        </div>

        <div v-else-if="filteredUsers.length === 0" class="empty-container">
          <p>초대 가능한 사용자가 없습니다.</p>
        </div>

        <div v-else class="users-list">
          <div
            v-for="user in filteredUsers"
            :key="user.uid"
            class="user-item"
            :class="{ selected: isSelected(user.uid) }"
            @click="toggleUser(user.uid)"
          >
            <el-checkbox
              :value="isSelected(user.uid)"
              @change="toggleUser(user.uid)"
              class="user-checkbox"
            />
            <div class="user-info">
              <div class="user-avatar">
                <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  <mask id="mask0" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                  </mask>
                  <g mask="url(#mask0)">
                    <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                    <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                  </g>
                </svg>
              </div>
              <div class="user-details">
                <span class="user-name">{{ user.actualName }}</span>
                <span class="user-email">{{ user.userId }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 선택된 사용자 -->
      <div v-if="selectedUserUids.length > 0" class="selected-users-section">
        <div class="section-header">
          <span class="section-title">선택된 사용자</span>
          <span class="user-count">{{ selectedUserUids.length }}명</span>
        </div>
        <div class="selected-users-chips">
          <el-tag
            v-for="uid in selectedUserUids"
            :key="uid"
            closable
            @close="removeUser(uid)"
            class="user-chip"
          >
            {{ getUserName(uid) }}
          </el-tag>
        </div>
      </div>
    </div>

    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">취소</el-button>
      <el-button
        type="primary"
        :loading="inviting"
        :disabled="selectedUserUids.length === 0"
        @click="handleInvite"
      >
        초대하기 ({{ selectedUserUids.length }})
      </el-button>
    </span>
  </el-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import { getInvitableUsers, inviteMultipleUsers } from '@/api/space';

interface InvitableUser {
  uid: string;
  actualName: string;
  userId: string;
}

@Component({
  name: 'SpaceInviteModal',
})
export default class extends Vue {
  @Prop({ required: true }) visible!: boolean;
  @Prop({ required: true }) spaceUid!: string;

  private dialogVisible = false;
  private loading = false;
  private inviting = false;
  private searchQuery = '';
  private users: InvitableUser[] = [];
  private selectedUserUids: string[] = [];

  get filteredUsers(): InvitableUser[] {
    if (!this.searchQuery.trim()) {
      return this.users;
    }

    const query = this.searchQuery.toLowerCase();
    return this.users.filter(
      (user) =>
        user.actualName.toLowerCase().includes(query) ||
        user.userId.toLowerCase().includes(query),
    );
  }

  @Watch('visible')
  onVisibleChange(newVal: boolean) {
    console.log('SpaceInviteModal: visible changed to', newVal);
    this.dialogVisible = newVal;
    if (newVal) {
      this.loadUsers();
    } else {
      this.resetModal();
    }
  }

  @Watch('dialogVisible')
  onDialogVisibleChange(newVal: boolean) {
    console.log('SpaceInviteModal: dialogVisible changed to', newVal);
    if (!newVal) {
      this.$emit('update:visible', false);
    }
  }

  private async loadUsers() {
    this.loading = true;
    try {
      console.log('SpaceInviteModal: Loading invitable users for space', this.spaceUid);
      const response = await getInvitableUsers(this.spaceUid);
      this.users = response.data;
      console.log('SpaceInviteModal: Loaded users', this.users.length);
    } catch (error: any) {
      console.error('SpaceInviteModal: Failed to load users', error);
      const message = error.response?.data?.message || '사용자 목록을 불러오는데 실패했습니다';
      this.$message.error(message);
    } finally {
      this.loading = false;
    }
  }

  private handleSearch() {
    // 검색은 computed property에서 처리
  }

  private isSelected(uid: string): boolean {
    return this.selectedUserUids.includes(uid);
  }

  private toggleUser(uid: string) {
    const index = this.selectedUserUids.indexOf(uid);
    if (index > -1) {
      this.selectedUserUids.splice(index, 1);
    } else {
      this.selectedUserUids.push(uid);
    }
  }

  private removeUser(uid: string) {
    const index = this.selectedUserUids.indexOf(uid);
    if (index > -1) {
      this.selectedUserUids.splice(index, 1);
    }
  }

  private getUserName(uid: string): string {
    const user = this.users.find((u) => u.uid === uid);
    return user ? user.actualName : '';
  }

  private async handleInvite() {
    if (this.selectedUserUids.length === 0) {
      this.$message.warning('초대할 사용자를 선택해주세요');
      return;
    }

    this.inviting = true;
    try {
      console.log('SpaceInviteModal: Inviting users', this.selectedUserUids);
      await inviteMultipleUsers(this.spaceUid, this.selectedUserUids);
      this.$message.success(`${this.selectedUserUids.length}명의 사용자를 초대했습니다`);
      this.$emit('invited');
      this.handleClose();
    } catch (error: any) {
      console.error('SpaceInviteModal: Failed to invite users', error);
      const message = error.response?.data?.message || '초대에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.inviting = false;
    }
  }

  private handleClose() {
    console.log('SpaceInviteModal: Closing modal');
    this.dialogVisible = false;
  }

  private resetModal() {
    this.searchQuery = '';
    this.selectedUserUids = [];
    this.users = [];
  }
}
</script>

<style scoped lang="scss">
.space-invite-modal {
  .invite-modal-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-height: 500px;
  }

  .search-input {
    margin-bottom: 10px;
  }

  .invitable-users-section,
  .selected-users-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid #e8e8e8;

      .section-title {
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }

      .user-count {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .invitable-users-section {
    .loading-container,
    .empty-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px 20px;
      color: #999;
      gap: 8px;

      i {
        font-size: 24px;
      }
    }

    .users-list {
      max-height: 300px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 8px;

      .user-item {
        display: flex;
        align-items: center;
        padding: 12px;
        border: 1px solid #e8e8e8;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          background-color: #f5f7fa;
          border-color: #073dff;
        }

        &.selected {
          background-color: #e6f0ff;
          border-color: #073dff;
        }

        .user-checkbox {
          margin-right: 12px;
        }

        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;
          flex: 1;

          .user-avatar {
            flex-shrink: 0;
          }

          .user-details {
            display: flex;
            flex-direction: column;
            gap: 4px;
            min-width: 0;

            .user-name {
              text-align: left;
              font-size: 14px;
              font-weight: 500;
              color: #333;
            }

            .user-email {
              font-size: 12px;
              color: #999;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
        }
      }
    }
  }

  .selected-users-section {
    padding-top: 16px;
    border-top: 1px solid #e8e8e8;

    .selected-users-chips {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .user-chip {
        font-size: 13px;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}
</style>
