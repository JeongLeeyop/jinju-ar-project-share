-- ============================================
-- Collation 불일치 해결 스크립트
-- 모든 관련 테이블을 utf8mb4_unicode_ci로 통일
-- ============================================

-- ============================================
-- 방법 1: 모든 테이블을 utf8mb4_unicode_ci로 변환
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

-- 8. 기타 관련 테이블들 (JOIN에 사용될 수 있는 모든 테이블)
ALTER TABLE channel_member 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE channel_member_permission 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE space 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE space_member 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE post 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE comment 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE board 
  CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 방법 2: 데이터베이스 전체 기본 collation 변경
-- (주의: 새로 생성되는 테이블에만 적용됨)
-- ============================================

-- 데이터베이스명을 실제 사용 중인 데이터베이스명으로 변경하세요
-- ALTER DATABASE your_database_name 
--   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

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

-- ============================================
-- 방법 3: 데이터베이스 내 모든 테이블 자동 변환 (가장 확실한 방법)
-- ============================================

-- 아래 쿼리를 실행하면 현재 데이터베이스의 모든 테이블을 변환하는 SQL이 생성됩니다.
-- 생성된 SQL을 복사해서 다시 실행하세요.

SELECT CONCAT(
  'ALTER TABLE `', table_name, '` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'
) AS conversion_sql
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_TYPE = 'BASE TABLE';

-- ============================================
-- 방법 4: 긴급 임시 해결 (COLLATE 명시)
-- application.yml에 아래 설정 추가
-- ============================================

/*
spring:
  jpa:
    properties:
      hibernate:
        connection:
          characterEncoding: utf8mb4
          useUnicode: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8mb4&connectionCollation=utf8mb4_unicode_ci&serverTimezone=Asia/Seoul
*/

-- ============================================
-- 권장 실행 순서
-- ============================================

/*
1단계: 방법 1 실행 (모든 테이블 변환)
   - 위의 ALTER TABLE 명령들을 순서대로 실행

2단계: 확인 쿼리 실행 (아래 "확인 쿼리" 섹션)
   - 모든 테이블이 utf8mb4_unicode_ci로 변경되었는지 확인

3단계: 백엔드 재시작
   - Spring Boot 애플리케이션 재시작

4단계 (선택): 여전히 문제가 있다면 방법 3 실행
   - 방법 3의 SELECT 쿼리를 실행하여 생성된 SQL을 다시 실행
*/
