<template>
  <div class="community-chat-page">
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="spaceId" @space-select="handleSpaceSelect" />

    <!-- Main Chat Area -->
    <div class="community-main chat-main">
      <div class="space-header">
        <div class="space-name-tag">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="8" cy="8" r="8" :fill="spaceColor"/>
          </svg>
          <span>{{ spaceName }}</span>
        </div>
        <button class="toggle-participants-btn" @click="toggleParticipants">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 5V19M5 12C10.4673 12 13.5327 12 19 12" stroke="black" stroke-width="1.5" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <div class="chat-container">
        <!-- Notice Section (Collapsible) -->
        <div class="notice-section" :class="{ collapsed: noticeCollapsed }">
          <div class="notice-content">
            <p class="notice-text">
              공지사항 예시입니다. 공지사항은 오른쪽에 북마크로 공지사항을 표시해다. 공지사항 예시입니다. 공지사항은니다. 공지사항 예시입니다. 예시입니다. 예시입니다. 예시입니다. 예시입니다. 예시입니다. 예시입니다. 예시입니다. 예시입니다. ...
            </p>
            <button class="toggle-btn" @click="noticeCollapsed = !noticeCollapsed">
              <svg width="24" height="28" viewBox="0 0 24 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 10L12.5 17.5L5 10" stroke="black" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Chat Messages -->
        <div class="chat-messages">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            class="message-wrapper"
            :class="{ 'message-sent': message.isSent, 'message-received': !message.isSent }"
          >
            <div v-if="!message.isSent" class="message-left">
              <div class="message-header">
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
                <span class="user-name">{{ message.userName }}</span>
                <span class="message-time">{{ message.time }}</span>
              </div>
              <div class="message-bubble">
                <p class="message-text">{{ message.text }}</p>
                <div class="bubble-tail left-tail"></div>
              </div>
            </div>

            <div v-else class="message-right">
              <span class="message-time">{{ message.time }}</span>
              <div class="message-bubble sent-bubble">
                <p class="message-text">{{ message.text }}</p>
                <div class="bubble-tail right-tail"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- Message Input -->
        <div class="message-input-section">
          <input
            type="text"
            class="message-input"
            placeholder="채팅메세지를 작성하세요!"
            v-model="newMessage"
            @keyup.enter="sendMessage"
          />
          <button class="send-btn" @click="sendMessage">채팅입력</button>
        </div>
      </div>
    </div>

    <!-- Participants Panel Overlay on Mobile -->
    <div class="participants-overlay" v-if="showParticipants" @click="toggleParticipants"></div>

    <div class="participants-wrapper" :class="{ 'show': showParticipants }">
      <button class="close-participants-btn" @click="toggleParticipants">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
      <ParticipantsList
        :participants="participants"
        :participantCount="participantCount"
        @invite="handleInvite"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import ParticipantsList from './components/ParticipantsList.vue';
import chatService, { ChatMessage } from '@/api/chatService';

interface DisplayMessage {
  id: string;
  userName: string;
  time: string;
  text: string;
  isSent: boolean;
  userId: string;
  userAvatar?: string;
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
  },
})
export default class extends Vue {
  private spaceId = '';
  private spaceName = '산청등산모임';
  private spaceColor = '#FF5858';
  private noticeCollapsed = false;
  private newMessage = '';
  private participantCount = 15;
  // private messages: DisplayMessage[] = [];
  private unsubscribe: (() => void) | null = null;
  private isLoading = false;
  private isSending = false;
  private showParticipants = false;

  private messages: DisplayMessage[] = [
    {
      id: '1',
      userName: '오형래',
      time: 'AM 09:51',
      text: '오늘 비가 많이 와서 사전 등산\n일정은 취소해야 할 듯..',
      isSent: false,
      userId: 'user_123',
      userAvatar: '',
    },
    {
      id: '2',
      userName: '나',
      time: 'AM 09:51',
      text: '오늘 비가 많이 와서 사전 등산\n일정은 취소해야 할 듯..',
      isSent: true,
      userId: 'user_789',
      userAvatar: '',
    },
    {
      id: '3',
      userName: '이명관',
      time: 'AM 09:51',
      text: '밖에 비 많이 오나요?',
      isSent: false,
      userId: 'user_456',
      userAvatar: '',
    },
  ];
  
  // 현재 로그인한 사용자 정보 (임시 - 실제로는 Vuex store나 auth service에서 가져와야 함)
  private currentUser = {
    id: 'user_' + Math.random().toString(36).substr(2, 9),
    name: '나',
    avatar: '',
  };

  private participants: Participant[] = [
    { id: 1, name: '이명관' },
    { id: 2, name: '오형래' },
    { id: 3, name: '이명관' },
  ];

  async mounted() {
    this.spaceId = this.$route.params.spaceId || 'default-space';
    
    // Firebase 실시간 리스너 등록
    // await this.initializeChat();
  }

  beforeDestroy() {
    // 컴포넌트 파괴 시 리스너 해제
    if (this.unsubscribe) {
      this.unsubscribe();
    }
  }

  @Watch('$route.params.spaceId')
  private async onSpaceIdChange(newSpaceId: string) {
    if (newSpaceId && newSpaceId !== this.spaceId) {
      // 이전 리스너 해제
      if (this.unsubscribe) {
        this.unsubscribe();
      }
      
      this.spaceId = newSpaceId;
      await this.initializeChat();
    }
  }

  /**
   * 채팅 초기화 - Firebase 리스너 등록
   */
  private async initializeChat() {
    try {
      this.isLoading = true;
      
      // 실시간 메시지 수신 리스너 등록
      this.unsubscribe = chatService.onMessagesChange(
        this.spaceId,
        (firebaseMessages: ChatMessage[]) => {
          this.messages = firebaseMessages.map((msg) => this.convertToDisplayMessage(msg));
          
          // 새 메시지가 추가되면 자동 스크롤
          this.$nextTick(() => {
            this.scrollToBottom();
          });
        },
      );

      // 채팅 공간 정보 로드 (옵션)
      await this.loadChatSpaceInfo();
      
    } catch (error) {
      console.error('Failed to initialize chat:', error);
      this.$message.error('채팅을 불러오는데 실패했습니다.');
    } finally {
      this.isLoading = false;
    }
  }

  /**
   * 채팅 공간 정보 로드
   */
  private async loadChatSpaceInfo() {
    try {
      const spaceInfo = await chatService.getChatSpace(this.spaceId);
      if (spaceInfo) {
        this.spaceName = spaceInfo.name;
        this.spaceColor = spaceInfo.color;
        this.participantCount = spaceInfo.participants.length;
      }
    } catch (error) {
      console.error('Failed to load chat space info:', error);
    }
  }

  /**
   * Firebase 메시지를 화면 표시용 메시지로 변환
   */
  private convertToDisplayMessage(msg: ChatMessage): DisplayMessage {
    return {
      id: msg.id,
      userName: msg.userName,
      time: this.formatTime(msg.createdAt || new Date()),
      text: msg.text,
      isSent: msg.userId === this.currentUser.id,
      userId: msg.userId,
      userAvatar: msg.userAvatar,
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

  /**
   * 메시지 전송
   */
  private async sendMessage() {
    if (!this.newMessage.trim() || this.isSending) return;

    try {
      this.isSending = true;
      const messageText = this.newMessage.trim();
      this.newMessage = ''; // 입력창 즉시 비우기

      // Firebase에 메시지 저장
      await chatService.sendMessage(
        this.spaceId,
        this.currentUser.id,
        this.currentUser.name,
        messageText,
        this.currentUser.avatar,
      );

      // 실시간 리스너가 자동으로 새 메시지를 추가하므로 별도로 추가하지 않음
      
    } catch (error) {
      console.error('Failed to send message:', error);
      this.$message.error('메시지 전송에 실패했습니다.');
      // 실패 시 입력값 복원
      this.newMessage = this.newMessage || '';
    } finally {
      this.isSending = false;
    }
  }

  /**
   * 채팅 영역 하단으로 스크롤
   */
  private scrollToBottom(smooth: boolean = true) {
    this.$nextTick(() => {
      const chatMessages = this.$el.querySelector('.chat-messages');
      if (chatMessages) {
        chatMessages.scrollTo({
          top: chatMessages.scrollHeight,
          behavior: smooth ? 'smooth' : 'auto',
        });
      }
    });
  }

  /**
   * 시간 포맷팅
   */
  private formatTime(date: Date): string {
    const now = new Date();
    const messageDate = new Date(date);
    
    const hours = messageDate.getHours();
    const minutes = messageDate.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHours = hours % 12 || 12;
    const displayMinutes = minutes < 10 ? `0${minutes}` : minutes;
    
    // 오늘 날짜면 시간만, 다른 날이면 날짜도 표시
    if (
      now.getFullYear() === messageDate.getFullYear() &&
      now.getMonth() === messageDate.getMonth() &&
      now.getDate() === messageDate.getDate()
    ) {
      return `${ampm} ${displayHours}:${displayMinutes}`;
    } else {
      const month = messageDate.getMonth() + 1;
      const day = messageDate.getDate();
      return `${month}/${day} ${ampm} ${displayHours}:${displayMinutes}`;
    }
  }

  private handleInvite() {
    this.$message.info('초대하기 기능은 준비 중입니다.');
  }

  private toggleParticipants() {
    this.showParticipants = !this.showParticipants;
  }
}
</script>

<style scoped lang="scss">
.community-chat-page {
  display: flex;
  min-height: calc(100vh - 120px);
  background: #FFF;
}

.chat-main {
  flex: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  margin-left: 270px;
  // margin-right: 460px;
  min-width: 0;
}

.space-header {
  position: relative;
  padding: 40px 40px 20px;
  text-align: left;
  border-left: 2px solid #EBEBEB;
  border-right: 2px solid #EBEBEB;
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
  padding: 0 40px 40px;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
  border-left: 2px solid #EBEBEB;
  border-right: 2px solid #EBEBEB;
  border-bottom: 2px solid #EBEBEB;
  border-top: none;
  box-sizing: border-box;
}

.notice-section {
  width: 100%;
  padding: 30px;
  border: 2px solid #EBEBEB;
  border-radius: 8000px;
  margin-bottom: 30px;
  transition: all 0.3s ease;

  &.collapsed {
    .notice-text {
      -webkit-line-clamp: 1;
    }

    .toggle-btn svg {
      transform: rotate(180deg);
    }
  }
}

.notice-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.notice-text {
  flex: 1;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
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

  svg {
    width: 100%;
    height: 100%;
  }

  &:hover {
    opacity: 0.7;
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  gap: 40px;

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
  align-items: center;
  gap: 20px;
  padding-top: 30px;
  border-top: 2px solid #EBEBEB;
}

.message-input {
  flex: 1;
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
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
  width: 136px;
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
  .chat-container {
    max-width: 800px;
    padding: 80px 30px 40px;
  }
}

@media screen and (max-width: 1024px) {
  .chat-main {
    margin-left: 0;
    margin-right: 0;
  }

  .space-header {
    padding: 30px 40px 20px;
    border-left: 2px solid #EBEBEB;
    border-right: 2px solid #EBEBEB;
  }

  .chat-container {
    padding: 0 40px 40px;
    border-left: 2px solid #EBEBEB;
    border-right: 2px solid #EBEBEB;
  }

  .message-left,
  .message-right {
    max-width: 85%;
  }
}

@media screen and (max-width: 768px) {
  .community-chat-page {
    display: block;
    padding-top: 54px;
    position: relative;
  }

  .chat-main {
    border: none;
  }

  .space-header {
    padding: 16px 20px;
    border-left: none;
    border-right: none;
    border-bottom: 1px solid #EBEBEB;
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
    padding: 20px;
    border-left: none;
    border-right: none;
    border-bottom: none;
    height: calc(100vh - 54px);
  }

  .notice-section {
    padding: 14px 20px;
    border-radius: 8000px;
    margin-bottom: 20px;
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
    border: none;
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
</style>
