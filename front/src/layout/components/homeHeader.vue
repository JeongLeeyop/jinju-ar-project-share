<template>
  <div class="header home-header" :class="isInCommunity ? 'community-header': '' ">
    <div class="header-container">
      <button class="mobile-menu-btn" @click="toggleMobileMenu">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3.75 6.75H20.25M3.75 12H20.25M3.75 17.25H20.25" stroke="black" stroke-width="1.5"
            stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </button>
      <div class="header-left">
        <div class="header-logo" @click="moveHome">
          <img src="@/assets/images/logo.png" alt="진주알 커뮤니티">
          <div v-if="isInCommunity" class="member-badge">
            <span>회원 수: {{ memberCount.toLocaleString() }}명</span>
          </div>
        </div>
      </div>
      <div class="mobile-menu-overlay" v-if="mobileMenuOpen" @click="toggleMobileMenu"></div>
      <div class="header-nav" :class="{ 'mobile-open': mobileMenuOpen }">
        <button class="mobile-close-btn" @click="toggleMobileMenu">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="black" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
        </button>

        <router-link to="/" class="nav-item" :class="{ active: isActiveRoute('Home') }" @click.native="toggleMobileMenu">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M14.6249 17.4686C14.6249 17.2446 14.4426 17.0624 14.2187 17.0624H11.7812C11.5572 17.0624 11.3749 17.2446 11.3749 17.4686V21.9374H14.6249V17.4686ZM21.9374 21.5311C21.9374 22.6526 21.0276 23.5624 19.9062 23.5624H6.09365C4.97217 23.5624 4.0624 22.6526 4.0624 21.5311V12.5227L3.01187 13.5743C2.69458 13.8917 2.18026 13.8916 1.86294 13.5743C1.54569 13.257 1.54568 12.7427 1.86294 12.4254L11.5632 2.72407V2.72301C12.3566 1.93141 13.6423 1.93095 14.4355 2.72407L24.1369 12.4254C24.4542 12.7427 24.4541 13.257 24.1369 13.5743C23.8196 13.8916 23.3052 13.8916 22.9879 13.5743L21.9374 12.5238V21.5311ZM16.2499 21.9374H19.9062C20.1302 21.9374 20.3124 21.7551 20.3124 21.5311V10.8988L13.2866 3.873C13.1287 3.7152 12.8709 3.71458 12.7111 3.87406L5.6874 10.8977V21.5311C5.6874 21.7551 5.86964 21.9374 6.09365 21.9374H9.7499V17.4686C9.74994 16.3472 10.6597 15.4374 11.7812 15.4374H14.2187C15.3401 15.4374 16.2499 16.3472 16.2499 17.4686V21.9374Z"
              :fill="isActiveRoute('Home') ? '#073DFF' : '#444444'" />
          </svg>
          <span>홈</span>
        </router-link>

        <!-- Community Sidebar Items (Only show when in community) -->
        <template v-if="isInCommunity">
          <!-- 공간 개설하기 -->
          <div class="nav-item nav-section-title" @click="handleCreateSpace">
            <span>공간 개설하기</span>
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 5V15M15 10H5" stroke="#073DFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>          <!-- Dynamic Space List from API -->
            <div 
              v-for="space in spaces" 
              :key="space.uid" 
              class="nav-item nav-subitem" 
              @click="navigateToSpace(space.uid, space.name, space.spaceType)"
            >
              <span 
                class="space-dot" 
                :class="getSpaceDotClass(space.spaceType)"
              ></span>
              <span>{{ space.name }}</span>
              <!-- <span v-if="space.isAdmin" class="admin-badge-inline">관리자</span> -->
            </div>
            
            <!-- Empty State -->
            <div v-if="spaces.length === 0 && !loadingSpaces && isInCommunity" class="nav-item nav-subitem empty-state">
              <span>생성된 공간이 없습니다</span>
            </div>
        </template>

        <router-link :to="{ name: 'Lession' }" class="nav-item" :class="{ active: isActiveRoute('Lession') }"
          @click.native="toggleMobileMenu">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M13 6.77404V20.8574M13 6.77404C11.7347 5.93338 10.0165 5.41663 8.125 5.41663C6.2335 5.41663 4.51533 5.93338 3.25 6.77404V20.8574C4.51533 20.0167 6.2335 19.5 8.125 19.5C10.0165 19.5 11.7347 20.0167 13 20.8574M13 6.77404C14.2653 5.93338 15.9835 5.41663 17.875 5.41663C19.7676 5.41663 21.4847 5.93338 22.75 6.77404V20.8574C21.4847 20.0167 19.7676 19.5 17.875 19.5C15.9835 19.5 14.2653 20.0167 13 20.8574"
              :stroke="isActiveRoute('Lession') ? '#073DFF' : '#444444'" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
          <span>강좌</span>
        </router-link>
        <router-link :to="{ name: 'Calendar' }" class="nav-item" :class="{ active: isActiveRoute('Calendar') }"
          @click.native="toggleMobileMenu">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M8.66667 7.58333V3.25M17.3333 7.58333V3.25M7.58333 11.9167H18.4167M5.41667 22.75H20.5833C21.158 22.75 21.7091 22.5217 22.1154 22.1154C22.5217 21.7091 22.75 21.158 22.75 20.5833V7.58333C22.75 7.0087 22.5217 6.4576 22.1154 6.05127C21.7091 5.64494 21.158 5.41667 20.5833 5.41667H5.41667C4.84203 5.41667 4.29093 5.64494 3.8846 6.05127C3.47827 6.4576 3.25 7.0087 3.25 7.58333V20.5833C3.25 21.158 3.47827 21.7091 3.8846 22.1154C4.29093 22.5217 4.84203 22.75 5.41667 22.75Z"
              :stroke="isActiveRoute('Calendar') ? '#073DFF' : '#444444'" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
          <span>일정</span>
        </router-link>
        <router-link :to="{ name: 'Marketplace' }" class="nav-item" :class="{ active: isActiveRoute('Marketplace') }"
          @click.native="toggleMobileMenu">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M17.333 11.9167V7.58333C17.333 6.43406 16.8765 5.33186 16.0638 4.5192C15.2511 3.70655 14.1489 3.25 12.9997 3.25C11.8504 3.25 10.7482 3.70655 9.93555 4.5192C9.12289 5.33186 8.66634 6.43406 8.66634 7.58333V11.9167M5.41634 9.75H20.583L21.6663 22.75H4.33301L5.41634 9.75Z"
              :stroke="isActiveRoute('Marketplace') ? '#073DFF' : '#444444'" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
          <span>장터</span>
        </router-link>
        <router-link :to="{ name: 'Member' }" class="nav-item" :class="{ active: isActiveRoute('Member') }"
          @click.native="toggleMobileMenu">
          <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
              d="M18.417 21.6667H23.8337V19.5C23.8336 18.8246 23.6231 18.166 23.2315 17.6157C22.8398 17.0654 22.2865 16.6509 21.6483 16.4296C21.0101 16.2083 20.3189 16.1914 19.6707 16.3811C19.0225 16.5709 18.4495 16.9579 18.0313 17.4883M18.417 21.6667H7.58366M18.417 21.6667V19.5C18.417 18.7894 18.2805 18.1101 18.0313 17.4883M18.0313 17.4883C17.629 16.4829 16.9347 15.6211 16.038 15.0141C15.1413 14.4071 14.0832 14.0826 13.0003 14.0826C11.9174 14.0826 10.8594 14.4071 9.96266 15.0141C9.06592 15.6211 8.37163 16.4829 7.96933 17.4883M7.58366 21.6667H2.16699V19.5C2.16704 18.8246 2.37752 18.166 2.76918 17.6157C3.16083 17.0654 3.71419 16.6509 4.35235 16.4296C4.9905 16.2083 5.68173 16.1914 6.32996 16.3811C6.97818 16.5709 7.55119 16.9579 7.96933 17.4883M7.58366 21.6667V19.5C7.58366 18.7894 7.72016 18.1101 7.96933 17.4883M16.2503 7.58337C16.2503 8.44533 15.9079 9.27198 15.2984 9.88147C14.6889 10.491 13.8623 10.8334 13.0003 10.8334C12.1384 10.8334 11.3117 10.491 10.7022 9.88147C10.0927 9.27198 9.75032 8.44533 9.75032 7.58337C9.75032 6.72142 10.0927 5.89477 10.7022 5.28528C11.3117 4.67578 12.1384 4.33337 13.0003 4.33337C13.8623 4.33337 14.6889 4.67578 15.2984 5.28528C15.9079 5.89477 16.2503 6.72142 16.2503 7.58337ZM22.7503 10.8334C22.7503 11.408 22.5221 11.9591 22.1157 12.3654C21.7094 12.7718 21.1583 13 20.5837 13C20.009 13 19.4579 12.7718 19.0516 12.3654C18.6453 11.9591 18.417 11.408 18.417 10.8334C18.417 10.2587 18.6453 9.70764 19.0516 9.30131C19.4579 8.89498 20.009 8.66671 20.5837 8.66671C21.1583 8.66671 21.7094 8.89498 22.1157 9.30131C22.5221 9.70764 22.7503 10.2587 22.7503 10.8334ZM7.58366 10.8334C7.58366 11.408 7.35539 11.9591 6.94906 12.3654C6.54273 12.7718 5.99163 13 5.41699 13C4.84236 13 4.29126 12.7718 3.88493 12.3654C3.4786 11.9591 3.25033 11.408 3.25033 10.8334C3.25033 10.2587 3.4786 9.70764 3.88493 9.30131C4.29126 8.89498 4.84236 8.66671 5.41699 8.66671C5.99163 8.66671 6.54273 8.89498 6.94906 9.30131C7.35539 9.70764 7.58366 10.2587 7.58366 10.8334Z"
              :stroke="isActiveRoute('Member') ? '#073DFF' : '#444444'" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round" />
          </svg>
          <span>회원</span>
        </router-link>

        <!-- 오프라인 장터 섹션 (Only show when in community) -->
        <template v-if="isInCommunity">
          <div class="nav-divider"></div>
          <div class="nav-item nav-section-title">
            <span>오프라인 장터</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToMarketplace('sancheong-clinic')">
            <span>산청 한의원</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToMarketplace('dongsan-cafe')">
            <span>동산카페</span>
          </div>

          <!-- 마이페이지 섹션 -->
          <div class="nav-divider"></div>
          <div class="nav-item nav-section-title">
            <span>마이페이지</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToPage('MyMarketplace')">
            <span>-내 장터 관리</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToPage('MySchedule')">
            <span>-내 일정 관리</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToPage('RPointHistory')">
            <span>-R포인트 내역</span>
          </div>
          <div class="nav-item nav-subitem" @click="navigateToPage('ActivityList')">
            <span>-활동리스트</span>
          </div>

          <!-- 매니저 메뉴 섹션 (공간생성 또는 오프라인장터등록 권한이 있는 경우에만 표시) -->
          <template v-if="hasManagerPermission || isChannelCreator">
            <div class="nav-divider"></div>
            <div class="nav-item nav-section-title">
              <span>매니저 메뉴</span>
            </div>
            <div class="nav-item nav-subitem" @click="navigateToPage('SpaceManagement')">
              <span>-공간 관리</span>
            </div>
            <div class="nav-item nav-subitem" @click="navigateToPage('SmsManagement')">
              <span>-문자 발송</span>
            </div>
          </template>

          <!-- 관리자 메뉴 섹션 (커뮤니티 생성자인 경우에만 표시) -->
          <template v-if="isChannelCreator">
            <div class="nav-divider"></div>
            <div class="nav-item nav-section-title">
              <span>관리자 메뉴</span>
            </div>
            <div class="nav-item nav-subitem" @click="navigateToPage('MemberManagement')">
              <span>-회원 관리</span>
            </div>
            <div class="nav-item nav-subitem" @click="navigateToPage('CommunityManagement')">
              <span>-커뮤니티 관리</span>
            </div>
          </template>
        </template>
      </div>

      <!-- Mobile User Profile Menu -->
      <div class="mobile-user-menu" v-if="showMobileProfile && isLogin" @click="closeMobileProfile">
        <div class="mobile-user-menu-content" @click.stop>
          <div class="mobile-user-header">
            <div class="user-avatar">
              <img v-if="userInfo.iconFileUid" :src="`${apiUrl}/attached-file/${userInfo.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
              <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                <mask id="mask0_mobile_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36"
                  height="36">
                  <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                </mask>
                <g mask="url(#mask0_mobile_user)">
                  <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5" />
                  <circle cx="18" cy="11" r="7" fill="#F5F5F5" />
                </g>
              </svg>
            </div>
            <div class="mobile-user-info">
              <span class="mobile-user-name">{{ userInfo.actualName || '사용자' }}님</span>
              <span class="mobile-user-points">알포인트: {{ userPoints.toLocaleString() }}</span>
            </div>
            <button class="mobile-menu-close" @click="closeMobileProfile">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 6L6 18M6 6L18 18" stroke="black" stroke-width="2" stroke-linecap="round"
                  stroke-linejoin="round" />
              </svg>
            </button>
          </div>
          <div class="mobile-user-actions">
            <button class="mobile-action-btn" @click="handleUserInfoMobile">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M10 12.5C11.3807 12.5 12.5 11.3807 12.5 10C12.5 8.61929 11.3807 7.5 10 7.5C8.61929 7.5 7.5 8.61929 7.5 10C7.5 11.3807 8.61929 12.5 10 12.5Z"
                  stroke="#444" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
                <path
                  d="M16.1667 12.5C16.0557 12.7513 16.0226 13.0302 16.0717 13.3005C16.1209 13.5708 16.2501 13.8203 16.4417 14.0167L16.4917 14.0667C16.6461 14.221 16.7687 14.4046 16.8527 14.6071C16.9368 14.8096 16.9806 15.0269 16.9806 15.2467C16.9806 15.4665 16.9368 15.6838 16.8527 15.8863C16.7687 16.0888 16.6461 16.2724 16.4917 16.4267C16.3373 16.5811 16.1537 16.7037 15.9512 16.7877C15.7487 16.8718 15.5315 16.9156 15.3117 16.9156C15.0918 16.9156 14.8746 16.8718 14.6721 16.7877C14.4696 16.7037 14.286 16.5811 14.1317 16.4267L14.0817 16.3767C13.8853 16.1851 13.6358 16.0559 13.3655 16.0067C13.0952 15.9576 12.8163 15.9907 12.565 16.1017C12.3137 16.2127 12.1013 16.3967 11.9547 16.6309C11.8082 16.8651 11.7339 17.1391 11.7417 17.4167V17.5833C11.7417 18.0308 11.5639 18.4599 11.2482 18.7756C10.9325 19.0913 10.5034 19.2692 10.0558 19.2692C9.60832 19.2692 9.17919 19.0913 8.86351 18.7756C8.54783 18.4599 8.37 18.0308 8.37 17.5833V17.5C8.37 17.2224 8.29567 16.9484 8.14913 16.7142C8.00259 16.48 7.79019 16.296 7.53889 16.185C7.28759 16.074 7.00868 16.0409 6.73839 16.09C6.4681 16.1392 6.21859 16.2684 6.02222 16.46L5.97222 16.51C5.81789 16.6644 5.63427 16.787 5.43178 16.871C5.22929 16.9551 5.01201 16.9989 4.79222 16.9989C4.57244 16.9989 4.35516 16.9551 4.15267 16.871C3.95018 16.787 3.76656 16.6644 3.61222 16.51C3.45785 16.3557 3.33524 16.1721 3.25119 15.9696C3.16714 15.7671 3.12333 15.5498 3.12333 15.33C3.12333 15.1102 3.16714 14.8929 3.25119 14.6904C3.33524 14.4879 3.45785 14.3043 3.61222 14.15L3.66222 14.1C3.85382 13.9036 3.98301 13.6541 4.03217 13.3838C4.08133 13.1135 4.04818 12.8346 3.93722 12.5833C3.82626 12.332 3.64225 12.1196 3.40805 11.9731C3.17385 11.8265 2.89985 11.7522 2.62222 11.76H2.45556C2.00802 11.76 1.57889 11.5822 1.26321 11.2665C0.947529 10.9508 0.769699 10.5217 0.769699 10.0742C0.769699 9.62662 0.947529 9.19749 1.26321 8.88181C1.57889 8.56613 2.00802 8.3883 2.45556 8.3883H2.52222C2.79985 8.38051 3.07385 8.30618 3.30805 8.15964C3.54225 8.0131 3.72626 7.8007 3.83722 7.5494C3.94818 7.2981 3.98133 7.01919 3.93217 6.7489C3.88301 6.47861 3.75382 6.2291 3.56222 6.03273L3.51222 5.98273C3.35785 5.8284 3.23524 5.64478 3.15119 5.44229C3.06714 5.2398 3.02333 5.02252 3.02333 4.80273C3.02333 4.58295 3.06714 4.36567 3.15119 4.16318C3.23524 3.96069 3.35785 3.77707 3.51222 3.62273C3.66656 3.46837 3.85018 3.34576 4.05267 3.26171C4.25516 3.17766 4.47244 3.13385 4.69222 3.13385C4.91201 3.13385 5.12929 3.17766 5.33178 3.26171C5.53427 3.34576 5.71789 3.46837 5.87222 3.62273L5.92222 3.67273C6.11859 3.86433 6.3681 3.99352 6.63839 4.04268C6.90868 4.09184 7.18759 4.05869 7.43889 3.94773H7.53889C7.79019 3.83677 8.00259 3.65276 8.14913 3.41856C8.29567 3.18436 8.37 2.91036 8.37 2.63273V2.46607C8.37 2.01853 8.54783 1.5894 8.86351 1.27372C9.17919 0.958039 9.60832 0.780212 10.0558 0.780212C10.5034 0.780212 10.9325 0.958039 11.2482 1.27372C11.5639 1.5894 11.7417 2.01853 11.7417 2.46607V2.53273C11.7417 2.81036 11.816 3.08436 11.9626 3.31856C12.1091 3.55276 12.3215 3.73677 12.5728 3.84773C12.8241 3.95869 13.103 3.99184 13.3733 3.94268C13.6436 3.89352 13.8931 3.76433 14.0895 3.57273L14.1395 3.52273C14.2938 3.36837 14.4774 3.24576 14.6799 3.16171C14.8824 3.07766 15.0997 3.03385 15.3195 3.03385C15.5393 3.03385 15.7565 3.07766 15.959 3.16171C16.1615 3.24576 16.3451 3.36837 16.4995 3.52273C16.6539 3.67707 16.7765 3.86069 16.8605 4.06318C16.9446 4.26567 16.9884 4.48295 16.9884 4.70273C16.9884 4.92252 16.9446 5.1398 16.8605 5.34229C16.7765 5.54478 16.6539 5.7284 16.4995 5.88273L16.4495 5.93273C16.2579 6.1291 16.1287 6.37861 16.0796 6.6489C16.0304 6.91919 16.0635 7.1981 16.1745 7.4494C16.2855 7.7007 16.4695 7.9131 16.7037 8.05964C16.9379 8.20618 17.2119 8.28051 17.4895 8.2883H17.6562C18.1037 8.2883 18.5328 8.46613 18.8485 8.78181C19.1642 9.09749 19.342 9.52662 19.342 9.97416C19.342 10.4217 19.1642 10.8508 18.8485 11.1665C18.5328 11.4822 18.1037 11.66 17.6562 11.66H17.5895C17.3119 11.66 17.0379 11.7344 16.8037 11.8809C16.5695 12.0274 16.3855 12.2398 16.2745 12.4911V12.5Z"
                  stroke="#444" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <span>내 정보 수정</span>
            </button>
            <button class="mobile-action-btn" @click="handleLogoutMobile">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M7.5 17.5H4.16667C3.72464 17.5 3.30072 17.3244 2.98816 17.0118C2.67559 16.6993 2.5 16.2754 2.5 15.8333V4.16667C2.5 3.72464 2.67559 3.30072 2.98816 2.98816C3.30072 2.67559 3.72464 2.5 4.16667 2.5H7.5M13.3333 14.1667L17.5 10M17.5 10L13.3333 5.83333M17.5 10H7.5"
                  stroke="#444" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
              </svg>
              <span>로그아웃</span>
            </button>
          </div>
        </div>
      </div>
    
      <!-- Create Space Modal Component -->
      <CreateSpaceModal
        v-if="createSpaceModalVisible"
        class="create-space-modal"
        :visible.sync="createSpaceModalVisible"
        @space-created="handleSpaceCreated"
      />

      <div class="mobile-user-icon" @click="handleMobileUser">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path
            d="M15.75 6C15.75 6.99456 15.3549 7.94839 14.6516 8.65165C13.9484 9.35491 12.9945 9.75 12 9.75C11.0054 9.75 10.0516 9.35491 9.34833 8.65165C8.64506 7.94839 8.24998 6.99456 8.24998 6C8.24998 5.00544 8.64506 4.05161 9.34833 3.34835C10.0516 2.64509 11.0054 2.25 12 2.25C12.9945 2.25 13.9484 2.64509 14.6516 3.34835C15.3549 4.05161 15.75 5.00544 15.75 6ZM4.50098 20.118C4.53311 18.1504 5.33731 16.2742 6.74015 14.894C8.14299 13.5139 10.0321 12.7405 12 12.7405C13.9679 12.7405 15.857 13.5139 17.2598 14.894C18.6626 16.2742 19.4668 18.1504 19.499 20.118C17.1464 21.1968 14.5881 21.7535 12 21.75C9.32398 21.75 6.78398 21.166 4.50098 20.118Z"
            stroke="black" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
      </div>
      <!-- <div class="header-content" :class="{ 'mobile-open': mobileMenuOpen }"> -->
      <div class="header-content">
        <div class="aside-btn">
          <slot v-if="!isLogin">
            <button class="login-button" @click="handleLoginModal">로그인</button>
          </slot>
          <slot v-else>
            <div class="user-section">
              <!-- <button class="notification-btn">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M12 22C13.1 22 14 21.1 14 20H10C10 21.1 10.89 22 12 22ZM18 16V11C18 7.93 16.36 5.36 13.5 4.68V4C13.5 3.17 12.83 2.5 12 2.5C11.17 2.5 10.5 3.17 10.5 4V4.68C7.63 5.36 6 7.92 6 11V16L4 18V19H20V18L18 16Z"
                    fill="#444" />
                </svg>
              </button> -->
              <div class="points-display">
                <span class="points-label">알 포인트:</span>
                <span class="points-value">{{ userPoints.toLocaleString() }}</span>
              </div>
              <el-popover v-model="showProfile" placement="bottom-end" width="450" trigger="click" popper-class="alarm"
                :popper-append-to-body="false" :title='`${userInfo.actualName}님 환영합니다.`'>
                <div @click="showProfile = false" class="alarm-close">
                  <i class="el-icon-close"></i>
                </div>
                <div class="empty">
                  <el-button @click="handleLogout()">로그아웃</el-button>
                  <el-button @click="handleUserInfo()">내정보수정</el-button>
                </div>
                <div slot="reference" class="user-profile">
                  <div class="user-avatar">
                    <img v-if="userInfo.iconFileUid" :src="`${apiUrl}/attached-file/${userInfo.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                    <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                      <mask id="mask0_user" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36"
                        height="36">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9" />
                      </mask>
                      <g mask="url(#mask0_user)">
                        <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5" />
                        <circle cx="18" cy="11" r="7" fill="#F5F5F5" />
                      </g>
                    </svg>
                  </div>
                  <span class="user-name-text">{{ userInfo.actualName || '' }} 님</span>
                </div>
              </el-popover>
            </div>
          </slot>
        </div>
        <div class="component-container">
          <div class="component">
            <UserInfo :userModalVisible="userModalVisible" @close="userModalVisible = false;" />
            <UserModal :userModalVisible="loginModalVisible" :activeStep="loginActiveStep"
              @close="loginModalVisible = false;" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { PermissionModule } from '@/store/modules/permission';
import { UserModule } from '@/store/modules/user';
import ChannelSelect from '@/views/components/channelSelect.vue';
import Alram from '@/views/components/alram.vue';
import UserInfo from '@/views/components/UserInfo.vue';
import UserModal from '@/views/components/userModal.vue';
import CreateSpaceModal from '@/views/components/CreateSpaceModal.vue';
import { getUserInfo, updateOnline } from '@/api/user';
import { getChannelMemberCount, getChannelDomainDetail } from '@/api/channel';
import { getSpacesByChannel, Space } from '@/api/space';
import { checkPermissionByUser } from '@/api/channelMemberPermission';
import { EventBus, EVENTS } from '@/utils/eventBus';
import path from 'path';
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component({
  components: {
    ChannelSelect,
    UserInfo,
    UserModal,
    Alram,
    CreateSpaceModal,
  },
})

export default class extends Vue {
  async mounted() {
    if (UserModule.isLogin) {
      await getUserInfo().then((res) => {
        this.userInfo = res.data;
        // 전역 포인트 설정 (user 테이블의 point)
        this.userPoints = res.data.point || 0;
        if (!this.userInfo.isOnline) {
          this.userInfo = { ...this.userInfo, isOnline: true };
          updateOnline().then(() => {
            console.log();
          });
        }
      });
    }
    // Fetch member count, spaces, and permissions on initial load
    this.fetchMemberCount();
    await this.loadSpaces();
    await this.checkPermissions();

    // EventBus: 포인트 갱신 이벤트 리스닝
    EventBus.$on(EVENTS.POINTS_UPDATED, this.refreshUserPoints);
  }

  beforeDestroy() {
    // EventBus 리스너 제거
    EventBus.$off(EVENTS.POINTS_UPDATED, this.refreshUserPoints);
  }

  /**
   * 사용자 포인트 갱신 (EventBus에서 호출)
   */
  private async refreshUserPoints() {
    if (UserModule.isLogin) {
      try {
        const res = await getUserInfo();
        this.userInfo = res.data;
        this.userPoints = res.data.point || 0;
      } catch (error) {
        console.error('Failed to refresh user points:', error);
      }
    }
  }

  get routes() {
    return PermissionModule.routes;
  }

  private userInfo: any = {};

  private showProfile = false;

  // 로그인 창 모달
  private userModalVisible = false;

  private loginModalVisible = false;

  private loginActiveStep = 'Login';

  private createSpaceModalVisible = false;

  private actualName = UserModule.actualName;

  private activeChannel = '1';

  private apiUrl = process.env.VUE_APP_BASE_API;

  private memberCount = 0;

  private userPoints = 0;

  private mobileMenuOpen = false;

  private showMobileProfile = false;

  private spaces: Space[] = [];

  private loadingSpaces = false;

  // Permission and role flags
  private hasManagerPermission = false;

  private isChannelCreator = false;

  private currentChannelUid = '';

  @Watch('$route.path')
  private async handlechangepath() {
    this.fetchMemberCount();
    await this.loadSpaces();
    await this.checkPermissions();
    this.mobileMenuOpen = false;
    this.showMobileProfile = false;
  }

  private async fetchMemberCount() {
    // Check if we're on a community page
    if (this.$route.path.startsWith('/community/')) {
      const { domain } = this.$route.params;
      if (domain) {
        try {
          const response = await getChannelMemberCount(domain as string);
          this.memberCount = response.data || 0;
        } catch (error) {
          console.error('Failed to fetch member count:', error);
          this.memberCount = 0;
        }
      }
    }
  }

  private async loadSpaces() {
    if (!this.isInCommunity) {
      this.spaces = [];
      return;
    }

    const channelUid = this.$route.params.domain;
    if (!channelUid) return;

    try {
      this.loadingSpaces = true;
      const response = await getSpacesByChannel(channelUid as string);
      this.spaces = response.data || [];
    } catch (error) {
      console.error('공간 목록 조회 실패:', error);
      this.spaces = [];
    } finally {
      this.loadingSpaces = false;
    }
  }

  /**
   * Check manager and admin permissions for menu visibility
   */
  private async checkPermissions() {
    // Reset permissions
    this.hasManagerPermission = false;
    this.isChannelCreator = false;
    this.currentChannelUid = '';

    // Only check permissions when in community
    if (!this.isInCommunity) {
      return;
    }

    // Only check if user is logged in
    if (!UserModule.isLogin) {
      return;
    }

    const domain = this.$route.params.domain;
    if (!domain) return;

    try {
      // Get channel information
      const channelResponse = await getChannelDomainDetail(domain as string);
      this.currentChannelUid = channelResponse.data.uid;

      // Get current user information
      const userResponse: any = await getUserInfo();
      const currentUserInfo: any = userResponse.data;

      // Check if user is the channel creator (for Admin Menu)
      this.isChannelCreator = channelResponse?.data.userUid === currentUserInfo.uid;

      // Check manager permissions (for Manager Menu)
      // User needs either SPACE_CREATE or MARKETPLACE_OFFLINE_REGISTER permission
      try {
        const spaceCreateResponse = await checkPermissionByUser(
          this.currentChannelUid,
          'SPACE_CREATE'
        );
        const marketplaceOfflineResponse = await checkPermissionByUser(
          this.currentChannelUid,
          'MARKETPLACE_OFFLINE_REGISTER'
        );

        this.hasManagerPermission = 
          spaceCreateResponse.data.hasPermission || 
          marketplaceOfflineResponse.data.hasPermission;
      } catch (error) {
        console.error('권한 체크 실패:', error);
        this.hasManagerPermission = false;
      }
    } catch (error) {
      console.error('채널 정보 조회 실패:', error);
    }
  }

  private handleLogin() {
    this.$router.push({ name: 'Login' });
  }

  private handleLoginModal() {
    this.loginModalVisible = true;
    this.loginActiveStep = 'Login';
  }

  private handleCommunity() {
    this.$router.push({ name: 'CommunityMain' });
  }

  private handleHome() {
    this.$router.push({ name: 'Home' });
  }

  private activeMenu() {
    const route: any = this.$route;
    if (route.redirect) return route.redirect;
    return route.name;
  }

  private async handleLogout() {
    UserModule.LogOut();
    this.$message.info('로그아웃 되셨습니다');
    this.$router.push({ name: 'Home' });
  }

  private async handleUserInfo() {
    this.showProfile = false;
    this.userModalVisible = true;
  }

  private resolvePath(route: any, childRoute: any) {
    if (childRoute.path) {
      if (childRoute.path.indexOf('/') === 0) {
        return childRoute.path;
      }
      return path.resolve(route.path, childRoute.path);
    }
    return route.path;
  }

  get isLogin() {
    return UserModule.isLogin;
  }

  get isInCommunity() {
    // Check if current route starts with /community/
    return this.$route.path.startsWith('/community/');
  }

  private moveHome() {
    this.$router.push({ name: 'Home' });
  }

  private isActiveRoute(routeName: string): boolean {
    const currentRoute = this.$route.name;

    // Check exact match
    if (currentRoute === routeName) {
      return true;
    }

    // Check for related routes
    const routeMap: { [key: string]: string[] } = {
      'Lession': ['Lession', 'LessionDetail', 'Video', 'VideoDetail'],
      'Calendar': ['Calendar'],
      'Marketplace': ['Marketplace', 'MarketplaceDetail'],
      'Member': ['Member', 'MemberDetail'],
      'Home': ['Home'],
    };

    const relatedRoutes = routeMap[routeName] || [routeName];
    return relatedRoutes.includes(currentRoute as string);
  }

  private toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
    if (this.mobileMenuOpen) {
      this.showMobileProfile = false;
    }
  }

  private handleMobileUser() {
    if (!this.isLogin) {
      this.handleLoginModal();
    } else {
      this.showMobileProfile = !this.showMobileProfile;
      if (this.showMobileProfile) {
        this.mobileMenuOpen = false;
      }
    }
  }

  private handleCreateSpace() {
    this.createSpaceModalVisible = true;
    this.toggleMobileMenu();
  }

  private async handleSpaceCreated(space: Space) {
    // Refresh space list after creation
    await this.loadSpaces();
  }

  private getSpaceDotClass(spaceType: string): string {
    return spaceType === 'BOARD' ? 'red-dot' : 'orange-dot';
  }

  private navigateToSpace(spaceUid: string, spaceName: string, spaceType: string) {
    const space = this.spaces.find(s => s.uid === spaceUid);
    if (!space) return;

    // Check access permission
    if (!space.isPublic && !space.isMember && !space.isAdmin) {
      this.$message.error('이 공간에 접근할 권한이 없습니다. 초대를 받아야 참여할 수 있습니다.');
      return;
    }

    // Navigate based on space type
    const routeName = spaceType === 'CHAT' ? 'CommunityChat' : 'CommunitySpace';
    
    this.$router.push({
      name: routeName,
      params: {
        domain: this.$route.params.domain || 'default',
        spaceId: spaceUid,
      },
      query: {
        spaceName: spaceName,
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
    this.toggleMobileMenu();
  }

  private navigateToMarketplace(storeName: string) {
    this.$router.push({
      name: 'Marketplace',
      params: {
        domain: this.$route.params.domain || 'default',
      },
      query: {
        store: storeName,
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
    this.toggleMobileMenu();
  }

  private navigateToPage(pageName: string) {
    this.$router.push({
      name: pageName,
      params: {
        domain: this.$route.params.domain || 'default',
      },
    }).catch(err => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('Navigation error:', err);
      }
    });
    this.toggleMobileMenu();
  }

  private closeMobileProfile() {
    this.showMobileProfile = false;
  }

  private handleUserInfoMobile() {
    this.showMobileProfile = false;
    this.userModalVisible = true;
  }

  private async handleLogoutMobile() {
    this.showMobileProfile = false;
    UserModule.LogOut();
    this.$message.info('로그아웃 되셨습니다');
    this.$router.push({ name: 'Home' });
  }
}
</script>

<style scoped lang="scss">
.header-container { display: flex; flex-wrap: wrap; align-items: center; padding: 20px 0; z-index: 1000;}
.header-left {flex: 0 1 300px;}

.home-header.community-header {padding: 0 40px;}
.home-header.community-header .aside-btn {margin: 0;}
.community-header .header-container {padding: 0;min-height: 120px;}
.community-header .header-left {}
.community-header .header-logo {}

@media screen and (max-width: 1440px) {
  .home-header.community-header {padding: 0 20px;}
  .header-container {padding: 20px;}
  .header-left {flex: 0 1 230px;}
}

@media screen and (max-width: 1200px) {
  .header-left {flex: 0 1 200px;}
  .home-header.community-header {padding: 0 20px;}
}
@media screen and (max-width: 1200px) {
  .home-header.community-header {padding: 0}
}

@media screen and (max-width: 768px) {
  .header-container {padding: 15px 0;}
  .header-left {flex: 0 1 calc(100% - 70px);}
  .community-header .header-container {padding: 0 20px;min-height: 100px;}
}

@media screen and (max-width: 500px) {
  .community-header .header-container {padding: 0;min-height: 80px;}
}

.mobile-menu-btn {display: none; align-items: center; justify-content: center; width: 40px; height: 40px; background: transparent; border: none; cursor: pointer; padding: 0; flex-shrink: 0;
  &:hover { opacity: 0.7;}
  svg { width: 24px; height: 24px;}
}

.mobile-user-icon { display: none;cursor: pointer; flex-shrink: 0;
  &:hover { opacity: 0.7;}
  svg { width: 30px; height: 30px}
}

.mobile-menu-overlay { display: none; position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); z-index: 9998; animation: fadeIn 0.3s ease-in-out forwards;}

@keyframes fadeIn { 
  from{ opacity: 0} 
  to{ opacity: 0.5}
}
@keyframes fadeOut{ 
  from{ opacity: 0.5} 
  to{ opacity: 0}
}

.mobile-close-btn { display: none; align-items: center; justify-content: center; width: 40px; height: 40px; background: transparent; border: none; cursor: pointer; padding: 0; position: absolute; top: 16px; right: 16px;

  &:hover {
    opacity: 0.7;
  }

  svg {
    width: 24px;
    height: 24px;
  }
}

.header-logo {
  a {
    text-decoration: none;
    display: flex;
    align-items: center;
  }

  .logo-text {
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 32px;
    font-weight: 800;
    line-height: 100%;
  }

  .logo-primary {
    color: #073DFF;
  }

  .logo-secondary {
    margin-left: 8px;
    color: #222;
  }

  .member-badge {
    display: flex;
    padding: 6px 10px;
    justify-content: center;
    align-items: center;
    border-radius: 30px;
    background: #EFEFEF;

    span {
      color: #888;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 14px;
      font-weight: 700;
      line-height: 100%;
    }
  }
}

.header-nav {
  flex: 0 1 calc(100% - 300px - 310px);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30px;
}

@media screen and (max-width: 1440px) {
  .header-nav {flex: 0 1 calc(100% - 230px - 180px)}
}

@media screen and (max-width: 1200px) {
  .header-nav {flex: 0 1 calc(100% - 200px - 160px)}
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 16px;
  height: 32px;
  border-radius: 32px;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    opacity: 0.7;
  }

  span {
    color: #444;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    line-height: 100%;
  }

  &.active {
    svg path {
      fill: #073DFF;
    }

    span {
      color: #073DFF;
    }
  }
}

@media screen and (max-width: 1440px) {
  .nav-item {padding: 0 10px}
}

.header-content{ flex: 0 1 310px;}

@media screen and (max-width: 1440px) {
  .header-content{ flex: 0 1 160px;}
}

.aside-btn {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-section{flex: 0 1 100% ;display: flex; align-items: center; gap: 15px;}

@media screen and (max-width: 1440px) {
  .user-section{flex-direction: column-reverse;gap: 0px;}
}
@media screen and (max-width: 1024px) {
  .user-section{display: flex; align-items: center;}
}

.notification-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.7;
  }

  svg {
    width: 24px;
    height: 24px;
  }
}

.points-display {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 20px;
  background: #F0F4FF;
  width: 100%;
  max-width: 150px;
  text-align: center;

  .points-label,
  .points-value {
    color: #073DFF;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 700;
    line-height: 100%;
  }
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.user-avatar{ 
  width: 36px; 
  height: 36px; 
  flex-shrink: 0;
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

@media screen and (max-width:1024px) {
  .user-avatar{ width: 30px; height: 30px;}
}

.user-name-text {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
  white-space: nowrap;
}

@media screen and (max-width:1440px) {
  .user-name-text {font-size: 18px;}
}

.login-button {
  color: #444;
  text-align: right;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
  transition: color 0.3s ease;

  &:hover {
    color: #073DFF;
  }
}

@media (max-width: 1200px) {
  .header-nav {
    gap: 20px;
  }

  .nav-item {
    padding: 0 12px;

    span {
      font-size: 18px;
    }

    svg {
      width: 22px;
      height: 22px;
    }
  }
}

@media screen and (max-width: 1024px) {
  .header-nav { position: static; transform: none; gap: 16px;}

  .nav-item { padding: 0 10px; gap: 4px; 
    span{ font-size: 16px;} 
    svg{ width: 20px; height: 20px;}
  }

  .header-logo { gap: 12px; 
    .logo-text{ font-size: 28px;} 
    .member-badge{ padding: 4px 8px; 
    span{ font-size: 12px;}}}

  .login-button {
    font-size: 18px;
  }

  .aside-btn {
    gap: 16px;
  }
  .user-name-text {
    font-size: 18px;
  }

  .points-display {

    .points-label,
    .points-value {
      font-size: 13px;
    }
  }
}

// Hide community sidebar items in header on PC (desktop)
@media (min-width: 769px) {
  .header-nav {
    .nav-section-title,
    .nav-subitem,
    .nav-divider {
      display: none !important;
    }
  }
}

@media (max-width: 768px) {
  .mobile-menu-btn { display: flex;}
  .header-content{ display: none;}
  .mobile-user-icon { display: flex;}
  .mobile-menu-overlay{ display: block;}

  .header-nav {
    display: none;

    &.mobile-open {
      display: flex;
      justify-content: start;
      flex-direction: column;
      position: fixed;
      top: 0;
      left: 0;
      bottom: 0;
      width: 360px;
      max-width: 85vw;
      background: #FFF;
      padding: 70px 50px;
      gap: 25px;
      box-shadow: 4px 0 12px rgba(0, 0, 0, 0.1);
      z-index: 9999;
      overflow-y: auto;
      animation: slideInFromLeft 0.3s ease-in-out forwards;
    }
  }

  @keyframes slideInFromLeft {
    from { transform: translateX(-100%); opacity: 0;}

    to { transform: translateX(0); opacity: 1;}
  }

  @keyframes slideOutToLeft {
    from { transform: translateX(0); opacity: 1;}

    to { transform: translateX(-100%); opacity: 0;}
  }

  .mobile-close-btn { display: flex;}

  .nav-item {
    width: 100%;
    justify-content: flex-start;
    padding: 0;
    gap: 16px;
    height: auto;
    border-radius: 0;
    background: transparent;

    svg {
      width: 22px;
      height: 22px;
      flex-shrink: 0;
    }

    span {
      font-size: 20px;
      font-weight: 800;
      color: #444;
    }

    &.active {
      svg path {
        stroke: #444;
        fill: #444;
      }

      span {
        color: #444;
      }
    }

    &.nav-section-title {
      font-weight: 700;
      color: #073DFF;
      margin-top: 12px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: space-between;

      span {
        color: #073DFF;
        font-size: 18px;
        font-weight: 700;
      }

      svg {
        width: 20px;
        height: 20px;
      }
    }

    &.nav-subitem {
      padding-left: 0;
      margin-top: 0;
      cursor: pointer;

      span {
        font-size: 16px;
        font-weight: 400;
        color: #6B7280;
      }

      .space-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        flex-shrink: 0;
        margin-right: 4px;

        &.red-dot {
          background-color: #FF5858;
        }

        &.orange-dot {
          background-color: #FFAD3A;
        }
      }

      .admin-badge-inline {
        display: inline-block;
        margin-left: 6px;
        padding: 2px 8px;
        border-radius: 8px;
        background: #E3EBFF;
        color: #073DFF;
        font-size: 12px;
        font-weight: 600;
        line-height: 100%;
      }

      &.empty-state {
        cursor: default;
        opacity: 0.6;

        &:hover span {
          color: #6B7280;
        }
      }

      &:hover span {
        color: #073DFF;
      }
    }
  }

  .nav-divider {
    width: 100%;
    height: 1px;
    background: #EBEBEB;
    // margin: 16px 0;
  }

  .header-content {
    display: none;

    &.mobile-open {
      display: flex;
      position: fixed;
      top: 54px;
      right: 0;
      background: #FFF;
      padding: 20px;
      border-left: 2px solid #EBEBEB;
      border-bottom: 2px solid #EBEBEB;
      box-shadow: -4px 4px 12px rgba(0, 0, 0, 0.1);
      z-index: 10000;
    }
  }

  .header-logo {
    display: flex;
    flex-wrap: wrap;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 0px;

    .logo-text {
      font-size: 20px;
    }

    .member-badge {
      width: 100%;
      max-width: 250px;

      span {
        font-size: 11px;
      }
    }
  }

  .login-button {
    font-size: 16px;
  }

  .user-section {
    gap: 12px;
  }

  .user-name-text {
    font-size: 16px;
  }

  .user-avatar {
    width: 32px;
    height: 32px;

    svg {
      width: 32px;
      height: 32px;
    }
  }

  .notification-btn {
    width: 36px;
    height: 36px;

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .points-display {
    padding: 5px 8px;

    .points-label,
    .points-value {
      font-size: 12px;
    }
  }
}
@media screen and (max-width: 1024px) {
  .notification-btn{ width: 30px; height: 30px; }
}

@media (max-width: 600px) {
  .header-logo {
    gap: 4px;

    .logo-text {
      font-size: 20px;
    }

    .logo-primary,
    .logo-secondary {
      display: inline;
    }

    .member-badge {
      display: none;
    }
  }

  .header-nav {
    &.mobile-open {
      width: 100%;
      max-width: 100vw;
    }
  }

  .nav-item {
    span {
      font-size: 20px;
    }
  }

  .header-content {
    &.mobile-open {
      top: 56px;
    }
  }

  .login-button {
    font-size: 14px;
  }

  .user-section {
    gap: 10px;
  }

  .user-name-text {
    display: none;
  }

  .points-display {
    padding: 0;

    .points-label {
      display: none;
    }

    .points-value {
      font-size: 11px;
    }
  }

  .notification-btn {
    width: 32px;
    height: 32px;

    svg {
      width: 18px;
      height: 18px;
    }
  }

  .user-avatar {
    width: 28px;
    height: 28px;

    svg {
      width: 28px;
      height: 28px;
    }
  }
}

.empty .el-button {
  margin-left: 10px;
}

/* Mobile User Profile Menu Styles */
.mobile-user-menu {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10001;
  // animation: fadeIn 0.3s ease-in-out forwards;
}

.mobile-user-menu-content {
  position: absolute;
  top: 0;
  right: 0;
  width: 360px;
  max-width: 85vw;
  height: 100%;
  background: #FFF;
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.1);
  animation: slideInFromRight 0.3s ease-in-out forwards;
  display: flex;
  flex-direction: column;
}

@keyframes slideInFromRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }

  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.mobile-user-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-bottom: 2px solid #EBEBEB;
  position: relative;
}

.mobile-user-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.mobile-user-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
}

.mobile-user-points {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 100%;
  white-space: nowrap;
}

.mobile-menu-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  flex-shrink: 0;

  &:hover {
    opacity: 0.7;
  }

  svg {
    width: 20px;
    height: 20px;
  }
}

.mobile-user-actions {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 20px;
}

.mobile-action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 12px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
  border-radius: 8px;

  &:hover {
    background: #F8F9FB;
  }

  svg {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  span {
    color: #444;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 600;
    line-height: 100%;
    text-align: left;
  }
}

@media (max-width: 768px) {
  .mobile-user-menu {
    display: block;
  }
}

/* Fix popover z-index to appear above header */
::v-deep .el-popover {
  z-index: 10000 !important;
}

::v-deep .el-popper.alarm {
  z-index: 10000 !important;
}
</style>

