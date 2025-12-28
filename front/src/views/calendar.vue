<template>
  <div class="calendar-page">
    <!-- HomeHeader is auto-inserted by layout -->
    
    <!-- Left Sidebar -->
    <CommunitySidebar :selectedSpaceId="'calendar'" @open-write-modal="handleWriteModal" />

    <!-- Main Content Area -->
    <div class="calendar-main">
      <!-- Empty State -->
      <div v-if="eventList.length === 0 && !loading" class="empty-state">
        <div class="empty-box">
          <h2 class="empty-title">예정된 일정이 없습니다.</h2>
          <p class="empty-subtitle">예정된 일정은 여기에 표시됩니다.</p>
        </div>
      </div>

      <!-- Events List -->
      <div v-else class="events-container">
        <div
          v-for="event in eventList"
          :key="event.idx"
          class="event-card-wrapper"
        >
          <!-- Event Header -->
          <div class="event-header">
            <div class="event-date-info">
              <div class="event-date">
                <svg width="25" height="25" viewBox="0 0 25 25" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M8.27778 7.22222V3M16.7222 7.22222V3M7.22222 11.4444H17.7778M5.11111 22H19.8889C20.4488 22 20.9858 21.7776 21.3817 21.3817C21.7776 20.9858 22 20.4488 22 19.8889V7.22222C22 6.66232 21.7776 6.12535 21.3817 5.72944C20.9858 5.33353 20.4488 5.11111 19.8889 5.11111H5.11111C4.55121 5.11111 4.01424 5.33353 3.61833 5.72944C3.22242 6.12535 3 6.66232 3 7.22222V19.8889C3 20.4488 3.22242 20.9858 3.61833 21.3817C4.01424 21.7776 4.55121 22 5.11111 22Z" stroke="black" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span class="date-text">{{ event.start | formatEventDate }}</span>
              </div>
              
              <div class="event-meta">
                <div class="meta-item">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 8V12L15 15M21 12C21 13.1819 20.7672 14.3522 20.3149 15.4442C19.8626 16.5361 19.1997 17.5282 18.364 18.364C17.5282 19.1997 16.5361 19.8626 15.4442 20.3149C14.3522 20.7672 13.1819 21 12 21C10.8181 21 9.64778 20.7672 8.55585 20.3149C7.46392 19.8626 6.47177 19.1997 5.63604 18.364C4.80031 17.5282 4.13738 16.5361 3.68508 15.4442C3.23279 14.3522 3 13.1819 3 12C3 9.61305 3.94821 7.32387 5.63604 5.63604C7.32387 3.94821 9.61305 3 12 3C14.3869 3 16.6761 3.94821 18.364 5.63604C20.0518 7.32387 21 9.61305 21 12Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ event.start | formatEventTime }} ~ {{ event.end | formatEventTime }}</span>
                </div>

                <div v-if="event.location" class="meta-item">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17.657 16.657L13.414 20.9C13.2284 21.0857 13.0081 21.2331 12.7656 21.3336C12.523 21.4342 12.2631 21.4859 12.0005 21.4859C11.738 21.4859 11.478 21.4342 11.2354 21.3336C10.9929 21.2331 10.7726 21.0857 10.587 20.9L6.343 16.657C5.22422 15.5382 4.46234 14.1127 4.15369 12.5609C3.84504 11.009 4.00349 9.40051 4.60901 7.93873C5.21452 6.47694 6.2399 5.22754 7.55548 4.3485C8.87107 3.46947 10.4178 3.00029 12 3.00029C13.5822 3.00029 15.1289 3.46947 16.4445 4.3485C17.7601 5.22754 18.7855 6.47694 19.391 7.93873C19.9965 9.40051 20.155 11.009 19.8463 12.5609C19.5377 14.1127 18.7758 15.5382 17.657 16.657Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M15 11C15 11.7956 14.6839 12.5587 14.1213 13.1213C13.5587 13.6839 12.7956 14 12 14C11.2044 14 10.4413 13.6839 9.87868 13.1213C9.31607 12.5587 9 11.7956 9 11C9 10.2044 9.31607 9.44129 9.87868 8.87868C10.4413 8.31607 11.2044 8 12 8C12.7956 8 13.5587 8.31607 14.1213 8.87868C14.6839 9.44129 15 10.2044 15 11Z" stroke="#6B7280" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ event.location || '산청의료협동조합 본관 2층' }}</span>
                </div>

                <!-- 일정 타입 (무료/유료/획득) 및 포인트 정보 -->
                <div class="meta-item event-type-indicator">
                  <span v-if="event.type === 'free'" class="type-badge type-free">무료</span>
                  <span v-else-if="event.type === 'paid'" class="type-badge type-paid">
                    유료 {{ event.points ? `${Number(event.points).toLocaleString()}P` : '' }}
                  </span>
                  <span v-else-if="event.type === 'earn'" class="type-badge type-earn">
                    획득 {{ event.points ? `+${Number(event.points).toLocaleString()}P` : '' }}
                  </span>
                  <span v-else class="type-badge type-free">-</span>
                </div>
              </div>
            </div>

            <div class="action-buttons">
              <!-- 본인 일정이면 참여자 관리 버튼 표시 -->
              <button 
                v-if="isOwnEvent(event)"
                class="manage-participants-button"
                @click="handleViewParticipants(event)"
              >
                참여자 관리
              </button>
              
              <!-- 본인 일정이 아니면 참여하기 버튼 표시 -->
              <button 
                v-else
                class="join-button"
                :disabled="event.isParticipating"
                @click="handleJoinEvent(event)"
              >
                {{ event.isParticipating ? '참여중' : '참여하기' }}
              </button>
            </div>
          </div>

          <!-- Event Content Card -->
          <div class="event-content">
            <div class="timeline-connector"></div>
            
            <div class="event-card">
              <!-- User Info -->
              <div class="user-info">
                <div class="user-info-wr">
                <div class="user-avatar">
                  <img v-if="event.iconFileUid" :src="`${apiUrl}/attached-file/${event.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
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
                <span class="user-name">{{ event.writer || '작성자 미상' }}</span>
              </div>

                <!-- 본인 작성 일정인 경우 ... 버튼 표시 -->
                <el-dropdown v-if="isOwnEvent(event)" trigger="click" @command="handleEventAction">
                  <span class="el-dropdown-link">
                    <i class="el-icon-more"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="{action: 'edit', event: event}">수정</el-dropdown-item>
                    <el-dropdown-item :command="{action: 'delete', event: event}">삭제</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
            </div>

              <!-- Event Details -->
              <div class="event-details">
                <h3 class="event-title">{{ event.title }}</h3>
                <p class="event-description">{{ event.description || event.extendedProps?.description }}</p>
                
                <!-- Stats -->
                <div class="event-stats">
                  <div class="stat-item" :class="{ liked: isLiked(event.idx) }" @click.stop="toggleLike(event.idx)">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M7.49301 21.5C7.06801 21.5 6.67301 21.264 6.51801 20.868C6.17475 19.9943 5.99902 19.0637 6.00001 18.125C6.00001 16.375 6.59901 14.767 7.60201 13.491C7.75301 13.299 7.97501 13.182 8.20201 13.094C8.67501 12.911 9.09201 12.58 9.41401 12.17C10.1903 11.1796 11.1647 10.3622 12.275 9.77C12.998 9.386 13.625 8.814 13.928 8.055C14.1408 7.52325 14.2501 6.95575 14.25 6.383V5.75C14.25 5.55109 14.329 5.36032 14.4697 5.21967C14.6103 5.07902 14.8011 5 15 5C15.5967 5 16.169 5.23705 16.591 5.65901C17.013 6.08097 17.25 6.65326 17.25 7.25C17.25 8.402 16.99 9.493 16.527 10.468C16.261 11.026 16.634 11.75 17.252 11.75H20.378C21.404 11.75 22.323 12.444 22.432 13.465C22.477 13.887 22.5 14.315 22.5 14.75C22.5041 17.4863 21.5691 20.1412 19.851 22.271C19.463 22.753 18.864 23 18.246 23H14.23C13.747 23 13.266 22.922 12.807 22.77L9.69301 21.73C9.23411 21.5774 8.75362 21.4997 8.27001 21.5H7.49301ZM2.33101 13.727C1.77993 15.1277 1.498 16.6198 1.50001 18.125C1.49875 19.3133 1.67399 20.4952 2.02001 21.632C2.28001 22.482 3.10501 23 3.99401 23H4.90001C5.34501 23 5.62001 22.502 5.42301 22.102C4.81315 20.8651 4.49695 19.5041 4.49901 18.125C4.49901 16.417 4.97501 14.82 5.80101 13.459C6.04601 13.056 5.77301 12.5 5.30101 12.5H4.25001C3.41801 12.5 2.63701 12.953 2.33101 13.727Z" :fill="isLiked(event.idx) ? '#073DFF' : '#CECECE'"/>
                    </svg>
                    <span class="stat-count">{{ getLikeCount(event.idx) }}</span>
                  </div>

                  <div class="stat-divider"></div>

                  <div class="stat-item" :class="{ active: shouldShowComments(event.idx) }" @click.stop="toggleComments(event.idx)">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path fill-rule="evenodd" clip-rule="evenodd" d="M4.848 2.771C7.21613 2.4234 9.60649 2.24927 12 2.25C14.43 2.25 16.817 2.428 19.152 2.77C21.13 3.062 22.5 4.794 22.5 6.74V12.76C22.5 14.706 21.13 16.438 19.152 16.73C17.9983 16.8991 16.8389 17.0268 15.676 17.113C15.6168 17.1169 15.5593 17.1342 15.5079 17.1637C15.4564 17.1931 15.4123 17.234 15.379 17.283L12.624 21.416C12.5555 21.5187 12.4627 21.6029 12.3539 21.6612C12.245 21.7194 12.1235 21.7499 12 21.7499C11.8765 21.7499 11.755 21.7194 11.6461 21.6612C11.5373 21.6029 11.4445 21.5187 11.376 21.416L8.621 17.283C8.58768 17.234 8.54361 17.1931 8.49214 17.1637C8.44068 17.1342 8.38317 17.1169 8.324 17.113C7.16113 17.0265 6.00172 16.8984 4.848 16.729C2.87 16.439 1.5 14.705 1.5 12.759V6.741C1.5 4.795 2.87 3.061 4.848 2.771ZM6.75 8.25C6.75 8.05109 6.82902 7.86032 6.96967 7.71967C7.11032 7.57902 7.30109 7.5 7.5 7.5H16.5C16.6989 7.5 16.8897 7.57902 17.0303 7.71967C17.171 7.86032 17.25 8.05109 17.25 8.25C17.25 8.44891 17.171 8.63968 17.0303 8.78033C16.8897 8.92098 16.6989 9 16.5 9H7.5C7.30109 9 7.11032 8.92098 6.96967 8.78033C6.82902 8.63968 6.75 8.44891 6.75 8.25ZM7.5 10.5C7.30109 10.5 7.11032 10.579 6.96967 10.7197C6.82902 10.8603 6.75 11.0511 6.75 11.25C6.75 11.4489 6.82902 11.6397 6.96967 11.7803C7.11032 11.921 7.30109 12 7.5 12H12C12.1989 12 12.3897 11.921 12.5303 11.7803C12.671 11.6397 12.75 11.4489 12.75 11.25C12.75 11.0511 12.671 10.8603 12.5303 10.7197C12.3897 10.579 12.1989 10.5 12 10.5H7.5Z" :fill="shouldShowComments(event.idx) ? '#073DFF' : '#CECECE'"/>
                    </svg>
                    <span class="stat-count">{{ event.commentCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Comments Section -->
          <div class="comments-section" v-if="shouldShowComments(event.idx)">
            <div class="comment-list" v-if="getCommentsList(event.idx).length > 0">
              <div 
                v-for="comment in getCommentsList(event.idx)" 
                :key="comment.idx"
                class="comment-item"
              >
                <div class="comment-header">
                  <div class="user-info">
                    <div class="user-avatar">
                      <img v-if="comment.iconFileUid" :src="`${apiUrl}/attached-file/${comment.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                      <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        <mask :id="'mask_comment_' + comment.idx" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                          <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                        </mask>
                        <g :mask="'url(#mask_comment_' + comment.idx + ')'">
                          <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                          <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                        </g>
                      </svg>
                    </div>
                    <span class="user-name">{{ comment.userName }}</span>
                  </div>
                  <span class="comment-date">{{ comment.createdAt | formatDate }}</span>
                </div>
                <div class="comment-text">
                  {{ comment.content }}
                </div>
              </div>
            </div>
            <div v-else class="empty-comments">
              <p>첫 번째 댓글을 작성해보세요!</p>
            </div>

            <div class="comment-input-area">
              <input 
                type="text" 
                class="comment-input" 
                placeholder="댓글을 입력하세요." 
                v-model="commentInputs[event.idx]"
                @keyup.enter="handleCommentSubmit(event.idx)"
              />
              <button class="comment-submit-btn" @click="handleCommentSubmit(event.idx)">댓글입력</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Floating Write Button (Mobile Only, Admin Only) -->
      <button 
        v-if="isChannelAdmin"
        class="write-event-btn-fixed" 
        @click="handleWriteModal"
      >
        <svg class="btn-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 5V19M5 12H19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>

    <!-- Create Event Modal -->
    <el-dialog
      :visible.sync="createEventModalVisible"
      width="704px"
      custom-class="create-event-modal"
      :show-close="false"
    >
      <div class="modal-content-wrapper">
        <button class="modal-close-btn" @click="closeCreateModal">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">{{ isEditMode ? '일정 수정하기' : '어떤 일정인가요?' }}</h2>

        <div class="form-container">
          <!-- Event Title -->
          <div class="form-group">
            <label class="form-label">일정 제목</label>
            <input
              v-model="eventForm.title"
              type="text"
              class="form-input"
              placeholder="일정 제목에 대해서 작성해주세요."
            />
          </div>

          <!-- Event Description -->
          <div class="form-group">
            <label class="form-label">일정소개</label>
            <textarea
              v-model="eventForm.description"
              class="form-textarea"
              placeholder="일정에 대해서 간략하게 소개해 주세요."
              rows="4"
            ></textarea>
          </div>

          <!-- Date and Time -->
          <div class="form-group">
            <label class="form-label">날짜 및 시간</label>
            <div class="datetime-container">
              <el-date-picker
                v-model="eventForm.date"
                type="date"
                placeholder="날짜를 선택해주세요"
                class="date-picker"
                format="yyyy. MM. dd"
                value-format="yyyy-MM-dd"
                :picker-options="datePickerOptions"
                popper-class="calendar-date-picker-popper"
                :append-to-body="true"
              />
              
              <div class="time-range">
                <el-time-select
                  v-model="eventForm.startTime"
                  placeholder="시작 시간"
                  :picker-options="{
                    start: '00:00',
                    step: '00:15',
                    end: '23:45'
                  }"
                  class="time-picker"
                  popper-class="calendar-time-picker-popper"
                  :append-to-body="true"
                />
                
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 10C4 10 6.58778 6.62471 10 10C13.4122 13.3753 16 10 16 10" stroke="#CECECE" stroke-width="1.5" stroke-linecap="round"/>
                </svg>
                
                <el-time-select
                  v-model="eventForm.endTime"
                  placeholder="종료 시간"
                  :picker-options="{
                    start: eventForm.startTime || '00:00',
                    step: '00:15',
                    end: '23:45',
                    minTime: eventForm.startTime
                  }"
                  class="time-picker"
                  popper-class="calendar-time-picker-popper"
                  :append-to-body="true"
                />
              </div>
            </div>
          </div>

          <!-- Location -->
          <div class="form-group">
            <label class="form-label">진행 장소</label>
            <input
              v-model="eventForm.location"
              type="text"
              class="form-input"
              placeholder="어디에서 진행되는 거래인가요?"
            />
          </div>

          <!-- Event Type -->
          <div class="form-group">
            <label class="form-label">일정 참여 방법</label>
            <div v-if="isEditMode" class="event-type-readonly">
              <div class="readonly-badge">
                <span v-if="eventForm.type === 'free'" class="type-badge type-free">무료</span>
                <span v-else-if="eventForm.type === 'paid'" class="type-badge type-paid">
                  유료 {{ eventForm.points ? `${Number(eventForm.points).toLocaleString()}P` : '' }}
                </span>
                <span v-else-if="eventForm.type === 'earn'" class="type-badge type-earn">
                  획득 {{ eventForm.points ? `+${Number(eventForm.points).toLocaleString()}P` : '' }}
                </span>
              </div>
              <p class="readonly-notice">※ 일정 참여 방법 및 포인트는 수정할 수 없습니다.</p>
            </div>
            <template v-else>
              <div class="event-type-buttons">
                <button
                  v-for="type in eventTypes"
                  :key="type.value"
                  class="event-type-btn"
                  :class="{ active: eventForm.type === type.value }"
                  @click="eventForm.type = type.value"
                >
                  {{ type.label }}
                </button>
              </div>
              <input
                v-if="eventForm.type !== 'free'"
                v-model="eventForm.points"
                type="text"
                class="form-input"
                placeholder="R포인트를 작성해 주세요."
              />
            </template>
          </div>

          <!-- Submit Button -->
          <button class="submit-button" @click="handleCreateEvent">
            {{ isEditMode ? '일정 수정하기' : '일정 만들기' }}
          </button>
        </div>
      </div>
    </el-dialog>

    <!-- Join Event Modal -->
    <el-dialog
      :visible.sync="joinEventModalVisible"
      width="498px"
      custom-class="join-event-modal"
      :show-close="false"
    >
      <div class="modal-content-wrapper">
        <button class="modal-close-btn" @click="joinEventModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">{{ selectedEvent.title }}</h2>
        <div class="modal-subtitle">R: {{ selectedEvent.points || 5000 }}</div>

        <div class="points-breakdown">
          <div class="breakdown-header">
            <h3>{{ currentUserName }} 님의 R 포인트</h3>
          </div>
          
          <div class="breakdown-items">
            <div class="breakdown-item">
              <span>사용 가능한 R 포인트</span>
              <span class="amount">{{ currentUserBalance.toLocaleString() }}</span>
            </div>
            <div class="breakdown-item" v-if="selectedEvent.type === 'paid'">
              <span>차감 R 포인트</span>
              <span class="amount">-{{ (selectedEvent.points || 0).toLocaleString() }}</span>
            </div>
            <div class="breakdown-item" v-if="selectedEvent.type === 'earn'">
              <span>획득 예정 R 포인트</span>
              <span class="amount">+{{ (selectedEvent.points || 0).toLocaleString() }}</span>
            </div>
            <div class="breakdown-item total">
              <span>잔액 R 포인트</span>
              <span class="amount highlight">{{ pointsAfterJoin.toLocaleString() }}</span>
            </div>
          </div>
        </div>

        <button class="submit-button" @click="handleConfirmJoin">
          R 포인트 지급하기
        </button>
      </div>
    </el-dialog>

    <!-- Participants Management Modal -->
    <el-dialog
      :visible.sync="participantsModalVisible"
      width="600px"
      custom-class="participants-modal"
      :show-close="false"
    >
      <div class="modal-content-wrapper">
        <button class="modal-close-btn" @click="participantsModalVisible = false">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>

        <h2 class="modal-title">참여자 관리</h2>
        <div class="modal-subtitle">{{ selectedEvent.title }}</div>

        <div class="participants-list" v-loading="loadingParticipants">
          <div v-if="participantsList.length === 0" class="empty-participants">
            <p>참여자가 없습니다.</p>
          </div>
          <div v-else class="participant-items">
            <div 
              v-for="participant in participantsList"
              :key="participant.idx"
              class="participant-item"
            >
              <div class="participant-info">
                <div class="participant-avatar">
                  <svg width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask :id="'mask_participant_' + participant.idx" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g :mask="'url(#mask_participant_' + participant.idx + ')'">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <div class="participant-details">
                  <span class="participant-name">{{ participant.userName }}</span>
                  <span class="participant-date">{{ participant.createdAt | formatDate }}</span>
                </div>
              </div>
              
              <!-- 획득 타입 일정인 경우에만 포인트 지급 버튼 표시 -->
              <button 
                v-if="selectedEvent.type === 'earn' && !participant.pointGranted"
                class="grant-point-btn"
                @click="handleGrantPoint(participant)"
              >
                포인트 지급 ({{ selectedEvent.points }}P)
              </button>
              <span v-else-if="participant.pointGranted" class="point-granted-badge">
                지급완료
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import { 
  getCalendarList, 
  addCalendar, 
  joinCalendarEvent,
  toggleCalendarLike,
  getCalendarComments,
  createCalendarComment,
  getCalendarParticipants,
  grantPointToParticipant,
} from '@/api/manager/calendar';
import { getCurrentPoint } from '@/api/point';
import { getUserInfo } from '@/api/user';
import { ChannelModule } from '@/store/modules/channel';
import { UserModule } from '@/store/modules/user';
import request from '@/utils/request';
import moment from 'moment';

@Component({
  name: 'Calendar',
  components: {
    CommunitySidebar,
  },
  filters: {
    formatEventDate(date: string) {
      return moment(date).format('M/D (ddd)');
    },
    formatEventTime(date: string) {
      return moment(date).format('A h:mm');
    },
    formatDate(date: string) {
      return moment(date).format('YYYY. MM. DD.');
    },
  },
})
export default class extends Vue {
  private loading = false;
  private eventList: any[] = [];
  private createEventModalVisible = false;
  private joinEventModalVisible = false;
  private participantsModalVisible = false;
  private loadingParticipants = false;
  private participantsList: any[] = [];
  private selectedEvent: any = {};
  private joiningEvent = false;
  private currentUserPoint = 0;
  private currentUserInfo: any = null;

  // ✅ apiUrl을 computed getter로 변경하여 템플릿에서 접근 가능하도록 수정
  get apiUrl() {
    return (process as any).env.VUE_APP_COMMON_API || '/api';
  }

  // Comment state management
  private showCommentsForEvent: { [key: number]: boolean } = {};
  private commentInputs: { [key: number]: string } = {};
  private commentsData: { [key: number]: any[] } = {};

  private eventForm = {
    title: '',
    description: '',
    date: '',
    startTime: '',
    endTime: '',
    location: '',
    type: 'free',
    points: '',
  };

  private eventTypes = [
    { label: '무료', value: 'free' },
    { label: '유료', value: 'paid' },
    { label: '획득', value: 'earn' },
  ];

  // Date picker options
  private datePickerOptions = {
    disabledDate(time: Date) {
      // 과거 날짜 선택 불가 (오늘은 선택 가능)
      return time.getTime() < Date.now() - 86400000;
    },
    firstDayOfWeek: 7, // 일요일부터 시작 (1=월요일, 7=일요일)
  };

  // Computed property to expose UserModule to template
  get UserModule() {
    return UserModule;
  }

  // Safe computed property for current user name
  get currentUserName() {
    return UserModule.actualName || UserModule.userId || '사용자';
  }

  // 수정 모드 여부 확인
  get isEditMode(): boolean {
    return this.selectedEvent && this.selectedEvent.idx !== undefined && this.createEventModalVisible;
  }

  // 커뮤니티 관리자 여부 확인
  get isChannelAdmin(): boolean {
    const currentChannel = ChannelModule.selectedChannel;
    const currentUserUid = this.currentUserInfo?.uid;
    
    console.log('=== 관리자 체크 ===');
    console.log('currentChannel:', currentChannel);
    console.log('currentUserUid:', currentUserUid);
    console.log('channel.userUid:', currentChannel?.userUid);
    console.log('일치 여부:', currentChannel?.userUid === currentUserUid);
    console.log('==================');
    
    if (!currentChannel || !currentUserUid) {
      console.warn('⚠️ Channel 또는 UserUid가 없습니다');
      return false;
    }
    
    return currentChannel.userUid === currentUserUid;
  }

  async mounted() {
    await this.loadCurrentUserInfo();
    await this.loadCurrentPoint();
    this.fetchEventList();
  }

  private async loadCurrentUserInfo() {
    try {
      if (UserModule.isLogin) {
        const response = await getUserInfo();
        this.currentUserInfo = response.data;
      }
    } catch (error) {
      console.error('Failed to load current user info:', error);
    }
  }

  private async loadCurrentPoint() {
    try {
      const channelUid = ChannelModule.selectedChannel?.uid || 'default';
      const response = await getCurrentPoint(channelUid);
      this.currentUserPoint = response.data.currentPoint || 0;
    } catch (error) {
      console.error('Failed to load current point:', error);
      this.currentUserPoint = 0;
    }
  }

  private async fetchEventList() {
    this.loading = true;
    try {
      const res = await getCalendarList({
        channelUid: ChannelModule.selectedChannel.uid,
        page: 1,
      });
      
      // Transform API response to match frontend format
      const apiData = res.data.content || res.data || [];
      console.log('===== Calendar API Full Response =====');
      console.log('Full response:', res.data);
      console.log('Content array:', apiData);
      if (apiData.length > 0) {
        console.log('First item detail:', apiData[0]);
        console.log('writerName:', apiData[0].writerName);
        console.log('writerUid:', apiData[0].writerUid);
      }
      console.log('=====================================');
      
      this.eventList = apiData.map((item: any) => {
        // writerName과 writerUid가 모두 null일 경우 처리
        let writerDisplay = '작성자 미상';
        if (item.writerName) {
          writerDisplay = item.writerName;
        } else if (item.writerUid) {
          writerDisplay = item.writerUid;
        }
        
        const mappedItem = {
          idx: item.idx,
          title: item.title || '제목 없음',
          description: item.content || '',
          start: item.startDate,
          end: item.endDate,
          location: item.location || '',
          writer: writerDisplay,
          writerUid: item.writerUid || '',
          points: item.points || 0,
          likeCount: item.likeCount || 0,
          isLiked: item.isLiked || false,
          commentCount: item.commentCount || 0,
          type: item.eventType || 'free',
          isParticipating: item.isParticipating || false,
          participantCount: item.participantCount || 0,
        };
        console.log('Mapped event:', mappedItem);
        return mappedItem;
      });
    } catch (error: any) {
      console.error('Error fetching calendar list:', error);
      this.$message.error(error.response?.data?.message || '일정 목록을 불러오는데 실패했습니다.');
    } finally {
      this.loading = false;
    }
  }


  private handleWriteModal() {
    // 커뮤니티 관리자만 일정 작성 가능
    if (!this.isChannelAdmin) {
      console.error('❌ 관리자 권한이 없습니다');
      console.log('현재 사용자 정보:', this.currentUserInfo);
      console.log('채널 정보:', ChannelModule.selectedChannel);
      this.$message.warning('일정 작성은 커뮤니티 관리자만 가능합니다.');
      return;
    }
    
    console.log('✅ 관리자 권한 확인 완료');
    // 새 일정 작성을 위해 폼 초기화 및 선택된 이벤트 리셋
    this.selectedEvent = {};
    this.eventForm = {
      title: '',
      description: '',
      date: '',
      startTime: '',
      endTime: '',
      location: '',
      type: 'free',
      points: '',
    };
    this.createEventModalVisible = true;
  }

  private handleJoinEvent(event: any) {
    // Check if user is the event creator
    if (this.isOwnEvent(event)) {
      this.$message.info('본인이 작성한 일정입니다.');
      return;
    }
    
    // Check if already participating
    if (event.isParticipating) {
      this.$message.info('이미 참여 중인 일정입니다.');
      return;
    }
    
    this.selectedEvent = event;
    this.joinEventModalVisible = true;
  }

  // Check if the event is created by current user
  private isOwnEvent(event: any): boolean {
    const currentUserUid = this.currentUserInfo?.uid;
    return event.writerUid && currentUserUid && event.writerUid === currentUserUid;
  }

  // Dropdown 메뉴 액션 핸들러
  private handleEventAction(command: any) {
    const { action, event } = command;
    
    if (action === 'edit') {
      this.handleEditEvent(event);
    } else if (action === 'delete') {
      this.handleDeleteEvent(event);
    }
  }

  // 일정 수정 핸들러
  private handleEditEvent(event: any) {
    // 일정 데이터를 폼에 로드
    this.eventForm = {
      title: event.title,
      description: event.description || event.content || event.extendedProps?.description || '',
      date: moment(event.start || event.startDate).format('YYYY-MM-DD'),
      startTime: moment(event.start || event.startDate).format('HH:mm'),
      endTime: moment(event.end || event.endDate).format('HH:mm'),
      location: event.location || '',
      type: event.eventType || 'free',
      points: event.points ? String(event.points) : '',
    };
    
    // 수정할 일정 정보 저장
    this.selectedEvent = event;
    this.createEventModalVisible = true;
  }

  // 일정 삭제 핸들러
  private async handleDeleteEvent(event: any) {
    try {
      const confirmed = await this.$confirm(
        '정말 이 일정을 삭제하시겠습니까?',
        '일정 삭제',
        {
          confirmButtonText: '삭제',
          cancelButtonText: '취소',
          type: 'warning',
        }
      );

      if (confirmed) {
        // API 호출 (백엔드에 deleteCalendar 메서드가 필요합니다)
        await this.deleteCalendarEvent(event.idx);
        
        this.$message.success('일정이 삭제되었습니다.');
        
        // 목록 새로고침
        this.fetchEventList();
      }
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('Error deleting event:', error);
        this.$message.error(error.response?.data?.message || '일정 삭제에 실패했습니다.');
      }
    }
  }

  // 일정 삭제 API 호출
  private async deleteCalendarEvent(eventIdx: number) {
    const response = await request({
      url: `/calendar/${eventIdx}`,
      method: 'delete',
    });
    return response;
  }

  private async handleCreateEvent() {
    // Validate form
    if (!this.eventForm.title) {
      this.$message.warning('일정 제목을 입력해주세요.');
      return;
    }
    if (!this.eventForm.date) {
      this.$message.warning('날짜를 선택해주세요.');
      return;
    }
    if (!this.eventForm.startTime || !this.eventForm.endTime) {
      this.$message.warning('시간을 선택해주세요.');
      return;
    }

    try {
      // Combine date and time
      const dateStr = moment(this.eventForm.date).format('YYYY-MM-DD');
      const startDateTime = `${dateStr}T${this.eventForm.startTime}:00`;
      const endDateTime = `${dateStr}T${this.eventForm.endTime}:00`;

      const eventData = {
        title: this.eventForm.title,
        content: this.eventForm.description,
        startDate: startDateTime,
        endDate: endDateTime,
        location: this.eventForm.location,
        eventType: this.eventForm.type,
        points: this.eventForm.type !== 'free' ? parseInt(this.eventForm.points) || 0 : 0,
        channelUid: ChannelModule.selectedChannel.uid,
      };

      if (this.isEditMode) {
        // 수정 모드
        await request({
          url: `/calendar/${this.selectedEvent.idx}`,
          method: 'put',
          data: eventData,
        });
        this.$message.success('일정이 수정되었습니다!');
      } else {
        // 생성 모드
        await addCalendar(eventData);
        this.$message.success('일정이 생성되었습니다!');
      }
      
      this.closeCreateModal();
      
      // Refresh the list
      this.fetchEventList();
    } catch (error: any) {
      console.error('Error creating/updating event:', error);
      this.$message.error(error.response?.data?.message || (this.isEditMode ? '일정 수정에 실패했습니다.' : '일정 생성에 실패했습니다.'));
    }
  }

  // 모달 닫기 및 폼 초기화
  private closeCreateModal() {
    this.createEventModalVisible = false;
    this.selectedEvent = {};
    this.eventForm = {
      title: '',
      description: '',
      date: '',
      startTime: '',
      endTime: '',
      location: '',
      type: 'free',
      points: '',
    };
  }

  private async handleConfirmJoin() {
    if (this.joiningEvent) return;
    
    this.joiningEvent = true;
    try {
      const res = await joinCalendarEvent(this.selectedEvent.idx);
      const result = res.data;
      
      if (result.success) {
        this.$message.success(result.message);
        this.joinEventModalVisible = false;
        
        // Refresh event list and point balance
        await Promise.all([
          this.fetchEventList(),
          this.loadCurrentPoint()
        ]);
      } else {
        this.$message.error(result.message);
      }
    } catch (error: any) {
      console.error('Error joining event:', error);
      this.$message.error(error.response?.data?.message || '일정 참여에 실패했습니다.');
    } finally {
      this.joiningEvent = false;
    }
  }

  // Computed properties for join modal
  get currentUserBalance(): number {
    return this.currentUserPoint;
  }

  get pointsAfterJoin(): number {
    const eventPoints = this.selectedEvent?.points || 0;
    const eventType = this.selectedEvent?.type || 'free';
    
    if (eventType === 'paid') {
      return this.currentUserBalance - eventPoints;
    } else if (eventType === 'earn') {
      return this.currentUserBalance + eventPoints;
    }
    return this.currentUserBalance;
  }

  get joinButtonText(): string {
    const eventType = this.selectedEvent?.type || 'free';
    if (eventType === 'paid') {
      return 'R 포인트 지급하기';
    } else if (eventType === 'earn') {
      return '참여하기 (포인트 획득)';
    }
    return '참여하기';
  }

  // Like functionality
  private async toggleLike(eventIdx: number) {
    if (!eventIdx) {
      console.error('Event idx is undefined');
      this.$message.error('일정 정보가 올바르지 않습니다.');
      return;
    }
    
    const event = this.eventList.find(e => e.idx === eventIdx);
    if (!event) {
      this.$message.error('일정을 찾을 수 없습니다.');
      return;
    }
    
    // 현재 상태 저장 (롤백용)
    const originalIsLiked = event.isLiked;
    const originalCount = event.likeCount;
    
    try {
      // 즉시 UI 업데이트
      event.isLiked = !originalIsLiked;
      event.likeCount = originalIsLiked ? originalCount - 1 : originalCount + 1;
      
      console.log('Toggling like for event idx:', eventIdx);
      
      // 백엔드 요청
      const res = await toggleCalendarLike(eventIdx);
      const result = res.data;
      
      console.log('Like toggle response:', result);
      
      if (result.success) {
        // 서버 응답으로 정확한 값 설정
        event.isLiked = result.isLiked;
        event.likeCount = result.likeCount;
        
        if (result.isLiked) {
          this.$message.success('좋아요를 눌렀습니다');
        } else {
          this.$message.info('좋아요를 취소했습니다');
        }
      } else {
        // 서버가 success: false를 반환한 경우 롤백
        event.isLiked = originalIsLiked;
        event.likeCount = originalCount;
      }
    } catch (error: any) {
      // 에러 발생 시 롤백
      event.isLiked = originalIsLiked;
      event.likeCount = originalCount;
      
      console.error('Error toggling like:', error);
      this.$message.error(error.response?.data?.message || '좋아요 처리에 실패했습니다.');
    }
  }

  private isLiked(eventIdx: number): boolean {
    const event = this.eventList.find(e => e.idx === eventIdx);
    return event?.isLiked || false;
  }

  private getLikeCount(eventIdx: number): number {
    const event = this.eventList.find(e => e.idx === eventIdx);
    return event?.likeCount || 0;
  }

  // Comment functionality
  private async toggleComments(eventIdx: number) {
    const isCurrentlyShowing = this.showCommentsForEvent[eventIdx] || false;
    
    if (!isCurrentlyShowing) {
      // Load comments when opening
      await this.loadComments(eventIdx);
    }
    
    this.$set(this.showCommentsForEvent, eventIdx, !isCurrentlyShowing);
  }

  private async loadComments(eventIdx: number) {
    if (!eventIdx) {
      console.error('Event idx is undefined');
      return;
    }
    
    try {
      console.log('Loading comments for event idx:', eventIdx); // Debug
      const res = await getCalendarComments(eventIdx);
      const result = res.data;
      this.$set(this.commentsData, eventIdx, result.comments || []);
    } catch (error: any) {
      console.error('Error loading comments:', error);
      this.$message.error(error.response?.data?.message || '댓글을 불러오는데 실패했습니다.');
    }
  }

  private shouldShowComments(eventIdx: number): boolean {
    return this.showCommentsForEvent[eventIdx] || false;
  }

  private getCommentsList(eventIdx: number): any[] {
    return this.commentsData[eventIdx] || [];
  }

  private async handleCommentSubmit(eventIdx: number) {
    if (!eventIdx) {
      console.error('Event idx is undefined');
      this.$message.error('일정 정보가 올바르지 않습니다.');
      return;
    }
    
    const commentText = this.commentInputs[eventIdx];
    if (!commentText || commentText.trim() === '') {
      this.$message.warning('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      console.log('Submitting comment for event idx:', eventIdx); // Debug
      const res = await createCalendarComment(eventIdx, {
        content: commentText,
      });
      
      this.$message.success('댓글이 등록되었습니다!');
      
      // Clear the input
      this.$set(this.commentInputs, eventIdx, '');
      
      // Reload comments
      await this.loadComments(eventIdx);
      
      // Update comment count
      const event = this.eventList.find(e => e.idx === eventIdx);
      if (event) {
        event.commentCount = (event.commentCount || 0) + 1;
      }
    } catch (error: any) {
      console.error('Error creating comment:', error);
      this.$message.error(error.response?.data?.message || '댓글 작성에 실패했습니다.');
    }
  }

  // 참여자 관리 모달 열기
  private async handleViewParticipants(event: any) {
    this.selectedEvent = event;
    this.participantsModalVisible = true;
    await this.loadParticipants(event.idx);
  }

  // 참여자 목록 불러오기
  private async loadParticipants(eventIdx: number) {
    this.loadingParticipants = true;
    try {
      const response = await getCalendarParticipants(eventIdx);
      this.participantsList = response.data || [];
    } catch (error: any) {
      console.error('Failed to load participants:', error);
      this.$message.error('참여자 목록을 불러오는데 실패했습니다.');
      this.participantsList = [];
    } finally {
      this.loadingParticipants = false;
    }
  }

  // 참여자에게 포인트 지급
  private async handleGrantPoint(participant: any) {
    try {
      await this.$confirm(
        `${participant.userName}님에게 ${this.selectedEvent.points}P를 지급하시겠습니까?`,
        '포인트 지급',
        {
          confirmButtonText: '지급',
          cancelButtonText: '취소',
          type: 'warning',
        }
      );

      await grantPointToParticipant(participant.idx);
      this.$message.success('포인트가 지급되었습니다.');
      
      // Reload participants list
      await this.loadParticipants(this.selectedEvent.idx);
    } catch (error: any) {
      if (error !== 'cancel') {
        console.error('Failed to grant point:', error);
        this.$message.error(error.response?.data?.message || '포인트 지급에 실패했습니다.');
      }
    }
  }
}
</script>

<style scoped lang="scss">
.calendar-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #FFF;
  position: relative;
}

.calendar-main {
  margin-left: 270px;
  padding: 150px 40px 100px 40px;
  flex: 1;
  min-height: calc(100vh - 124px);
  background: #F8F9FB;
}

.empty-state {
  display: flex;
  // justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 12px;
  padding: 60px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FAFAFA;
  min-width: 600px;
  width: 100%;
  transition: border-color 0.3s ease;

  &:hover {
    border-color: #D0D0D0;
  }
}

.empty-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.empty-subtitle {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  margin: 0;
  text-align: left;
}

.events-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.event-card-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;

  &:not(:last-child) {
    margin-bottom: 16px;
  }
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 8px;
}

.event-date-info {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.event-date {
  display: flex;
  align-items: center;
  gap: 8px;

  svg {
    flex-shrink: 0;
  }

  .date-text {
    color: #000;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 22px;
    font-weight: 700;
    line-height: 100%;
  }
}

.event-meta {
  display: flex;
  align-items: flex-start;
  gap: 36px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 100%;
  white-space: nowrap;

  svg {
    flex-shrink: 0;
    width: 24px;
    height: 24px;
  }
}

/* 일정 타입 뱃지 스타일 */
.event-type-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 20px;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
}

.type-free {
  background: rgba(34, 197, 94, 0.1);
  color: #16A34A;
}

.type-paid {
  background: rgba(239, 68, 68, 0.1);
  color: #DC2626;
}

.type-earn {
  background: rgba(59, 130, 246, 0.1);
  color: #2563EB;
}

.points-badge {
  display: flex;
  width: 24px;
  height: 24px;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border-radius: 50%;
  background: rgba(7, 61, 255, 0.08);
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
}

.points-value {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 100%;
}

.join-button {
  display: flex;
  width: 252px;
  height: 60px;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  border-radius: 10px;
  border: 1px solid #073DFF;
  background: transparent;
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    background: #073DFF;
    color: #FFF;
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.2);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled,
  &.is-own-event {
    border-color: #CECECE;
    color: #999;
    cursor: not-allowed;
    background: #F5F5F5;
    
    &:hover {
      background: #F5F5F5;
      color: #999;
      border-color: #CECECE;
      transform: none;
      box-shadow: none;
    }
  }
}

.event-content {
  display: flex;
  align-items: stretch;
  gap: 40px;
  position: relative;
  min-height: 200px;
}

@media screen and (max-width: 768px) {
  .event-content {
    gap: 20px;
  }
}

.timeline-connector {
  width: 12px;
  border-right: 2px solid #EBEBEB;
  flex-shrink: 0;
  position: relative;
  align-self: stretch;

  &::before {
    content: '';
    position: absolute;
    right: -2px;
    top: 0;
    width: 2px;
    height: 100%;
    background: linear-gradient(180deg, #EBEBEB 0%, rgba(235, 235, 235, 0.3) 100%);
  }
  
/*   &::after {
    content: '';
    position: absolute;
    right: -6px;
    top: 0;
    width: 10px;
    height: 10px;
    background: #073DFF;
    border-radius: 50%;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  } */
}

.event-card {
  display: flex;
  flex-direction: column;
  gap: 32px;
  flex: 1;
  padding: 40px;
  margin-bottom: 64px;
  border-radius: 10px;
  border: 2px solid #EBEBEB;
  background: #FFF;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    border-color: #D0D0D0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: space-between;
  .user-info-wr{display: flex; align-items: center; gap: 8px;}

  .user-avatar {
    flex-shrink: 0;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    overflow: hidden;

    .user-avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    svg {
      display: block;
      width: 100%;
      height: 100%;
    }
  }

  .user-name {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 600;
    line-height: 100%;
  }
}

/* Element UI Dropdown 스타일 */
.el-dropdown-link {
  cursor: pointer;
  color: #888;
  font-size: 20px;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
  margin-left: auto;
  
  &:hover {
    color: #222;
  }
  
  i {
    font-size: 20px;
  }
}

.event-details {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.event-title {
  color: #000;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 36px;
  font-weight: 700;
  line-height: 100%;
  margin: 0;
  word-break: keep-all;
  text-align: left;
}

.event-description {
  color: #6B7280;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 22px;
  font-weight: 400;
  line-height: 150%;
  margin: 0;
  word-break: keep-all;
  text-align: left;
}

.event-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-top: 18px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  transition: all 0.2s ease;

  svg {
    flex-shrink: 0;
    transition: all 0.2s ease;
  }

  &:hover {
    color: #073DFF;
    cursor: pointer;

    svg {
      opacity: 0.8;
    }
  }

  &.liked {
    color: #073DFF;
    font-weight: 600;

    .stat-count {
      color: #073DFF;
    }
  }

  &.active {
    color: #073DFF;
    font-weight: 600;

    .stat-count {
      color: #073DFF;
    }
  }
}

.stat-count {
  transition: color 0.2s ease;
}

.stat-divider {
  width: 1px;
  height: 14px;
  background: #EBEBEB;
}

/* Comments Section */
.comments-section {
  width: calc(100% - 50px);
  margin: -50px 0px 20px 50px;
  padding: 24px;
  background: #F9FAFB;
  border-radius: 10px;
  border: 1px solid #E5E7EB;
  animation: slideDown-15226a58 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.empty-comments {
  padding: 40px 20px;
  text-align: center;
  color: #888;
  font-size: 16px;
  
  p {
    margin: 0;
  }
}

.comment-item {
  background: #FFF;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  transition: all 0.2s ease;

  &:hover {
    border-color: #D1D5DB;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .user-avatar {
      flex-shrink: 0;

      svg {
        display: block;
      }
    }

    .user-name {
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 18px;
      font-weight: 600;
      line-height: 100%;
    }
  }
}

.comment-date {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 100%;
}

.comment-text {
  color: #374151;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  word-break: keep-all;
}

.comment-input-area {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.comment-input {
  flex: 0 1 calc(100% - 146px);
  width: 100%;
  height: 48px;
  padding: 0 16px;
  border-radius: 8px;
  border: 1px solid #D1D5DB;
  background: #FFF;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #9CA3AF;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #9CA3AF;
  }
}

.comment-submit-btn {
  flex: 0 1 136px;
  width: 100%;
  height: 48px;
  padding: 0 24px;
  border-radius: 8px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 600;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;

  &:hover {
    background: #0530CC;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }

  &:active {
    transform: translateY(0);
  }

  &:disabled {
    background: #D1D5DB;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

/* Modal Styles */
::v-deep .create-event-modal {
  border-radius: 12px;
  overflow: visible;

  .el-dialog {
    border-radius: 12px;
    overflow: visible;
  }

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
    overflow: visible;
  }
}

::v-deep .join-event-modal {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog {
    border-radius: 12px;
  }

  .el-dialog__header {
    padding: 0;
    display: none;
  }

  .el-dialog__body {
    padding: 40px;
    position: relative;
    width: 100%;
  }

  @media screen and (max-width: 768px) {
    width: 100% !important;
    margin: 0 !important;
    margin-top: auto !important;
    border-radius: 10px 10px 0 0;
    max-width: 100vw;
    position: fixed;
    bottom: 0;
    left: 0;
    animation: slideUp 0.3s ease-out;

    .el-dialog__body {
      padding: 40px 20px;
    }
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Fix z-index to appear above header */
::v-deep .el-dialog__wrapper {
  z-index: 9999 !important;

  @media screen and (max-width: 768px) {
    .join-event-modal {
      display: flex;
      align-items: flex-end;
    }
  }
}

.modal-content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 40px;
  position: relative;
  overflow: visible;
}

.write-event-btn-fixed {
  display: none;
  position: fixed;
  right: 20px;
  bottom: 20px;
  width: 56px;
  height: 56px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 50%;
  color: #FFF;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-icon {
    display: block;
  }

  &:hover {
    background: #0530CC;
    transform: scale(1.1);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: scale(0.95);
  }
}

.modal-close-btn {
  position: absolute;
  right: 0;
  top: 0;
  width: 24px;
  height: 24px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  color: #888;
  transition: all 0.2s ease;

  &:hover {
    color: #000;
    transform: rotate(90deg);
  }
}

.modal-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 150%;
  margin: 0;
  text-align: center;
  word-break: keep-all;
}

.modal-subtitle {
  color: #073DFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 700;
  line-height: 100%;
  margin-top: -30px;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  overflow: visible;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: visible;
}

.form-label {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 700;
  line-height: 100%;
}

.form-input {
  height: 52px;
  padding: 0 16px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #B0B0B0;
  }
}

.form-textarea {
  min-height: 104px;
  padding: 16px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 150%;
  resize: vertical;
  outline: none;
  transition: all 0.3s ease;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
    box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
  }

  &:hover:not(:focus) {
    border-color: #B0B0B0;
  }
}

.datetime-container {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
  overflow: visible;
  position: relative;
  z-index: 1;
}

.date-picker {
  width: 208px;
  
  ::v-deep .el-input__inner {
    height: 48px;
    border-radius: 8px;
    border: 1px solid #E0E0E0;
    font-size: 14px;
    color: #333;
    padding-left: 40px;
    cursor: pointer;
    
    &::placeholder {
      color: #B0B0B0;
    }
    
    &:hover {
      border-color: #073DFF;
    }
    
    &:focus {
      border-color: #073DFF;
      box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
    }
  }
  
  ::v-deep .el-input__prefix {
    left: 12px;
  }
  
  ::v-deep .el-input__icon {
    line-height: 48px;
  }
}

.time-range {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 280px;
}

.time-picker {
  flex: 1;
  
  ::v-deep .el-input__inner {
    height: 48px;
    border-radius: 8px;
    border: 1px solid #E0E0E0;
    font-size: 14px;
    color: #333;
    cursor: pointer;
    
    &::placeholder {
      color: #B0B0B0;
    }
    
    &:hover {
      border-color: #073DFF;
    }
    
    &:focus {
      border-color: #073DFF;
      box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
    }
  }
  
  ::v-deep .el-input__icon {
    line-height: 48px;
  }
}

.event-type-buttons {
  display: flex;
  gap: 20px;
}

/* 수정 모드에서 읽기 전용 일정 타입 표시 */
.event-type-readonly {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.readonly-badge {
  display: flex;
  align-items: center;
}

.readonly-notice {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 500;
  margin: 0;
}

.event-type-btn {
  width: 100px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #F5F5F5;
  color: #666;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover:not(.active) {
    border-color: #073DFF;
    background: #FAFAFA;
    transform: translateY(-1px);
  }

  &.active {
    background: #073DFF;
    border-color: #073DFF;
    color: #FFF;
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }
}

.submit-button {
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 10px;
  border: none;
  background: #073DFF;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    background: #0535e6;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.35);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(7, 61, 255, 0.25);
  }
}

.points-breakdown {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-self: stretch;
  padding: 30px;
  border-radius: 10px;
  border: 1px solid #CECECE;
  background: #FFF;
}

.breakdown-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #EBEBEB;

  h3 {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 20px;
    font-weight: 700;
    line-height: 150%;
    margin: 0;
  }
}

.breakdown-items {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.breakdown-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;

  span {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 150%;
  }

  .amount {
    font-size: 22px;
    font-weight: 700;
    transition: color 0.2s ease;

    &.highlight {
      color: #073DFF;
    }
  }

  &.total {
    padding-top: 16px;
    margin-top: 8px;
    border-top: 1px solid #EBEBEB;
  }
}

/* Responsive Design */
@media screen and (max-width: 1366px) {
  .event-title {
    font-size: 32px;
  }

  .event-description {
    font-size: 20px;
  }
}

@media screen and (max-width: 1024px) {
  .calendar-main {
    margin-left: 240px;
    padding: 140px 30px 100px 30px;
  }

  .event-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .event-card {
    padding: 30px 20px;
    gap: 24px;
  }

  .join-button {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .comments-section {
    width: calc(100% - 30px);
    margin: -50px 0px 20px 30px;
  }
  .calendar-main {
    margin: 100px 0 0 0;
    padding: 32px 24px;
  }

  .event-meta {
    flex-wrap: wrap;
    gap: 16px;
  }

  .event-card {
    padding: 32px 24px;
    gap: 24px;
  }

  .event-title {
    font-size: 28px;
  }

  .event-description {
    font-size: 18px;
    line-height: 160%;
  }

  .event-details {
    gap: 24px;
  }

  .event-stats {
    margin-top: 12px;
  }

  .join-button {
    height: 45px;
  }

  .write-event-btn-fixed {
    display: flex;
  }
}

@media screen and (max-width: 600px) {
  .calendar-main {
    padding: 20px 16px;
    margin-top: 100px;
  }

  .comment-submit-btn {
    width:100%;
  }

  .empty-box {
    min-width: auto;
    width: 100%;
    padding: 40px 24px;
  }

  .empty-title {
    font-size: 22px;
  }

  .empty-subtitle {
    font-size: 14px;
  }

  .modal-content-wrapper {
    gap: 30px;
  }

  ::v-deep .create-event-modal {
    width: 100% !important;
    margin: 0 !important;
    
    .el-dialog {
      width: 100% !important;
      height: 100%;
      margin: 0 !important;
      max-height: 100vh;
    }

    .el-dialog__body {
      padding: 24px;
      height: 100vh;
      overflow-y: auto;
    }
  }

  ::v-deep .join-event-modal{
    width: 100% !important;
    margin: 0 !important;
    
    .el-dialog {
      width: 100% !important;
      height: 100%;
      margin: 0 !important;
      max-height: 100vh;
    }

    .el-dialog__body {
      padding: 24px;
      height: 70vh;
      overflow-y: auto;
    }
  }

  .event-meta {
    gap: 12px;
  }

  .meta-item {
    font-size: 16px;

    svg {
      width: 20px;
      height: 20px;
    }
  }
}

@media screen and (max-width: 500px) {
  .timeline-connector {width: 0px;border-right: 0;}
  .timeline-connector::before {right: -3px;}
  .calendar-main { padding: 20px 16px; margin-top: 80px;} 

  .event-content {gap: 15px;}
  .user-info .user-avatar {width: 30px; height: 30px;}
  .user-info .user-name {font-size: 18px;}
  .event-title {font-size: 22px;}
  .event-details {gap: 15px;}
  .event-description {font-size: 16px;}

  .comment-header {gap: 10px;}
  .comments-section {width: calc(100% - 20px); margin: -50px 0px 20px 16px; padding: 20px 15px;}
  .comment-input-area {gap:10px;}
  .comment-input {flex: 0 1 calc(100% - 110px);}
  .comment-submit-btn {flex: 0 1 100px; padding: 0;}
}
/* Element UI Custom Styling */
::v-deep .el-date-picker,
::v-deep .el-time-select {
  .el-input__inner {
    height: 52px;
    border-radius: 10px;
    border: 1px solid #CECECE;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    color: #222;
    transition: all 0.3s ease;

    &::placeholder {
      color: #888;
    }

    &:hover {
      border-color: #B0B0B0;
    }

    &:focus {
      border-color: #073DFF;
      box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.1);
    }
  }

  .el-input__icon {
    line-height: 52px;
  }
}

::v-deep .el-picker-panel {
  border-radius: 10px;
  border: 1px solid #EBEBEB;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

::v-deep .el-date-picker__header-label,
::v-deep .el-picker-panel__icon-btn {
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
}

::v-deep .el-date-table td.available:hover,
::v-deep .el-month-table td .cell:hover,
::v-deep .el-year-table td .cell:hover {
  color: #073DFF;
}

::v-deep .el-date-table td.today span {
  color: #073DFF;
  font-weight: 700;
}

::v-deep .el-date-table td.current:not(.disabled) span {
  background-color: #073DFF;
}

::v-deep .el-time-select-item.selected:not(.disabled) {
  background-color: #073DFF;
  font-weight: 700;
}

::v-deep .el-time-select-item:hover {
  background-color: rgba(7, 61, 255, 0.1);
}

/* Smooth page transitions */
.events-container {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.event-card-wrapper {
  animation: slideIn 0.4s ease-out backwards;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Stagger animation for event cards */
.event-card-wrapper:nth-child(1) { animation-delay: 0.05s; }
.event-card-wrapper:nth-child(2) { animation-delay: 0.1s; }
.event-card-wrapper:nth-child(3) { animation-delay: 0.15s; }
.event-card-wrapper:nth-child(4) { animation-delay: 0.2s; }
.event-card-wrapper:nth-child(5) { animation-delay: 0.25s; }
.event-card-wrapper:nth-child(6) { animation-delay: 0.3s; }
.event-card-wrapper:nth-child(7) { animation-delay: 0.35s; }
.event-card-wrapper:nth-child(8) { animation-delay: 0.4s; }

/* Selection and interaction states */
::selection {
  background-color: rgba(7, 61, 255, 0.2);
  color: inherit;
}

::-moz-selection {
  background-color: rgba(7, 61, 255, 0.2);
  color: inherit;
}

/* Scrollbar styling */
.calendar-main::-webkit-scrollbar {
  width: 8px;
}

.calendar-main::-webkit-scrollbar-track {
  background: #F5F5F5;
  border-radius: 4px;
}

.calendar-main::-webkit-scrollbar-thumb {
  background: #CECECE;
  border-radius: 4px;
  transition: background 0.2s ease;
}

.calendar-main::-webkit-scrollbar-thumb:hover {
  background: #B0B0B0;
}

/* Focus visible for accessibility */
.join-button:focus-visible,
.submit-button:focus-visible,
.event-type-btn:focus-visible {
  outline: 2px solid #073DFF;
  outline-offset: 2px;
}

.form-input:focus-visible,
.form-textarea:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(7, 61, 255, 0.15);
}

/* Responsive datetime container */
@media (max-width: 768px) {
  .datetime-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-picker,
  .time-range {
    width: 100%;
    min-width: auto;
  }
}
</style>

<style lang="scss">
/* Global styles for Element UI Date & Time Pickers */
.calendar-date-picker-popper,
.calendar-time-picker-popper {
  z-index: 99999 !important;
}

.el-picker-panel {
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
  z-index: 99999 !important;
  
  .el-date-picker__header-label {
    color: #333 !important;
    font-weight: 600 !important;
  }
  
  .el-picker-panel__icon-btn {
    color: #666 !important;
    
    &:hover {
      color: #073DFF !important;
    }
  }
}

.el-date-table {
  td {
    &.available:hover {
      color: #073DFF !important;
    }
    
    &.today span {
      color: #073DFF !important;
      font-weight: 600 !important;
    }
    
    &.current:not(.disabled) span {
      background-color: #073DFF !important;
      color: white !important;
    }
  }
}

.el-select-dropdown.el-popper {
  z-index: 99999 !important;
}

.el-time-panel {
  z-index: 99999 !important;
}

.el-popper {
  z-index: 99999 !important;
}

.el-time-select-item {
  &:hover {
    background-color: rgba(7, 61, 255, 0.1) !important;
    color: #073DFF !important;
  }
  
  &.selected:not(.disabled) {
    background-color: #073DFF !important;
    color: white !important;
    font-weight: 600 !important;
  }
  
  &.disabled {
    color: #C0C4CC !important;
    cursor: not-allowed !important;
  }
}

.el-picker-panel__footer {
  border-top: 1px solid #E4E7ED !important;
  padding: 8px !important;
  
  .el-button--text {
    color: #073DFF !important;
    
    &:hover {
      background-color: rgba(7, 61, 255, 0.1) !important;
    }
  }
}

/* Participants Modal Styles */
.participants-modal {
  .modal-content-wrapper {
    padding: 32px;
  }

  .modal-title {
    font-size: 24px;
    font-weight: 700;
    color: #1a1a1a;
    margin-bottom: 8px;
  }

  .modal-subtitle {
    font-size: 14px;
    color: #6B7280;
    margin-bottom: 24px;
  }

  .participants-list {
    min-height: 200px;
    max-height: 500px;
    overflow-y: auto;
  }

  .empty-participants {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 200px;
    color: #9CA3AF;
    font-size: 14px;
  }

  .participant-items {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .participant-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    border: 1px solid #E5E7EB;
    border-radius: 12px;
    background-color: #F9FAFB;
  }

  .participant-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .participant-avatar {
    flex-shrink: 0;
  }

  .participant-details {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .participant-name {
    font-size: 14px;
    font-weight: 600;
    color: #1F2937;
  }

  .participant-date {
    font-size: 12px;
    color: #9CA3AF;
  }

  .grant-point-btn {
    padding: 8px 16px;
    background-color: #073DFF;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    white-space: nowrap;
    
    &:hover {
      background-color: #0531CC;
    }
    
    &:disabled {
      background-color: #E5E7EB;
      color: #9CA3AF;
      cursor: not-allowed;
    }
  }

  .point-granted-badge {
    padding: 6px 12px;
    background-color: #10B981;
    color: white;
    border-radius: 6px;
    font-size: 12px;
    font-weight: 600;
  }
}

/* 참여자 관리 버튼 스타일 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.manage-participants-button {
  padding: 12px 24px;
  background-color: #073DFF;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #0531CC;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);
  }
  
  &:active {
    transform: translateY(0);
  }
}
</style>
