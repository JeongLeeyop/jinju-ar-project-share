-- calendar 테이블의 모든 컬럼 확인
SHOW COLUMNS FROM calendar;

-- max_participants 컬럼 존재 여부 확인
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'jinju_ar_community' 
  AND TABLE_NAME = 'calendar'
  AND COLUMN_NAME = 'max_participants';

-- 만약 컬럼이 없다면 아래 쿼리 실행
-- ALTER TABLE calendar ADD COLUMN max_participants INT NULL COMMENT '최대 참석 가능 인원';

-- 기존 데이터 확인
SELECT idx, title, max_participants, event_type FROM calendar ORDER BY idx DESC LIMIT 10;
