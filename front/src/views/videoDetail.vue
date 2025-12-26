<template>
  <div class="lession-detail-container" v-loading="loading">
    <CommunitySidebar :selectedSpaceId="'lession'" />
    <div class="content">
      <div class="content-header">
        <div class="content-ttl">{{ videoData.title }}</div>
        <div class="back-btn-wr">
          <el-button class="back-btn" @click="handleGoBack">돌아가기</el-button>
        </div>
      </div>
      <div class="content-description">{{ videoData.description }}</div>
      <div class="youtube">
        <div id="player"></div>
        <!-- <iframe id="videoFrame" width="700" height="400" src="https://www.youtube.com/embed/Yu29nG-38jM?si=3PITrVs6-28jYUtI" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe> -->
      </div>
      <div class="info">
        <div class="info-ttl" v-html="videoData.content"></div>
        <template v-if="videoData.timeLineList.length > 0">
          <div class="info-timeline-ttl">타임라인</div>
          <div class="timeline" v-for="timeline in videoData.timeLineList" :key="timeline.idx">
            <div class="item">
              <a @click="moveToTime(timeline.time)" class="time">{{ timeline.time }}</a>
              <div class="hipen">-</div>
              <div class="description">{{ timeline.description }}</div>
            </div>
          </div>
        </template>
        <div class="down_wr" v-if="videoData.fileList.length > 0">
          <div class="info-ttl">첨부파일</div>
          <slot v-if="videoData.fileList.length > 0">
            <div class="file" v-for="file in videoData.fileList" :key="file.fileUid">
            <a target="_blank" :href="`${apiUrl}/attached-file/${file.fileUid}`" :file="file.name">{{ file.name }}</a>
            </div>
            <div class="file" v-for="file in videoData.fileList" :key="file.fileUid">
              <a target="_blank" :href="`${apiUrl}/attached-file/${file.fileUid}`" :file="file.name">{{ file.name }}</a>
            </div>
          </slot>
          <slot v-else>
            등록된 첨부 파일이 없습니다.
          </slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ILession, IVideo, IWatchHistory } from '@/types/lession';
import { getVideo, saveWatchHistory } from '@/api/video';
import { ChannelModule } from '@/store/modules/channel';
import CommunitySidebar from './components/communitySidebar.vue';

@Component({
  name: '',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  async mounted() {
    await this.getVideo();
    if (this.videoData.urlCode !== null) {
      (window as any).onYouTubeIframeAPIReady = this.onYouTubeIframeAPIReady(this.videoData.urlCode, this.videoData.lastWatchSecond);
    }
  }

  private player: any = '';

  private loading = true;

  private videoData: IVideo = {
    idx: '',
    userUid: '',
    writer: '',
    contents: '',
    description: '',
    postTitle: '',
    lessionUid: '',
    createDate: '',
    urlCode: '',
    myWatchPercent: 0,
    lastWatchSecond: 0,
    timeLineList: [],
    fileList: [],
    viewOrder: 0,
    title: '',
    content: '',
    viewCount: 0,
    lastWatchUpdate: '',
  };

  private watchHistoryData: IWatchHistory = {
    percent: '',
    videoIdx: this.$route.params.videoIdx,
    lastWatchSecond: 0,
    channelUid: ChannelModule.selectedChannel.uid,
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private async getVideo() {
    await getVideo(this.$route.params.videoIdx).then((res) => {
        this.videoData = res.data;
        this.loading = false;
      }).catch(() => {
        this.$message.error('강의를 불러오는데 실패했습니다.');
        this.$router.push({ name: 'VideoDetail' });
      });
  }

  private async moveToTime(seconds: any) {
    if (this.player) {
        await this.player.seekTo(this.convertTimeToSeconds(seconds), true); // 지정한 초로 이동
        setTimeout(() => {
          this.trackWatchPercentage();
        }, 1000);
    }
  }

  private convertTimeToSeconds(time: any) {
    let s = 0;
    const parts = time.split(':').map(Number);
    if (parts.length === 2) { // MM:SS 형식
      const [minutes, seconds] = parts;
      s = minutes * 60 + seconds;
    } else if (parts.length === 3) { // HH:MM:SS 형식
      const [hours, minutes, seconds] = parts;
      s = hours * 3600 + minutes * 60 + seconds;
    }
    return s;
}

  private onYouTubeIframeAPIReady(urlCode: string, start: number) {
      const screenWidth = window.innerWidth;
      let width;

      if (screenWidth <= 425) {
          width = screenWidth * 0.8; // 화면 너비의 70% 사용
      } else {
          width = screenWidth * 0.6; // 화면 너비의 90% 사용
      }

      const height = width * (9 / 16); // 16:9 비율 유지

      this.player = new (window as any).YT.Player('player', {
          width,
          height,
          videoId: urlCode,
          events: {
              onReady: this.onPlayerReady,
              onStateChange: this.onPlayerStateChange,
          },
          playerVars: {
            rel: 0, // 연관동영상 표시여부(0:표시안함)
            controls: 1, // 플레이어 컨트롤러 표시여부(0:표시안함)
            autoplay: 0, // 자동재생 여부(1:자동재생 함, mute와 함께 설정)
            mute: 0, // 음소거여부(1:음소거 함)
            loop: 1, // 반복재생여부(1:반복재생 함)
            playsinline: 1, // iOS환경에서 전체화면으로 재생하지 않게
            // playlist: 'M7lc1UVf-VE'   //재생할 영상 리스트
            start,
          },
      });
  }

  private saveWatchHistory() {
    saveWatchHistory(this.watchHistoryData).then((res: any) => {
      console.log('현재 시청 구간 저장완료');
    });
  }

  private Interval: any;

  private onPlayerReady(event: any) {
    event.target.playVideo();
    this.Interval = setInterval(() => {
        this.trackWatchPercentage();
    }, 60000); // 1분마다 저장
  }

  private onPlayerStateChange(event: any) {
      if (event.data === (window as any).YT.PlayerState.ENDED) {
        this.saveWatchPercentage(100, this.player.getCurrentTime().toFixed(0));
        this.$message.info('학습이 완료되었습니다.');
      } else if (event.data === (window as any).YT.PlayerState.PAUSED) {
        this.trackWatchPercentage();
      }
  }

  private saveWatchPercentage(percentage: any, currentTime: any) {
      this.watchHistoryData.percent = percentage;
      this.watchHistoryData.lastWatchSecond = currentTime;
      this.saveWatchHistory();
  }

  private trackWatchPercentage() {
      const duration = this.player.getDuration();
      const currentTime = this.player.getCurrentTime();
      const percentage = (currentTime / duration) * 100;
      console.log(`현재 재생 위치: ${currentTime.toFixed(0)}초, 퍼센트2: ${percentage.toFixed(0)}%`);
      this.saveWatchPercentage(percentage.toFixed(0), currentTime.toFixed(0));
  }

  private handleGoBack() {
    this.$router.push({ name: 'Video', params: { lessionUid: this.$route.params.lessionUid } });
  }

  beforeDestroy() {
    clearInterval(this.Interval);
    if (this.player) {
        this.player.destroy();
        this.player = null; // 인스턴스를 null로 설정하여 메모리 해제
    }
  }
}
</script>

<style scoped lang="scss">
.lession-detail-container {
  display: flex;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.content {
  flex: 1;
  margin-left: 270px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  gap: 32px;

  @media screen and (max-width: 1024px) {
    margin-left: 0;
  }

  @media screen and (max-width: 768px) {
    padding: 40px 20px;
    gap: 32px;
  }
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;

  @media screen and (max-width: 768px) {
    flex-direction: column;
    gap: 16px;
  }
}

.content-ttl {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 42px;
  font-weight: 700;
  line-height: 100%;
  flex: 1;

  @media screen and (max-width: 1024px) {
    font-size: 32px;
  }

  @media screen and (max-width: 768px) {
    font-size: 18px;
    font-weight: 700;
    line-height: 100%;
  }
}

.back-btn-wr {
  flex-shrink: 0;

  @media screen and (max-width: 768px) {
    width: 100%;
  }
}

.back-btn {
  padding: 12px 24px;
  background: #F5F5F5;
  border: none;
  border-radius: 8px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #E0E0E0;
  }

  @media screen and (max-width: 768px) {
    width: 100%;
    padding: 14px 20px;
    font-size: 16px;
  }
}

.content-description {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;

  @media screen and (max-width: 768px) {
    font-size: 14px;
  }
}

.youtube {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  background: #000;
  border-radius: 10px;
  overflow: hidden;

  #player {
    width: 100%;
    max-width: 100%;
    aspect-ratio: 16/9;
  }

  @media screen and (max-width: 768px) {
    border-radius: 10px;

    #player {
      width: 100%;
      height: auto;
    }
  }
}

.info {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: #F8F9FB;
  border-radius: 10px;

  @media screen and (max-width: 768px) {
    padding: 20px;
    gap: 20px;
  }
}

.info-ttl {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 150%;

  @media screen and (max-width: 768px) {
    font-size: 16px;
  }
}

.info-timeline-ttl {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 150%;
  margin-top: 16px;

  @media screen and (max-width: 768px) {
    font-size: 16px;
  }
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FFF;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #F0F0F0;
  }

  @media screen and (max-width: 768px) {
    gap: 8px;
    padding: 10px;
  }
}

.time {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.7;
  }

  @media screen and (max-width: 768px) {
    font-size: 14px;
  }
}

.hipen {
  color: #888;
  font-size: 16px;
  font-weight: 400;

  @media screen and (max-width: 768px) {
    font-size: 14px;
  }
}

.description {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  flex: 1;

  @media screen and (max-width: 768px) {
    font-size: 14px;
  }
}

.down_wr {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.file {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #FFF;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #F0F0F0;
  }

  a {
    color: #073DFF;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 500;
    line-height: 150%;
    text-decoration: none;
    transition: opacity 0.2s;

    &:hover {
      opacity: 0.7;
    }

    @media screen and (max-width: 768px) {
      font-size: 14px;
    }
  }
}
</style>
