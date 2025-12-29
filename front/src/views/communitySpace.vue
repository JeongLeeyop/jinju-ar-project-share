<template>
  <div class="community-space-page">
    <CommunitySidebar 
      :selectedSpaceId="spaceId" 
      @space-select="handleSpaceSelect"
      @open-write-modal="openWriteModal"
    />

    <div class="community-main space-main">
      <div class="space-header">
        <div class="space-name-tag">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="8" cy="8" r="8" :fill="spaceColor"/>
          </svg>
          <span>{{ spaceName }}</span>
          <span class="space-visibility-badge" :class="{ 'public-badge': isPublic, 'private-badge': !isPublic }">
            {{ isPublic ? '공개' : '비공개' }}
          </span>
        </div>
      </div>
      <div class="posts-container">
        <!-- 로딩 상태 -->
        <div v-if="loading" class="loading-container">
          <i class="el-icon-loading"></i>
          <span>게시글을 불러오는 중...</span>
        </div>

        <!-- 게시글이 없는 경우 -->
        <div v-else-if="posts.length === 0" class="empty-container">
          <p>아직 작성된 게시글이 없습니다.</p>
          <p>첫 게시글을 작성해보세요!</p>
        </div>

        <!-- 실제 데이터 렌더링 (백엔드에서 이미 정렬된 순서: 상단 고정 공지 3개 + 나머지 최신순) -->
        <template v-else>
          <!-- 모든 게시글 (공지사항 + 일반 게시글, 백엔드 정렬 순서 유지) -->
          <div 
            v-for="post in posts" 
            :key="post.uid" 
            class="post-card"
            :class="{ 'notice-post': post.isNotice }"
          >
            <div class="post-content">
              <div class="post-header">
                <div class="user-info">
                  <div class="user-avatar">
                    <img v-if="post.iconFileUid" :src="`${apiUrl}/attached-file/${post.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                    <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                      <mask :id="`mask-${post.uid}`" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                        <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                      </mask>
                      <g :mask="`url(#mask-${post.uid})`">
                        <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                        <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                      </g>
                    </svg>
                  </div>
                  <span class="user-name">{{ post.authorName }}</span>
                </div>
                <div class="post-actions">
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                  <el-dropdown v-if="post.isAuthor" trigger="click" @command="handlePostAction">
                    <span class="el-dropdown-link">
                      <i class="el-icon-more"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item :command="{action: 'edit', post: post}">수정</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'delete', post: post}">삭제</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>

              <!-- 제목 -->
              <!-- <div v-if="post.title" class="post-title">{{ post.title }}</div> -->

              <div class="post-text-content summernote-content" :class="{ expanded: isExpanded(post.uid) }">
                <div class="text-preview" v-html="post.content"></div>
                <span v-if="post.content.length > 150" class="read-more-link" @click.stop="toggleExpand(post.uid)">
                  {{ isExpanded(post.uid) ? '접기' : '더보기' }}
                </span>
              </div>

              <!-- 첨부 이미지가 있는 경우 -->
              <div v-if="post.attachments && post.attachments.length > 0" class="post-images">
                <img 
                  v-for="(attachment, index) in post.attachments" 
                  :key="index"
                  :src="getAttachmentUrl(attachment)" 
                  :alt="`이미지 ${index + 1}`" 
                  class="post-image" 
                />
              </div>

              <div class="post-stats">
                <div class="stat-item" :class="{ liked: isLiked(post.uid) }" @click.stop="toggleLike(post.uid)">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path class="like-icon" d="M7.49301 21.5C7.06801 21.5 6.67301 21.264 6.51801 20.868C6.17475 19.9943 5.99902 19.0637 6.00001 18.125C6.00001 16.375 6.59901 14.767 7.60201 13.491C7.75301 13.299 7.97501 13.182 8.20201 13.094C8.67501 12.911 9.09201 12.58 9.41401 12.17C10.1903 11.1796 11.1647 10.3622 12.275 9.77C12.998 9.386 13.625 8.814 13.928 8.055C14.1408 7.52325 14.2501 6.95575 14.25 6.383V5.75C14.25 5.55109 14.329 5.36032 14.4697 5.21967C14.6103 5.07902 14.8011 5 15 5C15.5967 5 16.169 5.23705 16.591 5.65901C17.013 6.08097 17.25 6.65326 17.25 7.25C17.25 8.402 16.99 9.493 16.527 10.468C16.261 11.026 16.634 11.75 17.252 11.75H20.378C21.404 11.75 22.323 12.444 22.432 13.465C22.477 13.887 22.5 14.315 22.5 14.75C22.5041 17.4863 21.5691 20.1412 19.851 22.271C19.463 22.753 18.864 23 18.246 23H14.23C13.747 23 13.266 22.922 12.807 22.77L9.69301 21.73C9.23411 21.5774 8.75362 21.4997 8.27001 21.5H7.49301ZM2.33101 13.727C1.77993 15.1277 1.498 16.6198 1.50001 18.125C1.49875 19.3133 1.67399 20.4952 2.02001 21.632C2.28001 22.482 3.10501 23 3.99401 23H4.90001C5.34501 23 5.62001 22.502 5.42301 22.102C4.81315 20.8651 4.49695 19.5041 4.49901 18.125C4.49901 16.417 4.97501 14.82 5.80101 13.459C6.04601 13.056 5.77301 12.5 5.30101 12.5H4.25001C3.41801 12.5 2.63701 12.953 2.33101 13.727Z" fill="#CECECE"/>
                  </svg>
                  <span class="stat-count">{{ getLikeCount(post.uid) }}</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item" :class="{ active: shouldShowComments(post.uid) }" @click.stop="toggleComments(post.uid)">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd" clip-rule="evenodd" d="M4.848 2.771C7.21613 2.4234 9.60649 2.24927 12 2.25C14.43 2.25 16.817 2.428 19.152 2.77C21.13 3.062 22.5 4.794 22.5 6.74V12.76C22.5 14.706 21.13 16.438 19.152 16.73C17.9983 16.8991 16.8389 17.0268 15.676 17.113C15.6168 17.1169 15.5593 17.1342 15.5079 17.1637C15.4564 17.1931 15.4123 17.234 15.379 17.283L12.624 21.416C12.5555 21.5187 12.4627 21.6029 12.3539 21.6612C12.245 21.7194 12.1235 21.7499 12 21.7499C11.8765 21.7499 11.755 21.7194 11.6461 21.6612C11.5373 21.6029 11.4445 21.5187 11.376 21.416L8.621 17.283C8.58768 17.234 8.54361 17.1931 8.49214 17.1637C8.44068 17.1342 8.38317 17.1169 8.324 17.113C7.16113 17.0265 6.00172 16.8984 4.848 16.729C2.87 16.439 1.5 14.705 1.5 12.759V6.741C1.5 4.795 2.87 3.061 4.848 2.771ZM6.75 8.25C6.75 8.05109 6.82902 7.86032 6.96967 7.71967C7.11032 7.57902 7.30109 7.5 7.5 7.5H16.5C16.6989 7.5 16.8897 7.57902 17.0303 7.71967C17.171 7.86032 17.25 8.05109 17.25 8.25C17.25 8.44891 17.171 8.63968 17.0303 8.78033C16.8897 8.92098 16.6989 9 16.5 9H7.5C7.30109 9 7.11032 8.92098 6.96967 8.78033C6.82902 8.63968 6.75 8.44891 6.75 8.25ZM7.5 10.5C7.30109 10.5 7.11032 10.579 6.96967 10.7197C6.82902 10.8603 6.75 11.0511 6.75 11.25C6.75 11.4489 6.82902 11.6397 6.96967 11.7803C7.11032 11.921 7.30109 12 7.5 12H12C12.1989 12 12.3897 11.921 12.5303 11.7803C12.671 11.6397 12.75 11.4489 12.75 11.25C12.75 11.0511 12.671 10.8603 12.5303 10.7197C12.3897 10.579 12.1989 10.5 12 10.5H7.5Z" :fill="shouldShowComments(post.uid) ? '#073DFF' : '#CECECE'"/>
                  </svg>
                  <span class="stat-count">{{ post.commentCount }}</span>
                </div>
              </div>
            </div>

            <!-- 공지사항 배지 -->
            <div v-if="post.isNotice" class="notice-badge">
              <svg width="100" height="38" viewBox="0 0 100 38" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M0 0H100V38H0L9.16667 19L0 0Z" fill="#073DFF"/>
              </svg>
              <span class="badge-text">공지사항</span>
            </div>

            <!-- 댓글 섹션 -->
            <div class="comments-section" v-if="shouldShowComments(post.uid)">
              <div class="comment-list">
                <div v-if="!postComments[post.uid] || postComments[post.uid].length === 0" class="empty-comments">
                  <p>첫 댓글을 작성해보세요!</p>
                </div>
                <div 
                  v-else
                  v-for="comment in postComments[post.uid]" 
                  :key="comment.uid"
                  class="comment-item"
                >
                  <div class="comment-header">
                    <div class="user-info">
                      <div class="user-avatar">
                        <img v-if="comment.iconFileUid" :src="`${apiUrl}/attached-file/${comment.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                        <svg v-else width="28" height="28" viewBox="0 0 28 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <circle cx="14" cy="14" r="14" fill="#D9D9D9"/>
                          <mask :id="`mask-comment-${comment.uid}`" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="28" height="28">
                            <circle cx="14" cy="14" r="14" fill="#D9D9D9"/>
                          </mask>
                          <g :mask="`url(#mask-comment-${comment.uid})`">
                            <rect x="3" y="16" width="22" height="25" rx="11" fill="#F5F5F5"/>
                            <circle cx="14" cy="9" r="5.5" fill="#F5F5F5"/>
                          </g>
                        </svg>
                      </div>
                      <span class="user-name">{{ comment.authorName }}</span>
                    </div>
                    <div class="comment-actions">
                      <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                      <el-dropdown v-if="comment.isAuthor && !commentEditMode[comment.uid]" trigger="click" @command="handleCommentAction">
                        <span class="el-dropdown-link">
                          <i class="el-icon-more"></i>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item :command="{action: 'edit', comment: comment, postUid: post.uid}">수정</el-dropdown-item>
                          <el-dropdown-item :command="{action: 'delete', comment: comment, postUid: post.uid}">삭제</el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </div>
                  
                  <!-- 댓글 내용 (일반 모드) -->
                  <div v-if="!commentEditMode[comment.uid]" class="comment-text summernote-content" :class="{ expanded: isCommentExpanded(comment.uid) }">
                    <div v-html="comment.content"></div>
                    <span v-if="comment.content.length > 100" class="read-more-link" @click.stop="toggleCommentExpand(comment.uid)">
                      {{ isCommentExpanded(comment.uid) ? '접기' : '더보기' }}
                    </span>
                  </div>
                  
                  <!-- 댓글 내용 (수정 모드) -->
                  <div v-else class="comment-edit-area">
                    <input 
                      type="text" 
                      class="comment-input" 
                      v-model="editingCommentContent[comment.uid]"
                      @keyup.enter="saveCommentEdit(post.uid, comment.uid)"
                    />
                    <div class="comment-edit-actions">
                      <el-button size="mini" @click="cancelCommentEdit(comment.uid)">취소</el-button>
                      <el-button size="mini" type="primary" @click="saveCommentEdit(post.uid, comment.uid)">저장</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="comment-input-area">
                <input 
                  type="text" 
                  class="comment-input" 
                  placeholder="댓글을 입력하세요." 
                  v-model="commentInputs[post.uid]"
                  @keyup.enter="submitComment(post.uid)"
                />
                <button 
                  class="comment-submit-btn"
                  @click="submitComment(post.uid)"
                  :disabled="!commentInputs[post.uid] || !commentInputs[post.uid].trim()"
                >
                  댓글입력
                </button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <ParticipantsList
      :participants="participants"
      :participantCount="participantCount"
      :isAdmin="isAdmin"
      :isPublic="isPublic"
      @invite="openInviteModal"
    />

    <!-- Floating Write Button (Mobile) -->
    <button class="write-post-btn-fixed" @click="openWriteModal">
      <span class="btn-text">글 작성하기</span>
      <svg class="btn-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
        <path d="M12 5V19M5 12H19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      </svg>
    </button>

    <!-- Write Post Dialog -->
    <el-dialog 
      :title="editingPostUid ? '글 수정' : '글 작성'" 
      class="community-post-form dialog-wrap" 
      :visible.sync="writeFormVisible"
      @open="handleDialogOpen"
    >
      <SpaceWrite 
        v-if="writeFormVisible" 
        :spaceUid="spaceId" 
        :postUid="editingPostUid" 
        :post="editingPost"
        :isAdmin="isAdmin"
        @cancel="handleCancelWrite" 
        @success="handleWriteSuccess" 
      />
    </el-dialog>

    <!-- 초대 모달 -->
    <SpaceInviteModal
      :visible.sync="inviteModalVisible"
      :space-uid="spaceId"
      @invited="handleInvited"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import CommunitySidebar from './components/communitySidebar.vue';
import ParticipantsList from './components/ParticipantsList.vue';
import SpaceWrite from './components/spaceWrite.vue';
import SpaceInviteModal from '@/components/SpaceInviteModal.vue';
import { getSpace, getSpaceMembers } from '@/api/space';
import { 
  getSpacePostsBySpace, 
  getSpacePostComments,
  createSpacePostComment,
  updateSpacePostComment,
  deleteSpacePostComment,
  deleteSpacePost,
  toggleSpacePostLike,
  SpacePost,
  SpacePostComment,
} from '@/api/spacePost';

interface Participant {
  id: string | number;
  name: string;
  avatar?: string;
  email?: string;
  joinedAt?: string;
}

@Component({
  name: 'CommunitySpace',
  components: {
    CommunitySidebar,
    ParticipantsList,
    SpaceWrite,
    SpaceInviteModal,
  },
})
export default class CommunitySpace extends Vue {
  private spaceId = '';
  private spaceName = '커뮤니티 공간';
  private spaceColor = '#FFAD3A';
  private channelUid = '';
  private isAdmin = false; // 현재 사용자가 관리자인지
  private isPublic = false; // 공간이 공개인지
  private expandedPosts: { [key: string]: boolean } = {};
  private expandedComments: { [key: string]: boolean } = {};
  private showCommentsForPost: { [key: string]: boolean } = {};
  private likedPosts: { [key: string]: boolean } = {};
  private likeCounts: { [key: string]: number } = {};
  private participantCount = 0;
  private participants: Participant[] = [];
  private loading = false;
  private posts: SpacePost[] = []; // ✅ 백엔드에서 정렬된 순서 그대로 저장 (공지사항 3개 + 나머지 최신순)
  private postComments: { [key: string]: SpacePostComment[] } = {};
  private commentInputs: { [key: string]: string } = {};
  private commentEditMode: { [key: string]: boolean } = {}; // 댓글 수정 모드
  private editingCommentContent: { [key: string]: string } = {}; // 수정 중인 댓글 내용
  
  // Write 컴포넌트 관련 데이터
  private writeFormVisible = false;
  private editingPostUid = ''; // 수정 중인 게시글 UID
  private editingPost: any = null; // 수정 중인 게시글 데이터

  // 초대 모달 관련
  private inviteModalVisible = false;

  // ✅ apiUrl을 computed getter로 변경하여 템플릿에서 접근 가능하도록 수정
  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  mounted() {
    this.spaceId = this.$route.params.spaceId || '';
    
    if (this.spaceId) {
      this.loadSpaceData();
      this.loadPosts();
      this.loadParticipants();
    }
  }

  @Watch('$route.params.spaceId')
  private onSpaceIdChange(newSpaceId: string) {
    if (newSpaceId) {
      this.spaceId = newSpaceId;
      this.loadSpaceData();
      this.loadPosts();
      this.loadParticipants();
    }
  }

  // 공간 정보 로드
  private async loadSpaceData() {
    try {
      this.loading = true;
      const response = await getSpace(this.spaceId);
      if (response.data) {
        this.spaceName = response.data.name || '커뮤니티 공간';
        this.channelUid = response.data.channelUid || '';
        this.isAdmin = response.data.isAdmin || false;
        this.isPublic = response.data.isPublic || false;
        // Space color는 백엔드에 없으므로 기본값 사용
      }
    } catch (error) {
      console.error('공간 정보 로드 실패:', error);
    } finally {
      this.loading = false;
    }
  }

  // 게시글 목록 로드
  private async loadPosts() {
    try {
      // 백엔드에서 이미 정렬된 순서로 반환 (상단 고정 공지 3개 + 나머지 게시글 최신순)
      const postsResponse = await getSpacePostsBySpace(this.spaceId);
      if (postsResponse.data) {
        const allPosts = postsResponse.data;
        
        // ✅ 백엔드가 반환한 순서 그대로 사용 (재정렬 하지 않음)
        // 백엔드 정렬 순서:
        // 1. 공지사항 상단 3개 고정
        // 2. 나머지 게시글 (3개 이후 공지사항 + 일반 게시글) 최신순
        this.posts = allPosts;
        
        // 좋아요 수 초기화
        allPosts.forEach((post: SpacePost) => {
          this.likeCounts[post.uid] = post.likeCount;
          this.likedPosts[post.uid] = post.isLiked;
        });
      }
    } catch (error: any) {
      console.error('Failed to load posts:', error);
      const message = error.response?.data?.message || '게시글을 불러올 수 없습니다.';
      this.$message.error(message);
    }
  }

  // 참여자 목록 로드
  private async loadParticipants() {
    try {
      const response = await getSpaceMembers(this.spaceId);
      if (response.data && Array.isArray(response.data)) {
        this.participants = response.data.map((member: any) => ({
          id: member.uid,
          name: member.actualName || member.name || '익명',
          avatar: member.profileImage || member.avatar,
          email: member.email,
          joinedAt: member.joinedAt || member.createdAt,
        }));
        this.participantCount = this.participants.length;
      }
    } catch (error: any) {
      console.error('Failed to load participants:', error);
      // 에러 시 빈 배열 유지
    }
  }

  // 댓글 로드
  private async loadComments(postUid: string) {
    try {
      const response = await getSpacePostComments(this.spaceId, postUid);
      if (response.data) {
        this.$set(this.postComments, postUid, response.data);
      }
    } catch (error: any) {
      console.error('Failed to load comments:', error);
      const message = error.response?.data?.message || '댓글을 불러올 수 없습니다.';
      this.$message.error(message);
    }
  }

  private handleSpaceSelect(spaceId: string) {
    this.$router.push({
      name: 'CommunitySpace',
      params: {
        domain: this.$route.params.domain,
        spaceId,
      },
    });
  }

  private toggleExpand(postId: string) {
    this.$set(this.expandedPosts, postId, !this.expandedPosts[postId]);
  }

  private toggleCommentExpand(commentId: string) {
    this.$set(this.expandedComments, commentId, !this.expandedComments[commentId]);
  }

  private toggleComments(postId: string) {
    this.$set(this.showCommentsForPost, postId, !this.showCommentsForPost[postId]);
    // 댓글을 처음 열 때만 로드
    if (this.showCommentsForPost[postId] && !this.postComments[postId]) {
      this.loadComments(postId);
    }
  }

  private isExpanded(postId: string): boolean {
    return this.expandedPosts[postId] || false;
  }

  private isCommentExpanded(commentId: string): boolean {
    return this.expandedComments[commentId] || false;
  }

  private shouldShowComments(postId: string): boolean {
    return this.showCommentsForPost[postId] || false;
  }

  private async toggleLike(postId: string) {
    // 현재 상태 저장 (에러 시 롤백용)
    const isCurrentlyLiked = this.likedPosts[postId] || false;
    const currentCount = this.likeCounts[postId] || 0;
    
    // 새로운 상태 계산
    const newLikedState = !isCurrentlyLiked;
    const newCount = isCurrentlyLiked ? currentCount - 1 : currentCount + 1;
    
    // 즉시 UI 업데이트 (낙관적 업데이트)
    // Vue.set을 사용하여 반응성 보장
    this.$set(this.likedPosts, postId, newLikedState);
    this.$set(this.likeCounts, postId, newCount);
    
    // 게시글 배열에서도 직접 업데이트 (이중 업데이트로 반응성 강화)
    const targetPost = this.posts.find(p => p.uid === postId);
    if (targetPost) {
      this.$set(targetPost, 'isLiked', newLikedState);
      this.$set(targetPost, 'likeCount', newCount);
    }
    
    // 강제 리렌더링
    this.$forceUpdate();
    
    // DOM 업데이트 강제 대기
    await this.$nextTick();
    
    // 성공 메시지 즉시 표시
    if (newLikedState) {
      this.$message.success('좋아요를 눌렀습니다');
    } else {
      this.$message.info('좋아요를 취소했습니다');
    }
    
    try {
      // 백엔드에 요청
      await toggleSpacePostLike(this.spaceId, postId);
    } catch (error: any) {
      // 에러 발생 시 UI 롤백
      this.$set(this.likedPosts, postId, isCurrentlyLiked);
      this.$set(this.likeCounts, postId, currentCount);
      
      // 게시글 배열에서도 롤백
      if (targetPost) {
        this.$set(targetPost, 'isLiked', isCurrentlyLiked);
        this.$set(targetPost, 'likeCount', currentCount);
      }
      
      // 강제 리렌더링
      this.$forceUpdate();
      
      // DOM 업데이트 강제
      await this.$nextTick();
      
      console.error('Failed to toggle like:', error);
      const message = error.response?.data?.message || '좋아요 처리에 실패했습니다.';
      this.$message.error(message);
    }
  }

  private isLiked(postId: string): boolean {
    return this.likedPosts[postId] || false;
  }

  private getLikeCount(postId: string): number {
    return this.likeCounts[postId] || 0;
  }

  // 날짜 포맷 함수
  private formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}. ${month}. ${day}.`;
  }

  // 첨부파일 URL 생성
  private getAttachmentUrl(attachmentUid: string): string {
    // 백엔드에서 UID만 전송하므로 /api/attached-file/{uid} 형식으로 변환
    return `/api/attached-file/${attachmentUid}`;
  }

  // 댓글 작성
  private async submitComment(postUid: string) {
    const content = this.commentInputs[postUid];
    if (!content || !content.trim()) {
      this.$message.warning('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      await createSpacePostComment(this.spaceId, postUid, content.trim());
      this.$message.success('댓글이 작성되었습니다.');
      
      // 입력창 초기화
      this.$set(this.commentInputs, postUid, '');
      
      // 댓글 목록 다시 로드
      await this.loadComments(postUid);
      
      // 게시글의 댓글 수 업데이트
      const post = this.posts.find(p => p.uid === postUid);
      if (post) {
        post.commentCount++;
      }
    } catch (error: any) {
      console.error('Failed to submit comment:', error);
      const message = error.response?.data?.message || '댓글 작성에 실패했습니다.';
      this.$message.error(message);
    }
  }

  // 게시글 액션 처리 (수정/삭제)
  private async handlePostAction(command: any) {
    const { action, post } = command;
    
    if (action === 'edit') {
      // 게시글 수정 모달 열기
      this.editingPost = post;
      this.editingPostUid = post.uid;
      this.writeFormVisible = true;
    } else if (action === 'delete') {
      try {
        await this.$confirm('이 게시글을 삭제하시겠습니까?', '게시글 삭제', {
          confirmButtonText: '삭제',
          cancelButtonText: '취소',
          type: 'warning',
        });
        
        await deleteSpacePost(this.spaceId, post.uid);
        this.$message.success('게시글이 삭제되었습니다.');
        
        // 게시글 목록 다시 로드
        await this.loadPosts();
      } catch (error: any) {
        if (error !== 'cancel') {
          console.error('Failed to delete post:', error);
          const message = error.response?.data?.message || '게시글 삭제에 실패했습니다.';
          this.$message.error(message);
        }
      }
    }
  }

  // 댓글 액션 처리 (수정/삭제)
  private async handleCommentAction(command: any) {
    const { action, comment, postUid } = command;
    
    if (action === 'edit') {
      // 댓글 수정 모드 활성화
      this.$set(this.commentEditMode, comment.uid, true);
      this.$set(this.editingCommentContent, comment.uid, comment.content);
    } else if (action === 'delete') {
      try {
        await this.$confirm('이 댓글을 삭제하시겠습니까?', '댓글 삭제', {
          confirmButtonText: '삭제',
          cancelButtonText: '취소',
          type: 'warning',
        });
        
        await deleteSpacePostComment(this.spaceId, postUid, comment.uid);
        this.$message.success('댓글이 삭제되었습니다.');
        
        // 댓글 목록 다시 로드
        await this.loadComments(postUid);
        
        // 게시글의 댓글 수 업데이트
        const post = this.posts.find(p => p.uid === postUid);
        if (post && post.commentCount > 0) {
          post.commentCount--;
        }
      } catch (error: any) {
        if (error !== 'cancel') {
          console.error('Failed to delete comment:', error);
          const message = error.response?.data?.message || '댓글 삭제에 실패했습니다.';
          this.$message.error(message);
        }
      }
    }
  }

  // 댓글 수정 저장
  private async saveCommentEdit(postUid: string, commentUid: string) {
    const content = this.editingCommentContent[commentUid];
    if (!content || !content.trim()) {
      this.$message.warning('댓글 내용을 입력해주세요.');
      return;
    }

    try {
      await updateSpacePostComment(this.spaceId, postUid, commentUid, content.trim());
      this.$message.success('댓글이 수정되었습니다.');
      
      // 수정 모드 해제
      this.$set(this.commentEditMode, commentUid, false);
      this.$delete(this.editingCommentContent, commentUid);
      
      // 댓글 목록 다시 로드
      await this.loadComments(postUid);
    } catch (error: any) {
      console.error('Failed to update comment:', error);
      const message = error.response?.data?.message || '댓글 수정에 실패했습니다.';
      this.$message.error(message);
    }
  }

  // 댓글 수정 취소
  private cancelCommentEdit(commentUid: string) {
    this.$set(this.commentEditMode, commentUid, false);
    this.$delete(this.editingCommentContent, commentUid);
  }

  private async handleInvite() {
    this.$message.info('초대 기능은 준비 중입니다.');
  }

  private openWriteModal() {
    // 게시글 작성 모달 열기
    this.editingPost = null;
    this.editingPostUid = '';
    this.writeFormVisible = true;
  }

  private handleDialogOpen() {
    // 다이얼로그 열림 이벤트
  }

  private handleCancelWrite() {
    // 작성 취소
    this.writeFormVisible = false;
    this.editingPostUid = '';
    this.editingPost = null;
  }

  private async handleWriteSuccess() {
    // 작성/수정 성공
    this.writeFormVisible = false;
    this.editingPostUid = '';
    this.editingPost = null;
    
    // 게시글 목록 다시 로드
    await this.loadPosts();
  }

  private openInviteModal() {
    // 공개 공간인 경우 초대 불가
    if (this.isPublic) {
      this.$message.info('공개 공간은 초대 기능을 사용할 수 없습니다. 커뮤니티의 모든 멤버가 자동으로 참여합니다.');
      return;
    }
    
    // 관리자가 아닌 경우 초대 불가
    if (!this.isAdmin) {
      this.$message.warning('공간 관리자만 멤버를 초대할 수 있습니다.');
      return;
    }
    
    console.log('openInviteModal called', {
      spaceId: this.spaceId,
      isAdmin: this.isAdmin,
      isPublic: this.isPublic,
      inviteModalVisible: this.inviteModalVisible
    });
    this.inviteModalVisible = true;
    console.log('Modal visible set to:', this.inviteModalVisible);
  }

  private async handleInvited() {
    this.$message.success('초대가 완료되었습니다.');
    this.inviteModalVisible = false;
    // 참여자 목록 다시 로드
    await this.loadParticipants();
  }
}
</script>

<style scoped lang="scss">
.community-space-page {
  display: flex;
  gap: 0;
  min-height: calc(100vh - 120px);
  background: #F8F9FB;
  align-items: flex-start;
}

.space-main {
  padding-bottom: 100px;
  position: relative;
  margin-left: 270px; /* Sidebar width */
  padding: 120px 40px 100px 40px;
  padding-left: 40px;
  padding-right: 40px;
}

.space-header {
  text-align: left;
  position: relative;
  z-index: 100;
  padding-top: 40px;
}

.space-name-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 20px;

  svg {
    width: 16px;
    height: 16px;
    flex-shrink: 0;
  }

  span {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    line-height: 20px;
  }

  .space-visibility-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
    margin-left: 8px;
    
    &.public-badge {
      background-color: #E8F5E9;
      color: #2E7D32;
    }
    
    &.private-badge {
      background-color: #FFF3E0;
      color: #E65100;
    }
  }
}

.posts-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.post-card {
  background: #FFF;
  border: 2px solid #EBEBEB;
  border-radius: 10px;
  padding: 40px;
  position: relative;
display: flex;
    flex-direction: column;
    gap: 50px;
  &.notice-post {
    min-height: 242px;
  }
}

.post-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.post-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.post-date {
  margin-right: 30px;
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

.post-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 140%;
  word-break: break-word;
}

.post-text-content {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;
  text-overflow: ellipsis;

  &.expanded {
    display: block;
    -webkit-line-clamp: unset;
    line-clamp: unset;
    overflow: visible;
  }
}

.text-preview {
  color: #222;
}

.read-more-link {
  color: #073DFF;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.post-images {
  display: flex;
  gap: 30px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar {
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #F5F5F5;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #CECECE;
    border-radius: 4px;

    &:hover {
      background: #999;
    }
  }
}

.post-image {
  width: 419px;
  height: 220px;
  border-radius: 10px;
  object-fit: contain;
  flex-shrink: 0;
}

.post-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    opacity: 0.8;
  }

  svg {
    transition: all 0.2s ease;
  }

  .like-icon {
    fill: #CECECE;
    transition: fill 0.2s ease;
  }

  &.liked {
    .stat-count {
      color: #073DFF;
      font-weight: 600;
    }
    
    .like-icon {
      fill: #073DFF;
    }
  }

  &.active {
    .stat-count {
      color: #073DFF;
    }
  }
}

.stat-count {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
}

.stat-divider {
  width: 1px;
  height: 14px;
  background: #D9D9D9;
}

.notice-badge {
  position: absolute;
  right: -4px;
  top: 25px;
  width: 100px;
  height: 38px;
}

.badge-text {
  position: absolute;
  left: 19px;
  top: 9px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.comments-section {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .user-avatar {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    
    .user-avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .comment-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

.comment-date {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

.comment-text {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &.expanded {
    white-space: normal;
    overflow: visible;
  }
}

.comment-input-area {
  display: flex;
  align-items: center;
  gap: 20px;
}

.comment-edit-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.comment-edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.comment-input {
  flex: 1;
  height: 52px;
  padding: 0 16px;
  border: 1px solid #CECECE;
  border-radius: 10px;
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 400;
  line-height: 20px;
  outline: none;
  transition: border-color 0.2s;

  &::placeholder {
    color: #888;
  }

  &:focus {
    border-color: #073DFF;
  }
}

.comment-submit-btn {
  width: 136px;
  height: 52px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #0530CC;
  }

  &:active {
    background: #042099;
  }

  &:disabled {
    background: #CECECE;
    cursor: not-allowed;
  }
}

// 로딩 상태
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  gap: 20px;
  
  i {
    font-size: 48px;
    color: #073DFF;
  }
  
  span {
    color: #888;
    font-size: 18px;
  }
}

// 빈 상태
.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  gap: 10px;
  
  p {
    color: #888;
    font-size: 18px;
    margin: 0;
    
    &:first-child {
      font-size: 20px;
      font-weight: 600;
      color: #666;
    }
  }
}

.empty-comments {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  
  p {
    color: #888;
    font-size: 16px;
    margin: 0;
  }
}

// 게시글/댓글 액션 버튼
.post-actions,
.comment-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.el-dropdown-link {
  cursor: pointer;
  color: #888;
  font-size: 20px;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
  
  &:hover {
    color: #222;
  }
  
  i {
    font-size: 20px;
  }
}

// Floating Write Button (initially hidden on desktop, shown on mobile)
.write-post-btn-fixed {
  position: fixed;
  left: 30px;
  bottom: 40px;
  width: 210px;
  height: 50px;
  padding: 0;
  background: #073DFF;
  border: none;
  border-radius: 10px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 100;
  display: none; // Hidden on desktop (sidebar has write button)
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(7, 61, 255, 0.3);

  .btn-icon {
    display: none;
  }

  &:hover {
    background: #0530CC;
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(7, 61, 255, 0.4);
  }

  &:active {
    background: #042099;
    transform: translateY(0);
  }
}

@media screen and (max-width: 1600px) {
  .space-main {
    padding-left: 30px;
    padding-right: 30px;
  }

  .post-image {
    width: 320px;
    height: 180px;
  }
}

@media screen and (max-width: 1400px) {
  .space-main {
    padding-left: 20px;
    padding-right: 20px;
  }

  .post-image {
    width: 280px;
    height: 160px;
  }
}

@media screen and (max-width: 1200px) {
  .space-main {
    padding-left: 20px;
    padding-right: 20px;
  }

  .post-image {
    width: 240px;
    height: 140px;
  }
}

@media screen and (max-width: 1024px) {
  .posts-container {
    padding: 20px 0;
  }
  .space-main {
    margin-left: 240px; /* Remove sidebar margin on mobile */
    padding-left: 20px;
    padding-right: 20px;
  }

  .space-header {
    padding-top: 30px;
  }

  .post-image {
    width: 220px;
    height: 130px;
  }

  // Show floating write button on mobile (circular style)
  .write-post-btn-fixed {
    display: flex; // Show button on mobile
    width: 50px;
    height: 50px;
    border-radius: 50%;
    left: auto;
    right: 24px;
    bottom: 24px;

    .btn-text {
      display: none;
    }

    .btn-icon {
      display: block;
    }
  }
}

@media screen and (max-width: 768px) {
  .community-space-page {
    display: block;
    padding-top: 80px;
  }

  .space-main {
    margin-left: 0;
    border: none;
    padding: 30px 0 0;
  }

  .space-header {
    padding: 20px 20px 16px;
    border-left: 2px solid #EBEBEB;
    border-right: 2px solid #EBEBEB;
  }

  .space-name-tag {
    span {
      font-size: 16px;
    }

    svg {
      width: 14px;
      height: 14px;
    }

    .space-visibility-badge {
      font-size: 10px;
      padding: 3px 8px;
      margin-left: 6px;
    }
  }

  .posts-container {
    padding: 0 20px;
    border-left: 2px solid #EBEBEB;
    border-right: 2px solid #EBEBEB;
  }

  .post-card {
    padding: 30px 20px;
    border-bottom: 2px solid #EBEBEB;

    &:first-child {
      padding-top: 20px;
    }
  }

  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .post-title {
    font-size: 20px;
  }

  .post-text-content,
  .comment-text {
    font-size: 18px;
  }

  .stat-count {
    font-size: 18px;
  }

  .comment-input {
    font-size: 16px;
  }

  .post-image {
    width: 200px;
    height: 120px;
  }

  .comment-submit-btn {
    width: 120px;
    font-size: 16px;
  }
}

@media screen and (max-width: 600px) {
  .space-header {
    padding: 16px 16px 12px;
  }

  .space-name-tag {
    span {
      font-size: 14px;
    }

    svg {
      width: 12px;
      height: 12px;
    }
  }

  .posts-container {
    padding: 0 16px;
  }

  .post-title {
    font-size: 18px;
  }

  .post-text-content,
  .comment-text {
    font-size: 16px;
  }

  .stat-count {
    font-size: 16px;
  }

  .user-name {
    font-size: 18px;
  }

  .post-date,
  .comment-date {
    font-size: 14px;
  }

  .comment-input {
    font-size: 14px;
    height: 48px;
  }

  .post-images {
    gap: 20px;
  }

  .post-image {
    width: 180px;
    height: 100px;
  }

  .comment-submit-btn {
    width: 100px;
    height: 48px;
    font-size: 15px;
  }

  .notice-badge {
    width: 80px;
    height: 30px;
    right: 0;

    svg {
      width: 80px;
      height: 30px;
    }
  }

  .badge-text {
    font-size: 16px;
    left: 14px;
    top: 6px;
  }
}


@media screen and (max-width: 500px) {
  .space-main {padding: 20px 0; }
}

/* Element UI Message z-index fix */
::v-deep .el-message {
  z-index: 9999 !important;
  top: 20px;
}

/* Write Dialog Styles - Matching community.vue exactly */
.el-dialog {
  width: clamp(60vw, 2vw, 80px);
}
::v-deep .community-post-form.dialog-wrap {
  .el-dialog {
    // max-width: 1200px;
    width: clamp(60vw, 2vw, 80px);
    border-radius: 10px;
  }

  .el-dialog__header {
    padding: 30px 30px 0;
    border-bottom: none;

    .el-dialog__title {
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 24px;
      font-weight: 700;
      line-height: 1.2;
    }
  }

  .el-dialog__body {
    padding: 20px 30px 30px;
  }

  .el-dialog__close {
    font-size: 24px;
    color: #888;
    
    &:hover {
      color: #222;
    }
  }

  /* SummerNote Editor Styles */
  .note-editor {
    border: 1px solid #DCDFE6;
    border-radius: 6px;
    
    &.note-frame {
      border-color: #DCDFE6;
    }

    .note-toolbar {
      background-color: #F5F7FA;
      border-bottom: 1px solid #DCDFE6;
      padding: 10px 15px;
    }

    .note-editable {
      min-height: 300px;
      padding: 15px;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 16px;
      line-height: 1.6;
      color: #222;

      &:focus {
        background-color: #FFF;
      }
    }

    .note-statusbar {
      background-color: #F5F7FA;
      border-top: 1px solid #DCDFE6;
    }
  }
}

/* Responsive dialog */
@media screen and (max-width: 1024px) {
  ::v-deep .community-post-form.dialog-wrap {
    .el-dialog {
      width: 95%;
      max-width: none;
    }

    .el-dialog__header {
      padding: 20px 20px 0;

      .el-dialog__title {
        font-size: 20px;
      }
    }

    .el-dialog__body {
      padding: 15px 20px 25px;
    }
  }
}

@media screen and (max-width: 768px) {
  ::v-deep .community-post-form.dialog-wrap {
    .el-dialog {
      width: 100%;
      margin: 0 !important;
      border-radius: 0;
      min-height: 100vh;
    }

    .el-dialog__header {
      padding: 16px 16px 0;

      .el-dialog__title {
        font-size: 18px;
      }
    }

    .el-dialog__body {
      padding: 12px 16px 20px;
    }

    .note-editor .note-editable {
      min-height: 200px;
      font-size: 15px;
    }
  }
}
</style>
