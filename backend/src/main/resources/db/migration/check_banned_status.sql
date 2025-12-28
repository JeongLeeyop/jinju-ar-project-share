-- =====================================================
-- 추방 기능 상태 체크 스크립트
-- =====================================================

-- 1. channel_member 테이블 구조 확인
DESCRIBE channel_member;

-- 2. banned 컬럼 존재 여부 확인
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'channel_member' 
  AND COLUMN_NAME = 'banned';

-- 3. 현재 회원 통계
SELECT 
    COUNT(*) as '총 회원 수',
    SUM(CASE WHEN banned = TRUE THEN 1 ELSE 0 END) as '추방된 회원',
    SUM(CASE WHEN banned = FALSE OR banned IS NULL THEN 1 ELSE 0 END) as '활동 회원',
    SUM(CASE WHEN approval_status = TRUE THEN 1 ELSE 0 END) as '승인된 회원',
    SUM(CASE WHEN approval_status = FALSE THEN 1 ELSE 0 END) as '대기 중 회원'
FROM channel_member;

-- 4. 추방된 회원 목록 (있다면)
SELECT 
    cm.idx,
    cm.user_uid,
    u.actual_name as '회원명',
    u.email as '이메일',
    cm.banned as '추방 여부',
    cm.approval_status as '승인 상태',
    cm.create_date as '가입일'
FROM channel_member cm
LEFT JOIN user u ON cm.user_uid = u.uid
WHERE cm.banned = TRUE
LIMIT 10;

-- 5. 테스트: 특정 회원 추방 (주석 해제하여 실행)
-- UPDATE channel_member SET banned = TRUE WHERE idx = 1;

-- 6. 테스트: 추방 해제 (주석 해제하여 실행)
-- UPDATE channel_member SET banned = FALSE WHERE idx = 1;
