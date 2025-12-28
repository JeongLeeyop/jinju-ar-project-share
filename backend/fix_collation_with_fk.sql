-- ============================================
-- Collation 불일치 해결 (외래키 제약조건 처리 포함)
-- 실행 전 반드시 데이터베이스 백업!
-- ============================================

-- ============================================
-- 1단계: 모든 외래키 제약조건 확인 및 저장
-- ============================================

-- 현재 데이터베이스의 모든 외래키 조회 (백업용)
SELECT 
    CONCAT(
        'ALTER TABLE `', TABLE_NAME, '` ',
        'DROP FOREIGN KEY `', CONSTRAINT_NAME, '`;'
    ) AS drop_fk_sql
FROM 
    information_schema.TABLE_CONSTRAINTS
WHERE 
    CONSTRAINT_TYPE = 'FOREIGN KEY'
    AND TABLE_SCHEMA = DATABASE();

-- ============================================
-- 2단계: 외래키 제약조건 삭제
-- ============================================

-- 아래 명령어들을 실행하여 주요 외래키 삭제
-- (에러 나는 것은 무시하고 계속 진행)

SET FOREIGN_KEY_CHECKS = 0;  -- 외래키 체크 일시 중지

-- calendar 관련 외래키 삭제
ALTER TABLE calendar DROP FOREIGN KEY IF EXISTS FKm2v6j9fadfbcpnjy5p6mpheii;
ALTER TABLE calendar DROP FOREIGN KEY IF EXISTS FK_calendar_channel;
ALTER TABLE calendar DROP FOREIGN KEY IF EXISTS FK_calendar_user;

-- schedule_participant 관련 외래키 삭제
ALTER TABLE schedule_participant DROP FOREIGN KEY IF EXISTS FK_schedule_participant_calendar;
ALTER TABLE schedule_participant DROP FOREIGN KEY IF EXISTS FK_schedule_participant_user;

-- channel_member 관련 외래키 삭제
ALTER TABLE channel_member DROP FOREIGN KEY IF EXISTS FK_channel_member_channel;
ALTER TABLE channel_member DROP FOREIGN KEY IF EXISTS FK_channel_member_user;

-- space 관련 외래키 삭제
ALTER TABLE space DROP FOREIGN KEY IF EXISTS FK_space_channel;

-- space_member 관련 외래키 삭제
ALTER TABLE space_member DROP FOREIGN KEY IF EXISTS FK_space_member_space;
ALTER TABLE space_member DROP FOREIGN KEY IF EXISTS FK_space_member_user;

-- post 관련 외래키 삭제
ALTER TABLE post DROP FOREIGN KEY IF EXISTS FK_post_user;
ALTER TABLE post DROP FOREIGN KEY IF EXISTS FK_post_board;

-- comment 관련 외래키 삭제
ALTER TABLE comment DROP FOREIGN KEY IF EXISTS FK_comment_post;
ALTER TABLE comment DROP FOREIGN KEY IF EXISTS FK_comment_user;

-- point_history 관련 외래키 삭제
ALTER TABLE point_history DROP FOREIGN KEY IF EXISTS FK_point_history_user;
ALTER TABLE point_history DROP FOREIGN KEY IF EXISTS FK_point_history_channel;

-- ============================================
-- 3단계: 모든 테이블 Collation 변경
-- ============================================

-- 핵심 테이블들
ALTER TABLE user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE channel CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE calendar CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE schedule_participant CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 관련 테이블들
ALTER TABLE shop CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE channel_member CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE channel_member_permission CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space_member CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space_invitation CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE board CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE post CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE comment CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE point_history CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE activity_log CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 추가 테이블들 (있는 경우만 실행)
ALTER TABLE space_post CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space_post_comment CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space_post_file CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE space_post_like CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE post_like CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE attached_file CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE marketplace CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE product CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE product_order CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE lession CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 4단계: 외래키 제약조건 재생성
-- ============================================

-- calendar 외래키 재생성
ALTER TABLE calendar 
  ADD CONSTRAINT FK_calendar_channel 
  FOREIGN KEY (channel_uid) REFERENCES channel(uid) ON DELETE CASCADE;

ALTER TABLE calendar 
  ADD CONSTRAINT FK_calendar_user 
  FOREIGN KEY (writer_uid) REFERENCES user(uid) ON DELETE CASCADE;

-- schedule_participant 외래키 재생성
ALTER TABLE schedule_participant 
  ADD CONSTRAINT FK_schedule_participant_calendar 
  FOREIGN KEY (calendar_idx) REFERENCES calendar(idx) ON DELETE CASCADE;

ALTER TABLE schedule_participant 
  ADD CONSTRAINT FK_schedule_participant_user 
  FOREIGN KEY (user_uid) REFERENCES user(uid) ON DELETE CASCADE;

-- channel_member 외래키 재생성
ALTER TABLE channel_member 
  ADD CONSTRAINT FK_channel_member_channel 
  FOREIGN KEY (channel_uid) REFERENCES channel(uid) ON DELETE CASCADE;

ALTER TABLE channel_member 
  ADD CONSTRAINT FK_channel_member_user 
  FOREIGN KEY (user_uid) REFERENCES user(uid) ON DELETE CASCADE;

-- space 외래키 재생성
ALTER TABLE space 
  ADD CONSTRAINT FK_space_channel 
  FOREIGN KEY (channel_uid) REFERENCES channel(uid) ON DELETE CASCADE;

-- space_member 외래키 재생성
ALTER TABLE space_member 
  ADD CONSTRAINT FK_space_member_space 
  FOREIGN KEY (space_uid) REFERENCES space(uid) ON DELETE CASCADE;

ALTER TABLE space_member 
  ADD CONSTRAINT FK_space_member_user 
  FOREIGN KEY (user_uid) REFERENCES user(uid) ON DELETE CASCADE;

-- point_history 외래키 재생성
ALTER TABLE point_history 
  ADD CONSTRAINT FK_point_history_user 
  FOREIGN KEY (user_uid) REFERENCES user(uid) ON DELETE CASCADE;

ALTER TABLE point_history 
  ADD CONSTRAINT FK_point_history_channel 
  FOREIGN KEY (channel_uid) REFERENCES channel(uid) ON DELETE CASCADE;

SET FOREIGN_KEY_CHECKS = 1;  -- 외래키 체크 재활성화

-- ============================================
-- 5단계: 확인 쿼리
-- ============================================

-- 테이블별 collation 확인
SELECT 
  TABLE_NAME,
  TABLE_COLLATION
FROM 
  information_schema.TABLES
WHERE 
  TABLE_SCHEMA = DATABASE()
  AND TABLE_TYPE = 'BASE TABLE'
ORDER BY 
  TABLE_NAME;

-- utf8mb4_unicode_ci가 아닌 테이블 찾기
SELECT 
  TABLE_NAME,
  TABLE_COLLATION
FROM 
  information_schema.TABLES
WHERE 
  TABLE_SCHEMA = DATABASE()
  AND TABLE_TYPE = 'BASE TABLE'
  AND TABLE_COLLATION != 'utf8mb4_unicode_ci';

-- 컬럼별 collation 확인 (주요 테이블)
SELECT 
  TABLE_NAME,
  COLUMN_NAME,
  COLLATION_NAME
FROM 
  information_schema.COLUMNS
WHERE 
  TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('calendar', 'channel', 'user', 'schedule_participant')
  AND COLLATION_NAME IS NOT NULL
ORDER BY 
  TABLE_NAME, COLUMN_NAME;

-- ============================================
-- 실행 완료 메시지
-- ============================================

SELECT '✅ Collation 변경 완료! 백엔드를 재시작하세요.' AS status;
