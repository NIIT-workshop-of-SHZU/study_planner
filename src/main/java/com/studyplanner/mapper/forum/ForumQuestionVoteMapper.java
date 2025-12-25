package com.studyplanner.mapper.forum;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumQuestionVoteMapper {
    
    @Insert("INSERT INTO forum_question_vote (user_id, question_id, create_time) " +
            "VALUES (#{userId}, #{questionId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Delete("DELETE FROM forum_question_vote WHERE user_id = #{userId} AND question_id = #{questionId}")
    int delete(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_question_vote WHERE user_id = #{userId} AND question_id = #{questionId}")
    boolean exists(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Update("UPDATE forum_question SET vote_count = vote_count + 1 WHERE id = #{questionId}")
    int incrementVoteCount(@Param("questionId") Long questionId);
    
    @Update("UPDATE forum_question SET vote_count = vote_count - 1 WHERE id = #{questionId} AND vote_count > 0")
    int decrementVoteCount(@Param("questionId") Long questionId);
}



