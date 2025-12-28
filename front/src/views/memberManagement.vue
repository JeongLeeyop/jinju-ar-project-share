<template>
  <div class="member-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'member-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="member-management-main">
      <div class="page-header">
        <h1 class="page-title">회원 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'members' }"
          @click="activeTab = 'members'"
        >
          회원 목록
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'pending' }"
          @click="activeTab = 'pending'"
        >
          가입 승인 대기
          <span v-if="pendingMembers.length > 0" class="badge">{{ pendingMembers.length }}</span>
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'banned' }"
          @click="activeTab = 'banned'"
        >
          추방된 회원
          <span v-if="bannedMembersCount > 0" class="badge">{{ bannedMembersCount }}</span>
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'permissions' }"
          @click="activeTab = 'permissions'"
        >
          권한 관리
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'points' }"
          @click="activeTab = 'points'; loadMembersWithPoints();"
        >
          포인트 관리
        </button>
      </div>

      <!-- 회원 목록 -->
      <div v-if="activeTab === 'members'" class="tab-content">
        <!-- 검색 및 필터 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
          <!-- 회원 초대하기 버튼 숨김 처리 -->
          <!-- <button class="invite-btn" @click="openInviteModal">
            <i class="el-icon-plus"></i>
            회원 초대하기
          </button> -->
        </div>

        <!-- 회원 테이블 -->
        <div v-if="loadingMembers" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredMembers.length > 0" class="members-table-wrapper">
          <table class="members-table">
            <thead>
              <tr>
                <th>회원명</th>
                <th>이메일/아이디</th>
                <th>가입일</th>
                <th>상태</th>
                <th>관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in filteredMembers" :key="member.uid">
                <td>
                  <div class="member-info">
                    <div class="member-avatar-placeholder">
                      <img v-if="member.user?.iconFileUid" :src="`${apiUrl}/attached-file/${member.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                      <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                          <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        </mask>
                        <g mask="url(#mask0_user)">
                          <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                          <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                        </g>
                      </svg>
                    </div>
                    <span class="member-name">{{ member.user?.actualName || '-' }}</span>
                  </div>
                </td>
                <td>{{ member.user?.email || member.user?.userId || '-' }}</td>
                <td>{{ formatDate(member.createDate) }}</td>
                <td>
                  <span class="status-badge" :class="getBadgeClass(member)">
                    {{ getStatusText(member) }}
                  </span>
                </td>
                <td>
                  <button 
                    v-if="!member.banned"
                    class="action-btn danger-outline" 
                    @click="confirmBanMember(member)"
                  >
                    추방
                  </button>
                  <button 
                    v-else
                    class="action-btn primary-outline" 
                    @click="confirmUnbanMember(member)"
                  >
                    추방 해제
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="empty-container">
          <p>회원이 없습니다</p>
        </div>
      </div>

      <!-- 가입 승인 대기 -->
      <div v-if="activeTab === 'pending'" class="tab-content">
        <div v-if="loadingPending" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="pendingMembers.length > 0" class="pending-list">
          <div 
            v-for="pending in pendingMembers" 
            :key="pending.uid"
            class="pending-card"
          >
            <div class="pending-header">
              <div class="member-info">
                <div class="member-avatar-large-placeholder">
                  <img v-if="pending.user?.iconFileUid" :src="`${apiUrl}/attached-file/${pending.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                  <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_user)">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="member-details">
                  <h3 class="member-name">{{ pending.user?.actualName || '-' }}</h3>
                  <p class="member-email">{{ pending.user?.email || pending.user?.userId || '-' }}</p>
                </div>
              </div>
              <span class="pending-date">{{ formatDate(pending.createDate) }}</span>
            </div>

            <div v-if="pending.introduce" class="pending-message">
              <p class="message-label">자기소개</p>
              <p class="message-content">{{ pending.introduce }}</p>
            </div>

            <div class="pending-actions">
              <button 
                v-if="pending.answerList && pending.answerList.length > 0"
                class="action-btn secondary" 
                @click="openSurveyModal(pending)"
              >
                설문조사 확인
              </button>
              <button 
                class="action-btn secondary" 
                @click="rejectMemberAction(pending)"
              >
                거절
              </button>
              <button 
                class="action-btn primary" 
                @click="approveMember(pending)"
              >
                승인
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>승인 대기 중인 회원이 없습니다</p>
        </div>
      </div>

      <!-- 추방된 회원 -->
      <div v-if="activeTab === 'banned'" class="tab-content">
        <!-- 검색 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="bannedSearchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
        </div>

        <div v-if="loadingMembers" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredBannedMembers.length > 0" class="members-table-wrapper">
          <table class="members-table">
            <thead>
              <tr>
                <th>회원명</th>
                <th>이메일/아이디</th>
                <th>가입일</th>
                <th>상태</th>
                <th>관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="member in filteredBannedMembers" :key="member.idx">
                <td>
                  <div class="member-info">
                    <div class="member-avatar-placeholder">
                      <img v-if="member.user?.iconFileUid" :src="`${apiUrl}/attached-file/${member.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                      <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        <mask id="mask0_user_banned" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                          <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        </mask>
                        <g mask="url(#mask0_user_banned)">
                          <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                          <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                        </g>
                      </svg>
                    </div>
                    <span class="member-name">{{ member.user?.actualName || '-' }}</span>
                  </div>
                </td>
                <td>{{ member.user?.email || member.user?.userId || '-' }}</td>
                <td>{{ formatDate(member.createDate) }}</td>
                <td>
                  <span class="status-badge banned">
                    추방됨
                  </span>
                </td>
                <td>
                  <button 
                    class="action-btn primary-outline" 
                    @click="confirmUnbanMember(member)"
                  >
                    추방 해제
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-else class="empty-container">
          <p>추방된 회원이 없습니다</p>
        </div>
      </div>

      <!-- 권한 관리 -->
      <div v-if="activeTab === 'permissions'" class="tab-content">
        <!-- 검색 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="permissionSearchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
        </div>

        <div v-if="loadingPermissions" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredPermissionMembers.length > 0" class="permissions-list">
          <div 
            v-for="member in filteredPermissionMembers" 
            :key="member.uid"
            class="permission-card"
          >
            <div class="permission-header">
              <div class="member-info">
                <div class="member-avatar-placeholder">
                  <img v-if="member.user?.iconFileUid" :src="`${apiUrl}/attached-file/${member.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                  <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_user)">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="member-details">
                  <h3 class="member-name">{{ member.user?.actualName || '-' }}</h3>
                  <p class="member-email">{{ member.user?.email || member.user?.userId || '-' }}</p>
                </div>
              </div>
              <button 
                class="action-btn primary" 
                @click="openPermissionModal(member)"
              >
                <i class="el-icon-edit"></i>
                권한 수정
              </button>
            </div>

            <div class="permission-tags">
              <span 
                v-for="perm in member.permissions" 
                :key="perm"
                class="permission-tag"
              >
                {{ getPermissionLabel(perm) }}
              </span>
              <span v-if="!member.permissions || member.permissions.length === 0" class="no-permission">
                부여된 권한 없음
              </span>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>회원이 없습니다</p>
        </div>
      </div>

      <!-- 포인트 관리 -->
      <div v-if="activeTab === 'points'" class="tab-content">
        <!-- 검색 -->
        <div class="filter-bar">
          <div class="search-box">
            <i class="el-icon-search"></i>
            <input 
              v-model="pointSearchKeyword" 
              type="text" 
              placeholder="회원 이름 또는 이메일로 검색"
              class="search-input"
            />
          </div>
        </div>

        <div v-if="loadingPoints" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="filteredPointMembers.length > 0" class="points-list">
          <div 
            v-for="member in filteredPointMembers" 
            :key="member.userUid"
            class="point-card"
          >
            <div class="point-header">
              <div class="member-info">
                <div class="member-avatar-placeholder">
                  <img v-if="member.iconFileUid" :src="`${apiUrl}/attached-file/${member.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                  <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_user)">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="member-details">
                  <h3 class="member-name">{{ member.actualName || '-' }}</h3>
                  <p class="member-email">{{ member.email || member.userId || '-' }}</p>
                </div>
              </div>
              <div class="point-info">
                <span class="point-balance">{{ member.currentPoint?.toLocaleString() || 0 }} P</span>
              </div>
            </div>

            <div class="point-actions">
              <button 
                class="action-btn primary-outline" 
                @click="openPointHistoryModal(member)"
              >
                <i class="el-icon-document"></i>
                포인트 내역
              </button>
              <button 
                class="action-btn primary" 
                @click="openPointAdjustModal(member, 'add')"
              >
                <i class="el-icon-plus"></i>
                지급
              </button>
              <button 
                class="action-btn danger-outline" 
                @click="openPointAdjustModal(member, 'deduct')"
              >
                <i class="el-icon-minus"></i>
                차감
              </button>
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>회원이 없습니다</p>
        </div>
      </div>
    </div>

    <!-- 포인트 지급/차감 모달 -->
    <el-dialog
      :visible.sync="pointAdjustModalVisible"
      width="500px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="point-adjust-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closePointAdjustModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">{{ pointAdjustType === 'add' ? '포인트 지급' : '포인트 차감' }}</h3>

        <div v-if="selectedPointMember" class="point-member-info">
          <div class="member-avatar-placeholder">
            <img v-if="selectedPointMember.iconFileUid" :src="`${apiUrl}/attached-file/${selectedPointMember.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
            <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
              <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
              </mask>
              <g mask="url(#mask0_user)">
                <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
              </g>
            </svg>
          </div>
          <div class="member-details">
            <h3 class="member-name">{{ selectedPointMember.actualName || '-' }}</h3>
            <p class="member-current-point">현재 보유: {{ selectedPointMember.currentPoint?.toLocaleString() || 0 }} P</p>
          </div>
        </div>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">{{ pointAdjustType === 'add' ? '지급할 포인트' : '차감할 포인트' }}</label>
            <input 
              v-model.number="pointAdjustForm.amount" 
              type="number" 
              min="1"
              :placeholder="pointAdjustType === 'add' ? '지급할 포인트 입력' : '차감할 포인트 입력'"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">사유</label>
            <textarea 
              v-model="pointAdjustForm.description" 
              placeholder="포인트 지급/차감 사유를 입력해주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>

          <div v-if="pointAdjustForm.amount > 0" class="point-preview">
            <div class="preview-row">
              <span>현재 포인트</span>
              <span>{{ selectedPointMember?.currentPoint?.toLocaleString() || 0 }} P</span>
            </div>
            <div class="preview-row" :class="pointAdjustType === 'add' ? 'add' : 'deduct'">
              <span>{{ pointAdjustType === 'add' ? '지급' : '차감' }}</span>
              <span>{{ pointAdjustType === 'add' ? '+' : '-' }}{{ pointAdjustForm.amount?.toLocaleString() || 0 }} P</span>
            </div>
            <div class="preview-row result">
              <span>예상 잔액</span>
              <span>{{ expectedBalance.toLocaleString() }} P</span>
            </div>
          </div>
        </div>

        <button 
          class="submit-btn" 
          :class="{ 'deduct': pointAdjustType === 'deduct' }"
          @click="submitPointAdjust"
          :disabled="!pointAdjustForm.amount || !pointAdjustForm.description || submittingPoint"
        >
          {{ submittingPoint ? '처리 중...' : (pointAdjustType === 'add' ? '포인트 지급' : '포인트 차감') }}
        </button>
      </div>
    </el-dialog>

    <!-- 포인트 내역 모달 -->
    <el-dialog
      :visible.sync="pointHistoryModalVisible"
      width="700px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="point-history-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closePointHistoryModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">포인트 내역</h3>

        <div v-if="selectedPointMember" class="point-member-info">
          <div class="member-avatar-placeholder">
            <img v-if="selectedPointMember.iconFileUid" :src="`${apiUrl}/attached-file/${selectedPointMember.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
          </div>
          <div class="member-details">
            <h3 class="member-name">{{ selectedPointMember.actualName || '-' }}</h3>
            <p class="member-current-point">현재 보유: {{ pointHistoryCurrentPoint?.toLocaleString() || 0 }} P</p>
          </div>
        </div>

        <div v-if="loadingPointHistory" class="loading-container">
          <i class="el-icon-loading"></i>
          <p>로딩 중...</p>
        </div>

        <div v-else-if="pointHistoryList.length > 0" class="point-history-list">
          <div 
            v-for="history in pointHistoryList" 
            :key="history.id"
            class="history-item"
          >
            <div class="history-info">
              <p class="history-description">{{ history.description }}</p>
              <p class="history-date">{{ formatDateTime(history.createdAt) }}</p>
            </div>
            <div class="history-amount" :class="history.pointAmount >= 0 ? 'add' : 'deduct'">
              {{ history.pointAmount >= 0 ? '+' : '' }}{{ history.pointAmount?.toLocaleString() }} P
            </div>
          </div>
        </div>

        <div v-else class="empty-container">
          <p>포인트 내역이 없습니다</p>
        </div>
      </div>
    </el-dialog>

    <!-- 회원 초대 모달 -->
    <el-dialog
      :visible.sync="inviteModalVisible"
      width="600px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="invite-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closeInviteModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">회원 초대하기</h3>

        <div class="form-section">
          <div class="form-group">
            <label class="form-label">초대할 이메일</label>
            <input 
              v-model="inviteForm.email" 
              type="email" 
              placeholder="이메일 주소를 입력해주세요"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label class="form-label">초대 가능한 공간 선택</label>
            <div class="space-checkboxes">
              <label 
                v-for="space in availableSpaces" 
                :key="space.uid"
                class="space-checkbox-item"
              >
                <input 
                  type="checkbox" 
                  :value="space.uid"
                  v-model="inviteForm.spaceUids"
                />
                <span class="space-checkbox-label">{{ space.name }}</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">초대 메시지 (선택)</label>
            <textarea 
              v-model="inviteForm.message" 
              placeholder="초대 메시지를 입력해주세요"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>

        <button 
          class="submit-btn" 
          @click="submitInvite"
          :disabled="!inviteForm.email || submittingInvite"
        >
          {{ submittingInvite ? '전송 중...' : '초대 보내기' }}
        </button>
      </div>
    </el-dialog>

    <!-- 설문조사 확인 모달 -->
    <el-dialog
      :visible.sync="surveyModalVisible"
      width="700px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="survey-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closeSurveyModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">가입 설문조사</h3>

        <div v-if="selectedPending" class="survey-content">
          <div class="survey-applicant">
            <div class="member-avatar-large-placeholder">
              <img v-if="selectedPending.user?.iconFileUid" :src="`${apiUrl}/attached-file/${selectedPending.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
              <svg v-else width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                <mask id="mask0_survey_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
                  <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_survey_user)">
                  <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
                  <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
                </g>
              </svg>
            </div>
            <div class="applicant-info">
              <h4>{{ selectedPending.user?.actualName || '-' }}</h4>
              <p>{{ selectedPending.user?.email || selectedPending.user?.userId || '-' }}</p>
            </div>
          </div>

          <div v-if="selectedPending.introduce" class="survey-item">
            <h4 class="survey-question">자기소개</h4>
            <p class="survey-answer">{{ selectedPending.introduce }}</p>
          </div>

          <div class="survey-questions">
            <div 
              v-for="(answer, index) in selectedPending.answerList" 
              :key="index"
              class="survey-item"
            >
              <h4 class="survey-question">{{ index + 1 }}. {{ answer.title || '질문' }}</h4>
              <p class="survey-answer">{{ answer.answer || '응답 없음' }}</p>
            </div>
            <div v-if="!selectedPending.answerList || selectedPending.answerList.length === 0" class="survey-item">
              <p class="survey-answer">설문 응답이 없습니다.</p>
            </div>
          </div>

          <div class="survey-actions">
            <button 
              class="action-btn secondary" 
              @click="rejectFromSurvey"
            >
              거절
            </button>
            <button 
              class="action-btn primary" 
              @click="approveFromSurvey"
            >
              승인
            </button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 권한 수정 모달 -->
    <el-dialog
      :visible.sync="permissionModalVisible"
      width="650px"
      center
      :show-close="false"
      :append-to-body="true"
      :modal-append-to-body="true"
      :close-on-click-modal="false"
      custom-class="permission-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="closePermissionModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h3 class="modal-title">회원 권한 설정</h3>

        <div v-if="selectedMember" class="permission-member-info">
          <div class="member-avatar-placeholder">
            <img v-if="selectedMember.user?.iconFileUid" :src="`${apiUrl}/attached-file/${selectedMember.user.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
            <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
              <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
              </mask>
              <g mask="url(#mask0_user)">
                <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
              </g>
            </svg>
          </div>
          <div class="member-details">
            <h4>{{ selectedMember.user?.actualName || '-' }}</h4>
            <p>{{ selectedMember.user?.email || selectedMember.user?.userId || '-' }}</p>
          </div>
        </div>

        <div class="permission-section">
          <p class="permission-section-label">부여할 권한을 선택해주세요 (다중 선택 가능)</p>
          
          <div class="permission-checkboxes">
            <label 
              v-for="perm in availablePermissions" 
              :key="perm.value"
              class="permission-checkbox-item"
            >
              <input 
                type="checkbox" 
                :value="perm.value"
                v-model="permissionForm.permissions"
              />
              <span class="permission-checkbox-label">
                {{ perm.label }}
              </span>
            </label>
          </div>
        </div>

        <button 
          class="submit-btn" 
          @click="submitPermission"
          :disabled="submittingPermission"
        >
          {{ submittingPermission ? '저장 중...' : '권한 저장' }}
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { getChannelMemberList, approval, rejectMember, removeMember, unbanMember } from '@/api/channelMember';
import { getMemberPermissions, bulkUpdatePermissions, getAllPermissionTypes } from '@/api/channelMemberPermission';
import { getChannelDomainDetail } from '@/api/channel';
import { getUserInfo } from '@/api/user';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { getTokenDecode } from '@/utils/cookies';
import { getChannelMembersWithPoints, adminAdjustPoint, getTargetUserPointHistory, MemberWithPoint, PointHistory } from '@/api/point';
import { EventBus, EVENTS } from '@/utils/eventBus';

// API 응답에 맞는 인터페이스
interface Member {
  idx: number;
  uid: string;
  userUid: string;
  channelUid: string;
  approvalStatus: boolean;
  banned: boolean;
  createDate: string;
  user?: {
    uid: string;
    userId: string;
    actualName: string;
    email?: string;
    iconFileUid?: string;
  };
  permissions: string[];
  isOnline?: boolean;
}

interface PendingMember {
  idx: number;
  uid: string;
  userUid: string;
  channelUid: string;
  approvalStatus: boolean;
  createDate: string;
  user?: {
    uid: string;
    userId: string;
    actualName: string;
    email?: string;
    iconFileUid?: string;
  };
  introduce?: string;
  answerList?: any[];
}

interface Space {
  uid: string;
  name: string;
  spaceType: string;
}

interface Channel {
  uid: string;
  name: string;
  userUid: string;
  domain: string;
}

@Component({
  name: 'MemberManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  private activeTab: 'members' | 'pending' | 'permissions' | 'banned' | 'points' = 'members';
  private loadingMembers = false;
  private loadingPending = false;
  private loadingPermissions = false;
  private searchKeyword = '';
  private permissionSearchKeyword = '';
  private bannedSearchKeyword = '';
  private pointSearchKeyword = '';

  // 포인트 관리 관련
  private loadingPoints = false;
  private membersWithPoints: MemberWithPoint[] = [];
  private selectedPointMember: MemberWithPoint | null = null;
  private pointAdjustModalVisible = false;
  private pointAdjustType: 'add' | 'deduct' = 'add';
  private submittingPoint = false;
  private pointAdjustForm = {
    amount: 0,
    description: '',
  };

  // 포인트 내역 관련
  private pointHistoryModalVisible = false;
  private loadingPointHistory = false;
  private pointHistoryList: PointHistory[] = [];
  private pointHistoryCurrentPoint = 0;

  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  // 채널 정보 및 관리자 여부
  private currentChannel: Channel | null = null;
  private isChannelAdmin = false;
  private checkingAdminStatus = false;
  private currentUserUid = ''; // 현재 로그인한 사용자의 UID

  // 페이지네이션
  private currentPage = 1;
  private pageSize = 20;
  private totalMembers = 0;

  // 회원 목록 (API에서 로드)
  private members: Member[] = [];

  // 가입 승인 대기 (API에서 로드)
  private pendingMembers: PendingMember[] = [];

  // 초대 가능한 공간 (API에서 로드 또는 필요시 유지)
  private availableSpaces: Space[] = [];

  // 초대 모달
  private inviteModalVisible = false;
  private submittingInvite = false;
  private inviteForm = {
    email: '',
    spaceUids: [] as string[],
    message: '',
  };

  // 설문조사 모달
  private surveyModalVisible = false;
  private selectedPending: PendingMember | null = null;

  // 권한 모달
  private permissionModalVisible = false;
  private submittingPermission = false;
  private selectedMember: Member | null = null;
  private permissionForm = {
    permissions: [] as string[],
  };

  // 사용 가능한 권한 목록 (백엔드 Enum과 일치하도록 수정)
  private availablePermissions = [
    { value: 'POST_CREATE', label: '게시글 등록' },
    { value: 'POST_UPDATE', label: '게시글 수정' },
    { value: 'POST_DELETE', label: '게시글 삭제' },
    { value: 'SPACE_CREATE', label: '공간 생성' },
    { value: 'MARKETPLACE_USE', label: '장터 이용' },
    { value: 'MARKETPLACE_REGISTER', label: '장터 등록' },
    { value: 'OFFLINE_MARKETPLACE_REGISTER', label: '오프라인 장터 등록' },
    { value: 'SCHEDULE_PARTICIPATE', label: '일정 참여' },
  ];

  get filteredMembers() {
    // 추방된 회원 제외
    let members = this.members.filter(m => !m.banned);
    
    if (!this.searchKeyword) return members;
    const keyword = this.searchKeyword.toLowerCase();
    return members.filter(member => 
      (member.user?.actualName || '').toLowerCase().includes(keyword) || 
      (member.user?.email || '').toLowerCase().includes(keyword)
    );
  }

  get filteredPermissionMembers() {
    // 추방된 회원 제외
    let members = this.members.filter(m => !m.banned);
    
    if (!this.permissionSearchKeyword) return members;
    const keyword = this.permissionSearchKeyword.toLowerCase();
    return members.filter(member => 
      (member.user?.actualName || '').toLowerCase().includes(keyword) || 
      (member.user?.email || '').toLowerCase().includes(keyword)
    );
  }

  get filteredBannedMembers() {
    if (!this.bannedSearchKeyword) return this.members.filter(m => m.banned);
    const keyword = this.bannedSearchKeyword.toLowerCase();
    return this.members.filter(member => 
      (member.user?.actualName || '').toLowerCase().includes(keyword) || 
      (member.user?.email || '').toLowerCase().includes(keyword)
    ).filter(m => m.banned);
  }

  get filteredPointMembers() {
    if (!this.pointSearchKeyword) return this.membersWithPoints;
    const keyword = this.pointSearchKeyword.toLowerCase();
    return this.membersWithPoints.filter(member => 
      (member.actualName || '').toLowerCase().includes(keyword) || 
      (member.email || '').toLowerCase().includes(keyword) ||
      (member.userId || '').toLowerCase().includes(keyword)
    );
  }

  get expectedBalance() {
    if (!this.selectedPointMember) return 0;
    const currentPoint = this.selectedPointMember.currentPoint || 0;
    const amount = this.pointAdjustForm.amount || 0;
    if (this.pointAdjustType === 'add') {
      return currentPoint + amount;
    } else {
      return Math.max(0, currentPoint - amount);
    }
  }

  get bannedMembersCount() {
    return this.members.filter(m => m.banned).length;
  }

  get channelUid() {
    // ChannelModule에서 실제 channelUid를 가져옴 (domain이 아닌 uid)
    // fallback: currentChannel.uid (checkChannelAdminStatus에서 설정됨)
    return ChannelModule.selectedChannel?.uid || this.currentChannel?.uid || '';
  }

  get channelDomain() {
    return this.$route.params.domain || '';
  }

  async mounted() {
    await this.loadCurrentUser(); // 현재 사용자 정보 먼저 로드 (완료될 때까지 대기)
    await this.checkChannelAdminStatus(); // 채널 정보 로드 완료 대기 (channelUid 설정)
    
    // ChannelModule에 selectedChannel이 설정되어 있지 않으면 명시적으로 설정
    if (!ChannelModule.selectedChannel?.uid && this.currentChannel) {
      console.log('⚠️ ChannelModule.selectedChannel이 없어서 수동 설정');
      ChannelModule.setSelectedChannel({
        uid: this.currentChannel.uid,
        name: this.currentChannel.name,
        domain: this.currentChannel.domain,
      });
    }
    
    this.loadMembers();
    this.loadPendingMembers();
  }

  @Watch('$route.params.domain')
  private async onDomainChange() {
    await this.loadCurrentUser(); // 도메인 변경 시에도 사용자 정보 재로드
    await this.checkChannelAdminStatus(); // 채널 정보 로드 완료 대기
    
    // ChannelModule에 selectedChannel이 설정되어 있지 않으면 명시적으로 설정
    if (!ChannelModule.selectedChannel?.uid && this.currentChannel) {
      console.log('⚠️ ChannelModule.selectedChannel이 없어서 수동 설정 (onDomainChange)');
      ChannelModule.setSelectedChannel({
        uid: this.currentChannel.uid,
        name: this.currentChannel.name,
        domain: this.currentChannel.domain,
      });
    }
    
    this.loadMembers();
    this.loadPendingMembers();
  }

  /**
   * 현재 로그인한 사용자 정보 로드
   */
  private async loadCurrentUser() {
    try {
      const response = await getUserInfo();
      if (response.data && response.data.uid) {
        this.currentUserUid = response.data.uid;
        console.log('✅ 현재 사용자 UID 로드 성공:', this.currentUserUid);
      } else {
        console.error('❌ 사용자 정보에 UID가 없습니다:', response.data);
      }
    } catch (error) {
      console.error('❌ 현재 사용자 정보 로드 실패:', error);
    }
  }

  /**
   * 채널 관리자 여부 확인
   */
  private async checkChannelAdminStatus() {
    if (!this.channelDomain) return;

    try {
      this.checkingAdminStatus = true;

      // 채널 정보 조회 (domain으로 조회)
      const channelResponse = await getChannelDomainDetail(this.channelDomain);
      this.currentChannel = channelResponse.data;

      console.log('=== 채널 관리자 체크 ===');
      console.log('현재 사용자 UID:', this.currentUserUid);
      console.log('채널 관리자 UID:', this.currentChannel?.userUid);

      if (!this.currentUserUid) {
        console.warn('⚠️ 현재 사용자 UID가 없습니다. (아직 로드 중)');
        this.isChannelAdmin = false;
        return;
      }

      // 채널 관리자 여부 확인
      if (this.currentChannel && this.currentChannel.userUid === this.currentUserUid) {
        this.isChannelAdmin = true;
        console.log('✅ 채널 관리자입니다.');
      } else {
        this.isChannelAdmin = false;
        console.log('❌ 채널 관리자가 아닙니다.');
      }
    } catch (error) {
      console.error('채널 정보 조회 실패:', error);
      this.isChannelAdmin = false;
    } finally {
      this.checkingAdminStatus = false;
    }
  }

  /**
   * 승인된 회원 목록 조회
   */
  private async loadMembers() {
    if (!this.channelUid) return;

    try {
      this.loadingMembers = true;
      this.loadingPermissions = true;
      
      const response = await getChannelMemberList({
        channelUid: this.channelUid,
        isHolding: false, // 승인된 회원만
        excludeBanned: false, // 추방된 회원도 포함 (프론트에서 필터링)
        page: this.currentPage,
        size: this.pageSize,
      });

      if (response.data && response.data.content) {
        const members = response.data.content;
        
        // 각 멤버의 권한 정보를 병렬로 로드
        const membersWithPermissions: Member[] = await Promise.all(
          members.map(async (m: any): Promise<Member> => {
            try {
              const permResponse = await getMemberPermissions(m.idx);
              const permissions = permResponse.data && permResponse.data.permissions
                ? permResponse.data.permissions
                    .filter((p: any) => p.hasPermission)
                    .map((p: any) => p.permissionType)
                : [];
              
              return {
                ...m,
                permissions,
              } as Member;
            } catch (error) {
              console.error(`회원 ${m.idx} 권한 로드 실패:`, error);
              // 권한 로드 실패 시 빈 배열
              return {
                ...m,
                permissions: [],
              } as Member;
            }
          })
        );

        this.members = membersWithPermissions;
        this.totalMembers = response.data.totalElements || 0;
        
        // 디버깅: 추방된 회원 확인
        const bannedCount = this.members.filter(m => m.banned).length;
        console.log('=== 회원 목록 로드 완료 ===');
        console.log('총 회원 수:', this.members.length);
        console.log('추방된 회원 수:', bannedCount);
        
        // 각 멤버의 banned 상태 출력
        this.members.forEach((m, index) => {
          console.log(`회원 ${index + 1}: ${m.user?.actualName || '이름없음'} (banned: ${m.banned})`);
        });
        
        if (bannedCount > 0) {
          console.log('추방된 회원 상세:', this.members.filter(m => m.banned).map(m => ({
            name: m.user?.actualName,
            email: m.user?.email,
            banned: m.banned,
            idx: m.idx
          })));
        }
      }
    } catch (error) {
      console.error('회원 목록 조회 실패:', error);
      this.$message.error('회원 목록을 불러오지 못했습니다.');
    } finally {
      this.loadingMembers = false;
      this.loadingPermissions = false;
    }
  }

  /**
   * 가입 대기 회원 목록 조회
   */
  private async loadPendingMembers() {
    if (!this.channelUid) return;

    try {
      this.loadingPending = true;
      
      const response = await getChannelMemberList({
        channelUid: this.channelUid,
        isHolding: true, // 대기중인 회원만
        page: 1,
        size: 100,
      });

      if (response.data && response.data.content) {
        this.pendingMembers = response.data.content;
      }
    } catch (error) {
      console.error('가입 대기 목록 조회 실패:', error);
      this.$message.error('가입 대기 목록을 불러오지 못했습니다.');
    } finally {
      this.loadingPending = false;
    }
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  private formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
  }

  private getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      active: '활동중',
      banned: '추방됨',
    };
    return labels[status] || status;
  }

  // 회원 상태 텍스트
  private getStatusText(member: Member): string {
    if (member.banned) {
      return '추방됨';
    }
    return member.approvalStatus ? '활동중' : '대기중';
  }

  // 상태 뱃지 클래스
  private getBadgeClass(member: Member): string {
    if (member.banned) {
      return 'banned';
    }
    return member.approvalStatus ? 'active' : 'pending';
  }

  // 회원 추방 확인
  private async confirmBanMember(member: Member) {
    const memberName = member.user?.actualName || '회원';
    try {
      await this.$confirm(
        `${memberName}님을 추방하시겠습니까? 추방된 회원은 커뮤니티에 접근할 수 없습니다.`,
        '회원 추방',
        {
          confirmButtonText: '추방',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );
      await this.banMember(member);
    } catch (error) {
      if (error === 'cancel') return;
    }
  }

  // 회원 추방
  private async banMember(member: Member) {
    const memberName = member.user?.actualName || '회원';
    try {
      // 실제 API 호출 - idx를 사용
      await removeMember(member.idx);

      // 로컬 데이터 업데이트 (목록에서 제거하지 않고 상태만 변경)
      const index = this.members.findIndex(m => m.idx === member.idx);
      if (index > -1) {
        // Vue 2 반응성 보장
        const updatedMembers = [...this.members];
        updatedMembers[index] = {
          ...updatedMembers[index],
          banned: true,
        };
        this.members = updatedMembers;
      }

      this.$message.success(`${memberName}님이 추방되었습니다`);
    } catch (error: any) {
      const message = error.response?.data?.message || '회원 추방에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 회원 추방 해제 확인
  private async confirmUnbanMember(member: Member) {
    const memberName = member.user?.actualName || '회원';
    try {
      await this.$confirm(
        `${memberName}님의 추방을 해제하시겠습니까?`,
        '추방 해제',
        {
          confirmButtonText: '해제',
          cancelButtonText: '취소',
          type: 'info',
        },
      );
      await this.unbanMember(member);
    } catch (error) {
      if (error === 'cancel') return;
    }
  }

  // 회원 추방 해제
  private async unbanMember(member: Member) {
    const memberName = member.user?.actualName || '회원';
    try {
      // 실제 API 호출 - idx를 사용
      await unbanMember(member.idx);

      // 로컬 데이터 업데이트
      const index = this.members.findIndex(m => m.idx === member.idx);
      if (index > -1) {
        // Vue 2 반응성 보장
        const updatedMembers = [...this.members];
        updatedMembers[index] = {
          ...updatedMembers[index],
          banned: false,
        };
        this.members = updatedMembers;
      }

      this.$message.success(`${memberName}님의 추방이 해제되었습니다`);
    } catch (error: any) {
      const message = error.response?.data?.message || '추방 해제에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 가입 승인
  private async approveMember(pending: PendingMember) {
    const memberName = pending.user?.actualName || '회원';
    try {
      // 실제 API 호출
      await approval(pending.uid);

      // 목록에서 제거
      const index = this.pendingMembers.findIndex(m => m.uid === pending.uid);
      if (index > -1) {
        this.pendingMembers.splice(index, 1);
      }

      // 회원 목록 새로고침
      await this.loadMembers();

      this.$message.success(`${memberName}님의 가입을 승인했습니다`);
    } catch (error: any) {
      const message = error.response?.data?.message || '가입 승인에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 가입 거절
  private async rejectMemberAction(pending: PendingMember) {
    const memberName = pending.user?.actualName || '회원';
    try {
      await this.$confirm(
        `${memberName}님의 가입 신청을 거절하시겠습니까?`,
        '가입 거절',
        {
          confirmButtonText: '거절',
          cancelButtonText: '취소',
          type: 'warning',
        },
      );

      // 실제 API 호출
      await rejectMember(pending.uid);

      // 목록에서 제거
      const index = this.pendingMembers.findIndex(m => m.uid === pending.uid);
      if (index > -1) {
        this.pendingMembers.splice(index, 1);
      }

      this.$message.success(`${memberName}님의 가입 신청을 거절했습니다`);
    } catch (error: any) {
      if (error === 'cancel') return;
      const message = error.response?.data?.message || '가입 거절에 실패했습니다';
      this.$message.error(message);
    }
  }

  // 초대 모달 열기
  private openInviteModal() {
    this.inviteForm = {
      email: '',
      spaceUids: [],
      message: '',
    };
    this.inviteModalVisible = true;
  }

  // 초대 모달 닫기
  private closeInviteModal() {
    this.inviteModalVisible = false;
  }

  // 초대 보내기
  private async submitInvite() {
    if (!this.inviteForm.email) return;

    try {
      this.submittingInvite = true;

      // TODO: 실제 API 호출
      // await inviteMember({
      //   email: this.inviteForm.email,
      //   spaceUids: this.inviteForm.spaceUids,
      //   message: this.inviteForm.message,
      // });

      // 임시 딜레이
      await new Promise(resolve => setTimeout(resolve, 1000));

      this.$message.success('초대를 보냈습니다');
      this.closeInviteModal();
    } catch (error: any) {
      const message = error.response?.data?.message || '초대 전송에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingInvite = false;
    }
  }

  // 설문조사 모달 열기
  private openSurveyModal(pending: PendingMember) {
    this.selectedPending = pending;
    this.surveyModalVisible = true;
  }

  // 설문조사 모달 닫기
  private closeSurveyModal() {
    this.surveyModalVisible = false;
    this.selectedPending = null;
  }

  // 설문조사 모달에서 승인
  private async approveFromSurvey() {
    if (!this.selectedPending) return;
    
    const pending = this.selectedPending;
    this.closeSurveyModal();
    await this.approveMember(pending);
  }

  // 설문조사 모달에서 거절
  private async rejectFromSurvey() {
    if (!this.selectedPending) return;
    
    const pending = this.selectedPending;
    this.closeSurveyModal();
    await this.rejectMemberAction(pending);
  }

  // 권한 라벨 가져오기
  private getPermissionLabel(value: string): string {
    const perm = this.availablePermissions.find(p => p.value === value);
    return perm ? perm.label : value;
  }

  // 권한 모달 열기
  private async openPermissionModal(member: Member) {
    console.log('=== 권한 모달 열기 ===');
    console.log('isChannelAdmin:', this.isChannelAdmin);
    console.log('현재 사용자 UID:', this.currentUserUid);
    console.log('채널 관리자 UID:', this.currentChannel?.userUid);
    console.log('===================');

    // 채널 관리자가 아닌 경우 경고
    if (!this.isChannelAdmin) {
      this.$message.warning('권한 관리는 채널 관리자만 이용할 수 있습니다.');
      return;
    }

    this.selectedMember = member;
    // 로컬에 저장된 권한으로 초기화 (서버에서 다시 로드할 때까지 표시)
    this.permissionForm.permissions = [...(member.permissions || [])];
    this.permissionModalVisible = true;

    // 서버에서 최신 권한 로드
    try {
      const response = await getMemberPermissions(member.idx);
      if (response.data && response.data.permissions) {
        this.permissionForm.permissions = response.data.permissions
          .filter((p: any) => p.hasPermission)
          .map((p: any) => p.permissionType);
      }
    } catch (error: any) {
      console.error('권한 로드 실패:', error);
      const message = error.response?.data?.message || '권한을 불러오지 못했습니다';
      
      // 권한이 없거나 채널 멤버가 아닌 경우
      if (message.includes('채널 관리자만') || message.includes('채널 멤버가 아닙니다') || message.includes('권한이 없습니다')) {
        this.$message.error(message);
        this.closePermissionModal();
        return;
      }
      
      // 에러 발생 시 로컬 데이터 유지
      console.log('로컬 권한 데이터 사용:', member.permissions);
    }
  }

  // 권한 모달 닫기
  private closePermissionModal() {
    this.permissionModalVisible = false;
    this.selectedMember = null;
    this.permissionForm.permissions = [];
  }

  // 권한 저장
  private async submitPermission() {
    if (!this.selectedMember) return;

    try {
      this.submittingPermission = true;

      // 권한 데이터 구성
      const permissionData = this.availablePermissions.map(perm => ({
        permissionType: perm.value,
        hasPermission: this.permissionForm.permissions.includes(perm.value),
      }));

      // 실제 API 호출
      await bulkUpdatePermissions({
        channelMemberIdx: this.selectedMember.idx,
        permissions: permissionData,
      });

      // 로컬 데이터 업데이트
      const updatedPermissions = [...this.permissionForm.permissions];
      
      // members 배열에서 해당 멤버를 찾아서 권한 업데이트
      const memberIndex = this.members.findIndex(m => m.idx === this.selectedMember!.idx);
      if (memberIndex > -1) {
        // Vue 2의 반응성을 보장하기 위해 전체 배열을 새로 생성
        const updatedMembers = [...this.members];
        updatedMembers[memberIndex] = {
          ...updatedMembers[memberIndex],
          permissions: updatedPermissions,
        };
        this.members = updatedMembers;
      }

      // selectedMember도 업데이트 (모달에서 다시 열었을 때를 위해)
      this.selectedMember.permissions = updatedPermissions;

      this.$message.success('권한이 저장되었습니다');
      this.closePermissionModal();
    } catch (error: any) {
      const message = error.response?.data?.message || '권한 저장에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingPermission = false;
    }
  }

  // ========== 포인트 관리 관련 메서드 ==========

  /**
   * 포인트가 있는 회원 목록 조회
   */
  private async loadMembersWithPoints() {
    console.log('=== 포인트 회원 조회 ===');
    console.log('channelUid:', this.channelUid);
    console.log('channelDomain:', this.channelDomain);
    console.log('ChannelModule.selectedChannel:', ChannelModule.selectedChannel);
    console.log('currentChannel:', this.currentChannel);
    
    // channelUid가 domain인지 확인
    const channelUid = this.channelUid;
    if (!channelUid) {
      console.error('❌ channelUid가 없습니다!');
      this.$message.error('채널 정보를 불러오지 못했습니다. 페이지를 새로고침해주세요.');
      return;
    }
    
    // channelUid가 실제 UUID 형식인지 간단히 체크 (domain이 아닌지)
    if (channelUid === this.channelDomain) {
      console.error('❌ channelUid가 domain과 같습니다! (UUID가 아님)');
      console.error('channelUid:', channelUid);
      console.error('channelDomain:', this.channelDomain);
      this.$message.error('채널 UID를 가져올 수 없습니다. 페이지를 새로고침해주세요.');
      return;
    }

    // 채널 관리자가 아닌 경우 경고
    if (!this.isChannelAdmin) {
      this.$message.warning('포인트 관리는 채널 관리자만 이용할 수 있습니다.');
      this.activeTab = 'members';
      return;
    }

    try {
      this.loadingPoints = true;
      
      console.log('✅ 포인트 API 호출 - channelUid:', channelUid);
      const response = await getChannelMembersWithPoints({
        channelUid: channelUid,
        page: 0,
        size: 100,
      });

      if (response.data && response.data.content) {
        this.membersWithPoints = response.data.content;
      }
    } catch (error: any) {
      console.error('포인트 회원 목록 조회 실패:', error);
      const message = error.response?.data?.message || '포인트 회원 목록을 불러오지 못했습니다';
      this.$message.error(message);
    } finally {
      this.loadingPoints = false;
    }
  }

  /**
   * 포인트 지급/차감 모달 열기
   */
  private openPointAdjustModal(member: MemberWithPoint, type: 'add' | 'deduct') {
    // 채널 관리자가 아닌 경우 경고
    if (!this.isChannelAdmin) {
      this.$message.warning('포인트 지급/차감은 채널 관리자만 가능합니다.');
      return;
    }

    this.selectedPointMember = member;
    this.pointAdjustType = type;
    this.pointAdjustForm = {
      amount: 0,
      description: '',
    };
    this.pointAdjustModalVisible = true;
  }

  /**
   * 포인트 지급/차감 모달 닫기
   */
  private closePointAdjustModal() {
    this.pointAdjustModalVisible = false;
    this.selectedPointMember = null;
    this.pointAdjustForm = {
      amount: 0,
      description: '',
    };
  }

  /**
   * 포인트 지급/차감 처리
   */
  private async submitPointAdjust() {
    if (!this.selectedPointMember || !this.pointAdjustForm.amount || !this.pointAdjustForm.description) {
      return;
    }

    // 차감 시 현재 포인트보다 많이 차감하려는 경우 경고
    if (this.pointAdjustType === 'deduct') {
      const currentPoint = this.selectedPointMember.currentPoint || 0;
      if (this.pointAdjustForm.amount > currentPoint) {
        this.$message.warning('현재 포인트보다 많은 포인트를 차감할 수 없습니다.');
        return;
      }
    }

    try {
      this.submittingPoint = true;

      // 포인트 금액 계산 (차감 시 음수)
      const pointAmount = this.pointAdjustType === 'add' 
        ? this.pointAdjustForm.amount 
        : -this.pointAdjustForm.amount;

      await adminAdjustPoint({
        channelUid: this.channelUid,
        targetUserUid: this.selectedPointMember.userUid,
        pointAmount: pointAmount,
        description: this.pointAdjustForm.description,
      });

      const memberName = this.selectedPointMember.actualName || '회원';
      const actionText = this.pointAdjustType === 'add' ? '지급' : '차감';
      this.$message.success(`${memberName}님에게 ${this.pointAdjustForm.amount.toLocaleString()}P를 ${actionText}했습니다.`);

      // 목록 새로고침
      await this.loadMembersWithPoints();

      // 헤더의 알포인트 갱신을 위해 EventBus로 이벤트 발생
      EventBus.$emit(EVENTS.POINTS_UPDATED);

      this.closePointAdjustModal();
    } catch (error: any) {
      console.error('포인트 지급/차감 실패:', error);
      const message = error.response?.data?.message || '포인트 처리에 실패했습니다';
      this.$message.error(message);
    } finally {
      this.submittingPoint = false;
    }
  }

  /**
   * 포인트 내역 모달 열기
   */
  private async openPointHistoryModal(member: MemberWithPoint) {
    // 채널 관리자가 아닌 경우 경고
    if (!this.isChannelAdmin) {
      this.$message.warning('포인트 내역 조회는 채널 관리자만 가능합니다.');
      return;
    }

    this.selectedPointMember = member;
    this.pointHistoryCurrentPoint = member.currentPoint || 0;
    this.pointHistoryModalVisible = true;
    
    await this.loadPointHistory(member.userUid);
  }

  /**
   * 포인트 내역 로드
   */
  private async loadPointHistory(targetUserUid: string) {
    try {
      this.loadingPointHistory = true;
      
      const response = await getTargetUserPointHistory({
        targetUserUid: targetUserUid,
        channelUid: this.channelUid,
        page: 0,
        size: 50,
      });

      if (response.data && response.data.content) {
        this.pointHistoryList = response.data.content;
        // 현재 포인트도 업데이트
        if (response.data.currentPoint !== undefined) {
          this.pointHistoryCurrentPoint = response.data.currentPoint;
        }
      }
    } catch (error: any) {
      console.error('포인트 내역 조회 실패:', error);
      const message = error.response?.data?.message || '포인트 내역을 불러오지 못했습니다';
      this.$message.error(message);
    } finally {
      this.loadingPointHistory = false;
    }
  }

  /**
   * 포인트 내역 모달 닫기
   */
  private closePointHistoryModal() {
    this.pointHistoryModalVisible = false;
    this.selectedPointMember = null;
    this.pointHistoryList = [];
    this.pointHistoryCurrentPoint = 0;
  }

  /**
   * 날짜+시간 포맷팅
   */
  private formatDateTime(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
  }
}
</script>

<style scoped lang="scss">
.member-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.member-management-main {
  flex: 1;
  margin-left: 310px;
  padding: 140px 60px 40px;
}

.page-header {
  margin-bottom: 40px;
}

.page-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 32px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
}

// 탭 메뉴
.tabs-section {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
  border-bottom: 2px solid #EBEBEB;
}

.tab-btn {
  padding: 16px 24px;
  background: none;
  border: none;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;

  &:hover {
    color: #073DFF;
  }

  &.active {
    color: #073DFF;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 3px;
      background: #073DFF;
    }
  }

  .badge {
    background: #FF5858;
    color: #FFF;
    font-size: 12px;
    font-weight: 700;
    padding: 2px 8px;
    border-radius: 12px;
    min-width: 20px;
    text-align: center;
  }
}

// 탭 컨텐츠
.tab-content {
  min-height: 400px;
}

// 필터 바
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  height: 48px;
  background: #FFF;
  border: 1px solid #CECECE;
  border-radius: 8px;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #073DFF;
  }

  i {
    color: #888;
    font-size: 18px;
  }
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  color: #222;

  &::placeholder {
    color: #AAA;
  }
}

.invite-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 48px;
  padding: 0 24px;
  background: #073DFF;
  color: #FFF;
  border: none;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0535e6;
    transform: translateY(-2px);
  }

  i {
    font-size: 18px;
  }
}

// 로딩
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;

  i {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 회원 테이블
.members-table-wrapper {
  background: #FFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.members-table {
  width: 100%;
  border-collapse: collapse;

  thead {
    background: #F8F9FA;

    th {
      padding: 16px 20px;
      text-align: left;
      color: #666;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
      font-weight: 600;
      border-bottom: 1px solid #EBEBEB;
    }
  }

  tbody {
    tr {
      border-bottom: 1px solid #F0F0F0;
      transition: background 0.2s;

      &:hover {
        background: #F8F9FA;
      }

      &:last-child {
        border-bottom: none;
      }
    }

    td {
      padding: 16px 20px;
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
    }
  }
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.member-avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 20px;
  flex-shrink: 0;
}

.member-avatar-large {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.member-avatar-large-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #5B8DEE 0%, #4A7DD9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FFF;
  font-size: 28px;
  flex-shrink: 0;
  border: 2px solid #4A7DD9;
}

.member-name {
  color: #222;
  font-weight: 600;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;

  &.active {
    background: #E8F0FF;
    color: #073DFF;
  }

  &.pending {
    background: #FFF4E5;
    color: #F5A623;
  }

  &.banned {
    background: #FFE8E8;
    color: #FF5858;
  }
}

// 액션 버튼
.action-btn {
  height: 36px;
  padding: 16px;
  border: none;
  border-radius: 6px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &.primary {
    background: #073DFF;
    color: #FFF;

    &:hover {
      background: #0535e6;
    }
  }

  &.secondary {
    background: #F5F5F5;
    color: #666;
    border: 1px solid #CECECE;

    &:hover {
      background: #E0E0E0;
    }
  }

  &.danger-outline {
    line-height:5px;
    background: #FFF;
    color: #FF5858;
    border: 1px solid #FF5858;

    &:hover {
      background: #FF5858;
      color: #FFF;
    }
  }

  &.primary-outline {
    line-height:5px;
    background: #FFF;
    color: #073DFF;
    border: 1px solid #073DFF;

    &:hover {
      background: #073DFF;
      color: #FFF;
    }
  }
}

// 가입 대기 목록
.pending-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.pending-card {
  background: #FFF;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .member-details {
    .member-name {
        text-align:left;
      font-size: 18px;
      margin: 0 0 4px 0;
    }

    .member-email {
      color: #888;
      font-size: 14px;
      margin: 0;
    }
  }
}

.pending-date {
  color: #888;
  font-size: 14px;
}

.pending-message {
  background: #F8F9FA;
  border-radius: 8px;
  text-align:left;

  .message-label {
    color: #666;
    font-size: 13px;
    font-weight: 600;
    margin: 0 0 8px 0;
  }

  .message-content {
    color: #222;
    font-size: 14px;
    line-height: 1.6;
    margin: 0;
  }
}

.pending-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

// 빈 상태
.empty-container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #666;
  font-size: 16px;
}

// 초대 모달
::v-deep .invite-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.modal-close-btn {
  position: absolute;
  right: 20px;
  top: 20px;
  width: 32px;
  height: 32px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #666;
  transition: color 0.2s;

  &:hover {
    color: #333;
  }
}

.modal-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0;
  text-align: center;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
}

.form-input {
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #CECECE;
  border-radius: 8px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  box-sizing: border-box;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: #073DFF;
  }

  &::placeholder {
    color: #AAA;
  }
}

// 공간 체크박스
.space-checkboxes {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: #F8F9FA;
  border-radius: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.space-checkbox-item {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }

  input[type="checkbox"] {
    width: 20px;
    height: 20px;
    cursor: pointer;
  }

  .space-checkbox-label {
    color: #222;
    font-size: 15px;
    font-weight: 500;
    user-select: none;
  }
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(:disabled) {
    background: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
  }
}

// 설문조사 모달 스타일
.survey-modal {
  .modal-content {
    max-height: 80vh;
    overflow-y: auto;
  }

  .survey-content {
    margin-top: 20px;
  }

  .survey-applicant {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background-color: #f5f8fa;
    border-radius: 12px;
    margin-bottom: 24px;

    .member-avatar-large-placeholder {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      background: linear-gradient(135deg, #5B8DEE 0%, #4A7DD9 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #FFF;
      font-size: 28px;
      flex-shrink: 0;
      border: 2px solid #4A7DD9;
    }

    .applicant-info {
      h4 {
        font-size: 18px;
        font-weight: 600;
        color: #1a1a1a;
        margin: 0 0 4px 0;
      }

      p {
        font-size: 14px;
        color: #666;
        margin: 0;
      }
    }
  }

  .survey-questions {
    display: flex;
    flex-direction: column;
    gap: 24px;
    margin-bottom: 32px;
  }

  .survey-item {
    .survey-question {
      font-size: 16px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0 0 12px 0;
    }

    .survey-answer {
      font-size: 15px;
      color: #333;
      line-height: 1.6;
      padding: 16px;
      background-color: #f9fafb;
      border-radius: 8px;
      margin: 0;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }

  .survey-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #e5e7eb;

    .action-btn {
      width: 100%;
    }
  }
}

// 권한 관리 스타일
.permissions-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.permission-card {
  background: #FFF;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.permission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .member-details {
    .member-name {
      text-align: left;
      font-size: 18px;
      margin: 0 0 4px 0;
    }

    .member-email {
      color: #888;
      font-size: 14px;
      margin: 0;
      text-align: left;
    }
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  min-height: 32px;
}

.permission-tag {
  display: inline-block;
  padding: 6px 14px;
  background: #E8F0FF;
  color: #073DFF;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}

.no-permission {
  color: #AAA;
  font-size: 14px;
  font-style: italic;
}

// 권한 모달 스타일
::v-deep .permission-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.permission-member-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 12px;
  margin-bottom: 24px;

  .member-details {
    h4 {
      font-size: 18px;
      font-weight: 600;
      color: #222;
      margin: 0 0 4px 0;
      text-align: left;
    }

    p {
      font-size: 14px;
      color: #888;
      margin: 0;
      text-align: left;
    }
  }
}

.permission-section {
  margin-bottom: 24px;
}

.permission-section-label {
  color: #222;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
}

.permission-checkboxes {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 8px;
  max-height: 400px;
  overflow-y: auto;
}

.permission-checkbox-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #FFF;
  border: 2px solid #E0E0E0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    background: #F5F8FF;
  }

  input[type="checkbox"] {
    width: 20px;
    height: 20px;
    cursor: pointer;
    flex-shrink: 0;
  }

  .permission-checkbox-label {
    color: #222;
    font-size: 15px;
    font-weight: 500;
    user-select: none;
    flex: 1;
  }
}

// 포인트 관리 스타일
.points-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.point-card {
  background: #FFF;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.point-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .member-details {
    .member-name {
      text-align: left;
      font-size: 18px;
      margin: 0 0 4px 0;
    }

    .member-email {
      color: #888;
      font-size: 14px;
      margin: 0;
      text-align: left;
    }
  }
}

.point-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.point-balance {
  font-size: 24px;
  font-weight: 700;
  color: #073DFF;
}

.point-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;

  .action-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    
    i {
      font-size: 14px;
    }
  }
}

// 포인트 지급/차감 모달
::v-deep .point-adjust-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.point-member-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 12px;
  margin-bottom: 24px;

  .member-details {
    .member-name {
      font-size: 18px;
      font-weight: 600;
      color: #222;
      margin: 0 0 4px 0;
      text-align: left;
    }

    .member-current-point {
      font-size: 14px;
      color: #073DFF;
      font-weight: 600;
      margin: 0;
      text-align: left;
    }
  }
}

.point-preview {
  background: #F8F9FA;
  border-radius: 12px;
  padding: 20px;
  margin-top: 20px;
}

.preview-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 15px;
  color: #666;

  &.add {
    color: #073DFF;
  }

  &.deduct {
    color: #FF5858;
  }

  &.result {
    border-top: 1px solid #E0E0E0;
    margin-top: 8px;
    padding-top: 16px;
    font-weight: 700;
    font-size: 18px;
    color: #222;
  }
}

.submit-btn.deduct {
  background: #FF5858;

  &:hover:not(:disabled) {
    background: #E04848;
    box-shadow: 0 4px 12px rgba(255, 88, 88, 0.3);
  }
}

// 포인트 내역 모달
::v-deep .point-history-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
  }
}

.point-history-list {
  max-height: 400px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #F0F0F0;

  &:last-child {
    border-bottom: none;
  }
}

.history-info {
  flex: 1;

  .history-description {
    font-size: 15px;
    color: #222;
    margin: 0 0 4px 0;
    text-align: left;
  }

  .history-date {
    font-size: 13px;
    color: #888;
    margin: 0;
    text-align: left;
  }
}

.history-amount {
  font-size: 16px;
  font-weight: 700;
  min-width: 100px;
  text-align: right;

  &.add {
    color: #073DFF;
  }

  &.deduct {
    color: #FF5858;
  }
}

@media screen and (max-width: 1024px) {
  .member-management-main {
    margin-left: 0;
    padding: 110px 20px 30px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;

    .search-box {
      max-width: none;
    }
  }

  .members-table-wrapper {
    overflow-x: auto;
  }

  .members-table {
    min-width: 700px;
  }
}

@media screen and (max-width: 768px) {
  .page-title {
    font-size: 24px;
  }

  .tabs-section {
    gap: 10px;
  }

  .tab-btn {
    padding: 12px 16px;
    font-size: 16px;
  }

  .pending-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .pending-actions {
    width: 100%;

    .action-btn {
      flex: 1;
    }
  }

  .permission-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .action-btn {
      width: 100%;
      justify-content: center;
    }
  }

  .permission-checkboxes {
    grid-template-columns: 1fr;
  }

  .survey-modal {
    .survey-applicant {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .survey-actions {
      flex-direction: column;

      .action-btn {
        width: 100%;
      }
    }
  }

  // 포인트 관리 반응형
  .point-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .point-actions {
    width: 100%;

    .action-btn {
      flex: 1;
    }
  }

  .point-balance {
    font-size: 20px;
  }
}

// 425px 이하 모바일 반응형
@media screen and (max-width: 425px) {
  .member-management-main {
    padding: 100px 16px 20px;
  }

  .page-header {
    margin-bottom: 24px;
  }

  .page-title {
    font-size: 20px;
  }

  .tabs-section {
    gap: 0;
    margin-bottom: 24px;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .tab-btn {
    flex: 1;
    min-width: max-content;
    padding: 12px 10px;
    font-size: 13px;
    gap: 4px;
    white-space: nowrap;

    .badge {
      font-size: 10px;
      padding: 2px 6px;
      min-width: 16px;
    }
  }

  .tab-content {
    min-height: 300px;
  }

  // 필터 바
  .filter-bar {
    margin-bottom: 20px;
    gap: 12px;
  }

  .search-box {
    height: 40px;
    padding: 0 12px;
    gap: 10px;

    i {
      font-size: 16px;
    }
  }

  .search-input {
    font-size: 14px;
  }

  .invite-btn {
    height: 40px;
    padding: 0 16px;
    font-size: 14px;

    i {
      font-size: 16px;
    }
  }

  // 로딩
  .loading-container {
    padding: 60px 20px;

    i {
      font-size: 36px;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
    }
  }

  // 회원 테이블
  .members-table-wrapper {
    border-radius: 10px;
    width: 100%;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;

    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background: #ccc;
      border-radius: 2px;
    }
  }

  .members-table {
    min-width: 550px;
    width: max-content;

    thead th {
      padding: 12px 12px;
      font-size: 12px;
      white-space: nowrap;
    }

    tbody td {
      padding: 12px 12px;
      font-size: 12px;
      white-space: nowrap;
    }
  }

  .member-avatar-placeholder {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }

  .member-name {
    font-size: 13px;
  }

  .status-badge {
    padding: 3px 8px;
    font-size: 11px;
  }

  .action-btn {
    height: 32px;
    padding: 0 12px;
    font-size: 12px;
  }

  // 가입 대기 목록
  .pending-list {
    gap: 16px;
  }

  .pending-card {
    padding: 16px;
    border-radius: 10px;
  }

  .pending-header {
    margin-bottom: 12px;

    .member-details {
      .member-name {
        font-size: 16px;
      }

      .member-email {
        font-size: 12px;
      }
    }
  }

  .member-avatar-large-placeholder {
    width: 48px;
    height: 48px;
    font-size: 22px;
  }

  .pending-date {
    font-size: 12px;
  }

  .pending-message {
    padding: 12px;
    border-radius: 6px;
    margin-bottom: 12px;

    .message-label {
      font-size: 12px;
      margin-bottom: 6px;
    }

    .message-content {
      font-size: 13px;
    }
  }

  .pending-actions {
    gap: 8px;

    .action-btn {
      height: 36px;
      font-size: 13px;
    }
  }

  // 빈 상태
  .empty-container {
    padding: 60px 20px;
    font-size: 14px;
  }

  // 권한 관리
  .permissions-list {
    gap: 16px;
  }

  .permission-card {
    padding: 16px;
    border-radius: 10px;
  }

  .permission-header {
    margin-bottom: 16px;

    .member-details {
      .member-name {
        font-size: 16px;
      }

      .member-email {
        font-size: 12px;
      }
    }
  }

  .permission-tags {
    gap: 6px;
  }

  .permission-tag {
    padding: 4px 10px;
    font-size: 11px;
    border-radius: 4px;
  }

  .no-permission {
    font-size: 12px;
  }

  // 모달 공통 스타일
  ::v-deep .invite-modal,
  ::v-deep .survey-modal,
  ::v-deep .permission-modal {
    width: 100% !important;
    max-width: 100vw;
    margin: 0 !important;
    height: 100%;
    border-radius: 0;

    .el-dialog__body {
      padding: 60px 16px 20px;
      height: 100%;
      overflow-y: auto;
    }
  }

  .modal-content {
    gap: 20px;
  }

  .modal-close-btn {
    right: 16px;
    top: 16px;
  }

  .modal-title {
    font-size: 20px;
  }

  .form-section {
    gap: 16px;
  }

  .form-group {
    gap: 6px;
  }



  .form-label {
    font-size: 14px;
  }

  .form-input {
    height: 44px;
    padding: 0 12px;
    font-size: 14px;
    border-radius: 6px;
  }

  .form-textarea {
    padding: 10px 12px;
    font-size: 14px;
    border-radius: 6px;
  }

  .space-checkboxes {
    padding: 12px;
    gap: 10px;
    max-height: 160px;
  }

  .space-checkbox-item {
    gap: 10px;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
    }

    .space-checkbox-label {
      font-size: 14px;
    }
  }

  .submit-btn {
    height: 48px;
    font-size: 16px;
    border-radius: 8px;
  }

  // 설문조사 모달
  .survey-modal {
    .survey-applicant {
      padding: 16px;
      gap: 12px;

      .member-avatar-large-placeholder {
        width: 48px;
        height: 48px;
        font-size: 22px;
      }

      .applicant-info {
        h4 {
          font-size: 16px;
        }

        p {
          font-size: 12px;
        }
      }
    }

    .survey-questions {
      gap: 20px;
      margin-bottom: 24px;
    }

    .survey-item {
      .survey-question {
        font-size: 14px;
        margin-bottom: 10px;
      }

      .survey-answer {
        font-size: 13px;
        padding: 12px;
        border-radius: 6px;
      }
    }

    .survey-actions {
      margin-top: 24px;
      padding-top: 20px;
      gap: 10px;

      .action-btn {
        height: 44px;
        font-size: 14px;
      }
    }
  }

  // 권한 모달
  .permission-member-info {
    padding: 16px;
    gap: 12px;
    margin-bottom: 20px;
    border-radius: 10px;

    .member-avatar-placeholder {
      width: 40px;
      height: 40px;
      font-size: 18px;
    }

    .member-details {
      h4 {
        font-size: 16px;
      }

      p {
        font-size: 12px;
      }
    }
  }

  .permission-section {
    margin-bottom: 20px;
  }

  .permission-section-label {
    font-size: 14px;
    margin-bottom: 12px;
  }

  .permission-checkboxes {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 12px;
    max-height: 280px;
  }

  .permission-checkbox-item {
    padding: 10px;
    gap: 10px;
    border-radius: 6px;

    input[type="checkbox"] {
      width: 18px;
      height: 18px;
    }

    .permission-checkbox-label {
      font-size: 13px;
    }
  }
}
</style>
