package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 论坛话题 Mapper（对应表：forum_topic）
 */
@Mapper
public interface ForumTopicMapper {

    /**
     * 获取热门话题（用于 /api/forum/topic/hot）
     */
    @Select("SELECT * FROM forum_topic " +
            "ORDER BY follow_count DESC, question_count DESC, id DESC " +
            "LIMIT #{limit}")
    List<ForumTopic> findHotTopics(@Param("limit") int limit);

    /**
     * 根据 ID 获取话题详情（用于 /api/forum/topic/{id}）
     */
    @Select("SELECT * FROM forum_topic WHERE id = #{id}")
    ForumTopic findById(@Param("id") Long id);

    /**
     * 获取全部话题（用于 /api/forum/topic）
     */
    @Select("SELECT * FROM forum_topic ORDER BY follow_count DESC, question_count DESC, id DESC")
    List<ForumTopic> findAll();
}
