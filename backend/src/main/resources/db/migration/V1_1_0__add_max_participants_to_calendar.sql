-- Add max_participants column to calendar table
ALTER TABLE calendar ADD COLUMN max_participants INT NULL COMMENT '최대 참석 가능 인원';
