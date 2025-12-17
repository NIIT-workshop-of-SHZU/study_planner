package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 论坛评论/回复 Mapper（对应表：forum_comment）
 * parent_id 为空：评论回答；不为空：回复评论
 */
@Mapper
public interface ForumCommentMapper {

    /**
     * 发布评论/回复（用于 POST /api/forum/comment）
     */
    @Insert("INSERT INTO forum_comment " +
            "(answer_id, author_id, parent_id, content, vote_count, create_time, update_time) " +
            "VALUES " +
            "(#{answerId}, #{authorId}, #{parentId}, #{content}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ForumComment comment);

    /**
     * 获取某回答下的全部评论（用于 GET /api/forum/comment?answer_id=...）
     * Service 层后续会按 parent_id 组装 replies（前端需要 comment.replies）
     */
    @Select("SELECT * FROM forum_comment " +
            "WHERE answer_id = #{answerId} " +
            "ORDER BY create_time ASC, id ASC")
    List<ForumComment> findByAnswerId(@Param("answerId") Long answerId);
}
