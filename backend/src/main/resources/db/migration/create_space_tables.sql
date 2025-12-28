-- Space 테이블 생성
CREATE TABLE IF NOT EXISTS `space` (
    `uid` VARCHAR(100) NOT NULL PRIMARY KEY,
    `channel_uid` VARCHAR(100) NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `description` TEXT,
    `space_type` VARCHAR(20) NOT NULL,
    `admin_uid` VARCHAR(100) NOT NULL,
    `icon_url` VARCHAR(500),
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `max_members` INT NOT NULL DEFAULT 0,
    `member_count` INT NOT NULL DEFAULT 1,
    `invitation_required` BOOLEAN NOT NULL DEFAULT TRUE,
    `is_public` BOOLEAN NOT NULL DEFAULT FALSE,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_channel_uid` (`channel_uid`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- SpaceInvitation 테이블 생성
CREATE TABLE IF NOT EXISTS `space_invitation` (
    `uid` VARCHAR(100) NOT NULL PRIMARY KEY,
    `space_uid` VARCHAR(100) NOT NULL,
    `inviter_uid` VARCHAR(100) NOT NULL,
    `inviter_name` VARCHAR(100),
    `invited_user_uid` VARCHAR(100) NOT NULL,
    `invited_user_name` VARCHAR(100),
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    `message` VARCHAR(1000),
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `expires_at` TIMESTAMP NOT NULL,
    `responded_at` TIMESTAMP NULL,
    INDEX `idx_space_uid` (`space_uid`),
    INDEX `idx_invited_user_uid` (`invited_user_uid`),
    INDEX `idx_status` (`status`),
    INDEX `idx_expires_at` (`expires_at`),
    CONSTRAINT `fk_space_invitation_space` FOREIGN KEY (`space_uid`) 
        REFERENCES `space` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Space-Member 중간 테이블 생성 (다대다 관계)
CREATE TABLE IF NOT EXISTS `space_member` (
    `space_uid` VARCHAR(100) NOT NULL,
    `user_uid` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`space_uid`, `user_uid`),
    INDEX `idx_user_uid` (`user_uid`),
    CONSTRAINT `fk_space_member_space` FOREIGN KEY (`space_uid`) 
        REFERENCES `space` (`uid`) ON DELETE CASCADE,
    CONSTRAINT `fk_space_member_user` FOREIGN KEY (`user_uid`) 
        REFERENCES `user` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기존 activity_log 테이블에 Space 관련 ActivityType이 없다면 추가
-- (ActivityType은 Enum이므로 코드에서 관리되지만, 참고용 주석)
-- 추가된 ActivityType: SPACE_INVITED, SPACE_CREATED, SPACE_JOINED
