-- 添加问题点赞表
-- 执行方式：在MySQL客户端中执行此脚本
-- mysql -h localhost -P 3307 -u root -p123456 study_planner < add_forum_question_vote_table_executable.sql

USE study_planner;

-- 创建问题点赞表
CREATE TABLE IF NOT EXISTS `forum_question_vote` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '问题ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question_vote` (`user_id`, `question_id`),
    KEY `idx_qv_question` (`question_id`),
    KEY `idx_qv_user` (`user_id`),
    CONSTRAINT `fk_qv_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_qv_question` FOREIGN KEY (`question_id`) REFERENCES `forum_question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题点赞表';

-- 为问题表添加点赞数字段（检查字段是否存在，如果不存在则添加）
SET @dbname = DATABASE();
SET @tablename = 'forum_question';
SET @columnname = 'vote_count';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT NOT NULL DEFAULT 0 COMMENT ''点赞数（可冗余）'' AFTER `follow_count`')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SELECT 'SQL脚本执行完成！' AS result;



