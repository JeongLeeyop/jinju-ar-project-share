-- ============================================
-- Collation 불일치 해결 스크립트
-- 모든 관련 테이블을 utf8mb4_unicode_ci로 통일
-- ============================================

-- 1. schedule_participant 테이블 (참여자)
ALTER TABLE schedule_participant 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. calendar 테이블 (일정)
ALTER TABLE calendar 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. channel 테이블
ALTER TABLE channel 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 4. user 테이블
ALTER TABLE user 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 5. shop 테이블 (user와 JOIN되므로 포함)
ALTER TABLE shop 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 6. point_history 테이블 (포인트 관련)
ALTER TABLE point_history 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 7. activity_log 테이블 (활동 로그)
ALTER TABLE activity_log 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 특정 외래키 컬럼 collation 명시적 변경
-- (CONVERT TO로 해결되지 않는 경우 추가 실행)
-- ============================================

-- schedule_participant의 외래키 컬럼
ALTER TABLE schedule_participant 
  MODIFY COLUMN calendar_idx INT,
  MODIFY COLUMN user_uid VARCHAR(255) COLLATE utf8mb4_unicode_ci;

-- calendar의 외래키 컬럼
ALTER TABLE calendar 
  MODIFY COLUMN channel_uid VARCHAR(255) COLLATE utf8mb4_unicode_ci,
  MODIFY COLUMN writer_uid VARCHAR(255) COLLATE utf8mb4_unicode_ci;

-- channel의 UID 컬럼
ALTER TABLE channel 
  MODIFY COLUMN uid VARCHAR(255) COLLATE utf8mb4_unicode_ci;

-- user의 UID 컬럼
ALTER TABLE user 
  MODIFY COLUMN uid VARCHAR(255) COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 확인 쿼리 (실행 후 collation 확인)
-- ============================================

-- 테이블별 collation 확인
SELECT 
  TABLE_NAME,
  TABLE_COLLATION
FROM 
  information_schema.TABLES
WHERE 
  TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN (
    'schedule_participant', 
    'calendar', 
    'channel', 
    'user', 
    'shop',
    'point_history',
    'activity_log'
  );

-- 컬럼별 collation 확인
SELECT 
  TABLE_NAME,
  COLUMN_NAME,
  COLLATION_NAME
FROM 
  information_schema.COLUMNS
WHERE 
  TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN (
    'schedule_participant', 
    'calendar', 
    'channel', 
    'user'
  )
  AND COLLATION_NAME IS NOT NULL
ORDER BY 
  TABLE_NAME, COLUMN_NAME;
