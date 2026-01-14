<template>
  <div class="user-wrap">
    <div class="user-title">
      <div class="tl__box">
        <p class="tl">회원 상세</p>
        <router-link class="back-btn" :to="{ name: 'MemberList' }">
          <i class="el-icon-arrow-left"></i> 목록으로
        </router-link>
      </div>
    </div>

    <div v-loading="loading" class="detail-content">
      <!-- 기본 정보 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-user"></i> 기본 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>이름</label>
            <span>{{ member.actualName }}</span>
          </div>
          <div class="info-item">
            <label>이메일</label>
            <span>{{ member.email || '-' }}</span>
          </div>
          <div class="info-item">
            <label>연락처</label>
            <span>{{ member.concatNumber || '-' }}</span>
          </div>
          <div class="info-item">
            <label>생년월일</label>
            <span>{{ member.birth || '-' }}</span>
          </div>
          <div class="info-item">
            <label>성별</label>
            <span>{{ member.gender === 0 ? '남자' : '여자' }}</span>
          </div>
          <div class="info-item">
            <label>가입유형</label>
            <el-tag v-if="member.provider === 'KAKAO'" type="warning" effect="dark" size="small">카카오</el-tag>
            <el-tag v-else-if="member.provider === 'NAVER'" type="success" effect="dark" size="small">네이버</el-tag>
            <el-tag v-else-if="member.provider === 'APPLE'" type="info" effect="dark" size="small">애플</el-tag>
            <el-tag v-else type="primary" effect="plain" size="small">일반</el-tag>
          </div>
          <div class="info-item">
            <label>가입일</label>
            <span>{{ member.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}</span>
          </div>
          <div class="info-item">
            <label>보유 포인트</label>
            <span class="point">{{ member.point | parseKrw }}P</span>
          </div>
        </div>
      </div>

      <!-- 가입 커뮤니티 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-office-building"></i> 가입 커뮤니티</h3>
        <el-table
          :data="channelList"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column prop="name" label="커뮤니티명" />
          <el-table-column label="권한" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.isAdmin ? 'danger' : 'info'" size="small">
                {{ scope.row.isAdmin ? '관리자' : '멤버' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="가입일" width="160" align="center">
            <template slot-scope="scope">
              {{ scope.row.joinDate | parseDate('YYYY-MM-DD HH:mm') }}
            </template>
          </el-table-column>
        </el-table>
        <div v-if="channelList.length === 0" class="empty-message">
          가입된 커뮤니티가 없습니다.
        </div>
      </div>

      <!-- 가입 공간 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-folder"></i> 가입 공간</h3>
        <el-table
          :data="spaceList"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column prop="name" label="공간명" />
          <el-table-column prop="channelName" label="소속 커뮤니티" />
          <el-table-column label="타입" width="100" align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.spaceType === 'BOARD' ? 'primary' : 'success'" size="small">
                {{ scope.row.spaceType === 'BOARD' ? '게시판' : '채팅' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="가입일" width="160" align="center">
            <template slot-scope="scope">
              {{ scope.row.joinDate | parseDate('YYYY-MM-DD HH:mm') }}
            </template>
          </el-table-column>
        </el-table>
        <div v-if="spaceList.length === 0" class="empty-message">
          가입된 공간이 없습니다.
        </div>
      </div>

      <!-- 포인트 히스토리 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-coin"></i> 포인트 히스토리</h3>
        <el-table
          :data="pointHistory"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column label="일시" width="180" align="center">
            <template slot-scope="scope">
              {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="내용" />
          <el-table-column label="포인트" width="120" align="right">
            <template slot-scope="scope">
              <span :class="scope.row.point > 0 ? 'point-plus' : 'point-minus'">
                {{ scope.row.point > 0 ? '+' : '' }}{{ scope.row.point | parseKrw }}P
              </span>
            </template>
          </el-table-column>
          <el-table-column label="잔액" width="120" align="right">
            <template slot-scope="scope">
              {{ scope.row.balance | parseKrw }}P
            </template>
          </el-table-column>
        </el-table>
        <Pagination
          v-if="pointTotalElements > 0"
          :total="pointTotalElements"
          :page.sync="pointQuery.page"
          :limit.sync="pointQuery.size"
          @pagination="getPointHistory()"
        />
        <div v-if="pointHistory.length === 0" class="empty-message">
          포인트 내역이 없습니다.
        </div>
      </div>

      <!-- 활동 내역 -->
      <div class="detail-section">
        <h3 class="section-title"><i class="el-icon-time"></i> 활동 내역</h3>
        <el-table
          :data="activityLog"
          border
          style="width: 100%"
          :header-cell-style="{ background: '#667eea', color: '#fff', padding: '10px 0' }"
        >
          <el-table-column label="일시" width="180" align="center">
            <template slot-scope="scope">
              {{ scope.row.createDate | parseDate('YYYY-MM-DD HH:mm:ss') }}
            </template>
          </el-table-column>
          <el-table-column label="활동유형" width="120" align="center">
            <template slot-scope="scope">
              <el-tag :type="getActivityType(scope.row.activityType)" size="small">
                {{ getActivityText(scope.row.activityType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="내용" />
        </el-table>
        <Pagination
          v-if="activityTotalElements > 0"
          :total="activityTotalElements"
          :page.sync="activityQuery.page"
          :limit.sync="activityQuery.size"
          @pagination="getActivityLog()"
        />
        <div v-if="activityLog.length === 0" class="empty-message">
          활동 내역이 없습니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Pagination from '@/components/Pagination/index.vue';
import { getMember, getMemberChannels, getMemberSpaces, getMemberPointHistory, getMemberActivityLog } from '@/api/member';

@Component({
  components: {
    Pagination,
  },
})
export default class MemberDetail extends Vue {
  private loading = false;
  private member: any = {};
  private channelList: any[] = [];
  private spaceList: any[] = [];
  private pointHistory: any[] = [];
  private activityLog: any[] = [];

  private pointTotalElements = 0;
  private activityTotalElements = 0;

  private pointQuery = { page: 1, size: 5 };
  private activityQuery = { page: 1, size: 5 };

  created() {
    this.loadData();
  }

  private async loadData() {
    this.loading = true;
    try {
      const uid = this.$route.params.uid;

      // 회원 정보
      const { data: member } = await getMember(uid);
      this.member = member;

      // 가입 커뮤니티
      try {
        const { data: channels } = await getMemberChannels(uid);
        this.channelList = channels || [];
      } catch (e) {
        this.channelList = [];
      }

      // 가입 공간
      try {
        const { data: spaces } = await getMemberSpaces(uid);
        this.spaceList = spaces || [];
      } catch (e) {
        this.spaceList = [];
      }

      // 포인트 히스토리
      await this.getPointHistory();

      // 활동 내역
      await this.getActivityLog();

    } catch (error) {
      console.error(error);
      this.$message.error('데이터를 불러오는 중 오류가 발생했습니다.');
    } finally {
      this.loading = false;
    }
  }

  private async getPointHistory() {
    try {
      const uid = this.$route.params.uid;
      const { data } = await getMemberPointHistory(uid, this.pointQuery);
      this.pointHistory = data.content || [];
      this.pointTotalElements = data.totalElements || 0;
    } catch (e) {
      this.pointHistory = [];
    }
  }

  private async getActivityLog() {
    try {
      const uid = this.$route.params.uid;
      const { data } = await getMemberActivityLog(uid, this.activityQuery);
      this.activityLog = data.content || [];
      this.activityTotalElements = data.totalElements || 0;
    } catch (e) {
      this.activityLog = [];
    }
  }

  private getActivityType(type: string) {
    const types: any = {
      POST: 'primary',
      COMMENT: 'success',
      LIKE: 'warning',
      PURCHASE: 'danger',
    };
    return types[type] || 'info';
  }

  private getActivityText(type: string) {
    const texts: any = {
      POST: '게시글',
      COMMENT: '댓글',
      LIKE: '좋아요',
      PURCHASE: '구매',
    };
    return texts[type] || type;
  }
}
</script>

<style scoped>
.back-btn {
  font-size: 14px;
  color: #667eea;
  margin-left: 20px;
}
.back-btn:hover {
  text-decoration: underline;
}

.detail-content {
  margin-top: 20px;
}

.detail-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #667eea;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #667eea;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 5px;
}

.info-item span {
  font-size: 15px;
  font-weight: 500;
}

.point {
  color: #667eea;
  font-weight: 700;
  font-size: 18px;
}

.point-plus {
  color: #67c23a;
  font-weight: 600;
}

.point-minus {
  color: #f56c6c;
  font-weight: 600;
}

.empty-message {
  text-align: center;
  color: #909399;
  padding: 30px;
}
</style>
