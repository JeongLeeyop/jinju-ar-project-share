-- 포인트 적립 설정 테이블
-- 채널별 이벤트에 대한 포인트 적립량을 설정합니다.

CREATE TABLE IF NOT EXISTS point_setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uid VARCHAR(36) NOT NULL UNIQUE COMMENT '채널 UID',
    
    -- 이벤트별 포인트 설정
    post_create INT NOT NULL DEFAULT 100 COMMENT '게시글 작성 시 적립 포인트',
    comment_create INT NOT NULL DEFAULT 50 COMMENT '댓글 작성 시 적립 포인트',
    like_give INT NOT NULL DEFAULT 10 COMMENT '좋아요 누르기 시 적립 포인트',
    daily_attendance INT NOT NULL DEFAULT 50 COMMENT '출석 체크 시 적립 포인트',
    marketplace_create INT NOT NULL DEFAULT 100 COMMENT '장터 상품 등록 시 적립 포인트',
    marketplace_sell INT NOT NULL DEFAULT 200 COMMENT '장터 상품 판매 완료 시 적립 포인트',
    course_complete INT NOT NULL DEFAULT 150 COMMENT '강좌 수강 완료 시 적립 포인트',
    
    -- 일일 적립 횟수 제한 (0 = 무제한)
    post_daily_limit INT NOT NULL DEFAULT 10 COMMENT '게시글 일일 적립 횟수 제한',
    comment_daily_limit INT NOT NULL DEFAULT 20 COMMENT '댓글 일일 적립 횟수 제한',
    like_daily_limit INT NOT NULL DEFAULT 30 COMMENT '좋아요 일일 적립 횟수 제한',
    marketplace_create_daily_limit INT NOT NULL DEFAULT 5 COMMENT '장터 등록 일일 적립 횟수 제한',
    marketplace_sell_daily_limit INT NOT NULL DEFAULT 0 COMMENT '장터 판매 일일 적립 횟수 제한 (0=무제한)',
    course_complete_daily_limit INT NOT NULL DEFAULT 0 COMMENT '강좌 완료 일일 적립 횟수 제한 (0=무제한)',
    
    -- 최소 글자수 제한
    post_min_length INT NOT NULL DEFAULT 20 COMMENT '게시글 최소 글자수',
    comment_min_length INT NOT NULL DEFAULT 5 COMMENT '댓글 최소 글자수',
    
    -- 메타 정보
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_point_setting_channel (channel_uid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='포인트 적립 설정';

-- 기본값 설명:
-- post_create: 100P - 게시글 작성 (일일 10회, 최소 20자)
-- comment_create: 50P - 댓글 작성 (일일 20회, 최소 5자, 게시글당 1회, 자기글 제외)
-- like_give: 10P - 좋아요 누르기 (일일 30회, 게시글당 1회, 자기글 제외)
-- daily_attendance: 50P - 출석 체크 (일일 1회)
-- marketplace_create: 100P - 장터 상품 등록 (일일 5회)
-- marketplace_sell: 200P - 장터 상품 판매 완료 (무제한)
-- course_complete: 150P - 강좌 수강 완료 (강좌당 1회, 무제한)

-- 악용 방지 로직:
-- point_history 테이블을 활용하여 중복 적립 및 일일 제한 체크
-- 별도의 point_earn_history 테이블 불필요 (point_history에 이미 모든 정보 포함)
