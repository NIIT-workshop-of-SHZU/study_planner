package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumAnswer;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 论坛回答 Mapper（对应表：forum_answer）
 * 注意：前端“回复帖子”主要是回答（answer），评论是 comment
 */
@Mapper
public interface ForumAnswerMapper {

    /**
     * 发布回答（用于 POST /api/forum/answer）
     */
    @Insert("INSERT INTO forum_answer " +
            "(question_id, author_id, content, vote_count, comment_count, create_time, update_time) " +
            "VALUES " +
            "(#{questionId}, #{authorId}, #{content}, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ForumAnswer answer);

    /**
     * 获取某问题下的回答列表（用于 GET /api/forum/question/{questionId}/answers）
     * sort 允许：vote_count / created_at（前端传 created_at，我们映射为按 create_time）
     */
    @Select({
            "<script>",
            "SELECT * FROM forum_answer",
            "WHERE question_id = #{questionId}",
            "ORDER BY",
            "<choose>",
            "  <when test='sort != null and sort == \"vote_count\"'> vote_count DESC, id DESC </when>",
            "  <otherwise> create_time DESC, id DESC </otherwise>",
            "</choose>",
            "</script>"
    })
    List<ForumAnswer> findByQuestionId(
            @Param("questionId") Long questionId,
            @Param("sort") String sort
    );
}
