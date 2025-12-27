<template>
  <div class="community-card">
    <div class="card-header">
      <img
        v-if="channel && channel.iconImageList && channel.iconImageList.length > 0"
        :src="apiUrl + '/attached-file/' + channel.iconImageList[0].fileUid"
        class="community-icon"
      >
      <img
        v-else
        src="@/assets/images/logo.png"
        class="community-icon"
      >
      <div class="header-info">
        <div class="community-type">
          <slot v-if="channel?.privateStatus">비공개</slot>
          <slot v-else>공개</slot>
          커뮤니티
        </div>
        <div class="community-name">{{ channel?.name }}</div>
      </div>
    </div>

    <div class="card-description">
      {{ channel?.introduce }}
    </div>

    <div class="card-stats">
      <div class="stat-box">
        <div class="stat-badge">가입 회원수</div>
        <div class="stat-number">{{ channel?.memberCount || 0 }}</div>
      </div>
      <!-- <div class="stat-box">
        <div class="stat-badge">실시간 접속자 수</div>
        <div class="stat-number">{{ channel?.onlineCount || 0 }}</div>
      </div> -->
    </div>

    <div class="card-join-btn">
      <el-button v-if="communityCheck()" @click="handleJoin()" class="join-button">
        <slot v-if="channel?.myChannelStatus">
          커뮤니티 입장
        </slot>
        <slot v-else-if="channel?.myJoinStatus === null || !channel?.myJoinStatus">
          커뮤니티 가입하기
        </slot>
        <slot v-else-if="!channel?.myApprovalStatus">가입 요청중</slot>
        <slot v-else-if="channel?.myJoinStatus">커뮤니티 이동</slot>
      </el-button>
      <el-button v-else @click="handleMoveCommunity()" class="join-button">커뮤니티 소개</el-button>
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
      <!-- TODO 비밀커뮤니티 가입 기능 -->
      <div class="section step3 channelForm password active" v-if="activeStep === 'PasswordForm'">
        <div class="form_wr">
          <div class="thumbnail_wr">
            <div class="thumbnail"></div>
          </div>
          <div class="title_wr">
          <div class="profile">{{ channel.name }}</div>
          <div class="ttl">커뮤니티 가입</div>
            <!-- <div class="ttl">커뮤니티에 가입 하기 위한 질문에 답변하세요.</div> -->
          </div>
          <!-- TODO 커뮤니티 가입시 질의문답 기능 -->
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
          <div class="profile">{{ channel.name }}</div>
          <div class="ttl">커뮤니티 가입</div>
            <!-- <div class="ttl">커뮤니티에 가입 하기 위한 질문에 답변하세요.</div> -->
          </div>
          <!-- TODO 커뮤니티 가입시 질의문답 기능 -->
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
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { IChannel } from '@/types/channel';
import { Component, Vue, Prop } from 'vue-property-decorator';
import { Form } from 'element-ui';
import { joinchannel, validateCode } from '@/api/channel';
import UserModal from '@/views/components/userModal.vue';
import { getRanking } from '@/api/ranking';
import { userIdCheck, addUser } from '@/api/user';

@Component({
  name: 'Component1',
  components: {
    UserModal,
  },
})
export default class extends Vue {
  mounted() {
    this.channel = ChannelModule.selectedChannel;
    this.setQuestionForm();
    this.getRanking();
  }

  private rankingList: any = {};

  private apiUrl = process.env.VUE_APP_BASE_API;

  private modalVisible = false;

  private userModalVisible = false;

  private activeStep = '';

  private channel: IChannel | null = null;

  private handleReady() {
    alert('준비중 입니다.');
  }

  private loading = false;

  private setQuestionForm() {
    if (this.channel && this.channel.questionList) {
      this.joinForm.questionList = this.channel.questionList.map((question: any) => ({
        channelQuestionIdx: question.idx,
        title: question.title,
        answer: '',
      }));
    }
  }

  private rankingView() {
    return this.$route.name === 'CommunityMain' || this.$route.name === 'Channel';
  }

  private handleJoin() {
    if (!UserModule.isLogin) { // 비로그인시 로그인페이지로
        this.modalVisible = false;
        this.userModalVisible = true;
    } else if (UserModule.isLogin) { // 로그인시
      if (this.channel?.myChannelStatus) { // 본인 채널 요청시 - 바로 입장
        this.$router.push({ name: 'CommunityMain', params: { domain: this.$route.params.domain } });
      } else if (this.channel?.myJoinStatus && !this.channel?.myApprovalStatus) { // 가입 요청중일 경우
        this.$message.warning('가입 승인 대기중입니다.');
      } else if (this.channel?.myJoinStatus && this.channel?.myApprovalStatus) { // 가입된 커뮤니티인 경우
        this.$router.push({ name: 'CommunityMain', params: { domain: this.$route.params.domain } });
      } else if (this.channel?.privateStatus) { // 비공개 채널 가입 요청시
        this.viewPasswordForm();
        this.modalVisible = true;
      } else if ((this.channel?.price ?? 0) > 0) { // 유료 채널 가입 요청 (null 또는 undefined일 경우 0으로 할당)
        this.viewPrimiumJoinForm();
      } else { // 무료 채널 가입 요청
        this.viewJoinForm();
        this.modalVisible = true;
      }
    }
  }

  private getRanking() {
      if (!ChannelModule.selectedChannel) {
        return;
      }
      this.loading = true;
        getRanking(ChannelModule.selectedChannel.uid, '30').then((res) => {
            this.rankingList = res.data;
            this.loading = false;
        });
        this.loading = false;
    }

  private viewPrimiumJoinForm() {
    this.activeStep = 'PrimiumJoinForm';
  }

  private viewJoinForm() {
    this.activeStep = 'JoinForm';
  }

  private viewPasswordForm() {
    this.activeStep = 'PasswordForm';
  }

  private passwordForm = {
    code: '',
    uid: ChannelModule.selectedChannel?.uid || '',
  }

  private handleValidateCode() {
    validateCode(this.passwordForm).then((res: any) => {
      console.log(res.data);
      if (res.data) {
        this.$message.info('인증코드가 일치 합니다.');
        if ((this.channel?.price ?? 0) > 0) { // 유료 채널 가입 요청 (null 또는 undefined일 경우 0으로 할당)
            this.viewPrimiumJoinForm();
        } else { // 무료 채널 가입 요청
          this.viewJoinForm();
        }
      } else {
        this.$message.warning('인증코드가 일치하지 않습니다.');
        this.$router.push({ name: 'Home' });
      }
    });
  }

  private viewJoinHold() {
    this.activeStep = 'JoinHold';
  }

  private joinForm: any = {
    channelUid: ChannelModule.selectedChannel?.uid || '',
    introduce: '',
    questionList: [],
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
      method: 'CARD', // 자동결제(빌링)은 카드만 지원합니다
      successUrl: `${window.location.origin}/community`, // 요청이 성공하면 리다이렉트되는 URL
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

  private handleGoRanking() {
    this.$router.push({ name: 'Ranking' });
  }
}
</script>

<style scoped>
.community-card {
  background: #fff;
  border: 1px solid #EBEBEB;
  border-radius: 10px;
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 22px;
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEBEB;
  margin-bottom: 16px;
}

.community-icon {
  width: 68px;
  height: 68px;
  border-radius: 10px;
  object-fit: cover;
  flex-shrink: 0;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.community-type {
  text-align: left;
  color: #888;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
}

.community-name {
  text-align: left;
  color: #222;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.card-description {
  color: #888;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEBEB;
  margin-bottom: 16px;
}

.card-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  margin-bottom: 32px;
}

.stat-box {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.stat-badge {
  display: inline-flex;
  padding: 4px;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  background: rgba(7, 61, 255, 0.10);
  color: #073DFF;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 100%;
  white-space: nowrap;
}

.stat-number {
  color: #222;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 42px;
  font-weight: 600;
  line-height: 100%;
}

.card-join-btn {
  width: 100%;
}

.join-button {
  width: 100%;
  height: 54px;
  border-radius: 10px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 150%;
  cursor: pointer;
  transition: background 0.3s;
}

.join-button:hover {
  background: #0532CC;
}

@media (max-width: 768px) {
  .community-card {
    width: 100%;
    max-width: 373px;
  }

  .card-stats {
    gap: 20px;
  }

  .stat-badge {
    font-size: 12px;
  }

  .stat-number {
    font-size: 32px;
  }
}
</style>
