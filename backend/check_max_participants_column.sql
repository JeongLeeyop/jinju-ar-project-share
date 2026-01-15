-- ===== 단계 1: calendar 테이블의 모든 컬럼 확인 =====
SHOW COLUMNS FROM calendar;

-- ===== 단계 2: max_participants 컬럼 상세 정보 확인 =====
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT,
    COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'jinju_ar_community' 
  AND TABLE_NAME = 'calendar'
  AND COLUMN_NAME = 'max_participants';

-- ===== 단계 3: 컬럼이 없다면 아래 쿼리 실행 =====
-- ALTER TABLE calendar ADD COLUMN max_participants INT NULL COMMENT '최대 참석 가능 인원';

-- ===== 단계 4: 기존 일정 데이터 확인 (최근 10개) =====
SELECT 
    idx, 
    title, 
    max_participants, 
    event_type,
    points,
    start_date,
    created_date
FROM calendar 
ORDER BY idx DESC 
LIMIT 10;

-- ===== 단계 5: Flyway 마이그레이션 히스토리 확인 =====
SELECT * FROM flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 10;

-- ===== 단계 6: 특정 일정 업데이트 테스트 (idx를 실제 값으로 변경) =====
-- UPDATE calendar SET max_participants = 13 WHERE idx = 18;
-- SELECT idx, title, max_participants FROM calendar WHERE idx = 18;
