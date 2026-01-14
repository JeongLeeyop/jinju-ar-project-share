-- Channel 테이블에 create_date 컬럼 추가
-- 생성일시를 저장하기 위한 컬럼 추가

-- 1. create_date 컬럼 추가 (NULL 허용)
ALTER TABLE channel 
ADD COLUMN create_date DATETIME(6) NULL COMMENT '생성일시';

-- 2. 기존 데이터에 대해 현재 시간으로 초기화 (선택사항)
UPDATE channel 
SET create_date = NOW() 
WHERE create_date IS NULL;

-- 3. NOT NULL 제약조건 추가 (선택사항 - 기존 데이터 초기화 후 실행)
-- ALTER TABLE channel 
-- MODIFY COLUMN create_date DATETIME(6) NOT NULL COMMENT '생성일시';

-- 4. 인덱스 추가 (정렬 성능 향상)
CREATE INDEX idx_channel_create_date ON channel(create_date DESC);

-- 5. delete_status 인덱스 추가 (필터링 성능 향상)
CREATE INDEX idx_channel_delete_status ON channel(delete_status);

-- 6. 복합 인덱스 추가 (delete_status + create_date 조합 쿼리 최적화)
CREATE INDEX idx_channel_delete_status_create_date ON channel(delete_status, create_date DESC);

-- 확인 쿼리
SELECT 
    uid,
    name,
    delete_status,
    create_date,
    CASE 
        WHEN delete_status = 0 THEN '정상'
        WHEN delete_status = 1 THEN '삭제됨'
        ELSE '알 수 없음'
    END AS status_label
FROM channel
ORDER BY create_date DESC
LIMIT 10;
