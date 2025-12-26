-- Activity Log 테이블 생성
-- 사용자 활동 로그를 저장하는 테이블

CREATE TABLE IF NOT EXISTS activity_log (
    idx BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '활동 로그 고유 ID',
    user_uid VARCHAR(100) NOT NULL COMMENT '사용자 UID',
    user_name VARCHAR(100) COMMENT '사용자 이름',
    channel_uid VARCHAR(100) NOT NULL COMMENT '채널 UID',
    channel_name VARCHAR(200) COMMENT '채널 이름',
    activity_type VARCHAR(50) NOT NULL COMMENT '활동 타입',
    description VARCHAR(1000) NOT NULL COMMENT '활동 설명',
    related_uid VARCHAR(100) COMMENT '관련 엔티티 UID (게시글, 댓글 등)',
    related_name VARCHAR(200) COMMENT '관련 엔티티 이름',
    target_user_uid VARCHAR(100) COMMENT '대상 사용자 UID (댓글, 좋아요 등)',
    target_user_name VARCHAR(100) COMMENT '대상 사용자 이름',
    meta_data TEXT COMMENT '추가 메타 데이터 (JSON 형식)',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성 일시',
    INDEX idx_user_channel_created (user_uid, channel_uid, created_at),
    INDEX idx_channel_created (channel_uid, created_at),
    INDEX idx_user_created (user_uid, created_at),
    INDEX idx_activity_type (activity_type),
    INDEX idx_related_uid (related_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 활동 로그 테이블';

-- Activity Type 목록:
-- SPACE_INVITED: 공간 초대
-- SPACE_CREATED: 공간 개설
-- SPACE_JOINED: 공간 참여
-- POST_CREATED: 게시글 작성
-- POST_UPDATED: 게시글 수정
-- POST_DELETED: 게시글 삭제
-- COMMENT_CREATED: 댓글 작성
-- COMMENT_UPDATED: 댓글 수정
-- COMMENT_DELETED: 댓글 삭제
-- LIKE_ADDED: 좋아요 추가
-- LIKE_REMOVED: 좋아요 취소
-- COMMUNITY_JOINED: 커뮤니티 가입
-- COMMUNITY_LEFT: 커뮤니티 탈퇴
-- EVENT_CREATED: 일정 생성
-- EVENT_JOINED: 일정 참여
-- EVENT_CANCELLED: 일정 취소
-- PRODUCT_CREATED: 상품 등록
-- PRODUCT_PURCHASED: 상품 구매
-- LESSON_CREATED: 강의 생성
-- VIDEO_UPLOADED: 영상 업로드
-- VIDEO_WATCHED: 영상 시청
-- VIDEO_COMPLETED: 영상 시청 완료
-- RANK_UP: 랭킹 상승
-- RANK_DOWN: 랭킹 하락
-- RPOINT_EARNED: R포인트 획득
-- RPOINT_USED: R포인트 사용
