package com.studyplanner.mapper.forum;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumQuestionFollowMapper {
    
    @Select("SELECT COUNT(*) > 0 FROM forum_question_follow WHERE user_id = #{userId} AND question_id = #{questionId}")
    boolean exists(@Param("userId") Long userId, @Param("questionId") Long questionId);
}




