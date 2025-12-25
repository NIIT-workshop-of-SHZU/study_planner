package com.studyplanner.mapper.forum;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumTopicFollowMapper {
    
    @Insert("INSERT INTO forum_topic_follow (user_id, topic_id, create_time) " +
            "VALUES (#{userId}, #{topicId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("topicId") Long topicId);
    
    @Delete("DELETE FROM forum_topic_follow WHERE user_id = #{userId} AND topic_id = #{topicId}")
    int delete(@Param("userId") Long userId, @Param("topicId") Long topicId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_topic_follow WHERE user_id = #{userId} AND topic_id = #{topicId}")
    boolean exists(@Param("userId") Long userId, @Param("topicId") Long topicId);
    
    @Update("UPDATE forum_topic SET follow_count = follow_count + 1 WHERE id = #{topicId}")
    int incrementFollowCount(@Param("topicId") Long topicId);
    
    @Update("UPDATE forum_topic SET follow_count = follow_count - 1 WHERE id = #{topicId} AND follow_count > 0")
    int decrementFollowCount(@Param("topicId") Long topicId);
}




