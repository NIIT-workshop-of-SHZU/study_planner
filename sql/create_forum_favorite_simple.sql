USE study_planner;

CREATE TABLE IF NOT EXISTS forum_question_favorite (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_question_favorite (user_id, question_id),
    KEY idx_favorite_question (question_id),
    KEY idx_favorite_user (user_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_question FOREIGN KEY (question_id) REFERENCES forum_question (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;




