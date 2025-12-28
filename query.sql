-- SMS 템플릿 테이블 생성
CREATE TABLE IF NOT EXISTS sms_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '템플릿 ID',
    name VARCHAR(100) NOT NULL COMMENT '템플릿 이름',
    content TEXT NOT NULL COMMENT '템플릿 내용',
    channel_uid VARCHAR(50) NULL COMMENT '채널 UID (NULL이면 전체 공통)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    INDEX idx_channel_uid (channel_uid),
    INDEX idx_is_active (is_active),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SMS 템플릿';

-- 기본 템플릿 데이터 삽입
INSERT INTO sms_template (name, content, channel_uid, is_active, sort_order) VALUES
('오프라인 장터 안내', '[와로 커뮤니티]\n오프라인 장터가 오픈되었습니다.\n다양한 상품을 확인해보세요!', NULL, TRUE, 1),
('신규 상품 등록 알림', '[와로 커뮤니티]\n새로운 상품이 등록되었습니다.\n지금 바로 확인해보세요!', NULL, TRUE, 2),
('이벤트 안내', '[와로 커뮤니티]\n특별 이벤트가 진행중입니다.\n자세한 내용은 앱을 확인해주세요.', NULL, TRUE, 3);

alter table calendar
                        add max_participants int null