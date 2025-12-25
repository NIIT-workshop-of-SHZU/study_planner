package com.studyplanner.mapper.forum;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ForumCommentVoteMapper {
    
    @Insert("INSERT INTO forum_comment_vote (user_id, comment_id, create_time) " +
            "VALUES (#{userId}, #{commentId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("commentId") Long commentId);
    
    @Delete("DELETE FROM forum_comment_vote WHERE user_id = #{userId} AND comment_id = #{commentId}")
    int delete(@Param("userId") Long userId, @Param("commentId") Long commentId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_comment_vote WHERE user_id = #{userId} AND comment_id = #{commentId}")
    boolean exists(@Param("userId") Long userId, @Param("commentId") Long commentId);
    
    @Update("UPDATE forum_comment SET vote_count = vote_count + 1 WHERE id = #{commentId}")
    int incrementVoteCount(@Param("commentId") Long commentId);
    
    @Update("UPDATE forum_comment SET vote_count = vote_count - 1 WHERE id = #{commentId} AND vote_count > 0")
    int decrementVoteCount(@Param("commentId") Long commentId);
}



