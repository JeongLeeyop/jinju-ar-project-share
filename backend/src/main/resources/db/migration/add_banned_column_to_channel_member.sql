-- =====================================================
-- 추방 기능 구현: channel_member 테이블에 banned 컬럼 추가
-- =====================================================

-- 1. 컬럼 존재 여부 확인 (실행 전 확인용)
-- SELECT COLUMN_NAME 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_NAME = 'channel_member' AND COLUMN_NAME = 'banned';

-- 2. banned 컬럼 추가 (컬럼이 없을 경우에만 실행)
ALTER TABLE channel_member
ADD COLUMN IF NOT EXISTS banned BOOLEAN DEFAULT FALSE COMMENT '추방 여부';

-- 3. 기존 레코드의 banned 값을 FALSE로 설정
UPDATE channel_member SET banned = FALSE WHERE banned IS NULL;

-- 4. 인덱스 추가 (선택사항 - 추방된 회원 조회 성능 향상)
-- CREATE INDEX idx_channel_member_banned ON channel_member(banned);

-- 5. 확인 쿼리 (실행 후 확인용)
-- SELECT COUNT(*) as total_members,
--        SUM(CASE WHEN banned = TRUE THEN 1 ELSE 0 END) as banned_members,
--        SUM(CASE WHEN banned = FALSE THEN 1 ELSE 0 END) as active_members
-- FROM channel_member;

