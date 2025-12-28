<template>
  <div class="community-chat-page">
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="spaceId" @space-select="handleSpaceSelect" />

    <!-- Main Chat Area -->
    <div class="community-main chat-main">
      <div class="space-header">
        <div class="space-name-tag">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="8" cy="8" r="8" :fill="spaceColor" />
          </svg>
          <span>{{ spaceName }}</span>
          <span class="space-visibility-badge" :class="{ 'public-badge': isPublic, 'private-badge': !isPublic }">
            {{ isPublic ? '공개' : '비공개' }}
          </span>
        </div>
        <button class="toggle-participants-btn" @click="toggleParticipants">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 5V19M5 12C10.4673 12 13.5327 12 19 12" stroke="black" stroke-width="1.5"
              stroke-linecap="round" />
          </svg>
        </button>
      </div>

      <div class="chat-container">
        <!-- Notice Section (Collapsible) -->
        <div class="notice-section" :class="{ collapsed: noticeCollapsed }" v-if="currentNotice">
          <div class="notice-content">
            <p class="notice-text" ref="noticeText">{{ currentNotice.text }}</p>
            <button v-if="isNoticeOverflow" class="toggle-btn" @click="noticeCollapsed = !noticeCollapsed">
              <svg width="24" height="28" viewBox="0 0 24 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 10L12.5 17.5L5 10" stroke="black" stroke-width="1.5" stroke-linecap="round"
                  stroke-linejoin="round" />
              </svg>
            </button>
          </div>
          <div class="notice-meta">
            <span class="notice-author">{{ currentNotice.userName }}</span>
            <span class="notice-date">{{ formatNoticeDate(currentNotice.createdAt) }}</span>
            <button v-if="isAdmin" class="notice-delete-btn" @click="handleDeleteNotice">삭제</button>
          </div>
        </div>

        <!-- Chat Messages -->
        <div class="chat-messages" ref="chatMessages">
          <div v-if="isLoading" class="loading-indicator">
            <i class="el-icon-loading"></i> 메시지 불러오는 중...
          </div>
          <div v-else-if="messages.length === 0" class="no-messages">
            아직 메시지가 없습니다. 첫 번째 메시지를 보내보세요!
          </div>
          <div v-else v-for="message in messages" :key="message.id" class="message-wrapper"
            :class="{ 'message-sent': message.isSent, 'message-received': !message.isSent }">
            <div v-if="!message.isSent" class="message-left">
              <div class="message-header">
                <div class="user-avatar">
                  <img v-if="message.userAvatar" :src="`${apiUrl}/attached-file/${message.userAvatar}`" alt="avatar"
                    class="user-avatar-img" />
                  <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                    <mask :id="`mask-${message.id}`" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0"
                      width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                    </mask>
                    <g :mask="`url(#mask-${message.id})`">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5" />
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5" />
                    </g>
                  </svg>
                </div>
                <span class="user-name">{{ message.userName }}</span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-bubble" @contextmenu.prevent="showContextMenu($event, message)">
                <p class="message-text">{{ message.text }}</p>
                <div class="bubble-tail left-tail"></div>
              </div>
            </div>

            <div v-else class="message-right">
              <span class="message-time">{{ message.time }}</span>
              <div class="message-bubble sent-bubble" @contextmenu.prevent="showContextMenu($event, message)">
                <p class="message-text">{{ message.text }}</p>
                <div class="bubble-tail right-tail"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Message Input -->
        <div class="message-input-section">
          <input ref="messageInput" type="text" class="message-input" placeholder="채팅메세지를 작성하세요!" v-model="newMessage"
            @keyup.enter="sendMessage" :disabled="isSending" />
          <button class="send-btn" @click="sendMessage" :disabled="isSending || !newMessage.trim()">
            {{ isSending ? '전송 중...' : '채팅입력' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Participants Panel Overlay on Mobile -->
    <div class="participants-overlay" v-if="showParticipants" @click="toggleParticipants"></div>

    <div class="participants-wrapper" :class="{ 'show': showParticipants }">
      <button class="close-participants-btn" @click="toggleParticipants">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round"
            stroke-linejoin="round" />
        </svg>
      </button>
      <ParticipantsList :participants="participants" :participantCount="participantCount" :isAdmin="isAdmin"
        :isPublic="isPublic" @invite="openInviteModal" />
    </div>

    <!-- Context Menu -->
    <div v-if="contextMenu.visible" class="context-menu"
      :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }" @click.stop>
      <div class="context-menu-item" @click="setAsNotice" v-if="isAdmin">
        <i class="el-icon-bell"></i> 공지로 등록
      </div>
      <div class="context-menu-item" @click="copyMessage">
        <i class="el-icon-document-copy"></i> 복사
      </div>
      <div class="context-menu-item delete" @click="deleteMessage"
        v-if="contextMenu.message && (contextMenu.message.isSent || isAdmin)">
        <i class="el-icon-delete"></i> 삭제
      </div>
    </div>
    <div v-if="contextMenu.visible" class="context-menu-overlay" @click="hideContextMenu"></div>

    <!-- Invite Modal -->
    <SpaceInviteModal :visible.sync="inviteModalVisible" :space-uid="spaceId" @invited="handleInvited" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { UserModule } from '@/store/modules/user';
import chatService, { ChatMessage, ChatNotice } from '@/api/chatService';
import CommunitySidebar from './components/communitySidebar.vue';
import ParticipantsList from './components/ParticipantsList.vue';
import SpaceInviteModal from '@/components/SpaceInviteModal.vue';
import { getSpace, getSpaceMembers } from '@/api/space';

interface DisplayMessage {
  id: string;
  userName: string;
  time: string;
  text: string;
  isSent: boolean;
  userId: string;
  userAvatar?: string;
  createdAt?: Date; // 원본 timestamp 저장
}

interface Participant {
  id: string | number;
  name: string;
  avatar?: string;
}

@Component({
  name: 'CommunityChat',
  components: {
    CommunitySidebar,
    ParticipantsList,
    SpaceInviteModal,
  },
})
export default class CommunityChat extends Vue {
  private spaceId = '';
  private spaceName = '';
  private spaceColor = '#FF5858';
  private noticeCollapsed = false;
  private newMessage = '';
  private participantCount = 0;
  private messages: DisplayMessage[] = [];
  private unsubscribeMessages: (() => void) | null = null;
  private unsubscribeNotice: (() => void) | null = null;
  private isLoading = false;
  private isSending = false;
  private showParticipants = false;
  private isAdmin = false;
  private isPublic = false;
  private currentNotice: ChatNotice | null = null;
  private participants: Participant[] = [];
  private currentUserIconFileUid: string | null = null;  // 현재 사용자 아이콘 UID

  private isLoadingMore = false;
  private hasMoreMessages = true;
  private messageLimit = 10;
  private isInitialLoad = true;
  private isNoticeOverflow = false;

  private contextMenu = {
    visible: false,
    x: 0,
    y: 0,
    message: null as DisplayMessage | null,
  };

  // 초대 모달 관련
  private inviteModalVisible = false;

  private get currentUser() {
    return {
      id: UserModule.userId || '',
      name: UserModule.actualName || UserModule.userId || '',
      avatar: this.currentUserIconFileUid || '',  // iconFileUid 사용
    };
  }

  // ✅ apiUrl을 computed getter로 변경하여 템플릿에서 접근 가능하도록 수정
  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  async mounted() {
    this.spaceId = this.$route.params.spaceId || '';
    this.spaceName = this.$route.query.spaceName as string || '';

    // 현재 사용자 정보 로드 (iconFileUid 포함)
    await this.loadCurrentUserInfo();

    if (this.spaceId) {
      await this.initializeChat();
    }

    document.addEventListener('click', this.hideContextMenu);

    this.$nextTick(() => {
      const chatMessages = this.$refs.chatMessages as HTMLElement;
      if (chatMessages) {
        chatMessages.addEventListener('scroll', this.handleScroll);
      }
    });
  }

  beforeDestroy() {
    if (this.unsubscribeMessages) {
      this.unsubscribeMessages();
    }
    if (this.unsubscribeNotice) {
      this.unsubscribeNotice();
    }
    document.removeEventListener('click', this.hideContextMenu);

    const chatMessages = this.$refs.chatMessages as HTMLElement;
    if (chatMessages) {
      chatMessages.removeEventListener('scroll', this.handleScroll);
    }
  }

  @Watch('$route.params.spaceId')
  private async onSpaceIdChange(newSpaceId: string) {
    if (newSpaceId && newSpaceId !== this.spaceId) {
      if (this.unsubscribeMessages) {
        this.unsubscribeMessages();
      }
      if (this.unsubscribeNotice) {
        this.unsubscribeNotice();
      }

      this.spaceId = newSpaceId;
      this.spaceName = this.$route.query.spaceName as string || '';
      await this.initializeChat();
    }
  }

  /**
   * 현재 사용자 정보 로드 (iconFileUid 포함)
   */
  private async loadCurrentUserInfo() {
    try {
      const { getUserInfo } = await import('@/api/user');
      const response = await getUserInfo();
      if (response.data && response.data.iconFileUid) {
        this.currentUserIconFileUid = response.data.iconFileUid;
      }
    } catch (error) {
      console.error('Failed to load user info:', error);
      // 에러 발생해도 계속 진행
    }
  }

  private async initializeChat() {
    try {
      this.isLoading = true;
      this.messages = [];

      await chatService.getOrCreateChatSpace(
        this.spaceId,
        this.spaceName || '채팅 공간',
        this.spaceColor,
        this.currentUser.id,
      );

      this.unsubscribeMessages = chatService.onMessagesChange(
        this.spaceId,
        (firebaseMessages: ChatMessage[]) => {
          console.log('Received messages from Firebase:', firebaseMessages.length, firebaseMessages);

          // 초기 로딩인 경우에만 메시지 전체 교체
          if (this.isInitialLoad) {
            this.messages = firebaseMessages.map((msg) => this.convertToDisplayMessage(msg));
            console.log('Converted messages:', this.messages.length, this.messages);
            this.isInitialLoad = false;
            // 초기 로딩 시 즉시 맨 아래로 스크롤 (애니메이션 없이)
            this.$nextTick(() => {
              setTimeout(() => {
                this.scrollToBottom(false);
              }, 100);
            });
          } else {
            // 새 메시지가 추가된 경우 (실시간 업데이트)
            // 기존 메시지와 비교하여 새로운 메시지만 추가
            const existingIds = new Set(this.messages.map(m => m.id));
            const newMessages = firebaseMessages.filter(msg => !existingIds.has(msg.id));

            if (newMessages.length > 0) {
              const convertedNew = newMessages.map((msg) => this.convertToDisplayMessage(msg));
              this.messages = [...this.messages, ...convertedNew];
              this.$nextTick(() => {
                this.scrollToBottom();
              });
            }
          }
        },
        this.messageLimit,
      );

      this.unsubscribeNotice = chatService.onNoticeChange(
        this.spaceId,
        (notice: ChatNotice | null) => {
          this.currentNotice = notice;
          // 공지사항이 변경되면 오버플로우 체크
          this.$nextTick(() => {
            this.checkNoticeOverflow();
          });
        },
      );

      await this.loadChatSpaceInfo();

    } catch (error) {
      this.$message.error('채팅을 불러오는데 실패했습니다.');
    } finally {
      this.isLoading = false;
      // 로딩 완료 후 맨 아래로 스크롤
      this.$nextTick(() => {
        setTimeout(() => {
          this.scrollToBottom(false);
        }, 200);
      });
    }
  }

  private async loadChatSpaceInfo() {
    try {
      // Firebase 채팅 공간 정보 로드
      const spaceInfo = await chatService.getChatSpace(this.spaceId);
      if (spaceInfo) {
        if (!this.spaceName) {
          this.spaceName = spaceInfo.name;
        }
        this.spaceColor = spaceInfo.color || '#FF5858';
        this.participantCount = spaceInfo.participants?.length || 0;

        if (spaceInfo.notice) {
          this.currentNotice = spaceInfo.notice;
          // 공지사항 로드 후 오버플로우 체크
          this.$nextTick(() => {
            this.checkNoticeOverflow();
          });
        }
      }

      // 백엔드 공간 정보 로드 (isPublic, isAdmin 등)
      try {
        const response = await getSpace(this.spaceId);
        if (response.data) {
          this.isPublic = response.data.isPublic || false;
          // 백엔드에서 받은 isAdmin 값 사용 (커뮤니티 관리자 포함)
          this.isAdmin = response.data.isAdmin || false;

          console.log('Space info loaded:', {
            spaceId: this.spaceId,
            isAdmin: this.isAdmin,
            isPublic: this.isPublic,
          });
        }
      } catch (error) {
        console.error('Failed to load space info from backend:', error);
        // 백엔드에서 공간 정보를 가져오지 못한 경우 Firebase 정보로 fallback
        if (spaceInfo) {
          this.isAdmin = spaceInfo.createdBy === this.currentUser.id;
        }
      }

      // 참여자 목록 로드
      await this.loadParticipants();
    } catch (error) {
      console.error('Failed to load chat space info:', error);
      // 에러 무시
    }
  }

  private async loadParticipants() {
    try {
      const response = await getSpaceMembers(this.spaceId);
      if (response.data && Array.isArray(response.data)) {
        this.participants = response.data.map((member: any) => ({
          id: member.uid,
          name: member.actualName || member.name || '익명',
          avatar: member.profileImage || member.avatar,
        }));
        this.participantCount = this.participants.length;
      }
    } catch (error) {
      console.error('Failed to load participants:', error);
      // 에러 시 빈 배열 유지
    }
  }

  private checkNoticeOverflow() {
    const noticeText = this.$refs.noticeText as HTMLElement;
    if (noticeText) {
      // 텍스트가 2줄을 초과하는지 체크 (scrollHeight > clientHeight)
      this.isNoticeOverflow = noticeText.scrollHeight > noticeText.clientHeight;
    } else {
      this.isNoticeOverflow = false;
    }
  }

  private convertToDisplayMessage(msg: ChatMessage): DisplayMessage {
    const createdAtDate = msg.createdAt || new Date();
    return {
      id: msg.id,
      userName: msg.userName,
      time: this.formatTime(createdAtDate),
      text: msg.text,
      isSent: msg.userId === this.currentUser.id,
      userId: msg.userId,
      userAvatar: msg.userAvatar,
      createdAt: createdAtDate instanceof Date ? createdAtDate : new Date(createdAtDate),
    };
  }

  private handleSpaceSelect(spaceId: string) {
    this.$router.push({
      name: 'CommunityChat',
      params: {
        domain: this.$route.params.domain,
        spaceId,
      },
    });
  }

  private async sendMessage() {
    if (!this.newMessage.trim() || this.isSending) return;

    try {
      this.isSending = true;
      const messageText = this.newMessage.trim();
      this.newMessage = '';

      await chatService.sendMessage(
        this.spaceId,
        this.currentUser.id,
        this.currentUser.name,
        messageText,
        this.currentUser.avatar,
      );

    } catch (error) {
      this.$message.error('메시지 전송에 실패했습니다.');
    } finally {
      this.isSending = false;

      // 메시지 전송 완료 후 입력창에 포커스
      this.$nextTick(() => {
        setTimeout(() => {
          const input = this.$refs.messageInput as HTMLInputElement;
          if (input) {
            input.focus();
          }
        }, 50);
      });
    }
  }

  private scrollToBottom(smooth: boolean = true) {
    const scrollAction = () => {
      const chatMessages = this.$refs.chatMessages as HTMLElement;
      if (chatMessages) {
        if (smooth) {
          chatMessages.scrollTo({
            top: chatMessages.scrollHeight,
            behavior: 'smooth',
          });
        } else {
          // 즉시 스크롤 (초기 로딩 시)
          chatMessages.scrollTop = chatMessages.scrollHeight;
        }
      }
    };

    // DOM 업데이트 완료 후 스크롤
    this.$nextTick(() => {
      scrollAction();
      // 이미지 등 비동기 컨텐츠 로딩을 위해 추가 딜레이
      setTimeout(scrollAction, 50);
      setTimeout(scrollAction, 150);
    });
  }

  private formatTime(date: Date): string {
    const now = new Date();
    const messageDate = new Date(date);

    const hours = messageDate.getHours();
    const minutes = messageDate.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHours = hours % 12 || 12;
    const displayMinutes = minutes < 10 ? `0${minutes}` : minutes;

    if (
      now.getFullYear() === messageDate.getFullYear() &&
      now.getMonth() === messageDate.getMonth() &&
      now.getDate() === messageDate.getDate()
    ) {
      return `${ampm} ${displayHours}:${displayMinutes}`;
    }
    const month = messageDate.getMonth() + 1;
    const day = messageDate.getDate();
    return `${month}/${day} ${ampm} ${displayHours}:${displayMinutes}`;
  }

  private formatNoticeDate(date: Date | any): string {
    if (!date) return '';

    let d: Date;

    // Firestore Timestamp 객체 처리
    if (date && typeof date.toDate === 'function') {
      d = date.toDate();
    } else if (date && typeof date.seconds === 'number') {
      // Firestore Timestamp 형식 (seconds, nanoseconds)
      d = new Date(date.seconds * 1000);
    } else {
      d = new Date(date);
    }

    // 유효한 날짜인지 확인
    if (isNaN(d.getTime())) return '';

    const year = d.getFullYear();
    const month = d.getMonth() + 1;
    const day = d.getDate();

    return `${year}.${month}.${day}`;
  }

  private openInviteModal() {
    // 공개 공간인 경우 초대 불가
    if (this.isPublic) {
      this.$message.info('공개 공간은 초대 기능을 사용할 수 없습니다. 커뮤니티의 모든 멤버가 자동으로 참여합니다.');
      return;
    }

    // 관리자가 아닌 경우 초대 불가
    if (!this.isAdmin) {
      this.$message.warning('공간 관리자만 멤버를 초대할 수 있습니다.');
      return;
    }

    console.log('openInviteModal called', {
      spaceId: this.spaceId,
      isAdmin: this.isAdmin,
      isPublic: this.isPublic,
    });
    this.inviteModalVisible = true;
  }

  private async handleInvited() {
    this.$message.success('초대가 완료되었습니다.');
    this.inviteModalVisible = false;
    await this.loadParticipants();
  }

  private handleInvite() {
    // deprecated - openInviteModal 사용
    this.openInviteModal();
  }

  private toggleParticipants() {
    this.showParticipants = !this.showParticipants;
  }

  private showContextMenu(event: MouseEvent, message: DisplayMessage) {
    event.preventDefault();
    this.contextMenu = {
      visible: true,
      x: event.clientX,
      y: event.clientY,
      message,
    };
  }

  private hideContextMenu() {
    this.contextMenu.visible = false;
  }

  private copyMessage() {
    if (this.contextMenu.message) {
      navigator.clipboard.writeText(this.contextMenu.message.text)
        .then(() => {
          this.$message.success('메시지가 복사되었습니다.');
        })
        .catch(() => {
          this.$message.error('메시지 복사에 실패했습니다.');
        });
    }
    this.hideContextMenu();
  }

  private async deleteMessageAction() {
    if (!this.contextMenu.message) return;

    try {
      await chatService.deleteMessage(this.contextMenu.message.id);
      this.$message.success('메시지가 삭제되었습니다.');
    } catch (error) {
      this.$message.error('메시지 삭제에 실패했습니다.');
    }
    this.hideContextMenu();
  }

  private deleteMessage() {
    if (!this.contextMenu.message) return;

    this.$confirm('이 메시지를 삭제하시겠습니까?', '메시지 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    })
      .then(() => this.deleteMessageAction())
      .catch(() => { });
  }

  private async setAsNotice() {
    if (!this.contextMenu.message) return;

    this.$confirm('이 메시지를 공지로 등록하시겠습니까?', '공지 등록', {
      confirmButtonText: '등록',
      cancelButtonText: '취소',
      type: 'info',
    })
      .then(async () => {
        try {
          const msg = this.contextMenu.message!;
          await chatService.setNotice(
            this.spaceId,
            msg.userId,
            msg.userName,
            msg.text,
          );
          this.$message.success('공지로 등록되었습니다.');
        } catch (error) {
          this.$message.error('공지 등록에 실패했습니다.');
        }
      })
      .catch(() => { });

    this.hideContextMenu();
  }

  private async handleDeleteNotice() {
    this.$confirm('공지사항을 삭제하시겠습니까?', '공지 삭제', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning',
    })
      .then(async () => {
        try {
          await chatService.deleteNotice(this.spaceId);
          this.currentNotice = null;
          this.$message.success('공지가 삭제되었습니다.');
        } catch (error) {
          this.$message.error('공지 삭제에 실패했습니다.');
        }
      })
      .catch(() => { });
  }

  private handleScroll() {
    const chatMessages = this.$refs.chatMessages as HTMLElement;
    if (!chatMessages) return;

    if (chatMessages.scrollTop < 100 && !this.isLoadingMore && this.hasMoreMessages) {
      this.loadMoreMessages();
    }
  }

  private async loadMoreMessages() {
    if (this.isLoadingMore || !this.hasMoreMessages || this.messages.length === 0) return;

    try {
      this.isLoadingMore = true;
      const chatMessages = this.$refs.chatMessages as HTMLElement;
      const previousScrollHeight = chatMessages?.scrollHeight || 0;

      // 가장 오래된 메시지의 createdAt timestamp를 기준으로 이전 메시지 로드
      const oldestMessage = this.messages[0];
      const oldestTimestamp = oldestMessage?.createdAt;

      const olderMessages = await chatService.getOlderMessages(
        this.spaceId,
        oldestMessage?.id || null,
        this.messageLimit,
        oldestTimestamp,
      );

      if (olderMessages.length < this.messageLimit) {
        this.hasMoreMessages = false;
      }

      if (olderMessages.length > 0) {
        // 중복 제거
        const existingIds = new Set(this.messages.map(m => m.id));
        const uniqueOlderMessages = olderMessages.filter(msg => !existingIds.has(msg.id));

        if (uniqueOlderMessages.length > 0) {
          const convertedMessages = uniqueOlderMessages.map((msg: ChatMessage) => this.convertToDisplayMessage(msg));
          this.messages = [...convertedMessages, ...this.messages];

          this.$nextTick(() => {
            if (chatMessages) {
              const newScrollHeight = chatMessages.scrollHeight;
              chatMessages.scrollTop = newScrollHeight - previousScrollHeight;
            }
          });
        }
      }
    } catch (error) {
      console.error('Failed to load more messages:', error);
    } finally {
      this.isLoadingMore = false;
    }
  }
}
</script>

<style scoped lang="scss">
.community-chat-page {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  min-height: calc(100vh - 120px);
  background: #F8F9FB;
}

.chat-main {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  margin: 120px 0 0 270px;
  min-width: 0;
  padding: 0px 0px 80px 40px;
}

.space-header {
  position: relative;
  padding: 40px 0;
  text-align: left;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.space-name-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 20px;

  svg {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
  }

  span {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 20px;
  }

  .space-visibility-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
    margin-left: 8px;

    &.public-badge {
      background-color: #E8F5E9;
      color: #2E7D32;
    }

    &.private-badge {
      background-color: #FFF3E0;
      color: #E65100;
    }
  }
}

.toggle-participants-btn {
  display: none;
  width: 24px;
  height: 24px;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  transition: opacity 0.2s;
  flex-shrink: 0;

  &:hover {
    opacity: 0.7;
  }

  svg {
    width: 100%;
    height: 100%;
  }
}

.participants-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(17, 17, 17, 0.5);
  z-index: 999;
}

.participants-wrapper {
  position: relative;
}

.close-participants-btn {
  display: none;
  position: absolute;
  right: 16px;
  top: 30px;
  width: 24px;
  height: 24px;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  z-index: 10;
  color: #444;
  transition: color 0.2s;

  &:hover {
    color: #000;
  }

  svg {
    width: 100%;
    height: 100%;
  }
}

.chat-container {
  flex: 1;
  width: 100%;
  padding: 40px 40px 20px;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  border: 2px solid #EBEBEB;
  box-sizing: border-box;
  background: #FFF;
}

.notice-section {
  width: 100%;
  padding: 10px 40px;
  border: 2px solid #EBEBEB;
  border-radius: 50px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  position: relative;
  min-height: 80px;
  background: #EBEBEB;

  &.collapsed {
    .notice-text {
      -webkit-line-clamp: 1;
      line-clamp: 1;
    }

    .toggle-btn svg {
      transform: rotate(180deg);
    }
  }
}

.notice-badge {
  position: absolute;
  right: -4px;
  top: -4px;
  width: 100px;
  height: 38px;
}

.badge-text {
  position: absolute;
  left: 19px;
  top: 9px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.notice-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.notice-text {
  flex: 1;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
}

.toggle-btn {
  width: 24px;
  height: 24px;
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  flex-shrink: 0;
  transition: transform 0.3s;
  margin-left: auto;

  svg {
    width: 100%;
    height: 100%;
  }

  &:hover {
    opacity: 0.7;
  }
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}

.notice-author {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 100%;
}

.notice-date {
  color: #CECECE;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 100%;
}

.notice-delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 10px;
  background: #FF5858;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #FF3B3B;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 0 20px 20px 0;
  display: flex;
  flex-direction: column;
  gap: 40px;
  min-height: 0;
  max-height: calc(100vh - 350px);

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #F5F5F5;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #CECECE;
    border-radius: 3px;

    &:hover {
      background: #999;
    }
  }
}

.loading-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

.message-wrapper {
  width: 100%;
}

.message-left {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-start;
  max-width: 70%;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;

  .user-avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.user-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.message-time {
  color: #CECECE;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
}

.message-bubble {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 16px 20px;
  border-radius: 10px;
  background: #EBEBEB;
  position: relative;
}

.sent-bubble {
  background: #FFEB9A;
}

.message-text {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 130%;
  margin: 0;
  white-space: pre-line;
  overflow-wrap: break-word;
}

.bubble-tail {
  width: 12.5px;
  height: 22.679px;
  position: absolute;
  top: 9px;
}

.left-tail {
  left: -6px;
  background: #EBEBEB;
  clip-path: polygon(100% 0%, 0% 0%, 66% 42%, 52% 100%, 48% 88%);
}

.right-tail {
  right: -5.5px;
  background: #FFEB9A;
  clip-path: polygon(0% 0%, 100% 0%, 34% 42%, 48% 100%, 52% 88%);
}

.message-right {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  gap: 10px;
  justify-content: flex-end;
  margin-left: auto;
  max-width: 70%;

  .message-time {
    align-self: flex-end;
    padding-bottom: 16px;
  }
}

.message-input-section {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
  padding-top: 30px;
  border-top: 2px solid #EBEBEB;
}

.message-input {
  width: 100%;
  flex: 0 1 calc(100% - 156px);
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: clamp(16px, 2vw, 18px);
  font-weight: 400;
  line-height: 20px;
  outline: none;
  transition: border-color 0.2s;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }
}

.send-btn {
  flex: 0 1 136px;
  height: 52px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;
  flex-shrink: 0;

  &:hover {
    background: #0530CC;
  }

  &:active {
    background: #042099;
  }
}

.context-menu {
  position: absolute;
  z-index: 1000;
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.context-menu-item {
  padding: 10px 20px;
  cursor: pointer;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
  transition: background 0.2s;

  &:hover {
    background: #F5F5F5;
  }

  i {
    margin-right: 8px;
  }
}

.context-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

@media screen and (max-width: 1800px) {
  .chat-container {
    max-width: 1000px;
  }
}

@media screen and (max-width: 1400px) {
  .chat-container {
    max-width: 900px;
  }
}

@media screen and (max-width: 1200px) {
  .community-chat-page {
    gap: 20px;
  }

  .chat-main {
    padding: 0px 0px 80px 20px
  }

  .chat-container {
    max-width: 800px;
    padding: 20px;
  }

  .message-input-section {
    gap: 10px;
    padding-top: 20px;
  }

  .message-input {
    font-size: 16px;
    height: 45px;
    flex: 0 1 calc(100% - 110px);
    padding: 0 10px;
  }

  .send-btn {
    height: 45px;
    flex: 0 1 100px;
    font-size: 16px;
  }
}

@media screen and (max-width: 1024px) {
  .chat-main {
    margin: 120px 0 0 240px;
  }

  .space-header {
    padding: 30px 0px 20px;
    // border-left: 2px solid #EBEBEB;
    // border-right: 2px solid #EBEBEB;
  }

  .chat-container {
    padding: 20px;
    border-left: 2px solid #EBEBEB;
    border-right: 2px solid #EBEBEB;
  }

  .message-left,
  .message-right {
    max-width: 85%;
  }

  .message-input-section { 
    gap: 10px;
  }
}

@media screen and (max-width: 768px) {
  .community-chat-page {
    display: block;
    padding: 100px 0 20px;
    position: relative;
  }

  .chat-main {
    padding: 0 20px;
    margin: 0;
    border: none;
  }

  .space-header {
    padding: 16px 20px;
  }

  .space-name-tag {
    span {
      font-size: 16px;
    }

    svg {
      width: 14px;
      height: 14px;
    }

    .space-visibility-badge {
      font-size: 10px;
      padding: 3px 8px;
      margin-left: 6px;
    }
  }

  .toggle-participants-btn {
    display: flex;
  }

  .participants-overlay {
    display: block;
  }

  .participants-wrapper {
    position: fixed;
    top: 0;
    right: -100%;
    bottom: 0;
    width: 267px;
    background: #FFF;
    border-left: 2px solid #EBEBEB;
    z-index: 1000;
    transition: right 0.3s ease-in-out;
    box-shadow: -4px 0 12px rgba(0, 0, 0, 0.1);
    overflow-y: auto;

    &.show {
      right: 0;
    }
  }

  .close-participants-btn {
    display: flex;
  }

  .chat-container {
    flex: unset;
    max-height: calc(100vh - 196px);
    padding: 20px;
  }

  .notice-section {
    padding: 30px 20px 14px;
    border-radius: 10px;
    margin-bottom: 20px;
    min-height: 80px;
  }

  .notice-badge {
    width: 80px;
    height: 30px;
    right: 0;
    top: -2px;

    svg {
      width: 80px;
      height: 30px;
    }
  }

  .badge-text {
    font-size: 16px;
    left: 14px;
    top: 6px;
  }

  .notice-text {
    font-size: 12px;
    line-height: 150%;
  }

  .toggle-btn {
    width: 16px;
    height: 16px;

    svg {
      width: 16px;
      height: 16px;
    }
  }

  .chat-messages {
    gap: 30px;
  }

  .message-left,
  .message-right {
    max-width: 90%;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
  }

  .user-name {
    font-size: 12px;
  }

  .message-time {
    font-size: 12px;
  }

  .message-bubble {
    padding: 8px 16px;
  }

  .message-text {
    font-size: 12px;
    line-height: 150%;
  }

  .message-input-section {
    padding-top: 20px;
    gap: 4px;
  }

  .message-input {
    font-size: 16px;
    height: 52px;
    padding: 16px;
  }

  .send-btn {
    width: auto;
    height: 45px;
    padding: 16px;
    font-size: 16px;
  }
}

@media screen and (max-width: 600px) {
  .chat-main {
    border: none;
  }

  .space-header {
    padding: 16px 20px;
    border-left: none;
    border-right: none;
  }

  .space-name-tag {
    span {
      font-size: 16px;
    }

    svg {
      width: 14px;
      height: 14px;
    }
  }

  .chat-container {
    padding: 16px 20px 30px;
  }

  .notice-section {
    padding: 14px 20px;
  }

  .notice-text {
    font-size: 12px;
  }

  .chat-messages {
    gap: 30px;
    padding: 16px 0;
  }

  .message-text {
    font-size: 12px;
  }

  .user-name {
    font-size: 12px;
  }

  .message-time {
    font-size: 12px;
  }

  .message-input {
    font-size: 16px;
    height: 52px;
  }

  .send-btn {
    padding: 16px;
    height: 45px;
    font-size: 16px;
    width: auto;
  }

  .message-input-section {
    gap: 4px;
    padding-top: 16px;
  }
}

@media screen and (max-width: 500px) {
  .community-chat-page {
    display: block;
    padding: 80px 0 20px;
    position: relative;
  }
}

.loading-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
  color: #888;
  font-size: 16px;

  i {
    margin-right: 8px;
  }
}

.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
  text-align: center;
}

.context-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

.context-menu {
  position: fixed;
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  min-width: 150px;
  overflow: hidden;
}

.context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #F5F5F5;
  }

  &.delete {
    color: #FF5858;
  }

  i {
    font-size: 16px;
  }
}
</style>
