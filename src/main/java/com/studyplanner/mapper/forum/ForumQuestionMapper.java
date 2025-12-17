package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 论坛问题/帖子 Mapper（对应表：forum_question）
 */
@Mapper
public interface ForumQuestionMapper {

    /**
     * 发帖：插入问题（用于 POST /api/forum/question）
     * 由 DB 写入 create_time / update_time（NOW()）
     */
    @Insert("INSERT INTO forum_question " +
            "(author_id, title, content, anonymous, view_count, answer_count, follow_count, create_time, update_time) " +
            "VALUES " +
            "(#{authorId}, #{title}, #{content}, #{anonymous}, 0, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ForumQuestion question);

    /**
     * 问题详情（用于 GET /api/forum/question/{id}）
     */
    @Select("SELECT * FROM forum_question WHERE id = #{id}")
    ForumQuestion findById(@Param("id") Long id);

    /**
     * 话题页：获取某话题下的问题列表（用于 GET /api/forum/topic/{id}/questions）
     */
    @Select("SELECT q.* FROM forum_question q " +
            "JOIN forum_question_topic qt ON q.id = qt.question_id " +
            "WHERE qt.topic_id = #{topicId} " +
            "ORDER BY q.create_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ForumQuestion> findByTopicId(
            @Param("topicId") Long topicId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
