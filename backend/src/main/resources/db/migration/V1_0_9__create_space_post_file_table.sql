-- Space Post File Table (공간 게시글 첨부파일)
-- COLLATION을 space_post 테이블과 일치시키기 위해 명시적으로 지정
CREATE TABLE IF NOT EXISTS `space_post_file` (
  `uid` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `post_uid` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_uid` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `view_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`uid`),
  INDEX `idx_post_uid` (`post_uid`),
  INDEX `idx_file_uid` (`file_uid`),
  CONSTRAINT `fk_space_post_file_post` FOREIGN KEY (`post_uid`) REFERENCES `space_post` (`uid`) ON DELETE CASCADE,
  CONSTRAINT `fk_space_post_file_file` FOREIGN KEY (`file_uid`) REFERENCES `attached_file` (`uid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공간 게시글 첨부파일';
