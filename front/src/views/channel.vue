<template>
  <div id="container" class="channel-main">
    <div class="channel-layout">
      <div class="channel-content-wrapper">
        <div class="channel-hero-section">
          <div class="hero-image-container">
            <slot v-if="channel && channel.coverImageList && channel.coverImageList.length > 0">
              <el-carousel indicator-position="outside" height="428px" class="desktop-carousel">
                <el-carousel-item v-for="item in channel.coverImageList" :key="item.uid">
                  <img :src="apiUrl + '/attached-file/' + item.fileUid" class="hero-image">
                </el-carousel-item>
              </el-carousel>
              <img :src="apiUrl + '/attached-file/' + channel.coverImageList[0].fileUid" class="hero-image mobile-hero">
            </slot>
            <slot v-else>
              <div class="hero-placeholder">
                <el-skeleton style="width: 100%; height: 428px;" :loading="loading" animated class="desktop-skeleton">
                  <template slot="template">
                    <el-skeleton-item variant="image" style="height: 428px;" />
                  </template>
                </el-skeleton>
                <el-skeleton style="width: 100%; height: 160px;" :loading="loading" animated class="mobile-skeleton">
                  <template slot="template">
                    <el-skeleton-item variant="image" style="height: 160px;" />
                  </template>
                </el-skeleton>
              </div>
            </slot>
          </div>
        </div>
        
        <div class="channel-info-section">
          <div class="mobile-unified-card">
            <div class="card-header-mobile">
              <img
                v-if="channel && channel.iconImageList && channel.iconImageList.length > 0"
                :src="apiUrl + '/attached-file/' + channel.iconImageList[0].fileUid"
                class="community-icon-mobile"
              >
              <div class="header-info-mobile">
                <div class="community-type-mobile">
                  <slot v-if="channel?.privateStatus">비공개</slot>
                  <slot v-else>공개</slot>
                  커뮤니티
                </div>
                <div class="community-name-mobile">{{ channel?.name }}</div>
              </div>
            </div>

            <div class="info-stats-mobile">
              <div class="stats-column">
                <div class="stat-item-mobile">
                  <div class="stat-label-mobile">개설자</div>
                  <div class="stat-value-mobile">{{ channel?.creatorName || '오형래' }} 님</div>
                </div>
                <div class="stat-item-mobile">
                  <div class="stat-label-mobile">공개여부</div>
                  <div class="stat-value-mobile">
                    <slot v-if="channel?.privateStatus">비공개</slot>
                    <slot v-else>공개</slot>
                  </div>
                </div>
              </div>
              <div class="stats-column">
                <div class="stat-item-mobile">
                  <div class="stat-label-mobile">회원수</div>
                  <div class="stat-value-mobile">{{ channel?.memberCount || 0 }}명</div>
                </div>
                <div class="stat-item-mobile">
                  <div class="stat-label-mobile">카테고리명</div>
                  <div class="stat-value-mobile">{{ channel?.categoryName || '지역' }}</div>
                </div>
              </div>
            </div>

            <div class="info-description-mobile">
              {{ channel?.introduce || '커뮤니티 소개' }}
            </div>

            <div class="card-stats-mobile">
              <div class="stat-box-mobile">
                <div class="stat-badge-mobile">가입 회원수</div>
                <div class="stat-number-mobile">{{ (channel?.memberCount || 0).toLocaleString() }}</div>
              </div>
              <!-- <div class="stat-box-mobile">
                <div class="stat-badge-mobile">실시간 접속자 수</div>
                <div class="stat-number-mobile">{{ (channel?.onlineCount || 0).toLocaleString() }}</div>
              </div> -->
            </div>

            <div class="card-join-btn-mobile">
              <el-button v-if="communityCheck()" @click="handleJoin()" class="join-button-mobile">
                <slot v-if="channel?.myJoinStatus === null || !channel?.myJoinStatus">
                  커뮤니티 가입하기
                </slot>
                <slot v-else-if="!channel?.myApprovalStatus">가입 요청중</slot>
                <slot v-else-if="channel?.myJoinStatus">커뮤니티 이동</slot>
              </el-button>
              <el-button v-else @click="handleMoveCommunity()" class="join-button-mobile">커뮤니티 소개</el-button>
            </div>
          </div>

          <div class="desktop-layout">
            <div class="channel-info-inner">
              <div class="info-stats">
                <div class="stat-item">
                  <div class="stat-label">개설자</div>
                  <div class="stat-value">{{ channel?.creatorName || '오형래' }} 님</div>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <div class="stat-label">공개여부</div>
                  <div class="stat-value">
                    <slot v-if="channel?.privateStatus">비공개</slot>
                    <slot v-else>공개</slot>
                  </div>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <div class="stat-label">회원수</div>
                  <div class="stat-value">{{ channel?.memberCount || 0 }}명</div>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item">
                  <div class="stat-label">카테고리명</div>
                  <div class="stat-value">{{ channel?.categoryName || '지역' }}</div>
                </div>
              </div>
              <div class="info-description">
                {{ channel?.introduce || '커뮤니티 소개' }}
              </div>
            </div>
            <div class="component-container">
              <Component1 />
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog width="550px" class="dialog-wrap" :visible.sync="modalVisible">
      <div class="section step3 channelForm active" v-if="activeStep === 'PrimiumJoinForm'">
        <div class="form_wr">
          <div class="thumbnail_wr">
            <div class="thumbnail"></div>
          </div>
          <div class="title_wr">
            <div class="profile">청년사업가 오형래</div>
            <div class="ttl">매 달 9,900원 맴버십 가입을 위한 결제를 진행합니다.</div>
          </div>
          <div class="question-wrap">
            <div class="question">커뮤니티에 참여하고 싶은 이유가 무엇입니까?</div>
            <div class="btn-wr name"><el-input placeholder="질문에 답변해 주세요."/></div>
          </div>
          <div class="register-btn"><el-button @click="requestBillingAuth()">가입하기 / 매 달 9,900원</el-button></div>
        </div>
      </div>
      <div class="section step3 channelForm password active" v-if="activeStep === 'PasswordForm'">
        <div class="form_wr">
          <div class="thumbnail_wr">
            <div class="thumbnail"></div>
          </div>
          <div class="title_wr">
          <div class="profile">{{ channel?.name }}</div>
          <div class="ttl">커뮤니티 가입</div>
          </div>
          <div class="question-wrap">
            <div class="question">인증 코드를 입력하세요</div>
            <div class="btn-wr name"><el-input type="password" v-model="passwordForm.code" placeholder="인증코드를 입력하세요."/></div>
          </div>
          <div class="register-btn"><el-button @click="handleValidateCode()">인증코드 확인</el-button></div>
        </div>
      </div>
      <div class="section step3 channelForm active" v-if="activeStep === 'JoinForm'">
        <div class="form_wr">
          <div class="thumbnail_wr">
            <div class="thumbnail"></div>
          </div>
          <div class="title_wr">
          <div class="profile">{{ channel?.name }}</div>
          <div class="ttl">커뮤니티 가입</div>
          </div>
          <div class="question-wrap">
            <div class="question">간단한 자기소개를 작성해 주세요.</div>
            <div class="btn-wr name"><el-input v-model="joinForm.introduce" placeholder="자기소개를 작성해 주세요."/></div>
          </div>
          <div class="question-wrap" v-for="question in joinForm.questionList" :key="question.idx">
              <div class="question">{{ question.title }}</div>
              <div class="btn-wr name"><el-input v-model="question.answer" placeholder="질문에 답변해 주세요."/></div>
          </div>
          <div class="register-btn"><el-button @click="handleApprovalCheck()">커뮤니티 가입하기</el-button></div>
        </div>
      </div>
      <div class="section step4 channelJoin active" v-if="activeStep === 'ApprovalCheck'">
        <div class="form_wr">
          <div class="title_wr">
            <div class="profile">가입 절차 진행 중</div>
            <div class="txt">
              {{ channel?.name }} 커뮤니티 관리자가.
              귀하의 가입 요청을 검토하고 있습니다
              승인되면 알림을 받게 됩니다.
            </div>
          </div>
          <div class="register-btn"><el-button @click="handleFinish()">확인했습니다.</el-button></div>
        </div>
      </div>
    </el-dialog>
    <UserModal :userModalVisible="userModalVisible" @close="userModalVisible = false;"/>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Component1 from './components/component1.vue';
import UserModal from '@/views/components/userModal.vue';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import { IChannel } from '@/types/channel';
import { joinchannel, validateCode } from '@/api/channel';
import Slick from 'vue-slick';
import $ from 'jquery';

@Component({
  name: '',
  components: {
    Component1,
    Slick,
    UserModal,
  },
})
export default class extends Vue {
  mounted() {
    this.channel = ChannelModule.selectedChannel;
    this.setQuestionForm();
  }

  private apiUrl = process.env.VUE_APP_BASE_API;

  private slickOptions = {
    autoplay: true,
    autoplaySpeed: 4000,
    infinite: true,
    slidesToShow: 1,
    slidesToScroll: 1,
    speed: 400,
    prevArrow: $('.iconslick-prev'),
    nextArrow: $('.iconslick-next'),
    draggable: true,
    pauseOnHover: true,
  };

  private filterVisible = false;

  private modalVisible = false;

  private userModalVisible = false;

  private loading = true;

  private channel: IChannel | null = null;

  private activeStep = '';

  private listQuery: any = {
    typeUid: '',
    categoryUid: '',
    searchType: 'name',
    searchValue: '',
    page: 0,
    size: 10,
    status: this.$route.params.status ? this.$route.params.status : 0,
  }

  private passwordForm = {
    code: '',
    uid: ChannelModule.selectedChannel?.uid || '',
  }

  private joinForm: any = {
    channelUid: ChannelModule.selectedChannel?.uid || '',
    introduce: '',
    questionList: [],
  }

  private setQuestionForm() {
    if (this.channel && this.channel.questionList) {
      this.joinForm.questionList = this.channel.questionList.map((question: any) => ({
        channelQuestionIdx: question.idx,
        title: question.title,
        answer: '',
      }));
    }
  }

  private handleJoin() {
    if (!UserModule.isLogin) {
        this.modalVisible = false;
        this.userModalVisible = true;
    } else if (UserModule.isLogin) {
      if (this.channel?.myChannelStatus) {
        this.$message.warning('본인의 커뮤니티에는 가입하실 수 없습니다.');
      } else if (this.channel?.myJoinStatus && !this.channel?.myApprovalStatus) {
        this.$message.warning('가입 승인 대기중입니다.');
      } else if (this.channel?.myJoinStatus && this.channel?.myApprovalStatus) {
        this.$router.push({ name: 'CommunityMain', params: { domain: this.$route.params.domain } });
      } else if (this.channel?.privateStatus) {
        this.viewPasswordForm();
        this.modalVisible = true;
      } else if ((this.channel?.price ?? 0) > 0) {
        this.viewPrimiumJoinForm();
      } else {
        this.viewJoinForm();
        this.modalVisible = true;
      }
    }
  }

  private viewPrimiumJoinForm() {
    this.activeStep = 'PrimiumJoinForm';
    this.modalVisible = true;
  }

  private viewJoinForm() {
    this.activeStep = 'JoinForm';
  }

  private viewPasswordForm() {
    this.activeStep = 'PasswordForm';
  }

  private handleValidateCode() {
    validateCode(this.passwordForm).then((res: any) => {
      if (res.data) {
        this.$message.info('인증코드가 일치 합니다.');
        if ((this.channel?.price ?? 0) > 0) {
            this.viewPrimiumJoinForm();
        } else {
          this.viewJoinForm();
        }
      } else {
        this.$message.warning('인증코드가 일치하지 않습니다.');
        this.$router.push({ name: 'Home' });
      }
    });
  }

  private handleApprovalCheck() {
    joinchannel(this.joinForm).then(() => {
      this.$message.info('커뮤니티 가입이 신청되었습니다.');
      this.activeStep = 'ApprovalCheck';
    }).catch(() => {
      this.$message.error('커뮤니티 가입이 실패하였습니다.');
    });
  }

  private handleFinish() {
      this.$router.go(0);
      this.modalVisible = false;
  }

  private handleMoveCommunity() {
    if (this.$route.name === 'About') {
      this.$router.push({ name: 'CommunityMain' });
    } else {
      this.$router.push({ name: 'About' });
    }
  }

  private clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq';

  private customerKey = '123123';

  private tossPayments = (window as any).TossPayments(this.clientKey);

  private payment = this.tossPayments.payment({
      customerKey: this.customerKey,
  });

  private async requestBillingAuth() {
    await this.payment.requestBillingAuth({
      method: 'CARD',
      successUrl: `${window.location.origin}/community`,
      failUrl: `${window.location.origin}/fail.html`,
      customerEmail: 'customer123@gmail.com',
      customerName: '김토스',
    });
  }

  private communityCheck() {
    for (let i = 0; i < this.$route.matched.length; i += 1) {
        const route = this.$route.matched[i];
        if (route.name && ['Community', 'MemberSetting'].includes(route.name)) {
          return false;
        }
      }
      return true;
  }
}
</script>

<style scoped lang="scss">
.channel-main {
  background: #fff;
  min-height: 100vh;
  padding: 40px;
}

.channel-layout {
  display: flex;
  flex: 0 1 100%;
  justify-content: center;
  gap: 40px;
  align-items: flex-start;
}

.channel-content-wrapper {
  max-width: 1200px;
  flex: 0 1 100%;
}

.channel-hero-section {
  margin-bottom: 32px;
}

.hero-image-container {
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
}

.hero-image {
  width: 100%;
  height: 428px;
  object-fit: cover;
  display: block;
}

.mobile-hero {
  display: none;
}

.hero-placeholder {
  width: 100%;
  height: 428px;
  background: #D9D9D9;
  border-radius: 10px;
}

.mobile-skeleton {
  display: none;
}

.channel-info-section {
  max-width: 1200px;
  gap: 40px;
  display: flex;
}

.desktop-layout {
  display: flex;
  gap: 40px;
  width: 100%;
}

.mobile-unified-card {
  display: none;
}

.info-stats {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 0 32px;
}

.stat-item:first-child {
  padding-left: 0;
}

.stat-divider {
  width: 1px;
  height: 20px;
  background: #444;
}

.stat-label {
  color: #222;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.stat-value {
  color: #444;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
}

.info-description {
  padding: 32px;
  border-radius: 10px;
  color: #888;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
  text-align: left;
}

.channel-info-inner {
  flex: 0 1 70%;
}

.component-container {
  flex: 0 1 30%;
}

@media (max-width: 1024px) {
  .channel-layout {
    flex-direction: column;
    align-items: center;
  }

  .desktop-layout {
    flex-direction: column;
    align-items: center;
  }

  .channel-info-inner {
    flex: 1;
    width: 100%;
  }

  .component-container {
    position: static;
    width: 100%;
    max-width: 500px;
  }
}

@media (max-width: 768px) {
  .channel-main {
    padding: 0;
    background: #FFF;
  }

  .channel-layout {
    gap: 0;
  }

  .channel-content-wrapper {
    max-width: 100%;
    width: 100%;
  }

  .channel-hero-section {
    margin-bottom: 0;
  }

  .hero-image-container {
    border-radius: 0;
  }

  .desktop-carousel {
    display: none;
  }

  .mobile-hero {
    display: block;
    height: 160px;
    border-radius: 0;
  }

  .hero-image {
    height: 160px;
  }

  .hero-placeholder {
    height: 160px;
    border-radius: 0;
  }

  .desktop-skeleton {
    display: none;
  }

  .mobile-skeleton {
    display: block;
  }

  .desktop-layout {
    display: none;
  }

  .mobile-unified-card {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: calc(100% - 40px);
    margin: 20px;
    padding: 20px;
    border: 1px solid #EBEBEB;
    border-radius: 10px;
    background: #FFF;
  }

  .card-header-mobile {
    display: flex;
    align-items: center;
    gap: 22px;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEBEB;
  }

  .community-icon-mobile {
    width: 68px;
    height: 68px;
    border-radius: 16px;
    object-fit: cover;
    flex-shrink: 0;
  }

  .header-info-mobile {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .community-type-mobile {
    color: #888;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 14px;
    font-weight: 700;
    line-height: 100%;
  }

  .community-name-mobile {
    color: #222;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 18px;
    font-weight: 700;
    line-height: 100%;
  }

  .info-stats-mobile {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEBEB;
  }

  .stats-column {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1;
  }

  .stat-item-mobile {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .stat-label-mobile {
    width: 56px;
    color: #222;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 12px;
    font-weight: 700;
    line-height: 100%;
  }

  .stat-value-mobile {
    color: #444;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 12px;
    font-weight: 400;
    line-height: 150%;
  }

  .info-description-mobile {
    flex: 1;
    color: #888;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 12px;
    font-weight: 400;
    line-height: 150%;
    padding-bottom: 16px;
    border-bottom: 1px solid #EBEBEB;
  }

  .card-stats-mobile {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 52px;
  }

  .stat-box-mobile {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .stat-badge-mobile {
    display: inline-flex;
    padding: 4px;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    background: rgba(7, 61, 255, 0.10);
    color: #073DFF;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 12px;
    font-weight: 700;
    line-height: 100%;
    white-space: nowrap;
  }

  .stat-number-mobile {
    color: #222;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 36px;
    font-weight: 600;
    line-height: 100%;
  }

  .card-join-btn-mobile {
    width: 100%;
  }

  .join-button-mobile {
    width: 100%;
    height: 54px;
    border-radius: 10px;
    background: #073DFF;
    border: none;
    color: #FFF;
    font-family: Pretendard, -apple-system, sans-serif;
    font-size: 16px;
    font-weight: 700;
    line-height: 100%;
    cursor: pointer;
    transition: background 0.3s;
  }

  .join-button-mobile:hover {
    background: #0532CC;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .channel-main {
    padding: 32px;
  }

  .hero-image {
    height: 320px;
  }

  .stat-item {
    padding: 0 20px;
  }

  .stat-label {
    font-size: 18px;
  }

  .stat-value {
    font-size: 16px;
  }

  .info-description {
    padding: 24px;
    font-size: 16px;
  }
}
</style>
