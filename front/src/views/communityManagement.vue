<template>
  <div class="community-management-page">
    <CommunitySidebar 
      :selectedSpaceId="'community-management'" 
      @space-select="handleSpaceSelect"
    />

    <div class="community-management-main">
      <div class="page-header">
        <h1 class="page-title">커뮤니티 관리</h1>
      </div>

      <!-- 탭 메뉴 -->
      <div class="tabs-section">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'point' }"
          @click="activeTab = 'point'"
        >
          포인트 관리
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'adjust' }"
          @click="activeTab = 'adjust'"
        >
          포인트 차감/지급
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'settings' }"
          @click="activeTab = 'settings'"
        >
          커뮤니티 설정
        </button>
      </div>

      <!-- 포인트 관리 탭 -->
      <div v-if="activeTab === 'point'" class="point-management-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-coin"></i>
            포인트 적립 설정
          </h2>
          <p class="section-description">
            사용자 활동에 따른 포인트 적립 금액을 설정합니다.
          </p>
        </div>

        <!-- 로딩 스피너 -->
        <div v-if="loadingPointSettings" class="loading-container">
          <el-icon class="is-loading">
            <i class="el-icon-loading"></i>
          </el-icon>
          <p class="loading-text">포인트 설정을 불러오는 중...</p>
        </div>

        <!-- 포인트 설정 폼 -->
        <div v-else>
        <div class="point-settings-form">
          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-edit"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  게시글 작성
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 최소 {{ pointSettings.postMinLength }}자 이상 작성 시 포인트 지급<br>
                      • 일일 최대 {{ pointSettings.postDailyLimit }}회 적립 가능 (0 = 무제한)
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">게시판에 새 글을 작성할 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 최소 글자수 미달 시 포인트 미지급<br>
                      • 일일 적립 횟수 제한 적용
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.postCreate"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.postDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">최소 글자수</span>
                <el-input-number
                  v-model="pointSettings.postMinLength"
                  :min="0"
                  :max="500"
                  :step="5"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">자</span>
              </div>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-chat-line-square"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  댓글 작성
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 최소 {{ pointSettings.commentMinLength }}자 이상 작성 시 포인트 지급<br>
                      • 본인 게시글에 댓글 작성 시 포인트 미지급<br>
                      • 게시글당 1회만 적립 가능<br>
                      • 일일 최대 {{ pointSettings.commentDailyLimit }}회 적립 가능 (0 = 무제한)
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">게시글에 댓글을 작성할 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 최소 글자수 미달 시 포인트 미지급<br>
                      • 본인 게시글에 댓글 작성 시 미지급<br>
                      • 게시글당 1회만 적립 가능<br>
                      • 일일 적립 횟수 제한 적용
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.commentCreate"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.commentDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">최소 글자수</span>
                <el-input-number
                  v-model="pointSettings.commentMinLength"
                  :min="0"
                  :max="500"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">자</span>
              </div>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  좋아요 누름
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 본인 게시글 좋아요 시 포인트 미지급<br>
                      • 게시글당 1회만 적립 가능<br>
                      • 일일 최대 {{ pointSettings.likeDailyLimit }}회 적립 가능 (0 = 무제한)
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">게시글이나 댓글에 좋아요를 누를 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 본인 게시글 좋아요 시 포인트 미지급<br>
                      • 게시글당 1회만 적립 가능<br>
                      • 일일 적립 횟수 제한 적용
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.likeGive"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.likeDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
            </div>
          </div>

         <!--  <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-user-solid"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">출석 체크</h3>
                <p class="setting-description">매일 첫 로그인 시 적립되는 포인트 (현재 미구현)</p>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.dailyAttendance"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                  disabled
                />
                <span class="unit">P</span>
              </div>
            </div>
          </div> -->

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-shopping-cart-2"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  장터 상품 등록
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 일일 최대 {{ pointSettings.marketplaceCreateDailyLimit }}회 적립 가능 (0 = 무제한)
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">장터에 상품을 등록할 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 일일 적립 횟수 제한 적용
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.marketplaceCreate"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.marketplaceCreateDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-sold-out"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  장터 상품 판매
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 일일 최대 {{ pointSettings.marketplaceSellDailyLimit || '무제한' }}회 적립 가능
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">장터에서 상품을 판매했을 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 일일 적립 횟수 제한 적용 (0 = 무제한)
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.marketplaceSell"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.marketplaceSellDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
            </div>
          </div>

          <div class="point-setting-item">
            <div class="setting-info">
              <div class="setting-icon">
                <i class="el-icon-reading"></i>
              </div>
              <div class="setting-details">
                <h3 class="setting-title">
                  강좌 수강 완료
                  <el-tooltip placement="top">
                    <div slot="content">
                      • 강좌당 1회만 적립 가능<br>
                      • 일일 최대 {{ pointSettings.courseCompleteDailyLimit || '무제한' }}회 적립 가능
                    </div>
                    <i class="el-icon-question" style="color: #909399; margin-left: 4px;"></i>
                  </el-tooltip>
                </h3>
                <p class="setting-description">강좌를 완료했을 때 적립되는 포인트</p>
                <div class="abuse-prevention-notice">
                  <el-alert
                    type="info"
                    :closable="false"
                  >
                    <div class="abuse-rules">
                      • 강좌당 1회만 적립 가능<br>
                      • 일일 적립 횟수 제한 적용 (0 = 무제한)
                    </div>
                  </el-alert>
                </div>
              </div>
            </div>
            <div class="setting-input-group">
              <div class="setting-input">
                <el-input-number
                  v-model="pointSettings.courseComplete"
                  :min="0"
                  :max="10000"
                  :step="10"
                  controls-position="right"
                />
                <span class="unit">P</span>
              </div>
              <div class="setting-input-sub">
                <span class="sub-label">일일 제한</span>
                <el-input-number
                  v-model="pointSettings.courseCompleteDailyLimit"
                  :min="0"
                  :max="100"
                  :step="1"
                  controls-position="right"
                  size="small"
                />
                <span class="sub-unit">회</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 저장 버튼 -->
        <div class="save-section">
          <el-button
            type="primary"
            size="large"
            icon="el-icon-check"
            @click="savePointSettings"
            :loading="saving"
          >
            설정 저장
          </el-button>
          <el-button
            size="large"
            icon="el-icon-refresh"
            @click="resetPointSettings"
            :loading="saving"
          >
            기본값으로 초기화
          </el-button>
        </div>
        </div>
      </div>

      <!-- 포인트 차감/지급 탭 -->
      <div v-if="activeTab === 'adjust'" class="point-adjust-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="el-icon-wallet"></i>
            포인트 차감/지급
          </h2>
          <p class="section-description">
            회원을 선택하여 포인트를 차감하거나 지급할 수 있습니다.
          </p>
        </div>

        <!-- 회원 검색 -->
        <div class="member-search-section">
          <el-input
            v-model="memberSearchQuery"
            placeholder="회원 검색 (이름, 이메일, 아이디)"
            prefix-icon="el-icon-search"
            clearable
            style="max-width: 500px;"
          />
        </div>

        <!-- 회원 목록 -->
        <div class="member-list-section">
          <div class="member-list-header">
            <span class="header-item name">회원 정보</span>
            <span class="header-item point">보유 포인트</span>
            <span class="header-item action">액션</span>
          </div>

          <div class="member-list-body">
            <!-- 로딩 상태 -->
            <div v-if="loadingMembers" class="loading-members">
              <i class="el-icon-loading"></i>
              <p>회원 목록을 불러오는 중...</p>
            </div>

            <!-- 회원 목록 -->
            <div
              v-else
              v-for="member in filteredMembers"
              :key="member.memberUid"
              class="member-row"
            >
              <div class="member-info-cell">
                <div class="member-avatar">
                  <img v-if="member.iconFileUid" :src="`${apiUrl}/attached-file/${member.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
                  <svg v-else width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                    <mask id="mask0_community_member" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="40" height="40">
                      <circle cx="20" cy="20" r="20" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0_community_member)">
                      <rect x="4.44" y="23.33" width="31.11" height="35.56" rx="15.56" fill="#F5F5F5"/>
                      <circle cx="20" cy="12.22" r="7.78" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="member-details">
                  <h4 class="member-name">{{ member.name }}</h4>
                  <p class="member-email">{{ member.email }}</p>
                </div>
              </div>

              <div class="member-point-cell">
                <span class="point-amount">{{ member.point.toLocaleString() }}</span>
                <span class="point-unit">P</span>
              </div>

              <div class="member-action-cell">
                <el-button
                  type="success"
                  size="small"
                  icon="el-icon-plus"
                  @click="openPointModal('add', member)"
                >
                  지급
                </el-button>
                <el-button
                  type="warning"
                  size="small"
                  icon="el-icon-minus"
                  @click="openPointModal('subtract', member)"
                >
                  차감
                </el-button>
              </div>
            </div>

            <!-- 빈 상태 -->
            <div v-if="!loadingMembers && filteredMembers.length === 0" class="empty-members">
              <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                <mask id="mask0_empty_community" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
                  <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
                </mask>
                <g mask="url(#mask0_empty_community)">
                  <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
                  <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
                </g>
              </svg>
              <p>{{ memberSearchQuery ? '검색 결과가 없습니다.' : '회원이 없습니다.' }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 커뮤니티 설정 탭 -->
      <div v-if="activeTab === 'settings'" class="settings-section">
        <div v-loading="loadingSettings" class="community-setting-content">
          <el-form
            ref="settingsForm"
            class="community-form"
            :model="settingsForm"
            :rules="settingsRules"
            onsubmit="return false;"
          >
            <div class="settings-form-grid">
              <!-- Left Column -->
              <div class="form-column">
                <!-- Icon Image Upload -->
                <div class="form-group">
                  <label class="form-label">커뮤니티 아이콘 이미지 등록</label>
                  <!-- Upload Box - shown when no image -->
                  <div v-if="settingsForm.iconImageList.length === 0" class="upload-box">
                    <div class="upload-content">
                      <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M6.25 34.375V5.625C6.25 3.89964 7.64964 2.5 9.375 2.5H17.5C21.8098 2.5 25.9434 4.21165 28.9909 7.25911C32.0384 10.3066 33.75 14.4402 33.75 18.75V34.375C33.75 36.1004 32.3504 37.5 30.625 37.5H9.375C7.64964 37.5 6.25 36.1004 6.25 34.375ZM18.75 28.75V21.7676L15.8838 24.6338C15.3956 25.1219 14.6044 25.1219 14.1162 24.6338C13.6281 24.1456 13.6281 23.3544 14.1162 22.8662L19.1162 17.8662L19.2106 17.7799C19.7016 17.3794 20.4261 17.4085 20.8838 17.8662L25.8838 22.8662C26.3719 23.3544 26.3719 24.1456 25.8838 24.6338C25.3956 25.1219 24.6044 25.1219 24.1162 24.6338L21.25 21.7676V28.75C21.25 29.4404 20.6904 30 20 30C19.3096 30 18.75 29.4404 18.75 28.75ZM23.75 11.875C23.75 12.0408 23.8151 12.2005 23.9323 12.3177C24.0495 12.4349 24.2092 12.5 24.375 12.5H26.875C28.0209 12.5 29.1372 12.7861 30.1286 13.3187C29.4463 11.7322 28.4681 10.2715 27.2233 9.02669C25.978 7.78136 24.5169 6.80213 22.9297 6.11979C23.4629 7.11167 23.75 8.2284 23.75 9.375V11.875ZM8.75 34.375C8.75 34.7196 9.03036 35 9.375 35H30.625C30.9696 35 31.25 34.7196 31.25 34.375V19.375C31.25 18.2147 30.7895 17.1014 29.9691 16.2809C29.1486 15.4605 28.0353 15 26.875 15H24.375C23.5462 15 22.7508 14.6713 22.1647 14.0853C21.5787 13.4992 21.25 12.7038 21.25 11.875V9.375C21.25 8.21468 20.7895 7.1014 19.9691 6.28092C19.1486 5.46045 18.0353 5 16.875 5H9.375C9.03036 5 8.75 5.28036 8.75 5.625V34.375Z" fill="#6B7280"/>
                      </svg>
                      <span class="upload-hint">추천 이미지 사이즈: 128 x 128</span>
                      <el-upload
                        :action="`${apiUrl}/channel/upload`"
                        :headers="requestHeaders"
                        ref="iconImageUpload"
                        :before-upload="(file) => handleSettingsBeforeUpload(file, 'icon')"
                        :on-success="(file) => handleSettingsUploadSuccess(file, 'icon')"
                        :limit="1"
                        :on-exceed="() => handleSettingsExceed('icon')"
                        :show-file-list="false"
                      >
                        <button type="button" class="upload-btn">등록하기</button>
                      </el-upload>
                    </div>
                  </div>
                  <!-- Image Preview -->
                  <div v-else class="image-preview-container">
                    <div v-for="(image, index) in settingsForm.iconImageList" :key="index" class="image-preview-item">
                      <img :src="image.url" :alt="image.name" class="preview-image" />
                      <div class="image-info">
                        <span class="image-name">{{ image.name }}</span>
                        <button type="button" class="remove-image-btn" @click="handleSettingsRemoveFile(image, 'icon')">
                          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 4L4 12M4 4L12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                          </svg>
                          삭제
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Community Name -->
                <div class="form-group">
                  <label class="form-label">커뮤니티 이름</label>
                  <el-form-item prop="name">
                    <input
                      type="text"
                      class="form-input"
                      placeholder="이름을 입력하세요"
                      v-model="settingsForm.name"
                      maxlength="30"
                    />
                  </el-form-item>
                </div>

                <!-- Category -->
                <div class="form-group">
                  <label class="form-label">커뮤니티 카테고리</label>
                  <el-form-item prop="category">
                    <el-select 
                      v-model="settingsForm.categoryList" 
                      multiple
                      placeholder="해당 커뮤니티의 카테고리를 선택해주세요" 
                      value-key="categoryUid"
                      class="category-select"
                      popper-class="category-dropdown"
                    >
                      <el-option 
                        v-for="category in categoryList" 
                        :key="category.uid" 
                        :label="category.name" 
                        :value="{categoryUid: category.uid}"
                      >
                        {{ category.name }}
                      </el-option>
                    </el-select>
                  </el-form-item>
                </div>
              </div>

              <!-- Right Column -->
              <div class="form-column">
                <!-- Cover Image Upload -->
                <div class="form-group">
                  <label class="form-label">커뮤니티 대표 이미지 등록</label>
                  <!-- Upload Box - shown when no image -->
                  <div v-if="settingsForm.coverImageList.length === 0" class="upload-box">
                    <div class="upload-content">
                      <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M6.25 34.375V5.625C6.25 3.89964 7.64964 2.5 9.375 2.5H17.5C21.8098 2.5 25.9434 4.21165 28.9909 7.25911C32.0384 10.3066 33.75 14.4402 33.75 18.75V34.375C33.75 36.1004 32.3504 37.5 30.625 37.5H9.375C7.64964 37.5 6.25 36.1004 6.25 34.375ZM18.75 28.75V21.7676L15.8838 24.6338C15.3956 25.1219 14.6044 25.1219 14.1162 24.6338C13.6281 24.1456 13.6281 23.3544 14.1162 22.8662L19.1162 17.8662L19.2106 17.7799C19.7016 17.3794 20.4261 17.4085 20.8838 17.8662L25.8838 22.8662C26.3719 23.3544 26.3719 24.1456 25.8838 24.6338C25.3956 25.1219 24.6044 25.1219 24.1162 24.6338L21.25 21.7676V28.75C21.25 29.4404 20.6904 30 20 30C19.3096 30 18.75 29.4404 18.75 28.75ZM23.75 11.875C23.75 12.0408 23.8151 12.2005 23.9323 12.3177C24.0495 12.4349 24.2092 12.5 24.375 12.5H26.875C28.0209 12.5 29.1372 12.7861 30.1286 13.3187C29.4463 11.7322 28.4681 10.2715 27.2233 9.02669C25.978 7.78136 24.5169 6.80213 22.9297 6.11979C23.4629 7.11167 23.75 8.2284 23.75 9.375V11.875ZM8.75 34.375C8.75 34.7196 9.03036 35 9.375 35H30.625C30.9696 35 31.25 34.7196 31.25 34.375V19.375C31.25 18.2147 30.7895 17.1014 29.9691 16.2809C29.1486 15.4605 28.0353 15 26.875 15H24.375C23.5462 15 22.7508 14.6713 22.1647 14.0853C21.5787 13.4992 21.25 12.7038 21.25 11.875V9.375C21.25 8.21468 20.7895 7.1014 19.9691 6.28092C19.1486 5.46045 18.0353 5 16.875 5H9.375C9.03036 5 8.75 5.28036 8.75 5.625V34.375Z" fill="#6B7280"/>
                      </svg>
                      <span class="upload-hint">추천 이미지 사이즈: 1084 x 576</span>
                      <el-upload
                        :action="`${apiUrl}/channel/upload`"
                        :headers="requestHeaders"
                        ref="coverImageUpload"
                        :before-upload="(file) => handleSettingsBeforeUpload(file, 'cover')"
                        :on-success="(file) => handleSettingsUploadSuccess(file, 'cover')"
                        :limit="4"
                        :on-exceed="() => handleSettingsExceed('cover')"
                        :show-file-list="false"
                      >
                        <button type="button" class="upload-btn">등록하기</button>
                      </el-upload>
                    </div>
                  </div>
                  <!-- Image Preview -->
                  <div v-else class="image-preview-container cover-preview">
                    <div v-for="(image, index) in settingsForm.coverImageList" :key="index" class="image-preview-item cover-item">
                      <img :src="image.url" :alt="image.name" class="preview-image cover-preview-image" />
                      <div class="image-info">
                        <span class="image-name">{{ image.name }}</span>
                        <button type="button" class="remove-image-btn" @click="handleSettingsRemoveFile(image, 'cover')">
                          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12 4L4 12M4 4L12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                          </svg>
                          삭제
                        </button>
                      </div>
                    </div>
                    <!-- Add more button if under limit -->
                    <div v-if="settingsForm.coverImageList.length < 4" class="add-more-container">
                      <el-upload
                        :action="`${apiUrl}/channel/upload`"
                        :headers="requestHeaders"
                        :before-upload="(file) => handleSettingsBeforeUpload(file, 'cover')"
                        :on-success="(file) => handleSettingsUploadSuccess(file, 'cover')"
                        :limit="4"
                        :on-exceed="() => handleSettingsExceed('cover')"
                        :show-file-list="false"
                      >
                        <button type="button" class="add-more-btn">+ 추가</button>
                      </el-upload>
                    </div>
                  </div>
                </div>

                <!-- Domain (읽기 전용) -->
                <div class="form-group">
                  <label class="form-label">커뮤니티 도메인(영문)</label>
                  <el-form-item prop="domain">
                    <input
                      type="text"
                      class="form-input"
                      placeholder="커뮤니티 도메인"
                      v-model="settingsForm.domain"
                      maxlength="30"
                      readonly
                    />
                  </el-form-item>
                </div>

                <!-- Access Control -->
                <div class="form-group">
                  <label class="form-label">공간 접근</label>
                  <div class="access-control">
                    <button 
                      type="button"
                      class="access-btn" 
                      :class="{ active: !settingsForm.privateStatus }"
                      @click="handleSettingsAccessChange(false)"
                    >
                      공개
                    </button>
                    <button 
                      type="button"
                      class="access-btn" 
                      :class="{ active: settingsForm.privateStatus }"
                      @click="handleSettingsAccessChange(true)"
                    >
                      비공개
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <!-- Full Width Description -->
            <div class="form-group form-group-full">
              <label class="form-label">커뮤니티 설명</label>
              <textarea
                class="form-textarea"
                placeholder="커뮤니티에 대해서 설명해주세요"
                v-model="settingsForm.introduce"
                maxlength="500"
                rows="6"
              ></textarea>
            </div>

            <!-- Password Field (Conditional) -->
            <div v-if="settingsPasswordVisible" class="form-group form-group-full">
              <label class="form-label">암호 설정</label>
              <el-form-item prop="password">
                <input
                  type="password"
                  class="form-input"
                  placeholder="암호를 입력해주세요"
                  v-model="settingsForm.password"
                  maxlength="15"
                />
              </el-form-item>
            </div>

            <!-- Join Questions -->
            <div class="form-group form-group-full">
              <label class="form-label">가입 질문 항목 설정</label>
              <p class="form-hint">*최대 5개까지 설정 가능합니다.</p>
              <div class="questions-list">
                <div v-for="(question, index) in settingsForm.questionList" :key="index" class="question-item">
                  <input
                    type="text"
                    class="form-input"
                    placeholder="질문을 입력해주세요."
                    v-model="question.title"
                    maxlength="100"
                  />
                  <button type="button" class="remove-btn" @click="handleSettingsDeleteQuestion(index)">제거</button>
                </div>
                <button type="button" class="add-question-btn" @click="handleSettingsAddQuestion()">+ 추가</button>
              </div>
            </div>

            <!-- Action Buttons -->
            <div class="form-actions">
              <button type="button" class="save-btn" @click="handleSettingsSave()" :disabled="savingSettings">
                {{ savingSettings ? '저장 중...' : '저장하기' }}
              </button>
            </div>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 포인트 조정 모달 -->
    <el-dialog
      :visible.sync="pointModalVisible"
      width="500px"
      :show-close="false"
      custom-class="point-adjust-modal"
    >
      <div class="modal-content">
        <button class="modal-close-btn" @click="pointModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">
          {{ pointModalType === 'add' ? '포인트 지급' : '포인트 차감' }}
        </h2>

        <div class="modal-member-info">
          <div class="member-avatar">
            <img v-if="selectedMember?.iconFileUid" :src="`${apiUrl}/attached-file/${selectedMember.iconFileUid}`" alt="프로필 이미지" class="member-avatar-img">
            <svg v-else width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
              <mask id="mask0_point_modal" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="48" height="48">
                <circle cx="24" cy="24" r="24" fill="#D9D9D9"/>
              </mask>
              <g mask="url(#mask0_point_modal)">
                <rect x="5.33" y="28" width="37.33" height="42.67" rx="18.67" fill="#F5F5F5"/>
                <circle cx="24" cy="14.67" r="9.33" fill="#F5F5F5"/>
              </g>
            </svg>
          </div>
          <div class="member-details">
            <h4 class="member-name">{{ selectedMember?.name }}</h4>
            <p class="member-point">
              현재 포인트: <strong>{{ selectedMember?.point.toLocaleString() }}P</strong>
            </p>
          </div>
        </div>

        <div class="modal-form">
          <div class="form-group">
            <label class="form-label">
              {{ pointModalType === 'add' ? '지급할' : '차감할' }} 포인트
            </label>
            <el-input-number
              v-model="pointAmount"
              :min="1"
              :max="pointModalType === 'subtract' ? selectedMember?.point : 999999"
              :step="100"
              controls-position="right"
              style="width: 100%;"
            />
          </div>

          <div class="form-group">
            <label class="form-label">사유 (선택)</label>
            <el-input
              v-model="pointReason"
              type="textarea"
              :rows="3"
              placeholder="포인트 조정 사유를 입력하세요."
            />
          </div>
        </div>

        <div class="modal-actions">
          <el-button @click="pointModalVisible = false">
            취소
          </el-button>
          <el-button
            :type="pointModalType === 'add' ? 'success' : 'warning'"
            @click="adjustPoint"
            :loading="adjusting"
          >
            {{ pointModalType === 'add' ? '지급하기' : '차감하기' }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { 
  getChannelDomainDetail,
  updateChannel,
  getChannelCategory,
} from '@/api/channel';
import { 
  getChannelMembersWithPoints, 
  adminAdjustPoint, 
  MemberWithPoint,
  getPointSettings,
  savePointSettings,
  resetPointSettings,
  PointSettingDto,
} from '@/api/point';
import { ChannelModule } from '@/store/modules/channel';
import { Form } from 'element-ui';
import { getToken } from '@/utils/cookies';

interface PointSettings {
  // 포인트 적립량
  postCreate: number;
  commentCreate: number;
  likeGive: number;
  dailyAttendance: number;
  marketplaceCreate: number;
  marketplaceSell: number;
  courseComplete: number;
  // 일일 적립 횟수 제한 (0 = 무제한)
  postDailyLimit: number;
  commentDailyLimit: number;
  likeDailyLimit: number;
  marketplaceCreateDailyLimit: number;
  marketplaceSellDailyLimit: number;
  courseCompleteDailyLimit: number;
  // 최소 글자수 제한
  postMinLength: number;
  commentMinLength: number;
}

interface Member {
  memberIdx: number;
  memberUid: string;
  userUid: string;
  userId: string;
  name: string;
  email: string;
  iconFileUid: string | null;
  point: number;
}

@Component({
  name: 'CommunityManagement',
  components: {
    CommunitySidebar,
  },
})
export default class extends Vue {
  @Watch('settingsForm.questionList', { deep: true })
  private handleChangeQuestion() {
    if (!this.isSetSettingsForm) {
      this.settingsForm.questionChangeFlag = true;
    }
  }

  private activeTab = 'point';
  private saving = false;
  private loadingPointSettings = false;
  private currentChannelUid = '';
  private loadingMembers = false;

  // 커뮤니티 설정 관련
  private loadingSettings = false;
  private savingSettings = false;
  private isSetSettingsForm = false;
  private settingsPasswordVisible = false;
  private categoryList: any[] = [];

  get requestHeaders() {
    return {
      Authorization: `Bearer ${getToken()}`,
    };
  }

  private settingsForm: any = {
    uid: '',
    name: '',
    introduce: '',
    privateStatus: false,
    password: '',
    domain: '',
    iconImageList: [],
    coverImageList: [],
    categoryList: [],
    questionList: [],
    questionChangeFlag: false,
  };

  private settingsRules = {
    name: [
      { required: true, message: '커뮤니티 이름을 입력해주세요.', trigger: ['change', 'blur'] },
    ],
    password: [
      { required: true, message: '패스워드를 입력하세요.', trigger: ['change', 'blur'] },
      {
        min: 8,
        max: 20,
        message: '패스워드는 최소 8자, 최대 20자입니다.',
        trigger: ['change', 'blur'],
      },
    ],
  };

  get apiUrl() {
    return process.env.VUE_APP_BASE_API || '/api';
  }

  // 포인트 조정 관련
  private memberSearchQuery = '';
  private pointModalVisible = false;
  private pointModalType: 'add' | 'subtract' = 'add';
  private selectedMember: Member | null = null;
  private pointAmount = 100;
  private pointReason = '';
  private adjusting = false;

  // 포인트 설정 (기본값)
  private pointSettings: PointSettings = {
    postCreate: 100,
    commentCreate: 50,
    likeGive: 10,
    dailyAttendance: 50,
    marketplaceCreate: 100,
    marketplaceSell: 200,
    courseComplete: 150,
    // 일일 제한
    postDailyLimit: 10,
    commentDailyLimit: 20,
    likeDailyLimit: 30,
    marketplaceCreateDailyLimit: 5,
    marketplaceSellDailyLimit: 0,
    courseCompleteDailyLimit: 0,
    // 최소 글자수
    postMinLength: 20,
    commentMinLength: 5,
  };

  // 기본값 백업
  private defaultPointSettings: PointSettings = {
    postCreate: 100,
    commentCreate: 50,
    likeGive: 10,
    dailyAttendance: 50,
    marketplaceCreate: 100,
    marketplaceSell: 200,
    courseComplete: 150,
    // 일일 제한
    postDailyLimit: 10,
    commentDailyLimit: 20,
    likeDailyLimit: 30,
    marketplaceCreateDailyLimit: 5,
    marketplaceSellDailyLimit: 0,
    courseCompleteDailyLimit: 0,
    // 최소 글자수
    postMinLength: 20,
    commentMinLength: 5,
  };

  // 회원 데이터
  private members: Member[] = [];

  async mounted() {
    await this.loadChannelInfo();
    await this.loadMembers();
    await this.loadPointSettings();
    await this.getCategoryList();
    await this.loadSettingsForm();
  }

  // 카테고리 목록 조회
  private async getCategoryList() {
    try {
      const res = await getChannelCategory();
      this.categoryList = res.data;
    } catch (error) {
      console.error('카테고리 조회 실패:', error);
    }
  }

  // 커뮤니티 설정 폼 로드
  private async loadSettingsForm() {
    if (!this.currentChannelUid) return;

    this.loadingSettings = true;
    this.isSetSettingsForm = true;
    try {
      const channelData = ChannelModule.selectedChannel;
      if (channelData && channelData.uid) {
        this.settingsForm = {
          ...this.settingsForm,
          ...channelData,
        };
        if (this.settingsForm.privateStatus) {
          this.settingsPasswordVisible = true;
        }
        // 이미지 URL 설정
        this.settingsForm.iconImageList.forEach((image: any) => {
          image.url = `${this.apiUrl}/attached-file/${image.fileUid}`;
        });
        this.settingsForm.coverImageList.forEach((image: any) => {
          image.url = `${this.apiUrl}/attached-file/${image.fileUid}`;
        });
      }
    } catch (error) {
      console.error('커뮤니티 설정 로드 실패:', error);
    } finally {
      this.loadingSettings = false;
      this.isSetSettingsForm = false;
    }
  }

  // 커뮤니티 설정 저장
  private async handleSettingsSave() {
    const form = this.$refs.settingsForm as Form;
    if (!form) return;

    try {
      await form.validate();
    } catch (e) {
      return;
    }

    this.$confirm('정말 저장하시겠습니까?', '저장', {
      confirmButtonText: '저장',
      cancelButtonText: '취소',
    }).then(async () => {
      this.savingSettings = true;
      try {
        await updateChannel(this.settingsForm.uid, this.settingsForm);
        this.$message.success('저장이 완료되었습니다');
        // ChannelModule 업데이트
        ChannelModule.setSelectedChannel(this.settingsForm);
      } catch (error: any) {
        console.error('커뮤니티 설정 저장 실패:', error);
        this.$message.error(error.response?.data?.message || '저장에 실패했습니다.');
      } finally {
        this.savingSettings = false;
      }
    }).catch(() => {
      // 취소
    });
  }

  // 접근 설정 변경
  private handleSettingsAccessChange(isPrivate: boolean) {
    this.settingsForm.privateStatus = isPrivate;
    this.settingsPasswordVisible = isPrivate;
  }

  // 이미지 업로드 전 검증
  private handleSettingsBeforeUpload(file: File, type: string) {
    const isLimit = file.size / 1024 / 1024 < 10;
    const isImage = file.type.startsWith('image');
    if (!isLimit) {
      this.$message.warning('파일 용량은 10MB 를 초과 할 수 없습니다.');
      return false;
    }
    if (!isImage) {
      this.$message.warning('이미지 파일만 업로드 가능합니다.');
      return false;
    }
    return true;
  }

  // 이미지 업로드 성공
  private handleSettingsUploadSuccess(res: any, type: string) {
    if (type === 'icon') {
      this.settingsForm.iconImageList.push({
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      });
      this.$message.success('아이콘 이미지가 업로드되었습니다.');
    } else if (type === 'cover') {
      this.settingsForm.coverImageList.push({
        fileUid: res.uid,
        name: res.originalName,
        url: `${this.apiUrl}/attached-file/${res.uid}`,
      });
      this.$message.success('대표 이미지가 업로드되었습니다.');
    }
  }

  // 이미지 삭제
  private handleSettingsRemoveFile(file: any, type: string) {
    if (type === 'icon') {
      const index = this.settingsForm.iconImageList.findIndex((imgFile: any) => imgFile.fileUid === file.fileUid);
      if (index > -1) this.settingsForm.iconImageList.splice(index, 1);
    } else if (type === 'cover') {
      const index = this.settingsForm.coverImageList.findIndex((imgFile: any) => imgFile.fileUid === file.fileUid);
      if (index > -1) this.settingsForm.coverImageList.splice(index, 1);
    }
  }

  // 이미지 개수 초과
  private handleSettingsExceed(type: string) {
    if (type === 'icon') {
      this.$message.warning('아이콘 파일개수는 1개를 초과할 수 없습니다.');
    } else if (type === 'cover') {
      this.$message.warning('커버 파일개수는 4개를 초과할 수 없습니다.');
    }
  }

  // 질문 추가
  private handleSettingsAddQuestion() {
    if (this.settingsForm.questionList.length < 5) {
      this.settingsForm.questionList.push({ title: '' });
    } else {
      this.$message.warning('질문은 5개까지만 생성하실 수 있습니다.');
    }
  }

  // 질문 삭제
  private handleSettingsDeleteQuestion(index: number) {
    if (index >= 0 && index < this.settingsForm.questionList.length) {
      this.settingsForm.questionList.splice(index, 1);
    }
  }

  private async loadChannelInfo() {
    const domain = this.$route.params.domain;
    if (!domain) return;

    try {
      const response = await getChannelDomainDetail(domain as string);
      this.currentChannelUid = response.data.uid;
    } catch (error) {
      console.error('채널 정보 조회 실패:', error);
      this.$message.error('채널 정보를 불러올 수 없습니다.');
    }
  }

  private async loadMembers() {
    if (!this.currentChannelUid) return;

    try {
      this.loadingMembers = true;
      const response = await getChannelMembersWithPoints({
        channelUid: this.currentChannelUid,
        size: 1000, // 전체 회원 조회
      });

      // API 응답을 Member 인터페이스에 맞게 변환
      this.members = (response.data.content || []).map((member: MemberWithPoint) => ({
        memberIdx: member.memberIdx,
        memberUid: member.memberUid,
        userUid: member.userUid,
        userId: member.userId,
        name: member.actualName || member.userId,
        email: member.email,
        iconFileUid: member.iconFileUid,
        point: member.currentPoint || 0,
      }));
    } catch (error) {
      console.error('회원 목록 조회 실패:', error);
      this.$message.error('회원 목록을 불러올 수 없습니다.');
      this.members = [];
    } finally {
      this.loadingMembers = false;
    }
  }

  // 회원 검색 필터링
  get filteredMembers() {
    if (!this.memberSearchQuery) {
      return this.members;
    }
    const query = this.memberSearchQuery.toLowerCase();
    return this.members.filter(
      (member) =>
        member.name.toLowerCase().includes(query) ||
        member.email.toLowerCase().includes(query),
    );
  }

  private handleSpaceSelect(spaceId: string) {
    console.log('Space selected:', spaceId);
  }

  // 포인트 설정 저장
  private async savePointSettings() {
    if (!this.currentChannelUid) {
      this.$message.error('채널 정보를 찾을 수 없습니다.');
      return;
    }

    this.saving = true;

    try {
      console.log('포인트 설정 저장 시도:', this.pointSettings);
      const response = await savePointSettings(this.currentChannelUid, this.pointSettings);
      console.log('포인트 설정 저장 응답:', response);
      this.$message.success('포인트 설정이 저장되었습니다.');
      
      // 저장 후 다시 로드하여 최신 데이터 확인
      await this.loadPointSettings();
    } catch (error: any) {
      console.error('포인트 설정 저장 실패:', error);
      console.error('에러 상세:', error.response);
      this.$message.error(error.response?.data?.message || '포인트 설정 저장에 실패했습니다.');
    } finally {
      this.saving = false;
    }
  }

  // 기본값으로 초기화
  private resetPointSettings() {
    if (!this.currentChannelUid) {
      this.$message.error('채널 정보를 찾을 수 없습니다.');
      return;
    }

    this.$confirm(
      '포인트 설정을 기본값으로 초기화하시겠습니까?',
      '설정 초기화',
      {
        confirmButtonText: '초기화',
        cancelButtonText: '취소',
        type: 'warning',
      },
    ).then(async () => {
      this.saving = true;
      try {
        const response = await resetPointSettings(this.currentChannelUid);
        const settings = response.data.settings;
        this.pointSettings = {
          postCreate: settings.postCreate,
          commentCreate: settings.commentCreate,
          likeGive: settings.likeGive,
          dailyAttendance: settings.dailyAttendance,
          marketplaceCreate: settings.marketplaceCreate,
          marketplaceSell: settings.marketplaceSell,
          courseComplete: settings.courseComplete,
          // 일일 제한
          postDailyLimit: settings.postDailyLimit ?? 10,
          commentDailyLimit: settings.commentDailyLimit ?? 20,
          likeDailyLimit: settings.likeDailyLimit ?? 30,
          marketplaceCreateDailyLimit: settings.marketplaceCreateDailyLimit ?? 5,
          marketplaceSellDailyLimit: settings.marketplaceSellDailyLimit ?? 0,
          courseCompleteDailyLimit: settings.courseCompleteDailyLimit ?? 0,
          // 최소 글자수
          postMinLength: settings.postMinLength ?? 20,
          commentMinLength: settings.commentMinLength ?? 5,
        };
        this.$message.success('포인트 설정이 기본값으로 초기화되었습니다.');
      } catch (error: any) {
        console.error('포인트 설정 초기화 실패:', error);
        this.$message.error(error.response?.data?.message || '포인트 설정 초기화에 실패했습니다.');
      } finally {
        this.saving = false;
      }
    }).catch(() => {
      // 취소
    });
  }

  // 포인트 설정 불러오기
  private async loadPointSettings() {
    if (!this.currentChannelUid) return;

    this.loadingPointSettings = true;
    try {
      const response = await getPointSettings(this.currentChannelUid);
      const settings = response.data;
      this.pointSettings = {
        postCreate: settings.postCreate ?? 100,
        commentCreate: settings.commentCreate ?? 50,
        likeGive: settings.likeGive ?? 10,
        dailyAttendance: settings.dailyAttendance ?? 50,
        marketplaceCreate: settings.marketplaceCreate ?? 100,
        marketplaceSell: settings.marketplaceSell ?? 200,
        courseComplete: settings.courseComplete ?? 150,
        // 일일 제한
        postDailyLimit: settings.postDailyLimit ?? 10,
        commentDailyLimit: settings.commentDailyLimit ?? 20,
        likeDailyLimit: settings.likeDailyLimit ?? 30,
        marketplaceCreateDailyLimit: settings.marketplaceCreateDailyLimit ?? 5,
        marketplaceSellDailyLimit: settings.marketplaceSellDailyLimit ?? 0,
        courseCompleteDailyLimit: settings.courseCompleteDailyLimit ?? 0,
        // 최소 글자수
        postMinLength: settings.postMinLength ?? 20,
        commentMinLength: settings.commentMinLength ?? 5,
      };
      // 기본값 백업도 업데이트
      this.defaultPointSettings = { ...this.pointSettings };
    } catch (error) {
      console.error('포인트 설정 조회 실패:', error);
      // 기본값 유지
    } finally {
      this.loadingPointSettings = false;
    }
  }

  // 포인트 조정 모달 열기
  private openPointModal(type: 'add' | 'subtract', member: Member) {
    this.pointModalType = type;
    this.selectedMember = member;
    this.pointAmount = 100;
    this.pointReason = '';
    this.pointModalVisible = true;
  }

  // 포인트 조정 실행
  private async adjustPoint() {
    if (!this.selectedMember || this.pointAmount <= 0) {
      this.$message.warning('올바른 포인트를 입력해주세요.');
      return;
    }

    if (!this.currentChannelUid) {
      this.$message.error('채널 정보를 찾을 수 없습니다.');
      return;
    }

    this.adjusting = true;

    try {
      // API 호출: 포인트 지급 또는 차감
      const adjustAmount = this.pointModalType === 'add' ? this.pointAmount : -this.pointAmount;
      
      await adminAdjustPoint({
        channelUid: this.currentChannelUid,
        targetUserUid: this.selectedMember.userUid,
        pointAmount: adjustAmount,
        description: this.pointReason || (this.pointModalType === 'add' ? '관리자 포인트 지급' : '관리자 포인트 차감'),
      });

      // 로컬 상태 업데이트
      const member = this.members.find((m) => m.memberUid === this.selectedMember!.memberUid);
      if (member) {
        member.point += adjustAmount;
      }

      this.$message.success(
        this.pointModalType === 'add'
          ? `${this.selectedMember.name}님에게 ${this.pointAmount.toLocaleString()}P를 지급했습니다.`
          : `${this.selectedMember.name}님으로부터 ${this.pointAmount.toLocaleString()}P를 차감했습니다.`
      );

      this.pointModalVisible = false;
      this.selectedMember = null;
    } catch (error: any) {
      console.error('포인트 조정 실패:', error);
      this.$message.error(error.response?.data?.message || '포인트 조정에 실패했습니다.');
    } finally {
      this.adjusting = false;
    }
  }
}
</script>

<style scoped lang="scss">
.community-management-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FA;
}

.community-management-main {
  flex: 1;
  margin-left: 310px;
  padding: 40px 60px;
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
  position: relative;

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
}

// 포인트 관리 섹션
.point-management-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.section-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #EBEBEB;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    color: #073DFF;
    font-size: 28px;
  }
}

.section-description {
  font-size: 14px;
  color: #666;
  margin: 0;
}

// 로딩 컨테이너
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 16px;

  .loading-icon {
    font-size: 48px;
    color: #073DFF;
    animation: rotating 1.5s linear infinite;
  }

  .loading-text {
    font-size: 16px;
    color: #666;
    margin: 0;
  }
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

// 포인트 설정 폼
.point-settings-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.point-setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #F8F9FA;
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    // background: #F0F2FF;
    // border-color: #073DFF;
  }
}

.setting-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.setting-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #E0E7FF;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #073DFF;
  font-size: 24px;
  flex-shrink: 0;
}

.setting-details {
  flex: 1;
}

.setting-title {
  font-size: 16px;
  font-weight: 600;
  color: #222;
  margin: 0 0 4px 0;
}

.setting-description {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.setting-input {
  display: flex;
  align-items: center;
  gap: 8px;

  ::v-deep .el-input-number {
    width: 150px;

    .el-input__inner {
      height: 44px;
      border-radius: 8px;
      text-align: left;
      padding-left: 15px;
      padding-right: 50px;
    }
  }

  .unit {
    font-size: 16px;
    font-weight: 600;
    color: #073DFF;
  }
}

// 설정 입력 그룹 (포인트 + 일일 제한 + 최소 글자수)
.setting-input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.setting-input-sub {
  display: flex;
  align-items: center;
  gap: 6px;
  
  .sub-label {
    font-size: 12px;
    color: #909399;
    min-width: 55px;
  }
  
  ::v-deep .el-input-number {
    width: 100px;
    
    .el-input__inner {
      height: 32px;
      border-radius: 6px;
      text-align: left;
      padding-left: 10px;
      padding-right: 40px;
    }
  }
  
  .sub-unit {
    font-size: 12px;
    color: #666;
    min-width: 20px;
  }
}

// 악용 방지 안내 (항목별)
.abuse-prevention-notice {
  margin-top: 12px;
  
  ::v-deep .el-alert {
    padding: 12px 16px;
    background: #F8F9FA;
    border-radius: 8px;
    text-align: left;

    .el-alert__title {
      font-size: 14px;
      font-weight: 600;
      color: #5470d6;
      margin-bottom: 6px;

      i {
        margin-right: 4px;
      }
    }

    .el-alert__content {
      padding: 0;
    }
  }
  
  .abuse-rules {
    font-size: 13px;
    color: #606266;
    line-height: 1.7;
  }
}

// 저장 섹션
.save-section {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 30px;
  border-top: 1px solid #EBEBEB;

  ::v-deep .el-button {
    min-width: 180px;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
  }
}

// 포인트 차감/지급 섹션
.point-adjust-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.member-search-section {
  margin-bottom: 30px;

  ::v-deep .el-input__inner {
    height: 44px;
    border-radius: 8px;
  }
}

.member-list-section {
  border: 1px solid #EBEBEB;
  border-radius: 8px;
  overflow: hidden;
}

.member-list-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  padding: 16px 20px;
  background: #F8F9FA;
  border-bottom: 1px solid #EBEBEB;
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.header-item {
  &.name {
    text-align: left;
  }

  &.point {
    text-align: center;
  }

  &.action {
    text-align: right;
  }
}

.member-list-body {
  max-height: 600px;
  overflow-y: auto;
}

.loading-members {
  text-align: center;
  padding: 60px 20px;
  color: #666;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
    color: #073DFF;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

.member-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #EBEBEB;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #F8F9FA;
  }
}

.member-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  .member-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: #E0E7FF;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #073DFF;
    font-size: 24px;
    flex-shrink: 0;
    overflow: hidden;

    .member-avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .member-details {
    text-align: left;
  }

  .member-name {
    font-size: 16px;
    font-weight: 600;
    color: #222;
    margin: 0 0 4px 0;
  }

  .member-email {
    font-size: 14px;
    color: #666;
    margin: 0;
  }
}

.member-point-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  .point-amount {
    font-size: 20px;
    font-weight: 700;
    color: #073DFF;
  }

  .point-unit {
    font-size: 14px;
    font-weight: 600;
    color: #073DFF;
  }
}

.member-action-cell {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.empty-members {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    display: block;
  }

  p {
    font-size: 16px;
    margin: 0;
  }
}

// 포인트 조정 모달
::v-deep .point-adjust-modal {
  border-radius: 12px;

  .el-dialog__header {
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
  }
}

.modal-content {
  position: relative;
}

.modal-close-btn {
  position: absolute;
  top: 0;
  right: 0;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #666;

  &:hover {
    color: #333;
  }
}

.modal-title {
  font-size: 24px;
  font-weight: 700;
  color: #222;
  margin: 0 0 24px 0;
}

.modal-member-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #F8F9FA;
  border-radius: 8px;
  margin-bottom: 24px;

  .member-avatar {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: #E0E7FF;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #073DFF;
    font-size: 28px;
    overflow: hidden;
    flex-shrink: 0;

    .member-avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .member-details {
    text-align: left;
  }

  .member-name {
    font-size: 18px;
    font-weight: 600;
    color: #222;
    margin: 0 0 6px 0;
  }

  .member-point {
    font-size: 14px;
    color: #666;
    margin: 0;

    strong {
      color: #073DFF;
      font-weight: 700;
    }
  }
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #222;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  ::v-deep .el-button {
    min-width: 100px;
    height: 44px;
  }
}

// 설정 섹션 (준비중)
.settings-section {
  background: #FFF;
  border: 1px solid #EBEBEB;
  border-radius: 12px;
  padding: 40px;
}

.empty-container {
  text-align: center;
  padding: 80px 20px;
  color: #999;

  i {
    font-size: 64px;
    margin-bottom: 20px;
    display: block;
  }

  p {
    font-size: 18px;
    margin: 0;
  }
}

// 모바일 반응형
@media screen and (max-width: 768px) {
  .community-management-main {
    margin-left: 0;
    padding: 40px 16px;
  }

  .tabs-section {
    gap: 12px;
    overflow-x: auto;
    white-space: nowrap;
    padding-bottom: 2px;
  }

  .tab-btn {
    font-size: 16px;
    padding: 12px 16px;
  }

  .point-management-section {
    padding: 24px 16px;
  }

  .point-setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .setting-info {
    width: 100%;
  }

  .setting-input {
    width: 100%;
    justify-content: space-between;

    ::v-deep .el-input-number {
      flex: 1;
      max-width: 200px;
    }
  }

  .save-section {
    flex-direction: column;

    ::v-deep .el-button {
      width: 100%;
    }
  }

  .member-list-header {
    display: none;
  }

  .member-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .member-point-cell {
    justify-content: flex-start;
    padding-left: 60px;
  }

  .member-action-cell {
    justify-content: flex-start;
    padding-left: 60px;

    .el-button {
      flex: 1;
    }
  }

  ::v-deep .point-adjust-modal {
    .el-dialog {
      width: 95% !important;
    }

    .el-dialog__body {
      padding: 24px;
    }
  }

  .modal-actions {
    flex-direction: column-reverse;

    ::v-deep .el-button {
      width: 100%;
    }
  }
}

// 425px 이하 모바일 반응형
@media screen and (max-width: 425px) {
  .community-management-main {
    padding: 40px 16px;
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
    white-space: nowrap;
  }

  // 포인트 관리 섹션
  .point-management-section {
    padding: 20px 16px;
    border-radius: 10px;
  }

  .section-header {
    margin-bottom: 20px;
    padding-bottom: 16px;
  }

  .section-title {
    font-size: 18px;
    gap: 8px;

    i {
      font-size: 22px;
    }
  }

  .section-description {
    font-size: 13px;
  }

  // 포인트 설정 폼
  .point-settings-form {
    gap: 12px;
    margin-bottom: 24px;
  }

  .point-setting-item {
    padding: 14px;
    border-radius: 8px;
    gap: 12px;
  }

  .setting-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .setting-title {
    font-size: 14px;
  }

  .setting-description {
    font-size: 12px;
  }

  .setting-input {
    ::v-deep .el-input-number {
      width: 120px;

      .el-input__inner {
        height: 38px;
        font-size: 14px;
        padding-left: 12px;
        padding-right: 40px;
      }

      .el-input-number__decrease,
      .el-input-number__increase {
        width: 32px;
      }
    }

    .unit {
      font-size: 14px;
    }
  }

  // 저장 섹션
  .save-section {
    padding-top: 20px;
    gap: 10px;

    ::v-deep .el-button {
      min-width: auto;
      width: 100%;
      height: 44px;
      font-size: 14px;
    }
  }

  // 포인트 차감/지급 섹션
  .point-adjust-section {
    padding: 20px 16px;
    border-radius: 10px;
  }

  .member-search-section {
    margin-bottom: 20px;

    ::v-deep .el-input__inner {
      height: 40px;
      font-size: 14px;
    }
  }

  .member-list-section {
    border-radius: 8px;
  }

  .member-list-header {
    display: none;
  }

  .member-list-body {
    max-height: 450px;
  }

  .member-row {
    grid-template-columns: 1fr;
    gap: 12px;
    padding: 16px;
  }

  .member-info-cell {
    gap: 10px;

    .member-avatar {
      width: 40px;
      height: 40px;
      font-size: 20px;
    }

    .member-name {
      font-size: 14px;
      margin-bottom: 2px;
    }

    .member-email {
      font-size: 12px;
    }
  }

  .member-point-cell {
    justify-content: flex-start;
    padding-left: 50px;
    gap: 3px;

    .point-amount {
      font-size: 18px;
    }

    .point-unit {
      font-size: 12px;
    }
  }

  .member-action-cell {
    justify-content: flex-start;
    padding-left: 50px;
    gap: 8px;

    .el-button {
      flex: 1;
      padding: 8px 12px;
      font-size: 12px;
    }
  }

  .empty-members {
    padding: 40px 16px;

    i {
      font-size: 36px;
      margin-bottom: 12px;
    }

    p {
      font-size: 14px;
    }
  }

  // 설정 섹션
  .settings-section {
    padding: 20px 16px;
    border-radius: 10px;
  }

  .empty-container {
    padding: 60px 20px;

    i {
      font-size: 48px;
      margin-bottom: 16px;
    }

    p {
      font-size: 14px;
    }
  }

  // 포인트 조정 모달
  ::v-deep .point-adjust-modal {
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

  .modal-close-btn {
    top: 16px;
    right: 16px;
  }

  .modal-title {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .modal-member-info {
    padding: 16px;
    gap: 12px;
    margin-bottom: 20px;
    border-radius: 8px;

    .member-avatar {
      width: 48px;
      height: 48px;
      font-size: 24px;
    }

    .member-name {
      font-size: 16px;
      margin-bottom: 4px;
    }

    .member-point {
      font-size: 13px;
    }
  }

  .modal-form {
    gap: 16px;
    margin-bottom: 20px;
  }

  .form-group {
    gap: 6px;
  }

  .form-label {
    font-size: 13px;
  }

  .modal-actions {
    gap: 10px;

    ::v-deep .el-button {
      min-width: auto;
      flex: 1;
      height: 44px;
      font-size: 14px;
    }
  }
}

// 커뮤니티 설정 탭 스타일
.community-setting-content {
  max-width: 1200px;
  margin: 0 auto;
}

.community-form {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.settings-form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

.form-column {
  display: flex;
  flex-direction: column;
  gap: 36px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 16px;

  &.form-group-full {
    grid-column: 1 / -1;
  }
}

.settings-section .form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.form-hint {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 100%;
  margin: 0;
}

.upload-box {
  width: 100%;
  height: 219px;
  border: 1px dashed #CECECE;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F9FB;
  transition: all 0.3s;

  &:hover {
    border-color: #073DFF;
    background: #F0F4FF;
  }
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-hint {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  line-height: 150%;
  text-align: center;
}

.upload-btn {
  display: flex;
  width: 126px;
  height: 54px;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #FFF;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 150%;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0530CC;
    border-color: #FFF;
  }

  &:active {
    background: #042099;
  }
}

.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;

  &.cover-preview {
    .image-preview-item {
      width: calc(50% - 8px);
    }
  }
}

.image-preview-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
}

.preview-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;

  &.cover-preview-image {
    height: 100px;
  }
}

.image-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 150px;
}

.remove-image-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  background: #E74C3C;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #C0392B;
  }

  svg {
    width: 12px;
    height: 12px;
  }
}

.add-more-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: calc(50% - 8px);
  min-height: 150px;
  border: 1px dashed #CECECE;
  border-radius: 10px;
  background: #F8F9FB;
  transition: all 0.3s;

  &:hover {
    border-color: #073DFF;
    background: #F0F4FF;
  }
}

.add-more-btn {
  padding: 16px 32px;
  border: none;
  background: transparent;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
}

.settings-section .form-input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }

  &:read-only {
    background: #F5F5F5;
    cursor: not-allowed;
  }
}

.settings-section .form-textarea {
  width: 100%;
  min-height: 156px;
  padding: 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  outline: none;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }
}

::v-deep .category-select {
  width: 100%;

  .el-input__inner {
    height: 52px;
    padding: 0 16px;
    border: 1px solid #CECECE;
    border-radius: 10px;
    background: #FFF;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 150%;

    &::placeholder {
      color: #888;
    }
  }

  .el-input__suffix {
    display: flex;
    align-items: center;
    padding-right: 16px;
  }
}

.access-control {
  display: flex;
  align-items: center;
  gap: 20px;
}

.access-btn {
  display: flex;
  height: 54px;
  padding: 12px 0;
  justify-content: center;
  align-items: center;
  flex: 1;
  border-radius: 10px;
  border: 1px solid #FFF;
  background: #D2D2D2;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s;

  &.active {
    background: #073DFF;
    border-color: #FFF;
  }

  &:hover {
    opacity: 0.9;
  }
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.question-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.remove-btn {
  height: 52px;
  padding: 0 24px;
  border: 1px solid #E74C3C;
  border-radius: 10px;
  background: #E74C3C;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;

  &:hover {
    background: #C0392B;
    border-color: #C0392B;
  }
}

.add-question-btn {
  width: 100%;
  height: 52px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  background: #FFF;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #073DFF;
    background: #F8F9FF;
  }
}

.settings-section .form-actions {
  display: flex;
  justify-content: center;
  padding-top: 24px;
}

.save-btn {
  min-width: 200px;
  height: 56px;
  padding: 0 48px;
  border: none;
  border-radius: 10px;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #0530CC;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

.settings-section ::v-deep .el-form-item {
  margin-bottom: 0;
}

.settings-section ::v-deep .el-form-item__error {
  color: #E74C3C;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  padding-top: 8px;
}

// 커뮤니티 설정 반응형
@media screen and (max-width: 768px) {
  .settings-form-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .form-column {
    gap: 24px;
  }

  .image-preview-container.cover-preview .image-preview-item {
    width: 100%;
  }

  .add-more-container {
    width: 100%;
  }
}

@media screen and (max-width: 425px) {
  .settings-section .form-label {
    font-size: 16px;
  }

  .upload-box {
    height: 180px;
  }

  .upload-btn {
    width: 100px;
    height: 44px;
    font-size: 14px;
  }

  .access-control {
    gap: 12px;
  }

  .access-btn {
    height: 44px;
    font-size: 14px;
  }

  .question-item {
    flex-direction: column;
    align-items: stretch;
  }

  .remove-btn {
    height: 44px;
    font-size: 14px;
  }

  .add-question-btn {
    height: 44px;
    font-size: 14px;
  }

  .save-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    padding: 0 24px;
  }
}
</style>
