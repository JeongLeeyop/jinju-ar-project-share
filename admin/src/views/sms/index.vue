<template>
  <div class="page-wrap">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">문자 발송</h1>
        <p class="page-subtitle">회원에게 문자 메시지를 발송합니다</p>
      </div>
      <div class="header-right">
        <div class="remain-count">
          <i class="el-icon-message"></i>
          <span>잔여 {{ remainCount }}건</span>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="form-section">
        <div class="content-card">
          <h3 class="section-title">메시지 작성</h3>
          
          <el-form :model="smsForm" :rules="rules" ref="smsForm" label-width="100px">
            <el-form-item label="발신번호" prop="senderPhone">
              <el-input v-model="smsForm.senderPhone" placeholder="01012345678" />
            </el-form-item>
            
            <el-form-item label="메시지 유형">
              <el-radio-group v-model="smsForm.messageType">
                <el-radio label="SMS">단문(SMS)</el-radio>
                <el-radio label="LMS">장문(LMS)</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="제목" v-if="smsForm.messageType === 'LMS'" prop="title">
              <el-input v-model="smsForm.title" placeholder="메시지 제목" maxlength="40" show-word-limit />
            </el-form-item>
            
            <el-form-item label="내용" prop="content">
              <el-input
                type="textarea"
                v-model="smsForm.content"
                :rows="6"
                :placeholder="contentPlaceholder"
                :maxlength="contentMaxLength"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleSend" :loading="sending" :disabled="selectedUsers.length === 0">
                <i class="el-icon-s-promotion"></i> 발송하기 ({{ selectedUsers.length }}명)
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
      <div class="recipient-section">
        <div class="content-card">
          <div class="section-header">
            <h3 class="section-title">수신자 선택</h3>
            <el-input
              v-model="searchValue"
              placeholder="이름, 연락처로 검색"
              prefix-icon="el-icon-search"
              class="search-input"
              size="small"
              @keyup.enter.native="loadUsers"
            />
          </div>
          
          <div class="selected-info" v-if="selectedUsers.length > 0">
            <span class="selected-count">{{ selectedUsers.length }}명 선택됨</span>
            <el-button type="text" size="mini" @click="clearSelection">선택 해제</el-button>
          </div>
          
          <el-table
            ref="userTable"
            :data="userList"
            style="width: 100%"
            max-height="400"
            :header-cell-style="headerStyle"
            @selection-change="handleSelectionChange"
            v-loading="userLoading"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column prop="username" label="이름" min-width="100" />
            <el-table-column label="연락처" width="130">
              <template slot-scope="scope">
                {{ formatPhone(scope.row.phone) }}
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-wrap">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="totalElements"
              :page-size="listQuery.size"
              :current-page.sync="listQuery.page"
              @current-change="handlePageChange"
              small
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { sendSms, getRemainCount, getUsersForSms } from '@/api/sms';

@Component({
  name: 'SmsSend',
})
export default class extends Vue {
  private sending = false;
  private userLoading = false;
  private remainCount = 0;
  private searchValue = '';
  private selectedUsers: any[] = [];
  private userList: any[] = [];
  private totalElements = 0;
  
  private listQuery = {
    page: 1,
    size: 10,
  };
  
  private smsForm = {
    senderPhone: '',
    messageType: 'SMS',
    title: '',
    content: '',
  };
  
  private rules = {
    senderPhone: [
      { required: true, message: '발신번호를 입력하세요', trigger: 'blur' },
    ],
    content: [
      { required: true, message: '메시지 내용을 입력하세요', trigger: 'blur' },
    ],
    title: [
      { required: true, message: '제목을 입력하세요', trigger: 'blur' },
    ],
  };
  
  private headerStyle = {
    background: '#f5f7fa',
    color: '#606266',
    fontWeight: '600',
  };

  get contentMaxLength() {
    return this.smsForm.messageType === 'SMS' ? 90 : 2000;
  }

  get contentPlaceholder() {
    return this.smsForm.messageType === 'SMS' 
      ? '최대 90자까지 입력 가능합니다' 
      : '최대 2000자까지 입력 가능합니다';
  }

  mounted() {
    this.loadRemainCount();
    this.loadUsers();
  }

  private async loadRemainCount() {
    try {
      const res = await getRemainCount();
      this.remainCount = res.data?.count || res.data || 0;
    } catch (error) {
      console.error('Failed to load remain count:', error);
      this.remainCount = 1000; // 기본값
    }
  }

  private async loadUsers() {
    this.userLoading = true;
    try {
      const res = await getUsersForSms({
        ...this.listQuery,
        searchValue: this.searchValue,
      });
      this.userList = res.data.content || res.data || [];
      this.totalElements = res.data.totalElements || this.userList.length;
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      this.userLoading = false;
    }
  }

  private handlePageChange(page: number) {
    this.listQuery.page = page;
    this.loadUsers();
  }

  private handleSelectionChange(val: any[]) {
    this.selectedUsers = val;
  }

  private clearSelection() {
    (this.$refs.userTable as any).clearSelection();
  }

  private async handleSend() {
    const valid = await (this.$refs.smsForm as any).validate().catch(() => false);
    if (!valid) return;
    
    if (this.selectedUsers.length === 0) {
      this.$message.warning('수신자를 선택하세요.');
      return;
    }
    
    this.$confirm(
      `${this.selectedUsers.length}명에게 문자를 발송하시겠습니까?`,
      '문자 발송',
      {
        confirmButtonText: '발송',
        cancelButtonText: '취소',
        type: 'info',
      }
    ).then(async () => {
      this.sending = true;
      try {
        await sendSms({
          receivers: this.selectedUsers.map((u) => u.phone),
          message: this.smsForm.content,
          title: this.smsForm.title,
          msgType: this.smsForm.messageType,
        });
        this.$message.success('문자가 발송되었습니다.');
        this.smsForm.content = '';
        this.smsForm.title = '';
        this.clearSelection();
        this.loadRemainCount();
        this.$router.push('/sms/history');
      } catch (error) {
        console.error('Failed to send SMS:', error);
        this.$message.error('문자 발송에 실패했습니다.');
      } finally {
        this.sending = false;
      }
    }).catch(() => {});
  }

  private formatPhone(phone: string) {
    if (!phone) return '-';
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
  }
}
</script>

<style scoped lang="scss">
.page-wrap {
  padding: 32px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  
  .page-title {
    font-size: 24px;
    font-weight: 700;
    color: #222;
    margin-bottom: 4px;
  }
  
  .page-subtitle {
    font-size: 14px;
    color: #888;
  }
  
  .remain-count {
    background: linear-gradient(135deg, #073DFF 0%, #5B7FFF 100%);
    color: #fff;
    padding: 10px 20px;
    border-radius: 24px;
    font-weight: 600;
    
    i {
      margin-right: 8px;
    }
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  
  @media (max-width: 1200px) {
    grid-template-columns: 1fr;
  }
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  
  .section-title {
    margin-bottom: 0;
  }
  
  .search-input {
    width: 200px;
  }
}

.selected-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f0f4ff;
  padding: 8px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  
  .selected-count {
    color: #073DFF;
    font-weight: 600;
    font-size: 14px;
  }
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

::v-deep .el-form-item__label {
  font-weight: 500;
}

::v-deep .el-radio-group {
  display: flex;
  gap: 20px;
}
</style>
