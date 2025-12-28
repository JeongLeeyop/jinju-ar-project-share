import request from '@/utils/request';

// IMPORTANT: 백엔드 엔드포인트는 /api/spaces/{spaceUid}/posts 형태입니다
// request utility가 /api를 자동으로 추가하므로 /spaces/{spaceUid}/posts로 시작합니다

// ==================== Space Post Management ====================

/**
 * 공간 게시글 생성
 * POST /api/spaces/{spaceUid}/posts
 */
export const createSpacePost = (spaceUid: string, data: {
  title: string;
  content: string;
  isNotice?: boolean;
  attachments?: string[];
}) => {
  return request({
    url: `/spaces/${spaceUid}/posts`,
    method: 'post',
    data,
  });
};

/**
 * 공간 게시글 조회
 * GET /api/spaces/{spaceUid}/posts/{postUid}
 */
export const getSpacePost = (spaceUid: string, postUid: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}`,
  method: 'get',
});

/**
 * 공간의 모든 게시글 조회
 * GET /api/spaces/{spaceUid}/posts
 */
export const getSpacePostsBySpace = (spaceUid: string) => request({
  url: `/spaces/${spaceUid}/posts`,
  method: 'get',
});

/**
 * 공간의 게시글 페이징 조회
 * GET /api/spaces/{spaceUid}/posts/paged
 */
export const getSpacePostsPaged = (spaceUid: string, page: number = 0, size: number = 20) => request({
  url: `/spaces/${spaceUid}/posts/paged`,
  method: 'get',
  params: { page, size },
});

/**
 * 공간의 게시글 검색
 * GET /api/spaces/{spaceUid}/posts/search?keyword={keyword}
 */
export const searchSpacePosts = (spaceUid: string, keyword: string) => request({
  url: `/spaces/${spaceUid}/posts/search`,
  method: 'get',
  params: { keyword },
});

/**
 * 공간 게시글 수정
 * PUT /api/spaces/{spaceUid}/posts/{postUid}
 */
export const updateSpacePost = (spaceUid: string, postUid: string, data: {
  title?: string;
  content?: string;
  isNotice?: boolean;
  attachments?: string[];
}) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}`,
  method: 'put',
  data,
});

/**
 * 공간 게시글 삭제 (소프트 삭제)
 * DELETE /api/spaces/{spaceUid}/posts/{postUid}
 */
export const deleteSpacePost = (spaceUid: string, postUid: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}`,
  method: 'delete',
});

/**
 * 게시글 좋아요 토글
 * POST /api/spaces/{spaceUid}/posts/{postUid}/like
 */
export const toggleSpacePostLike = (spaceUid: string, postUid: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}/like`,
  method: 'post',
});

// ==================== Space Post Comment Management ====================

/**
 * 댓글 작성
 * POST /api/spaces/{spaceUid}/posts/{postUid}/comments
 */
export const createSpacePostComment = (spaceUid: string, postUid: string, content: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}/comments`,
  method: 'post',
  data: { content },
});

/**
 * 게시글의 댓글 목록 조회
 * GET /api/spaces/{spaceUid}/posts/{postUid}/comments
 */
export const getSpacePostComments = (spaceUid: string, postUid: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}/comments`,
  method: 'get',
});

/**
 * 댓글 수정
 * PUT /api/spaces/{spaceUid}/posts/{postUid}/comments/{commentUid}
 */
export const updateSpacePostComment = (spaceUid: string, postUid: string, commentUid: string, content: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}/comments/${commentUid}`,
  method: 'put',
  data: { content },
});

/**
 * 댓글 삭제
 * DELETE /api/spaces/{spaceUid}/posts/{postUid}/comments/{commentUid}
 */
export const deleteSpacePostComment = (spaceUid: string, postUid: string, commentUid: string) => request({
  url: `/spaces/${spaceUid}/posts/${postUid}/comments/${commentUid}`,
  method: 'delete',
});

// ==================== Types ====================

export interface SpacePost {
  uid: string;
  spaceUid: string;
  authorUid: string;
  authorName: string;
  title?: string;
  content: string;
  isNotice: boolean;
  attachments?: string[];
  viewCount: number;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt?: string;
  isAuthor: boolean;
  isLiked: boolean;
}

export interface SpacePostComment {
  uid: string;
  postUid: string;
  authorUid: string;
  authorName: string;
  content: string;
  createdAt: string;
  updatedAt?: string;
  isAuthor: boolean;
}
