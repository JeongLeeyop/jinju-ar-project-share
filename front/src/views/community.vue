<template>
  <div class="community-page" v-loading.fullscreen.lock="fullscreenLoading">
    <!-- Left Sidebar -->
    <CommunitySidebar @create-space="handleCreateSpace" @space-select="handleSpaceSelect" />

    <!-- Main Content Area -->
    <div class="community-main">
      <!-- Hero Banner -->
      <div class="hero-banner">
        <template v-if="selectedChannel && selectedChannel.coverImageList && selectedChannel.coverImageList.length > 0">
          <!-- 데스크탑: 캐러셀 -->
          <el-carousel v-if="selectedChannel.coverImageList.length > 1" indicator-position="outside" height="350px" class="desktop-carousel">
            <el-carousel-item v-for="item in selectedChannel.coverImageList" :key="item.uid">
              <img :src="`${apiUrl}/attached-file/${item.fileUid}`" alt="Community Banner" class="banner-image" />
            </el-carousel-item>
          </el-carousel>
          <!-- 단일 이미지인 경우 -->
          <img v-else :src="bannerImageUrl" alt="Community Banner" class="banner-image" @error="handleBannerError" />
        </template>
        <!-- 커버 이미지가 없는 경우 기본 배경색만 표시 -->
      </div>

      <!-- Empty State -->
      <div v-if="!listLoading && !fullscreenLoading && postList.length === 0" class="empty-state">
        <h1 class="welcome-title">진주알 커뮤니티에 오신 걸 환영합니다!</h1>
        <p class="welcome-subtitle">커뮤니티 회원분들의 모든 게시글을 확인할 수 있는 홈 화면 입니다.</p>
        <button class="create-post-btn" @click="handlewriteFormVisible">게시글 작성하기</button>
      </div>

      <!-- Posts List -->
      <div v-else-if="postList.length > 0" class="posts-container" v-loading="listLoading">
        <div v-for="post in postList" :key="post.uid" class="post-card">
          <div class="post-content-wrapper" @click="postDetail(post.uid, post.noticeStatus)">
            <!-- User Info -->
            <div class="post-header">
              <div class="user-info">
                <div class="user-avatar">
                  <img v-if="post.iconFileUid" :src="`${apiUrl}/attached-file/${post.iconFileUid}`" alt="프로필 이미지" class="user-avatar-img">
                  <svg v-else width="36" height="36" viewBox="0 0 36 36" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    <mask id="mask0" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="0" y="0" width="36" height="36">
                      <circle cx="18" cy="18" r="18" fill="#D9D9D9"/>
                    </mask>
                    <g mask="url(#mask0)">
                      <rect x="4" y="21" width="28" height="32" rx="14" fill="#F5F5F5"/>
                      <circle cx="18" cy="11" r="7" fill="#F5F5F5"/>
                    </g>
                  </svg>
                </div>
                <span class="user-name">{{ post.writer }}</span>
              </div>
              <div class="post-header-right">
                <span class="post-date">{{ post.createDate | parseDate('YYYY. MM. DD.') }}</span>
                <!-- 더보기 버튼 (작성자 또는 채널 관리자 표시) -->
                <el-dropdown v-if="post.myStatus || isChannelManager" trigger="click" @command="handlePostAction">
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

            <!-- Post Content -->
            <div class="post-body">
              <!-- 제목 -->
              <!-- <div v-if="post.title" class="post-title">{{ post.title }}</div> -->
              
              <div class="post-text summernote-content" :class="{ 'expanded': isExpanded(post.uid) }" :ref="`postText-${post.uid}`">
                <div class="content-preview" v-html="post.content"></div>
                <span class="read-more" v-if="isContentOverflowing(post) && !isExpanded(post.uid)" @click.stop="toggleExpand(post.uid)">더보기</span>
                <span class="read-less" v-if="isExpanded(post.uid)" @click.stop="toggleExpand(post.uid)">접기</span>
              </div>

              <!-- Post Images -->
              <div v-if="post.fileList && post.fileList.length > 0" class="post-images">
                <!-- 이미지가 2장 이상일 때 캐러셀 사용 -->
                <el-carousel v-if="post.fileList.length >= 2" height="220px" indicator-position="outside" arrow="always" class="post-image-carousel">
                  <el-carousel-item v-for="(fileUid, index) in post.fileList" :key="fileUid">
                    <div class="carousel-item-wrapper">
                      <img
                        :src="`${apiUrl}/attached-file/${fileUid}`"
                        :alt="`Image ${index + 1}`"
                        class="post-image"
                      />
                    </div>
                  </el-carousel-item>
                </el-carousel>
                <!-- 이미지가 1장일 때 일반 표시 -->
                <img
                  v-else
                  :src="`${apiUrl}/attached-file/${post.fileList[0]}`"
                  alt="Image"
                  class="post-image single-image"
                />
              </div>

              <!-- Engagement Stats -->
              <div class="post-stats">
                <div class="stat-item" @click.stop="handleLikePost(post)" style="cursor: pointer;">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M7.49301 21.5C7.06801 21.5 6.67301 21.264 6.51801 20.868C6.17475 19.9943 5.99902 19.0637 6.00001 18.125C6.00001 16.375 6.59901 14.767 7.60201 13.491C7.75301 13.299 7.97501 13.182 8.20201 13.094C8.67501 12.911 9.09201 12.58 9.41401 12.17C10.1903 11.1796 11.1647 10.3622 12.275 9.77C12.998 9.386 13.625 8.814 13.928 8.055C14.1408 7.52325 14.2501 6.95575 14.25 6.383V5.75C14.25 5.55109 14.329 5.36032 14.4697 5.21967C14.6103 5.07902 14.8011 5 15 5C15.5967 5 16.169 5.23705 16.591 5.65901C17.013 6.08097 17.25 6.65326 17.25 7.25C17.25 8.402 16.99 9.493 16.527 10.468C16.261 11.026 16.634 11.75 17.252 11.75H20.378C21.404 11.75 22.323 12.444 22.432 13.465C22.477 13.887 22.5 14.315 22.5 14.75C22.5041 17.4863 21.5691 20.1412 19.851 22.271C19.463 22.753 18.864 23 18.246 23H14.23C13.747 23 13.266 22.922 12.807 22.77L9.69301 21.73C9.23411 21.5774 8.75362 21.4997 8.27001 21.5H7.49301ZM2.33101 13.727C1.77993 15.1277 1.498 16.6198 1.50001 18.125C1.49875 19.3133 1.67399 20.4952 2.02001 21.632C2.28001 22.482 3.10501 23 3.99401 23H4.90001C5.34501 23 5.62001 22.502 5.42301 22.102C4.81315 20.8651 4.49695 19.5041 4.49901 18.125C4.49901 16.417 4.97501 14.82 5.80101 13.459C6.04601 13.056 5.77301 12.5 5.30101 12.5H4.25001C3.41801 12.5 2.63701 12.953 2.33101 13.727Z" :fill="post.likeStatus ? '#073DFF' : '#CECECE'"/>
                  </svg>
                  <span :class="{ 'active-stat': post.likeStatus }">{{ post.likeCount || 0 }}</span>
                </div>
                <div class="stat-divider"></div>
                <div class="stat-item" @click.stop="toggleComments(post.uid)" style="cursor: pointer;">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd" clip-rule="evenodd" d="M4.848 2.771C7.21613 2.4234 9.60649 2.24927 12 2.25C14.43 2.25 16.817 2.428 19.152 2.77C21.13 3.062 22.5 4.794 22.5 6.74V12.76C22.5 14.706 21.13 16.438 19.152 16.73C17.9983 16.8991 16.8389 17.0268 15.676 17.113C15.6168 17.1169 15.5593 17.1342 15.5079 17.1637C15.4564 17.1931 15.4123 17.234 15.379 17.283L12.624 21.416C12.5555 21.5187 12.4627 21.6029 12.3539 21.6612C12.245 21.7194 12.1235 21.7499 12 21.7499C11.8765 21.7499 11.755 21.7194 11.6461 21.6612C11.5373 21.6029 11.4445 21.5187 11.376 21.416L8.621 17.283C8.58768 17.234 8.54361 17.1931 8.49214 17.1637C8.44068 17.1342 8.38317 17.1169 8.324 17.113C7.16113 17.0265 6.00172 16.8984 4.848 16.729C2.87 16.439 1.5 14.705 1.5 12.759V6.741C1.5 4.795 2.87 3.061 4.848 2.771ZM6.75 8.25C6.75 8.05109 6.82902 7.86032 6.96967 7.71967C7.11032 7.57902 7.30109 7.5 7.5 7.5H16.5C16.6989 7.5 16.8897 7.57902 17.0303 7.71967C17.171 7.86032 17.25 8.05109 17.25 8.25C17.25 8.44891 17.171 8.63968 17.0303 8.78033C16.8897 8.92098 16.6989 9 16.5 9H7.5C7.30109 9 7.11032 8.92098 6.96967 8.78033C6.82902 8.63968 6.75 8.44891 6.75 8.25ZM7.5 10.5C7.30109 10.5 7.11032 10.579 6.96967 10.7197C6.82902 10.8603 6.75 11.0511 6.75 11.25C6.75 11.4489 6.82902 11.6397 6.96967 11.7803C7.11032 11.921 7.30109 12 7.5 12H12C12.1989 12 12.3897 11.921 12.5303 11.7803C12.671 11.6397 12.75 11.4489 12.75 11.25C12.75 11.0511 12.671 10.8603 12.5303 10.7197C12.3897 10.579 12.1989 10.5 12 10.5H7.5Z" :fill="showCommentsForPost[post.uid] ? '#073DFF' : '#CECECE'"/>
                  </svg>
                  <span :class="{ 'active-stat': showCommentsForPost[post.uid] }">{{ post.commentCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Notice Badge -->
          <div v-if="post.noticeStatus" class="notice-badge">
            <svg width="100" height="38" viewBox="0 0 100 38" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M0 0H100V38H0L9.16667 19L0 0Z" fill="#073DFF"/>
            </svg>
            <span class="notice-text">공지사항</span>
          </div>

          <!-- Comments Section -->
          <div class="comments-section" v-if="showCommentsForPost[post.uid]">
            <Comment
              :postUid="post.uid"
              :boardUid="boardUid"
              :channelUid="channelUid"
              @refresh="handleCommentChange(post)"
              @comment-count-change="(delta) => updateCommentCount(post, delta)"
            />
          </div>
        </div>

        <!-- Load More -->
        <infinite-loading v-if="!listLoading && postList.length > 0 && !isNoMore" @infinite="handleMorePostList" spinner="waveDots"></infinite-loading>
      </div>

      <!-- Write Button (Fixed) -->
      <button class="write-post-btn-fixed" @click="handlewriteFormVisible">
        <span class="btn-text">글 작성하기</span>
        <svg class="btn-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 5V19M5 12H19" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </button>
    </div>

    <!-- Write Post Dialog -->
    <el-dialog :title="updatePost.uid ? '글 수정' : '글 작성'" class="community-post-form dialog-wrap" :visible.sync="writeFormVisible" @open="handleDialogOpen">
      <Write v-if="writeFormVisible" :postUid="updatePost.uid" :post="updatePost" :board="board" :channelUid="channelUid" :boardUseCategoryList="boardUseCategoryList" @cancel="handleCancel" @success="handleChangeStatus()" />
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { getCommunityBoard } from '@/api/board';
import { getCommunityCategory } from '@/api/boardCategory';
import {
  getPostList,
  deletePost,
  updatePost,
  getPost,
} from '@/api/post';
import { likePost } from '@/api/postLike';
import Pagination from '@/components/Pagination/index.vue';
import { UserModule } from '@/store/modules/user';
import { ChannelModule } from '@/store/modules/channel';
import { ChannelPermissionModule } from '@/store/modules/channelPermission';
import { IBoard, IField } from '@/types/board';
import InfiniteLoading from 'vue-infinite-loading';
import { Component, Vue, Watch } from 'vue-property-decorator';
import Write from './components/write.vue';
import Comment from './components/Comment.vue';
import CommunitySidebar from './components/communitySidebar.vue';

@Component({
  name: 'Community',
  components: {
    Write,
    Comment,
    InfiniteLoading,
    Pagination,
    CommunitySidebar,
  },
})
export default class extends Vue {
  async mounted() {
    this.fullscreenLoading = true;
    this.listQuery.boardUid = this.boardUid;
    this.listQuery.channelUid = this.channelUid;
    
    // 채널 권한 로드
    if (this.channelUid) {
      await ChannelPermissionModule.loadPermissions(this.channelUid);
      // 채널 관리자 여부 업데이트
      this.isChannelManager = ChannelPermissionModule.isChannelAdmin;
      console.log('✅ Community 권한 로드 완료 - isChannelAdmin:', this.isChannelManager);
      this.$forceUpdate();
    }
    
    this.getPostList();
    this.getCommunityBoard();
  }

  @Watch('$route.query')
  private handleHeaderSearchEvent() {
    if (this.categoryList && this.categoryList.length > 0) this.listQuery.categoryList = this.categoryList.join(',');
    if (this.listQuery.categoryList === 'all') {
      this.listQuery = {
        ...this.listQuery,
        categoryList: '',
      };
    }
    this.listQuery.page = 0;
    this.listQuery.searchValue = this.$route.query.searchValue;
    this.getPostList();
  }

  private fullscreenLoading = false;

  private boardUid = process.env.VUE_APP_COMMUNITY_BOARD_UID;

  private channelUid = ChannelModule.selectedChannel.uid;

  private writeFormVisible = false;

  private boardUseCategoryList = [];

  private boardFieldList: IField[] = [];

  private board: IBoard | null = null;

  private selectedChannel = ChannelModule.selectedChannel;

  // ✅ 채널 관리자 여부 (로컬 data로 관리)
  private isChannelManager = false;

  // ✅ apiUrl을 computed getter로 변경하여 템플릿에서 접근 가능하도록 수정
  get apiUrl() {
    return process.env.VUE_APP_COMMON_API || '/api';
  }

  private listQuery: any = {
    typeUid: '',
    boardUid: '',
    channelUid: '',
    categoryList: [],
    searchType: 'content',
    searchValue: this.$route.query.searchValue ? this.$route.query.searchValue : '',
    page: 1,
    size: 10,
    sort: 'notice',  // ✅ 공지사항 정렬 활성화
  }

  private favoriteFlag = false;

  private async handleLikePost(post: any) {
    if (this.favoriteFlag) {
      this.$message.info('이전 동작이 실행 중 입니다.');
      return;
    }
    
    if (!UserModule.token) {
      this.$message.info('로그인이 필요한 서비스입니다.');
      return;
    }

    this.favoriteFlag = true;
    
    // 현재 상태 저장 (에러 시 롤백용)
    const isCurrentlyLiked = post.likeStatus || false;
    const currentCount = post.likeCount || 0;
    const currentIdx = post.likeIdx;
    
    try {
      // 즉시 UI 업데이트 (낙관적 업데이트)
      post.likeStatus = !isCurrentlyLiked;
      post.likeCount = isCurrentlyLiked ? currentCount - 1 : currentCount + 1;
      
      // 강제 리렌더링
      this.$forceUpdate();
      await this.$nextTick();
      
      // 백엔드에 요청
      const response = await likePost(post.uid);
      
      if (!isCurrentlyLiked) {
        // 좋아요 추가
        post.likeIdx = response.data;
        this.$message.success('좋아요를 눌렀습니다');
      } else {
        // 좋아요 취소
        post.likeIdx = null;
        this.$message.info('좋아요를 취소했습니다');
      }
    } catch (error: any) {
      // 에러 발생 시 UI 롤백
      post.likeStatus = isCurrentlyLiked;
      post.likeCount = currentCount;
      post.likeIdx = currentIdx;
      
      this.$forceUpdate();
      await this.$nextTick();
      
      console.error('Failed to toggle like:', error);
      const message = error.response?.data?.message || '좋아요 처리에 실패했습니다.';
      this.$message.error(message);
    } finally {
      setTimeout(() => {
        this.favoriteFlag = false;
      }, 500);
    }
  }

  private listLoading = false;

  private isNoMore = false;

  private postList = [];

  private totalElements: number = 0;

  private contentOverflowMap: { [key: string]: boolean } = {};

  private expandedPosts: { [key: string]: boolean } = {};

  private showCommentsForPost: { [key: string]: boolean } = {};

  private selectedPostUid = '';

  private selectedNoticePostUid = '';

  private updatePost: any = {};

  private isContentOverflowing(post: any) {
    // Check if we already calculated this
    if (this.contentOverflowMap[post.uid] !== undefined) {
      return this.contentOverflowMap[post.uid];
    }

    // Check if content length suggests it might overflow
    const stripped = post.content ? post.content.replace(/<[^>]*>/g, '') : '';
    const isOverflow = stripped.length > 150; // Approximate character count for 3 lines

    this.contentOverflowMap[post.uid] = isOverflow;
    return isOverflow;
  }

  private isExpanded(postUid: string) {
    return this.expandedPosts[postUid] || false;
  }

  private toggleExpand(postUid: string) {
    this.$set(this.expandedPosts, postUid, !this.expandedPosts[postUid]);
  }

  private toggleComments(postUid: string) {
    this.$set(this.showCommentsForPost, postUid, !this.showCommentsForPost[postUid]);
  }

  /**
   * 댓글 변경 시 해당 게시글의 댓글 수만 업데이트
   */
  private async handleCommentChange(post: any) {
    try {
      // 해당 게시글의 최신 정보만 가져와서 댓글 수 업데이트
      const res = await getPost(post.uid);
      const updatedPost = res.data;
      
      // postList에서 해당 게시글 찾아서 댓글 수만 업데이트
      const postIndex = this.postList.findIndex((p: any) => p.uid === post.uid);
      if (postIndex !== -1) {
        this.$set(this.postList[postIndex], 'commentCount', updatedPost.commentCount || 0);
      }
    } catch (error) {
      console.error('Failed to update comment count:', error);
      // 에러 발생 시 전체 목록 새로고침
      this.getPostList();
    }
  }

  private postDetail(uid: any, noticeStatus: any) {
    if (noticeStatus) {
      this.selectedPostUid = '';
      this.selectedNoticePostUid = uid;
    } else {
      this.selectedNoticePostUid = '';
      this.selectedPostUid = uid;
    }
  }

  private getCommunityBoard() {
    return new Promise((resolve) => {
      getCommunityBoard(this.boardUid).then((res) => {
        this.board = res.data;
        this.boardFieldList = res.data.fieldList;
        this.listLoading = false;
        resolve('');
      });
      getCommunityCategory({ boardUid: this.boardUid, channelUid: this.channelUid }).then((res) => {
        this.boardUseCategoryList = res.data;
        this.listLoading = false;
        resolve('');
      });
    });
  }

  private categoryList = ['all'];

  private handleSearch() {
    if (this.categoryList && this.categoryList.length > 0) this.listQuery.categoryList = this.categoryList.join(',');
    if (this.listQuery.categoryList === 'all') {
      this.listQuery = {
        ...this.listQuery,
        categoryList: '',
      };
    }
    this.listQuery.page = 0;
    this.getPostList();
  }

  private getPostList() {
    this.listLoading = true;
    getPostList(this.listQuery).then((res) => {
      this.listLoading = false;
      this.fullscreenLoading = false;
      const filteredContent = res.data.content.filter((list: any) => list.parentUid == null);
      this.postList = filteredContent;
      this.totalElements = res.data.totalElements;
      // Reset overflow map for new posts
      this.contentOverflowMap = {};
    }).catch((error) => {
      // ✅ 에러 발생 시에도 로딩 상태 해제
      this.listLoading = false;
      this.fullscreenLoading = false;
      console.error('Failed to load post list:', error);
      this.$message.error('게시글 목록을 불러오는데 실패했습니다.');
    });
  }

  private handleMorePostList($state: any) {
    this.listQuery.page += 1;
    setTimeout(() => {
      getPostList(this.listQuery).then((res) => {
        const filteredContent = res.data.content.filter((list: any) => list.parentUid == null);
        this.postList = this.postList.concat(filteredContent);
        this.totalElements = res.data.totalElements;
        if (res.data.content.length > 0) {
          $state.loaded();
        } else {
          this.isNoMore = true;
          $state.complete();
        }
      }).catch((error) => {
        this.$message.error('정보를 불러오는데 실패했습니다.');
        this.listLoading = false;
        $state.complete();
      });
    }, 1000);
  }

  // 댓글 수 업데이트 (게시글 목록 새로고침 없이)
  private updateCommentCount(post: any, delta: number) {
    post.commentCount = (post.commentCount || 0) + delta;
  }

  // 게시글 액션 처리 (수정/삭제)
  private async handlePostAction(command: any) {
    const { action, post } = command;
    
    if (action === 'edit') {
      // 게시글 수정 모달 열기
      await getPost(post.uid).then((res) => {
        this.updatePost = res.data;
        this.writeFormVisible = true;
      }).catch((error) => {
        const message = error.response?.data?.message || '게시글 정보를 불러오는데 실패했습니다.';
        this.$message.error(message);
      });
    } else if (action === 'delete') {
      // 게시글 삭제 확인
      this.$confirm('정말 게시글을 삭제하시겠습니까?', '게시글 삭제', {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'error',
      }).then(() => {
        deletePost(post.uid).then(() => {
          this.$message.success('게시글이 삭제되었습니다.');
          // 페이지 새로고침 대신 게시글 목록만 다시 로드
          this.listQuery.page = 1;
          this.isNoMore = false;
          this.getPostList();
        }).catch((error) => {
          const message = error.response?.data?.message || '게시글 삭제에 실패했습니다.';
          this.$message.error(message);
        });
      });
    }
  }

  private handlewriteFormVisible() {
    this.updatePost = {};
    this.writeFormVisible = true;
  }

  private handleDialogOpen() {
  }

  private handleChangeStatus(form: any) {
    this.writeFormVisible = false;
    // 페이지 새로고침 대신 게시글 목록만 다시 로드
    this.listQuery.page = 1; // ✅ 첫 페이지부터 다시 로드
    this.isNoMore = false; // ✅ 무한 스크롤 리셋
    this.getPostList();
  }

  private handleCancel() {
    this.writeFormVisible = false;
  }

  // ✅ 배너 이미지 URL을 채널 coverImageList에서 가져옴
  get bannerImageUrl(): string {
    if (this.selectedChannel?.coverImageList && this.selectedChannel.coverImageList.length > 0) {
      return `${this.apiUrl}/attached-file/${this.selectedChannel.coverImageList[0].fileUid}`;
    }
    return '';
  }

  private handleBannerError() {
    // 이미지 로드 실패 시 기본 배경색만 표시
  }

  private handleCreateSpace() {
    this.$message.info('공간 개설하기 기능은 준비중입니다.');
  }

  private handleSpaceSelect(spaceId: string) {
    this.$router.push({
      name: 'CommunitySpace',
      params: {
        domain: this.$route.params.domain || 'default',
        spaceId,
      },
    });
  }

  private getContentPreview(content: string, postUid: string) {
    if (!content) return '';
    const stripped = content.replace(/<[^>]*>/g, '');

    // If expanded, show full content
    if (this.isExpanded(postUid)) {
      return stripped;
    }

    // If content is long enough to overflow 3 lines, truncate and add ellipsis
    if (stripped.length > 150) {
      return `${stripped.substring(0, 150)}...`;
    }
    return stripped;
  }

  private convertFileList(fileList: any) {
    return fileList.map((item: any) => `${this.apiUrl}/attached-file/${item}`);
  }
}
</script>

<style scoped lang="scss">
// el-carousel 캐러셀 커스텀 스타일
.post-image-carousel {
  width: 100%;
  
  .carousel-item-wrapper {
    width: 100%;
    height: 220px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}

.community-page {
  display: flex;
  min-height: 100vh;
  background: #F8F9FB;
}

.community-main {
  flex: 1;
  min-width: 0;
  padding: 120px 40px 100px 40px;
  position: relative;
  margin-left: 270px;
}

.hero-banner {
  width: 100%;
  height: 400px;
  background: #D9D9D9;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: flex-end;
  position: relative;
  margin-left: -40px;
  margin-right: -40px;
  width: calc(100% + 80px);
}

.desktop-carousel {
  width: 100%;
  height: 100%;
  
  ::v-deep .el-carousel__container {
    height: 100% !important;
  }
  
  ::v-deep .el-carousel__item {
    height: 100%;
  }
  
  ::v-deep .el-carousel__indicators {
    bottom: 20px;
  }
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  border-radius: 0 0 24px 24px;
  position: absolute;
  top: 0;
  left: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 40px;
  text-align: center;
}

.welcome-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 52px;
  font-weight: 800;
  line-height: 100%;
  margin: 0 0 24px 0;
}

.welcome-subtitle {
  color: #083DFE;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 100%;
  margin: 0 0 60px 0;
}

.create-post-btn {
  display: inline-flex;
  padding: 24px 100px;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  background: #073DFF;
  border: none;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 28px;
  font-weight: 700;
  line-height: 100%;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #0530CC;
  }
}

.posts-container {
  padding: 40px;
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
  transition: border-color 0.2s;

  &:hover {
    border-color: #073DFF;
  }
}

.post-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 30px;
  cursor: pointer;
}

.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  width: 100%;
}

.post-header-right {
  margin-right: 60px;
  display: flex;
  justify-content: end;
  align-items: center;
  gap: 15px;
  margin-left: auto;
}

.user-avatar-img {height:100%;}

@media screen and (max-width: 1400px) {
  .posts-container {padding: 20px 0;}
}

@media screen and (max-width: 768px) {
  .post-header {display: block;}
  .post-header-right {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-right: 0px;
    margin-left: unset;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
}

.user-name {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.post-date {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 100%;
}

// 더보기 버튼 스타일
.el-dropdown-link {
  cursor: pointer;
  color: #888;
  font-size: 20px;
  padding: 4px 8px;
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

.post-body {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.post-title {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 24px;
  font-weight: 700;
  line-height: 140%;
  word-break: break-word;
}

.post-text {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  text-align: left;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  overflow: hidden;

  &.expanded {
    display: block;
    -webkit-line-clamp: unset;
    line-clamp: unset;
    overflow: visible;
  }
}

.content-preview {
  color: #222;
}

.read-more,
.read-less {
  color: #073DFF;
  font-weight: 700;
  margin-left: 8px;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }
}

.post-images {
  width: 100%;
  margin: 16px 0;
}

.post-image-slider {
  width: 100%;
  height: 220px;
}

.post-image {
  max-width: 100%;
  max-height: 220px;
  height: auto;
  width: auto;
  border-radius: 10px;
  object-fit: contain;
  display: block;
  
  &.single-image {
    display: block;
    margin: 0 auto;
  }
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
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 400;
  line-height: 150%;
  cursor: pointer;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.8;
  }

  span.active-stat {
    color: #073DFF;
    font-weight: 600;
  }
}

.stat-divider {
  width: 1px;
  height: 14px;
  background: #D9D9D9;
}

.comments-section {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.comment-box {
  display: flex;
  flex-direction: column;
  gap: 20px;

  &.active {
    opacity: 1;
  }
}

.comment-item-group {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px 0;
  border-bottom: 1px solid #EBEBEB;

  &:last-child {
    border-bottom: none;
  }
}

.comment-item-inner {
  display: flex;
  align-items: center;
  gap: 10px;
}

.writer {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 18px;
  font-weight: 600;
  line-height: 100%;
}

.txt {
  color: #222;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 16px;
  font-weight: 400;
  line-height: 150%;
  margin-top: 8px;
}

.date_wr {
  color: #888;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 14px;
  font-weight: 400;
  line-height: 100%;
  margin-top: 8px;
}

.btn_wr {
  margin-top: 8px;

  .del_btn {
    padding: 6px 16px;
    font-size: 14px;
  }
}

.comment-input-wr {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 20px;

  input {
    flex: 1;
    height: 52px;
    padding: 0 16px;
    border: 1px solid #CECECE;
    border-radius: 10px;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 400;
    outline: none;
    transition: border-color 0.2s;

    &::placeholder {
      color: #888;
    }

    &:focus {
      border-color: #073DFF;
    }
  }

  .btn {
    width: 52px;
    height: 52px;
    padding: 0;
    background: #073DFF;
    border: none;
    border-radius: 10px;
    color: #FFF;
    cursor: pointer;
    transition: background 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: #0530CC;
    }

    &:active {
      background: #042099;
    }
  }
}

.el-divider {
  margin: 0 0 20px 0;
}

.notice-badge {
  position: absolute;
  right: -4px;
  top: 25px;
  width: 100px;
  height: 38px;
}

.notice-text {
  position: absolute;
  left: 19px;
  top: 9px;
  color: #FFF;
  font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
  font-size: 20px;
  font-weight: 600;
  line-height: 100%;
}

.write-post-btn-fixed {
  position: fixed;
  left: 30px;
  bottom: 20px;
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
  display: flex;
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

/* Tablet Responsive Styles */
@media screen and (max-width: 1024px) {
  .community-main {
    margin: 0 0 0 240px;
    padding-left: 24px;
    padding-right: 24px;
  }

  .hero-banner {
    height: 300px;
    margin-left: -24px;
    margin-right: -24px;
    width: calc(100% + 48px);
  }

  .empty-state {
    padding: 80px 24px;
  }

  .welcome-title {
    font-size: 40px;
  }

  .welcome-subtitle {
    font-size: 20px;
    margin-bottom: 48px;
  }

  .create-post-btn {
    padding: 20px 80px;
    font-size: 24px;
  }

  .posts-container {
    padding: 24px;
    gap: 30px;
  }

  .post-card {
    padding: 30px;
  }

  .post-content-wrapper {
    gap: 24px;
  }

  .post-header {
    gap: 16px;
  }

  .user-name {
    font-size: 18px;
  }

  .post-date {
    font-size: 14px;
  }

  .post-body {
    gap: 24px;
  }

  .post-text {
    font-size: 18px;
  }

  .post-images {
    gap: 20px;
  }

  .post-image {
    width: 350px;
    height: 200px;
  }

  .stat-item {
    font-size: 18px;
  }

  .notice-badge {
    width: 90px;
    height: 34px;

    svg {
      width: 90px;
      height: 34px;
    }
  }

  .notice-text {
    font-size: 18px;
    left: 17px;
    top: 8px;
  }

  .write-post-btn-fixed {
    left: 24px;
    bottom: 36px;
    width: 180px;
    height: 48px;
    font-size: 16px;
  }
}

/* Mobile Responsive Styles */
@media screen and (max-width: 768px) {
  .community-main {
    margin-left: 0;
    padding: 100px 0 80px 0 ;
    padding-bottom: 80px;
  }

  .hero-banner {
    height: 160px;
    margin: 0;
    width: 100%;
    border-radius: 0;
  }

  .banner-image {
    border-radius: 0;
  }

  .empty-state {
    padding: 40px 26px;
  }

  .welcome-title {
    font-size: 18px;
    font-weight: 800;
    line-height: 150%;
    margin-bottom: 12px;
  }

  .welcome-subtitle {
    font-size: 12px;
    font-weight: 700;
    line-height: 100%;
    margin-bottom: 0;
  }

  .create-post-btn {
    display: none;
  }

  .posts-container {
    padding: 20px;
    gap: 20px;
  }

  .post-card {
    padding: 20px;
    border-radius: 8px;
  }

  .post-content-wrapper {
    gap: 16px;
  }

  .post-header {
    gap: 12px;
  }

  .user-avatar {
    width: 32px;
    height: 32px;

    svg {
      width: 32px;
      height: 32px;
    }
  }

  .user-name {
    font-size: 16px;
  }

  .post-date {
    font-size: 13px;
  }

  .post-body {
    gap: 16px;
  }

  .post-text {
    font-size: 15px;
    line-height: 150%;
  }

  .post-images {
    gap: 12px;
  }

  .post-image {
    width: 280px;
    height: 160px;
    border-radius: 8px;
  }

  .post-stats {
    gap: 10px;
  }

  .stat-item {
    font-size: 15px;
    gap: 4px;

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .stat-divider {
    height: 12px;
  }

  .notice-badge {
    width: 80px;
    height: 30px;
    right: 0;
    top: 15px;

    svg {
      width: 80px;
      height: 30px;
    }
  }

  .notice-text {
    font-size: 15px;
    left: 14px;
    top: 7px;
  }

  .write-post-btn-fixed {
    left: auto;
    right: 20px;
    bottom: 20px;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    font-size: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 14px;

    span {
      display: none;
    }

    &::before {
      content: '';
      display: block;
      width: 15px;
      height: 15px;
      background-image: url("data:image/svg+xml,%3Csvg width='15' height='15' viewBox='0 0 15 15' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M7.5 0V15M15 7.5H0' stroke='white' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
      background-repeat: no-repeat;
      background-position: center;
      background-size: contain;
    }
  }
}

@media screen and (max-width: 500px) {
  .community-main{padding: 80px 0;}
  .hero-banner { height: 130px;}
}
@media screen and (max-width: 480px) {
  .posts-container {
    padding: 16px;
    gap: 16px;
  }

  .post-card {
    padding: 16px;
  }

  .post-image {
    width: 100%;
    max-width: 280px;
  }

  .empty-state {
    padding: 30px 20px;
  }
}
</style>

<style lang="scss">
/* Unscoped styles for Comment component */
.comments-section {
  .comment-box {
    display: flex;
    flex-direction: column;
    gap: 20px;
    padding: 0;
  }

  .comment-item-group {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .comment-item {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 20px 0;
    border-bottom: 1px solid #EBEBEB;

    &:last-child {
      border-bottom: none;
    }
  }

  .comment-item-inner {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .writer {
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 18px;
    font-weight: 600;
    line-height: 100%;
  }

  .txt {
    text-align: left;
    color: #222;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 16px;
    font-weight: 400;
    line-height: 150%;
    margin-top: 8px;
  }

  .date_wr {
    text-align: right;
    margin-right:20px;
    color: #888;
    font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
    font-size: 14px;
    font-weight: 400;
    line-height: 100%;
    margin-top: 8px;
  }

  .btn_wr {
    margin-top: 8px;

    .del_btn {
      padding: 6px 16px;
      font-size: 14px;
    }
  }

  .comment-input-wr {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 20px;

    input {
      flex: 1;
      height: 52px;
      padding: 0 16px;
      border: 1px solid #CECECE;
      border-radius: 10px;
      color: #222;
      font-family: Pretendard, -apple-system, Roboto, Helvetica, sans-serif;
      font-size: 18px;
      font-weight: 400;
      outline: none;
      transition: border-color 0.2s;

      &::placeholder {
        color: #888;
      }

      &:focus {
        border-color: #073DFF;
      }
    }

    .btn {
      width: 52px;
      height: 52px;
      padding: 0;
      background: #073DFF;
      border: none;
      border-radius: 10px;
      color: #FFF;
      cursor: pointer;
      transition: background 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        background: #0530CC;
      }

      &:active {
        background: #042099;
      }
    }
  }

  .el-divider {
    margin: 0 0 20px 0;
  }
}

/* Responsive styles for comments */
@media screen and (max-width: 768px) {
  .comments-section {
    .writer {
      font-size: 16px;
    }

    .txt {
      font-size: 15px;
    }

    .date_wr {
      font-size: 13px;
    }

    .comment-input-wr {
      input {
        font-size: 16px;
        height: 48px;
      }

      .btn {
        width: 48px;
        height: 48px;
      }
    }
  }
}

@media screen and (max-width: 600px) {
  .comments-section {
    .writer {
      font-size: 15px;
    }

    .txt {
      font-size: 14px;
    }

    .date_wr {
      font-size: 12px;
    }

    .comment-input-wr {
      input {
        font-size: 14px;
        height: 44px;
      }

      .btn {
        width: 44px;
        height: 44px;
      }
    }

    .btn_wr .del_btn {
      padding: 4px 12px;
      font-size: 13px;
    }
  }
}

/* Responsive styles for notice badge and write button */
@media screen and (max-width: 768px) {
  .write-post-btn-fixed {
    right: 20px;
    bottom: 20px;
    left: auto;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    padding: 0;
    box-shadow: 0 4px 12px rgba(7, 61, 255, 0.4);

    .btn-text {
      display: none;
    }

    .btn-icon {
      display: block;
    }

    &:hover {
      transform: scale(1.1);
    }

    &:active {
      transform: scale(0.95);
    }
  }

  .comments-section .comment-box {gap: 0px;}
  .comments-section .comment-input-wr {margin-top: 0px;}
}

@media screen and (max-width: 600px) {
  .write-post-btn-fixed {
    right: 16px;
    bottom: 16px;
    width: 52px;
    height: 52px;
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

  .notice-text {
    font-size: 16px;
    left: 14px;
    top: 6px;
  }
}

/* Element UI Message z-index fix */
::v-deep .el-message {
  z-index: 9999 !important;
  top: 20px;
}
</style>
