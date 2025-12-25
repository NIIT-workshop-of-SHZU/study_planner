package com.studyplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子收藏实体类
 */
@Data
public class ForumQuestionFavorite {
    private Long id;
    private Long userId;
    private Long questionId;
    private LocalDateTime createTime;
}




