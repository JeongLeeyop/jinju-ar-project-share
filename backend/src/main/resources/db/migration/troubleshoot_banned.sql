-- 추방 기능 문제 해결 가이드

-- 1. banned 컬럼이 존재하는지 확인
SHOW COLUMNS FROM channel_member LIKE 'banned';

-- 2. banned 컬럼이 없다면 추가 (마이그레이션 재실행)
-- ALTER TABLE channel_member ADD COLUMN banned BOOLEAN DEFAULT FALSE NOT NULL;
-- UPDATE channel_member SET banned = FALSE WHERE banned IS NULL;

-- 3. 현재 회원 상태 확인
SELECT 
    cm.idx,
    cm.user_uid,
    u.actual_name,
    cm.approval_status,
    cm.banned,
    cm.create_date
FROM channel_member cm
LEFT JOIN user u ON cm.user_uid = u.uid
WHERE cm.channel_uid = 'YOUR_CHANNEL_UID'
ORDER BY cm.create_date DESC;

-- 4. 추방된 회원 확인
SELECT 
    cm.idx,
    cm.user_uid,
    u.actual_name,
    cm.banned,
    cm.create_date
FROM channel_member cm
LEFT JOIN user u ON cm.user_uid = u.uid
WHERE cm.channel_uid = 'YOUR_CHANNEL_UID'
  AND cm.banned = TRUE;

-- 5. 특정 회원을 수동으로 추방 상태로 변경
-- UPDATE channel_member 
-- SET banned = TRUE 
-- WHERE idx = YOUR_MEMBER_IDX;

-- 6. 특정 회원을 수동으로 추방 해제
-- UPDATE channel_member 
-- SET banned = FALSE 
-- WHERE idx = YOUR_MEMBER_IDX;

-- 7. 추방 시도 후 데이터가 삭제되었는지 확인
-- (최근 삭제된 데이터는 복구 불가능하므로 미리 백업 권장)
SELECT COUNT(*) as total_members
FROM channel_member
WHERE channel_uid = 'YOUR_CHANNEL_UID';

-- 8. 문제 해결 순서:
-- a) banned 컬럼 존재 여부 확인 (1번 쿼리)
-- b) 컬럼이 없으면 마이그레이션 재실행 (2번 쿼리)
-- c) 백엔드 재시작
-- d) 추방 기능 다시 테스트
-- e) 백엔드 로그에서 "=== 회원 추방 메서드 호출 ===" 확인
-- f) banned = true로 저장되는지 확인 (3번, 4번 쿼리)
