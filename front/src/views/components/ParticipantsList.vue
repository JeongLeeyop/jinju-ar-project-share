<template>
  <div class="participants-panel">
    <div class="participants-content">
      <h3 class="participants-title">참여자 {{ participantCount }}명</h3>
      
      <div class="participants-list">
        <!-- 초대하기 버튼 (관리자만, 비공개 공간만) -->
        <div v-if="isAdmin && !isPublic" class="invite-item" @click="handleInvite">
          <div class="invite-icon">
            <svg width="38" height="38" viewBox="0 0 38 38" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="1" y="1" width="36.0001" height="36.0001" rx="18" stroke="#073DFF" stroke-width="2"/>
              <path d="M19 12V26.0001M26.0001 19H12" stroke="#073DFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="invite-text">초대하기</span>
        </div>

        <div 
          v-for="participant in participants" 
          :key="participant.id"
          class="participant-item"
        >
          <div class="participant-avatar">
            <img 
              v-if="hasAvatar(participant)" 
              :src="`${apiUrl}/attached-file/${participant.avatar}`" 
              :alt="participant.name"
              class="avatar-image"
            />
            <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
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
          <span class="participant-name">{{ participant.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';

export interface Participant {
  id: string | number;
  name: string;
  avatar?: string;
  email?: string;
  joinedAt?: string;
}

@Component({
  name: 'ParticipantsList',
})
export default class extends Vue {
  @Prop({ default: () => [] }) participants!: Participant[];
  @Prop({ default: 0 }) participantCount!: number;
  @Prop({ default: false }) isAdmin!: boolean;
  @Prop({ default: false }) isPublic!: boolean;

  private handleInvite() {
    console.log('ParticipantsList: handleInvite called', {
      isAdmin: this.isAdmin,
      isPublic: this.isPublic
    });
    this.$emit('invite');
    console.log('ParticipantsList: invite event emitted');
  }

  // 아바타 이미지가 있으면 사용, 없으면 기본 SVG 사용
  private hasAvatar(participant: Participant): boolean {
    return !!(participant.avatar && participant.avatar.trim());
  }

  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }
}
</script>

<style scoped lang="scss">
.participants-panel {
    width: 380px;
    min-width: 320px;
    min-height: calc(100vh - 100px);
    margin: 220px 30px 0 0;
    border: 2px solid #EBEBEB;
    border-radius: 10px;
    padding: 40px;
    background: #FFF;
    position: relative;
    overflow-y: auto;
    flex-shrink: 0;

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

.participants-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.participants-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  text-align: left;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.participants-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.invite-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.invite-icon {
  width: 38px;
  height: 38px;
  flex-shrink: 0;
}

.invite-text {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.participant-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.participant-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;

  .avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.participant-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

@media screen and (max-width: 1800px) {
}

@media screen and (max-width: 1600px) {
  .participants-panel {
    width: 320px;
    padding: 30px;
  }

  .participants-title,
  .invite-text,
  .participant-name {
    font-size: 18px;
  }
}

@media screen and (max-width: 1200px) {
  .participants-panel {
    min-height: calc(100vh - 130px);
  }
}

@media screen and (max-width: 1024px) {
  .participants-panel {
    width: 200px;
    margin-right: 10px;
    min-width: 230px;
    min-height: calc(100vh - 115px);
    margin-top: 190px;
    padding: 30px 25px;
  }
}

@media screen and (max-width: 768px) {
  .participants-panel {
    border-top: 1px solid #EBEBEB;
    width: 100%;
    min-height: unset;
    margin: 0;
    padding: 30px;
    border: none;
    border-radius: 0;
  }

  .participants-content {
    gap: 24px;
    padding-top: 40px;
  }

  .participants-title {
    font-size: 20px;
  }

  .participants-list {
    gap: 24px;
  }

  .invite-text {
    font-size: 20px;
  }

  .participant-name {
    font-size: 16px;
  }

  .invite-icon {
    width: 38px;
    height: 38px;
  }

  .participant-avatar {
    width: 36px;
    height: 36px;
  }
}
</style>
