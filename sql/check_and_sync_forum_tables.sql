-- ============================================
-- 检查并同步论坛相关表
-- ============================================

USE study_planner;

-- 检查并创建 forum_question_favorite 表（收藏帖子）
CREATE TABLE IF NOT EXISTS `forum_question_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `question_id` BIGINT NOT NULL COMMENT '帖子ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question_favorite` (`user_id`, `question_id`),
    KEY `idx_favorite_question` (`question_id`),
    KEY `idx_favorite_user` (`user_id`),
    CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_favorite_question` FOREIGN KEY (`question_id`) REFERENCES `forum_question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子收藏表';

-- 检查表是否存在并显示状态
SELECT 
    'forum_question_favorite' AS table_name,
    CASE WHEN COUNT(*) > 0 THEN '存在' ELSE '不存在' END AS status
FROM information_schema.tables 
WHERE table_schema = 'study_planner' AND table_name = 'forum_question_favorite'

UNION ALL

SELECT 
    'forum_user_follow' AS table_name,
    CASE WHEN COUNT(*) > 0 THEN '存在' ELSE '不存在' END AS status
FROM information_schema.tables 
WHERE table_schema = 'study_planner' AND table_name = 'forum_user_follow'

UNION ALL

SELECT 
    'forum_answer_collect' AS table_name,
    CASE WHEN COUNT(*) > 0 THEN '存在' ELSE '不存在' END AS status
FROM information_schema.tables 
WHERE table_schema = 'study_planner' AND table_name = 'forum_answer_collect'

UNION ALL

SELECT 
    'forum_question_follow' AS table_name,
    CASE WHEN COUNT(*) > 0 THEN '存在' ELSE '不存在' END AS status
FROM information_schema.tables 
WHERE table_schema = 'study_planner' AND table_name = 'forum_question_follow';




