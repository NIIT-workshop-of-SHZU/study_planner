-- 添加问题点赞表
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

-- 为问题表添加点赞数字段
ALTER TABLE `forum_question` 
ADD COLUMN IF NOT EXISTS `vote_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数（可冗余）' AFTER `follow_count`;



