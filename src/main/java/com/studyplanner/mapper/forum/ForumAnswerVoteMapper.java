package com.studyplanner.mapper.forum;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumAnswerVoteMapper {
    
    @Insert("INSERT INTO forum_answer_vote (user_id, answer_id, create_time) " +
            "VALUES (#{userId}, #{answerId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Delete("DELETE FROM forum_answer_vote WHERE user_id = #{userId} AND answer_id = #{answerId}")
    int delete(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_answer_vote WHERE user_id = #{userId} AND answer_id = #{answerId}")
    boolean exists(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Update("UPDATE forum_answer SET vote_count = vote_count + 1 WHERE id = #{answerId}")
    int incrementVoteCount(@Param("answerId") Long answerId);
    
    @Update("UPDATE forum_answer SET vote_count = vote_count - 1 WHERE id = #{answerId} AND vote_count > 0")
    int decrementVoteCount(@Param("answerId") Long answerId);
}



